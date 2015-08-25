/**
 * 
 */
package zoo.threadpool;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import zoo.asynweb.pipe.impl.NamedThreadFactory;


/**
 * 简单的任务分发类，支持根据线程权重模型来分配资源，当前支持两类
 * 1.根据某一类key可以预留多少资源独享.
 * 2.根据某一类key可以限制最多使用多少资源。
 * @author fangweng
 *
 */
public class JobDispatcher extends Thread{
	
	private static final Log logger = LogFactory.getLog(JobDispatcher.class);
	
	public static final String DEFAULT_COUNTER = "defaultCounter";
	public static final String TOTAL_COUNTER = "totalCounter";
	public static final String QUEUE_JOB_LENGTH = "queue_job_length";
	

	private BlockingQueue<Job> jobQueue;
	private JobThreadPoolExecutor threadPool;
	
	private JobThreadWeightModel[] jobThreadWeightModel;
	
	private Map<String,AtomicInteger> counterPool;
	private Map<String,Integer> thresholdPool;
	private int defaultThreshold;
	private AtomicInteger defaultCounter;
	private AtomicInteger totalCounter;
	
	
	private int maximumPoolSize = 200;
	private boolean runFlag = true;
	
	//private ReentrantLock lock = new ReentrantLock();
	
	
	
	public Map<String,Integer> getCurrentThreadStatus()
	{
		Map<String,Integer> status = new HashMap<String,Integer>();
		
		Iterator<Entry<String,AtomicInteger>> entrys = counterPool.entrySet().iterator();
		
		while(entrys.hasNext())
		{
			Entry<String,AtomicInteger> e = entrys.next();
			
			status.put(e.getKey(), e.getValue().get());
		}
		
		status.put(DEFAULT_COUNTER, defaultCounter.get());
		status.put(QUEUE_JOB_LENGTH, jobQueue.size());
		status.put(TOTAL_COUNTER, totalCounter.get());
		
		return status;
	}
	
	
	public Map<String, Integer> getThresholdPool() {
		return thresholdPool;
	}


	public void setThresholdPool(Map<String, Integer> thresholdPool) {
		this.thresholdPool = thresholdPool;
	}


	public void init()
	{
		threadPool = new JobThreadPoolExecutor
						(maximumPoolSize,0,TimeUnit.SECONDS
									,new LinkedBlockingQueue<Runnable>(), 
										new NamedThreadFactory("jobDispatcher_worker"),this);
	
		defaultThreshold = maximumPoolSize;
		defaultCounter = new AtomicInteger(0);
		totalCounter = new AtomicInteger(0);
		
		jobQueue = new LinkedBlockingQueue<Job>();

		counterPool = new ConcurrentHashMap<String, AtomicInteger>();
		thresholdPool = new HashMap<String,Integer>();
		
		buildWeightModel();
	}
	
	public void stopDispatcher()
	{	
		if (threadPool != null)
			threadPool.shutdownNow();
		
		if (jobQueue != null)
			jobQueue.clear();

		if (counterPool != null)
			counterPool.clear();
		
		if (thresholdPool != null)
			thresholdPool.clear();
		
		runFlag = false;
		this.interrupt();
	}
	
	private void buildWeightModel()
	{
		if (jobThreadWeightModel != null && jobThreadWeightModel.length > 0)
		{
			for(JobThreadWeightModel j : jobThreadWeightModel)
			{
				try
				{
					counterPool.put(j.getKey(), new AtomicInteger(0));
						
					if (j.getValue() == 0)
						continue;
						
					if(j.getType().equals(JobThreadWeightModel.WEIGHT_MODEL_LIMIT))
					{
						thresholdPool.put(j.getKey(),j.getValue());
					}
					else
						if (j.getType().equals(JobThreadWeightModel.WEIGHT_MODEL_LEAVE))
						{
							thresholdPool.put(j.getKey(),j.getValue());
							
							defaultThreshold -= j.getValue();
						}
						else
							throw new java.lang.RuntimeException("thread weight config not support!");				
				}
				catch(Exception ex)
				{
					logger.error("create jobWeightModels : " + j.getKey()
							+ " error!",ex);
					
				}
			}
			
			if (defaultThreshold <= 0)
				throw new java.lang.RuntimeException("total leave resource > total resource...");
			
			for(JobThreadWeightModel j : jobThreadWeightModel)
			{
				try
				{
					
					if (j.getValue() == 0)
						continue;
						
					if(j.getType().equals(JobThreadWeightModel.WEIGHT_MODEL_LIMIT))
					{
						if (thresholdPool.get(j.getKey()) > defaultThreshold)
							thresholdPool.put(j.getKey(),-defaultThreshold);
						else
							thresholdPool.put(j.getKey(),-thresholdPool.get(j.getKey()));
					}			
				}
				catch(Exception ex)
				{
					logger.error("create jobWeightModels : " + j.getKey()
							+ " error!",ex);
					
				}
			}	
			
		}
	}
	
	
	/**
	 * 提交任务
	 * @param job
	 */
	public void submitJob(Job job) 
	{
		//第一层做总量判断，同时锁定总资源
		if (totalCounter.incrementAndGet() > this.maximumPoolSize)
		{
			totalCounter.decrementAndGet();
			
			if (!jobQueue.offer(job))
				throw new java.lang.RuntimeException("can't submit job, queue insert error.");
			
			return;
		}
			
		
		String key = job.getKey();
		Integer threshold = thresholdPool.get(key);	
		boolean isResourceFree = false;
		
		if (threshold == null)
		{
			if (defaultCounter.incrementAndGet() > defaultThreshold)
				defaultCounter.decrementAndGet();
			else
				isResourceFree = true;
		}
		else
		{
			AtomicInteger counter = counterPool.get(key);
			
			if (threshold > 0)//leave mode
			{
				if (counter.incrementAndGet() > threshold)
				{
					counter.decrementAndGet();
					
					if (defaultCounter.incrementAndGet() > defaultThreshold)
						defaultCounter.decrementAndGet();
					else
						isResourceFree = true;
				}
				else
					isResourceFree = true;
			}
			else// limit mode
			{
				if (counter.incrementAndGet() > -threshold)
				{
					counter.decrementAndGet();
				}
				else
				{
					if (defaultCounter.incrementAndGet() > defaultThreshold)
					{
						defaultCounter.decrementAndGet();
						counter.decrementAndGet();
					}	
					else
						isResourceFree = true;
				}
			}
		}
		
		if(isResourceFree)
			threadPool.execute(job);
		else
		{
			totalCounter.decrementAndGet();
			
			if (!jobQueue.offer(job))
				throw new java.lang.RuntimeException("can't submit job, queue full...");
		}
			
				
	}

	public JobThreadWeightModel[] getJobThreadWeightModel() {
		return jobThreadWeightModel;
	}

	public void setJobThreadWeightModel(JobThreadWeightModel[] jobThreadWeightModel) {
		this.jobThreadWeightModel = jobThreadWeightModel;
	}

	public BlockingQueue<Job> getJobQueue() {
		return jobQueue;
	}

	public void setJobQueue(BlockingQueue<Job> jobQueue) {
		this.jobQueue = jobQueue;
	}
	
	public int getMaximumPoolSize() {
		return maximumPoolSize;
	}

	public void setMaximumPoolSize(int maximumPoolSize) {
		this.maximumPoolSize = maximumPoolSize;
	}

	public Map<String, AtomicInteger> getCounterPool() {
		return counterPool;
	}

	public int getDefaultThreshold() {
		return defaultThreshold;
	}

	public AtomicInteger getDefaultCounter() {
		return defaultCounter;
	}
	
	
	
	public AtomicInteger getTotalCounter() {
		return totalCounter;
	}


	@Override
	public void run()
	{
		try
		{
			while(runFlag)
			{
				Job job = jobQueue.poll(1, TimeUnit.SECONDS);
				
				if (job != null)
				{
					submitJob(job);
				}
			}
		}
		catch(InterruptedException ex)
		{
			//do nothing
		}
		
	}
	
}
