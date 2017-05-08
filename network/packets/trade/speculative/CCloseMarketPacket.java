// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.trade.speculative;

import wallet.kernel.CPackets;
import wallet.kernel.UTILS;
import wallet.network.packets.CBroadcastPacket;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trade.speculative.CCloseMarketPayload;
import wallet.network.packets.trade.speculative.CClosePosPayload;
import wallet.network.packets.trans.CFeePayload;

public class CCloseMarketPacket extends CBroadcastPacket
{
   public CCloseMarketPacket(String fee_adr, 
                             String adr, 
                             long mktID) throws Exception
   {
          super("ID_CLOSE_MARGIN_MKT_PACKET");
	  
	  // Builds the payload class
	  CCloseMarketPayload dec_payload=new CCloseMarketPayload(adr, 
                                                                  mktID);
					
	  // Build the payload
	  this.payload=UTILS.SERIAL.serialize(dec_payload);
					
           CFeePayload fee=new CFeePayload(fee_adr,  0.0001);
	   this.fee_payload=UTILS.SERIAL.serialize(fee);
			   
	   // Sign packet
	   this.sign();
   }
   
   // Check 
   public void check(CBlockPayload block) throws Exception
   {
	// Super class
	super.check(block);
	    	   	
	// Check type
	if (!this.tip.equals("ID_CLOSE_MARGIN_MKT_PACKET")) 
	    throw new Exception("Invalid packet type - CClosePosPacket.java");
		   
        // Deserialize transaction data
	CCloseMarketPayload dec_payload=(CCloseMarketPayload) UTILS.SERIAL.deserialize(payload);
		   
	// Check payload
	dec_payload.check(block);
        
        // Deserialize payload
        CFeePayload fee=(CFeePayload) UTILS.SERIAL.deserialize(fee_payload);
     
        // Check fee
        if (fee.amount<0.0001)
           throw new Exception("Invalid fee - CClosePosPacket.java");
        
        // Footprint
        CPackets foot=new CPackets(this);
        foot.add("Address", dec_payload.target_adr);
        foot.add("Market ID", dec_payload.mktID);
        foot.write();
    }
		   
   
}
