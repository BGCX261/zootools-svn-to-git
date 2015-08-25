package zoo.asynweb.pipe.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import zoo.asynweb.pipe.IPipeContext;


/**
 * 上下文管理类
 * @author fangweng(wenchu)
 * @email fangweng@taobao.com
 *
 */
public class PipeContextManager
{
	private static final Log logger = LogFactory.getLog(PipeContextManager.class);
	
	/**
	 * 用于记录管道处理的上下文，线程独享
	 */
	static ThreadLocal<IPipeContext> _context =
		new ThreadLocal<IPipeContext>();
	
	/**
	 * 设置上下文
	 * @param context
	 */
	public static void setContext(IPipeContext context)
	{
		if (context == null)
			return;
		
		_context.set(context);
	}
	
	/**
	 * 获取上下文
	 * @return
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static IPipeContext getContext()
	{
		return _context.get();
	}
	
	/**
	 * 移除上下文
	 */
	public static void removeContext(boolean isDeepClear)
	{
		IPipeContext c = _context.get();
		
		try
		{
			if (c != null && isDeepClear)
			{
				c.removeAttachments();
			}
		}
		catch(Exception ex)
		{
			logger.error("removeContext error!",ex);
		}
		finally
		{
			_context.set(null);
			_context.remove();
		}
	}
}
