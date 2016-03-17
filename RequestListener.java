import java.net.*;
import java.io.*;

public class RequestListener {

    public static void main (String[] args) {

        ServerSocket serverSocket;
        Socket socket2;

        try {
            serverSocket = new ServerSocket(8811);

            while(true)
            {

                socket2 = serverSocket.accept();
                new Thread(new RequestParse(socket2)).start();

            }

        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
