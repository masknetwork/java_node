package wallet.network.packets.adr;

import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.trans.*;
import wallet.network.packets.blocks.*;
        
public class CAddSignPacket extends CBroadcastPacket 
{		
	// Deserialized payload
	CAddSignPayload sign_payload;
		
    public CAddSignPacket(String fee_adr,
                          String target_adr, 
                          String signer_1, 
                          String signer_2, 
                          String signer_3,
                          String signer_4, 
                          String signer_5,
                          int min,
                          long days)
    {
	   // Constructor
	   super("ID_MULTISIG_PACKET");
           
           try
           {
	      // Builds the transaction
	      CAddSignPayload sign_payload=new CAddSignPayload(target_adr, 
                                                            signer_1, 
                                                            signer_2, 
                                                            signer_3,
                                                            signer_4, 
                                                            signer_5,
                                                            min,
                                                            days);
			
	      // Build the payload
	      this.payload=UTILS.SERIAL.serialize(sign_payload);
			
	      // Network fee
	      fee=new CFeePayload(fee_adr, days*0.0001);
	   
	      // Sign packet
	      this.sign();
           }
           catch (Exception ex) 
           { 
	       UTILS.LOG.log("Exception", ex.getMessage(), "CAddSignPacket.java", 50); 
           }
    }
    
    // Check 
    public CResult check(CBlockPayload block)
    {
    	  try
          {
             // Super class
     	     CResult res=super.check(block);
     	     if (res.passed==false) return res;
     	
     	     // Check type
     	     if (!this.tip.equals("ID_MULTISIG_PACKET")) 
     		 return new CResult(false, "Invalid packet type", "CAddSignPacket", 64);
     	  
     	     // Deserialize
             CAddSignPayload pay=(CAddSignPayload) UTILS.SERIAL.deserialize(this.payload);
		  
	     // Check fee
	     if (this.fee.amount<pay.days*0.0001)
	         return new CResult(false, "Invalid packet type", "CAddSignPacket", 72);
		  
             // Check sig
	     if (!this.checkSign())
	         return new CResult(false, "Invalid signature", "CAddSignPacket", 76);
		  
	     // Check payload
	     res=pay.check(block);
	     if (res.passed==false) return res;
                  
             // Footprint
             CFootprint foot=new CFootprint("ID_MULTISIG_PACKET", 
                                         this.hash, 
                                         pay.hash, 
                                         this.fee.src, 
                                         this.fee.amount, 
                                         this.fee.hash,
                                         this.block);
          
             foot.add("Address", pay.target_adr);
             foot.add("Signer 1", String.valueOf(pay.signer_1));
             foot.add("Signer 2", String.valueOf(pay.signer_2));
             foot.add("Signer 3", String.valueOf(pay.signer_3));
             foot.add("Signer 4", String.valueOf(pay.signer_4));
             foot.add("Signer 5", String.valueOf(pay.signer_5));
             foot.add("Minimum signers", String.valueOf(pay.min));
             foot.add("Days", String.valueOf(pay.days));
             foot.write();
          }
          catch (Exception ex) 
          { 
	       UTILS.LOG.log("Exception", ex.getMessage(), "CMesPacket.java", 103); 
          }
      
     	  // Return 
     	  return new CResult(true, "Ok", "CAddSignPacket", 107);
    }
    
    public CResult commit(CBlockPayload block)
    {
        try
        {
    	   // Superclass
    	   CResult res=super.commit(block);
    	   if (res.passed==false) return res;
    	
    	   // Deserialize transaction data
    	   sign_payload=(CAddSignPayload) UTILS.SERIAL.deserialize(payload);
	
           // Result
           res=this.sign_payload.commit(block);
	   if (res.passed==false) return res;
        }
        catch (Exception ex) 
        { 
	    UTILS.LOG.log("Exception", ex.getMessage(), "CAddSignPacket.java", 127); 
        }
        
	// Return 
   	return new CResult(true, "Ok", "CAddSignPacket", 131);
    }
}
