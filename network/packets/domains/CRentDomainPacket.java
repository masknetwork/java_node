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
    // Serial
   private static final long serialVersionUID = 100L;
   
   public CRentDomainPacket(String fee_adr, 
                            String adr, 
                            String domain, 
                            long days)  throws Exception
   { 
       // Constructs the broadcast packet
       super("ID_RENT_DOMAIN_PACKET");
		  
       // Builds the payload class
       CRentDomainPayload dec_payload=new CRentDomainPayload(adr, domain, days);
				
       // Build the payload
       this.payload=UTILS.SERIAL.serialize(dec_payload);
				
       // Network fee
       CFeePayload fee=new CFeePayload(fee_adr,  0.0001*days);
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
	   if (!this.tip.equals("ID_RENT_DOMAIN_PACKET")) 
	      throw new Exception("Invalid packet type - CRentDomainPacket.java");
	   
	   // Deserialize transaction data
	   CRentDomainPayload dec_payload=(CRentDomainPayload) UTILS.SERIAL.deserialize(payload);
	   
           // Check payload
           dec_payload.check(block);
           
           // Deserialize payload
           CFeePayload fee=(CFeePayload) UTILS.SERIAL.deserialize(fee_payload);
        
	   // Check fee
	   if (fee.amount<(dec_payload.days*0.0001)) 
	       throw new Exception("Invalid fee - CRentDomainPacket.java");
	   
	   // Footprint
           CPackets foot=new CPackets(this);
           
           foot.add("Address", dec_payload.target_adr);
           foot.add("Name", dec_payload.domain);
           foot.add("Days", String.valueOf(dec_payload.days));
           foot.write();
          
	}
	   
	
}
