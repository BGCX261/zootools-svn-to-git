package zoo.asynweb.pipe.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import zoo.asynweb.pipe.IPipeContext;


/**
 * Web管道上下文实现
 * @author fangweng(wenchu)
 * @email fangweng@taobao.com
 *
 */
public class WebPipeContext implements IPipeContext{

	//由于异步方式可能会有多线程访问，因此采用线程安全的方式
	private Map<String,Object> attachments = new ConcurrentHashMap<String,Object>();
	
	/**
	 * @param attachment
	 */
	public Object getAttachment(String name)
	{
		return this.attachments.get(name);
	}
	
	/**
	 * @param attachment
	 */
	public void setAttachment(String name,Object attachment)
	{
		this.attachments.put(name, attachment);
	}
	
	/**
	 * @return the attachments
	 */
	public Map<String, Object> getAttachments() {
		return attachments;
	}

	@Override
	public void removeAttachments() {
		attachments.clear();
	}

	@Override
	public void removeAttachment(String name) {
		attachments.remove(name);
	}

	@Override
	public void addAllAttachments(Map<String, Object> atts)
	{
		if (atts != null && atts.size() > 0)
			attachments.putAll(atts);
	}
	
}
