// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.trade.pegged_assets;

import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CBroadcastPacket;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trade.speculative.CNewFeedMarketPayload;
import wallet.network.packets.trans.CFeePayload;

public class CNewPeggedAssetOrderPacket extends CBroadcastPacket
{
    public CNewPeggedAssetOrderPacket(String net_fee_adr,
                                      String adr,
                                      long mktID, 
                                      String tip, 
                                      double qty) throws Exception
    {
          super("ID_NEW_FEED_MARKET_ORDER_PACKET");
	  
	  // Builds the payload class
	  CNewPeggedAssetOrderPayload dec_payload=new CNewPeggedAssetOrderPayload(adr,
                                                                                mktID, 
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
	    if (!this.tip.equals("ID_NEW_FEED_MARKET_ORDER_PACKET")) 
	       return new CResult(false, "Invalid packet type", "CNewFeedMarketOrderPacket", 42);
		   
	    // Deserialize transaction data
	    CNewPeggedAssetOrderPayload dec_payload=(CNewPeggedAssetOrderPayload) UTILS.SERIAL.deserialize(payload);
		   
	    // Check payload
	    res=dec_payload.check(block);
            if (!res.passed) return res;
            
            // Check fee
            if (this.fee.amount<0.00001)
               return new CResult(false, "Invalid price", "CNewFeedMarketOrderPacket", 42);
            
	    // Return 
	    return new CResult(true, "Ok", "CNewFeedMarketOrderPacket", 74);
	}
		   
	public CResult commit(CBlockPayload block) throws Exception
	{
	    // Superclass
            CResult res=super.commit(block);
	    if (res.passed==false) return res;
		   	  
	    // Deserialize transaction data
	    CNewPeggedAssetOrderPayload ass_payload=(CNewPeggedAssetOrderPayload) UTILS.SERIAL.deserialize(payload);

	    // Commit payload
	    res=ass_payload.commit(block);	  
	    if (res.passed==false) return res;
	    		  
	    // Return 
            return new CResult(true, "Ok", "CNewFeedMarketOrderPacket", 9);
      }
}