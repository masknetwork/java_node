// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.tweets;

import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.trans.*;
import wallet.network.packets.blocks.*;


public class CNewTweetPacket extends CBroadcastPacket 
{
   public CNewTweetPacket(String fee_adr, 
		          String adr, 
		          String target_adr, 
		          String mes, 
                          long retweet_tweet_ID,
		          String pic_1,
                          String pic_2,
                          String pic_3,
                          String pic_4,
                          String pic_5,
                          String video, 
                          double budget, 
                          String budget_cur, 
                          long budget_expire) throws Exception
   {
	   // Super class
	   super("ID_NEW_TWEET_PACKET");
	   
	   // Builds the payload class
	   CNewTweetPayload dec_payload=new CNewTweetPayload(adr, 
		                                             target_adr, 
		                                             mes, 
                                                             retweet_tweet_ID,
		                                             pic_1,
                                                             pic_2,
                                                             pic_3,
                                                             pic_4,
                                                             pic_5,
                                                             video,
                                                             budget,
                                                             budget_cur,
                                                             budget_expire);
			
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
   	  if (!this.tip.equals("ID_NEW_TWEET_PACKET")) 
   		return new CResult(false, "Invalid packet type", "CNewAdPacket", 39);
   	  
   	  // Check sig
   	  if (this.checkSign()==false)
   		return new CResult(false, "Invalid signature", "CNewAdPacket", 39);
          
          // Deserialize transaction data
   	  CNewTweetPayload dec_payload=(CNewTweetPayload) UTILS.SERIAL.deserialize(payload);
          
          // Check payload
          res=dec_payload.check(block);
          if (!res.passed) return new CResult(false, res.reason, "CNewAdPacket", 39);
          
          
          // Check fee
	  if (this.fee.amount<0.0001)
	      return new CResult(false, "Invalid fee", "CBlockAdrPacket", 44);
          
          
          // Footprint
          CFootprint foot=new CFootprint("ID_NEW_TWEET_PACKET", 
                                         this.hash, 
                                         dec_payload.hash, 
                                         this.fee.src, 
                                         this.fee.amount, 
                                         this.fee.hash,
                                         this.block);
                  
          foot.add("Address", dec_payload.target_adr);
          foot.add("Target Wall", dec_payload.t_adr);
          foot.add("Message", String.valueOf(dec_payload.mes));
          foot.add("Pic 1", String.valueOf(dec_payload.pic_1));
          foot.add("Pic 2", dec_payload.pic_2);
          foot.add("Pic 3", dec_payload.pic_3);
          foot.add("Pic 4", dec_payload.pic_4);
          foot.add("Pic 5", dec_payload.pic_5);
          foot.add("Video", dec_payload.video);
          foot.add("Retweet Tweet ID", String.valueOf(dec_payload.retweet_tweet_ID));
          foot.write();
   	  
   	  // Return 
   	  return new CResult(true, "Ok", "CNewAdPacket", 45);
   }
   
   public CResult commit(CBlockPayload block) throws Exception
   {
          // Check
          CResult res=this.check(block);
   	  if (res.passed==false) return res;
   	  
   	  // Superclass
   	  res=super.commit(block);
   	  if (res.passed==false) return res;
   	  
   	  // Deserialize transaction data
   	  CNewTweetPayload dec_payload=(CNewTweetPayload) UTILS.SERIAL.deserialize(payload);

	  // Fee is 0.0001 / day ?
	  res=dec_payload.commit(block);
          if (res.passed==false) return res;
	  
	  // Return 
   	  return new CResult(true, "Ok", "CNewAdPacket", 62);
   }
}
