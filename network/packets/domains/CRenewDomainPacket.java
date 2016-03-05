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

public class CRenewDomainPacket extends CBroadcastPacket
{
    public CRenewDomainPacket(String fee_adr,
		           String domain, 
                           long days) throws Exception
    {
	 super("ID_RENEW_DOMAIN_PACKET");
	 
         String adr="";
         
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
           adr=rs.getString("adr");
           
           // Close
           rs.close(); s.close();
        }
        catch (SQLException ex)
        {
           UTILS.LOG.log("SQLException", ex.getMessage(), "CRenewDomainPacket.java", 60);
        }
          
          
	  // Builds the payload class
	  CRenewDomainPayload dec_payload=new CRenewDomainPayload(adr, domain, days);
					
	  // Build the payload
	  this.payload=UTILS.SERIAL.serialize(dec_payload);
					
          // Network fee
	  fee=new CFeePayload(fee_adr, days*0.0001);
			   
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
	    if (!this.tip.equals("ID_RENEW_DOMAIN_PACKET")) 
	       return new CResult(false, "Invalid packet type", "CRenewDomainPacket", 42);
		   
	    // Deserialize transaction data
	    CRenewDomainPayload dec_payload=(CRenewDomainPayload) UTILS.SERIAL.deserialize(payload);
		   
	    // Check payload
	    res=dec_payload.check(block);
            if (!res.passed) return res;
            
            // Check fee
            if (this.fee.amount<(dec_payload.days*0.0001))
               return new CResult(false, "Invalid price", "CRenewDomainPacket", 42);
            
	    // Return 
	    return new CResult(true, "Ok", "CRenewDomainPacket", 74);
	}
		   
	public CResult commit(CBlockPayload block) throws Exception
	{
	    // Superclass
            CResult res=super.commit(block);
	    if (res.passed==false) return res;
		   	  
	    // Deserialize transaction data
	    CRenewDomainPayload ass_payload=(CRenewDomainPayload) UTILS.SERIAL.deserialize(payload);

	    // Commit payload
	    res=ass_payload.commit(block);	  
	    if (res.passed==false) return res;
	    		  
	    // Return 
            return new CResult(true, "Ok", "CRenewDomainPacket", 9);
      }
  }