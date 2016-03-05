// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.domains;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.*;

public class CSaleDomainPacket extends CBroadcastPacket 
{
   public CSaleDomainPacket(String fee_adr, 
		            String domain, 
                            double sale_price, 
                            double mkt_bid, 
                            long mkt_days) throws Exception
   {
	   super("ID_SALE_DOMAIN_PACKET");
           
          
           try
           {
             // Load seller address
             Statement s=UTILS.DB.getStatement();
             ResultSet rs=s.executeQuery("SELECT * "
  		                         + "FROM domains "
  		                        + "WHERE domain='"+domain+"'");
             // Next
             rs.next();
           
             // Address
             String owner_adr=rs.getString("adr");
           
             // Close
             rs.close(); s.close();
           
	   
	     // Builds the payload class
	     CSaleDomainPayload dec_payload=new CSaleDomainPayload(owner_adr, 
			                                           domain, 
			                                           sale_price, 
			                                           mkt_bid, 
			                                           mkt_days);
					
	     // Build the payload
	     this.payload=UTILS.SERIAL.serialize(dec_payload);
					
	     // Network fee
	     fee=new CFeePayload(fee_adr,  mkt_days*mkt_bid);
			   
	     // Sign packet
	     this.sign();
           }
           catch (SQLException ex)
           {
              UTILS.LOG.log("SQLException", ex.getMessage(), "CRenewDomainPacket.java", 60);
           }
   }

    // Check 
	public CResult check(CBlockPayload block) throws Exception
	{
	   // Super class
	   CResult res=super.check(block);
	   if (res.passed==false) return res;
	   	
	   // Check type
	   if (!this.tip.equals("ID_SALE_DOMAIN_PACKET")) 
	   	  return new CResult(false, "Invalid packet type", "CSaleDomainPacket", 42);
	   
	   // Deserialize transaction data
	   CSaleDomainPayload dec_payload=(CSaleDomainPayload) UTILS.SERIAL.deserialize(payload);
	   
           // Check payoad
           res=dec_payload.check(block);
           if (!res.passed) return res;
           
	   // Check fee
	   if (this.fee.amount<(dec_payload.mkt_days*dec_payload.mkt_bid))
	       return new CResult(false, "Invalid fee", "CSaleDomainPacket", 42);
	   	
	   // Return 
	   return new CResult(true, "Ok", "CSaleDomainPacket", 74);
	}
	   
	public CResult commit(CBlockPayload block) throws Exception
	{
	   	  // Superclass
	   	  CResult res=super.commit(block);
	   	  if (res.passed==false) return res;
	   	  
	   	  // Deserialize transaction data
	   	  CSaleDomainPayload dec_payload=(CSaleDomainPayload) UTILS.SERIAL.deserialize(payload);

		  // Fee is 0.0001 / day ?
		  res=dec_payload.commit(block);
	          if (res.passed==false) return res;
		  		  
		  // Return 
	   	  return new CResult(true, "Ok", "CSaleDomainPacket", 9);
	   }
	}

