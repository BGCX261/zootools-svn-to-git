/**
 * 
 */
package zoo.threadpool;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @author fangweng
 *
 */
public class MockJob implements Job{

	private String tag;
	
	public MockJob(String tag)
	{
		this.tag = tag;
	}
	
	@Override
	public String getKey() {
		return "tag:" + tag;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(1000);
			System.out.println(getKey() + " done..");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException
	{
		String value = "%B2%8A%ED";
		//String value = "%E7%95%99%E6%84%8F%E4%B9%BE%E5%9D%A4";
		value = URLDecoder.decode(value, "UTF-8");
		System.out.println(value);
	}

}
