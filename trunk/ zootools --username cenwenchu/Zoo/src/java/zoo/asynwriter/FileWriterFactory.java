package zoo.asynwriter;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * 
 * @author fangweng(wenchu)
 * @email fangweng@taobao.com
 *
 */
public class FileWriterFactory<T> implements IWriterScheduleFactory<T>
{
	private static final Log Logger = LogFactory.getLog(FileWriterFactory.class);
			
	private Log writelogger;
	
	private String loggerClass;
	
	public FileWriterFactory(Log writerLogger){
		this.writelogger = writerLogger;
	}
	public FileWriterFactory(String loggerClass){
		this.loggerClass = loggerClass;
	}
	public FileWriterFactory(){
		
	}
	public void init()
	{
		if(writelogger == null){
			writelogger = LogFactory.getLog(FileWriterFactory.class);
		}
		
		if (loggerClass != null)
		{
			try
			{
				writelogger = LogFactory.getLog(Class.forName(loggerClass));
			}
			catch(Exception ex)
			{
				Logger.error("FileWriterFactory init error!",ex);
			}
			
		}
	}

	@Override
	public WriteSchedule<T> createWriter()
	{
		return new FileWriteSchedule();
	}
	
	public String getLoggerClass()
	{
		return loggerClass;
	}

	public void setLoggerClass(String loggerClass)
	{
		this.loggerClass = loggerClass;
	}
	
	class FileWriteSchedule extends WriteSchedule<T>
	{
				
		@Override
		public void write(Object content)
		{
			writelogger.fatal(content);
		}

		@Override
		public void write(RecordBundle<T> bundle)
		{
			if (bundle != null)
			{
				if (bundle.getCount() > 0 || 
						(bundle.getRecords() != null && bundle.getRecords().size() > 0))
				{
					StringBuilder content = new StringBuilder();
					
					List<T> records = bundle.getRecords();
					
					int count = records.size();
					
					for(int i =0 ; i < count ; i++)
					{
						T node = records.get(i);
						
						if (i == count -1)
							content.append(node);
						else
							content.append(node).append("\r\n");
					}
					
					writelogger.fatal(content.toString());
				}
			}
		}
	}// end class
	
	
}
