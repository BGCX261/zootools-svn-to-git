/**
 * 
 */
package zoo.asynweb.pipe;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import zoo.asynweb.pipe.impl.WebPipeInput;
import zoo.asynweb.pipe.impl.WebPipeLog;
import zoo.asynweb.pipe.impl.WebPipeManager;
import zoo.asynweb.pipe.impl.WebPipeResult;
import zoo.asynwriter.AsynWriter;
import zoo.asynwriter.FileWriterFactory;
import zoo.asynwriter.IWriterScheduleFactory;
import zoo.util.test.ITestTask;
import zoo.util.test.ITestTaskFactory;
import zoo.util.test.StressTestTemplate;

/**
 * @author fangweng
 * @email fangweng@taobao.com
 * @date 2010-11-18
 *
 */
public class WebPipeManagerStressTest implements ITestTaskFactory{

	private WebPipeManager webPipeManager;
	
	
	
	
	public WebPipeManager getWebPipeManager() {
		return webPipeManager;
	}

	public void setWebPipeManager(WebPipeManager webPipeManager) {
		this.webPipeManager = webPipeManager;
	}

	@Override
	public ITestTask getTaskInstance() {
		WebPipeTestTask task = new WebPipeTestTask();
		task.setWebPipeManager(webPipeManager);
		return task;
	}
	
	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		WebPipeManagerStressTest factory = new WebPipeManagerStressTest();
		
		WebPipeManager webPipeManager = new WebPipeManager();
		AsynWriter<WebPipeLog> asynWriter = new AsynWriter<WebPipeLog>();
		IWriterScheduleFactory<WebPipeLog> writerFactory = new FileWriterFactory<WebPipeLog>();
		writerFactory.init();
		asynWriter.setWriterFactory(writerFactory);
		asynWriter.init();
		
		webPipeManager.setAsynLogWriter(asynWriter);
		
		IPipe<WebPipeInput,WebPipeResult> pipe1 = new MockWebPipe("MockPipe1");
		IPipe<WebPipeInput,WebPipeResult> pipe2 = new MockWebPipe("MockPipe2");
		IPipe<WebPipeInput,WebPipeResult> pipe3 = new ExportMockWebPipe("MockPipe3");
		webPipeManager.register(pipe1);
		webPipeManager.register(pipe2);
		webPipeManager.register(pipe3);
		pipe2.setAsynMode(true);
		factory.setWebPipeManager(webPipeManager);
		
		Server _server = null;
		QueuedThreadPool _threads = null;
		SelectChannelConnector _connector = null;
		
		try
		{
			
			_server=new Server();
		    _threads=new QueuedThreadPool();
			_threads.setMaxThreads(30);
	        _server.setThreadPool(_threads);
	        
	        
	        _connector = new SelectChannelConnector();
	        _connector.setPort(8080);
	        _connector.setMaxIdleTime(30000);
	        _connector.setMaxIdleTime(120000);
	        _server.setConnectors(new Connector[]
	                                           {_connector });
	        
	        ContextHandler contextA = new ContextHandler("/demo");
	        PipeHandler handlerA = new PipeHandler(webPipeManager);
	        contextA.setHandler(handlerA);
	        
	        _server.setHandler(contextA);
	        _server.start();

			
			
	        Thread.sleep(3000);
	        
			StressTestTemplate.doStressTest(2, 1000, factory);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			webPipeManager.unregister(pipe1);
			webPipeManager.unregister(pipe2);
			webPipeManager.unregister(pipe3);
			
			
			Thread.sleep(10000000);
			
			webPipeManager.destory();
			asynWriter.destroy();
			
			if (_server != null)
				try {
					_server.stop();
					_threads.stop();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}	
		
		
	}

}

class PipeHandler extends HandlerWrapper{

	private WebPipeManager webPipeManager;
	
	public PipeHandler(WebPipeManager webPipeManager)
	{
		this.webPipeManager = webPipeManager;
	}
	
	@Override
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		
		WebPipeInput input = webPipeManager.getPipeInput(request, response);
		
		webPipeManager.doPipes(input);
		
	}
	
}
