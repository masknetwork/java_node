// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.trade.bets;

import wallet.kernel.CPackets;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CBroadcastPacket;
import wallet.network.packets.blocks.CBlockPayload;
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
                        String cur) throws Exception
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
                                                        cur);  
                       
					
	  // Build the payload
	  this.payload=UTILS.SERIAL.serialize(dec_payload);
					
          // Fee
          double trans_fee=(end_block-this.block)*0.0000001;
          if (trans_fee<0.0001) trans_fee=0.0001;
          
          // Network fee
	  fee=new CFeePayload(fee_adr, trans_fee);
			   
	   // Sign packet
	   this.sign();
	}
		
        // Check 
	public void check(CBlockPayload block) throws Exception
	{
	    // Super class
	    super.check(block);
	    	   	
	    // Check type
	    if (!this.tip.equals("ID_NEW_FEED_BET_PACKET")) 
	       throw new Exception("Invalid packet type - CNewBetPacket.java");
		   
	    // Deserialize transaction data
	    CNewBetPayload dec_payload=(CNewBetPayload) UTILS.SERIAL.deserialize(payload);
		   
	    // Check payload
	    dec_payload.check(block);
            
             // Fee
             double trans_fee=(dec_payload.end_block-this.block)*0.0000001;
             if (trans_fee<0.0001) trans_fee=0.0001;
            
            // Check fee
            if (this.fee.amount<trans_fee)
               throw new Exception("Invalid price - CNewBetPacket.java");
            
            // Footprint
            CPackets foot=new CPackets(this);
            foot.add("Feed Symbol 1", dec_payload.feed_symbol_1);
            foot.add("Feed Branch 1", dec_payload.feed_component_symbol_1);
            foot.add("Feed Symbol 2", dec_payload.feed_symbol_2);
            foot.add("Feed Branch 2", dec_payload.feed_component_symbol_2);
            foot.add("Feed Symbol 3", dec_payload.feed_symbol_3);
            foot.add("Feed Branch 3", dec_payload.feed_component_symbol_3);
            foot.add("Type", dec_payload.tip);
            foot.add("Value 1", dec_payload.val_1);
            foot.add("Value 2", dec_payload.val_2);
            foot.add("Budget", dec_payload.budget);
            foot.add("Win Multiplier", dec_payload.win_multiplier);
            foot.add("Start Block", dec_payload.start_block);
            foot.add("End Block", dec_payload.end_block);
            foot.add("Accept Block", dec_payload.accept_block);
            foot.add("Currency", dec_payload.cur);
            foot.add("Bet ID", dec_payload.betID);
            foot.add("Title", dec_payload.title);
            foot.add("Description", dec_payload.description);
            foot.write();
	}
}
