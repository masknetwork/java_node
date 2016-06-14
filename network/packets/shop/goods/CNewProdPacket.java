// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.shop.goods;

import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.blocks.*;
import wallet.network.packets.trans.*;

public class CNewProdPacket extends CBroadcastPacket 
{
	public CNewProdPacket(String fee_adr, 
			      String adr,
	                      String section,
	                      String section_country,
	                      String title,
	                      String description,
	                      String internalID,
	                      String web_page,
	                      String pic_1, String pic_2, String pic_3, String pic_4, String pic_5,
	                      String prod_location_town,
	                      String prod_location_country,
	                      String ships_to,
	                      String ships_exceptions,
	                      String condition,
	                      String delivery,
	                      String return_policy,
	                      double postage,
	                      String carrier,
	                      long mkt_days,
	                      double price,
	                      String categ,
	                      String sub_categ,
                              String packet_sign,
                              String payload_sign) throws Exception
	{
		super("ID_NEW_PHYS_PROD_PACKET");

		// Builds the payload class
		CNewProdPayload dec_payload=new CNewProdPayload(adr,
                                                                section,
                                                                section_country,
                                                                title,
                                                                description,
                                                                internalID,
                                                                web_page,
                                                                pic_1,  pic_2,  pic_3,  pic_4,  pic_5,
                                                                prod_location_town,
                                                                prod_location_country,
                                                                ships_to,
                                                                ships_exceptions,
                                                                condition,
                                                                delivery,
                                                                return_policy,
                                                                postage,
                                                                carrier,
                                                                mkt_days,
                                                                price,
                                                                categ,
                                                                sub_categ);
				
		// Build the payload
		this.payload=UTILS.SERIAL.serialize(dec_payload);
				
		// Network fee
		fee=new CFeePayload(fee_adr, mkt_days*0.00001);
		
		   
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
	   	  if (this.tip!="ID_ADD_ESCROWER") 
	   		return new CResult(false, "Invalid packet type", "CAddEscrowPacket", 42);
	   	
	   	  // Return 
	   	  return new CResult(true, "Ok", "CAddEscrowPacket", 45);
	   }
	   
	   public CResult commit(CBlockPayload block) throws Exception
	   {
	   	  // Superclass
	   	  CResult res=super.commit(block);
	   	  if (res.passed==false) return res;
	   	  
	   	  
		  // Return 
	   	  return new CResult(true, "Ok", "CAddEscrowPacket", 62);
	   }
}