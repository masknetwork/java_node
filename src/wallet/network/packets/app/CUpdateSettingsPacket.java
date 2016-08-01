package wallet.network.packets.app;

import wallet.kernel.CPackets;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CBroadcastPacket;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.CFeePayload;

public class CUpdateSettingsPacket extends CBroadcastPacket
{
   public CUpdateSettingsPacket(String net_fee_adr, 
                                String adr, 
                                long appID, 
                                String settings,
                                String packet_sign,
                                String payload_sign) throws Exception
   {
      super("ID_UPDATE_SETTINGS_PACKET");
      
     // Builds the payload class 
     CUpdateSettingsPayload dec_payload=new CUpdateSettingsPayload(adr, 
                                                                   appID, 
                                                                   settings);
			
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
     if (!this.tip.equals("ID_UPDATE_SETTINGS_PACKET")) 
        throw new Exception("Invalid packet type - CUpdateSettingsPacket.java");
  	  
     // Check
     CUpdateSettingsPayload dec_payload=(CUpdateSettingsPayload) UTILS.SERIAL.deserialize(payload);
     
     // check
     dec_payload.check(block);
          
     // Footprint
     CPackets foot=new CPackets(this);
                  
     foot.add("AppID", String.valueOf(dec_payload.appID));
     foot.add("Settings", String.valueOf(dec_payload.settings));
     foot.write();
      
  }


}



