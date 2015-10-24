package wallet.network.packets.markets.feeds.bets;

import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CBroadcastPacket;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.markets.feeds.CNewFeedMarketPayload;
import wallet.network.packets.markets.feeds.speculative.CNewSpecMarketPosPayload;
import wallet.network.packets.trans.CFeePayload;

public class CNewBetPacket extends CBroadcastPacket
{
   public CNewBetPacket(String fee_adr,
                        String adr, 
                        String feed_symbol, 
                        String feed_component_symbol, 
                        String tip, 
                        double val_1, 
                        double val_2, 
                        double budget, 
                        double win_multiplier, 
                        long start_block, 
                        long end_block, 
                        long expires_block, 
                        String cur,
                        double mkt_bid)
    {
          super("ID_NEW_FEED_BET_PACKET");
	  
	  // Builds the payload class
	  CNewBetPayload dec_payload=new CNewBetPayload(adr, 
                                                        feed_symbol, 
                                                        feed_component_symbol, 
                                                        tip, 
                                                        val_1, 
                                                        val_2, 
                                                        budget, 
                                                        win_multiplier, 
                                                        start_block, 
                                                        end_block, 
                                                        expires_block, 
                                                        cur,
                                                        mkt_bid);  
                       
					
	  // Build the payload
	  this.payload=UTILS.SERIAL.serialize(dec_payload);
					
          // Network fee
	  fee=new CFeePayload(fee_adr, (expires_block-this.block)*0.0001);
			   
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
	    if (this.tip!="ID_NEW_FEED_BET_PACKET") 
	       return new CResult(false, "Invalid packet type", "CBuyDomainPacket", 42);
		   
	    // Deserialize transaction data
	    CNewBetPayload dec_payload=(CNewBetPayload) UTILS.SERIAL.deserialize(payload);
		   
	    // Check payload
	    res=dec_payload.check(block);
            if (!res.passed) return res;
            
            // Check fee
            if (this.fee.amount<0.0001)
               return new CResult(false, "Invalid price", "CBuyDomainPacket", 42);
            
	    // Return 
	    return new CResult(true, "Ok", "CBuyDomainPacket", 74);
	}
		   
	public CResult commit(CBlockPayload block)
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
            return new CResult(true, "Ok", "CNewAssetPacket", 9);
      }
}
