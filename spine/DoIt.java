package spine;
import java.util.Vector;
import java.util.concurrent.*;
import spine.ComputeCenter;
import spine.SpineComm;
import java.lang.Exception;
import spine.LinearRegression;




public class DoIt{

	public static Callable<Vector<dataPacket>> getDataPacketCallable(ComputeCenter c){
		return new Callable<Vector<dataPacket>>(){
			public Vector<dataPacket> call() throws Exception{
				return c.test();
			}
		};
	}
	
	public static float angleCalculate(float x, float y){
		return (float)Math.atan2((double)y, (double)x);
	}

	public static Vector<dataPacket> combine(Vector<dataPacket> r1, Vector<dataPacket> r2){
		boolean checker = (r1.size()<r2.size());
		int size = checker? r1.size():r2.size();	
		
		LinearRegression pleaseGod = checker? new LinearRegression(r2): new LinearRegression(r1);
		
		float[] lrResult = pleaseGod.calculateY();
		
		for(int i=0;i<size;++i){
			if(checker){
				r1.get(i).y = lrResult[0] + lrResult[1]*r1.get(i).time;
			}
			else{
				r2.get(i).x = lrResult[0] + lrResult[1]*r2.get(i).time;
			}
		}
				
		if(checker){
			return r1;
		}
		else{
			return r2;
		}
	}

	public static void main(String[] args) throws ExecutionException, InterruptedException {

		String ec2_path = "http://ec2-34-235-166-91.compute-1.amazonaws.com/~hakkyung/archhacks17";

		SpineComm realDeal = new SpineComm(ec2_path);


		try {
			realDeal.serverToLocal("/status.txt");

			final ComputeCenter machine1 = new ComputeCenter("COM3");	
			final ComputeCenter machine2 = new ComputeCenter("COM5");

			final Callable<Vector<dataPacket>> callable1 = getDataPacketCallable(machine1);
			final Callable<Vector<dataPacket>> callable2 = getDataPacketCallable(machine2);
			final ExecutorService service = Executors.newFixedThreadPool(2);
			final Future<Vector<dataPacket>> result1 = service.submit(callable1);
			final Future<Vector<dataPacket>> result2 = service.submit(callable2);
			final Vector<dataPacket> v1 = result1.get();
			final Vector<dataPacket> v2 = result2.get();			

			System.out.println("COM3: "+v1.get(v1.size()-1).toString());
			System.out.println("COM5: "+v2.get(v2.size()-1).toString());
			
			Vector<dataPacket> v_final= combine(v1,v2);
			
			dataPacket r_final = v_final.get(v_final.size()-1);
			
			v_final.remove(v_final.size()-1);
			
			r_final.z = angleCalculate(r_final.x,r_final.y);
			
			System.out.println("-------------------------------------------------------------------------");
			
			for(int i=0;i<v_final.size();++i){
				System.out.println(v_final.get(i).toString());
			}

			System.out.println("-------------------------------------------------------------------------");

			
			System.out.println("Combined : "+r_final.toString());

			realDeal.dataToServer("/backend.php",v_final,r_final);

		}
		catch(Exception e) {
			System.err.println(e);
		}
	}	


}
