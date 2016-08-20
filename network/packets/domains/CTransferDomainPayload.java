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
        
    // Serial
    private static final long serialVersionUID = 100L;
	
    public CTransferDomainPayload(String adr, 
                                  String domain, 
                                  String to_adr) throws Exception
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
   
   public void check(CBlockPayload block) throws Exception
   {
        // Super class
        super.check(block);
          
        // Destination address valid
        if (!UTILS.BASIC.isAdr(this.to_adr))
           throw new Exception("Invalid address - CTransferDomainPayload.java");
           
        // Domain valid
        if (!UTILS.BASIC.isDomain(this.domain))
           throw new Exception("Invalid domain - CTransferDomainPayload.java");
           
        // Hash
        String h=UTILS.BASIC.hash(this.getHash()+
    		                  this.domain+
    		                  this.to_adr);
        
        if (!h.equals(this.hash))
     	    throw new Exception("Invalid hash - CTransferDomainPayload.java");
       
        // Domain owned by address ?
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
  		                           + "FROM domains "
  		                          + "WHERE domain='"+this.domain+"' "
                                            + "AND adr='"+this.target_adr+"'");
        
        if (!UTILS.DB.hasData(rs))
           throw new Exception("Invalid domain - CTransferDomainPayload.java");
    }
	
    public void commit(CBlockPayload block) throws Exception
    {
        // Superclass
	super.commit(block);
	    
	// Change owner
	UTILS.DB.executeUpdate("UPDATE domains "
	    		        + "SET adr='"+this.to_adr+"', "
                                    + "sale_price='0', "
                                    + "block='"+this.block+"' "
	   		      + "WHERE domain='"+this.domain+"'");
   }
}
