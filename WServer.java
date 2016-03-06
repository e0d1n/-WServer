
import java.net.*;

import org.jsoup.Jsoup;


import java.io.*;

public class WServer {

	public static String html2text(String html) {
	    return Jsoup.parse(html).text();
	}
	/*
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
	}*/
	/*
	public static Map<String, List<String>> splitQuery(URL url) throws UnsupportedEncodingException {
		  final Map<String, List<String>> query_pairs = new LinkedHashMap<String, List<String>>();
		  final String[] pairs = url.getQuery().split("&");
		  for (String pair : pairs) {
		    final int idx = pair.indexOf("=");
		    final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
		    if (!query_pairs.containsKey(key)) {
		      query_pairs.put(key, new LinkedList<String>());
		    }
		    final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
		    query_pairs.get(key).add(value);
		  }
		  return query_pairs;
		}
	
	 */
	public static void main (String[] args) {
		
		   ServerSocket serverSocket;
		   Socket socket2;
		   
		   
		try {
		     serverSocket = new ServerSocket(8811);
		     
		     while(true)
		     {
		    	 
		       socket2 = serverSocket.accept();
		       
		       new Thread(new PrimeRun(socket2)).start();
		       

		     }
		     
		   }catch (IOException e) {
		      e.printStackTrace();
		   } 
	}
}
