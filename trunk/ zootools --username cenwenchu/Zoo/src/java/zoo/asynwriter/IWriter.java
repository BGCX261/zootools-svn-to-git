package zoo.asynwriter;

/**
 * @author fangweng(wenchu)
 * @email fangweng@taobao.com
 *
 */
public interface IWriter<T>
{
	/**
	 * 单个输出
	 * @param content
	 */
	public void write(T content);
	
	/**
	 * 批量输出
	 * @param bundle
	 */
	public void write(RecordBundle<T> bundle);
}
