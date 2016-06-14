// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.ads;

import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.trans.*;
import wallet.network.packets.blocks.*;
import wallet.network.packets.shop.goods.CNewStorePayload;


public class CNewAdPacket extends CBroadcastPacket 
{
   public CNewAdPacket(String fee_adr, 
		       String adr, 
		       String country, 
		       long hours, 
		       double price, 
		       String title, 
		       String mes, 
		       String link,
                       String packet_sign,
                       String payload_sign) throws Exception
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
			                               link,
                                                       payload_sign);
			
	   // Build the payload
	   this.payload=UTILS.SERIAL.serialize(dec_payload);
			
	   // Network fee
	   fee=new CFeePayload(fee_adr,  price*hours);
	   
	   // Sign packet
           this.sign(packet_sign);
   }
   
   // Check 
   public CResult check(CBlockPayload block) throws Exception
   {
          // Super class
   	  CResult res=super.check(block);
   	  if (res.passed==false) return res;
   	
   	  // Check type
   	  if (!this.tip.equals("ID_NEW_AD_PACKET")) 
             return new CResult(false, "Invalid packet type", "CNewAdPacket", 39);
   	  
          // Deserialize transaction data
   	  CNewAdPayload dec_payload=(CNewAdPayload) UTILS.SERIAL.deserialize(payload);
          
          // Check fee
	  if (this.fee.amount<dec_payload.hours*0.0001)
	      throw new Exception("Invalid fee - CNewAdPacket.java");
          
          // Check payload
          dec_payload.check(block);
          
          // Footprint
          CFootprint foot=new CFootprint("ID_NEW_AD_PACKET", 
                                         this.hash, 
                                         dec_payload.hash, 
                                         this.fee.src, 
                                         this.fee.amount, 
                                         this.fee.hash,
                                         this.block);
                  
          foot.add("Address", dec_payload.target_adr);
          foot.add("Country", dec_payload.country);
          foot.add("Hours", String.valueOf(dec_payload.hours));
          foot.add("Price", String.valueOf(dec_payload.market_bid));
          foot.add("Title", dec_payload.title);
          foot.add("Message", dec_payload.mes);
          foot.add("Link", dec_payload.link);
          foot.write();
   	  
   	  // Return 
   	  return new CResult(true, "Ok", "CNewAdPacket", 45);
   }
   
   public CResult commit(CBlockPayload block) throws Exception
   {
       // Check
            CResult res=this.check(block);
                
	    // Superclass
	    res=super.commit(block);
	    if (res.passed==false) return res;
            
            try
            {
                // Begin
                UTILS.DB.begin();
                
                if (res.passed)
                {
                   // Deserialize transaction data
                   CNewAdPayload dec_payload=(CNewAdPayload) UTILS.SERIAL.deserialize(payload);
                
	           // Commit
	           res=dec_payload.commit(block);
	           if (res.passed==false) throw new Exception(res.reason); 
                }
                else throw new Exception(res.reason); 
                    
                // Commit
                UTILS.DB.commit();
            }
            catch (Exception ex)
            {
                // Rollback
                UTILS.DB.rollback();
                
                // Exception
                throw new Exception(ex.getMessage());
            }
			  
	    // Return 
	    return new CResult(true, "Ok", "CNewAdPacket.java", 9);
   }
}
