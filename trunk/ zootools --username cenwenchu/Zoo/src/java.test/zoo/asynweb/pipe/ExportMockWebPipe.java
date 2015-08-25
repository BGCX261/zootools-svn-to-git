package zoo.asynweb.pipe;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import zoo.asynweb.pipe.impl.AbstractWebPipe;
import zoo.asynweb.pipe.impl.WebPipeInput;
import zoo.asynweb.pipe.impl.WebPipeResult;

/**
 * @author fangweng
 * @email fangweng@taobao.com
 * @date 2010-11-23
 *
 */
public class ExportMockWebPipe extends AbstractWebPipe{

	public ExportMockWebPipe(String pipeName)
	{
		this.setPipeName(pipeName);
	}
	
	@Override
	public void doPipe(WebPipeInput pipeInput, WebPipeResult pipeResult) {
		
		try {
			pipeInput.getResponse().getOutputStream()
			.write("<html><body>welcome to hangzhou</body></html>".getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean ignoreIt(WebPipeInput pipeInput, WebPipeResult pipeResult) {
		
			return false;
	}

	@Override
	public boolean ignoreAsynMode(WebPipeInput pipeInput,
			WebPipeResult pipeResult) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
