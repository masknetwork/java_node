package wallet.network.packets.domains;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.*;

public class CUpdatePriceDomainPacket extends CBroadcastPacket
{
     public CUpdatePriceDomainPacket(String fee_adr,
		                     String domain, 
                                     double new_price)
    {
	 super("ID_UPDATE_DOMAIN_PRICE_PACKET");
	 
         String adr="";
         
         try
         {
           // Load seller address
           Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
           ResultSet rs=s.executeQuery("SELECT * "
  		                             + "FROM domains "
  		                            + "WHERE domain='"+domain+"'");
           // Next
           rs.next();
           
           // Address
           adr=rs.getString("adr");
           
           // Close
           s.close();
        }
        catch (SQLException ex)
        {
           UTILS.LOG.log("SQLException", ex.getMessage(), "CRenewDomainPacket.java", 60);
        }
          
          
	  // Builds the payload class
	  CUpdatePriceDomainPayload dec_payload=new CUpdatePriceDomainPayload(adr, domain, new_price);
					
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
	    if (this.tip!="ID_UPDATE_DOMAIN_PRICE_PACKET") 
	       return new CResult(false, "Invalid packet type", "CRenewDomainPacket", 42);
		   
	    // Deserialize transaction data
	    CUpdatePriceDomainPayload dec_payload=(CUpdatePriceDomainPayload) UTILS.SERIAL.deserialize(payload);
		   
	    // Check payload
	    res=dec_payload.check(block);
            if (!res.passed) return res;
            
            // Return 
	    return new CResult(true, "Ok", "CRenewDomainPacket", 74);
	}
		   
	public CResult commit(CBlockPayload block)
	{
	    // Superclass
            CResult res=super.commit(block);
	    if (res.passed==false) return res;
		   	  
	    // Deserialize transaction data
	    CUpdatePriceDomainPayload ass_payload=(CUpdatePriceDomainPayload) UTILS.SERIAL.deserialize(payload);

	    // Commit payload
	    res=ass_payload.commit(block);	  
	    if (res.passed==false) return res;
	    		  
	    // Return 
            return new CResult(true, "Ok", "CRenewDomainPacket", 9);
      }
  }