package wallet.kernel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;



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
		    ObjectOutputStream os = new ObjectOutputStream(out);
		    os.writeObject(obj);
		    return out.toByteArray();
		}
		catch (IOException ex) { UTILS.LOG.log("IOException", ex.getMessage(), "CSerializer.java", 27); }
		
		return new byte[100];
	}
	
	public  Object deserialize(byte[] data) 
    {
		try
		{
	      ByteArrayInputStream in = new ByteArrayInputStream(data);
	      ObjectInputStream is = new ObjectInputStream(in);
	      return is.readObject();
		}
		catch (IOException ex) { UTILS.LOG.log("IOException", ex.getMessage(), "CSerializer.java", 40); }
		catch (ClassNotFoundException ex) { UTILS.LOG.log("ClassNotFoundException", ex.getMessage(), "CSerializer.java", 41); }
		
		return new Object();
	}

}
