package zoo.asynweb.pipe.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

/**
 * @author fangweng(wenchu)
 * @email fangweng@taobao.com
 *
 */
public class WebPipeLog implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5062190806865256797L;
	
	/**
	 * Pipe消耗时间打点队列
	 */
	protected Map<String,Long> timeStampQueue = new HashMap<String,Long>();
	
	/**
	 * 临时用于记录某一个pipe执行的起点时间戳
	 */
	private long pipeBegTimeStamp;
	
	private Queue<String> timeSeqQueue = new java.util.PriorityQueue<String>();

	public long getPipeBegTimeStamp() {
		return pipeBegTimeStamp;
	}

	public void setPipeBegTimeStamp(long pipeBegTimeStamp) {
		this.pipeBegTimeStamp = pipeBegTimeStamp;
	}

	public Map<String, Long> getTimeStampQueue() {
		return timeStampQueue;
	}
	
	public void addTimeStamp(String pipe,Long timeStamp)
	{
		timeStampQueue.put(pipe, timeStamp);
		timeSeqQueue.offer(pipe);
	}

	public void setTimeStampQueue(Map<String, Long> timeStampQueue) {
		this.timeStampQueue = timeStampQueue;
	}
	
	public String toString()
	{
		StringBuilder result = new StringBuilder();
		
		long totalConsume = 0;
		String pipe;
		
		while((pipe = timeSeqQueue.poll()) != null)
		{
			Long value = timeStampQueue.get(pipe);
			totalConsume += value;
			
			result.append(pipe).append(" : ").append(value).append(",");
		}
		
		result.append("total : ").append(totalConsume);
		
		return result.toString();
	}
	
}
