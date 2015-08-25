/**
 * 
 */
package zoo.asynweb.comet;



import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.continuation.Continuation;

/**
 * @author fangweng
 * @email fangweng@taobao.com
 * @date 2010-11-3
 *
 */
public interface IResource {
	
	/**
	 * 获取资源
	 * @return
	 */
	public IResource doGet(HttpServletRequest request,
			HttpServletResponse response);
	
	/**
	 * 新增资源
	 * @param res
	 */
	public void doPost(HttpServletRequest request,
			HttpServletResponse response);
	
	/**
	 * 修改资源
	 * @param res
	 */
	public void doPut(HttpServletRequest request,
			HttpServletResponse response);
	
	/**
	 * 删除资源，释放必要的内部资源引用
	 * @return
	 */
	public IResource doDelete(HttpServletRequest request,
			HttpServletResponse response);
	
	
	/**
	 * 转化资源为字符串，可以是json,xml,html等字符串
	 * @param res
	 * @return
	 */
	public String transformR2Str();
	
	/**
	 * 获取变更通知的字符串
	 * @return
	 */
	public String getNotifyStr();
	
	
	/**
	 * 最后更新时间
	 * @return
	 */
	public long getLastModify();
	
	/**
	 * 设置最后更新时间
	 * @param lastmodify
	 */
	public void setLastModify(Long lastmodify);
	
	/**
	 * 获取关注者列表
	 * @return
	 */
	public List<Continuation> getFollows();
	
	
}
