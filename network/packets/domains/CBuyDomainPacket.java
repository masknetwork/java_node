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
  public CBuyDomainPacket(String fee_adr,
		          String buyer_adr, 
                          String attach_adr, 
                          String domain,
                          String packet_sign,
                          String payload_sign) throws Exception
  {
	  super("ID_BUY_DOMAIN_PACKET");
	  
	  // Builds the payload class
	  CBuyDomainPayload dec_payload=new CBuyDomainPayload(buyer_adr, attach_adr, domain);
					
	  // Build the payload
	  this.payload=UTILS.SERIAL.serialize(dec_payload);
					
          // Network fee
	  fee=new CFeePayload(fee_adr, dec_payload.pay.amount*0.001);
			   
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
	    if (!this.tip.equals("ID_BUY_DOMAIN_PACKET")) 
	       return new CResult(false, "Invalid packet type", "CBuyDomainPacket", 42);
		   
	    // Deserialize transaction data
	    CBuyDomainPayload dec_payload=(CBuyDomainPayload) UTILS.SERIAL.deserialize(payload);
		   
	    // Check payload
	    res=dec_payload.check(block);
            if (!res.passed) return res;
            
            // Check fee
            if (this.fee.amount<(dec_payload.pay.amount*0.001))
               return new CResult(false, "Invalid price", "CBuyDomainPacket", 42);
            
	    // Return 
	    return new CResult(true, "Ok", "CBuyDomainPacket", 74);
	}
		   
	public CResult commit(CBlockPayload block) throws Exception
	{
	    // Superclass
            CResult res=super.commit(block);
	    if (res.passed==false) return res;
		   	  
	    // Deserialize transaction data
	    CBuyDomainPayload ass_payload=(CBuyDomainPayload) UTILS.SERIAL.deserialize(payload);

	    // Commit payload
	    res=ass_payload.commit(block);	  
	    if (res.passed==false) return res;
	    		  
	    // Return 
            return new CResult(true, "Ok", "CNewAssetPacket", 9);
      }
  }
