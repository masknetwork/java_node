// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.mes;

import java.security.SecureRandom;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
		 
            // Generates a key
            String k=UTILS.BASIC.randString(25);
            
            // Subject and message
	    this.subj=UTILS.AES.encrypt(subj, k);
	    this.mes=UTILS.AES.encrypt(mes, k);
		    
	    // Encrypt key
	    CECC ecc=new CECC(receiver_adr);
	    this.key=ecc.encrypt(k);
               
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
	    if (UTILS.BASIC.isAdr(this.receiver_adr)==false)
	   	throw new Exception("Invalid receiver address - CMesPayload.java");
	    
            // Subject size
            if (this.subj.length()>250)
                throw new Exception("Invalid subject length - CMesPayload.java");
            
            // Subject size
            if (this.subj.length()>2500)
                throw new Exception("Invalid message length - CMesPayload.java");
            
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
    	   
    	        UTILS.DB.executeUpdate("INSERT INTO mes "
                                             + "SET from_adr='"+this.target_adr+"', "
    	   		                         + "to_adr='"+this.receiver_adr+"', "
    	   		                         + "subject='"+UTILS.BASIC.base64_encode(dec_subject)+"', "
    	   		                         + "mes='"+UTILS.BASIC.base64_encode(dec_mes)+"', "
                                                 + "status='0', "
    	                                         + "tstamp='"+String.valueOf(UTILS.BASIC.tstamp())+"', "
                                                 + "tgt='0'");
            }
           
            // Check hash
	    String h=UTILS.BASIC.hash(this.getHash()+
		                      this.receiver_adr+
		                      this.subj+
		                      this.mes+
		                      this.key);
	    
            if (!this.hash.equals(h))
		throw new Exception("Invalid hash - CMesPayload.java");
        }
	   
	public void commit(CBlockPayload block) throws Exception
	{
	    // Superclass
	    super.commit(block);
        }
    }

