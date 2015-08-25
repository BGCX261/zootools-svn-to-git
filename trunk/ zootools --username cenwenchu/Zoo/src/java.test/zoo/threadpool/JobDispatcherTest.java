package zoo.threadpool;

import java.util.Map;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class JobDispatcherTest {

	JobDispatcher jobDispatcher;
	JobThreadWeightModel[] jobThreadWeightModel;
	int maximumPoolSize = 10;
	
	@Before
	public void setUp() throws Exception {
		jobDispatcher = new JobDispatcher();
		jobDispatcher.setMaximumPoolSize(maximumPoolSize);
		
		jobThreadWeightModel = new JobThreadWeightModel[3];
		
		for(int i= 0 ; i < jobThreadWeightModel.length ; i++)
		{
			jobThreadWeightModel[i] = new JobThreadWeightModel();
			jobThreadWeightModel[i].setKey("tag:" + i);
		}
			
		//模型中tag:0最大7个资源，其中独享2个，tag:1最大8个资源，独享3个，tag:2最大4个资源，其他最大5个资源。
		jobThreadWeightModel[0].setType(JobThreadWeightModel.WEIGHT_MODEL_LEAVE);
		jobThreadWeightModel[0].setValue(2);
		
		jobThreadWeightModel[1].setType(JobThreadWeightModel.WEIGHT_MODEL_LEAVE);
		jobThreadWeightModel[1].setValue(3);
		
		jobThreadWeightModel[2].setType(JobThreadWeightModel.WEIGHT_MODEL_LIMIT);
		jobThreadWeightModel[2].setValue(4);
		
		jobDispatcher.setJobThreadWeightModel(jobThreadWeightModel);
		jobDispatcher.init();
		jobDispatcher.start();
	}

	@After
	public void tearDown() throws Exception {
		jobDispatcher.stopDispatcher();
	}

	@Ignore
	@Test
	public void testSubmitSimpleJob(){
		
		Job tag0Job = new MockJob("0");
		Job tag1Job = new MockJob("1");
		Job tag2Job = new MockJob("2");
		Job tag3Job = new MockJob("3");
		
		//简单测试
		for(int i=0; i < 10 ; i++)
			jobDispatcher.submitJob(tag0Job);

		Map<String, Integer> status = jobDispatcher.getCurrentThreadStatus();
		Assert.assertEquals(status.get("tag:0"), new Integer(7));
		
		try {
			Thread.sleep(8000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for(int i=0; i < 10 ; i++)
			jobDispatcher.submitJob(tag1Job);

		status = jobDispatcher.getCurrentThreadStatus();
		Assert.assertEquals(status.get("tag:1"), new Integer(8));
		
		try {
			Thread.sleep(8000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(int i=0; i < 10 ; i++)
			jobDispatcher.submitJob(tag2Job);

		status = jobDispatcher.getCurrentThreadStatus();
		Assert.assertEquals(status.get("tag:2"), new Integer(4));
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(int i=0; i < 10 ; i++)
			jobDispatcher.submitJob(tag3Job);

		status = jobDispatcher.getCurrentThreadStatus();
		Assert.assertEquals(status.get(JobDispatcher.DEFAULT_COUNTER), new Integer(5));
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void testSubmitComplexJob(){
		
		Job tag0Job = new MockJob("0");
		Job tag1Job = new MockJob("1");
		Job tag2Job = new MockJob("2");
		Job tag3Job = new MockJob("3");
		
		//简单测试
		for(int i=0; i < 10 ; i++)
			jobDispatcher.submitJob(tag0Job);

		jobDispatcher.submitJob(tag3Job);
		
		Map<String, Integer> status = jobDispatcher.getCurrentThreadStatus();
		//Assert.assertEquals(status.get("tag:0"), new Integer(7));
		//Assert.assertEquals(status.get(JobDispatcher.DEFAULT_COUNTER), new Integer(0));
		
		System.out.println(status.get("tag:0"));
		System.out.println(status.get(JobDispatcher.DEFAULT_COUNTER));
		System.out.println(status.get(JobDispatcher.TOTAL_COUNTER));
		System.out.println(status.get(JobDispatcher.QUEUE_JOB_LENGTH));
		
		Assert.assertEquals(status.get("tag:0"),new Integer(2));
		Assert.assertEquals(status.get(JobDispatcher.DEFAULT_COUNTER),new Integer(5));
		Assert.assertTrue(status.get(JobDispatcher.TOTAL_COUNTER) >= new Integer(7));
		
		try {
			Thread.sleep(8000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		status = jobDispatcher.getCurrentThreadStatus();
		
		System.out.println(status.get("tag:0"));
		System.out.println(status.get(JobDispatcher.DEFAULT_COUNTER));
		System.out.println(status.get(JobDispatcher.TOTAL_COUNTER));
		System.out.println(status.get(JobDispatcher.QUEUE_JOB_LENGTH));
		
	}

}
