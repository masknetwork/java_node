package wallet.network.packets.trans;

import java.sql.ResultSet;
import java.sql.Statement;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CBroadcastPacket;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.mes.CMesPayload;

public class CEscrowedTransSignPacket  extends CBroadcastPacket 
{
    public CEscrowedTransSignPacket(String fee_adr,
                                    String signer, 
                                    String trans_hash, 
                                    String type)
    {
	  super("ID_ESCROWED_TRANS_SIGN");
	  
          // Builds the payload class
	  CEscrowedTransSignPayload dec_payload=new CEscrowedTransSignPayload(signer, trans_hash, type);
			
	   // Build the payload
	   this.payload=UTILS.SERIAL.serialize(dec_payload);
			
	   // Network fee
	   fee=new CFeePayload(fee_adr);
	   
	   // Sign packet
	   this.sign();
  }
  
  // Check 
  public CResult check(CBlockPayload block)
  {
     // Super class
     CResult res=super.check(block);
     if (res.passed==false) return res;
  	
     // Check type
     if (!this.tip.equals("ID_ESCROWED_TRANS_SIGN")) 
         return new CResult(false, "Invalid packet type", "CMesPacketPacket", 39);
  	  
     // Check
     CEscrowedTransSignPacket pay=(CEscrowedTransSignPacket) UTILS.SERIAL.deserialize(payload);
     res=pay.check(block);
     if (!res.passed) return res;
  	
     // Return 
     return new CResult(true, "Ok", "CMesPacketPacket", 45);
  }
  
  public CResult commit(CBlockPayload block)
  {
     // Superclass
     CResult res=super.commit(block);
     if (res.passed==false) return res;
  	  
     // Deserialize transaction data
     CEscrowedTransSignPacket dec_payload=(CEscrowedTransSignPacket) UTILS.SERIAL.deserialize(payload);

     // Commit payload
     res=dec_payload.commit(block);
     if (res.passed==false) return res;
	  
     // Return 
     return new CResult(true, "Ok", "CMesPacketPacket", 62);
}
}
