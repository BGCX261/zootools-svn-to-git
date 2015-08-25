package zoo.asynweb.pipe.exception;

/**
 * @author fangweng(wenchu)
 * @email fangweng@taobao.com
 *
 */
public class ZooException extends Exception {

	private static final long serialVersionUID = -238091758285157331L;

	public ZooException() {
		super();
	}

	public ZooException(String message, Throwable cause) {
		super(message, cause);
	}

	public ZooException(String message) {
		super(message);
	}

	public ZooException(Throwable cause) {
		super(cause);
	}

}
