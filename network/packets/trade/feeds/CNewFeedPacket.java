// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.trade.feeds;

import wallet.kernel.*;
import wallet.network.packets.*;
import wallet.network.packets.trans.*;
import wallet.network.*;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.domains.CBuyDomainPayload;

public class CNewFeedPacket extends CBroadcastPacket
{
    public CNewFeedPacket(String fee_adr, 
                          String adr,
                          String title, 
                          String description,
                          String website,
                          String symbol, 
                          long days) throws Exception
    {
        // Constructor
        super("ID_NEW_FEED_PACKET");
	
        // Payload
        CNewFeedPayload dec_payload=new CNewFeedPayload(adr,
                                                        title, 
                                                        description,
                                                        website,
                                                        symbol, 
                                                        days);
        
	// Build the payload
	this.payload=UTILS.SERIAL.serialize(dec_payload);
					
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
	if (!this.tip.equals("ID_NEW_FEED_PACKET")) 
	    throw new Exception("Invalid packet type - CNewFeedPacket.java");
		   
        // Deserialize transaction data
	CNewFeedPayload dec_payload=(CNewFeedPayload) UTILS.SERIAL.deserialize(payload);
		   
	// Check payload
	dec_payload.check(block);
        
        // Deserialize payload
        CFeePayload fee=(CFeePayload) UTILS.SERIAL.deserialize(fee_payload);
     
        // Check fee
        if (fee.amount<(0.0001*dec_payload.days))
            throw new Exception("Invalid price - CNewFeedPacket.java");
        
        // Footprint
        CPackets foot=new CPackets(this);
        foot.add("Title", dec_payload.title);
        foot.add("Description", dec_payload.description);
        foot.add("Website", dec_payload.website);
        foot.add("Symbol", dec_payload.symbol);
        foot.add("Days", dec_payload.days);
        foot.write();
    }		   
}
