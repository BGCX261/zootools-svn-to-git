/**
 * 
 */
package zoo.asynweb.pipe;

import java.util.Map;


/**
 * 管道流程中的上下文对象
 * 用于传递流程处理中的结果及复用从tim或者集中式缓存中得到的数据对象
 * @author fangweng(wenchu)
 * @email fangweng@taobao.com
 *
 */
public interface IPipeContext
{

	/**
	 * @param 可以在上下文中放置任何的附加对象传递给后续的管道使用
	 */
	public Object getAttachment(String name);
	
	/**
	 * @param  可以在上下文中放置任何的附加对象传递给后续的管道使用
	 */
	public void setAttachment(String name,Object attachment);


	/**
	 * 清理所有的附件
	 */
	public void removeAttachments();
	
	/**
	 * 移出某一个附件
	 * @param name
	 */
	public void removeAttachment(String name);
	
	/**
	 * 加入所有的附件
	 * @param atts
	 */
	public void addAllAttachments(Map<String,Object> atts);
	

}
 
