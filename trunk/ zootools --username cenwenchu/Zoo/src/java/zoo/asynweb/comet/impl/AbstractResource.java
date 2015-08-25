/**
 * 
 */
package zoo.asynweb.comet.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationSupport;
import zoo.asynweb.comet.IResource;

/**
 * @author fangweng
 * @email fangweng@taobao.com
 * @date 2010-11-8
 *
 */
public abstract class AbstractResource implements IResource{
	
	private static final Log logger = LogFactory.getLog(AbstractResource.class);
	protected List<Continuation> follows = new CopyOnWriteArrayList<Continuation>();
	private long lastModify;

	/* 
	 * 返回关注数据，保持通道连接，接受后续资源变化
	 */
	@Override
	public IResource doGet(HttpServletRequest request,
			HttpServletResponse response) {
		
		
		if (ContinuationSupport.getContinuation(request).isInitial())
		{
			Continuation continuation = ContinuationSupport.getContinuation(request);
			
			AsynEventListener listener = new AsynEventListener();
			listener.setRemoteIp(request.getRemoteAddr());
			continuation.addContinuationListener(listener);
			continuation.suspend(response);
			continuation.setTimeout(-1);
			
			follows.add(continuation);
		}
		
		return this;
	}
	
	/**
	 * 最后更新时间
	 * @return
	 */
	public long getLastModify(){
		return this.lastModify;
	}
	
	/**
	 * 设置最后更新时间
	 * @param lastmodify
	 */
	public void setLastModify(Long lastModify)
	{
		this.lastModify = lastModify;
	}

	public List<Continuation> getFollows() {
		return follows;
	}
	


}
