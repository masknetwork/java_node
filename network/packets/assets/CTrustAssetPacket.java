// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.assets;

import java.sql.ResultSet;
import java.sql.Statement;
import wallet.kernel.*;
import wallet.network.CResult;
import wallet.network.packets.*;
import wallet.network.packets.ads.CNewAdPayload;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.CFeePayload;

public class CTrustAssetPacket extends CBroadcastPacket 
{
   public CTrustAssetPacket(String fee_adr,
		      String adr,
                      String asset,
                      long days) throws Exception
   {
	   super("ID_TRUST_ASSET_PACKET");

	   // Builds the payload class
	   CTrustAssetPayload dec_payload=new CTrustAssetPayload(adr,
                                                                 asset,
                                                                 days); 
           
	   // Build the payload
	   this.payload=UTILS.SERIAL.serialize(dec_payload);
           
           // Net fee 
           double net_fee=0.0001*days;
               
	   // Network fee
	  CFeePayload fee=new CFeePayload(fee_adr,  net_fee);
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
   	if (!this.tip.equals("ID_TRUST_ASSET_PACKET")) 
             throw new Exception("Invalid packet type - CTrustAssetPacket.java");
   	  
        // Deserialize transaction data
   	CTrustAssetPayload dec_payload=(CTrustAssetPayload) UTILS.SERIAL.deserialize(payload);
          
        // Check payload
        dec_payload.check(block);
          
        // Net fee
        double net_fee=dec_payload.days*0.0001;
        
        // Deserialize payload
        CFeePayload fee=(CFeePayload) UTILS.SERIAL.deserialize(fee_payload);
        
        // Check fee
	if (fee.amount<net_fee)
	      throw new Exception("Invalid fee - CTrustAssetPacket.java");
          
        // Footprint
        CPackets foot=new CPackets(this);
        foot.add("Address", dec_payload.target_adr);
        foot.add("Asset", dec_payload.asset);
        foot.add("Days", String.valueOf(dec_payload.days));
        foot.write();
   }
}