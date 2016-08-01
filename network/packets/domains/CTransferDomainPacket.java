// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.domains;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.*;


public class CTransferDomainPacket extends CBroadcastPacket 
{
    // Serial
   private static final long serialVersionUID = 100L;
   
   public CTransferDomainPacket(String fee_adr, 
                                String adr, 
                                String domain, 
                                String to_adr) throws Exception
   {
        // Constructor
	super("ID_TRANSFER_DOMAIN_PACKET");
	
        // Builds the payload class
	CTransferDomainPayload dec_payload=new CTransferDomainPayload(adr, 
	                                                              domain, 
	                                                              to_adr);
						
        // Build the payload
        this.payload=UTILS.SERIAL.serialize(dec_payload);
						
	// Network fee
	fee=new CFeePayload(fee_adr);
				   
	// Sign packet
	this.sign();
   }
			 
   // Check 
   public void check(CBlockPayload block) throws Exception
   {
	// Super class
	super.check(block);
			   	
	// Check type
	if (!this.tip.equals("ID_TRANSFER_DOMAIN_PACKET")) 
	  throw new Exception("Invalid packet type - CTransferDomainPacket.java");
        
        // Deserialize transaction data
	CTransferDomainPayload dec_payload=(CTransferDomainPayload) UTILS.SERIAL.deserialize(payload);
	   
        // Check payoad
        dec_payload.check(block);
         
        // Check fee
        if (this.fee.amount<0.0001)
               throw new Exception("Invalid fee - CTransferDomainPacket.java");
            
        // Footprint
        CPackets foot=new CPackets(this);
           
        foot.add("Domain", dec_payload.domain);
        foot.add("To Address", dec_payload.to_adr);
        foot.write();
   }
			   
 
}
