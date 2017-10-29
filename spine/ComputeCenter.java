package spine;

import java.util.Vector;

import spine.SerialComm;
import spine.dataPacket;

enum status{
	DEACTIVATED, INERTIA, MEASURE,  STRETCH
}

public class ComputeCenter{

	final private SerialComm port;
	status currentState;

	public ComputeCenter(String name){
		this.port = new SerialComm(name, true);
		this.currentState = status.INERTIA;
	}

	public Vector<dataPacket> getInfo(long range){
		range = range*1000;
		boolean gotIn = false;
		long startTime = 0;
		long endTime = 0;
		Vector<dataPacket> dataVec = new Vector<dataPacket>();
		while((endTime-startTime)<range){

			//System.out.println(endTime-startTime);
			if(this.port.available()){
				float x,y,z,timestamp;
				switch(this.port.readByte()){
				case 0x21:
					switch(this.port.readByte()){
					case 0x30:
						long une = this.port.readByte() & 0xff;
						long doux = this.port.readByte() & 0xff;
						long trois = this.port.readByte() & 0xff;
						long fois = this.port.readByte() & 0xff;
						long temp = (une << 24) + (doux << 16)	+ (trois << 8) + fois;
						if(!gotIn){
							startTime = temp;
							endTime = temp;
							gotIn = true;
						}
						timestamp = (float)temp;
						int firstX = this.port.readByte()& 0xff;
						int secondX = this.port.readByte()& 0xff;
						int thirdX = this.port.readByte()& 0xff;
						int fourthX = this.port.readByte()& 0xff;
						x = Float.intBitsToFloat((firstX<<24)+(secondX<<16)+(thirdX<<8)+fourthX);
						byte comma1 = this.port.readByte();
						if(comma1!=0x2c){
							break;
						}
						int firstY = this.port.readByte()& 0xff;
						int secondY = this.port.readByte()& 0xff;
						int thirdY = this.port.readByte()& 0xff;
						int fourthY = this.port.readByte()& 0xff;
						y = Float.intBitsToFloat((firstY<<24)+(secondY<<16)+(thirdY<<8)+fourthY);
						byte comma2 = this.port.readByte();
						if(comma2!=0x2c){
							break;
						}
						int firstZ = this.port.readByte()& 0xff;
						int secondZ = this.port.readByte()& 0xff;
						int thirdZ = this.port.readByte()& 0xff;
						int fourthZ = this.port.readByte()& 0xff;
						z = Float.intBitsToFloat((firstZ<<24)+(secondZ<<16)+(thirdZ<<8)+fourthZ);
						System.out.println(this.port.getName()+" : "+x+","+y+","+z+","+((timestamp/(float)1000.0)));
						dataVec.add(new dataPacket(x,y,z,(timestamp/(float)1000.0)));
						if(gotIn){
							endTime = temp;
						}
					}
				}

			}
			else{
				//throw new java.lang.Error("Error: Connection Not Available");
				//System.out.println("Error: Connection Not Available");
			}
		}		
		return dataVec;
	}
	
	public dataPacket measureInertia(Vector<dataPacket> input){
		float xSum=(float)0.0, ySum=(float)0.0, zSum=(float)0.0;
		for(int i=0;i<input.size();++i){
			xSum += input.get(i).x;
			ySum += input.get(i).y;
			zSum += input.get(i).z;
		}
		xSum /= input.size();
		ySum /= input.size();
		zSum /= input.size();
		
		System.out.println(this.port.getName()+" : The normal inertia is: "+xSum+","+ySum+","+zSum);
		
		return new dataPacket(xSum, ySum, zSum, input.get(input.size()-1).time);
	}
	
	public Vector<dataPacket> offset(Vector<dataPacket> input, dataPacket inertia){
		for(int i=0;i<input.size();++i){
			input.get(i).x=input.get(i).x-inertia.x;
			input.get(i).y=input.get(i).y-inertia.y;
			input.get(i).z=input.get(i).z-inertia.z;	
		}
		return input;
	}

	public dataPacket measureDistance(Vector<dataPacket> input, dataPacket inertia){
		Vector<dataPacket> temp = new Vector<dataPacket>();
		Vector<dataPacket> finale = new Vector<dataPacket>();
		
		input = offset(input,inertia);

		for(int i=1;i<input.size();++i){
			//System.out.println(input.get(i).toString());
			float deltaX = (input.get(i).x+input.get(i-1).x)/2;
			float delatY = (input.get(i).y+input.get(i-1).y)/2;
			float deltaZ = (input.get(i).z+input.get(i-1).z)/2;
			float tempTime = input.get(i).time-input.get(i-1).time;
			if(i==1){
				temp.add(new dataPacket((deltaX*tempTime),(delatY*tempTime),(deltaZ*tempTime),input.get(i).time));
			}
			else{
				temp.add(new dataPacket((temp.get(i-2).x+deltaX*tempTime),(temp.get(i-2).y+delatY*tempTime),(temp.get(i-2).z+deltaZ*tempTime),input.get(i).time));
			}
		}
		System.out.println();
		System.out.println();
		for(int i=1;i<temp.size();++i){
			System.out.println(this.port.getName()+" : "+temp.get(i).toString());
			float deltaX = (temp.get(i).x+temp.get(i-1).x)/2;
			float delatY = (temp.get(i).y+temp.get(i-1).y)/2;
			float deltaZ = (temp.get(i).z+temp.get(i-1).z)/2;
			float tempTime = temp.get(i).time-temp.get(i-1).time;
			if(i==1){
				finale.add(new dataPacket((deltaX*tempTime),(delatY*tempTime),(deltaZ*tempTime),temp.get(i).time));
			}
			else{
				finale.add(new dataPacket((finale.get(i-2).x+deltaX*tempTime),(finale.get(i-2).y+delatY*tempTime),(finale.get(i-2).z+deltaZ*tempTime),temp.get(i).time));
			}
		}
		dataPacket result = new dataPacket(finale.get(finale.size()-1).x-finale.get(0).x,finale.get(finale.size()-1).y-finale.get(0).y,finale.get(finale.size()-1).z-finale.get(0).z,finale.get(finale.size()-1).time);
		return result;
	}
	
	public dataPacket dataProcess(Vector<dataPacket> input, dataPacket inertia){
		
		input = offset(input,inertia);
		
		LinearRegression calculator = new LinearRegression(input);
		
		float[] resultX = calculator.calculateX();
		float[] resultY = calculator.calculateY();
		
		float estXVal = resultX[0] + resultX[1]*input.get(input.size()-1).time;
		float estYVal = resultY[0] + resultY[1]*input.get(input.size()-1).time;
		
		return new dataPacket(estXVal,estYVal,input.get(input.size()-1).z,input.get(input.size()-1).time);
		
	}

	public Vector<dataPacket> test(){
		dataPacket inertia = new dataPacket(0,0,0,0);
		Vector<dataPacket> result = new Vector<dataPacket>();
		
		System.out.println(this.port.getName()+" : Measuring Inertia...");
		System.out.println();
		
		if(this.currentState==status.INERTIA){
			inertia = this.measureInertia(this.getInfo(5));
		}
		System.out.println();
		System.out.println(this.port.getName()+" : Time to readjust yourself...");
		System.out.println();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(this.port.getName()+" : Calibrating Measures");
		this.currentState = status.MEASURE;
		
		if(this.currentState==status.MEASURE){
			//result = this.measureDistance(this.getInfo(20),inertia);
			result = this.getInfo(20);
			result.add(this.dataProcess(result, inertia));
		}
		
		/*
		System.out.println();
		System.out.println();
		System.out.println(result.x+","+result.y+","+result.z+","+result.time);
		*/
		
		return result;
	}

	public void send(){

	}


}
