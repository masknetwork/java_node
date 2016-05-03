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
	
	// Market bid
	double mkt_bid;
	
	// Market expires
	long mkt_days;
	
   public CSaleDomainPayload(String owner_adr, 
		             String domain, 
		             double sale_price, 
		             double mkt_bid, 
		             long mkt_days) throws Exception
   {
	   // Constructor
	   super(owner_adr);
	   
	   // Domain
	   this.domain=domain;
	   
	   // Sale price
	   this.sale_price=sale_price;
		
	   // Market bid
	   this.mkt_bid=mkt_bid;
		
	   // Market expires
	   this.mkt_days=mkt_days;
	   
	   // Hash
	   hash=UTILS.BASIC.hash(this.hash+
			         this.domain+
				 String.valueOf(this.sale_price)+
				 String.valueOf(this.mkt_bid)+
				 String.valueOf(this.mkt_days));
           
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
        
          
          // Domain exist
          Statement s=UTILS.DB.getStatement();
          ResultSet rs=s.executeQuery("SELECT * "
                                    + "FROM domains "
                                   + "WHERE adr='"+this.target_adr+"' "
                                     + "AND domain='"+this.domain+"' "
                                     + "AND sale_price='0'"); 
         
          if (!UTILS.DB.hasData(rs))
              return new CResult(false, "Invalid doamin owner", "CSaleDomainPayload.java", 74);
        
          // Hash
          String h=UTILS.BASIC.hash(this.getHash()+
    		                 this.domain+
    		                 String.valueOf(this.sale_price)+
		                 String.valueOf(this.mkt_bid)+
		                 String.valueOf(this.mkt_days));
      
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
                 // Expire
                 String expire=String.valueOf(this.block+(1440*this.mkt_days));
            
	         // Update
	         UTILS.DB.executeUpdate("UPDATE domains "
	    		                 + "SET sale_price='"+this.sale_price+"', "
	    		                     + "market_bid='"+this.mkt_bid+"', "
	    		                     + "market_expires='"+expire+"', "
                                             + "block='"+this.block+"' "
	    		               + "WHERE domain='"+this.domain+"'");
            }
            
            
	    // Return 
	    return new CResult(true, "Ok", "CAddNewAssetPayload.java", 149);
	}
}

