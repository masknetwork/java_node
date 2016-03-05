// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.tweets;

import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.trans.*;
import wallet.network.packets.blocks.*;


public class CResponseRewardPacket extends CBroadcastPacket 
{
   public CResponseRewardPacket(String fee_adr, 
                                String adr, 
		                long resID,
                                double amount) throws Exception
   {
	   // Super class
	   super("ID_RESPONSE_REWARD_PACKET");
	   
	   // Builds the payload class
	   CResponseRewardPayload dec_payload=new CResponseRewardPayload(adr, 
		                                                         resID,
                                                                         amount);
			
	   // Build the payload
	   this.payload=UTILS.SERIAL.serialize(dec_payload);
			
	   // Network fee
	   fee=new CFeePayload(fee_adr,  0.0001);
	   
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
   	  if (!this.tip.equals("ID_RESPONSE_REWARD_PACKET")) 
   		return new CResult(false, "Invalid packet type", "CTweetMesPacket", 39);
   	  
   	  // Check sig
   	  if (this.checkSign()==false)
   		return new CResult(false, "Invalid signature", "CTweetMesPacket", 39);
          
          // Deserialize transaction data
   	  CResponseRewardPayload dec_payload=(CResponseRewardPayload) UTILS.SERIAL.deserialize(payload);
          
          // Check fee
	  if (this.fee.amount<0.0001)
	      return new CResult(false, "Invalid fee", "CTweetMesPacket", 44);
          
          // Check payload
          res=dec_payload.check(block);
          if (!res.passed) return new CResult(false, res.reason, "CTweetMesPacket", 44);
          
          // Footprint
          CFootprint foot=new CFootprint("ID_RESPONSE_REWARD_PACKET", 
                                         this.hash, 
                                         dec_payload.hash, 
                                         this.fee.src, 
                                         this.fee.amount, 
                                         this.fee.hash,
                                         this.block);
                  
          foot.add("Address", dec_payload.target_adr);
          foot.add("Message ID", String.valueOf(dec_payload.resID));
          foot.add("Amount", String.valueOf(dec_payload.tip));
          foot.write();
   	  
   	  // Return 
   	  return new CResult(true, "Ok", "CTweetMesPacket", 45);
   }
   
   public CResult commit(CBlockPayload block) throws Exception
   {
   	  // Superclass
   	  CResult res=super.commit(block);
   	  if (res.passed==false) return res;
   	  
   	  // Deserialize transaction data
   	  CResponseRewardPayload dec_payload=(CResponseRewardPayload) UTILS.SERIAL.deserialize(payload);

	  // Fee is 0.0001 / day ?
	  res=dec_payload.commit(block);
          if (res.passed==false) return res;
	  
	  // Return 
   	  return new CResult(true, "Ok", "CTweetMesPacket", 62);
   }
}
