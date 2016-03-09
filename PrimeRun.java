import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.jsoup.Jsoup;

public class PrimeRun implements Runnable{
	
	private InputStream is;
	private OutputStream os;
	private Map<String, String> parameters;
	private Socket socket2;
	private String full_request;
	private String file_request;
	private String filetype;
	private String[] file_tokens;
	private String type;
	private BufferedReader br;
	private byte [] fullData;
	private String parameters_request;
	
	/////////////////////////////////
	long threadId;
	/////////////////////////////////
	
	public static byte[] zipBytes(String filename, byte[] input) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(baos);
		ZipEntry entry = new ZipEntry(filename);
		entry.setSize(input.length);
		zos.putNextEntry(entry);
		zos.write(input);
		zos.closeEntry();
		zos.close();
		return baos.toByteArray(); 
	}
	
	
	public static String html2text(String html) {
	    return Jsoup.parse(html).text();
	}
	
	public static Map<String, String> getQueryMap(String query)  
	{  
	    String[] params = query.split("&");  
	    Map<String, String> map = new HashMap<String, String>();  
	    for (String param : params)  
	    {  
	        String name = param.split("=")[0];
	        String value = null;
	        try{
	        	value = param.split("=")[1];
	        }catch (ArrayIndexOutOfBoundsException aiobe){
	        	value = null;
	        }
	        map.put(name, value);  
	    }  
	    return map;  
	}

    PrimeRun(Socket socket2) {
    	
    	this.socket2 = socket2;
    }

    public void run() {
        
	  try{
		  
		   this.threadId = Thread.currentThread().getId();
		   this.is = this.socket2.getInputStream();
		   this.os = this.socket2.getOutputStream();
	       
	       this.br = new BufferedReader(new InputStreamReader(is));
	      
	       
	       this.full_request = br.readLine().split("\\s+",-1)[1].substring(1); //GET
	       this.file_request = this.full_request.split("\\?",-1)[0]; //FILE+EXT
	       this.file_tokens = this.file_request.split("\\.(?=[^\\.]+$)",-1); //FILE+EXT split
	       
	       // CHECK IF FILETYPE
	       if (this.file_tokens.length > 1){
	    	   
	    	   this.filetype = new String(this.file_tokens[1]);
	    	   
	       }
	       
	       //CHECK IF PARAMETERS
	       try{
	    	   this.parameters_request = this.full_request.split("\\?",-1)[1]; //PARAMETERS
	    	   this.parameters = getQueryMap(this.parameters_request);
	    	   
	       }catch (ArrayIndexOutOfBoundsException aioobe){
	    	   
	    	   this.parameters = null;
	       }
	       
	       
	       // ========================
	       // 	DEFAULT HOME PAGE
	       // ========================
	       if(this.file_request.equals("")){
	    	   this.file_request = "index.html";
	    	   this.filetype = "html";
	       }
	       
	       System.out.println("Thread" + this.threadId + ": " + this.full_request );
	       
	       File myFile = new File (this.file_request);
	       
	       if(myFile.exists() && !myFile.isDirectory()) {
	    	   
	    	   	// ====================
	       		// 		READ FILE
	       		// ====================
	    	   
	    	   	this.fullData  = new byte [(int)myFile.length()];
	    	   	FileInputStream fis = new FileInputStream(myFile);
			    BufferedInputStream bis = new BufferedInputStream(fis);
			    bis.read(this.fullData,0,this.fullData.length);
			    bis.close();
			    fis.close();
	    	   
	       		if(this.filetype.equalsIgnoreCase("html")){
	       			
	       			  this.type = "Content-Type: text/html\n\n";
				       
				       if((this.parameters != null)){
				    	   
				    	   if (this.parameters.containsKey("asc")){
				    		   this.fullData = html2text(new String(this.fullData, "UTF-8")).getBytes();
				    		   this.type = "Content-Type: text/html\n\n";
					       }
				    	   if (this.parameters.containsKey("zip")){
				    		   this.fullData = zipBytes(this.file_request,this.fullData);
				    		   this.type = "Content-Type: application/zip\nContent-Disposition: filename=\""+(file_request)+".zip\"\n\n";
				    		   
				    	   }
				    	    
				       }
				       
		    	   
		       }else if(this.filetype.equalsIgnoreCase("txt")){
		    	   
		    	   this.type = "Content-Type: text/plain\n\n";
			       
			       if((this.parameters != null)){
			    	   	
			    	   if (this.parameters.containsKey("zip")){
			    		   this.fullData = zipBytes(this.file_request,this.fullData);
			    		   this.type = "Content-Type: application/zip\nContent-Disposition: filename=\""+(file_request)+".zip\"\n\n";
			    		   
			    	   }
			    	    
			       }
		    	   
		       }else if(this.filetype.equalsIgnoreCase("jpg") || this.filetype.equalsIgnoreCase("jpeg")){
		    	   
		    	   
		    	   this.type = "Content-Type: image/jpeg\n\n";
			       
			       if((this.parameters != null)){
			    	   
			    	   if (this.parameters.containsKey("zip")){
			    		   this.fullData = zipBytes(this.file_request,fullData);
			    		   this.type = "Content-Type: application/zip\nContent-Disposition: filename=\""+(file_request)+".zip\"\n\n";
			    		   
			    	   }
			    	    
			       }

		       }else if(this.filetype.equalsIgnoreCase("png")){
			       
			       
		    	   this.type = "Content-Type: image/png\n\n";
			       
			       if((this.parameters != null)){
			    	   
			    	   if (this.parameters.containsKey("zip")){
			    		   this.fullData = zipBytes(this.file_request,fullData);
			    		   this.type = "Content-Type: application/zip\nContent-Disposition: filename=\""+(file_request)+".zip\"\n\n";
			    		   
			    	   }
			    	    
			       }
			       
		       }
	       		
	       		// ====================
	       		// 		WRITE SOCKET
	       		// ====================
			       
			       os.write("HTTP/1.1 200 OK\n".getBytes());
			       os.write(this.type.getBytes());
	    	   	   os.write(this.fullData,0,this.fullData.length);
			       os.flush();				       
			       os.close();
			       this.br.close();
			       
	       		
	      }else{
	    	   
	    	   File errorFile = new File ("404.html");
	    	   
	    	   if(errorFile.exists()){
	    		   
	    		   	//String fullData = "";
	    		   
	    		   	this.fullData  = new byte [(int)errorFile.length()];
		    	   	FileInputStream fis = new FileInputStream(errorFile);
				    BufferedInputStream bis = new BufferedInputStream(fis);
				    bis.read(this.fullData,0,this.fullData.length);
				    bis.close();
		    	   	
		    	   	os.write("HTTP/1.1 404 Not Found\n".getBytes());
		    		os.write("Content-Type: text/html\n\n".getBytes());
		    		os.write(this.fullData,0,this.fullData.length);
		    	   	os.flush();
		    		os.close();
	    		   
	    	   }
	    	   else{
	    		   
	    		   os.write("HTTP/1.1 404 Not Found\n".getBytes());
	    		   os.write("Content-Type: text/html\n\n".getBytes());
	    		   os.write("<p> 404 - FILE NOT FOUND </p>".getBytes());
	    		   os.flush();
	    		   os.close();
	    		   
	    	   }
	    	   
	    	   this.br.close();
	    	   
	       }
	       
	  }catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         
    }

}
