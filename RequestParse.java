import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.*;

public class RequestParse implements Runnable{

    private InputStream is;
    private OutputStream os;
    private Map<String, String> parameters;
    private Socket socket2;
    private String full_request;
    private String file_request;
    private String filetype;
    private String[] file_tokens;
    private BufferedReader br_i;
    private String parameters_request;

    /////////////////////////////////
    long threadId;
    /////////////////////////////////

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

    RequestParse(Socket socket2) {

        this.socket2 = socket2;
    }

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

                System.out.println("Thread" + this.threadId + ": " + this.full_request );

                File myFile = new File (this.file_request);

                // ========================
                //   DIRECTORY LISTING
                // ========================

                if(myFile.isDirectory()){

                    String ignore[] = {".DS_Store"};
                    DirectoryListing listing = new DirectoryListing(this.file_request,os,ignore);
                    listing.list();

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

                        if (asc){

                            is = new RemoveHTML(fis);
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

                        if(this.filetype.equalsIgnoreCase("html")){

                            os.write("Content-Type: text/html\n\n".getBytes());

                        }else if(this.filetype.equalsIgnoreCase("txt")){

                            os.write("Content-Type: text/plain\n\n".getBytes());

                        }else if(this.filetype.equalsIgnoreCase("xml")){

                            os.write("Content-Type: application/xml\n\n".getBytes());

                        }else if(this.filetype.equalsIgnoreCase("png")){

                            os.write("Content-Type: image/png\n\n".getBytes());

                        }else if(this.filetype.equalsIgnoreCase("jpeg") || this.filetype.equalsIgnoreCase("jpg")){

                            os.write("Content-Type: image/jpeg\n\n".getBytes());

                        }else if(this.filetype.equalsIgnoreCase("gif")){

                            os.write("Content-Type: image/gif\n\n".getBytes());

                        }else{

                            os.write("Content-Type: application/octet-stream\n\n".getBytes());

                        }

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
