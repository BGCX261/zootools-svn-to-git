package zoo.asynwriter;

import java.net.InetAddress;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 通过配置文件读取配置信息，应用一份
 * @author fangweng(wenchu)
 * @email fangweng@taobao.com
 *
 */
public class LogConfig {
	private static final Log Logger = LogFactory.getLog(LogConfig.class);
	
	public final static String  LOG_CONFIG = "log-config.properties";
	
	private static LogConfig config = new LogConfig(); 
	
	/**
	 * 最大队列长度
	 */
	private int maxQueueSize = 50000;
	
	/**
	 * flush的间隔时间，单位秒
	 */
	private int flushInterval = 5;
	
	/**
	 * 创建分页的线程数
	 */
	private int createBundleServiceThreadCount = 3;
	
	/**
	 * 当记录数达到多少的时候写入外部存储
	 */
	private int bundleMaxCount = 100;
	
	/**
	 * 输出记录Bundle的工作线程池大小
	 */
	private int writerMaxCount = 50;
	
	/**
	 * 本地地址
	 */
	private String localAddress;
	
	
	
	public int getMaxQueueSize()
	{
		return maxQueueSize;
	}

	public void setMaxQueueSize(int maxQueueSize)
	{
		this.maxQueueSize = maxQueueSize;
	}

	public String getLocalAddress() {
		return localAddress;
	}

	public void setLocalAddress(String localAddress) {
		this.localAddress = localAddress;
	}

	public int getFlushInterval() {
		return flushInterval;
	}

	public void setFlushInterval(int flushInterval) {
		this.flushInterval = flushInterval;
	}

	public int getCreateBundleServiceThreadCount() {
		return createBundleServiceThreadCount;
	}

	public void setCreateBundleServiceThreadCount(int createBundleServiceThreadCount) {
		this.createBundleServiceThreadCount = createBundleServiceThreadCount;
	}

	public int getBundleMaxCount() {
		return bundleMaxCount;
	}

	public void setBundleMaxCount(int bundleMaxCount) {
		this.bundleMaxCount = bundleMaxCount;
	}

	public int getWriterMaxCount() {
		return writerMaxCount;
	}

	public void setWriterMaxCount(int writerMaxCount) {
		this.writerMaxCount = writerMaxCount;
	}

	private LogConfig()
	{
		Properties prop;
		
		try
		{		
			prop = new Properties();
			
			URL url = Thread.currentThread().getContextClassLoader()
						.getResource(LOG_CONFIG);
			
			if (url != null)
				prop.load(url.openStream());
			
			if (prop.get("flushInterval") != null)
				flushInterval = Integer.parseInt((String)prop.get("flushInterval"));
			
			if (prop.get("createBundleServiceThreadCount") != null)
				createBundleServiceThreadCount = 
					Integer.parseInt((String)prop.get("createBundleServiceThreadCount"));
			
			if (prop.get("bundleMaxCount") != null)
				bundleMaxCount = 
					Integer.parseInt((String)prop.get("bundleMaxCount"));
			
			if (prop.get("writerMaxCount") != null)
				writerMaxCount = 
					Integer.parseInt((String)prop.get("writerMaxCount"));
			
			localAddress = InetAddress.getLocalHost().getHostAddress().toString();
			
			if (localAddress == null || "".equals(localAddress))
				localAddress = InetAddress.getLocalHost().getHostName().toString();
		}
		catch(Exception ex)
		{
			Logger.error("Load AsynWriter error!",ex);
			throw new java.lang.RuntimeException(ex);
		}		
		
	};
	
	public static LogConfig getInstance()
	{
		if (config == null)
			config = new LogConfig();
			
		return config;
	}
	
}
