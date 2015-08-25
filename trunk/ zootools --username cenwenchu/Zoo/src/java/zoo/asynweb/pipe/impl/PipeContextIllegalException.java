package zoo.asynweb.pipe.impl;

/**
 * 管道上下文不合法异常
 * @author fangweng(wenchu)
 * @email fangweng@taobao.com
 *
 */
public class PipeContextIllegalException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4376779980050420855L;
	
	public PipeContextIllegalException(){
		
	}
	public PipeContextIllegalException(String message){
		super(message);
	}
	public PipeContextIllegalException(Throwable cause){
		super(cause);
	}
	public PipeContextIllegalException(String message, Throwable cause){
		super(message,cause);
	}
}
