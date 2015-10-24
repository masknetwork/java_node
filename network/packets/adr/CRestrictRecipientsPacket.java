package wallet.network.packets.adr;

import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.trans.*;
import wallet.network.packets.blocks.*;

public class CRestrictRecipientsPacket extends CBroadcastPacket 
{
   public CRestrictRecipientsPacket(String fee_adr, 
		                            String target_adr, 
		                            String adr_1, 
		                            String adr_2, 
		                            String adr_3, 
		                            String adr_4,
                                            String adr_5,
		                            int days)
   {
	   // Super class
	   super("ID_RESTRICT_REC_PACKET");
	   
	   // Builds the payload class
	   CRestrictRecipientsPayload dec_payload=new CRestrictRecipientsPayload(target_adr, 
                                                                                 adr_1,  
                                                                                 adr_2, 
                                                                                 adr_3, 
                                                                                 adr_4, 
                                                                                 adr_5, 
                                                                                 days);
			
	   // Build the payload
	   this.payload=UTILS.SERIAL.serialize(dec_payload);
			
	   // Network fee
	   fee=new CFeePayload(fee_adr, 0.0001*days);
	   
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
      if (!this.tip.equals("ID_RESTRICT_REC_PACKET")) 
            return new CResult(false, "Invalid packet type", "CRestrictRecipientsPacket", 39);
          
      // Deserialize
      CRestrictRecipientsPayload pay=(CRestrictRecipientsPayload) UTILS.SERIAL.deserialize(this.payload);
		  
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
      CFootprint foot=new CFootprint("ID_RESTRICT_REC_PACKET", 
                                     this.hash, 
                                     pay.hash, 
                                     this.fee.src, 
                                     this.fee.amount, 
                                     this.fee.hash,
                                     this.block);
      
      foot.add("Address", pay.target_adr);
      foot.add("Recipient 1", pay.adr_1);
      foot.add("Recipient 2", pay.adr_2);
      foot.add("Recipient 3", pay.adr_3);
      foot.add("Recipient 4", pay.adr_4);
      foot.add("Recipient 5", pay.adr_5);
      foot.add("Days", String.valueOf(pay.days));
      foot.write();
   	
      // Return 
      return new CResult(true, "Ok", "CRemoveEscrowPacket", 45);
   }
   
   public CResult commit(CBlockPayload block)
   {
   	  // Superclass
   	  CResult res=super.commit(block);
   	  if (res.passed==false) return res;
   	  
   	  // Deserialize transaction data
     CRestrictRecipientsPayload dec_payload=(CRestrictRecipientsPayload) UTILS.SERIAL.deserialize(payload);

	  // Fee is 0.0001 / day ?
	  if (this.fee.amount>=(0.0001*dec_payload.days)) 
	  {
		  res=dec_payload.commit(block);
		  if (res.passed==false) return res;
	  }
	  
	  // Return 
   	  return new CResult(true, "Ok", "CRestrictRecipientsPacket", 62);
   }
}
