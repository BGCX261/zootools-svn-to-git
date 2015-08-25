package zoo.asynweb.pipe;


import zoo.asynweb.pipe.impl.AbstractWebPipe;
import zoo.asynweb.pipe.impl.WebPipeInput;
import zoo.asynweb.pipe.impl.WebPipeResult;


public class MockWebPipe extends AbstractWebPipe{
	
	public MockWebPipe(String pipeName)
	{
		this.setPipeName(pipeName);
	}
	
	@Override
	public void doPipe(WebPipeInput pipeInput, WebPipeResult pipeResult) {
		if (pipeInput.getParameter("sleep") != null)
		{
			try
			{
				Thread.sleep(Long.valueOf((String)(pipeInput.getParameter("sleep"))));
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		
		if (this.isAsynMode())
		{
			try
			{
				Thread.sleep(1000);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		
		pipeResult.setResult(this.getPipeName());
		
		if (pipeInput.getParameter("isbreak") != null)
		{
			pipeResult.setBreakPipeChain(true);
		}
	}

	@Override
	public boolean ignoreIt(WebPipeInput pipeInput, WebPipeResult pipeResult) {
		
		if (pipeInput.getParameter("ignoreName") != null 
				&& pipeInput.getParameter("ignoreName").equals(this.getPipeName()))
			return true;
		else
			return false;
	}

	@Override
	public boolean ignoreAsynMode(WebPipeInput pipeInput,
			WebPipeResult pipeResult) {
		// TODO Auto-generated method stub
		return false;
	}


}
