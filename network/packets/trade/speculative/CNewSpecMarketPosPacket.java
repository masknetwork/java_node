// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.trade.speculative;

import wallet.kernel.CPackets;
import wallet.kernel.UTILS;
import wallet.network.packets.CBroadcastPacket;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.CFeePayload;

public class CNewSpecMarketPosPacket extends CBroadcastPacket
{
    public CNewSpecMarketPosPacket(String fee_adr,
                                   String adr, 
		                   long mktID, 
				   String tip, 
				   double open, 
				   double sl, 
				   double tp, 
				   long leverage, 
				   double qty,
                                   String ex_type,
                                   long days) throws Exception
    {
          super("ID_NEW_SPEC_MARKET_POS_PACKET");
	  
	  // Builds the payload class
	  CNewSpecMarketPosPayload dec_payload=new CNewSpecMarketPosPayload(adr, 
		                                                           mktID, 
				                                           tip, 
				                                           open, 
				                                           sl, 
				                                           tp, 
				                                           leverage, 
				                                           qty,
                                                                           ex_type,
                                                                           days);
					
	  // Build the payload
	  this.payload=UTILS.SERIAL.serialize(dec_payload);
					
          // Network fee
	   CFeePayload fee=new CFeePayload(fee_adr,  0.0001*days);
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
	    if (!this.tip.equals("ID_NEW_SPEC_MARKET_POS_PACKET"))
	       throw new Exception("Invalid packet type - CNewSpecMarketPosPacket.java");
		   
	    // Deserialize transaction data
	    CNewSpecMarketPosPayload dec_payload=(CNewSpecMarketPosPayload) UTILS.SERIAL.deserialize(payload);
		   
	    // Check payload
	    dec_payload.check(block);
            
            // Deserialize payload
            CFeePayload fee=(CFeePayload) UTILS.SERIAL.deserialize(fee_payload);
            
            // Check fee
            if (fee.amount<0.0001*dec_payload.days)
               throw new Exception("Invalid price - CNewSpecMarketPosPacket.java");
            
            // Footprint
            CPackets foot=new CPackets(this);
            foot.add("Address", dec_payload.target_adr);
            foot.add("Market ID", dec_payload.mktID);
            foot.add("Position ID", dec_payload.posID);
            foot.add("Open", dec_payload.open);
            foot.add("Stop Loss", dec_payload.sl);
            foot.add("Take Profit", dec_payload.tp);
            foot.add("Leverage", dec_payload.leverage);
            foot.add("Qty", dec_payload.qty);
            foot.add("Type", dec_payload.tip);
            foot.add("Days", dec_payload.days);
            foot.write();
	}
}
