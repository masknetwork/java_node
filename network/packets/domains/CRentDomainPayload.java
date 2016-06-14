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
                                  long days,
                                  String payload_sign)  throws Exception
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
	    this.sign(payload_sign);
	}
    
	public CResult check(CBlockPayload block) throws Exception
	{
           // Domain valid
           if (!UTILS.BASIC.isDomain(this.domain))
                 throw new Exception("Invalid domain name - CRentDomainPayload.java");
              
           // Hash
           String h=UTILS.BASIC.hash(this.getHash()+        
                                     domain+
			             String.valueOf(days));
           
           // Check hash
           if (!h.equals(this.hash))
      	       throw new Exception("Invalid hash - CRentDomainPayload.java");
        
           // Domain already taken ?
           Statement s=UTILS.DB.getStatement();
           ResultSet rs=s.executeQuery("SELECT * "
   		                       + "FROM domains "
   		                      + "WHERE domain='"+this.domain+"'");
           
           // Domain exist
           if (UTILS.DB.hasData(rs)==true)
               throw new Exception("Invalid domain name - CRentDomainPayload.java");
           
           // Days
           if (this.days<100)
               throw new Exception("Invalid rent period - CRentDomainPayload.java");
           
           // Close
           s.close();
           
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
        	   		                          + "expire, "
        	   		                          + "sale_price, "
        	   		                          + "block) VALUES ('"+
                                                          this.target_adr+"', '"+
                                                          this.domain+"', '"+
                                                          (this.block+this.days*1440)+"', "
                                                          + "'0', '"
                                                          +this.block+"')");
                
            
	   // Return 
	   return new CResult(true, "Ok", "CAddNewAssetPayload.java", 149);
	}
}
