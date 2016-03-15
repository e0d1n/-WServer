import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class RemoveHTML extends FilterInputStream{
	
	protected RemoveHTML(InputStream in) {
		super(in);
		
	}
	
	boolean intag = false; // Used to remember whether we are "inside" a tag
	/*
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
				
		int numchars = 0; 
	    
	    while (numchars == 0) {
	      numchars = in.read(b, off, len);
	      if (numchars == -1)
	        return -1; // Check for EOF

	      int last = off; 
	      
	      for (int i = off; i < off + numchars; i++) {
	        if (!intag) { 
	          if (b[i] == '<')
	            intag = true;
	          else
	            b[last++] = b[i];
	        } else if (b[i] == '>')
	          intag = false;
	      }
	      numchars = last - off;
	    } 
	    return numchars; 
	}
	*/

	/*@Override
	public int read() throws IOException {
	    byte[] buf = new byte[1];
	    int result = read(buf, 0, 1);
	    if (result == -1)
	      return -1;
	    else
	      return (int) buf[0];
	}*/
	
	@Override
	public int read() throws IOException {
		int ch = super.read();
	    while(ch == '<'){
	    	while(super.read() != '>');
	    		ch = super.read();
	    }
	    if(ch == -1){
	    	ch = '\n';
	    }
	    return ch;
	}
	
}
