// Author : Vlad Cristian
// Contact : vcris@gmx.com

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

public class CTransPayload extends CPayload 
{
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
	     
	     // Free address
             if (UTILS.BASIC.isSpecMktAddress(this.target_adr))
                throw new Exception("Source can't spend funds - CTransPayload");
             
             // Restrcited rec
             if (UTILS.BASIC.hasAttr(this.target_adr, "ID_RES_REC"))
             {
                 // Load data
                 ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                                    + "FROM adr_attr "
                                                   + "WHERE adr='"+this.target_adr+"' "
                                                     + "AND attr='ID_RES_REC'");
                 
                 // Next
                 rs.next();
                 
                 // Check recipients
                 if (!rs.getString("s1").equals(this.dest) &&
                     !rs.getString("s2").equals(this.dest) &&
                     !rs.getString("s3").equals(this.dest))
                 throw new Exception("Source is restricted - CTransPayload");
             }
             
             // Check dest
	     if (!UTILS.BASIC.isAdr(this.dest))
	        throw new Exception("Invalid destination address - CTransPayload");
             
             // Source and destination the same
             if (this.target_adr.equals(this.dest))
	         throw new Exception("Source and destination address can't be the same - CTransPayload");
             
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
            
            // Message 
            String mes_hash="";
            if (this.mes!=null)
            {
               // Check message
               if (!mes.check())
                  throw new Exception("Invalid message - CTransPayload");
               
               // Hash
               mes_hash=this.mes.hash;
            }
            
            // Check cur
	    if (!this.cur.equals("MSK"))
            {
                // Symbol valid ?
                if (!UTILS.BASIC.isSymbol(this.cur))
                   throw new Exception("Invalid currency, CTransPayload");
                     
                // Asset exist ?
                if (!UTILS.BASIC.isAsset(this.cur))
                   throw new Exception("Invalid currency, CTransPayload");
                
                // Recipient trust asset ?
                ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                                   + "FROM adr_attr "
                                                  + "WHERE adr='"+this.dest+"' "
                                                    + "AND attr='ID_TRUST_ASSET'");
                
                // No data ?
                if (!UTILS.DB.hasData(rs))
                    throw new Exception("Recipient doesn't trust asset, CTransPayload");
            }
            
            // Check escrower
	    if (!this.escrower.equals(""))
	    {
	        if (!UTILS.BASIC.isAdr(this.escrower))
	           throw new Exception("Invalid currency, CTransPayload");
	    }
	    
	    // Check source balance
            double balance=UTILS.ACC.getBalance(this.target_adr, this.cur, block);
                  
            // Insufficient funds
            if (balance<this.amount) 
	       throw new Exception("Insuficient funds, CTransPayload.java");
            
            // Digest
            String h=UTILS.BASIC.hash(this.getHash()+
		                      this.dest+
                                      this.amount+
                                      this.cur+
                                      this.escrower+
                                      mes_hash); 
                  
            // Check hash
            if (!this.hash.equals(h))
               throw new Exception("Invalid hash, CTransPayload");
	    
            // Insert pending transaction
	    UTILS.ACC.newTrans(this.target_adr, 
                                 this.dest,
                                 -this.amount,
                                 true,
                                 this.cur, 
                                 "Transaction to another address "+this.target_adr, 
                                 this.escrower, 
                                 this.hash, 
                                 this.block,
                                 block,
                                 0);
                
            // To destination
            if (this.escrower.equals(""))
            UTILS.ACC.newTrans(this.dest, 
                                 this.target_adr,
                                 this.amount,
                                 true,
                                 this.cur, 
                                 "Transaction from address "+this.target_adr, 
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
                if (block==null) this.checkIPN("unconfirmed");
            }
       
        }
         
         public void checkIPN(String status) throws Exception
         {
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
                   
                   // Source
                   loader.addParam("src", this.target_adr);
                   
                   // Dest
                   loader.addParam("dest", this.dest);
                   
                   // Amount
                   loader.addParam("amount", String.valueOf(this.amount));
                   
                   // Currency
                   loader.addParam("currency", this.cur);
                   
                   // Message
                   String message="";
                   if (this.mes!=null)
                       message=this.mes.mes;
                    
                   loader.addParam("mes", message);
                   
                   // transaction hash
                   loader.addParam("tx_hash", this.hash);
                   
                   // Block
                   loader.addParam("block", String.valueOf(this.block));
                   
                   // Start
                   loader.start();
            }
         
         }
         
         public void commit(CBlockPayload block) throws Exception
	 { 
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
                                                        + "sender_adr='"+this.target_adr+"', "
                                                        + "rec_adr='"+this.dest+"', "
                                                        + "escrower='"+this.escrower+"', "
                                                        + "amount='"+this.amount+"', "
                                                        + "expire='"+(this.block+43200)+"', "
                                                        + "cur='"+this.cur+"', "
                                                        + "block='"+this.block+"'");
                }
                
                if (!this.cur.equals("MSK"))
                {
                    // Load asset ID
                    ResultSet rs = UTILS.DB.executeQuery("SELECT * "
                                                         + "FROM assets "
                                                        + "WHERE symbol='"+this.cur+"'");
                    
                    if (UTILS.DB.hasData(rs))
                    {
                       // Next
                      rs.next();
                      
                      // Asset ID
                      long assetID=rs.getLong("assetID");
                      
                      // Vote
                      UTILS.BASIC.voteTarget(this.target_adr, "ID_ASSET", assetID, block.block);
                    }
               }
	}
	 
}
