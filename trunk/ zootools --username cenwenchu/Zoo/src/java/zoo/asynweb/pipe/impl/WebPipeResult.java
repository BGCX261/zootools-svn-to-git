package zoo.asynweb.pipe.impl;

import zoo.asynweb.pipe.IPipeResult;



/**
 * @author fangweng(wenchu)
 * @email fangweng@taobao.com
 *
 */
public class WebPipeResult implements IPipeResult {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8248968761082533843L;
	
	private boolean breakPipeChain = false;
	private Object result;
	private int status = STATUS_UNDO;//
	private Exception exception;

	@Override
	public Exception getException() {
		return exception;
	}

	@Override
	public void setException(Exception exception) {
		this.exception = exception;
	}

	@Override
	public boolean isBreakPipeChain() {
		
		return breakPipeChain;
	}

	@Override
	public void setBreakPipeChain(boolean breakPipeChain) {
		this.breakPipeChain = breakPipeChain;
	}

	@Override
	public boolean isDone() {
		if (status == STATUS_DONE)
			return true;
		else
			return false;
	}

	@Override
	public boolean isCancelled() {
		if (status == STATUS_CANCEL)
			return true;
		else
			return false;
	}

	@Override
	public Object getResult() {
		return result;
	}

	@Override
	public void setStatus(int status) {
		this.status = status;
	}
	
	@Override
	public void setResult(Object result) {
		this.result = result;
	}

}
