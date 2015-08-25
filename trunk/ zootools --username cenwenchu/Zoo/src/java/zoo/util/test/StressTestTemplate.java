/**
 * 
 */
package zoo.util.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author fangweng
 * @email fangweng@taobao.com
 * @date 2010-11-12
 *
 */
public class StressTestTemplate {

	
	public static void doStressTest(int vusers,int repreateTimes,ITestTaskFactory taskFactory)
	{
		try
		{		
			long totaltime = System.currentTimeMillis();
			AtomicLong errorCounter = new AtomicLong(0);

			List<Long> result = Collections.synchronizedList(new ArrayList<Long>());
					
			CountDownLatch startSignal = new CountDownLatch(1);
		    CountDownLatch doneSignal = new CountDownLatch(vusers);	
			
			for(int i = 0 ; i < vusers ; i++)
			{
				ITestTask t = taskFactory.getTaskInstance();
				t.setCount(repreateTimes);
				t.setDone(doneSignal);
				t.setErrorCounter(errorCounter);
				t.setName(String.valueOf(i));
				t.setResult(result);
				t.setStart(startSignal);

				new Thread(t).start();
			}
			
			startSignal.countDown();
			doneSignal.await();
			
			if (result.size() == vusers)
			{
				long total = 0;
				for(long l : result)
				{
					total += l;
				}
				
				System.out.println(new StringBuffer().append("stress test consume: ")
						.append(total).append(" , average boundle consume: ").append(total/(long)result.size())
						.append(", average per request :").append(total/(long)result.size()/(long)repreateTimes));
			}
			
			totaltime = System.currentTimeMillis() - totaltime;
			
			System.out.println("total consume: " + totaltime);
			
			Thread.sleep(1000);
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	
}
