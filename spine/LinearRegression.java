/*
 * Using LinearRegression java class 
 * implemented by Robert Sedgewick and Kevin Wayne
 * from Princeton University.
 * 
 * Modified by Ryun Han to implement
 * Vectors of dataPackets.
 */

package spine;
import spine.dataPacket;

import java.util.Vector;


public class LinearRegression {

	Vector<dataPacket> input;

	public LinearRegression(Vector<dataPacket> input){
		this.input = input;
	}

	public float[] calculateX() { 
		int N = input.size();
		float[] x = new float[N];
		float[] y = new float[N];

		float sumx = 0, sumy = 0;
		for(int i=0;i<input.size();++i){
			x[i] = input.get(i).time;
			y[i] = input.get(i).x;
			sumx += x[i];
			sumy += y[i];
		}

		float xbar = sumx / N;
		float ybar = sumy / N;

		// second pass: compute summary statistics
		float xxbar = 0, yybar = 0, xybar = 0;
		for (int i = 0; i < N; i++) {
			xxbar += (x[i] - xbar) * (x[i] - xbar);
			yybar += (y[i] - ybar) * (y[i] - ybar);
			xybar += (x[i] - xbar) * (y[i] - ybar);
		}

		float result[] = new float[2];

		result[1] = xybar / xxbar;
		result[0] = ybar - result[1] * xbar;

		return result;

	}	
	
	public float[] calculateY() { 
		int N = input.size();
		float[] x = new float[N];
		float[] y = new float[N];

		float sumx = 0, sumy = 0;
		for(int i=0;i<input.size();++i){
			x[i] = input.get(i).time;
			y[i] = input.get(i).y;
			sumx += x[i];
			sumy += y[i];
		}

		float xbar = sumx / N;
		float ybar = sumy / N;

		// second pass: compute summary statistics
		float xxbar = 0, yybar = 0, xybar = 0;
		for (int i = 0; i < N; i++) {
			xxbar += (x[i] - xbar) * (x[i] - xbar);
			yybar += (y[i] - ybar) * (y[i] - ybar);
			xybar += (x[i] - xbar) * (y[i] - ybar);
		}

		float result[] = new float[2];

		result[1] = xybar / xxbar;
		result[0] = ybar - result[1] * xbar;

		return result;

	}

}
