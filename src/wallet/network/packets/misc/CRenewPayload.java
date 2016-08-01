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
                                   long days,
                                   String table,
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
           
        // Address
        if (!UTILS.BASIC.adressValid(this.adr))
           throw new Exception("Invalid address - CRenewPayload.java");
           
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
    }
}


