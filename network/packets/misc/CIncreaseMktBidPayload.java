package wallet.network.packets.misc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.*;

public class CIncreaseMktBidPayload  extends CPayload
{
    // Address
    String adr;
    
    // Days
    double mkt_bid;
    
    // Table
    String table;
    
    // Rowhash
    String rowhash;
                                           
    public CIncreaseMktBidPayload(String adr, 
                                  double mkt_bid,
                                  String table,
                                  String rowhash)
    {
        // Constructor
        super(adr);
       
        // Address
        this.adr=adr;
    
        // Days
        this.mkt_bid=mkt_bid;
    
        // Table
        this.table=table;
    
        // Rowhash
        this.rowhash=rowhash;
        
        // Hash
        hash=UTILS.BASIC.hash(this.getHash()+
			      UTILS.FORMAT.format(mkt_bid)+
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
                return new CResult(false, "Invalid address", "CIncreaseMktBidPayload.java", 61);
           
           // Rowhash valid
           if (!UTILS.BASIC.isSHA256(this.rowhash))
               return new CResult(false, "Invalid row hash", "CIncreaseMktBidPayload.java", 61);
           
            // Check hash
            String h=UTILS.BASIC.hash(this.getHash()+
			              UTILS.FORMAT.format(mkt_bid)+
			              table+
                                      rowhash);
           
           if (!h.equals(hash))
              return new CResult(false, "Invalid row hash", "CIncreaseMktBidPayload.java", 61);
           
            // Row exist ?
            Statement s=UTILS.DB.getStatement();
            
            ResultSet rs=s.executeQuery("SELECT * "
  		                        + "FROM  "+this.table+
  		                         " WHERE rowhash='"+this.rowhash+"' "
                                         + "AND adr='"+this.adr+"' "
                                         + "AND mkt_bid>0 "
                                         + "AND expires>0");
            if (!UTILS.DB.hasData(rs))
	    {
	        s.close();
                return new CResult(false, "Invalid rowhash", "CIncreaseMktBidPayload.java", 61);
	    }
            
            // Next
            rs.next();
            
            // New bid is bigger
            if (rs.getDouble("mkt_bid")>this.mkt_bid)
	       return new CResult(false, "Invalid market bid", "CIncreaseMktBidPayload.java", 61);
            
	    s.close();
        }
        catch (SQLException ex)
        {
            UTILS.LOG.log("SQLException", ex.getMessage(), "CIncreaseMktBidPayload.java", 107);
            return new CResult(false, "Unexpected SQL Exception", "CIncreaseMktBidPayload.java", 108);
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
                                       +" SET mkt_bid='"+UTILS.FORMAT.format(this.mkt_bid)+"'"
                                     + " WHERE rowhash='"+this.rowhash+"'");
               
               
            }
            
	    // Return 
	   return new CResult(true, "Ok", "CIncreaseMktBidPayload.java", 149);
	}
}


