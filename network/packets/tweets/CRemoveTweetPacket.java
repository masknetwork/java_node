// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.tweets;

import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.ads.CNewAdPayload;
import wallet.network.packets.trans.*;
import wallet.network.packets.blocks.*;


public class CRemoveTweetPacket extends CBroadcastPacket 
{
   public CRemoveTweetPacket(String fee_adr,
                             String adr,
                             long tweetID,
                             String packet_sign,
                             String payload_sign) throws Exception
   {
	   // Super class
	   super("ID_REMOVE_TWEET_PACKET");
	   
	   // Builds the payload class
	   CRemoveTweetPayload dec_payload=new CRemoveTweetPayload(adr, tweetID, payload_sign);
			
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
   	  if (!this.tip.equals("ID_REMOVE_TWEET_PACKET")) 
   		return new CResult(false, "Invalid packet type", "CRemoveTweetPacket", 39);
   	  
   	  // Check sig
   	  if (this.checkSign()==false)
   		return new CResult(false, "Invalid signature", "CRemoveTweetPacket", 39);
          
          // Deserialize transaction data
   	  CRemoveTweetPayload dec_payload=(CRemoveTweetPayload) UTILS.SERIAL.deserialize(payload);
          
          // Check fee
	  if (this.fee.amount<0.0001)
	      return new CResult(false, "Invalid fee", "CRemoveTweetPacket", 44);
          
          // Check payload
          res=dec_payload.check(block);
          if (!res.passed) return new CResult(false, res.reason, "CRemoveTweetPacket", 44);
          
          // Footprint
          CFootprint foot=new CFootprint("ID_REMOVE_TWEET_PACKET", 
                                         this.hash, 
                                         dec_payload.hash, 
                                         this.fee.src, 
                                         this.fee.amount, 
                                         this.fee.hash,
                                         this.block);
                  
          foot.add("Address", dec_payload.target_adr);
          foot.add("TweetID", String.valueOf(dec_payload.tweetID));
          foot.write();
   	  
   	  // Return 
   	  return new CResult(true, "Ok", "CRemoveTweetPacket", 45);
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
                   CRemoveTweetPayload dec_payload=(CRemoveTweetPayload) UTILS.SERIAL.deserialize(payload);
                
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
	    return new CResult(true, "Ok", "CNewAdPacket.java", 9);
   }
}
