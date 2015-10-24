package wallet.network.packets.markets.feeds.bets;

import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CBroadcastPacket;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.CFeePayload;

public class CBuyBetPacket extends CBroadcastPacket
{
   public CBuyBetPacket(String fee_adr,
                        String adr, 
                        String bet_uid, 
                        double amount)
    {
          super("ID_NEW_BUY_BET_PACKET");
	  
	  // Builds the payload class
	  CBuyBetPayload dec_payload=new CBuyBetPayload(adr, 
                                                        bet_uid, 
                                                        amount);  
                       
					
	  // Build the payload
	  this.payload=UTILS.SERIAL.serialize(dec_payload);
					
          // Network fee
	  fee=new CFeePayload(fee_adr);
			   
	   // Sign packet
	   this.sign();
	}
		
        // Check 
	public CResult check(CBlockPayload block)
	{
	    // Super class
	    CResult res=super.check(block);
	    if (res.passed==false) return res;
		   	
	    // Check type
	    if (this.tip!="ID_NEW_BUY_BET_PACKET") 
	       return new CResult(false, "Invalid packet type", "CBuyDomainPacket", 42);
		   
	    // Deserialize transaction data
	    CBuyBetPayload dec_payload=(CBuyBetPayload) UTILS.SERIAL.deserialize(payload);
		   
	    // Check payload
	    res=dec_payload.check(block);
            if (!res.passed) return res;
            
            // Check fee
            if (this.fee.amount<0.0001)
               return new CResult(false, "Invalid price", "CBuyDomainPacket", 42);
            
	    // Return 
	    return new CResult(true, "Ok", "CBuyDomainPacket", 74);
	}
		   
	public CResult commit(CBlockPayload block)
	{
	    // Superclass
            CResult res=super.commit(block);
	    if (res.passed==false) return res;
		   	  
	    // Deserialize transaction data
	    CBuyBetPayload ass_payload=(CBuyBetPayload) UTILS.SERIAL.deserialize(payload);

	    // Commit payload
	    res=ass_payload.commit(block);	  
	    if (res.passed==false) return res;
	    		  
	    // Return 
            return new CResult(true, "Ok", "CNewAssetPacket", 9);
      }
}
