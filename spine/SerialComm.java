/*
 * This is the class that initiates, maintains, 
 * and get accelerometer information from
 * Arduino to PC.
 * 
 * We used SerialPort from jssc.jar file
 * to implement this class.
 */

package spine;

import jssc.SerialPort;
import jssc.SerialPortException;

public class SerialComm {

	SerialPort port;
	String name;
	boolean debug;

	public SerialComm(String name, boolean input) {
		port = new SerialPort(name);
		this.name = name;
		this.debug = input;
		try {
			port.openPort();
		} catch (SerialPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			port.setParams(SerialPort.BAUDRATE_9600,
				SerialPort.DATABITS_8,
				SerialPort.STOPBITS_1,
				SerialPort.PARITY_NONE);
		} catch (SerialPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean available(){
		int result=0;
		try {
			result = this.port.getInputBufferBytesCount();
		} catch (SerialPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(result>0) return true;
		else return false;
	}
	
	public byte readByte(){
		byte[] results = new byte[1];
		try {
			results = this.port.readBytes(1);
		} catch (SerialPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		if(this.debug){
			System.out.println("[0x"+String.format("%02x", results[0])+"]");
		}
		else{
			System.out.print(change(results[0]));
		}
		*/
		return results[0];
	}
	
	public char change(byte input){
		return (char)input;
	}

	public static void main(String[] args) {
		SerialComm test = new SerialComm("COM3",false);
		
		while(true){
			if(test.available()){
				test.readByte();
			}
		}

	}
	
	public String getName(){
		return this.name;
	}
	
	public void writeByte(byte input){
		try {
			this.port.writeByte(input);
		} catch (SerialPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(this.debug){
		//	System.out.println("[0x"+String.format("%02x", input)+"]");
		}
		else{
		//	System.out.print(change(input));
		}
	}
}
