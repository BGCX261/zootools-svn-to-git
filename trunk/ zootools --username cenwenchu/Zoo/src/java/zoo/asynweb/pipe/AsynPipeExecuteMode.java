/**
 * 
 */
package zoo.asynweb.pipe;

/**
 * @author fangweng(wenchu)
 * @email fangweng@taobao.com
 *
 */
public enum AsynPipeExecuteMode {
	
	BLOCK("block"),ASYN("asyn");
	
	private String v;
		
	AsynPipeExecuteMode(String value)
	{
		v = value;
	}
		
	@Override
	public String toString()
	{
		return v;
	}
}
