/**
 * 
 */
package zoo.asynweb.comet.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationListener;

/**
 * @author fangweng
 * @email fangweng@taobao.com
 * @date 2010-11-9
 *
 */
public class AsynEventListener implements ContinuationListener{
	private static final Log logger = LogFactory.getLog(AsynEventListener.class);

	String remoteIp;
	
	
	public void setRemoteIp(String remoteIp) {
		this.remoteIp = remoteIp;
	}

	@Override
	public void onComplete(Continuation continuation) {
		if (logger.isInfoEnabled())
		{
			logger.info("Remote : " + this.remoteIp + " leave.");
		}
		
	}

	@Override
	public void onTimeout(Continuation continuation) {
		if (logger.isWarnEnabled())
		{
			logger.warn("Remote : " + this.remoteIp + " time out.");
		}
		continuation.complete();
	}

}
