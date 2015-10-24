package wallet.network.packets.markets.goods;

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
	                      String esc_1, String esc_2, String esc_3, String esc_4, String esc_5,
	                      String pic_1, String pic_2, String pic_3, String pic_4, String pic_5,
	                      String prod_location_town,
	                      String prod_location_country,
	                      String ships_to,
	                      String ships_exceptions,
	                      String condition,
	                      String delivery,
	                      String accept_escrowers,
	                      String return_policy,
	                      double postage,
	                      String carrier,
	                      double mkt_bid,
	                      long mkt_days,
	                      double price,
	                      String categ,
	                      String sub_categ)
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
                                                                esc_1,  esc_2,  esc_3,  esc_4,  esc_5,
                                                                prod_location_town,
                                                                prod_location_country,
                                                                ships_to,
                                                                ships_exceptions,
                                                                condition,
                                                                delivery,
                                                                accept_escrowers,
                                                                return_policy,
                                                                postage,
                                                                carrier,
                                                                mkt_bid,
                                                                mkt_days,
                                                                price,
                                                                categ,
                                                                sub_categ);
				
		// Build the payload
		this.payload=UTILS.SERIAL.serialize(dec_payload);
				
		// Network fee
		fee=new CFeePayload(fee_adr, mkt_days*mkt_bid);
		
		   
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
	   	  if (this.tip!="ID_ADD_ESCROWER") 
	   		return new CResult(false, "Invalid packet type", "CAddEscrowPacket", 42);
	   	
	   	  // Return 
	   	  return new CResult(true, "Ok", "CAddEscrowPacket", 45);
	   }
	   
	   public CResult commit(CBlockPayload block)
	   {
	   	  // Superclass
	   	  CResult res=super.commit(block);
	   	  if (res.passed==false) return res;
	   	  
	   	  
		  // Return 
	   	  return new CResult(true, "Ok", "CAddEscrowPacket", 62);
	   }
}