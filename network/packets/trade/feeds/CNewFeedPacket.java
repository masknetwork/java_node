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
                          long mkt_days) throws Exception
    {
        // Constructor
        super("ID_NEW_FEED_PACKET");
	
        // Payload
        CNewFeedPayload dec_payload=new CNewFeedPayload(adr,
                                                        title, 
                                                        description,
                                                        website,
                                                        symbol, 
                                                        mkt_days);
        
	// Build the payload
	this.payload=UTILS.SERIAL.serialize(dec_payload);
					
        // Network fee
	fee=new CFeePayload(fee_adr, 0.0001*mkt_days);
			   
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
	    if (!this.tip.equals("ID_NEW_FEED_PACKET")) 
	       return new CResult(false, "Invalid packet type", "CNewFeedPacket", 42);
		   
	    // Deserialize transaction data
	    CNewFeedPayload dec_payload=(CNewFeedPayload) UTILS.SERIAL.deserialize(payload);
		   
	    // Check payload
	    res=dec_payload.check(block);
            if (!res.passed) return res;
            
            // Check fee
            if (this.fee.amount<(0.0001*dec_payload.mkt_days))
               return new CResult(false, "Invalid price", "CNewFeedPacket", 42);
            
	    // Return 
	    return new CResult(true, "Ok", "CNewFeedPacket", 74);
	}
		   
	public CResult commit(CBlockPayload block) throws Exception
	{
	    // Superclass
            CResult res=super.commit(block);
	    if (res.passed==false) return res;
		   	  
	    // Deserialize transaction data
	    CNewFeedPayload dec_payload=(CNewFeedPayload) UTILS.SERIAL.deserialize(payload);

	    // Commit payload
	    res=dec_payload.commit(block);	  
	    if (res.passed==false) return res;
	    		  
	    // Return 
            return new CResult(true, "Ok", "CNewFeedPacket", 9);
      }
}
