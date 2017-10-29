import java.util.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;


public class SpineComm {

	public static void main(String[] args) {
		
		String ec2_path = "http://ec2-34-235-166-91.compute-1.amazonaws.com/~hakkyung/archhacks17";
		
		try {
		
			//dataToServer(ec2_path + "/backend.php", "myfile.txt");
			serverToLocal(ec2_path + "/status.txt");
		}
		catch(Exception e) {
			
			System.err.println(e);
		}
		
	}
	
	//reference: https://stackoverflow.com/questions/40494871/send-post-data-with-java
	public static void dataToServer(String url, String file) throws Exception{
		
		URL web = new URL(url);
		//File f = new File(file);
		//BufferedReader br = new BufferedReader(new FileReader(f));
		
		HttpURLConnection connect = (HttpURLConnection) web.openConnection();
		Map<String,Object> params = new LinkedHashMap<>();
		
		for(int i = 0; i < 4; i++) {
			
			params.put("x_" + i, "test" + i);
		}
		
//	    params.put("message", "hello3\n");
//	    params.put("message2", "hello4\n");
//	    params.put("message3", "hello5\n");
//		String text = null;
//		
//		while((text = br.readLine()) != null) {
//			
//			int i = 0;
//			params.put("x_" + i, text);
//			i++;
//		}
//	   
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

	    String type = "application/x-www-form-urlencoded";
	    int len = dataToByte.length;
	    connect.setRequestMethod("POST");
	    connect.setRequestProperty("Content-Type", type);
	    connect.setRequestProperty("Content-Length", String.valueOf(len));
	    connect.setDoOutput(true);
	    connect.getOutputStream().write(dataToByte);

	    InputStreamReader isr = new InputStreamReader(connect.getInputStream(), "UTF-8");
	    BufferedReader br2 = new BufferedReader(isr);

	    for (int c; (c = br2.read()) >= 0;) {
	    
	        System.out.print((char) c);
	    }
	}
	
	public static void serverToLocal(String url) throws Exception {
		
		boolean checker = false;
		while(!checker) {
			
			URL web = new URL(url);
			URLConnection connect = web.openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(connect.getInputStream()));
			String line = br.readLine();
			System.out.println(line);
			if(line.compareTo("true") == 0) {
				
				checker = true;
			}
			br.close();
		}
		
	}
}
