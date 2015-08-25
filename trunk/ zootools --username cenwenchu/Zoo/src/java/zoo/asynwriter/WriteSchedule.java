package zoo.asynwriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 输出日志结果集抽象类
 * @author fangweng(wenchu)
 * @email fangweng@taobao.com
 *
 */
public abstract class WriteSchedule<T> implements java.lang.Runnable,IWriter<T>
{
	private static final Log logger = LogFactory.getLog(WriteSchedule.class);
	
	RecordBundle<T> bundle;
	
	public void run()
	{
		if (logger.isInfoEnabled())
			if (bundle != null &&  bundle.getRecords() != null)
				logger.info("WriteSchedule is called! size: " + bundle.getRecords().size());
		
		write(bundle);
	}
	
	public RecordBundle<T> getBundle()
	{
		return bundle;
	}

	public void setBundle(RecordBundle<T> bundle)
	{
		this.bundle = bundle;
	}
	
}
