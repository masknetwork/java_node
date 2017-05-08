// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.ads;

import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.trans.*;
import wallet.network.packets.blocks.*;


public class CNewAdPacket extends CBroadcastPacket 
{
   public CNewAdPacket(String fee_adr, 
		       String adr, 
		       String country, 
		       long hours, 
		       double price, 
		       String title, 
		       String mes, 
		       String link) throws Exception
   {
	   // Super class
	   super("ID_NEW_AD_PACKET");
	   
	   // Builds the payload class
	   CNewAdPayload dec_payload=new CNewAdPayload(adr, 
			                               country, 
			                               hours, 
			                               price, 
			                               title, 
			                               mes, 
			                               link);
			
	   // Build the payload
	   this.payload=UTILS.SERIAL.serialize(dec_payload);
			
	   // Network fee
	  CFeePayload fee=new CFeePayload(fee_adr,  price*hours);
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
   	  if (!this.tip.equals("ID_NEW_AD_PACKET")) 
             throw new Exception("Invalid packet type - CNewAdPacket.java");
   	  
          // Deserialize transaction data
   	  CNewAdPayload dec_payload=(CNewAdPayload) UTILS.SERIAL.deserialize(payload);
          
          // Deserialize payload
         CFeePayload fee=(CFeePayload) UTILS.SERIAL.deserialize(fee_payload);
        
          // Check fee
	  if (fee.amount<dec_payload.hours*0.0001)
	      throw new Exception("Invalid fee - CNewAdPacket.java");
          
          // Check payload
          dec_payload.check(block);
          
          // Footprint
          CPackets foot=new CPackets(this);
                  
          foot.add("Address", dec_payload.target_adr);
          foot.add("Country", dec_payload.country);
          foot.add("Hours", String.valueOf(dec_payload.hours));
          foot.add("Price", String.valueOf(dec_payload.market_bid));
          foot.add("Title", dec_payload.title);
          foot.add("Message", dec_payload.mes);
          foot.add("Link", dec_payload.link);
          foot.write();
   	  
   }
   
   
}
