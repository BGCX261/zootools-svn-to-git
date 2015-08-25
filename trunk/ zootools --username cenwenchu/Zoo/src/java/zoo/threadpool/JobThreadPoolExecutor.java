/**
 * 
 */
package zoo.threadpool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * 扩展线程池基本实现，
 * 在每个任务结束后需要减去任务计数器的值
 * @author fangweng
 *
 */
public class JobThreadPoolExecutor extends ThreadPoolExecutor
{

	JobDispatcher jobDispatcher;
	
	public JobThreadPoolExecutor(int maximumPoolSize,
			long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory,JobDispatcher jobDispatcher) {
		super(maximumPoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
				threadFactory);
		this.jobDispatcher = jobDispatcher;
	}
	
	@Override
	protected void afterExecute(Runnable r, Throwable t)
	{
		if (r instanceof Job)
		{
			String key = ((Job)r).getKey();
			
			jobDispatcher.getTotalCounter().decrementAndGet();
			
			if (jobDispatcher.getCounterPool().size() == 0)
				jobDispatcher.getDefaultCounter().decrementAndGet();
			else
			{
				AtomicInteger counter = jobDispatcher.getCounterPool().get(key);
				if (counter != null)
				{
					if (counter.decrementAndGet() < 0)
					{
						counter.incrementAndGet();
						jobDispatcher.getDefaultCounter().decrementAndGet();
					}
					else
					{
						Integer threshold = jobDispatcher.getThresholdPool().get(key);	
						
						if (threshold < 0)//limit mode
						{
							jobDispatcher.getDefaultCounter().decrementAndGet();
						}
					}
						
				}
				else
					jobDispatcher.getDefaultCounter().decrementAndGet();
			}
				
			
		}
	}
	
}
