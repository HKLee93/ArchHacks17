/*
 * This is the main class that does all the 
 * implementations, finishes the task, 
 * including communicating with php files
 * in ec2 server.
 * 
 * We also used multi-threading to process two Arduino machines
 * simultaneously.
 * 
 * 
 */

package spine;
import java.util.Vector;
import java.util.concurrent.*;
import spine.ComputeCenter;
import spine.SpineComm;
import java.lang.Exception;
import spine.LinearRegression;


public class DoIt{

//	reference: https://stackoverflow.com/questions/5516383/how-to-return-object-from-callable
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
	
	public static dataPacket convertToDist(dataPacket input){
		input.x = (float)(-49.41679*Math.pow((double)input.x,2)+ 5.638621*input.x + 0.0254205);
		input.y = (float)(191.882*Math.pow((double)input.y, 2) + 23.45816*input.y + 0.411644);
		
		return input;
	}

	public static void main(String[] args) throws ExecutionException, InterruptedException {

		float height = 1.75f;
		height *= 0.75f;
		
		String ec2_path = "http://ec2-52-14-54-180.us-east-2.compute.amazonaws.com/~annielee/archHacks/";

		SpineComm realDeal = new SpineComm(ec2_path);


		try {
			realDeal.serverToLocal("response.txt");

			final ComputeCenter machine1 = new ComputeCenter("COM5");	
			final ComputeCenter machine2 = new ComputeCenter("COM3");

			final Callable<Vector<dataPacket>> callable1 = getDataPacketCallable(machine1);
			final Callable<Vector<dataPacket>> callable2 = getDataPacketCallable(machine2);
			final ExecutorService service = Executors.newFixedThreadPool(2);
			Thread.sleep(2000);
			final Future<Vector<dataPacket>> result1 = service.submit(callable1);
			final Future<Vector<dataPacket>> result2 = service.submit(callable2);
			final Vector<dataPacket> v1 = result1.get();
			final Vector<dataPacket> v2 = result2.get();			

			System.out.println("COM5: "+v1.get(v1.size()-1).toString());
			System.out.println("COM3: "+v2.get(v2.size()-1).toString());
			
			Vector<dataPacket> v_final= combine(v1,v2);
			
			dataPacket r_final = v_final.get(v_final.size()-1);
			
			v_final.remove(v_final.size()-1);
			
			System.out.println("-------------------------------------------------------------------------");
			
			for(int i=0;i<v_final.size();++i){
				System.out.println(v_final.get(i).toString());
			}

			System.out.println("-------------------------------------------------------------------------");

			r_final = convertToDist(r_final);
			float temp = angleCalculate(height,r_final.y);
			temp = (float)(180*Math.abs(temp)/Math.PI);
			r_final.z = temp;
			
			System.out.println("Combined : "+r_final.toString());

			realDeal.dataToServer("backend.php",v_final,r_final);

		}
		catch(Exception e) {
			System.err.println(e);
		}
	}	


}
