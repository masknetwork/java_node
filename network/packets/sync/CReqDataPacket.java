package wallet.network.packets.sync;

import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CPacket;


public class CReqDataPacket extends CPacket 
{
   public CReqDataPacket(String data_type, long start, long end) throws Exception
   {
	   super("ID_REQ_DATA_PACKET");
	   
	   // Builds the transaction
	   CReqDataPayload trans_payload=new CReqDataPayload(data_type, start, end);
			
	   // Build the payload
	   this.payload=UTILS.SERIAL.serialize(trans_payload);
	   
	   // Hash
	   this.hash=UTILS.BASIC.hash(data_type+start+end);
   }
   
   
   public CResult commit(String sender) throws Exception
   {
	   super.commit(null);
	   
	   // Deserialize the payload
	   CReqDataPayload dec_payload=(CReqDataPayload) UTILS.SERIAL.deserialize(payload);
	   
	   // Commit
	   CResult res=dec_payload.commit(sender);
	   if (res.passed==false) return res;
	   
	   // Return
	   return new CResult(true, "Ok", "CReqDataPayload", 164);
   }
}
