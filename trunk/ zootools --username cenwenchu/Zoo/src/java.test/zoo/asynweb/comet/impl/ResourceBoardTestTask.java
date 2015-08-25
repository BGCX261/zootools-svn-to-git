/**
 * 
 */
package zoo.asynweb.comet.impl;

import org.eclipse.jetty.continuation.Continuation;
import org.junit.Assert;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import zoo.util.test.AbstractTestTask;

/**
 * @author fangweng
 * @email fangweng@taobao.com
 * @date 2010-11-12
 *
 */
public class ResourceBoardTestTask extends AbstractTestTask{

	ResourceBoard resourceBoard;
	
	public ResourceBoardTestTask()
	{	
	}
	
	@Override
	public boolean doTask() 
	{

		try 
		{

			this.testDoPUT();
		
			this.testDoDELETE();
			
			this.testDoPOST();
		} 
		catch (Exception e) {
			return false;
		}
		
		return true;
	}
	
	public ResourceBoard getResourceBoard() {
		return resourceBoard;
	}

	public void setResourceBoard(ResourceBoard resourceBoard) {
		this.resourceBoard = resourceBoard;
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
					//System.out.println(new String(responseValues,"UTF-8"));
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
				//System.out.println(new String(responseValues,"UTF-8"));
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
				//System.out.println(new String(responseValues,"UTF-8"));
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
				//System.out.println(new String(responseValues,"UTF-8"));
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			Assert.assertTrue(false);
		}


	}

}
