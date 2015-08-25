/**
 * 
 */
package zoo.asynweb.comet.impl;


import zoo.util.test.ITestTask;
import zoo.util.test.ITestTaskFactory;
import zoo.util.test.StressTestTemplate;

/**
 * @author fangweng
 * @email fangweng@taobao.com
 * @date 2010-11-12
 *
 */
public class ResourceBoardStressTest implements ITestTaskFactory{
	

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		ResourceBoardStressTest factory = new ResourceBoardStressTest();
		
		ResourceBoard resourceBoard = new ResourceBoard();
		factory.setResourceBoard(resourceBoard);
		
		for(int i = 0 ; i < 2000 ; i ++)
		{
			ResourceBoardTestTask task = new ResourceBoardTestTask();
			task.setResourceBoard(resourceBoard);
			new TestThread(task).start();
		}
		
		
		Thread.sleep(1000);
		
		StressTestTemplate.doStressTest(100, 100, factory);
		
		resourceBoard.stop();
		
		Thread.sleep(10000000);
	}
	
	
	ResourceBoard resourceBoard;
	
	public ResourceBoardStressTest()
	{
	}
	
	

	public ResourceBoard getResourceBoard() {
		return resourceBoard;
	}



	public void setResourceBoard(ResourceBoard resourceBoard) {
		this.resourceBoard = resourceBoard;
	}



	@Override
	public ITestTask getTaskInstance() {

		ResourceBoardTestTask instance = new ResourceBoardTestTask();
		instance.setResourceBoard(resourceBoard);
		
		return instance;
	}
	
	

}

class TestThread extends Thread
{
	ResourceBoardTestTask boardTest;
	
	public TestThread(ResourceBoardTestTask boardTest)
	{
		this.boardTest = boardTest;
	}
	
	public void run()
	{
		boardTest.testDoPOST();
		boardTest.testDoGET();
	}
}
