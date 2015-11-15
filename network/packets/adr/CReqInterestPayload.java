package wallet.network.packets.adr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CPayload;
import wallet.network.packets.blocks.CBlockPayload;

public class CReqInterestPayload extends CPayload
{
     // Info type
     String adr;
	
    public CReqInterestPayload(String adr)
    {
    	// Superclass
  	super(adr);
  	   
  	// Escrow address
  	this.adr=adr;
  	      
  	// Hash
   	hash=UTILS.BASIC.hash(this.getHash()+
   			      this.adr);
        
        // Sign
        this.sign();
    }
    
    public CResult check(CBlockPayload block)
    {
    	// Super class
    	CResult res=super.check(block);
    	if (res.passed==false) return res;
    	
    	// Target address valid
    	if (UTILS.BASIC.adressValid(this.target_adr)==false) 
    		return new CResult(false, "Invalid target address", "CReqInterestPayload", 47);
    	
        try
        {
    	   // Statement
           Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
           
           // Load address data
           ResultSet rs=s.executeQuery("SELECT * "
                                       + "FROM adr "
                                      + "WHERE adr='"+this.adr+"'");
           
           // Next
           rs.next();
           
           // Last interest
           long last_interest=rs.getLong("last_interest");
           
           // Balance
           double adr_balance=rs.getDouble("balance");
           
           // Can receive interest
           if (UTILS.BASIC.block()-last_interest<1140)
               return new CResult(false, "Invalid interest date", "CReqInterestPayload", 47);
           
           // Load default address data
           rs=s.executeQuery("SELECT * "
                             + "FROM adr "
                            + "WHERE adr='default'");
           
           // Next
           rs.next();
           
           // Balance
           double balance=rs.getDouble("balance");
           
           // Free cash
           long free_cash=Math.round(100000000-balance);
           
           // Interest
           double p=Math.round(100000000/free_cash);
           
           // Amount to pay
           double amount=adr_balance*p/36500;
           
           // Takes money from default address
           UTILS.BASIC.newTrans("default", 
                                this.adr,
                                -amount, 
                                false,
                                "MSK", 
                                "Interest was paid to address "+this.adr, 
                                "", 
                                hash, 
                                this.block);
           
           // Sends money from target address
           UTILS.BASIC.newTrans(this.adr, 
                                "default",
                                amount, 
                                false,
                                "MSK", 
                                "Interest was received", 
                                "", 
                                hash, 
                                this.block);
           
           // Close
           s.close();
        }
        catch (SQLException ex) 
       	{  
            UTILS.LOG.log("SQLException", ex.getMessage(), "CReqInterestPayload.java", 57);
        }
        
        
  	// Hash
    	String h=UTILS.BASIC.hash(this.getHash()+
    		              	  this.adr);
    	
        // Invalid hash
        if (!this.hash.equals(h))
    		return new CResult(false, "Invalid hash", "CReqInterestPayload", 67);
    	
  	// Return
  	return new CResult(true, "Ok", "CReqInterestPayload", 63);
    }
    
    public CResult commit(CBlockPayload block)
    {
        // Check parent
        CResult res=this.check(block);
        if (res.passed==false) return res;
 	  
        // Superclass
        super.commit(block);
           
        // Clear
        UTILS.BASIC.clearTrans(hash, "ID_ALL");
        
        // Last interest
        try
        {
    	   // Statement
           Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, 
                                                    ResultSet.CONCUR_READ_ONLY);
        
           // Load address data
           s.executeUpdate("UPDATE adr "
                               + "SET last_interest='"+UTILS.BASIC.block()
                            +"' WHERE adr='"+this.adr+"'");
           
           // Close
           s.close();
           
        }
        catch (SQLException ex) 
       	{  
            UTILS.LOG.log("SQLException", ex.getMessage(), "CReqInterestPayload.java", 57);
        }
        
        // Rowhash
        UTILS.ROWHASH.update("adr", "adr", this.adr);
         
    	// Return 
    	return new CResult(true, "Ok", "CReqInterestPayload", 70);
     }
}
