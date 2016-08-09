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

public class CRenewPayload extends CPayload
{
    // Address
    String adr;
    
    // Days
    long days;
    
    // Table
    String table;
    
    // Rowhash
    String rowhash;
    
    // Serial
   private static final long serialVersionUID = 100L;
                                           
    public CRenewPayload(String adr, 
                         String table,
                         long days,
                         String rowhash) throws Exception
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
    
     public void check(CBlockPayload block) throws Exception
     {
        // Constructor
        super.check(block);
        
        // Check rowhash
        if (!UTILS.BASIC.isHash(this.rowhash))
            throw new Exception("Invalid rowhash - CRenewPayload.java");
           
        // Table valid ?
        if (!this.table.equals("ads") && 
            !this.table.equals("agents") && 
            !this.table.equals("assets") && 
            !this.table.equals("assets_mkts") && 
            !this.table.equals("assets_mkts_pos") && 
            !this.table.equals("domains") && 
            !this.table.equals("feeds") && 
            !this.table.equals("feeds_branches"))
        throw new Exception("Invalid table");
        
        // Load data
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM "+this.table+" "
                                          + "WHERE rowhash='"+this.rowhash+"'");
        
        // Has data
        if (!UTILS.DB.hasData(rs))
           throw new Exception("Invalid rowhash - CRenewPayload.java");
        
        // Load data
        rs.next();
        
        // Owner ?
        if (!rs.getString("adr").equals(this.target_adr))
           throw new Exception("Invalid owner - CRenewPayload.java");
        
        // Check hash
        String h=UTILS.BASIC.hash(this.getHash()+
			             String.valueOf(days)+
			             table+
                                     rowhash);
           
        if (!h.equals(hash))
           throw new Exception("Invalid hash - CRenewPayload.java");
         
    }
	 
    public void commit(CBlockPayload block) throws Exception
    {
        // Superclass
	super.commit(block);
        
        // Renew
        UTILS.DB.executeUpdate("UPDATE "+this.table+" "
                                + "SET expire=expire+"+(this.days*1440)+" "
                              + "WHERE rowhash='"+this.rowhash+"'");
    }
}


