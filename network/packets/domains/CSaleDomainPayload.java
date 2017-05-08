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

public class CSaleDomainPayload extends CPayload 
{
    // Domain
    String domain;
	
    // Sale price
    double sale_price;
	
    // Serial
    private static final long serialVersionUID = 100L;
    
	
   public CSaleDomainPayload(String owner_adr, 
		             String domain, 
		             double sale_price) throws Exception
   {
	// Constructor
	super(owner_adr);
	   
	// Domain
	this.domain=domain;
	   
	// Sale price
	this.sale_price=sale_price;
		
	// Hash
	hash=UTILS.BASIC.hash(this.hash+
			      this.domain+
		              String.valueOf(this.sale_price));
           
        // Sign
        this.sign();
   }
   
   public void check(CBlockPayload block) throws Exception
   {
        // Super class
        super.check(block);
         
        // Domain
        if (!UTILS.BASIC.isDomain(this.domain))
            throw new Exception("Invalid domain - CSaleDomainPayload.java");
        
        // Domain exist
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM domains "
                                          + "WHERE adr='"+this.target_adr+"' "
                                            + "AND domain='"+this.domain+"'"); 
         
        if (!UTILS.DB.hasData(rs))
           throw new Exception("Invalid domain owner - CSaleDomainPayload.java");
        
        // Hash
        String h=UTILS.BASIC.hash(this.getHash()+
    	 	                  this.domain+
    	 	                  String.valueOf(this.sale_price));
      
        if (!h.equals(this.hash))
     	   throw new Exception("Invalid hash - CSaleDomainPayload.java");
      
    }
	
   public void commit(CBlockPayload block) throws Exception
   {
       // Super class
       super.commit(block);
       
       // Update
       UTILS.DB.executeUpdate("UPDATE domains "
                               + "SET sale_price='"+this.sale_price+"', "
                                   + "block='"+block.block+"' "
                             + "WHERE domain='"+this.domain+"'");
   }
   
}

