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
   // Serial
   private static final long serialVersionUID = 1L;
   
   public CNewTweetPacket(String fee_adr, 
		          String adr, 
		          String title,
                          String mes, 
                          long retweet_tweet_ID,
		          String pic,
                          long days) throws Exception
   {
	   // Super class
	   super("ID_NEW_TWEET_PACKET");
	   
	   // Builds the payload class
	   CNewTweetPayload dec_payload=new CNewTweetPayload(adr, 
                                                             title,
		                                             mes, 
                                                             pic,
                                                             retweet_tweet_ID,
                                                             days);
			
	   // Build the payload
	   this.payload=UTILS.SERIAL.serialize(dec_payload);
			
	   // Network fee
           CFeePayload fee=new CFeePayload(fee_adr,  0.0001*days);
	   this.fee_payload=UTILS.SERIAL.serialize(fee);
           
	   // Sign packet
           this.sign();
   }
   
   // Check 
   public void check(CBlockPayload block) throws Exception
   {
          // Super class
   	  super.check(block);
   	  
   	  // Check type
   	  if (!this.tip.equals("ID_NEW_TWEET_PACKET")) 
   		throw new Exception("Invalid packet type - CNewTweetPacket.java");
   	  
   	  // Deserialize transaction data
   	  CNewTweetPayload dec_payload=(CNewTweetPayload) UTILS.SERIAL.deserialize(payload);
          
          // Check payload
          dec_payload.check(block);
          
          // Deserialize payload
          CFeePayload fee=(CFeePayload) UTILS.SERIAL.deserialize(fee_payload);
          
          // Check fee
	  if (fee.amount<dec_payload.days*0.0001)
	     throw new Exception("Invalid fee - CNewTweetPacket.java");
 
          // Footprint
          CPackets foot=new CPackets(this);
          foot.add("Address", dec_payload.target_adr);
          foot.add("Title", String.valueOf(dec_payload.title));
          foot.add("Message", String.valueOf(dec_payload.mes));
          foot.add("Pic", String.valueOf(dec_payload.pic));
          foot.add("Retweet Tweet ID", String.valueOf(dec_payload.retweet_tweet_ID));
          foot.write();
   	
   }
   
  
}
