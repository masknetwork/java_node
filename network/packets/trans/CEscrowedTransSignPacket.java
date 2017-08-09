// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.trans;

import java.sql.ResultSet;
import java.sql.Statement;
import wallet.kernel.CPackets;
import wallet.kernel.UTILS;
import wallet.network.packets.CBroadcastPacket;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.mes.CMesPayload;

public class CEscrowedTransSignPacket  extends CBroadcastPacket 
{
    // Serial
    private static final long serialVersionUID = 100L;
   
    public CEscrowedTransSignPacket(String fee_adr,
                                    String trans_hash, 
                                    String signer, 
                                    String type) throws Exception
    {
	  super("ID_ESCROWED_TRANS_SIGN");
	  
          // Builds the payload class
	  CEscrowedTransSignPayload dec_payload=new CEscrowedTransSignPayload(signer, trans_hash, type);
			
	   // Build the payload
	   this.payload=UTILS.SERIAL.serialize(dec_payload);
			
	   // Network fee
           CFeePayload fee=new CFeePayload(fee_adr,  0.0001);
	   this.fee_payload=UTILS.SERIAL.serialize(fee);
	   
	   // Sign packet
	   this.sign();
  }
  
  // Check 
  public void check(CBlockPayload block) throws Exception
  {
     // Super class
     super.check(block);
     	
     // Check type
     if (!this.tip.equals("ID_ESCROWED_TRANS_SIGN")) 
         throw new Exception("Invalid packet type - CEscrowedTransSignPacket.java");
  	  
     // Check
     CEscrowedTransSignPayload dec_payload=(CEscrowedTransSignPayload) UTILS.SERIAL.deserialize(payload);
     dec_payload.check(block);
     
     // Deserialize payload
     CFeePayload fee=(CFeePayload) UTILS.SERIAL.deserialize(fee_payload);
          
     // Check fee
     if (fee.amount<0.0001)
	throw new Exception("Invalid fee - CTweetMesPacket.java"); 
     
     // Footprint
     CPackets foot=new CPackets(this);
     foot.add("Transaction hash", dec_payload.trans_hash);
     foot.add("Signer", dec_payload.target_adr);
     foot.add("type", dec_payload.type);
     foot.write();
  }
  
 
}
