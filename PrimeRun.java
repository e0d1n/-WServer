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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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
		ByteArrayOutputStream baos = new ByteArrayOutputStream(input.length);
		ZipOutputStream zos = new ZipOutputStream(baos);
		ZipEntry entry = new ZipEntry(filename);
		entry.setSize(input.length);
		zos.putNextEntry(entry);
		zos.write(input);
		zos.closeEntry();
		zos.close();
		return baos.toByteArray(); 
	}
	
	public static byte[] gzipBytes(byte[] data) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
		GZIPOutputStream gzip = new GZIPOutputStream(bos);
		gzip.write(data);
		gzip.close();
		byte[] compressed = bos.toByteArray();
		bos.close();
		return compressed;
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

	       try{
	    	   
	    	   this.full_request = br.readLine().split("\\s+",-1)[1].substring(1); //GET
	    	   
	       }catch (NullPointerException npe2){
	    	   
	    	   this.full_request = null;
	       }
	       
	       if((this.full_request != null)){
		  
	    	   //FILE PATH
		       this.file_request = this.full_request.split("\\?",-1)[0];
		       //FILE PATH + EXT split
		       this.file_tokens = this.file_request.split("\\.(?=[^\\.]+$)",-1);
		       
		       // CHECK IF FILETYPE
		       if (this.file_tokens.length > 1){
		    	   
		    	   this.filetype = new String(this.file_tokens[1]);
		    	   
		       }else{
		    	   this.filetype = "";
		       }
		       
		       //CHECK IF PARAMETERS
		       try{
		    	   
		    	   this.parameters_request = this.full_request.split("\\?",-1)[1];
		    	   this.parameters = getQueryMap(this.parameters_request);
		    	   
		       }catch (ArrayIndexOutOfBoundsException aioobe){
		    	   
		    	   this.parameters = null;
		       }
		       
		       // ========================
		       // 	DEFAULT HOME PAGE
		       // ========================
		       if(this.file_request.equals("")){
	
		    	   this.file_request = ".";
		    	   this.filetype = "";
		       }
		       
		       System.out.println("Thread" + this.threadId + ": " + this.full_request );
		       
		       File myFile = new File (this.file_request);
		       
		       // ========================
		       // 	DIRECTORY LISTING
		       // ========================
		       
		       if(myFile.isDirectory()){
		    	   
		    	   // IGNORE FILES
	    		   String ignore[] = {".DS_Store","404.html","4042.html","4043.html"};
	    		   
	    		   String directory;
		    	   File[] filesList = myFile.listFiles();
		    	   SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		    	   
		    	   String img_fol =   "<img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgA"
			    			   		+ "AABQAAAASCAYAAABb0P4QAAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgAAgIQ"
			    			   		+ "AAPoAAACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAAB1WlUWHRYTUw6Y29tLmFkb2J"
			    			   		+ "lLnhtcAAAAAAAPHg6eG1wbWV0YSB4bWxuczp4PSJhZG9iZTpuczptZXRhLyIgeDp"
			    			   		+ "4bXB0az0iWE1QIENvcmUgNS40LjAiPgogICA8cmRmOlJERiB4bWxuczpyZGY9Imh"
			    			   		+ "0dHA6Ly93d3cudzMub3JnLzE5OTkvMDIvMjItcmRmLXN5bnRheC1ucyMiPgogICA"
			    			   		+ "gICA8cmRmOkRlc2NyaXB0aW9uIHJkZjphYm91dD0iIgogICAgICAgICAgICB4bWx"
			    			   		+ "uczp0aWZmPSJodHRwOi8vbnMuYWRvYmUuY29tL3RpZmYvMS4wLyI+CiAgICAgICA"
			    			   		+ "gIDx0aWZmOkNvbXByZXNzaW9uPjE8L3RpZmY6Q29tcHJlc3Npb24+CiAgICAgICA"
			    			   		+ "gIDx0aWZmOk9yaWVudGF0aW9uPjE8L3RpZmY6T3JpZW50YXRpb24+CiAgICAgICA"
			    			   		+ "gIDx0aWZmOlBob3RvbWV0cmljSW50ZXJwcmV0YXRpb24+MjwvdGlmZjpQaG90b21"
			    			   		+ "ldHJpY0ludGVycHJldGF0aW9uPgogICAgICA8L3JkZjpEZXNjcmlwdGlvbj4KICA"
			    			   		+ "gPC9yZGY6UkRGPgo8L3g6eG1wbWV0YT4KAtiABQAAALZJREFUOBHtlGEOgzAIhcu"
			    			   		+ "yG9Uz1TPZM61n6ngwFm0sbvWXiSSGWuDjpUqpsgU2IoLr2ietG7fAEwvA6muxvV0"
			    			   		+ "vOdp7N26bkFWPYN/kabblxq/Vi8Kcc0gpbZLaF+T0Gq/VCxAwFHjmNVxS1GPjIxE"
			    			   		+ "gQF6B18iExKjQh5f8S6wVchpoCq35aeCtMFzsDO0X+Me3X5lwfWEWMT6jlotWllJ"
			    			   		+ "09HBbAIrxGTXApB4KzRiGy3boYZhg3oFVc6BestAmAAAAAElFTkSuQmCC\">";
		    	   
		    	   String img_text_html ="<img src=\" data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA"
	    			   				+ "ABIAAAAWCAYAAADNX8xBAAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgAAgIQAA"
	    			   				+ "PoAAACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAAB1WlUWHRYTUw6Y29tLmFkb2JlLn"
	    			   				+ "htcAAAAAAAPHg6eG1wbWV0YSB4bWxuczp4PSJhZG9iZTpuczptZXRhLyIgeDp4bXB"
	    			   				+ "0az0iWE1QIENvcmUgNS40LjAiPgogICA8cmRmOlJERiB4bWxuczpyZGY9Imh0dHA6"
	    			   				+ "Ly93d3cudzMub3JnLzE5OTkvMDIvMjItcmRmLXN5bnRheC1ucyMiPgogICAgICA8c"
			    			   		+ "mRmOkRlc2NyaXB0aW9uIHJkZjphYm91dD0iIgogICAgICAgICAgICB4bWxuczp0aW"
			    			   		+ "ZmPSJodHRwOi8vbnMuYWRvYmUuY29tL3RpZmYvMS4wLyI+CiAgICAgICAgIDx0aWZ"
			    			   		+ "mOkNvbXByZXNzaW9uPjE8L3RpZmY6Q29tcHJlc3Npb24+CiAgICAgICAgIDx0aWZm"
			    			   		+ "Ok9yaWVudGF0aW9uPjE8L3RpZmY6T3JpZW50YXRpb24+CiAgICAgICAgIDx0aWZmO"
			    			   		+ "lBob3RvbWV0cmljSW50ZXJwcmV0YXRpb24+MjwvdGlmZjpQaG90b21ldHJpY0ludG"
			    			   		+ "VycHJldGF0aW9uPgogICAgICA8L3JkZjpEZXNjcmlwdGlvbj4KICAgPC9yZGY6UkR"
			    			   		+ "GPgo8L3g6eG1wbWV0YT4KAtiABQAAANlJREFUOBHNlFEOwyAIQHXZjfRMeiY9k55p"
			    			   		+ "AxIWQNrZ/mwkBkF4Ao2NKaVXuChjjDUDQbvCsaxl3mNFf/dgRTlnFXgLhAQLuw2yM"
			    			   		+ "AWKMQZvcQ9zTmoJY7A11CikvcHJIco95OAXdtcTiSx8A9usAUZb1r13PiJdaw2qNQ"
			    			   		+ "z0lsoCo5RiXSHI1uDULdv64bKPtNYoR7UGp+tNmx4FOpqRZXkXKpAXYCFHtgL9X0W"
			    			   		+ "y7LPqvBGo1iTIC5bndn8I+llF6onYcq/Y1Br+Etz3s0HiBxxhqPQuzmaywQtvHhjj"
			    			   		+ "1Qq9/+wAAAAASUVORK5CYII=\">";
		    	   
		    	   String img_unk =   "<img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA"
	    			   				+ "ABIAAAAWCAYAAADNX8xBAAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgAAgIQAA"
	    			   				+ "PoAAACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAAB1WlUWHRYTUw6Y29tLmFkb2JlLn"
			    			   		+ "htcAAAAAAAPHg6eG1wbWV0YSB4bWxuczp4PSJhZG9iZTpuczptZXRhLyIgeDp4bXB"
			    			   		+ "0az0iWE1QIENvcmUgNS40LjAiPgogICA8cmRmOlJERiB4bWxuczpyZGY9Imh0dHA6"
			    			   		+ "Ly93d3cudzMub3JnLzE5OTkvMDIvMjItcmRmLXN5bnRheC1ucyMiPgogICAgICA8c"
			    			   		+ "mRmOkRlc2NyaXB0aW9uIHJkZjphYm91dD0iIgogICAgICAgICAgICB4bWxuczp0aW"
			    			   		+ "ZmPSJodHRwOi8vbnMuYWRvYmUuY29tL3RpZmYvMS4wLyI+CiAgICAgICAgIDx0aWZ"
			    			   		+ "mOkNvbXByZXNzaW9uPjE8L3RpZmY6Q29tcHJlc3Npb24+CiAgICAgICAgIDx0aWZm"
			    			   		+ "Ok9yaWVudGF0aW9uPjE8L3RpZmY6T3JpZW50YXRpb24+CiAgICAgICAgIDx0aWZmO"
			    			   		+ "lBob3RvbWV0cmljSW50ZXJwcmV0YXRpb24+MjwvdGlmZjpQaG90b21ldHJpY0ludG"
			    			   		+ "VycHJldGF0aW9uPgogICAgICA8L3JkZjpEZXNjcmlwdGlvbj4KICAgPC9yZGY6UkR"
			    			   		+ "GPgo8L3g6eG1wbWV0YT4KAtiABQAAAPVJREFUOBGtVAkOhCAMhI0/0jfJm/RN+qbd"
			    			   		+ "jmRMKRbYxCYI9Bhmihrnef6GP+04jroCQKPGXM667lND9z1gtCxLkVgB7fseMGKM1"
			    			   		+ "dCVFZimuW0b+uUOnUtZ9FWM9Kl2fZ7nJQlsIQ0z7JqJiBPEVwwwhPWYoi4CiNfJE3"
			    			   		+ "AKLGPntY0JeA7IM6UUpnsnC12o/Wh+17Q0yNDWksQ85rjNBgtQfjIt6457jCShaDz"
			    			   		+ "3YKCNjIoe3egPC7BY1/Uhkl2uNFvRAkGuy0joW6zm3gWy700PeFhak44EXwNypfWk"
			    			   		+ "WIbvMsIvoXe9lgH3/A4j3lI47S0xcXT+AcReH/+yIaq6AAAAAElFTkSuQmCC\">";
		    	   
		    	   String img_arrow = "<img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABQA"
		    	   					+ "AAAUCAYAAACNiR0NAAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgAAgIQAAPoAA"
		    	   					+ "ACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAAB1WlUWHRYTUw6Y29tLmFkb2JlLnhtcA"
		    	   					+ "AAAAAAPHg6eG1wbWV0YSB4bWxuczp4PSJhZG9iZTpuczptZXRhLyIgeDp4bXB0az0"
		    	   					+ "iWE1QIENvcmUgNS40LjAiPgogICA8cmRmOlJERiB4bWxuczpyZGY9Imh0dHA6Ly93"
		    	   					+ "d3cudzMub3JnLzE5OTkvMDIvMjItcmRmLXN5bnRheC1ucyMiPgogICAgICA8cmRmO"
		    	   					+ "kRlc2NyaXB0aW9uIHJkZjphYm91dD0iIgogICAgICAgICAgICB4bWxuczp0aWZmPS"
		    	   					+ "JodHRwOi8vbnMuYWRvYmUuY29tL3RpZmYvMS4wLyI+CiAgICAgICAgIDx0aWZmOkN"
		    	   					+ "vbXByZXNzaW9uPjE8L3RpZmY6Q29tcHJlc3Npb24+CiAgICAgICAgIDx0aWZmOk9y"
		    	   					+ "aWVudGF0aW9uPjE8L3RpZmY6T3JpZW50YXRpb24+CiAgICAgICAgIDx0aWZmOlBob"
		    	   					+ "3RvbWV0cmljSW50ZXJwcmV0YXRpb24+MjwvdGlmZjpQaG90b21ldHJpY0ludGVycH"
		    	   					+ "JldGF0aW9uPgogICAgICA8L3JkZjpEZXNjcmlwdGlvbj4KICAgPC9yZGY6UkRGPgo"
		    	   					+ "8L3g6eG1wbWV0YT4KAtiABQAAAOtJREFUOBGtlAEOhDAIBNvL/Ujf5J/8k77JY2sW"
		    	   					+ "V6Ki5khMaQtTLKtlSWwYhhbRdd2CJ7NvScwgpe97j1J/miZfp/OhczbO81yOEhEPu"
		    	   					+ "B6AtRQYK0RSNIWmwKsKFUxoCrxTIcGApsC7FeKeEVshA55wNfKVYgwbVmtdtzJdxX"
		    	   					+ "3V4jiOTZtGQlHrExMwx+aZHUIIwxgTeZKupxAB7r4Uvwejqm/T2+ZdfgvQkyCxBvw"
		    	   					+ "HDGD7kTT+1iG5C9t5tM6/UmvK02SNh4zQNJp3WYMyP0IIw+jANjl5zSuAwuDvZGNz"
		    	   					+ "lwv8N+ayYfJbEPN/ovu34zq68yEAAAAASUVORK5CYII=\">";
		    	   
		    	   String img_img = "<img src=\""
		    	   					+ "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABIAAAAWCAYAAADNX8"
		    	   					+ "xBAAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgAAgIQAAPoAAACA6AAAdTAAAOp"
		    	   					+ "gAAA6mAAAF3CculE8AAAB1WlUWHRYTUw6Y29tLmFkb2JlLnhtcAAAAAAAPHg6eG1w"
		    	   					+ "bWV0YSB4bWxuczp4PSJhZG9iZTpuczptZXRhLyIgeDp4bXB0az0iWE1QIENvcmUgN"
		    	   					+ "S40LjAiPgogICA8cmRmOlJERiB4bWxuczpyZGY9Imh0dHA6Ly93d3cudzMub3JnLz"
		    	   					+ "E5OTkvMDIvMjItcmRmLXN5bnRheC1ucyMiPgogICAgICA8cmRmOkRlc2NyaXB0aW9"
		    	   					+ "uIHJkZjphYm91dD0iIgogICAgICAgICAgICB4bWxuczp0aWZmPSJodHRwOi8vbnMu"
		    	   					+ "YWRvYmUuY29tL3RpZmYvMS4wLyI+CiAgICAgICAgIDx0aWZmOkNvbXByZXNzaW9uP"
		    	   					+ "jE8L3RpZmY6Q29tcHJlc3Npb24+CiAgICAgICAgIDx0aWZmOk9yaWVudGF0aW9uPj"
		    	   					+ "E8L3RpZmY6T3JpZW50YXRpb24+CiAgICAgICAgIDx0aWZmOlBob3RvbWV0cmljSW5"
		    	   					+ "0ZXJwcmV0YXRpb24+MjwvdGlmZjpQaG90b21ldHJpY0ludGVycHJldGF0aW9uPgog"
		    	   					+ "ICAgICA8L3JkZjpEZXNjcmlwdGlvbj4KICAgPC9yZGY6UkRGPgo8L3g6eG1wbWV0Y"
		    	   					+ "T4KAtiABQAAATBJREFUOBGdlQtyxCAIhrHTG+mZ9Ex6Jj2T5ScrIa62bpkxMYgfD0"
		    	   					+ "nivPedPpTWmnvbAtCpDNuV86838oGi1koz7F8g+Jph3xpACDqdJy1nRDCrBRZC6Kj"
		    	   					+ "ZUUTl5YQ3EG8k55zeX/R+BLKhAAaZ73dq1noz59OVlVLKwyKlRL9G5CIRRsmeXAka"
		    	   					+ "RYysnGQLAqDHKqP6LNtCSw+YBSoIJzNkQFAHRALAEKQxZNQJzwrCQ+KNGBAYWYAoz"
		    	   					+ "UXW010rBaFPMhcTA/In5PKnaAWphif+dmTVFNvVlGGCwGgJyujkCQYI0mnxrqX1su"
		    	   					+ "wjSZNhj8LSHgLgEoQFwPBiQq7Cy3R7WaZmrU8gsN9GhMX5iKHbCqdw+oFc2vHBoF+"
		    	   					+ "6RIRPg233rdfFwniB7Uf845+A5f4AtS30m043cXEAAAAASUVORK5CYII=\">";
		    	   
		    	   String img_comp = "<img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABIAAAAWC"
		    	   					+ "AIAAABCPVsWAAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgAAgIQAAPoAAACA6A"
		    	   					+ "AAdTAAAOpgAAA6mAAAF3CculE8AAAB1WlUWHRYTUw6Y29tLmFkb2JlLnhtcAAAAAA"
		    	   					+ "APHg6eG1wbWV0YSB4bWxuczp4PSJhZG9iZTpuczptZXRhLyIgeDp4bXB0az0iWE1Q"
		    	   					+ "IENvcmUgNS40LjAiPgogICA8cmRmOlJERiB4bWxuczpyZGY9Imh0dHA6Ly93d3cud"
		    	   					+ "zMub3JnLzE5OTkvMDIvMjItcmRmLXN5bnRheC1ucyMiPgogICAgICA8cmRmOkRlc2"
		    	   					+ "NyaXB0aW9uIHJkZjphYm91dD0iIgogICAgICAgICAgICB4bWxuczp0aWZmPSJodHR"
		    	   					+ "wOi8vbnMuYWRvYmUuY29tL3RpZmYvMS4wLyI+CiAgICAgICAgIDx0aWZmOkNvbXBy"
		    	   					+ "ZXNzaW9uPjE8L3RpZmY6Q29tcHJlc3Npb24+CiAgICAgICAgIDx0aWZmOk9yaWVud"
		    	   					+ "GF0aW9uPjE8L3RpZmY6T3JpZW50YXRpb24+CiAgICAgICAgIDx0aWZmOlBob3RvbW"
		    	   					+ "V0cmljSW50ZXJwcmV0YXRpb24+MjwvdGlmZjpQaG90b21ldHJpY0ludGVycHJldGF"
		    	   					+ "0aW9uPgogICAgICA8L3JkZjpEZXNjcmlwdGlvbj4KICAgPC9yZGY6UkRGPgo8L3g6"
		    	   					+ "eG1wbWV0YT4KAtiABQAAAPVJREFUOBGNk+0ZgyAMhKWPG8FMMJPOJDPZQ5ojScXWH"
		    	   					+ "3oh94YPQzjPc5EnpSSyfY/j0KHRwPjEGLvugiENFC9TwwaYzc3P/BMG04z8gc3Ie6"
		    	   					+ "zWiuWFEPobMDRX2AR3CcEzMA4JtHOVQfOFA/G+7xwtpVBDvApmt7+L6Zwz9RApAZG"
		    	   					+ "9zUkDi00wVJOhUdgpZVAY/pLzqRDHpaLFYC2hStKX1dn0QY+VWumm+B40GNKbuhDE"
		    	   					+ "4rY5cmBIVLsBYhD4g5r8YLN5NImFkGxd8g/T+UZezbnebkZP4nT3j7259HPoWxk3x"
		    	   					+ "XTTReue7uVCb3Zd218tyWnnG+XeolYpk3fUAAAAAElFTkSuQmCC\">";
		    	   
		    	   if(this.file_request.equals(".")){
		    		   
		    		   directory = "/";
		    		   
		    	   }else{
		    		   
		    		   directory = "/"+this.file_request;
		    	   }
		    	   
		    	   os.write("HTTP/1.1 200 OK\n".getBytes());
		    	   os.write("Content-Type: text/html\n\n".getBytes());
		    	   os.write(("<html>"
		    	   				+ "<head><title>Index of "+ directory +"</title></head>"
		    	   				+ "<body bgcolor=\"white\">"
		    	   				+ "<h1>Index of " + directory + "</h1>"
		    	   				+ "<pre>"
		    	   				+ "<table style=\"width:60%\">"
		    	   				).getBytes());
		    	   
		    	   // TABLE HEADER
		    	   os.write(("<tr>"
		    			   		+ "<th align=\"left\"></th>"
		    			   		+ "<th align=\"left\">Name</th>"
		    			   		+ "<th align=\"left\">Last modified</th>"
		    			   		+ "<th align=\"left\">Size</th>"
		    			   		+ "<th align=\"left\" colspan=\"3\">Actions</th>"
		    			   	+ "</tr>"
		    			   	+ "<tr>"
		    			   		+ "<td colspan=\"10\">"
		    			   			+ "<hr size=\"2\" width=\"100%\"/>"
		    			   		+ "</td>"
		    			   	+ "</tr>"
		    			   	).getBytes());
		    	   
		    	   // BACK POINTS + ARROW
		    	   if(!directory.equals("/")){
		    		   
		    		   os.write(("<tr>"
		    		   				+ "<td></td>"
		    		   				+ "<td>"
		    		   					+ "<a href=\"./\">.</a>"
		    		   				+ "</td>"
		    		   		   + "</tr>"
		    		   		   + "<tr>"
		    		   		   		+ "<td>" + img_arrow + "</td>"
		    		   				+ "<td>"
		    		   					+ "<a href=\"../\">...</a>"
		    		   				+ "</td>"
		    		   		   + "</tr>"
		    		   		   ).getBytes());
	 
	    		   }
		    	   
		    	   // GET ALL FILES
		    	   for (File file : filesList) {
		    		   
		    		   String file_name = file.getName();
		    		   String []file_name_p_ext = file_name.split("\\.(?=[^\\.]+$)",-1);
		    		   String file_ext = "";
		    		   
		    		   if(file_name_p_ext.length > 1){
		    			   file_ext = file_name_p_ext[1];
		    		   }
		    		   
		    		   if(!Arrays.asList(ignore).contains(file_name)){
		    			   
			    		   os.write("<tr>".getBytes());
			    		   
			    		   if (file.isDirectory()) {
			    			   
			    			   os.write(("<td>"+img_fol+"</td>"
			    			   			+ "<td>"
			    			   				+ "<a href=\""+directory+file.getName()+"/"+"\">"+file.getName()+"</a>"
			    			   			+ "</td>"
			    			   			+ "<td>"+format.format(file.lastModified())+"</td>"
			    			   			+ "<td>"+" - "+"</td>").getBytes());
			    			   
			    		   }else{

			    			   String actions = "";
			    			   String size;
			    			   
			    			   // IMG's + Actions
			    			   if(!file_ext.equals("")){
			    				   
			    				   if(file_ext.equalsIgnoreCase("html")){
			    					   os.write(("<td>"+img_text_html+"</td>").getBytes());
			    					   actions = "<td><a href=\""+directory+file.getName()+"?asc=true"+"\">asc</a></td>"
			    							   	+"<td><a href=\""+directory+file.getName()+"?zip=true"+"\">zip</a></td>"
			    							   	+"<td><a href=\""+directory+file.getName()+"?gzip=true"+"\">gz</a></td>"
			    							   	+"<td><a href=\""+directory+file.getName()+"?asc=true&zip=true"+"\">asc+zip</a></td>"
			    							   	+"<td><a href=\""+directory+file.getName()+"?asc=true&zip=true&gzip=true"+"\">asc+zip+gz</a></td>";
			    				   
			    				   }else if(file_ext.equalsIgnoreCase("txt") || file_ext.equalsIgnoreCase("xml") ){
			    					   os.write(("<td>"+img_text_html+"</td>").getBytes());
			    					   actions = "<td><a href=\""+directory+file.getName()+"?zip=true"+"\">zip</a></td>"
			    							   	+"<td><a href=\""+directory+file.getName()+"?gzip=true"+"\">gz</a></td>";
			    					   
			    				   }else if(file_ext.equalsIgnoreCase("jpg") || file_ext.equalsIgnoreCase("png") || file_ext.equalsIgnoreCase("gif")){
			    					   os.write(("<td>"+img_img+"</td>").getBytes());
			    					   actions = "<td><a href=\""+directory+file.getName()+"?zip=true"+"\">zip</a></td>"
			    							   	+"<td><a href=\""+directory+file.getName()+"?gzip=true"+"\">gz</a></td>";
			    					   
			    				   }else if(file_ext.equalsIgnoreCase("zip") || file_ext.equalsIgnoreCase("gz")){
			    					   
			    					   os.write(("<td>"+img_comp+"</td>").getBytes());
			    					   
			    				   }else{
				    				   
				    				   os.write(("<td>" + img_unk + "</td>").getBytes());
				    				   actions = "<td><a href=\""+directory+file.getName()+"?zip=true"+"\">zip</a></td>"
			    							   	+"<td><a href=\""+directory+file.getName()+"?gzip=true"+"\">gz</a></td>";
				    			   }
			    				   
			    			   }else{
			    				   
			    				   os.write(("<td>" + img_unk + "</td>").getBytes());
			    				   actions = "<td><a href=\""+directory+file.getName()+"?zip=true"+"\">zip</a></td>"
		    							   	+"<td><a href=\""+directory+file.getName()+"?gzip=true"+"\">gz</a></td>";
			    			   }

			    			   // File Name + Last Modified
			    			   os.write(("<td>"
			    			   				+ "<a href=\""+directory+file.getName()+"\">"+file.getName()+"</a>"
			    			   		    + "</td>"
			    			   		    + "<td>"+format.format(file.lastModified())+"</td>").getBytes());

				               // FIle Size
				               if(file.length() > 1024){
				            	   NumberFormat size_formater = new DecimalFormat("#0.0");
				            	   size = size_formater.format(file.length()/1024.0) + "K";
				               }else{
				            	   size = String.valueOf(file.length()) ;
				               }
				               
				               os.write(("<td>"+size+"</td>").getBytes());
				               os.write((actions).getBytes());
			    		   }
			    		   
			    		   os.write(("</tr>").getBytes());
		    		  }
		    	   }
		    	   
		    	   os.write(("<tr>"
		    	   				+ "<td colspan=\"10\">"
		    	   				+ "<hr size=\"2\" width=\"100%\"/>"
		    	   				+ "</td>"
		    	   			+ "</tr>"
		    	   			+ "</table>"
		    	   			+ "</pre>"
		    	   			+ "</body>"
		    	   			+ "</html>").getBytes());
		    	   os.flush();
		    	   os.close();
		    	   
	
		       }else if(myFile.exists()) {
		    	   
		    	   	// ====================
		       		// 		READ FILE
		       		// ====================
		    	   	String fullFileName="";
		    	   	this.fullData  = new byte [(int)myFile.length()];
		    	   	FileInputStream fis = new FileInputStream(myFile);
				    BufferedInputStream bis = new BufferedInputStream(fis);
				    bis.read(this.fullData,0,this.fullData.length);
				    bis.close();
				    fis.close();
				    fullFileName = this.file_request;
				    if(this.filetype.equalsIgnoreCase("html")){
		       			
		       			  this.type = "Content-Type: text/html\n\n";
		       			  
					       if((this.parameters != null)){
					    	 
					    	   if (this.parameters.containsKey("asc")){
					    		   this.fullData = html2text(new String(this.fullData, "UTF-8")).getBytes();
					    		   fullFileName += ".asc";
					    	   }
						       
					       }
					       
			    	   
			       }else if(this.filetype.equalsIgnoreCase("txt")){
			    	   
			    	   this.type = "Content-Type: text/plain\n\n";
				       
			       }else if(this.filetype.equalsIgnoreCase("xml")){
				 
			    	   this.type = "Content-Type: application/xml\n\n";
				       
			       }else if(this.filetype.equalsIgnoreCase("jpg") || this.filetype.equalsIgnoreCase("jpeg")){
			    	   
			    	   this.type = "Content-Type: image/jpeg\n\n";
				       
			       }else if(this.filetype.equalsIgnoreCase("png")){
				       
			    	   this.type = "Content-Type: image/png\n\n";
				        
			       }else if(this.filetype.equalsIgnoreCase("gif")){
				       
			    	   this.type = "Content-Type: image/gif\n\n";
				       
			       }else if(this.filetype.equalsIgnoreCase("zip")){
				       
			    	   this.type = "Content-Type: application/zip\nContent-Disposition: filename=\""+fullFileName+"\"\n\n";
				       
			       }else if(this.filetype.equalsIgnoreCase("gz")){
				       
			    	   this.type = "Content-Type: application/x-gzip\nContent-Disposition: filename=\""+fullFileName+"\"\n\n";
				       
			       }else{
			    	   
			    	   this.type = "Content-Type: application/octet-stream\n\n";
			       }
				    
				    // COMUN PARAMETERS
				    if((this.parameters != null)){
			    	   	
				    	   if (this.parameters.containsKey("zip")){
				    		   this.fullData = zipBytes(this.file_request,this.fullData);
				    		   fullFileName += ".zip";
				    		   this.type = "Content-Type: application/zip\nContent-Disposition: filename=\""+fullFileName+"\"\n\n";
				    		   
				    	   }
				    	   if (this.parameters.containsKey("gzip")){
				    		   this.fullData = gzipBytes(this.fullData);
				    		   fullFileName += ".gz";
				    		   this.type = "Content-Type: application/x-gzip\nContent-Disposition: filename=\""+fullFileName+"\"\n\n";
				    		   
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
		    		   os.write(("<h1>HTTP Error 404</h1><img src=\"http://www.sherv.net/cm/emo/funny/2/big-dancing-banana-smiley-emoticon.gif\" align=\"center\" width=200px><h2>You failed, but I believe in you, so keep trying :)"
		    		   		  + "</h2><p>The Web server cannot find the file or script you asked for. "
		    		   		  + "Please check the URL to ensure that the path is correct."
		    		   		  + "<br><br><a href=\"/"+"\">Home</a>").getBytes());
		    		   os.flush();
		    		   os.close();
		    		   
		    	   }
		    	   
		    	   this.br.close();
		      	}
	    	   
	       }// IF NOT REQUEST
	    
	  }catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         
    }

}
