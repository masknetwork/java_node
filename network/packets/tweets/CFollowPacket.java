package wallet.network.packets.tweets;

import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.trans.*;
import wallet.network.packets.blocks.*;


public class CFollowPacket extends CBroadcastPacket 
{
   public CFollowPacket(String fee_adr,
                        String adr, 
		        String follow_adr)
   {
	   // Super class
	   super("ID_FOLLOW_PACKET");
	   
	   // Builds the payload class
	   CFollowPayload dec_payload=new CFollowPayload(adr, 
		                                         follow_adr);
			
	   // Build the payload
	   this.payload=UTILS.SERIAL.serialize(dec_payload);
			
	   // Network fee
	   fee=new CFeePayload(fee_adr,  0.0001);
	   
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
   	  if (!this.tip.equals("ID_FOLLOW_PACKET")) 
   		return new CResult(false, "Invalid packet type", "CFollowPayload", 39);
   	  
   	  // Check sig
   	  if (this.checkSign()==false)
   		return new CResult(false, "Invalid signature", "CFollowPayload", 39);
          
          // Deserialize transaction data
   	  CFollowPayload dec_payload=(CFollowPayload) UTILS.SERIAL.deserialize(payload);
          
          // Check fee
	  if (this.fee.amount<0.0001)
	      return new CResult(false, "Invalid fee", "CFollowPayload", 44);
          
          // Check payload
          res=dec_payload.check(block);
          if (!res.passed) return new CResult(false, res.reason, "CTweetMesPacket", 44);
          
          // Footprint
          CFootprint foot=new CFootprint("ID_FOLLOW_PACKET", 
                                         this.hash, 
                                         dec_payload.hash, 
                                         this.fee.src, 
                                         this.fee.amount, 
                                         this.fee.hash,
                                         this.block);
                  
          foot.add("Address", dec_payload.target_adr);
          foot.add("Follows", dec_payload.follow_adr);
          foot.write();
   	  
   	  // Return 
   	  return new CResult(true, "Ok", "CFollowPayload", 45);
   }
   
   public CResult commit(CBlockPayload block)
   {
   	  // Superclass
   	  CResult res=super.commit(block);
   	  if (res.passed==false) return res;
   	  
   	  // Deserialize transaction data
   	  CFollowPayload dec_payload=(CFollowPayload) UTILS.SERIAL.deserialize(payload);

	  // Fee is 0.0001 / day ?
	  res=dec_payload.commit(block);
          if (res.passed==false) return res;
	  
	  // Return 
   	  return new CResult(true, "Ok", "CFollowPayload", 62);
   }
}
