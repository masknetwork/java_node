package wallet.network.packets.app;

import wallet.kernel.CPackets;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CBroadcastPacket;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.CFeePayload;

public class CRentAppPacket extends CBroadcastPacket
{
   public CRentAppPacket(String net_fee_adr, 
                         String adr, 
                         long appID, 
                         long days) throws Exception
  {
      super("ID_RENT_APP_PACKET");
      
     // Builds the payload class 
     CRentAppPayload dec_payload=new CRentAppPayload(adr, 
                                                   appID,
                                                   days);
			
     // Build the payload
     this.payload=UTILS.SERIAL.serialize(dec_payload);
			
     // Network fee
     fee=new CFeePayload(net_fee_adr, days*0.0001);
	   
     // Sign packet
     this.sign();
  }
  
  // Check 
  public void check(CBlockPayload block) throws Exception
  {
     // Super class
     super.check(block);
     	
     // Check type
     if (!this.tip.equals("ID_RENT_APP_PACKET")) 
        throw new Exception("Invalid packet type - CRentAppPacket.java");
  	  
     // Check
     CRentAppPayload dec_payload=(CRentAppPayload) UTILS.SERIAL.deserialize(payload);
     dec_payload.check(block);
          
     // Footprint
     CPackets foot=new CPackets(this);
                  
     foot.add("AppID", String.valueOf(dec_payload.appID));
     foot.add("Days", String.valueOf(dec_payload.days));
     foot.write();
     
  }
  
  
}
