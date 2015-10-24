package wallet.network.packets.misc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.*;

public class CIncreaseMktDaysPayload extends CPayload
{
    // Address
    String adr;
    
    // Days
    long days;
    
    // Table
    String table;
    
    // Rowhash
    String rowhash;
                                           
    public CIncreaseMktDaysPayload(String adr, 
                                   long days,
                                   String table,
                                   String rowhash)
    {
        // Constructor
        super(adr);
       
        // Address
        this.adr=adr;
    
        // Days
        this.days=days;
    
        // Table
        this.table=table;
    
        // Rowhash
        this.rowhash=rowhash;
        
        // Hash
        hash=UTILS.BASIC.hash(this.getHash()+
			      String.valueOf(days)+
			      table+
                              rowhash);
		
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
	   
           // Address
           if (!UTILS.BASIC.adressValid(this.adr))
                return new CResult(false, "Invalid address", "CRemoveItemPayload.java", 61);
           
           // Rowhash valid
           if (!UTILS.BASIC.isSHA256(this.rowhash))
               return new CResult(false, "Invalid row hash", "CRemoveItemPayload.java", 61);
           
            // Check hash
           String h=UTILS.BASIC.hash(this.getHash()+
			             String.valueOf(days)+
			             table+
                                     rowhash);
           
           if (!h.equals(hash))
              return new CResult(false, "Invalid row hash", "CRemoveItemPayload.java", 61);
           
            // Domain owned by address ?
            Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs=s.executeQuery("SELECT * "
  		                        + "FROM  "+this.table+
  		                         "WHERE rowhash='"+this.rowhash+"' "
                                         + "AND adr='"+this.adr+"' "
                                         + "AND mkt_bid>0 "
                                         + "AND expires>0");
            if (!UTILS.DB.hasData(rs))
	    {
	        s.close();
                return new CResult(false, "Invalid rowhash", "CRemoveItemPayload.java", 61);
	    }
			
	    s.close();
        }
        catch (SQLException ex)
        {
            UTILS.LOG.log("SQLException", ex.getMessage(), "CRemoveItemPayload.java", 79);
        }
            
        // Return
	return new CResult(true, "Ok", "CRemoveItemPayload", 67);
    }
	
	public CResult commit(CBlockPayload block)
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
	       UTILS.DB.executeUpdate("UPDATE "+this.table
                                       +" SET expires=expires+"+(this.days*864)
                                     + "WHERE rowhash='"+this.rowhash+"'");
               
               // Rowhash
               UTILS.ROWHASH.update(table, rowhash, this.rowhash);
            }
            
	    // Return 
	   return new CResult(true, "Ok", "CRemoveItemPayload.java", 149);
	}
}


