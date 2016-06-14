// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.mes;

import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.trans.*;
import wallet.network.packets.blocks.*;


public class CMesPacket extends CBroadcastPacket 
{
  public CMesPacket(String fee_adr, 
		    String sender_adr, 
		    String receiver_adr, 
		    String subject, 
		    String mes,
                    String packet_sign,
                    String payload_sign) throws Exception
  {
      super("ID_SEND_MES");
      
      try
      {
	  // Builds the payload class
	  CMesPayload dec_payload=new CMesPayload(sender_adr, 
	                                          receiver_adr, 
	                                          subject, 
	                                          mes);
			
	   // Build the payload
	   this.payload=UTILS.SERIAL.serialize(dec_payload);
			
	   // Network fee
	   fee=new CFeePayload(fee_adr);
	   
	   // Sign packet
	   this.sign();
      }
      catch (Exception ex) 
      { 
	       UTILS.LOG.log("Exception", ex.getMessage(), "CMesPacket.java", 67); 
      }
  }
  
  // Check 
  public CResult check(CBlockPayload block) throws Exception
  {
      try
      {
          // Super class
  	  CResult res=super.check(block);
  	  if (res.passed==false) return res;
  	
  	  // Check type
  	  if (!this.tip.equals("ID_SEND_MES")) 
  		return new CResult(false, "Invalid packet type", "CMesPacketPacket", 39);
  	  
  	   // Check
  	  CMesPayload dec_payload=(CMesPayload) UTILS.SERIAL.deserialize(payload);
          res=dec_payload.check(block);
          if (!res.passed) return res;
          
          // Footprint
          CFootprint foot=new CFootprint("ID_SEND_MES", 
                                         this.hash, 
                                         dec_payload.hash, 
                                         this.fee.src, 
                                         this.fee.amount, 
                                         this.fee.hash,
                                         this.block);
                  
          foot.add("Source", dec_payload.target_adr);
          foot.add("Destination", dec_payload.receiver_adr);
          foot.write();
      }
      catch (Exception ex) 
      { 
	  UTILS.LOG.log("Exception", ex.getMessage(), "CMesPacket.java", 67); 
          return new CResult(false, "Exception", "CMesPacketPacket", 77);
      }
      
      // Return 
      return new CResult(true, "Ok", "CMesPacketPacket", 45);
  }
  
  public CResult commit(CBlockPayload block) throws Exception
  {
      try
      {
  	  // Superclass
  	  CResult res=super.commit(block);
  	  if (res.passed==false) return res;
  	  
  	  // Deserialize transaction data
  	  CMesPayload dec_payload=(CMesPayload) UTILS.SERIAL.deserialize(payload);

	  // Fee is 0.0001 / day ?
	 res=dec_payload.commit(block);
         if (res.passed==false) return res;
      }
      catch (Exception ex) 
      { 
	  UTILS.LOG.log("Exception", ex.getMessage(), "CMesPacket.java", 67); 
          return new CResult(false, "Exception", "CMesPacketPacket", 77);
      }
      
       // Return 
       return new CResult(true, "Ok", "CMesPacketPacket", 62);
  }
}
