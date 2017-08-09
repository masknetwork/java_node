// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.trade.speculative;

import wallet.kernel.CPackets;
import wallet.kernel.UTILS;
import wallet.network.packets.CBroadcastPacket;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.assets.reg_mkts.CNewRegMarketPayload;
import wallet.network.packets.trans.CFeePayload;

public class CNewMarginMarketPacket extends CBroadcastPacket
{
   public CNewMarginMarketPacket(String fee_adr,
                                 String mkt_adr, 
			         String feed, 
			         String branch, 
			         String cur, 
			         long max_leverage,
                                 double max_total_margin,
			         double spread,
			         String title,
			         String desc,
                                 long max_down,
			         long max_up,
			         long days) throws Exception
    {
          super("ID_NEW_FEED_SPEC_MARKET_PACKET");
	  
	  // Builds the payload class
	  CNewMarginMarketPayload dec_payload=new CNewMarginMarketPayload(mkt_adr, 
                                                                    feed, 
			                                            branch, 
			                                            cur, 
				                                    max_leverage,
                                                                    max_total_margin,
			                                            spread,
				                                    title,
				                                    desc,
                                                                    max_down,
                                                                    max_up,
				                                    days);
					
	  // Build the payload
	  this.payload=UTILS.SERIAL.serialize(dec_payload);
          
          // Net fee
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
	    if (!this.tip.equals("ID_NEW_FEED_SPEC_MARKET_PACKET")) 
	       throw new Exception("Invalid packet type - CNewFeedMarketPayload.java");
		   
	    // Deserialize transaction data
	    CNewMarginMarketPayload dec_payload=(CNewMarginMarketPayload) UTILS.SERIAL.deserialize(payload);
		   
	    // Check payload
	    dec_payload.check(block);
            
            // Deserialize payload
            CFeePayload fee=(CFeePayload) UTILS.SERIAL.deserialize(fee_payload);
     
            // Check fee
            if (fee.amount<(dec_payload.days*0.0001))
               throw new Exception("Invalid fee - CNewFeedMarketPayload.java");
            
            // Footprint
            CPackets foot=new CPackets(this);
            foot.add("Address", dec_payload.target_adr);
            foot.add("Feed 1", dec_payload.feed);
            foot.add("Branch 1", dec_payload.branch);
            foot.add("Currency", dec_payload.cur);
            foot.add("Max Leverage", dec_payload.max_leverage);
            foot.add("Max Leverage", dec_payload.max_total_margin);
            foot.add("Spread", dec_payload.spread);
            foot.add("Title", dec_payload.title);
            foot.add("Description", dec_payload.desc);
            foot.add("Market ID", dec_payload.mktID);
            foot.add("Days", dec_payload.days);
            foot.write();
	}
}
