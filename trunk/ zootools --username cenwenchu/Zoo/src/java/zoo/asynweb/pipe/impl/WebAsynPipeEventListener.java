package zoo.asynweb.pipe.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationListener;

/**
 * @author fangweng(wenchu)
 * @email fangweng@taobao.com
 *
 * @param <I>
 * @param <R>
 * @param <D>
 */
public class WebAsynPipeEventListener <I extends WebPipeInput, 
		R extends WebPipeResult,D extends WebPipeData<I,R>>  implements ContinuationListener{

	private static final Log logger = LogFactory.getLog(WebAsynPipeEventListener.class);
	
	AbstractWebPipeManager<I,R,D> webPipeManager;
	D data;
	
	
	public WebAsynPipeEventListener(AbstractWebPipeManager<I,R,D> webPipeManager,D data)
	{
		this.webPipeManager = webPipeManager;
		this.data = data;
	}
	
	@Override
	public void onComplete(Continuation continuation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTimeout(Continuation continuation) {
		// TODO Auto-generated method stub
		
		logger.error("Asyn Web Request time out.");
		webPipeManager.removeAsynPipeResult(data);
		continuation.complete();
	}

}
