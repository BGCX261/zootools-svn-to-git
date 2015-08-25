/**
 * 
 */
package zoo.asynweb.pipe;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;




/**
 * 管道管理接口
 * @author fangweng(wenchu)
 * @email fangweng@taobao.com
 *
 */
public interface IPipeManager <I extends IPipeInput,R extends IPipeResult,D extends IPipeData<I,R>>
{
	/**
	 * 初始化资源
	 */
	public void init();
	
	/**
	 * 释放资源
	 */
	public void destory();
	
	/**
	 * 执行管道链，建立管道上下文，执行管道链调用，返回管道执行结果
	 * 执行顺序和注册的过程保持一致
	 * @param request
	 * @param response
	 * @return
	 */
	public R doPipes(I pipeInput);
	
	/**
	 * 获取注册管道
	 * @return
	 */
	public IPipe<? super I, ? super R>[] getPipes();
	
	/**
	 * 注册管道
	 * @param pipe
	 */
	public void register(IPipe<? super I, ? super R> pipe);
	
	/**
	 * 注销管道
	 * @param pipe
	 */
	public void unregister(IPipe<? super I, ? super R> pipe);
	
	/**
	 * 移除所有管道
	 */
	public void removeAll();
	
	/**
	 * 批量注册管道
	 * @param pipes
	 */
	public void addAll(Collection<IPipe<? super I, ? super R>> pipes);
	
	/**
	 * 批量替换
	 * @param pipes
	 */
	public void replaceAll(Collection<IPipe<? super I, ? super R>> pipes);
	
	
	/**
	 * 将异步管道执行结果放入内部结果队列
	 * @param data
	 */
	public void addAsynPipeResult(D data);
	
	/**
	 * 将异步管道执行结果从内部结果队列移除
	 * @param data
	 */
	public void removeAsynPipeResult(D data);
	

	/**
	 * 获得PipeInput实例
	 * @param request
	 * @param response
	 * @return
	 */
	public abstract I getPipeInput(HttpServletRequest request,
			HttpServletResponse response);


	/**
	 * 获得PipeResult实例
	 * @return
	 */
	public abstract R getPipeResult();

	/**
	 * 获得异步化过程中的pipe上下文数据对象
	 * @param pipeInput
	 * @param result
	 * @param context
	 * @return
	 */
	public abstract D getPipeData(I pipeInput, R result,IPipeContext context);
	
	/**
	 * 用于扩展业务任务分配资源的函数
	 * @param data
	 * @param pipe
	 * @return
	 */
	public abstract boolean submitAsynTask(D data,IPipe<? super I, ? super R> pipe);
	
	
}
