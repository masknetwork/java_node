package wallet.network.packets.markets.feeds;

import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CBroadcastPacket;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.markets.assets.regular.CNewRegMarketPayload;
import wallet.network.packets.trans.CFeePayload;

public class CNewFeedMarketPacket extends CBroadcastPacket
{
   public CNewFeedMarketPacket(String net_fee_adr,
                               String mkt_adr, 
			       String tip, 
                               String mkt_symbol, 
			       String feed, 
			       String branch, 
			       String asset, 
			       long asset_qty, 
			       String cur, 
			       String name, 
			       String desc, 
			       String fee_adr, 
			       double mkt_fee,
			       int decimals,
                               long max_leverage,
                               long min_hold,
			       double bid, 
			       long days)
    {
          super("ID_NEW_FEED_MARKET_PACKET");
	  
	  // Builds the payload class
	  CNewFeedMarketPayload dec_payload=new CNewFeedMarketPayload(tip,
                                                                      mkt_adr, 
			                                              mkt_symbol, 
			                                              feed, 
			                                              branch, 
			                                              asset, 
				                                      asset_qty, 
			                                              cur, 
			                                              name, 
			                                              desc, 
			                                              fee_adr, 
			                                              mkt_fee,
				                                      decimals,
                                                                      max_leverage,
                                                                      min_hold,
			                                              bid, 
			                                              days);
					
	  // Build the payload
	  this.payload=UTILS.SERIAL.serialize(dec_payload);
					
          // Network fee
	  fee=new CFeePayload(net_fee_adr, dec_payload.days*dec_payload.bid);
			   
	   // Sign packet
	   this.sign();
	}
		
        // Check 
	public CResult check(CBlockPayload block)
	{
	    // Super class
	    CResult res=super.check(block);
	    if (res.passed==false) return res;
		   	
	    // Check type
	    if (!this.tip.equals("ID_NEW_FEED_MARKET_PACKET")) 
	       return new CResult(false, "Invalid packet type", "CNewFeedMarketPayload", 42);
		   
	    // Deserialize transaction data
	    CNewFeedMarketPayload dec_payload=(CNewFeedMarketPayload) UTILS.SERIAL.deserialize(payload);
		   
	    // Check payload
	    res=dec_payload.check(block);
            if (!res.passed) return res;
            
            // Check fee
            if (this.fee.amount<(dec_payload.days*dec_payload.bid))
               return new CResult(false, "Invalid price", "CNewFeedMarketPayload", 42);
            
	    // Return 
	    return new CResult(true, "Ok", "CNewFeedMarketPayload", 74);
	}
		   
	public CResult commit(CBlockPayload block)
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
