// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.assets.auto_mkts;

import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CBroadcastPacket;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.CFeePayload;

public class CNewAutoMarketTradePacket extends CBroadcastPacket
{
      public CNewAutoMarketTradePacket(String net_fee_adr,
                                       String adr, 
                                       String mkt_symbol,
                                       String tip,
                                       double qty) throws Exception
    {
          super("ID_NEW_AUTO_ASSET_MARKET_POS_PACKET");
	  
	  // Builds the payload class 
	  CNewAutoMarketTradePayload dec_payload=new CNewAutoMarketTradePayload(adr, 
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
	public CResult check(CBlockPayload block) throws Exception
	{
	    // Super class
	    CResult res=super.check(block);
	    if (res.passed==false) return res;
		   	
	    // Check type
	    if (!this.tip.equals("ID_NEW_AUTO_ASSET_MARKET_POS_PACKET")) 
	       return new CResult(false, "Invalid packet type", "CNewAutoMarketTradePacket", 42);
		   
	    // Deserialize transaction data
	    CNewAutoMarketTradePayload dec_payload=(CNewAutoMarketTradePayload) UTILS.SERIAL.deserialize(payload);
		   
	    // Check payload
	    res=dec_payload.check(block);
            if (!res.passed) return res;
            
            // Check fee
            if (this.fee.amount<0.0001)
               return new CResult(false, "Invalid price", "CNewAutoMarketTradePacket", 42);
            
	    // Return 
	    return new CResult(true, "Ok", "CNewAutoMarketPacket", 74);
	}
		   
	public CResult commit(CBlockPayload block) throws Exception
	{
	    // Superclass
            CResult res=super.commit(block);
	    if (res.passed==false) return res;
		   	  
	    // Deserialize transaction data
	    CNewAutoMarketTradePayload ass_payload=(CNewAutoMarketTradePayload) UTILS.SERIAL.deserialize(payload);

	    // Commit payload
	    res=ass_payload.commit(block);	  
	    if (res.passed==false) return res;
	    		  
	    // Return 
            return new CResult(true, "Ok", "CNewAutoMarketTradePacket", 9);
      }
  }   