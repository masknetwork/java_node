// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.assets.reg_mkts;

import wallet.kernel.CPackets;
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
                                  long mkt_days) throws Exception
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
	public void check(CBlockPayload block) throws Exception
	{
	     // Super class
   	   super.check(block);
   	   
   	   // Check type
   	   if (!this.tip.equals("ID_NEW_REG_ASSET_MARKET_POS_PACKET")) 
                throw new Exception("Invalid packet type - CNewRegMarketPosPacket.java");
   	  
             // Deserialize transaction data
   	     CNewRegMarketPosPayload dec_payload=(CNewRegMarketPosPayload) UTILS.SERIAL.deserialize(payload);
           
             // Check fee
	     if (this.fee.amount<dec_payload.days*0.0001)
	         throw new Exception("Invalid fee - CNewRegMarketPosPacket.java");
          
             // Check payload
             dec_payload.check(block);
          
             // Footprint
             CPackets foot=new CPackets(this);
                  
              foot.add("Address", dec_payload.target_adr);
              foot.add("Market ID", String.valueOf(dec_payload.mktID));
              foot.add("OrderID", String.valueOf(dec_payload.orderID));
              foot.add("Type", dec_payload.tip);
              foot.add("Qty", String.valueOf(dec_payload.qty));
              foot.add("Price", String.valueOf(dec_payload.price));
              foot.add("Days", String.valueOf(dec_payload.days));
              foot.write();
   	  
	}
}   
