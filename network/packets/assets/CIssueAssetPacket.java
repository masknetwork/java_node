// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.assets;

import wallet.kernel.*;
import wallet.network.CResult;
import wallet.network.packets.*;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.CFeePayload;

public class CIssueAssetPacket extends CBroadcastPacket 
{
   public CIssueAssetPacket(String fee_adr,
		            String adr,
                            String symbol,
                            String title,
                            String description,
                            String how_buy,
                            String how_sell,
                            String web_page,
                            String pic,
                            long mkt_days,
                            long qty,
                            String trans_fee_adr,
                            double trans_fee,
                            String can_issue,
                            double interest,
                            long interval) throws Exception
   {
	   super("ID_NEW_ASSET_PACKET");

	   // Builds the payload class
	   CIssueAssetPayload dec_payload=new CIssueAssetPayload(adr,
                                                                 symbol,
                                                                 title,
                                                                 description,
                                                                 how_buy,
                                                                 how_sell,
                                                                 web_page,
                                                                 pic,
                                                                 mkt_days,
                                                                 qty,
                                                                 trans_fee_adr,
                                                                 trans_fee,
                                                                 can_issue,
                                                                 interest,
                                                                 interval); 
           
	   // Build the payload
	   this.payload=UTILS.SERIAL.serialize(dec_payload);
			
	   // Network fee
	   fee=new CFeePayload(fee_adr, (0.0001*mkt_days)+(qty*0.0001));
	   
	   // Sign packet
	   this.sign();
   }
   
   // Check 
   public CResult check(CBlockPayload block) throws Exception
   {
          // Super class
   	  CResult res=super.check(block);
   	  if (res.passed==false) return res;
   	
   	  // Check type
   	  if (!this.tip.equals("ID_NEW_ASSET_PACKET")) 
   		return new CResult(false, "Invalid packet type", "CNewAssetPacket", 42);
   	  
          // Deserialize transaction data
   	  CIssueAssetPayload dec_payload=(CIssueAssetPayload) UTILS.SERIAL.deserialize(payload);
          
          // Check payload
          res=dec_payload.check(block);
          if (!res.passed) return res;
   	  
   	  // Return 
   	  return new CResult(true, "Ok", "CNewAssetPacket", 74);
   }
   
   public CResult commit(CBlockPayload block) throws Exception
   {
   	  // Superclass
   	  CResult res=super.commit(block);
   	  if (res.passed==false) return res;
   	  
   	  // Deserialize transaction data
   	  CIssueAssetPayload ass_payload=(CIssueAssetPayload) UTILS.SERIAL.deserialize(payload);

	   res=ass_payload.commit(block);
	   if (res.passed==false) return res;
	  
	  // Return 
   	  return new CResult(true, "Ok", "CNewAssetPacket", 9);
   }
}