// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.assets.reg_mkts;

import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CBroadcastPacket;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.CFeePayload;

public class CCloseRegMarketPosPacket extends CBroadcastPacket
{
     public CCloseRegMarketPosPacket(String fee_adr,
                                    String adr,
                                    long orderID) throws Exception
    {
          super("ID_REG_ASSET_MARKET_CLOSE_POS_PACKET");
	  
	  // Builds the payload class
	  CCloseRegMarketPosPayload dec_payload=new CCloseRegMarketPosPayload(adr, orderID);
					
	  // Build the payload
	  this.payload=UTILS.SERIAL.serialize(dec_payload);
					
          // Network fee
	  fee=new CFeePayload(fee_adr, 0.0001);
			   
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
	    if (!this.tip.equals("ID_REG_ASSET_MARKET_CLOSE_POS_PACKET")) 
	       return new CResult(false, "Invalid packet type", "CCloseRegMarketPosPacket", 42);
		   
	    // Deserialize transaction data
	    CCloseRegMarketPosPayload dec_payload=(CCloseRegMarketPosPayload) UTILS.SERIAL.deserialize(payload);
		   
	    // Check payload
	    res=dec_payload.check(block);
            if (!res.passed) return res;
            
            // Check fee
            if (this.fee.amount<0.0001)
               return new CResult(false, "Invalid price", "CCloseRegMarketPosPacket", 42);
            
	    // Return 
	    return new CResult(true, "Ok", "CCloseRegMarketPosPacket", 74);
	}
		   
	public CResult commit(CBlockPayload block) throws Exception
	{
	    // Superclass
            CResult res=super.commit(block);
	    if (res.passed==false) return res;
		   	  
	    // Deserialize transaction data
	    CCloseRegMarketPosPayload ass_payload=(CCloseRegMarketPosPayload) UTILS.SERIAL.deserialize(payload);

	    // Commit payload
	    res=ass_payload.commit(block);	  
	    if (res.passed==false) return res;
	    		  
	    // Return 
            return new CResult(true, "Ok", "CCloseRegMarketPosPacket", 9);
      }
  }   
