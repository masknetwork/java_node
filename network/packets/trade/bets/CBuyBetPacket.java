// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.trade.bets;

import wallet.kernel.CPackets;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CBroadcastPacket;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.CFeePayload;

public class CBuyBetPacket extends CBroadcastPacket
{
   public CBuyBetPacket(String fee_adr,
                        String adr, 
                        long bet_uid, 
                        double amount) throws Exception
    {
          super("ID_NEW_BUY_BET_PACKET");
	  
	  // Builds the payload class
	  CBuyBetPayload dec_payload=new CBuyBetPayload(adr, 
                                                        bet_uid, 
                                                        amount);  
                       
					
	  // Build the payload
	  this.payload=UTILS.SERIAL.serialize(dec_payload);
					
          // Network fee
	  fee=new CFeePayload(fee_adr);
			   
	   // Sign packet
	   this.sign();
	}
		
        // Check 
	public void check(CBlockPayload block) throws Exception
	{
	    // Super class
	    super.check(block);
	    	   	
	    // Check type
	    if (!this.tip.equals("ID_NEW_BUY_BET_PACKET")) 
	       throw new Exception("Invalid packet type - CBuyBetPacket.java");
		   
	    // Deserialize transaction data
	    CBuyBetPayload dec_payload=(CBuyBetPayload) UTILS.SERIAL.deserialize(payload);
		   
	    // Check payload
	    dec_payload.check(block);
            
            // Check fee
            if (this.fee.amount<0.0001)
               throw new Exception("Invalid price - CBuyBetPacket.java");
            
            // Footprint
            CPackets foot=new CPackets(this);
            foot.add("Bet UID", dec_payload.bet_uid);
            foot.add("Amount", dec_payload.amount);
            foot.write();
	}
		   
	
}
