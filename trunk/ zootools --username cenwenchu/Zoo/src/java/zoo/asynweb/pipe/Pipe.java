/**
 * 
 */
package zoo.asynweb.pipe;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 用于pipe的annotations
 * @author fangweng(wenchu)
 * @email fangweng@taobao.com
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Pipe {
	/**
	 * 管道名称
	 * @return
	 */
	String name();
	/**
	 * 管道版本
	 * @return
	 */
	String version();

}
