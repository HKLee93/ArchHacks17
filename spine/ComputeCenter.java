package spine;

import java.util.Vector;

import spine.SerialComm;
import spine.dataPacket;

enum status{
	DEACTIVATED, CALIBRATION,  STRETCH
}

public class ComputeCenter {

	final private SerialComm port;
	final long range;
	final String url;
	status currentState;

	public ComputeCenter(String name, String url, long range){
		this.port = new SerialComm(name, true);
		this.url = url;
		this.range = range*1000;
		this.currentState = status.CALIBRATION;
	}

	public Vector<dataPacket> getInfo(){
		boolean gotIn = false;
		long startTime = 0;
		long endTime = 0;
		Vector<dataPacket> dataVec = new Vector<dataPacket>();
		while((endTime-startTime)<this.range){

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
						int firstX = this.port.readByte();
						int secondX = this.port.readByte();
						int thirdX = this.port.readByte();
						int fourthX = this.port.readByte();
						x = Float.intBitsToFloat((firstX<<24)+(secondX<<16)+(thirdX<<8)+fourthX);
						byte comma1 = this.port.readByte();
						if(comma1!=0x2c){
							break;
						}
						int firstY = this.port.readByte();
						int secondY = this.port.readByte();
						int thirdY = this.port.readByte();
						int fourthY = this.port.readByte();
						y = Float.intBitsToFloat((firstY<<24)+(secondY<<16)+(thirdY<<8)+fourthY);
						byte comma2 = this.port.readByte();
						if(comma2!=0x2c){
							break;
						}
						int firstZ = this.port.readByte();
						int secondZ = this.port.readByte();
						int thirdZ = this.port.readByte();
						int fourthZ = this.port.readByte();
						z = Float.intBitsToFloat((firstZ<<24)+(secondZ<<16)+(thirdZ<<8)+fourthZ);
						System.out.println(x+","+y+","+z+","+((timestamp/(float)1000.0)));
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

	public dataPacket calculateDelta(Vector<dataPacket> input){
		Vector<dataPacket> temp = new Vector<dataPacket>();
		Vector<dataPacket> finale = new Vector<dataPacket>();

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
			//System.out.println(temp.get(i).toString());
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

	public void test(){
		dataPacket result = new dataPacket(0,0,0,0);
		if(this.currentState==status.CALIBRATION){
			result = this.calculateDelta(this.getInfo());
		}
		System.out.println();
		System.out.println();
		System.out.println(result.x+","+result.y+","+result.z+","+result.time);
	}

	public void send(){

	}


}
