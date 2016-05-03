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
		
		// Return
	 	return new CResult(true, "Ok", "CReqDataPayload", 164);
	}
        
        public void sendBlocks()
        {
          
        }
}
