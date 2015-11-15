package wallet.network.packets.blocks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.SQLException;

import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.trans.*;
import wallet.kernel.*;


public class CBlockPacket extends CPacket 
{
	// Signer
        public String signer;
        
        // Sign
        public String sign;
        
        // Timestamp
        public long tstamp;
        
	public CBlockPacket(String signer)
        {
	   // Constructor
	   super("ID_BLOCK");
            
           // Signer
           this.signer=signer;
        }
	
        // Sign
	public void sign()
        { 
            // Packet hash
            this.hash=UTILS.BASIC.hash(this.signer+
                                       this.sign+
                   		       new String(UTILS.BASIC.hash(UTILS.SERIAL.serialize(this.payload))));
		   
            // Signature address
            CAddress adr=UTILS.WALLET.getAddress(this.signer);
	    this.sign=adr.sign(this.hash);
        }
        
        
	// Check 
	public CResult check()
	{
             // Check type
	     if (!this.tip.equals("ID_BLOCK")) 
	   	return new CResult(false, "Invalid packet type", "CBlockPacket", 39);
           
	     // Deserialize transaction data
	     CBlockPayload block_payload=(CBlockPayload) UTILS.SERIAL.deserialize(payload);
	   	  
	      // Super class
	      CResult res=super.check(block_payload);
	      if (res.passed==false) 
	      {
	   	return res;
	      }
	      else
	      {
	   	 res=block_payload.check();
	   	 if (res.passed==false) return res;
	      }
	   	  
	      // Return 
	      return new CResult(true, "Ok", "CBlockPacket", 42);
	}
	   
	public CResult commit() throws SQLException
	{
		// Deserialize transaction data
	   	CBlockPayload block_payload=(CBlockPayload) UTILS.SERIAL.deserialize(payload);
	   	  
	   	// Superclass
	   	CResult res=super.commit(block_payload);
	   	if (res.passed==false) return res;
	   	
	   	// Commit payload
	   	res=block_payload.commit();
	   	if (res.passed==false) return res;
                
                // Record block ?
                try
                { 
                   FileOutputStream fout = new FileOutputStream(new File(UTILS.WRITEDIR+"blocks/block_"+block_payload.block+".block"));
                   ObjectOutputStream oos = new ObjectOutputStream(fout);
                   oos.writeObject(this);
                }
                catch (IOException ex)
                {
        	   UTILS.LOG.log("IOException", ex.getMessage(), "CBlockPayload.java", 76);
                }
	   	
		// Return 
	   	return new CResult(true, "Ok", "CBlockPacket", 55);
	}
}