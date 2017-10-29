package spine;
import spine.ComputeCenter; 
import spine.dataPacket;
import java.util.concurrent.*;

public class CallableThread implements Callable {
	


	public dataPacket call(ComputeCenter c) throws Exception {
		// TODO Auto-generated method stub
		return c.test();
	}

	@Override
	public Object call() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
