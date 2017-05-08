package wallet.network.packets.trade.speculative;

import wallet.network.packets.CBroadcastPacket;
import wallet.kernel.CPackets;
import wallet.kernel.UTILS;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.CFeePayload;

public class CWthFundsPacket extends CBroadcastPacket
{
   public CWthFundsPacket(String net_fee_adr, 
                           String adr,
                           long mktID,
                           double amount,
                           String rec) throws Exception
   {
          super("ID_WTH_SPEC_MKT_FUNDS_PACKET");
	  
	  // Builds the payload class
	  CWthFundsPayload dec_payload=new CWthFundsPayload(adr,
                                                            mktID,
                                                            amount,
                                                            rec);
					
	  // Build the payload
	  this.payload=UTILS.SERIAL.serialize(dec_payload);
					
          // Network fee
	   CFeePayload fee=new CFeePayload(net_fee_adr,  0.0001);
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
	if (!this.tip.equals("ID_WTH_SPEC_MKT_FUNDS_PACKET")) 
	    throw new Exception("Invalid packet type - CWthFundsPacket.java"); 
		   
	// Deserialize transaction data
	CWthFundsPayload dec_payload=(CWthFundsPayload) UTILS.SERIAL.deserialize(payload);
		   
	// Check payload
	dec_payload.check(block);
        
        // Deserialize payload
       CFeePayload fee=(CFeePayload) UTILS.SERIAL.deserialize(fee_payload);
     
        // Check fee
        if (fee.amount<0.0001)
           throw new Exception("Invalid fee - CWthFundsPacket.java"); 
        
        // Footprint
        CPackets foot=new CPackets(this);
        foot.add("Address", dec_payload.target_adr);
        foot.add("Market ID", dec_payload.mktID);
        foot.add("Amount", dec_payload.amount);
        foot.write();
    }
		   
  
}