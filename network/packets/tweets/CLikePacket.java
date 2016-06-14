// Author : Vlad Cristian
// Contact : vcris@gmx.com

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
		      long tweetID,
                      String packet_sign,
                      String payload_sign) throws Exception
   {
	   // Super class
	   super("ID_TWEET_LIKE");
	   
	   // Builds the payload class
	   CLikePayload dec_payload=new CLikePayload(adr, 
		                                     tweetID,
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
                CLikePayload dec_payload=(CLikePayload) UTILS.SERIAL.deserialize(payload);
                
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
