package zoo.asynweb.pipe.exception;

/**
 * @author fangweng(wenchu)
 * @email fangweng@taobao.com
 *
 */
public class FileNumInvalidException extends ZooException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6257394005778092605L;

	/**
	 * 
	 */
	public FileNumInvalidException() {
	}

	/**
	 * @param message
	 * @param cause
	 */
	public FileNumInvalidException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public FileNumInvalidException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public FileNumInvalidException(Throwable cause) {
		super(cause);
	}

}
