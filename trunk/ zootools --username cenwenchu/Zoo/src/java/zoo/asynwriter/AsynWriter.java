package zoo.asynwriter;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



/**
 * 异步输出访问日志接口默认实现类
 * @author fangweng(wenchu)
 * @email fangweng@taobao.com
 *
 */
public class AsynWriter<T> implements IWriter<T>
{
	private static final Log Logger = LogFactory.getLog(AsynWriter.class);
	
	/**
	 * 日志队列
	 */
	private BlockingQueue<T> logQueue;
	
	/**
	 * 根据logQueue的数据来创建RecordBundle，一个线程对应到一个flushPool中的RecordBundle,
	 * 当数据满页或者在配置间隔时间已到的情况下,flushBundle中的数据到输出源
	 */
	private ExecutorService createBundleService;
	
	/**
	 * 定义一个或者多个消费者线程来读取队列数据，并批量刷出到外部输出源
	 */
	private ConsumerSchedule<T>[] consumers;
	
	/**
	 * 配置对象，应用唯一一份
	 */
	private LogConfig config = LogConfig.getInstance();
	
	/**
	 * 可以注入的Writer工厂类
	 */
	private IWriterScheduleFactory<T> writerFactory;
	
	private Log writerLog = null;
	
	public AsynWriter(Log writerLog){
		this.writerLog = writerLog;
	}
	
	public AsynWriter(){
		
	}
	
	@SuppressWarnings("unchecked")
	public void init()
	{
		logQueue = new LinkedBlockingQueue<T>(config.getMaxQueueSize());
		createBundleService = Executors.newFixedThreadPool(config.getCreateBundleServiceThreadCount());	
		consumers = new ConsumerSchedule[config.getCreateBundleServiceThreadCount()];
		
		if (writerFactory == null)
		{
			writerFactory = new FileWriterFactory<T>(writerLog);
			writerFactory.init();
		}
		
		for(int i = 0 ; i < config.getCreateBundleServiceThreadCount(); i++)
		{
			consumers[i] = new ConsumerSchedule<T>();
			consumers[i].setLogQueue(logQueue);
			consumers[i].setName(new StringBuffer().
					append("consumerSchedule").append(i).toString());
			consumers[i].setWriterFactory(writerFactory);
			consumers[i].init();
			
			createBundleService.submit(consumers[i]);
		}
		
		if (Logger.isInfoEnabled())
			Logger.info(new StringBuffer("AsynWriterConfig :").append(config));
	}
	
	public void destroy()
	{
		try
		{
			logQueue.clear();
			
			for(ConsumerSchedule<T> consumer: consumers)
			{
				consumer.stopSchedule();
			}	
			
			Thread.sleep(3000);
		} catch (InterruptedException e){}
		
		if (createBundleService != null)
			createBundleService.shutdown();
		
		createBundleService = null;
	}
	
	public void restart()
	{
		destroy();
		init();
	}
	
	@Override
	public void write(T content)
	{		
		logQueue.add(content);
	}


	public BlockingQueue<T> getLogQueue()
	{
		return logQueue;
	}


	public ConsumerSchedule<T>[] getConsumers()
	{
		return consumers;
	}
	
	public LogConfig getConfig()
	{
		return config;
	}


	public void setConfig(LogConfig config)
	{
		this.config = config;
	}

	@Override
	public void write(RecordBundle<T> bundle)
	{
		if (bundle != null && bundle.getRecords() != null
				&& bundle.getRecords().size() > 0)
		{
			logQueue.addAll(bundle.getRecords());
		}
	}

	public IWriterScheduleFactory<T> getWriterFactory()
	{
		return writerFactory;
	}

	public void setWriterFactory(IWriterScheduleFactory<T> writerFactory)
	{
		this.writerFactory = writerFactory;
	}	
	
}
