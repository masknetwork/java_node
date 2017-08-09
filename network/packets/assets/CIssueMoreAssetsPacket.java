package wallet.network.packets.assets;

import java.sql.ResultSet;
import java.sql.Statement;
import wallet.kernel.CPackets;
import wallet.kernel.UTILS;
import wallet.network.packets.CBroadcastPacket;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.CFeePayload;

public class CIssueMoreAssetsPacket extends CBroadcastPacket
{
    public CIssueMoreAssetsPacket(String fee_adr,
		                  String adr,
                                  String symbol,
                                  long qty) throws Exception
   {
	   super("ID_ISSUE_MORE_ASSETS_PACKET");

	   // Builds the payload class
	   CIssueMoreAssetsPayload dec_payload=new CIssueMoreAssetsPayload(adr,
                                                                           symbol,
                                                                           qty); 
           
	   // Build the payload
	   this.payload=UTILS.SERIAL.serialize(dec_payload);
           
           // Load asset data
           ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                              + "FROM assets "
                                             + "WHERE symbol='"+symbol+"'");
           
           // Next
           rs.next();
           
           // Trans fee
           double trans_fee=rs.getDouble("trans_fee");
           
           // Days
           long days=Math.round((rs.getLong("expire")-this.block)/1440);
           
           // Minimum days
           if (days<1) days=1;
           
           // Net fee 
           long t_fee=0;
           
           // Round transaction fee
           t_fee=Math.round(trans_fee);
           
           // Minimum 
           if (t_fee<1) t_fee=1;
           
           // Compute transaction fee
           double net_fee=((days*0.0001)+(qty*0.0001))*t_fee;
           if (net_fee<1) net_fee=1;
           
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
   	  if (!this.tip.equals("ID_ISSUE_MORE_ASSETS_PACKET")) 
             throw new Exception("Invalid packet type - CIssueMoreAssetsPacket.java");
   	  
          // Deserialize transaction data
   	  CIssueMoreAssetsPayload dec_payload=(CIssueMoreAssetsPayload) UTILS.SERIAL.deserialize(payload);
          
          // Deserialize payload
          CFeePayload fee=(CFeePayload) UTILS.SERIAL.deserialize(fee_payload);
          
          // Load asset data
           ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                              + "FROM assets "
                                             + "WHERE symbol='"+dec_payload.symbol+"'");
           
           // Next
           rs.next();
           
           // Trans fee
           double trans_fee=rs.getDouble("trans_fee");
           
           // Days
           long days=Math.round((rs.getLong("expire")-this.block)/1440);
           
           // Minimum days
           if (days<1) days=1;
           
           // Net fee 
           long t_fee=0;
           
           // Round transaction fee
           t_fee=Math.round(trans_fee);
           
           // Minimum 
           if (t_fee<1) t_fee=1;
           
           // Compute transaction fee
           double net_fee=((days*0.0001)+(dec_payload.qty*0.0001))*t_fee;
           if (net_fee<1) net_fee=1;
        
          // Check fee
	  if (fee.amount<net_fee)
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