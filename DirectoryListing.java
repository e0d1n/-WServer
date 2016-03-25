import java.io.*;
import java.text.*;
import java.util.Arrays;

/**
 * The Class DirectoryListing, gets the HTML page like Apache of files 
 * in the directory named while instantiating, also removes from the list,
 * ignored files.
 */
public class DirectoryListing {

    /** Ignore files array. */
    private String[] ignore;
    
    /** Directory name. */
    private String name;

    /**
     * Instantiates a new directory listing.
     *
     * @param name - Directory name
     * @param ignore_files - Ignore files array
     */
    DirectoryListing(String name,String[] ignore_files){

        this.name = name;
        this.ignore = ignore_files;

    }

    /**
     * Gets the HTML page like Apache of files in the directory.
     *
     * @return HTML page (String)
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public String getList() throws IOException{
    	
        // ========================
        // 	DIRECTORY LISTING
        // ========================

        // IGNORE FILES
    	
    	String html="";
        String directory;
        File myFile = new File(this.name);
        File[] filesList = myFile.listFiles();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        String img_fol =
                      "<img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgA"
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

        String img_text_html =
                     "<img src=\" data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA"
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

        String img_unk =
                      "<img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA"
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

        String img_arrow =
                  "<img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABQA"
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
            +  "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABIAAAAWCAYAAADNX8"
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

        String img_comp =
             "<img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABIAAAAWC"
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



        if(myFile.getName().equals(".")){

            directory = "/";

        }else{

            directory = "/"+this.name;
        }

        html += ("HTTP/1.1 200 OK\n");
        html +=("Content-Type: text/html\n\n");
        html +=(("<html>"
                    + "<head><title>Index of "+ directory +"</title></head>"
                    + "<body bgcolor=\"white\">"
                    + "<h1>Index of " + directory + "</h1>"
                    + "<pre>"
                    + "<table style=\"width:60%\">"
                 ));

        // TABLE HEADER
        html +=(("<tr>"
                    + "<th align=\"left\"></th>"
                    + "<th align=\"left\">Name</th>"
                    + "<th align=\"left\">Last modified</th>"
                    + "<th align=\"left\">Size</th>"
                    + "<th align=\"left\" colspan=\"6\">Actions</th>"
                    + "</tr>"
                    + "<tr>"
                    + "<td colspan=\"10\">"
                    + "<hr size=\"2\" width=\"100%\"/>"
                    + "</td>"
                    + "</tr>"
                 ));

        // BACK POINTS + ARROW
        if(!directory.equals("/")){

            html +=(("<tr>"
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
                     ));

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

                html +=("<tr>");

                if (file.isDirectory()) {

                    html +=(("<td>"+img_fol+"</td>"
                                + "<td>"
                                + "<a href=\""+directory+file.getName()+"/"+"\">"+file.getName()+"</a>"
                                + "</td>"
                                + "<td>"+format.format(file.lastModified())+"</td>"
                                + "<td>"+" - "+"</td>"));

                }else{

                    String actions = "";
                    String size;

                    // COMUN ACTIONS
                    actions = "<td style=\"text-align:center\"><a href=\""+directory+file.getName()+"?zip=true"+"\">zip</a></td>"
                        +"<td style=\"text-align:center\"><a href=\""+directory+file.getName()+"?gzip=true"+"\">gz</a></td>"
                        +"<td style=\"text-align:center\"><a href=\""+directory+file.getName()+"?zip=true&gzip=true"+"\">zip+gz</a></td>";

                    // IMG's + Actions
                    if(file_ext.equalsIgnoreCase("html")){

                        html +=(("<td>"+img_text_html+"</td>"));
                        actions +="<td style=\"text-align:center\"><a href=\""+directory+file.getName()+"?asc=true"+"\">asc</a></td>"
                            +"<td style=\"text-align:center\"><a href=\""+directory+file.getName()+"?asc=true&zip=true"+"\">asc+zip</a></td>"
                            +"<td style=\"text-align:center\"><a href=\""+directory+file.getName()+"?asc=true&zip=true&gzip=true"+"\">asc+zip+gz</a></td>";

                    }else if(file_ext.equalsIgnoreCase("txt") || file_ext.equalsIgnoreCase("xml") ){

                        html +=(("<td>"+img_text_html+"</td>"));


                    }else if(file_ext.equalsIgnoreCase("jpg") || file_ext.equalsIgnoreCase("png") || file_ext.equalsIgnoreCase("gif")){

                        html +=(("<td>"+img_img+"</td>"));


                    }else if(file_ext.equalsIgnoreCase("zip") || file_ext.equalsIgnoreCase("gz")){

                        html +=(("<td>"+img_comp+"</td>"));
                        actions = "";

                    }else{

                        html +=(("<td>" + img_unk + "</td>"));

                    }

                    // File Name + Last Modified
                    html +=(("<td>"
                                + "<a href=\""+directory+file.getName()+"\">"+file.getName()+"</a>"
                                + "</td>"
                                + "<td>"+format.format(file.lastModified())+"</td>"));

                    // FIle Size
                    if(file.length() > 1024){
                        NumberFormat size_formater = new DecimalFormat("#0.0");
                        size = size_formater.format(file.length()/1024.0) + "K";
                    }else{
                        size = String.valueOf(file.length()) ;
                    }

                    html +=(("<td>"+size+"</td>"));
                    html +=((actions));
                }

                html +=(("</tr>"));
            }
        }

        html +=(("<tr>"
                    + "<td colspan=\"10\">"
                    + "<hr size=\"2\" width=\"100%\"/>"
                    + "</td>"
                    + "</tr>"
                    + "</table>"
                    + "</pre>"
                    + "</body>"
                    + "</html>"));
        
        return html;

    }
}
