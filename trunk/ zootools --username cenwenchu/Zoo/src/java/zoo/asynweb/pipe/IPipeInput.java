/**
 * 
 */
package zoo.asynweb.pipe;

import java.io.IOException;

import zoo.asynweb.pipe.exception.FileNumInvalidException;
import zoo.asynweb.pipe.exception.FileSizeInvalidException;
import zoo.asynweb.pipe.exception.FileTypeInvalidException;


/**
 * 管道处理的输入，属于管道上下文的一部分
 * @author fangweng(wenchu)
 * @email fangweng@taobao.com
 *
 */
public interface IPipeInput  extends java.io.Serializable
{
	public Object getParameter(String key) throws FileTypeInvalidException,
						FileSizeInvalidException, FileNumInvalidException, IOException;
}
