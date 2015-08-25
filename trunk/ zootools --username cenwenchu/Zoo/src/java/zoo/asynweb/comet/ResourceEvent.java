/**
 * 
 */
package zoo.asynweb.comet;

import javax.servlet.http.HttpServletRequest;

/**
 * @author fangweng
 * @email fangweng@taobao.com
 * @date 2010-11-9
 *
 */
public class ResourceEvent {

	public final static String DELETE_EVENT = "_event_delete_";
	public final static String MODIFY_EVENT = "_event_modify_";
	public final static String ERROR_EVENT = "_event_error_";
	
	private IResource resource;
	private HttpServletRequest request;
	private String eventType;
	private String resourceId;
	
	
	
	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	public IResource getResource() {
		return resource;
	}
	public void setResource(IResource resource) {
		this.resource = resource;
	}
	public HttpServletRequest getRequest() {
		return request;
	}
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	
	
	
}
