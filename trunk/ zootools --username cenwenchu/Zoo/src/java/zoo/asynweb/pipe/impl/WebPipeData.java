package zoo.asynweb.pipe.impl;


import zoo.asynweb.pipe.IPipeContext;
import zoo.asynweb.pipe.IPipeData;
import zoo.lazyparser.LazyParser;


/**
 * @author fangweng(wenchu)
 * @email fangweng@taobao.com
 *
 * @param <I>
 * @param <R>
 */
public class WebPipeData <I extends WebPipeInput, R extends WebPipeResult> implements IPipeData<I,R> {
	
	private IPipeContext pipeContext;
	private I pipeInput;
	private R pipeResult;
	
	public WebPipeData(IPipeContext context,
			I pipeInput,R pipeResult)
	{
		pipeContext = context;
		this.pipeInput = pipeInput;
		this.pipeResult = pipeResult;
		
		
		pipeContext.setAttachment(LazyParser.LAZY_CONTEXT, LazyParser.getLazyContext());
	}
	
	@Override
	public IPipeContext getPipeContext() {
		// TODO Auto-generated method stub
		return pipeContext;
	}


	@Override
	public I getPipeInput() {
		// TODO Auto-generated method stub
		return pipeInput;
	}


	@Override
	public R getPipeResult() {
		// TODO Auto-generated method stub
		return pipeResult;
	}


	@Override
	public void setPipeContext(IPipeContext pipeContext) {
		this.pipeContext = pipeContext;
	}


	@Override
	public void setPipeInput(I pipeInput) {
		this.pipeInput = pipeInput;
	}

	@Override
	public void setPipeResult(R pipeResult) {
		this.pipeResult = pipeResult;
	}

}
