package wallet.network.packets.markets.assets.regular;

import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CBroadcastPacket;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.domains.CBuyDomainPayload;
import wallet.network.packets.trans.CFeePayload;

public class CNewRegMarketPacket extends CBroadcastPacket 
{
    public CNewRegMarketPacket(String net_fee_adr, 
                               String adr, 
                               String asset_symbol,
                               String cur_symbol, 
                               String mkt_symbol, 
                               String title, 
                               String description,
                               String fee_adr,
                               double fee_amount,
                               int decimals,
                               double bid, 
                               long days)
    {
          super("ID_NEW_REG_ASSET_MARKET_PACKET");
	  
	  // Builds the payload class
	  CNewRegMarketPayload dec_payload=new CNewRegMarketPayload(adr, 
                                                                    asset_symbol,
                                                                    cur_symbol, 
                                                                    mkt_symbol, 
                                                                    title, 
                                                                    description,
                                                                    fee_adr,
                                                                    fee_amount,
                                                                    decimals,
                                                                    bid, 
                                                                    days);
					
	  // Build the payload
	  this.payload=UTILS.SERIAL.serialize(dec_payload);
					
          // Network fee
	  fee=new CFeePayload(net_fee_adr, bid*days);
			   
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
	    if (!this.tip.equals("ID_NEW_REG_ASSET_MARKET_PACKET")) 
	       return new CResult(false, "Invalid packet type", "CNewRegMarketPacket", 42);
		   
	    // Deserialize transaction data
	    CNewRegMarketPayload dec_payload=(CNewRegMarketPayload) UTILS.SERIAL.deserialize(payload);
		   
	    // Check payload
	    res=dec_payload.check(block);
            if (!res.passed) return res;
            
            // Check fee
            if (this.fee.amount<(dec_payload.days*dec_payload.bid))
               return new CResult(false, "Invalid price", "CNewRegMarketPacket", 42);
            
	    // Return 
	    return new CResult(true, "Ok", "CNewRegMarketPacket", 74);
	}
		   
	public CResult commit(CBlockPayload block)
	{
	    // Superclass
            CResult res=super.commit(block);
	    if (res.passed==false) return res;
		   	  
	    // Deserialize transaction data
	    CNewRegMarketPayload ass_payload=(CNewRegMarketPayload) UTILS.SERIAL.deserialize(payload);

	    // Commit payload
	    res=ass_payload.commit(block);	  
	    if (res.passed==false) return res;
	    		  
	    // Return 
            return new CResult(true, "Ok", "CNewRegMarketPacket", 9);
      }
  }   

