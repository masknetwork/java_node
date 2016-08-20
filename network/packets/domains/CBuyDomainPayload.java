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
    
    // Attach address
    String buyer_adr;
	
    
    public CBuyDomainPayload(String buyer_adr, 
                             String domain) throws Exception
    {
        // Constructor
	super(buyer_adr);
		
        // Domain
	this.domain=domain;
        
        // Attach
        this.buyer_adr=buyer_adr;
	
        // Hash
	hash=UTILS.BASIC.hash(this.getHash()+
                              buyer_adr+
			      domain);
       
        // Sign
        this.sign();
    }
   
    public void check(CBlockPayload block) throws Exception
    {
        // Parent
        super.check(block);
                 
        // Attach address
        if (!UTILS.BASIC.isAdr(this.buyer_adr))
            throw new Exception("Invalid attach address - CBuyDomainPayload");
                 
        // Hash
        String h=UTILS.BASIC.hash(this.getHash()+
                                  buyer_adr+
			          domain);
        
        if (!h.equals(this.hash))
     	   throw new Exception("Invalid hash - CBuyDomainPayload");
       
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
                                  true,
                                  "MSK", 
                                  "Domain aquisition", 
                                  "", 
                                  this.hash, 
                                  this.block,
                                  block,
                                  0);
        }
        else throw new Exception("Invalid domain - CBuyDomainPayload.java");
    }
       
    public void commit(CBlockPayload block) throws Exception
    {	
        // Superclass
	super.commit(block);
        
        // Commit
        UTILS.ACC.clearTrans(this.hash, "ID_ALL", this.block);
	    
	// Transfer domain
	UTILS.DB.executeUpdate("UPDATE domains "
	     		             + "SET adr='"+this.buyer_adr+"', "
                                         + "sale_price='0', "
                                         + "block='"+this.block+"' "
	     		           + "WHERE domain='"+this.domain+"'");
   }
}