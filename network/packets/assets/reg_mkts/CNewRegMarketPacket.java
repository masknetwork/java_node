// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.assets.reg_mkts;

import wallet.kernel.CPackets;
import wallet.kernel.UTILS;
import wallet.network.packets.CBroadcastPacket;
import wallet.network.packets.assets.CIssueMoreAssetsPayload;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.domains.CBuyDomainPayload;
import wallet.network.packets.trans.CFeePayload;

public class CNewRegMarketPacket extends CBroadcastPacket 
{
     public CNewRegMarketPacket(String fee_adr,
                                String adr, 
                                String asset_symbol,
                                String cur_symbol,
                                int decimals,
                                String title, 
                                String description,
                                long days) throws Exception
   {
	   super("ID_NEW_ASSET_MKT_PACKET");

	   // Builds the payload class
	   CNewRegMarketPayload dec_payload=new CNewRegMarketPayload(adr, 
                                                                     asset_symbol,
                                                                     cur_symbol,
                                                                     decimals,
                                                                     title, 
                                                                     description,
                                                                     days); 
           
	   // Build the payload
	   this.payload=UTILS.SERIAL.serialize(dec_payload);
           
	   // Network fee
	  CFeePayload fee=new CFeePayload(fee_adr,  UTILS.CONSTANTS.FEE_MULT*days);
	  this.fee_payload=UTILS.SERIAL.serialize(fee);
	   
	   // Sign packet
	   this.sign();
   }
   
   // Check 
   public void check(CBlockPayload block) throws Exception
   {
         // Super class
   	  super.check(block);
   	  
   	  // Check type
   	  if (!this.tip.equals("ID_NEW_ASSET_MKT_PACKET")) 
             throw new Exception("Invalid packet type - CNewRegMarketPacket.java");
   	  
          // Deserialize transaction data
   	  CNewRegMarketPayload dec_payload=(CNewRegMarketPayload) UTILS.SERIAL.deserialize(payload);
          
          // Deserialize payload
          CFeePayload fee=(CFeePayload) UTILS.SERIAL.deserialize(fee_payload);
        
          // Check fee
	  if (fee.amount<dec_payload.days*0.0001)
	      throw new Exception("Invalid fee - CIssueMoreAssetsPacket.java");
          
          // Check payload
          dec_payload.check(block);
          
          // Footprint
          CPackets foot=new CPackets(this);
                  
          foot.add("Address", dec_payload.target_adr);
          foot.add("Asset Symbol", dec_payload.asset_symbol);
          foot.add("Currency Symbol", dec_payload.cur_symbol);
          foot.add("Title", dec_payload.title);
          foot.add("Description", dec_payload.description);
          foot.add("Decimals", String.valueOf(dec_payload.decimals));
          foot.add("Days", String.valueOf(dec_payload.days));
          foot.write();
   	  
   }
   
   
}