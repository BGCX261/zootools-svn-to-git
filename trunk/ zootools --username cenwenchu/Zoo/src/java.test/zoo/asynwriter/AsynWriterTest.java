package zoo.asynwriter;


import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import zoo.asynweb.pipe.impl.WebPipeLog;


/**
 * @author fangweng
 * @email fangweng@taobao.com
 * @date 2010-10-29
 *
 */
public class AsynWriterTest
{

	static AsynWriter<WebPipeLog> asynWriter;
	static AsynWriter<WebPipeLog> asynWriter2;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		asynWriter = new AsynWriter<WebPipeLog>();
		IWriterScheduleFactory<WebPipeLog> writerFactory 
			= new FileWriterFactory<WebPipeLog>();
		writerFactory.init();
		asynWriter.setWriterFactory(writerFactory);
		asynWriter.init();
		
		asynWriter2 = new AsynWriter<WebPipeLog>();
		IWriterScheduleFactory<WebPipeLog> writerFactory2 
			= new FileWriterFactory<WebPipeLog>();
		writerFactory2.setLoggerClass(WebPipeLog.class.getName());
		writerFactory2.init();
		asynWriter2.setWriterFactory(writerFactory2);
		asynWriter2.init();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
		if (asynWriter != null)
			asynWriter.destroy();
		
		if (asynWriter2 != null)
			asynWriter2.destroy();
	}
	
	@Test
	public void testAsynWriter()
	{
		try
		{
			WebPipeLog log = createLog("taobao.getUser");
			
			asynWriter.write(log);
			asynWriter2.write(log);
			
			log = createLog("taobao.getShop");
			asynWriter.write(log);
			asynWriter2.write(log);
			
			log = createLog("taobao.getProduct");
			asynWriter.write(log);
			asynWriter2.write(log);
			
			
			Thread.sleep(10000);
		}
		catch(Exception ex)
		{
			System.out.println(ex);
			Assert.assertNotNull(null);
		}
	}

	private WebPipeLog createLog(String apiName) {
		WebPipeLog requestLog = new WebPipeLog();
		
		requestLog.getTimeStampQueue().put("pip1", 5l);
		requestLog.getTimeStampQueue().put("pip2", 51l);
		requestLog.getTimeStampQueue().put("pip3", 52l);
		requestLog.getTimeStampQueue().put("pip4", 53l);
		
		return requestLog;
	}
	
}
