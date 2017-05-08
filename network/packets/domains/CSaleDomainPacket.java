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

public class CSaleDomainPacket extends CBroadcastPacket 
{
    // Serial
   private static final long serialVersionUID = 100L;
   
   public CSaleDomainPacket(String fee_adr, 
                            String adr, 
		            String domain, 
                            double sale_price) throws Exception
   {
        // Constructor
	super("ID_SALE_DOMAIN_PACKET");
           
        // Builds the payload class
	CSaleDomainPayload dec_payload=new CSaleDomainPayload(adr, 
			                                      domain, 
			                                      sale_price);
					
	// Build the payload
	this.payload=UTILS.SERIAL.serialize(dec_payload);
					
	// Network fee
	CFeePayload fee=new CFeePayload(fee_adr,  0.0001);
	this.fee_payload=UTILS.SERIAL.serialize(fee);
			   
	// Sign packet
	this.sign();
   }

    public void check(CBlockPayload block) throws Exception
    {
	// Super class
	super.check(block);
	   	
	// Check type
	if (!this.tip.equals("ID_SALE_DOMAIN_PACKET")) 
	    throw new Exception("Invalid packet type - CSaleDomainPacket.java");
	   
	// Deserialize transaction data
	CSaleDomainPayload dec_payload=(CSaleDomainPayload) UTILS.SERIAL.deserialize(payload);
	   
        // Check payoad
        dec_payload.check(block);
        
        // Deserialize payload
        CFeePayload fee=(CFeePayload) UTILS.SERIAL.deserialize(fee_payload);
           
	// Check fee
        if (fee.amount<0.0001)
	   throw new Exception("Invalid packet type - CSaleDomainPacket.java");
        
        // Footprint
        CPackets foot=new CPackets(this);
        foot.add("Address", dec_payload.target_adr);
        foot.add("Domain", dec_payload.domain);
        foot.add("Sale Price", dec_payload.sale_price);
        foot.write();
    }
}

