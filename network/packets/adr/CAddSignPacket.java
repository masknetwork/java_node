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
    
    // Check 
    public CResult check(CBlockPayload block)
    {
    	// Super class
     	  CResult res=super.check(block);
     	  if (res.passed==false) return res;
     	
     	  // Check type
     	  if (!this.tip.equals("ID_MULTISIG_PACKET")) 
     		return new CResult(false, "Invalid packet type", "CAddSignPacket", 42);
     	  
     	  // Deserialize
          CAddSignPayload pay=(CAddSignPayload) UTILS.SERIAL.deserialize(this.payload);
		  
	  // Check fee
	  if (this.fee.amount<pay.days*0.0001)
	      return new CResult(false, "Invalid packet type", "CBlockAdrPacket", 44);
		  
          // Check sig
	  if (!this.checkSign())
	      return new CResult(false, "Invalid signature", "CBlockAdrPacket", 44);
		  
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
     	 
     	  // Return 
     	  return new CResult(true, "Ok", "CAddSignPacket", 45);
    }
    
    public CResult commit(CBlockPayload block)
    {
    	// Superclass
    	CResult res=super.commit(block);
    	if (res.passed==false) return res;
    	
    	// Deserialize transaction data
    	sign_payload=(CAddSignPayload) UTILS.SERIAL.deserialize(payload);
	
       
        res=this.sign_payload.commit(block);
	if (res.passed==false) return res;
		
	// Return 
   	return new CResult(true, "Ok", "CAddSignPacket", 65);
    }
}
