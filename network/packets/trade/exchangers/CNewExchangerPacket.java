// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.trade.exchangers;

import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.blocks.*;
import wallet.network.packets.shop.escrowers.CNewEscrowerPayload;
import wallet.network.packets.trans.*;

public class CNewExchangerPacket extends CBroadcastPacket 
{
   public CNewExchangerPacket(String net_fee_adr,
                              String adr,
                              String title,
                              String desc,
                              String webpage,
                              String pos_type,
                              String asset,
                              String cur,
                              String pay_method,
                              String pay_details,
                              String price_type,
                              double price,
                              String price_feed,
                              String price_branch,
                              double price_margin,
                              String country,
                              String town_type,
                              String town,
                              String escrowers,
		              long days,
                              String packet_sign,
                              String payload_sign) throws Exception
   {
	   // Constructs the broadcast packet
	   super("ID_NEW_EXCHANGER_PACKET");
			  
	   // Builds the payload class
	   CNewExchangerPayload dec_payload=new CNewExchangerPayload(adr,
                                                                     title,
                                                                     desc,
                                                                     webpage,
                                                                     pos_type,
                                                                     asset,
                                                                     cur,
                                                                     pay_method,
                                                                     pay_details,
                                                                     price_type,
                                                                     price,
                                                                     price_feed,
                                                                     price_branch,
                                                                     price_margin,
                                                                     country,
                                                                     town_type,
                                                                     town,
                                                                     escrowers,
		                                                     days,
                                                                     payload_sign);
					
	   // Build the payload
	   this.payload=UTILS.SERIAL.serialize(dec_payload);
					
	   // Network fee
	   fee=new CFeePayload(net_fee_adr, days*0.0001);
			   
	    // Sign packet
	    this.sign(payload_sign);
	}
		
	// Check 
	public CResult check(CBlockPayload block) throws Exception
	{
	    // Super class
            CResult res=super.check(block);
            if (res.passed==false) return res;
  	
            // Check type
            if (!this.tip.equals("ID_NEW_EXCHANGER_PACKET")) 
                 return new CResult(false, "Invalid packet type", "CMesPacketPacket", 39);
  	  
            // Check
            CNewExchangerPayload dec_payload=(CNewExchangerPayload) UTILS.SERIAL.deserialize(payload);
            res=dec_payload.check(block);
            if (!res.passed) return res;
     
            // Footprint
            CFootprint foot=new CFootprint("ID_NEW_EXCHANGER_PACKET", 
                                           this.hash, 
                                           dec_payload.hash, 
                                           this.fee.src, 
                                           this.fee.amount, 
                                           this.fee.hash,
                                           this.block);
                                        
            foot.add("Title", dec_payload.title);
            foot.add("Description", dec_payload.desc);
            foot.add("Web Page", dec_payload.webpage);
            foot.add("Postition Type", dec_payload.pos_type);
            foot.add("Asset", dec_payload.asset);
            foot.add("Currency", dec_payload.cur);
            foot.add("Payment Method", dec_payload.pay_method);
            foot.add("Payment Details", dec_payload.pay_details);
            foot.add("Price Type", dec_payload.price_type);
            foot.add("Price", String.valueOf(dec_payload.price));
            foot.add("Price Feed", dec_payload.price_feed);
            foot.add("Price Branch", dec_payload.price_feed);
            foot.add("Price Margin", dec_payload.price_branch);
            foot.add("Country", dec_payload.country);
            foot.add("Town Type", dec_payload.town_type);
            foot.add("Town", dec_payload.town);
            foot.add("Escrowers", dec_payload.escrowers);
            foot.add("Days", String.valueOf(dec_payload.days));
            
            foot.write();
  	
            // Return 
            return new CResult(true, "Ok", "CMesPacketPacket", 45);
	}
		   
	public CResult commit(CBlockPayload block) throws Exception
	{
            // Superclass
	    CResult res=super.commit(block);
	    if (res.passed==false) return res;
		   	  
	    // Deserialize transaction data
	    CNewExchangerPayload ass_payload=(CNewExchangerPayload) UTILS.SERIAL.deserialize(payload);

	    // Fee is 0.0001 / day ?
	    res=ass_payload.commit(block);
	    if (res.passed==false) return res;
			  
	    // Return 
	    return new CResult(true, "Ok", "CNewEscrowerPacket", 9);
	}
}
