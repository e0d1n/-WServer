import java.net.*;
import java.io.*;

/**
 * The listener interface for receiving request events.
 * The class that is interested in processing a request
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's RequestParse method. When
 * the request event occurs, that object's appropriate
 * method is invoked.
 *
 * @see RequestParse
 */
public class RequestListener {

    /**
     * The main method.
     *
     * @param args the arguments
     */
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
