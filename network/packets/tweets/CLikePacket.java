package wallet.network.packets.tweets;

import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.trans.*;
import wallet.network.packets.blocks.*;


public class CLikePacket extends CBroadcastPacket 
{
   public CLikePacket(String fee_adr,
                      String adr, 
		      long tweetID)
   {
	   // Super class
	   super("ID_TWEET_LIKE");
	   
	   // Builds the payload class
	   CLikePayload dec_payload=new CLikePayload(adr, 
		                                     tweetID);
			
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
   	  if (!this.tip.equals("ID_TWEET_LIKE")) 
   		return new CResult(false, "Invalid packet type", "CLikePacket", 39);
   	  
   	  // Check sig
   	  if (this.checkSign()==false)
   		return new CResult(false, "Invalid signature", "CLikePacket", 39);
          
          // Deserialize transaction data
   	  CLikePayload dec_payload=(CLikePayload) UTILS.SERIAL.deserialize(payload);
          
          // Check fee
	  if (this.fee.amount<0.0001)
	      return new CResult(false, "Invalid fee", "CLikePacket", 44);
          
          // Check payload
          res=dec_payload.check(block);
          if (!res.passed) return new CResult(false, res.reason, "CLikePacket", 44);
          
          // Footprint
          CFootprint foot=new CFootprint("ID_TWEET_LIKE", 
                                         this.hash, 
                                         dec_payload.hash, 
                                         this.fee.src, 
                                         this.fee.amount, 
                                         this.fee.hash,
                                         this.block);
                  
          foot.add("Address", dec_payload.target_adr);
          foot.add("Tweet ID", String.valueOf(dec_payload.tweetID));
          foot.write();
   	  
   	  // Return 
   	  return new CResult(true, "Ok", "CLikePacket", 45);
   }
   
   public CResult commit(CBlockPayload block)
   {
   	  // Superclass
   	  CResult res=super.commit(block);
   	  if (res.passed==false) return res;
   	  
   	  // Deserialize transaction data
   	  CLikePayload dec_payload=(CLikePayload) UTILS.SERIAL.deserialize(payload);

	  // Fee is 0.0001 / day ?
	  res=dec_payload.commit(block);
          if (res.passed==false) return res;
	  
	  // Return 
   	  return new CResult(true, "Ok", "CLikePacket", 62);
   }
}
