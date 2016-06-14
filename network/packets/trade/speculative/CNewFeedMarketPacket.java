// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.trade.speculative;

import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CBroadcastPacket;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.assets.reg_mkts.CNewRegMarketPayload;
import wallet.network.packets.trans.CFeePayload;

public class CNewFeedMarketPacket extends CBroadcastPacket
{
   public CNewFeedMarketPacket(String net_fee_adr,
                               String mkt_adr, 
			       String feed_1, 
			       String branch_1, 
			       String feed_2, 
			       String branch_2, 
			       String feed_3, 
			       String branch_3, 
			       String cur, 
			       long min_hold,
			       long max_hold, 
			       long min_leverage,
			       long max_leverage,
			       double spread,
			       String real_symbol,
			       int decimals,
			       String pos_type,
			       double long_int,
			       double short_int,
			       long interest_interval,
			       String title,
			       String desc,
			       double max_margin,
			       long days,
                               String packet_sign,
                               String payload_sign) throws Exception
    {
          super("ID_NEW_FEED_SPEC_MARKET_PACKET");
	  
	  // Builds the payload class
	  CNewFeedMarketPayload dec_payload=new CNewFeedMarketPayload(mkt_adr, 
			                                              Math.round(Math.random()*10000000000L), 
			                                              feed_1, 
			                                              branch_1, 
				                                      feed_2, 
				                                      branch_2, 
			 	                                      feed_3, 
				                                      branch_3, 
				                                      cur, 
				                                      min_hold,
				                                      max_hold, 
			 	                                      min_leverage,
				                                      max_leverage,
			                                              spread,
				                                      real_symbol,
			                                              decimals,
			                                              pos_type,
				                                      long_int,
			                                              short_int,
				                                      interest_interval,
			                                              title,
				                                      desc,
				                                      max_margin,
				                                      days);
					
	  // Build the payload
	  this.payload=UTILS.SERIAL.serialize(dec_payload);
					
          // Network fee
	  fee=new CFeePayload(net_fee_adr, dec_payload.days*0.0001);
			   
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
	    if (!this.tip.equals("ID_NEW_FEED_SPEC_MARKET_PACKET")) 
	       return new CResult(false, "Invalid packet type", "CNewFeedMarketPayload", 42);
		   
	    // Deserialize transaction data
	    CNewFeedMarketPayload dec_payload=(CNewFeedMarketPayload) UTILS.SERIAL.deserialize(payload);
		   
	    // Check payload
	    res=dec_payload.check(block);
            if (!res.passed) return res;
            
            // Check fee
            if (this.fee.amount<(dec_payload.days*0.0001))
               return new CResult(false, "Invalid price", "CNewFeedMarketPayload", 42);
            
	    // Return 
	    return new CResult(true, "Ok", "CNewFeedMarketPayload", 74);
	}
		   
	public CResult commit(CBlockPayload block) throws Exception
	{
	    // Superclass
            CResult res=super.commit(block);
	    if (res.passed==false) return res;
		   	  
	    // Deserialize transaction data
	    CNewFeedMarketPayload ass_payload=(CNewFeedMarketPayload) UTILS.SERIAL.deserialize(payload);

	    // Commit payload
	    res=ass_payload.commit(block);	  
	    if (res.passed==false) return res;
	    		  
	    // Return 
            return new CResult(true, "Ok", "CNewFeedMarketPayload", 9);
      }
}
