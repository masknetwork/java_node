package wallet.network.packets.markets.feeds.regular;

import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CBroadcastPacket;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.markets.feeds.CNewFeedMarketPayload;
import wallet.network.packets.trans.CFeePayload;

public class CNewFeedMarketOrderPacket extends CBroadcastPacket
{
    public CNewFeedMarketOrderPacket(String net_fee_adr,
                                     String adr,
                                     String mkt_symbol, 
                                     String tip, 
                                     double qty)
    {
          super("ID_NEW_FEED_MARKET_ORDER_PACKET");
	  
	  // Builds the payload class
	  CNewFeedMarketOrderPayload dec_payload=new CNewFeedMarketOrderPayload(adr,
                                                                                mkt_symbol, 
                                                                                tip, 
                                                                                qty);
					
	  // Build the payload
	  this.payload=UTILS.SERIAL.serialize(dec_payload);
					
          // Network fee
	  fee=new CFeePayload(net_fee_adr, 0.0001);
			   
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
	    if (!this.tip.equals("ID_NEW_FEED_MARKET_ORDER_PACKET")) 
	       return new CResult(false, "Invalid packet type", "CNewFeedMarketOrderPacket", 42);
		   
	    // Deserialize transaction data
	    CNewFeedMarketOrderPayload dec_payload=(CNewFeedMarketOrderPayload) UTILS.SERIAL.deserialize(payload);
		   
	    // Check payload
	    res=dec_payload.check(block);
            if (!res.passed) return res;
            
            // Check fee
            if (this.fee.amount<0.00001)
               return new CResult(false, "Invalid price", "CNewFeedMarketOrderPacket", 42);
            
	    // Return 
	    return new CResult(true, "Ok", "CNewFeedMarketOrderPacket", 74);
	}
		   
	public CResult commit(CBlockPayload block)
	{
	    // Superclass
            CResult res=super.commit(block);
	    if (res.passed==false) return res;
		   	  
	    // Deserialize transaction data
	    CNewFeedMarketOrderPayload ass_payload=(CNewFeedMarketOrderPayload) UTILS.SERIAL.deserialize(payload);

	    // Commit payload
	    res=ass_payload.commit(block);	  
	    if (res.passed==false) return res;
	    		  
	    // Return 
            return new CResult(true, "Ok", "CNewFeedMarketOrderPacket", 9);
      }
}