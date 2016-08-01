// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.mes;

import java.security.SecureRandom;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.agents.CAgent;
import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.trans.*;
import wallet.network.packets.blocks.*;

public class CMesPayload extends CPayload 
{
    // Receiver
	String receiver_adr;
	
	// Subj
	String subj;
	
	// Mes
	String mes;
	
	// Key
	String key;
        
        // Serial
   private static final long serialVersionUID = 100L;
	
	public CMesPayload(String sender_adr, 
			           String receiver_adr, 
			           String subj, 
			           String mes)  throws Exception
	{
            // Constructor
            super(sender_adr);
	         
            // Receiver
            this.receiver_adr=receiver_adr;
		
	    // Subject
            this.subj=subj;
		 
            // Message
	    this.mes=mes;
		 
            // Statement 
            
                 
            // Contract ?
            ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                             + "FROM agents "
                                            + "WHERE adr='"+receiver_adr+"'");
                 
            // Has data
            if (UTILS.DB.hasData(rs))
            {
                     this.subj=subj;
                     this.mes=mes;
            }
            else
            {
		// Generates a key
		String k=UTILS.BASIC.randString(25);
                 
		this.subj=UTILS.AES.encrypt(subj, k);
		this.mes=UTILS.AES.encrypt(mes, k);
		    
		// Encrypt key
		CECC ecc=new CECC(receiver_adr);
	        this.key=ecc.encrypt(k);
            }
                 
            // Close
            
                 
            // Hash
	    hash=UTILS.BASIC.hash(this.getHash()+
				  this.receiver_adr+
				  this.subj+
				  this.mes+
				  this.key);
                    
	    // Sign
	    this.sign();
        }
	
	
	public void check(CBlockPayload block) throws Exception
	{
            // Constructor
            super.check(block);
	    	
	    // Check receiver ?
	    if (UTILS.BASIC.adressValid(this.receiver_adr)==false)
	   	throw new Exception("Invalid receiver address - CMesPayload.java");
	    
	    // Check hash
	    String h=UTILS.BASIC.hash(this.getHash()+
		                      this.receiver_adr+
		                      this.subj+
		                      this.mes+
		                      this.key);
	    
            if (!this.hash.equals(h))
		throw new Exception("Invalid hash - CMesPayload.java");
	   
	    // Statement 
            
                 
            // Contract ?
            ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                        + "FROM agents "
                                       + "WHERE adr='"+receiver_adr+"'");
                 
            // Has data
            if (!UTILS.DB.hasData(rs))
            {
	        // Insert message
                if (UTILS.WALLET.isMine(this.receiver_adr)==true && block==null)
                {
    	            // Decrypt key
    	            CAddress adr=UTILS.WALLET.getAddress(this.receiver_adr);
    	            String dec_key=adr.decrypt(this.key);
    	   
    	            // Decrypt subject
    	            String dec_subject=UTILS.AES.decrypt(subj, dec_key);
		   
    	            // Decrypt message
    	            String dec_mes=UTILS.AES.decrypt(mes, dec_key);
    	   
    	            UTILS.DB.executeUpdate("INSERT INTO mes(from_adr, "
    	   		                                  + "to_adr, "
    	   		                                  + "subject, "
    	   		                                  + "mes, "
    	   		                                  + "status, "
    	   		                                  + "tstamp, "
                                                          + "tgt)"
    	   		                                  + "VALUES ('"+
    	                                                  this.target_adr+"', '"+
    	   	                                          this.receiver_adr+"', '"+
    	     	                                          UTILS.BASIC.base64_encode(dec_subject)+"', '"+
    	   		                                  UTILS.BASIC.base64_encode(dec_mes)+"', '"+
                                                          "0', '"+
    	   		                                  String.valueOf(UTILS.BASIC.tstamp())+"', "
                                                          + "'0')");
                     }
    	    }
        }
	   
	public void commit(CBlockPayload block) throws Exception
	{
	    // Superclass
	    super.commit(block);
            
            // Statement 
            
                 
            // Contract ?
            ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                        + "FROM agents "
                                       + "WHERE adr='"+receiver_adr+"'");
            
            // Has data
            if (UTILS.DB.hasData(rs))
            {
                // Next
                rs.next();
                     
                // Load message
                CAgent AGENT=new CAgent(rs.getLong("aID"), false, this.block);
                    
                // Set Message 
                AGENT.VM.SYS.EVENT.loadMessage(this.target_adr,
                                               this.subj,
                                               this.mes, 
                                               this.hash);
                    
                // Execute
                AGENT.execute("#message#", false, this.block);
            }
            
	}
    }

