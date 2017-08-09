// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.trade.speculative;

import wallet.kernel.CPackets;
import wallet.kernel.UTILS;
import wallet.network.packets.CBroadcastPacket;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.CFeePayload;

public class CClosePosPacket extends CBroadcastPacket
{
   public CClosePosPacket(String fee_adr, 
                          String adr, 
                          long posID, 
                          long percent) throws Exception
   {
          super("ID_CLOSE_SPEC_POS_PACKET");
	  
	  // Builds the payload class
	  CClosePosPayload dec_payload=new CClosePosPayload(adr, 
                                                            posID, 
                                                            percent);
					
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
	if (!this.tip.equals("ID_CLOSE_SPEC_POS_PACKET")) 
	    throw new Exception("Invalid packet type - CClosePosPacket.java");
		   
        // Deserialize transaction data
	CClosePosPayload dec_payload=(CClosePosPayload) UTILS.SERIAL.deserialize(payload);
		   
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
        foot.add("Position ID", dec_payload.posID);
        foot.add("Percent", dec_payload.percent);
        foot.write();
    }
		   
   
}
