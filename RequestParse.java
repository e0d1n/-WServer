import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.*;

/**
 * The Class RequestParse receive a socket from RequestListener,
 * and serve to the Output socket the Input petition.
 * @see RequestListener
 */
public class RequestParse implements Runnable{

    /** The is. */
    private InputStream is;
    
    /** The os. */
    private OutputStream os;
    
    /** The parameters. */
    private Map<String, String> parameters;
    
    /** The socket2. */
    private Socket socket2;
    
    /** The full_request. */
    private String full_request;
    
    /** The file_request. */
    private String file_request;
    
    /** The filetype. */
    private String filetype;
    
    /** The file_tokens. */
    private String[] file_tokens;
    
    /** The br_i. */
    private BufferedReader br_i;
    
    /** The parameters_request. */
    private String parameters_request;

    /** The thread id. */
    /////////////////////////////////
    private long threadId;
    /////////////////////////////////

    /**
     * Parameters_ split.
     *
     * @param query URL string with all parameters to split
     * @return map of all URL parameters
     */
    public static Map<String, String> parameters_Split(String query)
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
            if (name != null && !name.equals("")){
                map.put(name, value);
            }
        }
        return map;
    }

    /**
     * Instantiates a new request parse.
     *
     * @param socket2 - requested socked
     */
    RequestParse(Socket socket2) {

        this.socket2 = socket2;
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {

        try{

            boolean valid_parameter = false;
            this.threadId = Thread.currentThread().getId();
            this.is = this.socket2.getInputStream();
            this.os = this.socket2.getOutputStream();
            this.br_i = new BufferedReader(new InputStreamReader(is));

            try{

                this.full_request = br_i.readLine().split("\\s+",-1)[1].substring(1); //GET

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

                    this.filetype = this.file_tokens[1];

                }else{

                    this.filetype = "";
                }

                //CHECK IF PARAMETERS
                try{

                    this.parameters_request = this.full_request.split("\\?",-1)[1];
                    this.parameters = parameters_Split(this.parameters_request);

                }catch (ArrayIndexOutOfBoundsException aioobe){

                    this.parameters = null;
                }

                // ========================
                //   DEFAULT HOME PAGE
                // ========================

                if(this.file_request.equals("")){

                    this.file_request = ".";
                    this.filetype = "";
                }

                /////////////////////////////////////////////////////////////////////////
                System.out.println("Thread" + this.threadId + ": " + this.full_request );
                /////////////////////////////////////////////////////////////////////////

                File myFile = new File (this.file_request);

                // ========================
                //   DIRECTORY LISTING
                // ========================

                if(myFile.isDirectory()){

                    String ignore[] = {".DS_Store"};
                    DirectoryListing listing = new DirectoryListing(this.file_request,ignore);
                    os.write(listing.getList().getBytes());

                }
                else if(myFile.exists()) {

                    // ====================
                    //   FILE + PARAMETERS
                    // ====================
                    String fullFileName = this.file_request;

                    FileInputStream fis = new FileInputStream(myFile);
                    is = new BufferedInputStream(fis);

                    os.write("HTTP/1.1 200 OK\n".getBytes());


                    if(this.parameters != null){

                        boolean asc = this.parameters.containsKey("asc");
                        boolean zip = this.parameters.containsKey("zip");
                        boolean gzip = this.parameters.containsKey("gzip");

                        if (asc && this.filetype.equalsIgnoreCase("html")){

                            is = new AsciiInputStream(fis);
                            fullFileName += ".asc";

                            if (!zip && !gzip){

                                os.write("Content-Type: text/plain\n\n".getBytes());

                            }

                            valid_parameter = true;
                        }

                        if (zip && gzip){

                            fullFileName += ".zip.gz";
                            os.write(("Content-Type: application/x-gzip\nContent-Disposition: filename=\""+fullFileName+"\"\n\n").getBytes());

                            os = new GZIPOutputStream(os);
                            os = new ZipOutputStream(os);
                            ZipEntry entry = new ZipEntry(this.file_request);
                            ((ZipOutputStream) os).putNextEntry(entry);

                            valid_parameter = true;


                        }else if (gzip){

                            fullFileName += ".gz";
                            os.write(("Content-Type: application/x-gzip\nContent-Disposition: filename=\""+fullFileName+"\"\n\n").getBytes());
                            os = new GZIPOutputStream(os);
                            valid_parameter = true;

                        }else if (zip){

                            fullFileName += ".zip";
                            os.write(("Content-Type: application/zip\nContent-Disposition: filename=\""+fullFileName+"\"\n\n").getBytes());
                            os = new ZipOutputStream(os);
                            ZipEntry entry = new ZipEntry(this.file_request);
                            ((ZipOutputStream) os).putNextEntry(entry);
                            valid_parameter = true;

                        }

                    }

                    // IF NO VALID PARAMETER DEFAULT FILETYPE HEADERS
                    
                    if(!valid_parameter){

                    	String header_type = "";
                    	
                        if(this.filetype.equalsIgnoreCase("html")){

                            header_type = "text/html";

                        }else if(this.filetype.equalsIgnoreCase("txt")){

                            header_type = "text/plain";

                        }else if(this.filetype.equalsIgnoreCase("xml")){

                            header_type = "application/xml";

                        }else if(this.filetype.equalsIgnoreCase("png")){

                            header_type = "image/png";

                        }else if(this.filetype.equalsIgnoreCase("jpeg") || this.filetype.equalsIgnoreCase("jpg")){

                            header_type = "image/jpeg";

                        }else if(this.filetype.equalsIgnoreCase("gif")){

                            header_type = "image/gif";

                        }else if(this.filetype.equalsIgnoreCase("zip")){

                            header_type = "application/zip";

                        }else if(this.filetype.equalsIgnoreCase("gz")){

                            header_type = "application/x-gzip";
                            
                        }else{

                            header_type = "application/octet-stream";

                        }
                        
                        // WRITE HEADER
                        
                        os.write(("Content-Type: " + header_type +"\n\n").getBytes());

                    }

                    // ================================
                    //    READ-WRITE FILE TO SOCKET
                    // ================================

                    while (is.available()>0){

                        os.write(is.read());

                    }

                }else{

                    os.write("HTTP/1.1 404 Not Found\n\n".getBytes());

                }

            }// IF NOT REQUEST

            // CLOSING ALL
            os.flush();
            os.close();
            is.close();
            this.br_i.close();

        }catch (IOException e) {

            e.printStackTrace();
        }

    }
}
