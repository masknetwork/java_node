package wallet.network.packets.tweets;

import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.trans.*;
import wallet.network.packets.blocks.*;


public class CTweetMesPacket extends CBroadcastPacket 
{
   public CTweetMesPacket(String fee_adr,
                          String adr, 
		          long tweetID,
                          long comID,
		          String mes)
   {
	   // Super class
	   super("ID_TWEET_COMMENT_PACKET");
	   
	   // Builds the payload class
	   CTweetMesPayload dec_payload=new CTweetMesPayload(adr, 
		                                             tweetID, 
                                                             comID,
		                                             mes);
			
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
   	  if (!this.tip.equals("ID_TWEET_COMMENT_PACKET")) 
   		return new CResult(false, "Invalid packet type", "CTweetMesPacket", 39);
   	  
   	  // Check sig
   	  if (this.checkSign()==false)
   		return new CResult(false, "Invalid signature", "CTweetMesPacket", 39);
          
          // Deserialize transaction data
   	  CTweetMesPayload dec_payload=(CTweetMesPayload) UTILS.SERIAL.deserialize(payload);
          
          // Check fee
	  if (this.fee.amount<0.0001)
	      return new CResult(false, "Invalid fee", "CTweetMesPacket", 44);
          
          // Check payload
          res=dec_payload.check(block);
          if (!res.passed) return new CResult(false, res.reason, "CTweetMesPacket", 44);
          
          // Footprint
          CFootprint foot=new CFootprint("ID_TWEET_COMMENT_PACKET", 
                                         this.hash, 
                                         dec_payload.hash, 
                                         this.fee.src, 
                                         this.fee.amount, 
                                         this.fee.hash,
                                         this.block);
                  
          foot.add("Address", dec_payload.target_adr);
          foot.add("Tweet ID", String.valueOf(dec_payload.tweetID));
          foot.add("Mes", dec_payload.mes);
          foot.write();
   	  
   	  // Return 
   	  return new CResult(true, "Ok", "CTweetMesPacket", 45);
   }
   
   public CResult commit(CBlockPayload block)
   {
   	  // Superclass
   	  CResult res=super.commit(block);
   	  if (res.passed==false) return res;
   	  
   	  // Deserialize transaction data
   	  CTweetMesPayload dec_payload=(CTweetMesPayload) UTILS.SERIAL.deserialize(payload);

	  // Fee is 0.0001 / day ?
	  res=dec_payload.commit(block);
          if (res.passed==false) return res;
	  
	  // Return 
   	  return new CResult(true, "Ok", "CTweetMesPacket", 62);
   }
}
