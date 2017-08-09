// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.trade.feeds;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.CPackets;
import wallet.kernel.UTILS;
import wallet.network.packets.CBroadcastPacket;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.CFeePayload;

public class CFeedPacket extends CBroadcastPacket
{
    public CFeedPacket(String fee_adr, 
                       String feed_symbol) throws Exception
    {
       // Constructor
       super("ID_FEED_PACKET");
           
       // Network fee
       CFeePayload fee=new CFeePayload(fee_adr,  0.0001);
       this.fee_payload=UTILS.SERIAL.serialize(fee);
    }
    
    public void addPayload(CFeedPayload pay) throws Exception
    {
         // Build the payload
	 this.payload=UTILS.SERIAL.serialize(pay);
         
         // Sign
         this.sign();
    }
    
   // Check  
    public void check(CBlockPayload block) throws Exception
    {
	    // Super class
	    super.check(block);
	    	   	
	    // Check type
	    if (!this.tip.equals("ID_FEED_PACKET")) 
	       throw new Exception("Invalid packet type");
		   
	    // Deserialize transaction data
	    CFeedPayload dec_payload=(CFeedPayload) UTILS.SERIAL.deserialize(payload);
		   
	    // Check payload
	    dec_payload.check(block);
            
            // Footprint
            CPackets foot=new CPackets(this);
            foot.add("Feed Symbol", dec_payload.feed_symbol);
            foot.write();
	}
		   
	
}
