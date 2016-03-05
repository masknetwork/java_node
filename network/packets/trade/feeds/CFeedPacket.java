// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.trade.feeds;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.CFootprint;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CBroadcastPacket;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.CFeePayload;

public class CFeedPacket extends CBroadcastPacket
{
    public CFeedPacket(String fee_adr, String feed_symbol) throws Exception
    {
       // Constructor
       super("ID_FEED_PACKET");
           
        try
        {
           // Network fee
	   fee=new CFeePayload(fee_adr);
         }
        catch (Exception e) 
	{ 
            UTILS.LOG.log("Exception", e.getMessage(), "CFeedSource.java", 639); 
        }
    }
    
    public void addPayload(CFeedPayload pay) throws Exception
    {
         // Build the payload
	 this.payload=UTILS.SERIAL.serialize(pay);
         
         // Sign
         this.sign();
    }
    
   // Check  
    public CResult check(CBlockPayload block) throws Exception
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
            
            // Footprint
            CFootprint foot=new CFootprint("ID_FEED_PACKET", 
                                           this.hash, 
                                           dec_payload.hash, 
                                           this.fee.src, 
                                           this.fee.amount, 
                                           this.fee.hash,
                                           this.block);
                  
            foot.add("Feed Symbol", dec_payload.feed_symbol);
            foot.write();
            
            // Return 
	    return new CResult(true, "Ok", "CFeedPacket.java", 74);
	}
		   
	public CResult commit(CBlockPayload block) throws Exception
	{
	    // Check
            CResult res=this.check(block);
            
            if (res.passed)
            {
              // Commit
              super.commit(block);
              
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
