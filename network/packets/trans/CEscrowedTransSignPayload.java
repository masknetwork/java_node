// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.trans;

import java.security.SecureRandom;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.CAddress;
import wallet.kernel.CECC;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CPayload;
import wallet.network.packets.blocks.CBlockPayload;

public class CEscrowedTransSignPayload extends CPayload
{
     // Transaction hash
    String trans_hash;
	
    // Type
    String type;
    
    // Serial
   private static final long serialVersionUID = 100L;
	
    public CEscrowedTransSignPayload(String signer, String trans_hash, String type)  throws Exception
    {
        super(signer);
			
        // Receiver
	this.trans_hash=trans_hash;
		
	// Subject
	this.type=type;
		 
        // Hash
	hash=UTILS.BASIC.hash(this.getHash()+
		              this.trans_hash+
			      this.type);
		 
	// Sign
        this.sign();
    }
	
    public void check(CBlockPayload block) throws Exception
    {
        // Check
        super.check(block);
	        
        // Check type ?
	if (!this.type.equals("ID_RELEASE") && 
            !this.type.equals("ID_RETURN"))
	throw new Exception("Invalid signature type - CEscrowedtransSignPayload.java");
	   
        // Load transaction data
        
    	          
        // Finds the user
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM escrowed "
                                          + "WHERE trans_hash='"+this.trans_hash+"'");
           
        // Has data
        if (!UTILS.DB.hasData(rs))
           throw new Exception("Invalid transaction hash - CEscrowedtransSignPayload.java");
           
        // Next
        rs.next();
                
        // Sender
        String sender=rs.getString("sender_adr");
                
        // Receiver
        String receiver=rs.getString("rec_adr");
           
        // Escrower
        String escrower=rs.getString("escrower");
                
        // Amount
        float amount=rs.getFloat("amount");
                
        // Currency
        String cur=rs.getString("cur");
                
        // Check signer for releasing funds
        if (this.type.equals("ID_RELEASE"))
            if (!this.target_adr.equals(sender) && 
                !this.target_adr.equals(escrower)) 
                   throw new Exception("Invalid signer - CEscrowedtransSignPayload.java");
                
        // Check signer for returning funds
        if (this.type.equals("ID_RETURN"))
           if (!this.target_adr.equals(receiver) && 
               !this.target_adr.equals(escrower)) 
               throw new Exception("Invalid signer - CEscrowedtransSignPayload.java");
           
	// Check hash
	String h=UTILS.BASIC.hash(this.getHash()+
		                  this.trans_hash+
			          this.type);
	         
        // Invalid hash
        if (!this.hash.equals(h))
	   throw new Exception("Invalid hash - CEscrowedtransSignPayload.java");
                 
        // Release ?
        if (this.type.equals("ID_RELEASE"))
        UTILS.ACC.newTrans(receiver, 
                           sender,
                           amount,
                           true,
                           cur, 
                           "Funds have been released by escrower / sender", 
                           escrower, 
                           this.hash, 
                           this.block,
                           block,
                           0);
                 
        // Return ?
        if (this.type.equals("ID_RETURN"))
        UTILS.ACC.newTrans(sender, 
                           receiver,
                           amount,
                           true,
                           cur, 
                           "Funds have been returned to you by escrower / sender", 
                           escrower, 
                           this.hash, 
                           this.block,
                           block,
                           0);
                 
        // Close
        
    }
	   
    public void commit(CBlockPayload block) throws Exception
    {
        // Check payload
        this.check(block);
              
        // Superclass
	super.commit(block);
               
        // Clear transactions
        UTILS.ACC.clearTrans(hash, "ID_ALL", this.block);
               
        // Delete transaction
        UTILS.DB.executeUpdate("DELETE FROM escrowed "
                                    + "WHERE trans_hash='"+this.trans_hash+"'");
    }
}


