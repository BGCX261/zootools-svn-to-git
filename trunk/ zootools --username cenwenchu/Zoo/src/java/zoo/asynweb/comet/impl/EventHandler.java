/**
 * 
 */
package zoo.asynweb.comet.impl;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationSupport;

import zoo.asynweb.comet.IResource;
import zoo.asynweb.comet.ResourceEvent;

/**
 * @author fangweng
 * @email fangweng@taobao.com
 * @date 2010-11-9
 *
 */
public class EventHandler implements java.lang.Runnable{

	private static final Log logger = LogFactory.getLog(EventHandler.class);
	
	ResourceEvent event;
	
	public EventHandler(ResourceEvent event)
	{
		this.event = event;
	}
	
	@Override
	public void run() {
		
		if (event != null && event.getResource() != null)
		{
			IResource resource = event.getResource();
			
			
			if (event.getEventType().equals(ResourceEvent.MODIFY_EVENT))
			{
				//通知所有关注这个资源的链接
				Continuation con = ContinuationSupport.getContinuation(event.getRequest());
				List<Continuation> follows = resource.getFollows();
				
				Iterator<Continuation> iter = follows.iterator();
				
				while(iter.hasNext())
				{
					Continuation follow = iter.next();
					
					if (follow == con)
						continue;
					
					try
					{
						follow.getServletResponse().getOutputStream()
							.write(resource.getNotifyStr().getBytes("UTF-8"));
						follow.getServletResponse().getOutputStream().flush();
					}
					catch(Exception ex)
					{
						logger.error("notify error.",ex);
						try
						{
							follows.remove(follow);
							follow.complete();
						}
						catch(Exception e)
						{
							logger.error(e);
						}
						
					}
				}
				
				if (logger.isInfoEnabled())
					logger.info("Modify event happen,resource id : " 
							+ event.getResourceId()+ " , follower count : " + follows.size());
				
			}
			else
				if (event.getEventType().equals(ResourceEvent.ERROR_EVENT))
				{
					//关闭事件中指定的某个长连接
					Continuation con = ContinuationSupport.getContinuation(event.getRequest());
					
					List<Continuation> follows = resource.getFollows();
					
					Iterator<Continuation> iter = follows.iterator();
					
					while(iter.hasNext())
					{
						Continuation follow = iter.next();
						
						if (follow == con)
						{
							follows.remove(follow);
							follow.complete();
							
							if (logger.isInfoEnabled())
								logger.info("ERROR event happen,resource id : " 
										+ event.getResourceId() + " , follower ip : " + event.getRequest().getRemoteAddr());
							
							break;
						}

					}
					
					
				}
				else
					if (event.getEventType().equals(ResourceEvent.DELETE_EVENT))
					{
						//关闭关注这个资源的所有链接
						List<Continuation> follows = resource.getFollows();
						
						Iterator<Continuation> iter = follows.iterator();
						
						while(iter.hasNext())
						{
							Continuation follow = iter.next();
							follow.complete();
						}
						
						follows.clear();
						
						if (logger.isInfoEnabled())
							logger.info("Delete event happen, resource id : " 
									+ event.getResourceId() + " , follower count : " + follows.size());
					}
		}
		
	}

}
