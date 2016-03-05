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

public class CTransferDomainPayload extends CPayload 
{
	// Domain
	String domain;
	
	// Transfer to adr
	String to_adr;
	
   public CTransferDomainPayload(String adr, String domain, String to_adr) throws Exception
   {
	super(adr);
		
        // Domain
	this.domain=domain;
		
	// Transfer to adr
	this.to_adr=to_adr;
		
	// Hash
	hash=UTILS.BASIC.hash(this.getHash()+
			      domain+
			      to_adr);
		
	// Sign
	this.sign();
   }
   
   public CResult check(CBlockPayload block) throws Exception
   {
        try
        {
	    // Super class
           CResult res=super.check(block);
           if (res.passed==false) return res;
	   
           // Destination address valid
           if (!UTILS.BASIC.adressValid(this.to_adr))
                return new CResult(false, "Invalid hash", "CTransferDomainPayload.java", 61);
           
           // Domain valid
           if (!UTILS.BASIC.domainValid(this.domain))
              return new CResult(false, "Invalid hash", "CTransferDomainPayload.java", 61);
           
           // Hash
           String h=UTILS.BASIC.hash(this.getHash()+
    		                     this.domain+
    		                     this.to_adr);
            if (!h.equals(this.hash))
     	       return new CResult(false, "Invalid hash", "CTransferDomainPayload.java", 61);
       
            // Domain owned by address ?
            Statement s=UTILS.DB.getStatement();
            ResultSet rs=s.executeQuery("SELECT * "
  		                              + "FROM domains "
  		                             + "WHERE domain='"+this.domain+"' "
                                               + "AND adr='"+this.target_adr+"'");
            if (!UTILS.DB.hasData(rs))
	    {
	        rs.close(); s.close();
                return new CResult(false, "Invalid domain owner", "CTransferDomainPayload.java", 61);
	    }
			
	    rs.close(); s.close();
        }
        catch (SQLException ex)
        {
            UTILS.LOG.log("SQLException", ex.getMessage(), "CTransferDomainPayload.java", 79);
        }
            
       // Return
	   return new CResult(true, "Ok", "CTransferDomainPayload", 67);
	}
	
	public CResult commit(CBlockPayload block) throws Exception
	{
            CResult res=this.check(block);
	    if (res.passed==false) return res;
		  
	    // Superclass
	    super.commit(block);
	    
	    // Loads domain data
	    res=this.check(block);
            if (res.passed)
            {
	       // Change owner
	       UTILS.DB.executeUpdate("UPDATE domains "
	    		               + "SET adr='"+this.to_adr+"', "
                                            + "sale_price='0', "
                                            + "market_bid=0, "
                                            + "market_expires=0, "
	    		                    + "block='"+this.block+"' "
	    		              + "WHERE domain='"+this.domain+"'");
          
                
            }
            
	    // Return 
	   return new CResult(true, "Ok", "CAddNewAssetPayload.java", 149);
	}
}
