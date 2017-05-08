// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.trade.speculative;

import wallet.kernel.CPackets;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CBroadcastPacket;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.CFeePayload;

public class CUpdatePosPacket extends CBroadcastPacket
{
   public CUpdatePosPacket(String fee_adr, 
                           String adr, 
                           long posID, 
                           double sl,
                           double tp) throws Exception
   {
          super("ID_UPDATE_SPEC_POS_PACKET");
	  
	  // Builds the payload class
	  CUpdatePosPayload dec_payload=new CUpdatePosPayload(adr,
                                                              posID,
                                                              sl,
                                                              tp);
					
	  // Build the payload
	  this.payload=UTILS.SERIAL.serialize(dec_payload);
					
          // Network fee
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
	if (!this.tip.equals("ID_UPDATE_SPEC_POS_PACKET")) 
	    throw new Exception("Invalid packet type - CUpdatePosPacket.java"); 
		   
	// Deserialize transaction data
	CUpdatePosPayload dec_payload=(CUpdatePosPayload) UTILS.SERIAL.deserialize(payload);
		   
	// Check payload
	dec_payload.check(block);
        
        // Deserialize payload
        CFeePayload fee=(CFeePayload) UTILS.SERIAL.deserialize(fee_payload);
     
        // Check fee
        if (fee.amount<0.0001)
           throw new Exception("Invalid fee - CUpdatePosPacket.java"); 
        
        // Footprint
        CPackets foot=new CPackets(this);
        foot.add("Address", dec_payload.target_adr);
        foot.add("Stop Loss", dec_payload.sl);
        foot.add("Take Profit", dec_payload.tp);
        foot.write();
    }
		   
  
}