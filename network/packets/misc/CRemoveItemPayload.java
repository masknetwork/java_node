// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.misc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.*;

public class CRemoveItemPayload extends CPayload
{
    // Owner address
    String adr;
    
    // Table
    String table;
    
    // Rowhash
    String rowhash;
    
    public CRemoveItemPayload(String adr, 
                              String table, 
                              String rowhash) throws Exception
    {
       // Constructor
       super(adr);
       
       // Owner address
       this.adr=adr;
    
       // Table
       this.table=table;
    
       // Rowhash
       this.rowhash=rowhash;
       
       // Hash
       hash=UTILS.BASIC.hash(this.getHash()+
			     adr+
			     table+
                             rowhash);
		
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
	   
           // Address
           if (!UTILS.BASIC.adressValid(this.adr))
                return new CResult(false, "Invalid address", "CRemoveItemPayload.java", 61);
           
           // Rowhash valid
           if (!UTILS.BASIC.isSHA256(this.rowhash))
               return new CResult(false, "Invalid row hash", "CRemoveItemPayload.java", 61);
           
           // Check hash
           String h=UTILS.BASIC.hash(this.getHash()+
			             this.adr+
			             this.table+
                                     this.rowhash);
           
           if (!h.equals(hash))
              return new CResult(false, "Invalid row hash", "CRemoveItemPayload.java", 61);
           
            // Record exist ?
            Statement s=UTILS.DB.getStatement();
           
            ResultSet rs=s.executeQuery("SELECT * "
  		                        + "FROM  "+this.table+
  		                         " WHERE rowhash='"+this.rowhash+"' "
                                         + "AND adr='"+this.adr+"'");
            if (!UTILS.DB.hasData(rs))
	    {
	        rs.close(); s.close();
                return new CResult(false, "Invalid rowhash", "CRemoveItemPayload.java", 61);
	    }
			
	    rs.close(); s.close();
        }
        catch (SQLException ex)
        {
            UTILS.LOG.log("SQLException", ex.getMessage(), "CRemoveItemPayload.java", 79);
        }
            
        // Return
	return new CResult(true, "Ok", "CRemoveItemPayload", 67);
    }
	
	public CResult commit(CBlockPayload block) throws Exception
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
	       UTILS.DB.executeUpdate("DELETE FROM "+this.table+" "
                                     + "WHERE rowhash='"+this.rowhash+"'");
            }
            
	    // Return 
	   return new CResult(true, "Ok", "CRemoveItemPayload.java", 149);
	}
}
