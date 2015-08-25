/**
 * 
 */
package zoo.lazyparser;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Http请求工具类
 * 
 * @author fangweng(wenchu)
 * @email fangweng@taobao.com
 * 
 */
public class HttpRequestUtil
{

	private static final Log logger = LogFactory.getLog(HttpRequestUtil.class);
	/**
	 * Multipart类型判断符
	 */
	private final static String MULTIPART = "multipart/form-data";


	/**
	 * 采用java原生方式发送Http请求
	 * 
	 * @param 目标url
	 * @param 发送内容
	 * @param 需要上传的消息头
	 * @param Http方法
	 * @param 内容类型
	 * @return
	 */
	public static byte[] sendRequestV2(String url, String content,
			Map<String, String> headers, String method, String contenttype)
	{
		byte[] result = null;

		try
		{
			HttpURLConnection httpConn = (HttpURLConnection) new URL(url)
					.openConnection();

			// Should never cache GData requests/responses
			httpConn.setUseCaches(false);

			// Always follow redirects
			httpConn.setInstanceFollowRedirects(true);

			httpConn.setRequestMethod(method);
			httpConn.setRequestProperty("Content-Type", contenttype);
			httpConn.setRequestProperty("Accept-Encoding", "gzip");

			if (headers != null && headers.size() > 0)
			{
				Iterator<String> keys = headers.keySet().iterator();

				while (keys.hasNext())
				{
					String key = keys.next();
					httpConn.setRequestProperty(key, headers.get(key));
				}
			}

			httpConn.setDoOutput(true);

			if (content != null)
				httpConn.getOutputStream().write(content.getBytes("UTF-8"));

			System.setProperty("http.strictPostRedirect", "true");
			httpConn.connect();

			ByteArrayOutputStream bout = new ByteArrayOutputStream();

			try
			{
				InputStream in = httpConn.getInputStream();

				byte[] buf = new byte[500];
				int count = 0;

				while ((count = in.read(buf)) > 0)
				{
					bout.write(buf, 0, count);
				}

				result = bout.toByteArray();
			} catch (Exception ex)
			{
				ex.printStackTrace();
			} finally
			{
				if (bout != null)
					bout.close();
			}

			System.clearProperty("http.strictPostRedirect");

		} catch (Exception e)
		{
			logger.error(e, e);
		}

		return result;
	}

	
	/**
	 * 判断是否是Multipart的类型请求
	 * 
	 * @param request
	 * @return
	 */
	public static final boolean isMultipartContent(HttpServletRequest request) {
		if (!"post".equalsIgnoreCase(request.getMethod())) {
			return false;
		}
		String contentType = request.getContentType();
		if (contentType == null) {
			return false;
		}
		if (contentType.toLowerCase().startsWith(MULTIPART)) {
			return true;
		}
		return false;
	}
	
	public static final boolean isMultipartContent(String contentType,String method)
	{
		if (!"post".equalsIgnoreCase(method)) {
			return false;
		}
	
		if (contentType != null && contentType.toLowerCase().startsWith(MULTIPART)) {
			return true;
		}
		
		return false;
	}

}
