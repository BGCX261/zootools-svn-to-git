/**
 * 
 */
package zoo.asynwriter;


/**
 * @author fangweng(wenchu)
 * @email fangweng@taobao.com
 *
 */
public interface IWriterScheduleFactory<T>
{
	public void init();
	
	public WriteSchedule<T> createWriter();
	
	public void setLoggerClass(String loggerClass);
}
