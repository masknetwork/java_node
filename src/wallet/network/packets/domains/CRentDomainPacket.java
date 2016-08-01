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
           
	   // Check fee
	   if (this.fee.amount<(dec_payload.days*0.0001)) 
	       throw new Exception("Invalid fee - CRentDomainPacket.java");
	   
	   // Footprint
           CPackets foot=new CPackets(this);
           
           foot.add("Address", dec_payload.target_adr);
           foot.add("Name", dec_payload.domain);
           foot.add("Days", String.valueOf(dec_payload.days));
           foot.write();
          
	}
	   
	
}
