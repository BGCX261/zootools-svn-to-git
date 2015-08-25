/**
 * 
 */
package zoo.asynweb.pipe;

/**
 * 包含了Pipe的所有数据，input,result,context，
 * 用于保存执行环境的数据
 * @author fangweng(wenchu)
 * @email fangweng@taobao.com
 *
 */
public interface IPipeData<I extends IPipeInput, R extends IPipeResult> {

	/**
	 * @return
	 */
	public I getPipeInput();
	/**
	 * @return
	 */
	public R getPipeResult();
	/**
	 * @return
	 */
	public IPipeContext getPipeContext();
	
	/**
	 * @param pipeInput
	 */
	public void setPipeInput(I pipeInput);
	/**
	 * @param pipeResult
	 */
	public void setPipeResult(R pipeResult);
	/**
	 * @param pipeContext
	 */
	public void setPipeContext(IPipeContext pipeContext);
}
