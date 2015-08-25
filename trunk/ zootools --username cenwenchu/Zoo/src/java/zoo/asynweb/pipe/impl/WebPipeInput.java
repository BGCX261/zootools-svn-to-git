package zoo.asynweb.pipe.impl;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import zoo.asynweb.pipe.IPipeInput;
import zoo.asynweb.pipe.exception.FileNumInvalidException;
import zoo.asynweb.pipe.exception.FileSizeInvalidException;
import zoo.asynweb.pipe.exception.FileTypeInvalidException;
import zoo.lazyparser.FileItem;
import zoo.lazyparser.HttpRequestUtil;
import zoo.lazyparser.LazyParser;


/**
 * @author fangweng(wenchu)
 * @email fangweng@taobao.com
 *
 */
public class WebPipeInput implements IPipeInput{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4568774338405257250L;
	private static final Log logger = LogFactory.getLog(WebPipeInput.class);
	
	HttpServletRequest request;
	HttpServletResponse response;
	
	String contentType;
	String method;
	String characterEncoding;
	Map<String,String> httpHeaders;
	
	
	public WebPipeInput(HttpServletRequest req,HttpServletResponse resp)
	{
		request = req;
		response = resp;
		
		contentType = request.getContentType();
		method = request.getMethod();
		characterEncoding = request.getCharacterEncoding();
		
		httpHeaders = new HashMap<String,String>();
		
		Enumeration<String> headers = request.getHeaderNames();
		
		while(headers.hasMoreElements())
		{
			String h = headers.nextElement();
			httpHeaders.put(h, request.getHeader(h));
		}

	}
	
	
	
	public String getCharacterEncoding() {
		return characterEncoding;
	}


	public Map<String, String> getHttpHeaders() {
		return httpHeaders;
	}


	public HttpServletRequest getRequest(){
		return request;
	}
	
	
	public HttpServletResponse getResponse(){
		return response;
	}

	
	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	/* (non-Javadoc)
	 * 如果出现IO
	 * @see zoo.asynweb.pipe.IPipeInput#getParameter(java.lang.String)
	 */
	@Override
	public Object getParameter(String key) 
	{
		boolean isMultipart = HttpRequestUtil.isMultipartContent(contentType,method);
		Object value = null;
		
		if (!isMultipart)
			value = request.getParameter(key);
		else
		{				
			try
			{
				value = LazyParser.getParameter(request, key);
			}
			catch(Exception ex)
			{
				logger.error("get parameter error.",ex);
			}
			
		}
		
		return value;
	}
	
	/**
	 * 取到上传文件内容的二进制数据
	 * @return
	 * @throws IOException 
	 * @throws FileNumInvalidException 
	 * @throws FileSizeInvalidException 
	 * @throws FileTypeInvalidException 
	 */
	public List<FileItem> getFileData() 
			throws FileTypeInvalidException, FileSizeInvalidException, 
				FileNumInvalidException, IOException {
		if(!HttpRequestUtil.isMultipartContent(contentType,method)){
			return null;
		}
		List<FileItem> fileList = null;
		
		fileList = LazyParser.getFileItemsFromRequest(request);

		return fileList;
	}
	
	/**
	 * 取到所有请求的参数key集合（只取客户端请求的参数集合）
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Set<String> getParameterNames(){
		boolean isMultipart = HttpRequestUtil.isMultipartContent(contentType,method);
		if(!isMultipart){
			return request.getParameterMap().keySet();
		}else{
			try{
				Map<String,Object> paramters = new HashMap<String,Object>(); 
				Set<String> keys = LazyParser.getParameterNames(request);
				for (String string : keys) {
					Object o = LazyParser.getParameter(request, string);
					if(o != null && o instanceof FileItem && !((FileItem)o).isFile()){
						paramters.put(string, o);
					}
				}
				return paramters.keySet();
			}catch(Exception ex){
				logger.error("getParameterNames error",ex);
			}
		}
		return new HashSet<String>();
	}

	
	/**
	 * 取到key对应的值，不同的应用场景下对该值是否需要trim要区别对待.
	 * 大多数情况都需要trim，以下几种典型情况需要区别对待：
	 * 1. 在做签名校验的时候，签名之外的各参数都不能trim
	 * 2. 后传参数到各isp的时候，需要根据参数类型判断是否需要trim
	 * 3. 1.0的情况不需要trim.(因为之前都不trim)
	 * @param key
	 * @param needTrim 是否需要对该值进行trim
	 * @return
	 */
	public String getString(String key, boolean needTrim){
		if(key == null){
			return null;
		}
		Object v = getParameter(key);
		if(v == null){
			return null;
		}
		if(v instanceof FileItem){
			String value = ((FileItem)v).getValue();
			if(value == null){
				value = "";
			}
			return needTrim ? trim(value) : value;//((FileItem)v).getValue();
		}
		String str = null;
		if (v.getClass().equals(String.class)) {
			str = (String) v;
		} else if (v.getClass().isArray()) {
			String[] array = (String[]) v;
			str = (String) array[0];
		}
		return needTrim ? trim(str) : str;
	}
	
	public static String trim(String str) {
        return str == null ? null : str.trim();
    }

}
