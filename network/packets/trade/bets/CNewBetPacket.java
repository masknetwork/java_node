// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.trade.bets;

import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CBroadcastPacket;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trade.speculative.CNewFeedMarketPayload;
import wallet.network.packets.trade.speculative.CNewSpecMarketPosPayload;
import wallet.network.packets.trans.CFeePayload;

public class CNewBetPacket extends CBroadcastPacket
{
   public CNewBetPacket(String fee_adr,
                        String adr, 
                        String feed_symbol_1, 
                        String feed_component_symbol_1, 
                        String feed_symbol_2, 
                        String feed_component_symbol_2, 
                        String feed_symbol_3, 
                        String feed_component_symbol_3, 
                        String tip, 
                        double val_1, 
                        double val_2, 
                        String title,
                        String description,
                        double budget, 
                        double win_multiplier, 
                        long start_block, 
                        long end_block, 
                        long accept_block, 
                        String cur,
                        String packet_sign,
                        String payload_sign) throws Exception
    {
          super("ID_NEW_FEED_BET_PACKET");
	  
	  // Builds the payload class
	  CNewBetPayload dec_payload=new CNewBetPayload(adr, 
                                                        feed_symbol_1, 
                                                        feed_component_symbol_1, 
                                                        feed_symbol_2, 
                                                        feed_component_symbol_2, 
                                                        feed_symbol_3, 
                                                        feed_component_symbol_3, 
                                                        tip, 
                                                        val_1, 
                                                        val_2, 
                                                        title,
                                                        description,
                                                        budget, 
                                                        win_multiplier, 
                                                        start_block, 
                                                        end_block, 
                                                        accept_block, 
                                                        cur,
                                                        payload_sign);  
                       
					
	  // Build the payload
	  this.payload=UTILS.SERIAL.serialize(dec_payload);
					
          // Network fee
	  fee=new CFeePayload(fee_adr, (end_block-this.block)*0.0001);
			   
	   // Sign packet
	   this.sign(payload_sign);
	}
		
        // Check 
	public CResult check(CBlockPayload block) throws Exception
	{
	    // Super class
	    CResult res=super.check(block);
	    if (res.passed==false) return res;
		   	
	    // Check type
	    if (!this.tip.equals("ID_NEW_FEED_BET_PACKET")) 
	       return new CResult(false, "Invalid packet type", "CNewBetPacket", 42);
		   
	    // Deserialize transaction data
	    CNewBetPayload dec_payload=(CNewBetPayload) UTILS.SERIAL.deserialize(payload);
		   
	    // Check payload
	    res=dec_payload.check(block);
            if (!res.passed) return res;
            
            // Check fee
            if (this.fee.amount<0.0001)
               return new CResult(false, "Invalid price", "CNewBetPacket", 42);
            
	    // Return 
	    return new CResult(true, "Ok", "CNewBetPacket", 74);
	}
		   
	public CResult commit(CBlockPayload block) throws Exception
	{
	    // Superclass
            CResult res=super.commit(block);
	    if (res.passed==false) return res;
		   	  
	    // Deserialize transaction data
	    CNewBetPayload ass_payload=(CNewBetPayload) UTILS.SERIAL.deserialize(payload);

	    // Commit payload
	    res=ass_payload.commit(block);	  
	    if (res.passed==false) return res;
	    		  
	    // Return 
            return new CResult(true, "Ok", "CNewBetPacket", 9);
      }
}
