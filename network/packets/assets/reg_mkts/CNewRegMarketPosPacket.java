// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.assets.reg_mkts;

import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CBroadcastPacket;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.CFeePayload;

public class CNewRegMarketPosPacket extends CBroadcastPacket
{
    public CNewRegMarketPosPacket(String fee_adr,
                                  String adr,
                                  long mktID,
                                  String tip,
                                  double price,
                                  double qty,
                                  long mkt_days,
                                  String packet_sign,
                                  String payload_sign) throws Exception
    {
          super("ID_NEW_REG_ASSET_MARKET_POS_PACKET");
	  
	  // Builds the payload class
	  CNewRegMarketPosPayload dec_payload=new CNewRegMarketPosPayload(adr,
                                                                          mktID,
                                                                          tip,
                                                                          price,
                                                                          qty,
                                                                          mkt_days);
					
	  // Build the payload
	  this.payload=UTILS.SERIAL.serialize(dec_payload);
					
          // Network fee
	  fee=new CFeePayload(fee_adr, dec_payload.days*0.0001);
			   
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
	    if (!this.tip.equals("ID_NEW_REG_ASSET_MARKET_POS_PACKET")) 
	       return new CResult(false, "Invalid packet type", "CNewRegMarketPosPacket", 42);
		   
	    // Deserialize transaction data
	    CNewRegMarketPosPayload dec_payload=(CNewRegMarketPosPayload) UTILS.SERIAL.deserialize(payload);
		   
	    // Check payload
	    res=dec_payload.check(block);
            if (!res.passed) return res;
            
            // Check fee
            if (this.fee.amount<(dec_payload.days*0.0001))
               return new CResult(false, "Invalid price", "CBuyDomainPacket", 42);
            
	    // Return 
	    return new CResult(true, "Ok", "CNewRegMarketPosPacket", 74);
	}
		   
	public CResult commit(CBlockPayload block) throws Exception
	{
	    // Superclass
            CResult res=super.commit(block);
	    if (res.passed==false) return res;
		   	  
	    // Deserialize transaction data
	    CNewRegMarketPosPayload ass_payload=(CNewRegMarketPosPayload) UTILS.SERIAL.deserialize(payload);

	    // Commit payload
	    res=ass_payload.commit(block);	  
	    if (res.passed==false) return res;
	    		  
	    // Return 
            return new CResult(true, "Ok", "CNewRegMarketPosPacket", 9);
      }
  }   
