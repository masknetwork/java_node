// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.tweets;

import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.trans.*;
import wallet.network.packets.blocks.*;


public class CTweetMesStatusPacket extends CBroadcastPacket 
{
   public CTweetMesStatusPacket(String fee_adr,
                                String adr, 
		                long mesID, 
		                String new_status,
                                String packet_sign,
                                String payload_sign) throws Exception
   {
	   // Super class
	   super("ID_TWEET_MES_STATUS_PACKET");
	   
	   // Builds the payload class
	   CTweetMesStatusPayload dec_payload=new CTweetMesStatusPayload(adr, 
		                                                        mesID, 
		                                                        new_status,
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
   	  if (!this.tip.equals("ID_TWEET_MES_STATUS_PACKET")) 
   		return new CResult(false, "Invalid packet type", "C", 39);
   	  
   	  // Check sig
   	  if (this.checkSign()==false)
   		return new CResult(false, "Invalid signature", "CTweetMesStatusPacket", 39);
          
          // Deserialize transaction data
   	  CTweetMesStatusPayload dec_payload=(CTweetMesStatusPayload) UTILS.SERIAL.deserialize(payload);
          
          // Check fee
	  if (this.fee.amount<0.0001)
	      return new CResult(false, "Invalid fee", "CTweetMesStatusPacket", 44);
          
          // Check payload
          res=dec_payload.check(block);
          if (!res.passed) return new CResult(false, res.reason, "CTweetMesStatusPacket", 44);
          
          // Footprint
          CFootprint foot=new CFootprint("ID_TWEET_MES_STATUS_PACKET", 
                                         this.hash, 
                                         dec_payload.hash, 
                                         this.fee.src, 
                                         this.fee.amount, 
                                         this.fee.hash,
                                         this.block);
                  
          foot.add("Address", dec_payload.target_adr);
          foot.add("Message ID", String.valueOf(dec_payload.mesID));
          foot.add("New Status", dec_payload.new_status);
          foot.write();
   	  
   	  // Return 
   	  return new CResult(true, "Ok", "CTweetMesStatusPacket", 45);
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
                CTweetMesStatusPayload dec_payload=(CTweetMesStatusPayload) UTILS.SERIAL.deserialize(payload);
                
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
