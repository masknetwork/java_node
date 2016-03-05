package wallet.network.packets.sync;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.blocks.*;

public class CReqDataPayload implements java.io.Serializable
{
	// Tip
	String req_type;
	
	// Start block
	long start;
	
	// End block
	long end;
	
	// Hash
	String hash;
	
	// Request a table
	public CReqDataPayload(String req_type, long start, long end)  throws Exception
	{
            // Type
	    this.req_type=req_type;
		
	    // Start
	    this.start=start;
		
	    // End
	    this.end=end;
		
	    // Hash
	    this.hash=UTILS.BASIC.hash(String.valueOf(this.start)+
                                       String.valueOf(this.end));
		
	}
        
        public CResult check()
        {
            // Req type
            if (!this.req_type.equals("ID_GET_BLOCKS") && 
                !this.req_type.equals("ID_GET_BLOCKCHAIN"))
            return new CResult(false, "Invalid request type", "CReqDataPayload", 164);
            
            // Start
            
            
            return new CResult(true, "Ok", "CReqDataPayload", 164);
        }
	
	public CResult commit(String sender) throws Exception
	{
		CDeliverBlocksPacket packet=new CDeliverBlocksPacket(this.start, this.end);
			
			for (long block=start; block<=end; block++)
			{
			  // Finds the block
			  File f = new File(UTILS.WRITEDIR+"blocks/block_"+block+".block");
			  if (f.exists())
 			  {
				  try
				  {
				     // Read image from disk
				     FileInputStream f_in = new FileInputStream(UTILS.WRITEDIR+"blocks/block_"+block+".block");

				     // Read object using ObjectInputStream
				     ObjectInputStream obj_in = new ObjectInputStream (f_in);

				     // Read an object
				     CBlockPacket obj = (CBlockPacket)obj_in.readObject();
				     
				     // Add block
				     packet.addBlock(obj);
				  }
				  catch (FileNotFoundException ex) 
				  {
					  UTILS.LOG.log("FileNotFoundException", ex.getMessage(), "CReqDataPayload.java", 77);
				  }
				  catch (IOException ex) 
				  {
					  UTILS.LOG.log("IOException", ex.getMessage(), "CReqDataPayload.java", 77);
				  }
				  catch (ClassNotFoundException ex) 
				  {
					  UTILS.LOG.log("ClassNotFoundException", ex.getMessage(), "CReqDataPayload.java", 77);
				  }
			  }
			 
			}
			
			// Send block
			UTILS.NETWORK.sendToPeer(sender, packet);
		
		
	
		
		
		// Return
	 	return new CResult(true, "Ok", "CReqDataPayload", 164);
	}
}
