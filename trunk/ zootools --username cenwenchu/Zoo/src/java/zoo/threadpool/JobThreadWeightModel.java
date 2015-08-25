/**
 * 
 */
package zoo.threadpool;

/**
 * 线程权重模型
 * @author fangweng
 *
 */
public class JobThreadWeightModel {

	public static final String WEIGHT_MODEL_LEAVE = "leave";
	public static final String WEIGHT_MODEL_LIMIT = "limit";
	
	private String key;
	private String type;
	private int value;
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
}
