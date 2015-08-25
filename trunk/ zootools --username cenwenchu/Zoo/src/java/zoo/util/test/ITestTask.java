/**
 * 
 */
package zoo.util.test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author fangweng
 * @email fangweng@taobao.com
 * @date 2010-11-11
 *
 */
public interface ITestTask extends java.lang.Runnable{

	boolean doTask();
	
	public void setName(String name);



	public void setResult(List<Long> result);



	public void setCount(int count);



	public void setStart(CountDownLatch start);



	public void setDone(CountDownLatch done);



	public void setErrorCounter(AtomicLong errorCounter);
	

}
