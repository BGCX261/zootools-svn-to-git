package zoo.asynweb.comet;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author fangweng
 * @email fangweng@taobao.com
 * @date 2010-11-3
 *
 */
public interface IResourceBoard {

	
	/**
	 * 对资源请求做相应处理，支持get,post,delete,put。
	 * @param request
	 * @param response
	 */
	public void doAction(HttpServletRequest request,
			HttpServletResponse response)throws IOException;
	
	/**
	 * 获取所有注册了的资源列表，用于客户端可以关注
	 * @return
	 */
	public String[] getResoures();
	
	
}
