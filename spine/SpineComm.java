/*
 * SpineComm.java
 * Contains two methods that communicate with php file on amazon ec2.
 * 
 * dataToServer(): collect data from accelerometers and store them in Map. 
 * Encode the map into byte and send to php file on remote server.
 * 
 * serverToLocal(): keep checking the status on server whether a user has initiated the measurement or not. 
 */

package spine;

import java.util.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import spine.dataPacket;


public class SpineComm {
	
	final String ec2_path;
	
	public SpineComm(String path){
		this.ec2_path = path;
	}

	
	//reference: https://stackoverflow.com/questions/40494871/send-post-data-with-java
	public void dataToServer(String url, Vector<dataPacket> input, dataPacket result) throws Exception{
		
		URL web = new URL(this.ec2_path+url);
		
		HttpURLConnection connect = (HttpURLConnection) web.openConnection();
		Map<String,Object> params = new LinkedHashMap<>();
		
		//column label
		params.put("xval","yval\n");
		
		//put x and y value into a map in pair
		for(int i=0;i<input.size();++i){
			params.put(String.format("%.10f",input.get(i).x),String.format("%.10f",input.get(i).y)+"\n");
		}
		
		//put final calculated value
		params.put("-99",String.format("%.10f",result.x)+"\n");
		params.put("-88",String.format("%.10f",result.y)+"\n");
		params.put("-77",String.format("%.10f",result.z));
		
		
	    StringBuilder postData = new StringBuilder();

	    for (Map.Entry<String,Object> p : params.entrySet()) {
	        
	    		if (postData.length() != 0) {
	    			
	    			postData.append('&');
	    		}
	        postData.append(URLEncoder.encode(p.getKey(), "UTF-8"));
	        postData.append('=');
	        postData.append(URLEncoder.encode(String.valueOf(p.getValue()), "UTF-8"));
	    }

	    byte[] dataToByte = postData.toString().getBytes("UTF-8");
	    
	    //set method to POST
	    String type = "application/x-www-form-urlencoded";
	    int len = dataToByte.length;
	    connect.setRequestMethod("POST");
	    connect.setRequestProperty("Content-Type", type);
	    connect.setRequestProperty("Content-Length", String.valueOf(len));
	    connect.setDoOutput(true);
	    connect.getOutputStream().write(dataToByte);
	    
	    InputStreamReader isr = new InputStreamReader(connect.getInputStream(), "UTF-8");
	    BufferedReader br2 = new BufferedReader(isr);
	    
	    //return value from the server
	    for (int c; (c = br2.read()) >= 0;) {
	    
	        System.out.print((char) c);
	    }
	}
	
	public void serverToLocal(String url) throws Exception {
		
		boolean checker = false;
		while(!checker) {
			
			URL web = new URL(this.ec2_path+url);
			URLConnection connect = web.openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(connect.getInputStream()));
			
			//keep reading the value(initially "false" on server until it changes to "true"
			String line = br.readLine();
			System.out.println(line);
			if(line.compareTo("true") == 0) {
				
				checker = true;
			}
			br.close();
		}
		
	}
}
