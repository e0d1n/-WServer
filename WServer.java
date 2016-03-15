
import java.net.*;

import java.io.*;

public class WServer {
	
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
