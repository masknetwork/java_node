// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.domains;

import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.*;

public class CBuyDomainPacket extends CBroadcastPacket 
{
    // Serial
   private static final long serialVersionUID = 100L;
   
  public CBuyDomainPacket(String fee_adr,
		          String buyer_adr, 
                          String domain) throws Exception
  {
	  super("ID_BUY_DOMAIN_PACKET");
	  
	  // Builds the payload class
	  CBuyDomainPayload dec_payload=new CBuyDomainPayload(buyer_adr, domain);
					
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
	    if (!this.tip.equals("ID_BUY_DOMAIN_PACKET")) 
	       throw new Exception("Invalid packet type - CBuyDomainPacket.java");
		   
	    // Deserialize transaction data
	    CBuyDomainPayload dec_payload=(CBuyDomainPayload) UTILS.SERIAL.deserialize(payload);
		   
	    // Check payload
	    dec_payload.check(block);
            
            // Check fee
            if (this.fee.amount<0.0001)
               throw new Exception("Invalid fee - CBuyDomainPacket.java");
            
           // Footprint
           CPackets foot=new CPackets(this);
           foot.add("Buyer", dec_payload.target_adr);
           foot.add("Domain", dec_payload.domain);
           foot.write();
        }
}
