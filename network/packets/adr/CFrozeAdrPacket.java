package wallet.network.packets.adr;

import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.trans.*;
import wallet.network.packets.blocks.*;

public class CFrozeAdrPacket extends CBroadcastPacket 
{
    public CFrozeAdrPacket(String fee_adr, String block_adr, int days)
    {
       // Constructor
       super("ID_FROZE_ADR_PACKET");
       
       try
       {	   
          // Builds the transaction
          CFrozeAdrPayload block_payload=new CFrozeAdrPayload(block_adr, days);
				
           // Build the payload
           this.payload=UTILS.SERIAL.serialize(block_payload);
				
           // Network fee
           fee=new CFeePayload(fee_adr, days*0.0001);
		   
           // Sign
	   this.sign();
        }
        catch (Exception ex) 
        { 
	    UTILS.LOG.log("Exception", ex.getMessage(), "CFrozeAdrPacket.java", 32); 
        }
    }
	   
    public CResult check(CBlockPayload block)
    {
        try
        {
	   // Super class
	   CResult res=super.check(block);
	   if (res.passed==false) return res;
		   	
	   // Check type
	   if (!this.tip.equals("ID_FROZE_ADR_PACKET")) 
	       return new CResult(false, "Invalid packet type", "CFrozeAdrPacket", 46);
		  
	   // Deserialize
	   CFrozeAdrPayload pay=(CFrozeAdrPayload) UTILS.SERIAL.deserialize(this.payload);
		  
	   // Check fee
	   if (this.fee.amount<pay.days*0.0001)
	      return new CResult(false, "Invalid packet type", "CFrozeAdrPacket", 53);
		  
	   // Check sig
	   if (!this.checkSign())
	      return new CResult(false, "Invalid signature", "CFrozeAdrPacket", 57);
		  
	   // Check payload
	   CFrozeAdrPayload payload=(CFrozeAdrPayload) UTILS.SERIAL.deserialize(this.payload);
	   res=payload.check(block);
	   if (res.passed==false) return res;
                  
           // Footprint
           CFootprint foot=new CFootprint("ID_FROZE_ADR_PACKET", 
                                          this.hash, 
                                          pay.hash, 
                                          this.fee.src, 
                                          this.fee.amount, 
                                          this.fee.hash,
                                          this.block);
           foot.add("Address", payload.adr);
           foot.add("Days", String.valueOf(payload.days));
           foot.write(); 
        }
        catch (Exception ex) 
        { 
	    UTILS.LOG.log("Exception", ex.getMessage(), "CFrozeAdrPacket.java", 78); 
        }
        
	// Return 
	return new CResult(true, "Ok", "CFrozeAdrPacket", 82);
    }
	  
    public CResult commit(CBlockPayload block) 
    {
        try
        {
           // Superclass
	   CResult res=super.commit(block);
	   if (res.passed==false) return res;
	   	   
	   // Deserialize
	   CFrozeAdrPayload pay=(CFrozeAdrPayload) UTILS.SERIAL.deserialize(this.payload);
	 	  
	   // Commit the payload
	   res=pay.commit(block);
	   if (res.passed==false) return res;
	}
        catch (Exception ex) 
        { 
	    UTILS.LOG.log("Exception", ex.getMessage(), "CFrozeAdrPacket.java", 102); 
        }
        
	// Return 
	return new CResult(true, "Ok", "CFrozeAdrPacket", 106);
    }
}
