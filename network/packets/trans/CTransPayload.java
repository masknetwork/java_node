package wallet.network.packets.trans;

import java.security.SecureRandom;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.crypto.SealedObject;
import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.blocks.*;
import wallet.network.packets.trans.details.*;

public class CTransPayload extends CPayload 
{
	// Source
	public String src="";
	
	// Destination
	public String dest="";
	
	// Amount
	public double amount=0.0001;
	
	// Currency
	public String cur="MSK";
	
	// Escrower
	public String escrower="";
        
        // Message
        public CMesDetails mes=null;
        
        
	public CTransPayload(String src, 
                             String dest, 
                             double amount, 
                             String cur, 
                             String escrower, 
                             String otp_old_pass, 
                             String otp_new_pass)
        {
           // Constructo
           super(src);
           
	   // Source
	   this.src=src;
		
           // Destination
	   this.dest=dest;
		
	   // Amount
	   this.amount=amount;
		
	   // Currency
	   this.cur=cur;
                
           // Escrower
           this.escrower=escrower;
           
	   hash=UTILS.BASIC.hash(this.getHash()+
				    this.src+
                                    this.dest+
                                    UTILS.FORMAT.format(this.amount)+
                                    this.cur+
                                    this.escrower);
            
	    // Sign
	    this.sign();
       }
        
        public void addMes(String mes)
        {
            this.mes=new CMesDetails(mes, this.dest);
            
            // Hash
	   hash=UTILS.BASIC.hash(this.getHash()+
				 this.src+
                                 this.dest+
                                 UTILS.FORMAT.format(this.amount)+
                                 this.cur+
                                 this.escrower+
                                 this.mes.hash);
		   
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
	    
	    // Check source
	    if (!UTILS.BASIC.addressExist(this.src))
	    	return new CResult(false, "Invalid source address.", "CTransPayload", 77);
	    
            // Source frozen ?
            if (UTILS.BASIC.hasAttr(this.src, "ID_FROZEN"))
               return new CResult(false, "Source address is frozen.", "CTransPayload", 77);
            
	    // Check dest
	    if (!UTILS.BASIC.adressValid(this.dest))
	    	return new CResult(false, "Invalid dest address.", "CTransPayload", 77);
	    
	    // Check amount
            if (this.cur.equals("MSK"))
            {
	      if (this.amount<0.0001)
	      	  return new CResult(false, "Invalid amount.", "CTransPayload", 77);
            }
            else
            {
                if (this.amount<0.00000001)
	      	  return new CResult(false, "Invalid amount.", "CTransPayload", 77);
            }
            
            // Hash
            String h;
            if (this.mes!=null)
            {
                h=UTILS.BASIC.hash(this.getHash()+
				 this.src+
                                 this.dest+
                                 UTILS.FORMAT.format(this.amount)+
                                 this.cur+
                                 this.escrower+
                                 this.mes.hash);
            }
            else
            {
               h=UTILS.BASIC.hash(this.getHash()+
				 this.src+
                                 this.dest+
                                 UTILS.FORMAT.format(this.amount)+
                                 this.cur+
                                 this.escrower);
            }
            
            // Check hash
            if (!this.hash.equals(h))
               return new CResult(false, "Invalid hash", "CTransPayload", 77);
            
            // Statement
            Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                   
	    // Check cur
	    if (!this.cur.equals("MSK"))
            {
               // Symbol valid ?
               if (!UTILS.BASIC.symbolValid(this.cur))
                  return new CResult(false, "Invalid currency", "CTransPayload", 77);
            }
            
            // Check escrower
	    if (!this.escrower.equals(""))
	    {
	        if (!UTILS.BASIC.adressValid(this.escrower))
	        	return new CResult(false, "Invalid escrower.", "CTransPayload", 77);
	    }
	    	
	    // Check source balance
            double balance=UTILS.NETWORK.TRANS_POOL.getBalance(this.src, this.cur);
	    if (balance<this.amount)
	    	return new CResult(false, "Insufficient funds to execute transaction ("+this.amount+")", "CTransPayload", 77);
	    
            // Insert pending transaction
	    UTILS.BASIC.newTrans(this.src, 
                                 -this.amount,
                                 true,
                                 this.cur, 
                                 "Transaction to another address "+this.src, 
                                 this.escrower, 
                                 this.hash, 
                                 this.block);
                
            // To destination
            if (this.escrower.equals("") && !UTILS.BASIC.hasAttr(this.src, "ID_MULTISIG"))
            UTILS.BASIC.newTrans(this.dest, 
                                 this.amount,
                                 true,
                                 this.cur, 
                                 "Transaction from address "+this.src, 
                                 this.escrower, 
                                 this.hash, 
                                 this.block);
            
            if (UTILS.WALLET.isMine(this.dest))
            {
               // Message
               if (this.mes!=null) this.mes.getMessage(this.dest, hash);
               
               // IPN
              this.checkIPN("unconfirmed");
            }
            }
            catch (SQLException ex) 
       	    {  
       	       UTILS.LOG.log("SQLException", ex.getMessage(), "CTransPayload.java", 57);
            }
              
	    // Return
	    return new CResult(true, "Ok", "CTransPayload", 164);
        }
         
         public void checkIPN(String status)
         {
             try
             {
                // Load details
                Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    
                 // IPN
                 ResultSet rs=s.executeQuery("SELECT * FROM ipn WHERE adr='"+this.dest+"'");
                 
                 if (UTILS.DB.hasData(rs) && UTILS.WALLET.isMine(this.dest))
                 {
                   // Next
                   rs.next();
                   
                   // creates loader thread
                   CLoader loader=new CLoader(rs.getString("web_link"), rs.getString("web_pass"));
                   
                   // Status
                   loader.addParam("status", status);
                   
                   // Source
                   loader.addParam("src", this.src);
                   
                   // Dest
                   loader.addParam("dest", this.dest);
                   
                   // Amount
                   loader.addParam("amount", String.valueOf(this.amount));
                   
                   // Currency
                   loader.addParam("currency", this.cur);
                   
                   // transaction hash
                   loader.addParam("tx_hash", this.hash);
                   
                   // Block
                   loader.addParam("block", String.valueOf(this.block));
                   
                   // Start
                   loader.start();
                 }
             }
             catch (SQLException ex) 
       	     {  
       	       UTILS.LOG.log("SQLException", ex.getMessage(), "CTransPayload.java", 57);
             }
         }
         
         public CResult commit(CBlockPayload block)
	 { 
             try
             {
                // Take coins
                UTILS.BASIC.clearTrans(hash, "ID_SEND");
                
                 // IPN
                 this.checkIPN("confirmed");
              
                // Multisig address ?
                if (UTILS.BASIC.hasAttr(this.src, "ID_MULTISIG"))
                {
                     // Load details
                     Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                
                    // Load data
                    ResultSet rs=s.executeQuery("SELECT * "
                                                + "FROM adr_options "
                                               + "WHERE adr='"+this.src+"' "
                                                 + "AND op_type='ID_MULTISIG'");
                    
                    // Next 
                    rs.next();
                    
                    // Signers
                    String signer_1=rs.getString("par_1");
                    String signer_2=rs.getString("par_2");
                    String signer_3=rs.getString("par_3");
                    String signer_4=rs.getString("par_4");
                    String signer_5=rs.getString("par_5");
                    
                    // Minimum signers
                    int min=rs.getInt("par_6");
                    
                    // Insert escrowed
                    UTILS.DB.executeUpdate("INSERT INTO multisig(trans_hash, "
                                                              + "sender_adr, "
                                                              + "rec_adr, "
                                                              + "signer_1, "
                                                              + "sign_1, "
                                                              + "signer_2, "
                                                              + "sign_2, "
                                                              + "signer_3, "
                                                              + "sign_3, "
                                                              + "signer_4, "
                                                              + "sign_4, "
                                                              + "signer_5, "
                                                              + "sign_5, "
                                                              + "amount, "
                                                              + "cur, "
                                                              + "required, "
                                                              + "block) VALUES('"
                                                              +this.hash+"', '"
                                                              +this.src+"', '"
                                                              +this.dest+"', '"
                                                              +signer_1+"', '', '"
                                                              +signer_2+"', '', '"
                                                              +signer_3+"', '', '"
                                                              +signer_4+"', '', '"
                                                              +signer_5+"', '', '"
                                                              +this.amount+"', '"
                                                              +this.cur+"', '"
                                                              +min+"', '"
                                                              +this.block+"')");
                    
                    // Source my address ?
                    if (UTILS.WALLET.isMine(this.src))
                    {
                          UTILS.DB.executeUpdate("UPDATE web_users "
                                               + "SET unread_multisig=unread_multisig+1 "
                                             + "WHERE ID='"+UTILS.BASIC.getAdrUserID(this.src)+"' ");
                    }
                    
                    // Destination my address ?
                    if (UTILS.WALLET.isMine(this.dest))
                    {
                          UTILS.DB.executeUpdate("UPDATE web_users "
                                               + "SET unread_multisig=unread_multisig+1 "
                                             + "WHERE ID='"+UTILS.BASIC.getAdrUserID(this.dest)+"' ");
                    }
                    
                    // Signer 1 my address ?
                    if (UTILS.WALLET.isMine(signer_1))
                    {
                          UTILS.DB.executeUpdate("UPDATE web_users "
                                               + "SET unread_multisig=unread_multisig+1 "
                                             + "WHERE ID='"+UTILS.BASIC.getAdrUserID(signer_1)+"' ");
                    }
                    
                    // Signer 2 my address ?
                    if (UTILS.WALLET.isMine(signer_2))
                    {
                          UTILS.DB.executeUpdate("UPDATE web_users "
                                               + "SET unread_multisig=unread_multisig+1 "
                                             + "WHERE ID='"+UTILS.BASIC.getAdrUserID(signer_2)+"' ");
                    }
                    
                    // Signer 3 my address ?
                    if (UTILS.WALLET.isMine(signer_3))
                    {
                          UTILS.DB.executeUpdate("UPDATE web_users "
                                               + "SET unread_multisig=unread_multisig+1 "
                                             + "WHERE ID='"+UTILS.BASIC.getAdrUserID(signer_3)+"' ");
                    }
                    
                    // Signer 4 my address ?
                    if (UTILS.WALLET.isMine(signer_4))
                    {
                          UTILS.DB.executeUpdate("UPDATE web_users "
                                               + "SET unread_multisig=unread_multisig+1 "
                                             + "WHERE ID='"+UTILS.BASIC.getAdrUserID(signer_4)+"' ");
                    }
                    
                    // Signer 5 my address ?
                    if (UTILS.WALLET.isMine(signer_5))
                    {
                          UTILS.DB.executeUpdate("UPDATE web_users "
                                               + "SET unread_multisig=unread_multisig+1 "
                                             + "WHERE ID='"+UTILS.BASIC.getAdrUserID(signer_5)+"' ");
                    }
                }
                else
                {
                   // Deposit coins
                   if (this.escrower.equals(""))
                   {
                      // Clear transaction for receiver
                      UTILS.BASIC.clearTrans(hash, "ID_RECEIVE");
                
                      // New transaction
                      if (UTILS.WALLET.isMine(this.dest))
                      UTILS.DB.executeUpdate("UPDATE web_users "
                                              + "SET unread_trans=unread_trans+1 "
                                            + "WHERE ID='"+UTILS.BASIC.getAdrUserID(this.dest)+"' ");
                   }
                   else
                   {
                      UTILS.DB.executeUpdate("INSERT INTO escrowed(trans_hash, "
                                                              + "sender_adr, "
                                                              + "rec_adr, "
                                                              + "escrower, "
                                                              + "amount, "
                                                              + "cur, "
                                                              + "block) VALUES ('"
                                                              +this.hash+"', '"
                                                              +this.src+"', '"
                                                              +this.dest+"', '"
                                                              +this.escrower+"', '"
                                                              +this.amount+"', '"
                                                              +this.cur+"', '"
                                                              +this.block+"')");
                   
                      // Source my address ?
                      if (UTILS.WALLET.isMine(this.src))
                      {
                          UTILS.DB.executeUpdate("UPDATE web_users "
                                               + "SET unread_esc=unread_esc+1 "
                                             + "WHERE ID='"+UTILS.BASIC.getAdrUserID(this.src)+"' ");
                      }
                   
                      // Destination my address ?
                      if (UTILS.WALLET.isMine(this.dest))
                      {
                          UTILS.DB.executeUpdate("UPDATE web_users "
                                               + "SET unread_esc=unread_esc+1 "
                                             + "WHERE ID='"+UTILS.BASIC.getAdrUserID(this.dest)+"' ");
                      }
                   
                      // Escrower my address ?
                      if (UTILS.WALLET.isMine(this.escrower))
                      {
                          UTILS.DB.executeUpdate("UPDATE web_users "
                                               + "SET unread_esc=unread_esc+1 "
                                             + "WHERE ID='"+UTILS.BASIC.getAdrUserID(this.escrower)+"' ");
                      }
                   }
                }
             }
             catch (SQLException ex) 
       	     {  
       	       UTILS.LOG.log("SQLException", ex.getMessage(), "CTransPayload.java", 57);
             }
             
             // Ok
	     return new CResult(true, "Ok", "CTransPayload", 130);
	}
	 
}
