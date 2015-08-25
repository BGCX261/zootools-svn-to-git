package zoo.asynweb.pipe.impl;


import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import zoo.asynweb.pipe.IPipe;
import zoo.lazyparser.LazyParser;

/**
 * 异步执行管道的任务封装
 * @author fangweng(wenchu)
 * @email fangweng@taobao.com
 *
 */
public class WebPipeTask<I extends WebPipeInput, R extends WebPipeResult,D extends WebPipeData<I,R>> 
			implements Runnable{

	private static final Log logger = LogFactory.getLog(WebPipeTask.class);
			
	D data;
	IPipe<? super I, ? super R> pipe = null;
	AbstractWebPipeManager<I,R,D> webPipeManager = null;
	boolean isAsyn = false;//是否是纯异步方式，还是同步等待服务返回
	
	public WebPipeTask(D webData,IPipe<? super I, ? super R> pipe
			,AbstractWebPipeManager<I,R,D> webPipeManager,boolean isAsyn)
	{
		data = webData;
		this.pipe = pipe;
		this.webPipeManager = webPipeManager;
		this.isAsyn = isAsyn;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void run()
	{
		try
		{
			if (data.getPipeContext().getAttachment(LazyParser.LAZY_CONTEXT) != null)
			{
				
				LazyParser.addParams2LazyContext(
						(Map<String,Object>)data.getPipeContext().getAttachment(LazyParser.LAZY_CONTEXT));
				
				data.getPipeContext().removeAttachment(LazyParser.LAZY_CONTEXT);
			}
			
			PipeContextManager.setContext(data.getPipeContext());
			
			//可能是同步也可能是异步，如果是异步需要在执行完毕后设置
			//data.getPipeResult().setStatus(IPipeResult.STATUS_DONE);
			if (pipe != null)
				pipe.doPipe(data.getPipeInput(), data.getPipeResult());
					
			if (isAsyn)
				webPipeManager.addAsynPipeResult(data);
			else
				webPipeManager.doPipes(data);
		}
		catch(Exception ex)
		{
			logger.error("WebPipeTask error!",ex);
		}
		
	}

}
