package wallet.network.packets.app;

import wallet.kernel.CFootprint;
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
                                long appID, 
                                long run_period, 
                                long days,
                                String packet_sign,
                                String payload_sign) throws Exception
  {
      super("ID_DEPLOY_APP_NET_PACKET");
      
     // Builds the payload class 
     CDeployAppNetPayload dec_payload=new CDeployAppNetPayload(adr, 
                                                               appID, 
                                                               run_period, 
                                                               days,
                                                               payload_sign);
			
     // Build the payload
     this.payload=UTILS.SERIAL.serialize(dec_payload);
			
     // Network fee
     fee=new CFeePayload(fee_adr, days*0.0001);
	   
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
     if (!this.tip.equals("ID_DEPLOY_APP_NET_PACKET")) 
        return new CResult(false, "Invalid packet type", "CDeployAppNetPayload", 39);
  	  
     // Check
     CDeployAppNetPayload dec_payload=(CDeployAppNetPayload) UTILS.SERIAL.deserialize(payload);
     res=dec_payload.check(block);
     if (!res.passed) return res;
          
     // Footprint
     CFootprint foot=new CFootprint("ID_DEPLOY_APP_NET_PACKET", 
                                    this.hash, 
                                    dec_payload.hash, 
                                    this.fee.src, 
                                    this.fee.amount, 
                                    this.fee.hash,
                                    this.block);
                  
     foot.add("Run Period", String.valueOf(dec_payload.run_period));
     foot.write();
      
      // Return 
      return new CResult(true, "Ok", "CDeployAppNetPayload", 45);
  }
  
  public CResult commit(CBlockPayload block) throws Exception
  {
      // Check
      CResult res=super.check(block);
      if (res.passed==false) return res;
  	
      // Superclass
      super.commit(block);
      if (res.passed==false) return res;
  	  
      // Deserialize transaction data
      CDeployAppNetPayload dec_payload=(CDeployAppNetPayload) UTILS.SERIAL.deserialize(payload);
    
      // Fee is 0.0001 / day ?
      res=dec_payload.commit(block);
      if (res.passed==false) return res;
     
      // Return 
      return new CResult(true, "Ok", "CDeployAppNetPayload", 62);
  }

}


