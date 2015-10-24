package wallet.network.packets.feeds;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CBroadcastPacket;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.CFeePayload;

public class CFeedPacket extends CBroadcastPacket
{
    public CFeedPacket(String fee_adr, String feed_symbol)
    {
        // Constructor
        super("ID_FEED_PACKET");
	
        // Network fee
	fee=new CFeePayload(fee_adr);
    }
    
    public void addPayload(CFeedPayload pay)
    {
         // Build the payload
	 this.payload=UTILS.SERIAL.serialize(pay);
         
         // Sign
         this.sign();
    }
    
   // Check  
    public CResult check(CBlockPayload block)
    {
	    // Super class
	    CResult res=super.check(block);
	    if (res.passed==false) return res;
		   	
	    // Check type
	    if (!this.tip.equals("ID_FEED_PACKET")) 
	       return new CResult(false, "Invalid packet type", "CFeedPacket.java", 42);
		   
	    // Deserialize transaction data
	    CFeedPayload dec_payload=(CFeedPayload) UTILS.SERIAL.deserialize(payload);
		   
	    // Check payload
	    res=dec_payload.check(block);
            if (!res.passed) return res;
            
            // Return 
	    return new CResult(true, "Ok", "CFeedPacket.java", 74);
	}
		   
	public CResult commit(CBlockPayload block)
	{
	    // Superclass
            CResult res=super.commit(block);
	    if (res.passed==false) return res;
            
            res=this.check(block);
            if (res.passed)
            {
	      // Deserialize transaction data
	      CFeedPayload dec_payload=(CFeedPayload) UTILS.SERIAL.deserialize(payload);

	      // Commit payload
	      res=dec_payload.commit(block);	  
	      if (!res.passed) return res;
            }
            
	    // Return 
            return new CResult(true, "Ok", "CFeedPacket.java", 9);
      }
}
