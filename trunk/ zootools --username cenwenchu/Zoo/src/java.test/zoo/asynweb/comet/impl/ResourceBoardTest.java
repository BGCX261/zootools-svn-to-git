/**
 * 
 */
package zoo.asynweb.comet.impl;



import org.eclipse.jetty.continuation.Continuation;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author fangweng
 * @email fangweng@taobao.com
 * @date 2010-11-10
 *
 */
public class ResourceBoardTest {

	ResourceBoard resourceBoard;
	
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		resourceBoard = new ResourceBoard();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		resourceBoard.stop();
	}
	
	@Test
	public void testDoAction()
	{
		this.testDoPOST();
		
		new TestThread(this).start();
		
		try 
		{
		
			Thread.sleep(1000);
			
			this.testDoPUT();
		
			Thread.sleep(1000);
		
			this.testDoDELETE();

			Thread.sleep(2000000);
		} 
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	class TestThread extends Thread
	{
		ResourceBoardTest boardTest;
		
		public TestThread(ResourceBoardTest boardTest)
		{
			this.boardTest = boardTest;
		}
		
		public void run()
		{
			boardTest.testDoGET();
		}
	}
	


	public void testDoGET() {
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		
		request.setParameter(ResourceBoard.RESOURCE_ID, "1");
		request.setMethod("GET");
		
		Continuation continuation = new MockContinuation();
		request.setAttribute(Continuation.ATTRIBUTE,continuation);
		
		try
		{
			resourceBoard.doAction(request, response);
			
			while(true)
			{
				byte[] responseValues = response.getContentAsByteArray();

				if (responseValues != null && responseValues.length > 0)
				{
					System.out.println(new String(responseValues,"UTF-8"));
				}
				
				if (new String(responseValues,"UTF-8").endsWith("complete"))
					break;
					
				Thread.sleep(500);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			Assert.assertTrue(false);
		}

	}
	

	public void testDoPOST() {
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		
		request.setParameter(ResourceBoard.RESOURCE_ID, "1");
		request.setParameter("price", "10");
		request.setParameter(ResourceBoard.RESOURCE_TYPE, "zoo.asynweb.comet.impl.MockResource");
		request.setMethod("POST");
		
		Continuation continuation = new MockContinuation();
		request.setAttribute(Continuation.ATTRIBUTE,continuation);
		
		try
		{
			resourceBoard.doAction(request, response);
			
			byte[] responseValues = response.getContentAsByteArray();

			if (responseValues != null && responseValues.length > 0)
			{
				System.out.println(new String(responseValues,"UTF-8"));
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			Assert.assertTrue(false);
		}

	}
	

	public void testDoDELETE() {
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		
		request.setParameter(ResourceBoard.RESOURCE_ID, "1");
		request.setMethod("DELETE");
		
		Continuation continuation = new MockContinuation();
		request.setAttribute(Continuation.ATTRIBUTE,continuation);
		
		try
		{
			resourceBoard.doAction(request, response);
			
			byte[] responseValues = response.getContentAsByteArray();

			if (responseValues != null && responseValues.length > 0)
			{
				System.out.println(new String(responseValues,"UTF-8"));
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			Assert.assertTrue(false);
		}

	}

	public void testDoPUT() {
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		request.setParameter(ResourceBoard.RESOURCE_ID, "1");
		request.setParameter("bid", "10");
		request.setMethod("PUT");
		
		Continuation continuation = new MockContinuation();
		request.setAttribute(Continuation.ATTRIBUTE,continuation);
		
		try
		{
			resourceBoard.doAction(request, response);
			
			byte[] responseValues = response.getContentAsByteArray();

			if (responseValues != null && responseValues.length > 0)
			{
				System.out.println(new String(responseValues,"UTF-8"));
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			Assert.assertTrue(false);
		}


	}

}
