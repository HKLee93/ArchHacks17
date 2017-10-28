package spine;

public class dataPacket {
	public float x;
	public float y;
	public float z;
	public float time;
	
	public dataPacket(float x, float y, float z, float time){
		this.x = x;
		this.y = y;
		this.z = z;
		this.time = time;
	}
	
	public String toString(){
		return this.x+","+this.y+","+this.z+","+this.time; 
	}
}
