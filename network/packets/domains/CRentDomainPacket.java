// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.domains;

import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.ads.CNewAdPayload;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.*;

public class CRentDomainPacket extends CBroadcastPacket 
{
   public CRentDomainPacket(String fee_adr, 
                            String adr, 
                            String domain, 
                            long days,
                            String packet_sign,
                            String payload_sign)  throws Exception
   {
       // Constructs the broadcast packet
       super("ID_RENT_DOMAIN_PACKET");
		  
       // Builds the payload class
       CRentDomainPayload dec_payload=new CRentDomainPayload(adr, domain, days, payload_sign);
				
       // Build the payload
       this.payload=UTILS.SERIAL.serialize(dec_payload);
				
       // Network fee
       fee=new CFeePayload(fee_adr, (days*0.0001));
		   
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
	   if (!this.tip.equals("ID_RENT_DOMAIN_PACKET")) 
	   	  return new CResult(false, "Invalid packet type", "CRentDomainPacket", 42);
	   
	   // Deserialize transaction data
	   CRentDomainPayload dec_payload=(CRentDomainPayload) UTILS.SERIAL.deserialize(payload);
	   
           // Check payload
           res=dec_payload.check(block);
           if (!res.passed) return res;
           
	   // Check fee
	   if (this.fee.amount<(dec_payload.days*0.0001)) 
	       return new CResult(false, "Invalid fee", "CRentDomainPacket", 42);
	   
	   // Check sig
	   if (!this.checkSign())
	       return new CResult(false, "Invalid signature", "CBlockAdrPacket", 44);
           
           // Footprint
           CFootprint foot=new CFootprint("ID_RENT_DOMAIN_PACKET", 
                                          this.hash, 
                                          dec_payload.hash, 
                                          this.fee.src, 
                                          this.fee.amount, 
                                          this.fee.hash,
                                          this.block);
           
          foot.add("Address", dec_payload.target_adr);
          foot.add("Name", dec_payload.domain);
          foot.add("Days", String.valueOf(dec_payload.days));
          foot.write();
          
	   // Return 
	   return new CResult(true, "Ok", "CRentDomainPacket", 74);
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
                   CRentDomainPayload dec_payload=(CRentDomainPayload) UTILS.SERIAL.deserialize(payload);
                
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
	    return new CResult(true, "Ok", "CRentDomainPacket.java", 9);
	}
}
