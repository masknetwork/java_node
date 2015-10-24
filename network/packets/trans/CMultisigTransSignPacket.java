package wallet.network.packets.trans;

import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CBroadcastPacket;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.mes.CMesPayload;

public class CMultisigTransSignPacket extends CBroadcastPacket
{
    public CMultisigTransSignPacket(String fee_adr,
                                    String trans_hash,
                                    String signer)
    {
        super("ID_MULTISIG_TRANS_SIGN_PACKET");
	  
        // Builds the payload class
        CMultisigTransSignPayload dec_payload=new CMultisigTransSignPayload(trans_hash, signer);
			
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
     if (!this.tip.equals("ID_MULTISIG_TRANS_SIGN_PACKET")) 
         return new CResult(false, "Invalid packet type", "CMesPacketPacket", 39);
  	  
     // Check
     CMultisigTransSignPayload pay=(CMultisigTransSignPayload) UTILS.SERIAL.deserialize(payload);
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
     CMultisigTransSignPayload dec_payload=(CMultisigTransSignPayload) UTILS.SERIAL.deserialize(payload);

     // Commit payload
     res=dec_payload.commit(block);
     if (res.passed==false) return res;
	  
     // Return 
     return new CResult(true, "Ok", "CMesPacketPacket", 62);
}
}
