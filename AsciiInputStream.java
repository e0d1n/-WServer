import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * AsciiInputStream removes all HTML tags 
 * while reading an InputStream, 
 * including comments
 */
public class AsciiInputStream extends FilterInputStream{

    /**
     * Instantiates a new ASCII input stream, 
     * it removes all HTML tags while reading,
     * including comments
     *
     * @param in InputStream
     */
    protected AsciiInputStream(InputStream in) {
        super(in);

    }
    
    /* (non-Javadoc)
     * @see java.io.FilterInputStream#read()
     */
    @Override
    public int read() throws IOException {
    	int ch = in.read();
    	int ch2;
    	int count=0;
        while(ch == '<'){
        	count ++;
        	while(count > 0){
        		ch2 = in.read();
	            while(ch2 != '>'){
	            	ch2 = in.read();
	            	if(ch2 == '<') count ++;
	            };
	            ch = in.read();
	            count --;
        	}
        }
        return ch;
    }

}
