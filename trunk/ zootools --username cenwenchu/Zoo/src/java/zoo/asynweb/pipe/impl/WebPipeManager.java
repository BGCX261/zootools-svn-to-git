/**
 * 
 */
package zoo.asynweb.pipe.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import zoo.asynweb.pipe.IPipe;
import zoo.asynweb.pipe.IPipeContext;

/**
 * @author fangweng
 * @email fangweng@taobao.com
 * @date 2010-11-1
 *
 */
public class WebPipeManager extends 
	AbstractWebPipeManager<WebPipeInput,WebPipeResult,WebPipeData<WebPipeInput,WebPipeResult>>{

	private static final Log logger = LogFactory.getLog(WebPipeManager.class);
	
	@Override
	public WebPipeInput getPipeInput(HttpServletRequest request,
			HttpServletResponse response) {
		
		return new WebPipeInput(request,response);
	}

	@Override
	public WebPipeResult getPipeResult() {
		// TODO Auto-generated method stub
		return new WebPipeResult();
	}

	@Override
	public WebPipeData<WebPipeInput, WebPipeResult> getPipeData(
			WebPipeInput pipeInput, WebPipeResult result, IPipeContext context) {
		// TODO Auto-generated method stub
		return new WebPipeData<WebPipeInput,WebPipeResult>(context,pipeInput,result);
	}

	@Override
	public boolean submitAsynTask(
			WebPipeData<WebPipeInput, WebPipeResult> data,
			IPipe<? super WebPipeInput, ? super WebPipeResult> pipe) {
		
		try
		{
			_threadPool.submit(new WebPipeTask<WebPipeInput,WebPipeResult
					,WebPipeData<WebPipeInput,WebPipeResult>>(data,pipe,this,false));
		}
		catch(Exception ex)
		{
			logger.error("submit AsynTask error.",ex);
			return false;
		}
		
		
		return true;
	}

	

}
