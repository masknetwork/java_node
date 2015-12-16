package wallet.network.packets.domains;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.blocks.*;
import wallet.network.packets.trans.*;

public class CRenewDomainPayload extends CPayload
{
    // Domain
    String domain;
    
    // Days
    long days;
    
    public CRenewDomainPayload(String adr, String domain, long days)
    {
        // Constructor
	super(adr);
		
        // Domain
	this.domain=domain;
        
        // Days
        this.days=days;
        
        // Hash
        hash=UTILS.BASIC.hash(this.getHash()+
                             domain+
                             String.valueOf(days));
        
        // Sign
        this.sign();
    }
    
    public CResult check(CBlockPayload block)
	{
            try
            {
	      // Check market days
              if (!UTILS.BASIC.mkt_days_valid(this.days))
               return new CResult(false, "Invalid market days", "CRentDomainPayload.java", 61);
              
              // Domain valid
              if (!UTILS.BASIC.domainValid(this.domain))
                 return new CResult(false, "Invalid domain", "CRentDomainPayload.java", 61);
              
              // Domain exist ?
              Statement s=UTILS.DB.getStatement();
              ResultSet rs=s.executeQuery("SELECT * "
   		                          + "FROM domains "
   		                         + "WHERE domain='"+this.domain+"' "
                                           + "AND adr='"+this.target_adr+"'");
              if (!UTILS.DB.hasData(rs)==true)
                 return new CResult(false, "Domain already exist", "CRentDomainPayload.java", 79);
        	 
              // Check days
              if (!UTILS.BASIC.mkt_days_valid(this.days))
                 return new CResult(false, "Invalid days", "CRentDomainPayload.java", 61);
              
              // Hash
              String h=UTILS.BASIC.hash(this.getHash()+        
                                        domain+
			                String.valueOf(days));
              if (!h.equals(this.hash))
      	         return new CResult(false, "Invalid hash", "CRentDomainPayload.java", 61);
              
              // Close
              s.close();
            }
            catch (SQLException ex)
            {
               UTILS.LOG.log("SQLException", ex.getMessage(), "CRentDomainPayload.java", 84);
            }
            
            // Return
	    return new CResult(true, "Ok", "CRentDomainPayload", 67);
	}
	
	public CResult commit(CBlockPayload block)
	{
            // Superclass
	    super.commit(block);
            
            // Check again
            CResult res=this.check(block);
	    if (res.passed==false) return res;
            
            
	    UTILS.DB.executeUpdate("UPDATE domains "
                                    + "SET expires=expires+"+(this.days*1440)+", "
                                        + "block='"+this.block+"' "
                                  + "WHERE domain='"+this.domain+"'");
                
            // Rowhash
            UTILS.ROWHASH.update("domains", "domain", this.domain);
          
            
	   // Return 
	   return new CResult(true, "Ok", "CAddNewAssetPayload.java", 149);
	}
}
