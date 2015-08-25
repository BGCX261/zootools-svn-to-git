/**
 * 
 */
package zoo.util.test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;



/**
 * @author fangweng
 * @email fangweng@taobao.com
 * @date 2010-11-11
 *
 */
public abstract class AbstractTestTask implements ITestTask {

	String name;
	List<Long> result;
	int count;
	CountDownLatch start;
	CountDownLatch done;
	AtomicLong errorCounter;

	
	public void setName(String name) {
		this.name = name;
	}



	public void setResult(List<Long> result) {
		this.result = result;
	}



	public void setCount(int count) {
		this.count = count;
	}



	public void setStart(CountDownLatch start) {
		this.start = start;
	}



	public void setDone(CountDownLatch done) {
		this.done = done;
	}



	public void setErrorCounter(AtomicLong errorCounter) {
		this.errorCounter = errorCounter;
	}



	public void run() 
	{
		
		try
		{
			start.await();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		long time = System.currentTimeMillis();
		long c = 0;
		
		for(int i= 0 ; i < count; i++)
		{
			if (!this.doTask())
				c += 1;	
		}
		
		time = System.currentTimeMillis() - time;
		
		result.add(time);
		errorCounter.addAndGet(c);
		done.countDown();
	}

}
