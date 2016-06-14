// Author : Vlad Cristian
// Contact : vcris@gmx.com

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
		        String follow_adr,
                        String packet_sign,
                        String payload_sign) throws Exception
   {
	   // Super class
	   super("ID_FOLLOW_PACKET");
	   
	   // Builds the payload class
	   CFollowPayload dec_payload=new CFollowPayload(adr, 
		                                         follow_adr,
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
   	  if (!this.tip.equals("ID_FOLLOW_PACKET")) 
             throw new Exception("Invalid packet type - CFollowPacket.java");
   	  
   	  // Deserialize transaction data
   	  CFollowPayload dec_payload=(CFollowPayload) UTILS.SERIAL.deserialize(payload);
          
          // Check fee
	  if (this.fee.amount<0.0001)
	      throw new Exception("Invalid fee - CFollowPacket.java");
          
          // Check payload
          res=dec_payload.check(block);
          if (!res.passed) throw new Exception(res.reason);
          
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
                CFollowPayload dec_payload=(CFollowPayload) UTILS.SERIAL.deserialize(payload);
                
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
