// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.trans;

import java.security.SecureRandom;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.crypto.SealedObject;
import wallet.agents.CAgent;
import wallet.agents.VM.VM;
import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.blocks.*;

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
        
        // Serial
        private static final long serialVersionUID = 100L;
        
        
	public CTransPayload(String src, 
                             String dest, 
                             double amount, 
                             String cur, 
                             String mes,
                             String escrower) throws Exception
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
           
           // Message 
           String mes_hash="";
           if (!mes.equals("")) 
           {
                this.mes=new CMesDetails(mes, this.dest);
                mes_hash=this.mes.hash;
           }
           
           // Digest
           this.hash=UTILS.BASIC.hash(this.getHash()+
		                        this.src+
                                        this.dest+
                                        this.amount+
                                        this.cur+
                                        this.escrower+
                                        mes_hash); 
           
          // Sign
          this.sign();
       }
        
       
	 public void check(CBlockPayload block) throws Exception
	 {
             // Super class
	     super.check(block);
	     
	     // Check source
	     if (!UTILS.BASIC.addressExist(this.src))
	    	     throw new Exception("Invalid source address, CTransPayload");
                 
             // Free address
	     if (!UTILS.BASIC.canSpend(this.src, this.block))
                     throw new Exception("Invalid source address, CTransPayload");
                 
             // Check dest
	     if (!UTILS.BASIC.adressValid(this.dest))
	        throw new Exception("Invalid destination address, CTransPayload");
             
             // Source and destination the same
             if (this.src.equals(this.dest))
	         throw new Exception("Source and destination address can't be the same, CTransPayload");
             
             // Check amount
             if (this.cur.equals("MSK"))
             {
	        if (this.amount<0.0001)
	      	   throw new Exception("Invalid amount, CTransPayload");
             }
             else
             {
                if (this.amount<0.00000001)
	      	   throw new Exception("Invalid amount, CTransPayload");
             }
            
             // Message included ?
             if (this.mes!=null)
             {
                if (!this.mes.check())
                   throw new Exception("Invalid message, CTransPayload");
             }
            
            // Message 
            String mes_hash="";
            if (this.mes!=null)
            {
               // Check message
               if (!mes.check())
                  throw new Exception("Invalid message, CTransPayload");
               
               // Hash
               mes_hash=this.mes.hash;
            }
            
            // Digest
            String h=UTILS.BASIC.hash(this.getHash()+
		                            this.src+
                                            this.dest+
                                            this.amount+
                                            this.cur+
                                            this.escrower+
                                            mes_hash); 
                  
            // Hash format
            if (!UTILS.BASIC.isHash(this.hash))
               throw new Exception("Invalid hash, CTransPayload");
                  
            // Check hash
            if (!this.hash.equals(h))
               throw new Exception("Invalid hash, CTransPayload");
            
            // Check cur
	    if (!this.cur.equals("MSK"))
            {
                // Symbol valid ?
                if (!UTILS.BASIC.isSymbol(this.cur))
                   throw new Exception("Invalid currency, CTransPayload");
                     
                // Asset exist ?
                if (!UTILS.BASIC.isAsset(this.cur))
                   throw new Exception("Invalid currency, CTransPayload");
            }
            
            // Check escrower
	    if (!this.escrower.equals(""))
	    {
	        if (!UTILS.BASIC.adressValid(this.escrower))
	           throw new Exception("Invalid currency, CTransPayload");
	    }
	    
	    // Check source balance
            double balance=UTILS.ACC.getBalance(this.src, this.cur, block);
                  
            // Insufficient funds
            if (balance<this.amount) 
	       throw new Exception("Insuficient funds, CTransPayload.java");
	    
            // Insert pending transaction
	    UTILS.ACC.newTrans(this.src, 
                                 this.dest,
                                 -this.amount,
                                 true,
                                 this.cur, 
                                 "Transaction to another address "+this.src, 
                                 this.escrower, 
                                 this.hash, 
                                 this.block,
                                 block,
                                 0);
                
            // To destination
            if (this.escrower.equals(""))
            UTILS.ACC.newTrans(this.dest, 
                                 this.src,
                                 this.amount,
                                 true,
                                 this.cur, 
                                 "Transaction from address "+this.src, 
                                 this.escrower, 
                                 this.hash, 
                                 this.block,
                                 block,
                                 0);
            
            if (UTILS.WALLET.isMine(this.dest))
            {
                // Message
                if (this.mes!=null) this.mes.getMessage(this.dest, hash);
              
                // IPN
                this.checkIPN("unconfirmed");
            }
       
        }
         
         public void checkIPN(String status) throws Exception
         {
            // Load details
            
                    
            // IPN
            ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                        + "FROM ipn "
                                       + "WHERE adr='"+this.dest+"'");
                 
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
            
            // Close
            
         }
         
         public void commit(CBlockPayload block) throws Exception
	 { 
             // Load details
                
                
                if (UTILS.BASIC.isContractAdr(this.dest))
                {
                    // Result set
                    ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                                       + "FROM agents "
                                                      + "WHERE adr='"+this.dest+"' "
                                                        + "AND status='ID_ONLINE'");
                    
                    // Next
                    rs.next();
                    
                    // Load VM
                    CAgent AGENT=new CAgent(rs.getLong("aID"), false, this.block);
                    
                    // Message ?
                    String message="";
                    if (this.mes!=null) 
                       message=this.mes.mes;
                    
                    // Set transaction
                    AGENT.VM.SYS.EVENT.loadTrans(this.src,
                                                 this.amount, 
                                                 this.cur, 
                                                 message,
                                                 this.escrower, 
                                                 this.hash);
                    
                    // Execute
                    AGENT.execute("#transaction#", false, this.block);
                    
                    // Refund ?
                    if (!AGENT.transAproved()) 
                        throw new Exception("Rejected by application, CTransPayload.java");
                }
                
                // Take coins
                UTILS.ACC.clearTrans(hash, "ID_SEND", this.block);
                
                // Deposit coins
                if (this.escrower.equals(""))
                {
                    // Clear transaction for receiver
                    UTILS.ACC.clearTrans(hash, "ID_RECEIVE", this.block);
                }
                else
                {
                      UTILS.DB.executeUpdate("INSERT INTO escrowed "
                                                   + "SET trans_hash='"+this.hash+"', "
                                                        + "sender_adr='"+this.src+"', "
                                                        + "rec_adr='"+this.dest+"', "
                                                        + "escrower='"+this.escrower+"', "
                                                        + "amount='"+this.amount+"', "
                                                        + "cur='"+this.cur+"', "
                                                        + "block='"+this.block+"'");
                }
                
                  
             
	}
	 
}