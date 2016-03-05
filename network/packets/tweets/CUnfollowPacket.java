// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.tweets;

import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.trans.*;
import wallet.network.packets.blocks.*;


public class CUnfollowPacket extends CBroadcastPacket 
{
   public CUnfollowPacket(String fee_adr,
                          String adr,
                          String unfollow_address) throws Exception
   {
	   // Super class
	   super("ID_UNFOLLOW_PACKET");
	   
	   // Builds the payload class
	   CUnfollowPayload dec_payload=new CUnfollowPayload(adr, unfollow_address);
			
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
   	  if (!this.tip.equals("ID_UNFOLLOW_PACKET")) 
   		return new CResult(false, "Invalid packet type", "CFollowPayload", 39);
   	  
   	  // Check sig
   	  if (this.checkSign()==false)
   		return new CResult(false, "Invalid signature", "CFollowPayload", 39);
          
          // Deserialize transaction data
   	  CUnfollowPayload dec_payload=(CUnfollowPayload) UTILS.SERIAL.deserialize(payload);
          
          // Check fee
	  if (this.fee.amount<0.0001)
	      return new CResult(false, "Invalid fee", "CFollowPayload", 44);
          
          // Check payload
          res=dec_payload.check(block);
          if (!res.passed) return new CResult(false, res.reason, "CTweetMesPacket", 44);
          
          // Footprint
          CFootprint foot=new CFootprint("ID_UNFOLLOW_PACKET", 
                                         this.hash, 
                                         dec_payload.hash, 
                                         this.fee.src, 
                                         this.fee.amount, 
                                         this.fee.hash,
                                         this.block);
                  
          foot.add("Address", dec_payload.target_adr);
          foot.add("Unfollow Address", dec_payload.unfollow_adr);
          foot.write();
   	  
   	  // Return 
   	  return new CResult(true, "Ok", "CFollowPayload", 45);
   }
   
   public CResult commit(CBlockPayload block) throws Exception
   {
   	  // Superclass
   	  CResult res=super.commit(block);
   	  if (res.passed==false) return res;
   	  
   	  // Deserialize transaction data
   	  CUnfollowPayload dec_payload=(CUnfollowPayload) UTILS.SERIAL.deserialize(payload);

	  // Fee is 0.0001 / day ?
	  res=dec_payload.commit(block);
          if (res.passed==false) return res;
	  
	  // Return 
   	  return new CResult(true, "Ok", "CFollowPayload", 62);
   }
}
