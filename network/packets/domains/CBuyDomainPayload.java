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


public class CBuyDomainPayload extends CPayload 
{
    // Serial
   private static final long serialVersionUID = 100L;
   
    // Domain
    String domain;
    
    
    public CBuyDomainPayload(String buyer_adr, 
                             String domain) throws Exception
    {
        // Constructor
	super(buyer_adr);
		
        // Domain
	this.domain=domain;
        
        // Hash
	hash=UTILS.BASIC.hash(this.getHash()+
                              domain);
       
        // Sign
        this.sign();
    }
   
    public void check(CBlockPayload block) throws Exception
    {
        // Parent
        super.check(block);
        
        // Check domain name
        if (!UTILS.BASIC.isDomain(this.domain))
           throw new Exception("Invalid domain name - CBuyDomainPayload.java");
       
        // Domain for sale ?
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
  		                           + "FROM domains "
  		                          + "WHERE domain='"+this.domain+
                                            "' AND sale_price>0");
        if (UTILS.DB.hasData(rs)==true)
        {
            // Next
       	    rs.next();
            
            // Transaction
            UTILS.ACC.newTransfer(this.target_adr,
                                  rs.getString("adr"), 
                                  rs.getDouble("sale_price"),
                                  "MSK", 
                                  "Domain aquisition ("+this.domain+")", 
                                  "", 
                                  this.hash, 
                                  this.block);
        }
        else throw new Exception("Invalid domain - CBuyDomainPayload.java");
        
        // Hash
        String h=UTILS.BASIC.hash(this.getHash()+
			          domain);
        
        if (!h.equals(this.hash))
     	   throw new Exception("Invalid hash - CBuyDomainPayload");
    }
       
    public void commit(CBlockPayload block) throws Exception
    {	
        // Superclass
	super.commit(block);
            
	// Transfer domain
	UTILS.DB.executeUpdate("UPDATE domains "
	     		        + "SET adr='"+this.target_adr+"', "
                                    + "sale_price='0', "
                                    + "block='"+this.block+"' "
	                      + "WHERE domain='"+this.domain+"'");
        
        // Commit
        UTILS.ACC.clearTrans(this.hash, "ID_ALL", this.block);
   }
}