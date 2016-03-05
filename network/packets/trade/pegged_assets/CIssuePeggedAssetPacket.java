package wallet.network.packets.trade.pegged_assets;

import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CBroadcastPacket;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.CFeePayload;

public class CIssuePeggedAssetPacket extends CBroadcastPacket
{
    public CIssuePeggedAssetPacket(String net_fee_adr,
                                     String adr, 
                                     String feed_1, String branch_1, 
				     String feed_2, String branch_2, 
				     String feed_3, String branch_3, 
				     String cur,
				     long qty,
				     double spread, 
				     String rl_symbol, 
			             int decimals, 
				     long interest_interval, 
				     double interest, 
				     String asset_symbol,
				     double trans_fee,
				     String trans_fee_adr,
				     String name, 
				     String description, 
				     String img,
                                     long days) throws Exception
    {
          super("ID_NEW_FEED_ASSET_MARKET_PACKET");
	  
	  // Builds the payload class
	  CIssuePeggedAssetPayload dec_payload=new CIssuePeggedAssetPayload(adr, 
                                                                                feed_1, branch_1, 
				                                                feed_2, branch_2, 
				                                                feed_3, branch_3, 
				                                                cur,
				                                                qty,
				                                                spread, 
				                                                rl_symbol, 
			                                                        decimals, 
				                                                interest_interval, 
				                                                interest, 
				                                                asset_symbol,
				                                                trans_fee,
				                                                trans_fee_adr,
				                                                name, 
				                                                description, 
				                                                img,
                                                                                days);
					
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
	    if (!this.tip.equals("ID_NEW_FEED_ASSET_MARKET_PACKET")) 
	       return new CResult(false, "Invalid packet type", "CNewFeedMarketOrderPacket", 42);
		   
	    // Deserialize transaction data
	    CIssuePeggedAssetPayload dec_payload=(CIssuePeggedAssetPayload) UTILS.SERIAL.deserialize(payload);
		   
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
	    CIssuePeggedAssetPayload ass_payload=(CIssuePeggedAssetPayload) UTILS.SERIAL.deserialize(payload);

	    // Commit payload
	    res=ass_payload.commit(block);	  
	    if (res.passed==false) return res;
	    		  
	    // Return 
            return new CResult(true, "Ok", "CNewFeedMarketOrderPacket", 9);
      }
}
