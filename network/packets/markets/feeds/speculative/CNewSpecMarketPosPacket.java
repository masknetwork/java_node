package wallet.network.packets.markets.feeds.speculative;

import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CBroadcastPacket;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.markets.feeds.CNewFeedMarketPayload;
import wallet.network.packets.trans.CFeePayload;

public class CNewSpecMarketPosPacket extends CBroadcastPacket
{
    public CNewSpecMarketPosPacket(String fee_adr,
                                   String adr, 
                                   String mkt_symbol, 
                                   String tip, 
                                   double open,
                                   double sl,
                                   double tp,
                                   double ts,
                                   double qty,
                                   long leverage)
    {
          super("ID_NEW_SPEC_MARKET_POS_PACKET");
	  
	  // Builds the payload class
	  CNewSpecMarketPosPayload dec_payload=new CNewSpecMarketPosPayload(adr, 
                                                                            mkt_symbol, 
                                                                            tip, 
                                                                            open,
                                                                            sl,
                                                                            tp,
                                                                            ts,
                                                                            qty,
                                                                            leverage);
					
	  // Build the payload
	  this.payload=UTILS.SERIAL.serialize(dec_payload);
					
          // Network fee
	  fee=new CFeePayload(fee_adr);
			   
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
	    if (this.tip!="ID_NEW_SPEC_MARKET_POS_PACKET") 
	       return new CResult(false, "Invalid packet type", "CBuyDomainPacket", 42);
		   
	    // Deserialize transaction data
	    CNewFeedMarketPayload dec_payload=(CNewFeedMarketPayload) UTILS.SERIAL.deserialize(payload);
		   
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
	    CNewFeedMarketPayload ass_payload=(CNewFeedMarketPayload) UTILS.SERIAL.deserialize(payload);

	    // Commit payload
	    res=ass_payload.commit(block);	  
	    if (res.passed==false) return res;
	    		  
	    // Return 
            return new CResult(true, "Ok", "CNewAssetPacket", 9);
      }
}
