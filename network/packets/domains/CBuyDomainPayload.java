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
    // Domain
    String domain;
    
    // Attach address
    String attach_adr;
	
    // Transaction
    public CTransPayload pay;
	
    public CBuyDomainPayload(String buyer_adr, 
                             String attach_adr, 
    		             String domain)
    {
        // Constructor
	super(buyer_adr);
		
        // Domain
	this.domain=domain;
        
        // Attach
        this.attach_adr=attach_adr;
	
        try
        {
           // Load seller address
           Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
           ResultSet rs=s.executeQuery("SELECT * "
  		                             + "FROM domains "
  		                            + "WHERE domain='"+this.domain+
                                              "' AND sale_price>0");
           // Next
           rs.next();
           
           // Transaction
	   pay=new CTransPayload(buyer_adr, 
                                 rs.getString("adr"), 
                                 rs.getDouble("sale_price"), 
                                 "MSK", 
                                 "", 
                                 "", 
                                 "");
		
           // Hash
	   hash=UTILS.BASIC.hash(this.getHash()+
                                 attach_adr+
			         domain+
		                 UTILS.BASIC.hash(UTILS.SERIAL.serialize(this.pay)));
           
           // Close
           s.close();
        }
        catch (SQLException ex)
        {
           UTILS.LOG.log("SQLException", ex.getMessage(), "CBuyDomainPayload.java", 60);
        }
        
        // Sign
        this.sign();
    }
   
    public CResult check(CBlockPayload block)
	{
             try
       	     { 
                 // Super class
                 CResult res=super.check(block);
                 if (res.passed==false) return res;
	         
                 // Attach address
                 if (!UTILS.BASIC.adressValid(this.attach_adr))
                    return new CResult(false, "Invalid attach address", "CBuyDomainPayload.java", 79);
                 
                 // Hash
                 String h=UTILS.BASIC.hash(this.getHash()+
                                           attach_adr+
    		                           this.domain+
    		                           UTILS.BASIC.hash(UTILS.SERIAL.serialize(this.pay)));
                 if (!h.equals(this.hash))
     	            return new CResult(false, "Invalid hash", "CBuyDomainPayload.java", 74);
       
                  // Check domain name
                 if (!UTILS.BASIC.domainValid(this.domain))
                     return new CResult(false, "Invalid domain name", "CBuyDomainPayload.java", 79);
       
                 // Domain for sale ?
                 Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                 ResultSet rs=s.executeQuery("SELECT * "
  		                             + "FROM domains "
  		                            + "WHERE domain='"+this.domain+
                                              "' AND sale_price>0");
                 if (UTILS.DB.hasData(rs)==true)
                 {
       	             rs.next();
       	   
       	             // Check price
       	             if (rs.getDouble("sale_price")!=this.pay.amount)
       	 	        return new CResult(false, "Invalid domain price", "CBuyDomainPayload.java", 45);
       	         }
                 else return new CResult(false, "Invalid domain price", "CBuyDomainPayload.java", 45);
                 
                 // Check payment amount and currency
                 if (!this.pay.cur.equals("MSK"))
                    return new CResult(false, "Invalid payment", "CBuyDomainPayload.java", 45);
                 
                 // Check payment recipient
                 if (!this.pay.dest.equals(rs.getString("adr")))
                    return new CResult(false, "Invalid payment recipient", "CBuyDomainPayload.java", 45);
                 
                  // Check payment
                 res=this.pay.check(null);
                 if (!res.passed) return res;
                 
                 
                 // Close
                 s.close();
              }
       	      catch (SQLException ex) 
       	      {  
       		UTILS.LOG.log("SQLException", ex.getMessage(), "CBuyDomainPayload.java", 57);
              }
             
             // Return
	    return new CResult(true, "Ok", "CBuyDomainPayload", 67);
       }
       
	
	public CResult commit(CBlockPayload block)
	{	
            // Superclass
	    super.commit(block);
            
	    CResult res=this.check(block);
	    if (res.passed==false) return res;
		     
	    // Transaction
	    this.pay.commit(block);
	    
	     // Transfer domain
	     UTILS.DB.executeUpdate("UPDATE domains "
	     		             + "SET adr='"+this.attach_adr+"', "
                                         + "sale_price='0', "
                                         + "market_bid=0, "
                                         + "market_expires=0, "
                                         + "block='"+this.block+"' "
	     		           + "WHERE domain='"+this.domain+"'");
             
             // Update
             UTILS.ROWHASH.update("domains", "domain", this.domain);
	    
	     // Return 
	     return new CResult(true, "Ok", "CBuyDomainPayload.java", 149);
	}
}