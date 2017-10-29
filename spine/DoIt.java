package spine;
import java.util.concurrent.*;
import spine.ComputeCenter;
import java.lang.Exception;



public class DoIt{
	
	public static Callable<dataPacket> getDataPacketCallable(ComputeCenter c){
		return new Callable<dataPacket>(){
			public dataPacket call() throws Exception{
				return c.test();
			}
		};
	}

	public static void main(String[] args) throws ExecutionException, InterruptedException {

		//ThreadFactory threadFactory = new SimpleThreadFactory();
		final ComputeCenter machine1 = new ComputeCenter("COM3","aaa");	
		final ComputeCenter machine2 = new ComputeCenter("COM4","bbb");
		
		final Callable<dataPacket> callable1 = getDataPacketCallable(machine1);
		final Callable<dataPacket> callable2 = getDataPacketCallable(machine2);
		final ExecutorService service = Executors.newFixedThreadPool(2);
		final Future<dataPacket> result1 = service.submit(callable1);
		final Future<dataPacket> result2 = service.submit(callable2);
		final dataPacket r1 = result1.get();
		final dataPacket r2 = result2.get();
		
		
		
		
		
		
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
		
		System.out.println(r1.toString());
		System.out.println(r2.toString());




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
