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
		          String mes, 
                          long retweet_tweet_ID,
		          String pic_1,
                          String pic_2,
                          String pic_3,
                          String pic_4,
                          String pic_5,
                          String video,
                          String packet_sign,
                          String payload_sign) throws Exception
   {
	   // Super class
	   super("ID_NEW_TWEET_PACKET");
	   
	   // Builds the payload class
	   CNewTweetPayload dec_payload=new CNewTweetPayload(adr, 
		                                             mes, 
                                                             retweet_tweet_ID,
		                                             pic_1,
                                                             pic_2,
                                                             pic_3,
                                                             pic_4,
                                                             pic_5,
                                                             video,
                                                             payload_sign);
			
	   // Build the payload
	   this.payload=UTILS.SERIAL.serialize(dec_payload);
			
	   // Network fee
	   fee=new CFeePayload(fee_adr,  0.0001);
	   
	   // Sign packet
           this.sign(packet_sign);
   }
   
   // Check 
   public CResult check(CBlockPayload block) throws Exception
   {
          // Super class
   	  CResult res=super.check(block);
   	  if (res.passed==false) throw new Exception(res.reason);
   	
   	  // Check type
   	  if (!this.tip.equals("ID_NEW_TWEET_PACKET")) 
   		throw new Exception("Invalid packet type - CNewTweetPacket.java");
   	  
   	  // Deserialize transaction data
   	  CNewTweetPayload dec_payload=(CNewTweetPayload) UTILS.SERIAL.deserialize(payload);
          
          // Check payload
          res=dec_payload.check(block);
          if (!res.passed) 
              throw new Exception(res.reason);
          
          
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
                
	    // Superclass
	    res=super.commit(block);
	    if (res.passed==false) return res;
            
            try
            {
                // Begin
                UTILS.DB.begin();
                
                if (res.passed)
                {
                   // Deserialize transaction data
                   CNewTweetPayload dec_payload=(CNewTweetPayload) UTILS.SERIAL.deserialize(payload);
                
	           // Commit
	           res=dec_payload.commit(block);
	           if (res.passed==false) throw new Exception(res.reason); 
                }
                else throw new Exception(res.reason); 
                    
                // Commit
                UTILS.DB.commit();
            }
            catch (Exception ex)
            {
                // Rollback
                UTILS.DB.rollback();
                
                // Exception
                throw new Exception(ex.getMessage());
            }
			  
	    // Return 
	    return new CResult(true, "Ok", "CNewTweetPacket.java", 9);
   }
}
