// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.assets.reg_mkts;

import wallet.kernel.CPackets;
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
	public void check(CBlockPayload block) throws Exception
	{
	    // Super class
   	  super.check(block);
   	  
   	  // Check type
   	  if (!this.tip.equals("ID_REG_ASSET_MARKET_CLOSE_POS_PACKET")) 
             throw new Exception("Invalid packet type - CCloseRegMarketPosPacket.java");
   	  
          // Deserialize transaction data
   	  CCloseRegMarketPosPayload dec_payload=(CCloseRegMarketPosPayload) UTILS.SERIAL.deserialize(payload);
           
          // Check fee
	  if (this.fee.amount<0.0001)
	      throw new Exception("Invalid fee - CCloseRegMarketPosPacket.java");
          
          // Check payload
          dec_payload.check(block);
          
          // Footprint
          CPackets foot=new CPackets(this);
                  
          foot.add("Address", dec_payload.target_adr);
          foot.add("Order ID", String.valueOf(dec_payload.orderID));
          foot.write();
        }
}   
