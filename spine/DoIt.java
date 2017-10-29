package spine;
import java.util.concurrent.*;
import spine.ComputeCenter;
import spine.CallableThread;
import java.lang.Exception;



public class DoIt{

	public static void main(String[] args) throws Exception {

		//ThreadFactory threadFactory = new SimpleThreadFactory();
		ComputeCenter machine1 = new ComputeCenter("COM3","aaa");	
		
		/*
		ComputeCenter machine2 = new ComputeCenter("COM4","aaa");	
		CallableThread callableThread1 = new CallableThread();
		CallableThread callableThread2 = new CallableThread();
		
		System.out.println("a");

		dataPacket data1 = new dataPacket(0,0,0,0);
		dataPacket data2 = new dataPacket(0,0,0,0);

		FutureTask task1 = new FutureTask(callableThread1);
		FutureTask task2 = new FutureTask(callableThread2);

		task1.run();
		task2.run();

		try {
			System.out.println(task1.get());
			System.out.println(task2.get());
		} catch (Exception e) {
		}
		*/
		
		dataPacket lalala = machine1.test();
		
		System.out.println(lalala.x+","+lalala.y+","+lalala.z+","+lalala.time);




		//		Runnable target = () ->{
		//			
		//			data1 = machine1.test();
		//		};
		//		
		//		Runnable target2 = () ->{
		//			
		//			ComputeCenter machine2 = new ComputeCenter("COM5","Aaa",5);	
		//			machine2.test();
		//		};
		//
		//		Thread t1 = threadFactory.newThread(target);
		//		Thread t2 = threadFactory.newThread(target2);
		//		
		//		t1.start();
		//		t2.start();
		//		
		//		t1.join();
		//		t2.join();



	}

}
