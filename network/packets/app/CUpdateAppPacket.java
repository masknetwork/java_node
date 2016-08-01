package wallet.network.packets.app;

import wallet.kernel.CPackets;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CBroadcastPacket;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.CFeePayload;

public class CUpdateAppPacket extends CBroadcastPacket
{
  public CUpdateAppPacket(String net_fee_adr, 
                          String adr, 
                          long appID, 
                          String op, 
                          long days) throws Exception
  {
      super("ID_UPDATE_APP_PACKET");
      
     // Builds the payload class 
     CUpdateAppPayload dec_payload=new CUpdateAppPayload(adr, 
                                                         appID, 
                                                         op, 
                                                         days);
			
     // Build the payload
     this.payload=UTILS.SERIAL.serialize(dec_payload);
			
     // Network fee
     fee=new CFeePayload(net_fee_adr);
	   
     // Sign packet
     this.sign();
  }
  
  // Check 
  public void check(CBlockPayload block) throws Exception
  {
     // Super class
     super.check(block);
     	
     // Check type
     if (!this.tip.equals("ID_UPDATE_APP_PACKET")) 
        throw new Exception("Invalid packet type - CRentAppPacket.java");
  	  
     // Check
     CUpdateAppPayload dec_payload=(CUpdateAppPayload) UTILS.SERIAL.deserialize(payload);
     
     // check
     dec_payload.check(block);
          
     // Footprint
     CPackets foot=new CPackets(this);
                  
     foot.add("AppID", String.valueOf(dec_payload.appID));
     foot.add("Operation", String.valueOf(dec_payload.op));
     foot.add("Days", String.valueOf(dec_payload.days));
     foot.write();
      
  }


}
