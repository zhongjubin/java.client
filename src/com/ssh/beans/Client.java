import java.util.List;
import java.lang.reflect.Type;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;  
import java.io.*;
import java.net.InetAddress;
/*

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
*/

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken; 

import com.ssh.beans.Agv;

public class Client {  
	
	private static String encoding = "GBK";
	private static String cam_dir = "/home/odroid/Downloads/FlyCapture2Test/"; 
	private static String cam_in = 	cam_dir+"cam_out";
	private static String svr_out = cam_dir+"svr_in";

	public static void main(String args[]) throws Exception {  	
				
		Runnable runnable = new Runnable() {

			public void run(){
				final long timeInterval = 1000;
				String host = "192.168.137.1";  
				int port = 8080;     	
				String page="/SSH_project/register";		       

 
				Socket client = null;
				try {
					client = new Socket(host, port);
				} catch (IOException e2) {
					e2.printStackTrace();
				}
				OutputStreamWriter writer = null;
				InputStream is = null;
				int contentLength = 0;        
			    String line = null;
				String result = null;
				String svr_out_str = null;
				BufferedReader cam_reader = null;
				BufferedWriter cam_writer = null;
				char[] buf = new char[100];
				File svr_out_fd = null;
				File cam_in_fd = null;
				Type typelist = new TypeToken<List<Agv>>(){}.getType();			
				Type type = new TypeToken<Agv>(){}.getType();			
				List<Agv> agvlist = null;
				Agv agv = null;
 
				if(cam_in_fd==null)
					cam_in_fd = new File(cam_in);
				try {
					if(cam_reader == null)
					cam_reader = new BufferedReader(new FileReader(cam_in_fd));
				} catch (FileNotFoundException e1) {	
					e1.printStackTrace();
				}				
				
				if(svr_out_fd==null)	
				 	svr_out_fd= new File(svr_out);	
				System.out.println("Listening...");			
				try{	
					if(cam_writer == null)
						cam_writer = new BufferedWriter(new FileWriter(svr_out));
				}catch (FileNotFoundException ex){
					ex.printStackTrace();
				}catch (IOException e1) {
					e1.printStackTrace();
				}

				try {
					writer = new OutputStreamWriter(client.getOutputStream(),StandardCharsets.UTF_8);
				} catch (IOException e2) {
					e2.printStackTrace();
				}
				
				Map<String, String> map = new HashMap<String, String>();  
				StringBuffer sb = new StringBuffer();	
		        
		        String jsonString;    
		          
		        byte[] jsonByte; 
		           
		        while (true) {        				
				
					try {
						cam_reader.read(buf);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					String output = new StringBuilder().append(buf).toString().trim(); 
					//System.out.println(output.length());							
					map.clear();	
					InetAddress address = null;
					try{
						 address= InetAddress.getLocalHost();
					}catch(IOException e1){
						e1.printStackTrace();
					}
					map.put("agv.netid",address.toString());  
		        	map.put("agv.xcoord","23"); 
		        	map.put("agv.ycoord","54");
					map.put("agv.msg",output);
					String data = map.toString().substring(1,map.toString().length()-1);
					data = data.replace(", ","&");
					
					sb.delete(0,sb.length());
					sb.append("POST "+page+" HTTP/1.1\r\n");  
		        	sb.append("Host: "+ host +":"+port+"\r\n");  
		        	sb.append("Accept: text/html\r\n");  
		        	sb.append("Connection: Keep-Alive\r\n"); 
					 
		        	sb.append("Content-Type: application/x-www-form-urlencoded; charset=UTF-8\r\n"); 
		        	sb.append("Content-Length:"+data.length()+"\r\n");
		        	sb.append("\r\n"); 
				
					sb.append(data);	

					try {
						System.out.println("Writing to server...");
						System.out.println(sb.toString()); 
						writer.write(sb.toString());
					} catch (IOException e1) {
						e1.printStackTrace();
					} 
					try {
						writer.flush();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					try {
						 is = client.getInputStream();
					} catch (IOException e1) {
						e1.printStackTrace();
					}  
			        do {  
			        	try {
							line = readLine(is, 0);
						} catch (IOException e) {
							e.printStackTrace();
						}  
			              
			            if (line.startsWith("Content-Length")) {  
			                contentLength = Integer.parseInt(line.split(":")[1].trim());  
			            }  
			             
			        } while (!line.equals("\r\n"));  
			        
			        try {
						result = readLine(is, contentLength);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
			        try
			        {
						result = result.substring(1,result.length()-1);
						result = result.replace("\\", "");
						agv= new Gson().fromJson(result, type);
						svr_out_str = "Agv "+agv.getNetid()+" at("+agv.getXcoord()+","+agv.getYcoord()+") is processed!";
					}
			        catch (Exception e)
					{
						System.out.println("Server Error");
						System.out.println(result);
					}
					
					System.out.println("Writing to client...");	
					try {
						cam_writer.write(svr_out_str);
						System.out.println(svr_out_str);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					
					try{
						cam_writer.flush();			
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					System.out.println("Write to client Done!");	
					System.out.println("");
					System.out.println("");	
				}
		    }
		};

		Thread thread = new Thread(runnable);
		thread.start();
		
				
    }  
	
    private static String readLine(InputStream is, int contentLe) throws IOException {  
        ArrayList lineByteList = new ArrayList();  
        byte readByte;  
        int total = 0;  
        if (contentLe != 0) {  
            do {  
                readByte = (byte) is.read();  
                lineByteList.add(Byte.valueOf(readByte));  
                total++;  
            } while (total < contentLe);  
        } else {  
            do {  
                readByte = (byte) is.read();  
                lineByteList.add(Byte.valueOf(readByte));  
            } while (readByte != 10);  
        }  
  
        byte[] tmpByteArr = new byte[lineByteList.size()];  
        for (int i = 0; i < lineByteList.size(); i++) {  
            tmpByteArr[i] = ((Byte) lineByteList.get(i)).byteValue();  
        }  
        lineByteList.clear();  
  
        return new String(tmpByteArr, encoding);  
    }  
      
}  
