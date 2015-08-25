package zoo.asynwriter;

import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * 读取访问日志，累计到输出页面大小
 * 或者到达输出间隔时间创建输出线程输出日志
 * @author fangweng(wenchu)
 * @email fangweng@taobao.com
 *
 * @param <T>
 */
public class ConsumerSchedule <T> implements ISchedule<T>{

	private static final Log Logger = LogFactory.getLog(ConsumerSchedule.class);
	
	/**
	 * 日志队列
	 */
	private BlockingQueue<T> logQueue;
	
	private RecordBundle<T> bundle;
	
	private String name;

	private LogConfig config = LogConfig.getInstance();
	
	/**
	 * 输出操作线程工作池
	 */
	private ExecutorService writeBundleService;
	
	private boolean activeFlag;
	
	/**
	 * Writer工厂类
	 */
	private IWriterScheduleFactory<T> writerFactory;
	
	public IWriterScheduleFactory<T> getWriterFactory()
	{
		return writerFactory;
	}

	public void setWriterFactory(IWriterScheduleFactory<T> writerFactory)
	{
		this.writerFactory = writerFactory;
	}

	public LogConfig getConfig()
	{
		return config;
	}


	public void setConfig(LogConfig config)
	{
		this.config = config;
	}
	
	
	/**
	 * 内部检查
	 * @return
	 */
	public boolean checkInnerElement()
	{
		if (logQueue == null  
				|| name == null || config == null)
			return false;
		else
			return true;
	}
	
	@Override
	public void init()
	{
		if (!checkInnerElement())
		{
			throw new java.lang.RuntimeException("ConsumerSchedule start Error!,please check innerElement");
		}
		
		bundle = new RecordBundle<T>(config.getFlushInterval());
		writeBundleService = Executors.newFixedThreadPool(config.getWriterMaxCount());
		activeFlag = true;
	}
	
	public void run()
	{
		try
		{
			while(activeFlag)
			{
				//到了输出间隔时间
				if (bundle.getCount() > 0 && bundle.getFlushTime().before(new Date()))
				{
					flush();
				}
				
				//缓存中的记录数到达
				if (bundle.getCount() >= config.getBundleMaxCount())
				{
					flush();
				}
				
				
				T node = logQueue.poll(100,TimeUnit.MICROSECONDS);
				

				if (node != null)
				{
					bundle.add(node);
				}
			}
		}
		catch (Exception e)
		{
			if (e instanceof java.lang.InterruptedException)
			{
				Logger.error("ConsumerSchedule is stop!");
			}
			else
				Logger.error("ConsumerSchedule error!",e);
		}
		finally
		{
			if (writeBundleService != null)
				writeBundleService.shutdown();
			
			writeBundleService = null;
		}
	}
	
	public void flush() throws CloneNotSupportedException, 
					InterruptedException, ExecutionException, TimeoutException
	{
		if (bundle != null && bundle.getRecords() != null 
				&& bundle.getRecords().size() > 0)
		{
			WriteSchedule<T> writeSchedule = writerFactory.createWriter();
			
			writeSchedule.setBundle(bundle.clone());
			bundle.reset(config.getFlushInterval());
			
			writeBundleService.submit(writeSchedule);
		}
			
	}
	
	public void stopSchedule()
	{
		try
		{
			flush();
		}
		catch(Exception ex)
		{
			Logger.error("ConsumerSchedule stop error!",ex);
		}
		finally
		{
			activeFlag = false;
			
			if (writeBundleService != null)
				writeBundleService.shutdown();
			
			writeBundleService = null;
		}
	}
	
	public String toString()
	{
		StringBuilder result = new StringBuilder();
		
		result.append("consumer name :").append(name);
		
		if (bundle != null)
			result.append(",bundle data count: ").append(bundle.getCount())
				.append(",flushTime:").append(bundle.getFlushTime());
		
		return result.toString();
	}

	public BlockingQueue<T> getLogQueue()
	{
		return logQueue;
	}

	public void setLogQueue(BlockingQueue<T> logQueue)
	{
		this.logQueue = logQueue;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public ExecutorService getWriteBundleService()
	{
		return writeBundleService;
	}

}
