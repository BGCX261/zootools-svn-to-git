/**
 * 
 */
package zoo.asynweb.pipe;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import zoo.asynweb.pipe.IPipe;
import zoo.asynweb.pipe.impl.WebPipeInput;
import zoo.asynweb.pipe.impl.WebPipeLog;
import zoo.asynweb.pipe.impl.WebPipeManager;
import zoo.asynweb.pipe.impl.WebPipeResult;
import zoo.asynwriter.AsynWriter;
import zoo.asynwriter.FileWriterFactory;
import zoo.asynwriter.IWriterScheduleFactory;

/**
 * @author fangweng
 *
 */
public class WebPipeManagerTest
{
	private WebPipeManager webPipeManager;
	private AsynWriter<WebPipeLog> asynWriter;
	private IWriterScheduleFactory<WebPipeLog> writerFactory ;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		webPipeManager = new WebPipeManager();
		
		asynWriter = new AsynWriter<WebPipeLog>();
		writerFactory = new FileWriterFactory<WebPipeLog>();
		writerFactory.init();
		asynWriter.setWriterFactory(writerFactory);
		asynWriter.init();
		
		webPipeManager.setAsynLogWriter(asynWriter);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception
	{
		Thread.sleep(300);
	}

	/**
	 * Test method for {@link com.taobao.top.core.framework.TopPipeManager#doPipes(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 */
	@Ignore
	@Test
	public void testDoPipes()
	{
		IPipe<WebPipeInput,WebPipeResult> pipe1 = new MockWebPipe("MockPipe1");
		IPipe<WebPipeInput,WebPipeResult> pipe2 = new MockWebPipe("MockPipe2");
		IPipe<WebPipeInput,WebPipeResult> pipe3 = new MockWebPipe("MockPipe3");
		webPipeManager.register(pipe1);
		webPipeManager.register(pipe2);
		webPipeManager.register(pipe3);
				
		
		try
		{
			MockHttpServletRequest request = new MockHttpServletRequest("GET", "/top/router/rest");
			MockHttpServletResponse response = new MockHttpServletResponse();
			
			
			WebPipeInput input = webPipeManager.getPipeInput(request, response);
			
			WebPipeResult result = webPipeManager.doPipes(input);
			Assert.assertEquals(result.getResult(), "MockPipe3");
			
			request = new MockHttpServletRequest("GET", "/top/router/rest");
			request.setParameter("isbreak", "true");
			input =  webPipeManager.getPipeInput(request, response);
			result = webPipeManager.doPipes(input);
			Assert.assertEquals(result.getResult(), "MockPipe1");
			
			request = new MockHttpServletRequest("GET", "/top/router/service");
			request.setParameter("ignoreName", "MockPipe3");
			input = webPipeManager.getPipeInput(request, response);
			result = webPipeManager.doPipes(input);
			Assert.assertEquals(result.getResult(), "MockPipe2");
		}
		finally
		{
			webPipeManager.unregister(pipe1);
			webPipeManager.unregister(pipe2);
			webPipeManager.unregister(pipe3);
		}

	}
	
	@Test 
	public void testAsynPipe()
	{
		IPipe<WebPipeInput,WebPipeResult> pipe1 = new MockWebPipe("MockPipe1");
		IPipe<WebPipeInput,WebPipeResult> pipe2 = new MockWebPipe("MockPipe2");
		pipe2.setAsynMode(true);
		webPipeManager.register(pipe1);
		webPipeManager.register(pipe2);
				
		
		try
		{
			MockHttpServletRequest request = new MockHttpServletRequest("GET", "/top/router/rest");
			MockHttpServletResponse response = new MockHttpServletResponse();
			
			
			WebPipeInput input = webPipeManager.getPipeInput(request, response);
			
			WebPipeResult result = webPipeManager.doPipes(input);
			Assert.assertEquals(result.getResult(), "MockPipe2");
			
		}
		finally
		{
			webPipeManager.unregister(pipe1);
			webPipeManager.unregister(pipe2);
		}
	}

	@Ignore
	@Test
	public void testManagement()
	{
		try
		{
			IPipe<WebPipeInput,WebPipeResult> pipe1 = new MockWebPipe("MockPipe1");
			IPipe<WebPipeInput,WebPipeResult> pipe2 = new MockWebPipe("MockPipe2");
			webPipeManager.register(pipe1);
			webPipeManager.register(pipe2);
			
			
			org.junit.Assert.assertArrayEquals(new IPipe[]{pipe1,pipe2}, webPipeManager.getPipes());
			
			
			webPipeManager.unregister(pipe1);
			webPipeManager.unregister(pipe2);
			
			org.junit.Assert.assertArrayEquals(new IPipe[]{},  webPipeManager.getPipes());
			
		}
		catch(Exception ex)
		{
			Assert.assertTrue(false);
		}
		
	}

}
