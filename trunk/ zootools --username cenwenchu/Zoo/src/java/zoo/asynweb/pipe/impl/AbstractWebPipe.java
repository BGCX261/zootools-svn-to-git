package zoo.asynweb.pipe.impl;

import zoo.asynweb.pipe.IPipe;
import zoo.asynweb.pipe.IPipeContext;

/**
 * @author fangweng
 * @email fangweng@taobao.com
 * @date 2010-11-2
 *
 */
public abstract class AbstractWebPipe implements IPipe<WebPipeInput,WebPipeResult>{

	String pipeName;
	boolean asynMode;
	
	@Override
	public String getPipeName() {
		if (pipeName == null)
			pipeName = this.getClass().getSimpleName();
		
		return pipeName;
	}

	@Override
	public void setPipeName(String pipeName) {
		this.pipeName = pipeName;
	}


	@Override
	public IPipeContext getContext() {
		return PipeContextManager.getContext();
	}

	@Override
	public boolean ignoreIt(WebPipeInput pipeInput, WebPipeResult pipeResult) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAsynMode() {
		return asynMode;
	}

	@Override
	public void setAsynMode(boolean mode) {
		this.asynMode = mode;
		
	}

}
