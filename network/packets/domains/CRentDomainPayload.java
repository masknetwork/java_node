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


public class CRentDomainPayload extends CPayload 
{
	// Domain name
	String domain;
	
        // Market days
        long days;
	
	public CRentDomainPayload(String adr, 
                                  String domain, 
                                  long days)  throws Exception
	{
            // Constructor
            super(adr);
		
            // Days
            this.days=days;
		
	    // Domain
	    this.domain=domain;
		
	    // Hash
	    hash=UTILS.BASIC.hash(this.getHash()+        
                                  domain+
			          String.valueOf(days));
		
	    // Signature
	    this.sign();
	}
    
	public CResult check(CBlockPayload block) throws Exception
	{
            try
            {
	      
              // Domain valid
              if (!UTILS.BASIC.domainValid(this.domain))
                 return new CResult(false, "Invalid domain", "CRentDomainPayload.java", 61);
              
              // Hash
              String h=UTILS.BASIC.hash(this.getHash()+        
                                        domain+
			                String.valueOf(days));
              if (!h.equals(this.hash))
      	         return new CResult(false, "Invalid hash", "CRentDomainPayload.java", 61);
        
              // Domain already taken ?
              Statement s=UTILS.DB.getStatement();
              ResultSet rs=s.executeQuery("SELECT * "
   		                          + "FROM domains "
   		                         + "WHERE domain='"+this.domain+"'");
              if (UTILS.DB.hasData(rs)==true)
                 return new CResult(false, "Domain already exist", "CRentDomainPayload.java", 79);
        	
              // Close
              rs.close(); s.close();
              
              
            }
            catch (SQLException ex)
            {
               UTILS.LOG.log("SQLException", ex.getMessage(), "CRentDomainPayload.java", 84);
            }
            
            // Return
	    return new CResult(true, "Ok", "CRentDomainPayload", 67);
	}
	
	public CResult commit(CBlockPayload block) throws Exception
	{
            // Superclass
	    super.commit(block);
            
            // Check again
            CResult res=this.check(block);
	    if (res.passed==false) return res;
            
            
	    UTILS.DB.executeUpdate("INSERT INTO domains (adr, "
        	   		                          + "domain, "
        	   		                          + "expires, "
        	   		                          + "sale_price, "
        	   		                          + "market_bid, "
        	   		                          + "market_expires, "
                                                          + "block) VALUES ('"+
                                                          this.target_adr+"', '"+
                                                          this.domain+"', '"+
                                                          (this.block+this.days*1440)+"', "
                                                          + "'0', "
                                                          + "'0', "
                                                          + "'0', '"+
                                                          String.valueOf(UTILS.BASIC.block())+"')");
                
            
	   // Return 
	   return new CResult(true, "Ok", "CAddNewAssetPayload.java", 149);
	}
}
