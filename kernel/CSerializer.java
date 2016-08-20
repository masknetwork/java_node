// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.kernel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CSerializer 
{

	public CSerializer() 
	{
		
		
	}
	
	public byte[] serialize(Object obj) throws IOException
	{
            // Byte array
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            
            // GZIP
            GZIPOutputStream gzipOut = new GZIPOutputStream(out);
	    
            // Compress
            ObjectOutputStream os = new ObjectOutputStream(gzipOut);
	    
            // Write
            os.writeObject(obj);
            
            // Close 
            gzipOut.close();
	    
            // Return
            return out.toByteArray();
        }
	
	public  Object deserialize(byte[] data)  throws Exception
        {
	    // Data
            ByteArrayInputStream in = new ByteArrayInputStream(data);
                   
            // GZIP
            GZIPInputStream gzipIn = new GZIPInputStream(in);
	           
            // Decompress
            ObjectInputStream is = new ObjectInputStream(gzipIn);
                   
            // Return
            return is.readObject();
	}
}
