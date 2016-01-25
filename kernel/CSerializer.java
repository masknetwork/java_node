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
	
	public byte[] serialize(Object obj)
	{
		try
		{
			ByteArrayOutputStream out = new ByteArrayOutputStream();
                        GZIPOutputStream gzipOut = new GZIPOutputStream(out);
		        ObjectOutputStream os = new ObjectOutputStream(gzipOut);
		        os.writeObject(obj);
                        gzipOut.close();
		        return out.toByteArray();
		}
		catch (IOException ex) 
                { 
                    UTILS.LOG.log("IOException", ex.getMessage(), "CSerializer.java", 27); 
                }
		
		return new byte[100];
	}
	
	public  Object deserialize(byte[] data) 
    {
		try
		{
	           ByteArrayInputStream in = new ByteArrayInputStream(data);
                   GZIPInputStream gzipIn = new GZIPInputStream(in);
	           ObjectInputStream is = new ObjectInputStream(gzipIn);
                   return is.readObject();
		}
		catch (IOException ex) 
                { 
                      UTILS.LOG.log("IOException", ex.getMessage(), "CSerializer.java", 40); 
                }
		catch (ClassNotFoundException ex) 
                { 
                    UTILS.LOG.log("ClassNotFoundException", ex.getMessage(), "CSerializer.java", 41); 
                }
		
		return new Object();
	}

}
