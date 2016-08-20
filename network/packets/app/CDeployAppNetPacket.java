package wallet.network.packets.app;

import wallet.kernel.CPackets;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CBroadcastPacket;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.mes.CMesPayload;
import wallet.network.packets.trans.CFeePayload;

public class CDeployAppNetPacket extends CBroadcastPacket
{
     public CDeployAppNetPacket(String fee_adr, 
                                String adr, 
                                String name,
                                String globals,
                                String iface,
                                String signals,
                                String code,
                                long run_period, 
                                long days) throws Exception
  {
      super("ID_DEPLOY_APP_NET_PACKET");
      
     // Builds the payload class 
     CDeployAppNetPayload dec_payload=new CDeployAppNetPayload(adr, 
                                                               name,
                                                               globals,
                                                               iface,
                                                               signals,
                                                               code,
                                                               run_period, 
                                                               days);
			
     // Build the payload
     this.payload=UTILS.SERIAL.serialize(dec_payload);
			
     // Network fee
     fee=new CFeePayload(fee_adr, days*0.0001);
	   
     // Sign packet
     this.sign();
  }
  
  // Check 
  public void check(CBlockPayload block) throws Exception
  {
     // Super class
     super.check(block);
     	
     // Check type
     if (!this.tip.equals("ID_DEPLOY_APP_NET_PACKET")) 
        throw new Exception("Invalid packet type - CDeployAppNetPacket.java");
  	  
     // Deserialize
     CDeployAppNetPayload dec_payload=(CDeployAppNetPayload) UTILS.SERIAL.deserialize(payload);
     
     // Check
     dec_payload.check(block);
          
     // Footprint
     CPackets foot=new CPackets(this);
                  
     foot.add("Run Period", String.valueOf(dec_payload.run_period));
     foot.add("Signals", dec_payload.signals);
     foot.add("Interface", dec_payload.iface);
     foot.add("Globals", dec_payload.globals);
     foot.add("Code", dec_payload.code);
     foot.write();
    
  }
  
 

}


