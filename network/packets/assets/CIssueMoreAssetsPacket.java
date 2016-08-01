package wallet.network.packets.assets;

import java.sql.ResultSet;
import java.sql.Statement;
import wallet.kernel.CPackets;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CBroadcastPacket;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.CFeePayload;

public class CIssueMoreAssetsPacket extends CBroadcastPacket
{
    public CIssueMoreAssetsPacket(String fee_adr,
		                  String adr,
                                  String symbol,
                                  long qty,
                                  String packet_sign,
                                  String payload_sign) throws Exception
   {
	   super("ID_ISSUE_MORE_ASSETS_PACKET");

	   // Builds the payload class
	   CIssueMoreAssetsPayload dec_payload=new CIssueMoreAssetsPayload(adr,
                                                                           symbol,
                                                                           qty,
                                                                           payload_sign); 
           
	   // Build the payload
	   this.payload=UTILS.SERIAL.serialize(dec_payload);
           
	   // Network fee
	   fee=new CFeePayload(fee_adr, dec_payload.qty*0.0001);
	   
	   // Sign packet
	   this.sign(packet_sign);
   }
   
   // Check 
   public void check(CBlockPayload block) throws Exception
   {
         // Super class
   	  super.check(block);
   	  
   	  // Check type
   	  if (!this.tip.equals("ID_ISSUE_MORE_ASSETS_PACKET")) 
             throw new Exception("Invalid packet type - CIssueMoreAssetsPacket.java");
   	  
          // Deserialize transaction data
   	  CIssueMoreAssetsPayload dec_payload=(CIssueMoreAssetsPayload) UTILS.SERIAL.deserialize(payload);
           
          // Check fee
	  if (this.fee.amount<dec_payload.qty*0.0001)
	      throw new Exception("Invalid fee - CIssueMoreAssetsPacket.java");
          
          // Check payload
          dec_payload.check(block);
          
          // Footprint
          CPackets foot=new CPackets(this);
                  
          foot.add("Address", dec_payload.target_adr);
          foot.add("Symbol", dec_payload.symbol);
          foot.add("Qty", String.valueOf(dec_payload.qty));
          foot.write();
   	  
   }
   
   
}