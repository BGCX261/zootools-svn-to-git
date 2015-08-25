/**
 * 
 */
package zoo.asynweb.pipe.impl;

import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationSupport;
import zoo.asynweb.pipe.IPipe;
import zoo.asynweb.pipe.IPipeContext;
import zoo.asynwriter.IWriter;
import zoo.lazyparser.LazyParser;


/**
 * Web管道管理类
 * @author fangweng(wenchu)
 * @email fangweng@taobao.com
 *
 */
public abstract class AbstractWebPipeManager <I extends WebPipeInput, R extends WebPipeResult,D extends WebPipeData<I,R>>
		extends AbstractPipeManager<I,R,D>
{
	private static final Log logger = LogFactory.getLog(AbstractWebPipeManager.class);
	public static final String CURRENT_PIPE = "_current_pipe_";
	public static final String PIPE_LOG = "_pipe_log_";
	
	protected ThreadPoolExecutor _threadPool;
	private int maxThreadCount = 500;//线程池最大线程数
	private int maxQueueLength = 2000;//线程池最大队列长度
	private volatile int isSupportAsynMode = -1;//-1表示还不确定，0表示支持异步模式，1表示不支持异步模式
	private int maxResultQueueLength = 10000;//结果集队列最大长度
	private int maxAsynTimeOut = 60 * 5;//5分钟钟作为默认的异步超时时间
	
	private int maxCheckerCount = 3;//最大的检查线程连接数
	
	private AtomicInteger counter = new AtomicInteger(0);
	
	AsynTaskChecker<I,R,D>[] asynTaskCheckers;//用于检查异步任务执行状况的工作者
	
	private IWriter<WebPipeLog> asynLogWriter;
	

	public void setAsynLogWriter(IWriter<WebPipeLog> asynLogWriter)
	{
		this.asynLogWriter = asynLogWriter;
	}
	
	public int getMaxThreadCount() {
		return maxThreadCount;
	}



	public void setMaxThreadCount(int maxThreadCount) {
		this.maxThreadCount = maxThreadCount;
	}


	public int getMaxQueueLength() {
		return maxQueueLength;
	}



	public void setMaxQueueLength(int maxQueueLength) {
		this.maxQueueLength = maxQueueLength;
	}



	public int getMaxResultQueueLength() {
		return maxResultQueueLength;
	}



	public void setMaxResultQueueLength(int maxResultQueueLength) {
		this.maxResultQueueLength = maxResultQueueLength;
	}



	public int getMaxAsynTimeOut() {
		return maxAsynTimeOut;
	}



	public void setMaxAsynTimeOut(int maxAsynTimeOut) {
		this.maxAsynTimeOut = maxAsynTimeOut;
	}

	public void getConfigFromJvmParams()
	{
		try
		{
			//get -DmaxThreadCount="xx" 从脚本获得内部最大线程池
			if (System.getProperty("maxThreadCount") != null)
			{
				maxThreadCount = Integer.valueOf(System.getProperty("maxThreadCount"));
				logger.warn("use maxThreadCount :" + maxThreadCount);
			}
			
			if (System.getProperty("maxQueueLength") != null)
			{
				maxQueueLength = Integer.valueOf(System.getProperty("maxQueueLength"));
				logger.warn("use maxQueueLength :" + maxQueueLength);
			}
			
			if (System.getProperty("maxResultQueueLength") != null)
			{
				maxResultQueueLength = Integer.valueOf(System.getProperty("maxResultQueueLength"));
				logger.warn("use maxResultQueueLength :" + maxResultQueueLength);
			}
			
			if (System.getProperty("maxAsynTimeOut") != null)
			{
				maxAsynTimeOut = Integer.valueOf(System.getProperty("maxAsynTimeOut"));
				logger.warn("use maxAsynTimeOut :" + maxAsynTimeOut);
			}
			
			if (System.getProperty("maxCheckerCount") != null)
			{
				maxCheckerCount = Integer.valueOf(System.getProperty("maxCheckerCount"));
				logger.warn("use maxCheckerCount :" + maxCheckerCount);
			}
			
		}
		catch(Exception ex)
		{
			logger.error(ex);
		}
	}

	public AbstractWebPipeManager()
	{
		init();
	}

	@SuppressWarnings("unchecked")
	public void init()
	{			
		getConfigFromJvmParams();
		
		_threadPool = new ThreadPoolExecutor(maxThreadCount, maxThreadCount,
                			0L, TimeUnit.MILLISECONDS,
                				new LinkedBlockingQueue<Runnable>(maxQueueLength),
                				new NamedThreadFactory("asyn-pipe-worker"));		
		
		asynTaskCheckers = new AsynTaskChecker[maxCheckerCount];
		
		for(int i =0 ; i < maxCheckerCount; i++)
		{
			asynTaskCheckers[i] = new AsynTaskChecker<I,R,D>(this,_threadPool
															,counter,maxResultQueueLength);
			asynTaskCheckers[i].setDaemon(true);
			asynTaskCheckers[i].start();
		}
		
	}
	
	
	public void destory()
	{
		if (_threadPool != null)
			_threadPool.shutdownNow();
		
		if (asynTaskCheckers != null && asynTaskCheckers.length > 0)
		{
			for(int i =0 ; i < maxCheckerCount; i++)
			{
				try
				{
					asynTaskCheckers[i].stopThread();
				}
				catch(Exception ex)
				{
					logger.error("AbstractWebPipeManager destory error!",ex);
				}
			}
		}
	}
	
	
	/**
	 * 每个实现TopPipeManager的类必须实现这个方法，在执行doPipes方法之前准备数据
	 * 
	 */
	public void prepare() {
		
		IPipeContext context = PipeContextManager.getContext();
		
		if (context == null)
		{
			context = new WebPipeContext();
			PipeContextManager.setContext(context);
		}
		
		//放置日志对象到上下文中
		if (context.getAttachment(PIPE_LOG) == null)
		{
			WebPipeLog log = new WebPipeLog();
			context.setAttachment(PIPE_LOG,log);
		}
		
	}
	
	/**
	 * 异步支持的检查
	 * @param request
	 */
	protected int asynSupportCheck(HttpServletRequest request)
	{
		//不用做并发保护
		if(isSupportAsynMode == -1)
		{
			Continuation continuation = null;
			
			try
			{
				continuation = ContinuationSupport.getContinuation(request);
			}
			catch(Exception ex)
			{
				isSupportAsynMode = 1;
			}
			
			if(continuation != null)
				isSupportAsynMode = 0;
		}
		
		return isSupportAsynMode;
	}
	
	private void asynSupportSuspend(HttpServletRequest request,HttpServletResponse response,D data)
	{
		if (isSupportAsynMode == 0)
		{
			Continuation continuation = ContinuationSupport.getContinuation(request);
			continuation.addContinuationListener(new WebAsynPipeEventListener<I,R,D>(this,data));
			continuation.suspend(response);
			continuation.setTimeout(maxAsynTimeOut * 1000);
		}
	}
	
	private void asynSupportComplete(HttpServletRequest request)
	{
		try
		{
			if (isSupportAsynMode == 0)
			{
				Continuation continuation = ContinuationSupport.getContinuation(request);
				if (continuation.isSuspended())
					continuation.complete();
			}
		}
		catch(Exception ex)
		{
			logger.error("asynSupportComplete error!",ex);
		}
		
	}
	
	
	/**
	 * 异步重入的另一个管道执行接口方法，暂时先和普通的管道执行接口分开
	 * @param data
	 * @return
	 */
	public R doPipes(WebPipeData<I,R> data)
	{
		R result = data.getPipeResult();
		PipeContextManager.setContext(data.getPipeContext());
		I pipeInput = data.getPipeInput();
		
		return doPipes(pipeInput,result);
	}
	
	
	/**
	 * 执行管道链，建立管道上下文，执行管道链调用，返回管道执行结果
	 * 执行顺序和注册的过程保持一致
	 * @return
	 */
	@Override
	public R doPipes(I pipeInput) 
	{
		asynSupportCheck(pipeInput.getRequest());
		R result = this.getPipeResult();
		prepare();
		
		return doPipes(pipeInput,result);
	}
	
	private R doPipes(I pipeInput,R result)
	{
		WebPipeLog webPipeLog = null;
		boolean hasAsynPipe = false;

		try
		{
				
			//TODO 需要检查TopPipeInput和TopPipeResult是否已经准备好了。
			IPipeContext context = PipeContextManager.getContext();
			Iterator<IPipe<? super I, ? super R>> iters = pipeChain.iterator();

			
			//取到上下文中的日志对象
			webPipeLog = (WebPipeLog)context.getAttachment(PIPE_LOG);
				
			if (context.getAttachment(CURRENT_PIPE) != null)
			{
				while(iters.hasNext())
				{
					IPipe<? super I, ? super R> pipe = iters.next();
				
					if (pipe.equals(context.getAttachment(CURRENT_PIPE)))
					{
						context.removeAttachment(CURRENT_PIPE);
						
						webPipeLog.addTimeStamp(pipe.getPipeName(), 
									System.currentTimeMillis() - webPipeLog.getPipeBegTimeStamp());
						
						break;
					}
				}
			}
				

			while(iters.hasNext())
			{
				IPipe<? super I, ? super R> pipe = iters.next();
			
				
				try
				{			
					if (!pipe.ignoreIt(pipeInput, result))
					{
						webPipeLog.setPipeBegTimeStamp(System.currentTimeMillis());
						
						//根据Pipe的类型判断是否异步执行
						if (isSupportAsynMode == 1 || !pipe.isAsynMode() 
								|| (pipe.isAsynMode() && pipe.ignoreAsynMode(pipeInput, result)))
						{
							pipe.doPipe(pipeInput, result);
							
							webPipeLog.addTimeStamp(pipe.getPipeName(), 
										System.currentTimeMillis() - webPipeLog.getPipeBegTimeStamp());
						}
						else
						{
							logger.info("asyn pipe execute...");
							
							context.setAttachment(CURRENT_PIPE, pipe);
							D tempData = this.getPipeData(pipeInput,result,context);
							asynSupportSuspend(pipeInput.getRequest(), pipeInput.getResponse(),tempData);
							submitAsynTask(tempData,pipe);
							hasAsynPipe = true;
							break;
						}
					}
					
					if (result != null && result.isBreakPipeChain())
					{
						break;
					}
					
				}
				catch(Exception ex)
				{
					result.setException(ex);
					logger.error("do Pipes error!",ex);
				}	
			}
		}
		catch(Exception ex)
		{
			logger.error("do Pipes error!",ex);
		}
		finally
		{
			if (!hasAsynPipe)
			{
				asynSupportComplete(pipeInput.getRequest());
				asynLogWriter.write(webPipeLog);
				PipeContextManager.removeContext(true);
			}
			else
				PipeContextManager.removeContext(false);

			LazyParser.release();		
		}
			
			
		return result;
	}

	@Override
	public void addAsynPipeResult(D data)
	{
		
		if (asynTaskCheckers != null && asynTaskCheckers.length > 0)
		{
			if (asynTaskCheckers.length == 1)
				asynTaskCheckers[0].addAsynPipeResult(data);
			else
			{
				
				int hash = data.hashCode();
				if (hash < 0)
					hash = -hash;
				
				asynTaskCheckers[hash % maxCheckerCount].addAsynPipeResult(data);
			}
		}
		else
			throw new java.lang.RuntimeException("asynTaskCheckers is null or length = 0...");
		
	}
	
	@Override
	public void removeAsynPipeResult(D data)
	{
		if (asynTaskCheckers != null && asynTaskCheckers.length > 0)
		{
			if (asynTaskCheckers.length == 1)
				asynTaskCheckers[0].removeAsynPipeResult(data);
			else
			{
				int hash = data.hashCode();
				if (hash < 0)
					hash = -hash;
				
				asynTaskCheckers[hash % maxCheckerCount].removeAsynPipeResult(data);
			}
		}
		else
			throw new java.lang.RuntimeException("asynTaskCheckers is null or length = 0...");
	}
	
}