package zoo.asynweb.pipe;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.eclipse.jetty.server.HttpConnection;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import junit.framework.Assert;
import zoo.asynweb.pipe.impl.WebPipeInput;
import zoo.asynweb.pipe.impl.WebPipeManager;
import zoo.asynweb.pipe.impl.WebPipeResult;
import zoo.util.test.AbstractTestTask;

public class WebPipeTestTask extends AbstractTestTask{

	private WebPipeManager webPipeManager;
	
	MockHttpServletRequest request;
	MockHttpServletResponse response;
	WebPipeInput input;
	
	public WebPipeTestTask()
	{
//		request = new MockHttpServletRequest("GET", "/top/router/rest");
//		response = new MockHttpServletResponse();
	}
	
	public WebPipeManager getWebPipeManager() {
		return webPipeManager;
	}

	public void setWebPipeManager(WebPipeManager webPipeManager) {
		this.webPipeManager = webPipeManager;
	}

	@Override
	public boolean doTask() {
		
		try
		{
			HttpURLConnection httpConn = (HttpURLConnection) new URL("http://localhost:8080/demo")
					.openConnection();
			
			httpConn.setRequestMethod("GET");
			
			httpConn.connect();

			try
			{
				InputStream in = httpConn.getInputStream();

//				byte[] buf = new byte[500];
//				int count = 0;
//
//				while ((count = in.read(buf)) > 0)
//				{
//					bout.write(buf, 0, count);
//				}
//
//				System.out.println(new String(bout.toByteArray(),"UTF-8"));
			} catch (Exception ex)
			{
				//ex.printStackTrace();
			} finally
			{
				if (httpConn != null)
					httpConn.disconnect();
			}
			
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			Assert.assertTrue(false);
		}
		
		return false;
	}

}
