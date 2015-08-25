/**
 * 
 */
package zoo.asynweb.comet.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import zoo.asynweb.comet.HttpAction;
import zoo.asynweb.comet.IResource;
import zoo.asynweb.comet.IResourceBoard;
import zoo.asynweb.comet.ResourceEvent;
import zoo.asynweb.pipe.impl.NamedThreadFactory;

/**
 * @author fangweng
 * @email fangweng@taobao.com
 * @date 2010-11-8
 *
 */
public class ResourceBoard implements IResourceBoard,Runnable{

	private static final Log logger = LogFactory.getLog(ResourceBoard.class);
	
	public static final String RESOURCE_ID = "resourceId";
	public static final String RESOURCE_TYPE = "rtype";
	public static final String REQUEST_METHOD = "_method";
	
	private BlockingQueue<ResourceEvent> eventQueue;
	private Map<String,IResource> resources;
	private ThreadPoolExecutor notifyWorkers;
	private int maxProcessPageSize = 10;
	private boolean runFlag = true;
	private Thread _innerThread;
	
	
	/**
	 * 后续做成资源可配置
	 */
	public ResourceBoard()
	{
		eventQueue = new LinkedBlockingQueue<ResourceEvent>();
		resources = new  ConcurrentHashMap<String,IResource>();
		notifyWorkers = new ThreadPoolExecutor(20,50,0,TimeUnit.SECONDS
				,new LinkedBlockingQueue<Runnable>(), 
					new NamedThreadFactory("board_notifyworker"));
		
		_innerThread = new Thread(this);
		_innerThread.setDaemon(true);
		_innerThread.setName("ResourceBoard-Backend-Thread");
		_innerThread.start();
	}
	
	private void destory()
	{
		eventQueue.clear();
		
		resources.clear();
		
		notifyWorkers.shutdown();
	}
	
	public void stop()
	{
		runFlag = false;
		_innerThread.interrupt();
	}


	@Override
	public void doAction(HttpServletRequest request,
			HttpServletResponse response) throws IOException{
		
		String resourceId = request.getParameter(RESOURCE_ID);
		
		boolean flag = isResourceExist(resourceId);
		
		//浏览器大部分其实还不兼容put,delete方式提交，因此如果传入隐含_method就认为可替换request method
		String method = request.getMethod().toLowerCase();
		if (request.getParameter(REQUEST_METHOD) != null 
				&& !"".equals(request.getParameter(REQUEST_METHOD)))
			method = request.getParameter(REQUEST_METHOD);
		
		HttpAction action = HttpAction.getAction(method);
		
		if (flag && action.equals(HttpAction.POST))
		{
			response.sendError(HttpServletResponse.SC_CONFLICT,
					new StringBuilder("resourceId: ").append(resourceId).append(" already exist.").toString());
			return;
		}
		
		if (!flag && action.equals(HttpAction.POST))
			flag = true;
		
		if (!flag)
		{
			response.sendError(HttpServletResponse.SC_GONE,
					new StringBuilder("resourceId: ").append(resourceId).append(" not exist.").toString());
			return;
		}
		
		IResource resource = null;
		
		try
		{
			resource = resources.get(resourceId);
			
			//以后可以设置对照表
			String resourceType = request.getParameter(RESOURCE_TYPE);
			response.setHeader("Content-Type","text/html;charset=utf-8 ");
			
			switch(action)
			{
				case GET:
					doGet(resourceId,resource,request,response);
					break;
					
				case POST:
					doPost(resourceId,resourceType,request,response);
					break;
					
				case PUT:
					doPut(resourceId,resource,request,response);
					break;
					
				case DELETE:
					doDelete(resourceId,resource,request,response);
					break;
			}
		}
		catch(Exception ex)
		{
			//做好回收处理
			if (action.equals(HttpAction.GET))
			{
				ResourceEvent event = new ResourceEvent();
				event.setRequest(request);
				event.setResource(resource);
				event.setEventType(ResourceEvent.ERROR_EVENT);
				event.setResourceId(resourceId);
				
				this.eventQueue.offer(event);
			}
			
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					ex.getMessage());
			
		}

	}
	
	
	/**
	 * 关注资源变化，当前立即返回状态，后续有变更也会接收更新
	 * @param resource
	 * @param request
	 * @param response
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	private void doGet(String resourceId,IResource resource,HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException, IOException
	{
		resource.doGet(request, response);
		
		response.getOutputStream().write(resource.transformR2Str().getBytes("UTF-8"));
		response.flushBuffer();
		
		if (logger.isInfoEnabled())
			logger.info("ip : " + request.getRemoteAddr() 
					+ " focus resource : " + resourceId);
	}
	
	/**
	 * 新增资源
	 * @param resource
	 * @param request
	 * @param response
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ServletException 
	 */
	private void doPost(String resourceId,String resourceType,HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException,
			IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, ServletException
	{
		IResource resource = (IResource)Class.forName(resourceType).newInstance();
		
		resources.put(resourceId, resource);
		
		
		resource.doPost(request,response);
		resource.setLastModify(System.currentTimeMillis());
		
		response.getOutputStream().write(resource.transformR2Str().getBytes("UTF-8"));
		response.flushBuffer();
		
		if (logger.isInfoEnabled())
			logger.info("create resource : " + resourceId + " , resourcetype : " +  resourceType);
		
	}
	
	private void doDelete(String resourceId,IResource resource,HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException, IOException
	{
		resources.remove(resourceId);
		resource.doDelete(request,response);
		
		ResourceEvent event = new ResourceEvent();
		event.setRequest(request);
		event.setResource(resource);
		event.setEventType(ResourceEvent.DELETE_EVENT);
		event.setResourceId(resourceId);
		
		this.eventQueue.offer(event);
	}
	
	private void doPut(String resourceId,IResource resource,HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException, IOException
	{
		resource.doPut(request,response);
		resource.setLastModify(System.currentTimeMillis());
		
		response.getOutputStream().write(resource.transformR2Str().getBytes("UTF-8"));
		response.flushBuffer();
		
		ResourceEvent event = new ResourceEvent();
		event.setRequest(request);
		event.setResource(resource);
		event.setEventType(ResourceEvent.MODIFY_EVENT);
		event.setResourceId(resourceId);
		
		this.eventQueue.offer(event);
	}
	
	
	public boolean isResourceExist(String resourceId)
	{
		if (resourceId == null 
				|| (resourceId != null && resources.get(resourceId) == null))
			return false;
		else
			return true;
	}


	@Override
	public String[] getResoures() {
		return resources.keySet().toArray(new String[resources.size()]);
	}


	/*
	 * 用于检查事件队列，合并事件，
	 * 并分发给通知线程处理事件相应
	 */
	@Override
	public void run() {
		
		
		ResourceEvent event;
		Map<IResource,ResourceEvent> tmpStore 
			= new HashMap<IResource,ResourceEvent>();
		
		while(runFlag)
		{
			try
			{
				tmpStore.clear();
				event = null;
				long beg = System.currentTimeMillis();
				
				//防止空循环太多次，消耗cpu
				event = eventQueue.poll(5, TimeUnit.SECONDS);
				
				while(event != null)
				{
					
					//如果是单个错误，直接处理掉
					if (event.getEventType().equals(ResourceEvent.ERROR_EVENT))
					{
						notifyWorkers.execute(new EventHandler(event));
						continue;
					}
					
					ResourceEvent e = tmpStore.get(event.getResource());
					
					if (e == null || 
							(e != null && e.getEventType().equals(ResourceEvent.MODIFY_EVENT)))
						tmpStore.put(event.getResource(), event);
					
					if (tmpStore.size() > maxProcessPageSize 
							|| (System.currentTimeMillis() - beg > 1000))
						break;
					
					event = eventQueue.poll();
				}
				
				Iterator<IResource> iter = tmpStore.keySet().iterator();
				
				while(iter.hasNext())
				{
					notifyWorkers.execute(new EventHandler(tmpStore.get(iter.next())));
				}
				
			}
			catch(Exception ex)
			{
				if (ex instanceof InterruptedException)
				{
					logger.error("ResourceBoard stoped .");
				}
				else
					logger.error("ResourceBoard process error.",ex);
			}

		}
		
		destory();
		
	}

}
