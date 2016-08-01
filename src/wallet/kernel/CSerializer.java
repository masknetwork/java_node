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



public class CSerializer {

	public CSerializer() 
	{
		// TODO Auto-generated constructor stub
		
	}
	
	public byte[] serialize(Object obj) throws IOException
	{
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            GZIPOutputStream gzipOut = new GZIPOutputStream(out);
	    ObjectOutputStream os = new ObjectOutputStream(gzipOut);
	    os.writeObject(obj);
            gzipOut.close();
	    return out.toByteArray();
        }
	
	public  Object deserialize(byte[] data)  throws Exception
        {
		ByteArrayInputStream in = new ByteArrayInputStream(data);
                   GZIPInputStream gzipIn = new GZIPInputStream(in);
	           ObjectInputStream is = new ObjectInputStream(gzipIn);
                   return is.readObject();
		
	}

}
