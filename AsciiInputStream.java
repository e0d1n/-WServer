import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class AsciiInputStream extends FilterInputStream{

    protected AsciiInputStream(InputStream in) {
        super(in);

    }
    
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
