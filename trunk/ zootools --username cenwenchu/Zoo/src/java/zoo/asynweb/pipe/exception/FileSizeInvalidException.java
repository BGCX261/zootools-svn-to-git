package zoo.asynweb.pipe.exception;


/**
 * @author fangweng(wenchu)
 * @email fangweng@taobao.com
 *
 */
public class FileSizeInvalidException extends ZooException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1093139935663157928L;
	public FileSizeInvalidException(){
		
	}
	public FileSizeInvalidException(String message,Throwable cause){
		super(message,cause);
	}
	public FileSizeInvalidException(String message){
		super(message);
	}
	public FileSizeInvalidException(Throwable cause){
		super(cause);
	}
}
