package zoo.threadpool;

/**
 * 简单的任务定义，需要支持能够产生key。
 * @author fangweng
 *
 */
public interface Job extends Runnable {

	String getKey();
}
