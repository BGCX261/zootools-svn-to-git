package zoo.asynweb.pipe;

/**
 * 管道处理的输出，属于管道上下文的一部分
 * @author fangweng(wenchu)
 * @email fangweng@taobao.com
 *
 */
public interface IPipeResult extends java.io.Serializable
{
	public static final int STATUS_UNDO = 0;
	public static final int STATUS_DONE = 1;
	public static final int STATUS_CANCEL = 2;
	
	/**
	 * 是否跳出管道链
	 * @return
	 */
	public boolean isBreakPipeChain();
	
	/**
	 * 设置是否跳出管道
	 * @param breakPipeChain
	 */
	public void setBreakPipeChain(boolean breakPipeChain);
	
	/**
	 * 任务是否已经完成
	 * @return
	 */
	public boolean isDone();
	
	/**
	 * 任务是否已经取消
	 * @return
	 */
	public boolean isCancelled();
	
	/**
	 * 获取具体的结果
	 * @return
	 */
	public Object getResult();
	
	/**
	 * 设置状态
	 * @param status
	 */
	public void setStatus(int status);
	
	/**
	 * 设置处理后的结果
	 * @param result
	 */
	public void setResult(Object result); 
	
	public Exception getException(); 

	public void setException(Exception exception);
	
}
