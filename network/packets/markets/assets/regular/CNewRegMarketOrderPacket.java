package wallet.network.packets.markets.assets.regular;

import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CBroadcastPacket;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.CFeePayload;

public class CNewRegMarketOrderPacket extends CBroadcastPacket
{
    public CNewRegMarketOrderPacket(String fee_adr,
                                    String adr,
                                    String uid,
                                    double qty)
    {
          super("ID_NEW_REG_ASSET_MARKET_ORDER_PACKET");
	  
	  // Builds the payload class
	  CNewRegMarketOrderPayload dec_payload=new CNewRegMarketOrderPayload(adr, 
                                                                              uid,
                                                                              qty);
					
	  // Build the payload
	  this.payload=UTILS.SERIAL.serialize(dec_payload);
					
          // Network fee
	  fee=new CFeePayload(fee_adr, 0.0001);
			   
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
	    if (!this.tip.equals("ID_NEW_REG_ASSET_MARKET_ORDER_PACKET")) 
	       return new CResult(false, "Invalid packet type", "CNewRegMarketPosPacket", 42);
		   
	    // Deserialize transaction data
	    CNewRegMarketOrderPayload dec_payload=(CNewRegMarketOrderPayload) UTILS.SERIAL.deserialize(payload);
		   
	    // Check payload
	    res=dec_payload.check(block);
            if (!res.passed) return res;
            
            // Check fee
            if (this.fee.amount<0.0001)
               return new CResult(false, "Invalid price", "CBuyDomainPacket", 42);
            
	    // Return 
	    return new CResult(true, "Ok", "CNewRegMarketPosPacket", 74);
	}
		   
	public CResult commit(CBlockPayload block)
	{
	    // Superclass
            CResult res=super.commit(block);
	    if (res.passed==false) return res;
		   	  
	    // Deserialize transaction data
	    CNewRegMarketOrderPayload ass_payload=(CNewRegMarketOrderPayload) UTILS.SERIAL.deserialize(payload);

	    // Commit payload
	    res=ass_payload.commit(block);	  
	    if (res.passed==false) return res;
	    		  
	    // Return 
            return new CResult(true, "Ok", "CNewRegMarketPosPacket", 9);
      }
  }   
