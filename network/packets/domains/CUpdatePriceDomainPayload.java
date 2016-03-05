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

public class CUpdatePriceDomainPayload extends CPayload
{
    // Domain
    String domain;
    
    // New price
    double new_price;
    
    // Days
    long days;
    
    // Mkt bid
    double mkt_bid;
    
    public CUpdatePriceDomainPayload(String adr, 
                                     String domain, 
                                     double new_price) throws Exception
    {
        // Constructor
        super(adr);
        
        // Domain
        this.domain=domain;
        
        // New price
        this.new_price=new_price;
        
        // Days
        this.days=days;
        
        // Market bid
        this.mkt_bid=mkt_bid;
        
        // Hash
        this.hash=UTILS.BASIC.hash(this.getHash()+ 
                                   this.domain+
                                   UTILS.FORMAT.format(this.new_price)+
                                   String.valueOf(days)+
                                   UTILS.FORMAT.format(this.mkt_bid));
        
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
	
        // Domain
        if (!UTILS.BASIC.domainValid(this.domain))
            return new CResult(false, "Invalid domain", "CSaleDomainPayload.java", 74);
        
        // Price
        if (!UTILS.BASIC.mktBidValid(this.new_price))
           return new CResult(false, "Invalid price", "CSaleDomainPayload.java", 74);
        
        // Domain exist
        Statement s=UTILS.DB.getStatement();
        ResultSet rs=s.executeQuery("SELECT * "
                                    + "FROM domains "
                                   + "WHERE adr='"+this.target_adr+"' "
                                     + "AND domain='"+this.domain+"' "
                                     + "AND sale_price>0"); 
         
        if (!UTILS.DB.hasData(rs))
            return new CResult(false, "Invalid doamin owner", "CSaleDomainPayload.java", 74);
        
        // Hash
        String h=UTILS.BASIC.hash(this.getHash()+ 
                                  this.domain+
                                  String.valueOf(this.new_price));
      
        if (!h.equals(this.hash))
     	    return new CResult(false, "Invalid hash", "CSaleDomainPayload.java", 74);
       
         // Close
         rs.close(); s.close();
       }
       catch (SQLException ex)
       {
           UTILS.LOG.log("SQLException", ex.getMessage(), "CNewFeedPayload.java", 60);
       }
       
       // Return
       return new CResult(true, "Ok", "CSaleDomainPayload", 67);
   }
	
	public CResult commit(CBlockPayload block) throws Exception
	{		
            CResult res=this.check(block);
	    if (res.passed==false) return res;
            
	    // Superclass
	    super.commit(block);
	       
            res=this.check(block);
            if (res.passed)
            {
                 // Update
	         UTILS.DB.executeUpdate("UPDATE domains "
	    		                 + "SET sale_price='"+this.new_price+"', "
	    		                     + "block='"+this.block+"' "
	    		               + "WHERE domain='"+this.domain+"'");
            }
            
           
            
	    // Return 
	    return new CResult(true, "Ok", "CAddNewAssetPayload.java", 149);
        }
}
