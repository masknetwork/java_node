// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.blocks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.zip.GZIPOutputStream;
import wallet.agents.CAgent;

import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.trans.*;
import wallet.kernel.*;


public class CBlockPacket extends CPacket 
{
        // Block
        public long block;
        
        // Prev hash
        public String prev_hash;
        
        // Prev hash
        public String payload_hash;
        
	// Signer
        public String signer;
        
        // Balance
        public long signer_balance;
        
        // Sign
        public String sign;
        
        // Timestamp
        public long tstamp;
        
        // Nonce
        public long nonce;
        
        // Dificulty
        public long net_dif;
        
        // Adress table hash
        public String tab_1="";
        
        // Adress table hash
        public String tab_2="";
        
        // Adress table hash
        public String tab_3="";
        
        // Adress table hash
        public String tab_4="";
        
        // Adress table hash
        public String tab_5="";
        
        // Adress table hash
        public String tab_6="";
        
        // Adress table hash
        public String tab_7="";
        
        // Adress table hash
        public String tab_8="";
        
        // Adress table hash
        public String tab_9="";
        
        // Adress table hash
        public String tab_10="";
        
	public CBlockPacket(String signer, long signer_balance) throws Exception
        {
	   // Constructor
	   super("ID_BLOCK");
           
           // Signer
           this.signer=signer;
              
           // Load signer balance
           this.signer_balance=signer_balance;
           
           // Block
           this.block=UTILS.NET_STAT.last_block+1;
           
           // Tstamp
           this.tstamp=UTILS.BASIC.tstamp();
           
           // Prev hash
           this.prev_hash=UTILS.NET_STAT.last_block_hash;
           
           // Dificulty
           this.net_dif=UTILS.NET_STAT.net_dif;
           
           // Checkpoint ?
           if (this.block%UTILS.SETTINGS.chk_blocks==0)
           {
               // Adr table hash
               this.tab_1=UTILS.NET_STAT.getHash("adr");
               
               // Adr table hash
               this.tab_2=UTILS.NET_STAT.getHash("adr");
              
               // Adr table hash
               this.tab_3=UTILS.NET_STAT.getHash("adr");
               
               // Adr table hash
               this.tab_4=UTILS.NET_STAT.getHash("adr");
               
               // Adr table hash
               this.tab_5=UTILS.NET_STAT.getHash("adr");
               
               // Adr table hash
               this.tab_6=UTILS.NET_STAT.getHash("adr");
               
               // Adr table hash
               this.tab_7=UTILS.NET_STAT.getHash("adr");
               
               // Adr table hash
               this.tab_8=UTILS.NET_STAT.getHash("adr");
               
               // Adr table hash
               this.tab_9=UTILS.NET_STAT.getHash("adr");
               
               // Adr table hash
               this.tab_10=UTILS.NET_STAT.getHash("adr");
           }
        }
	
        // Sign
	public void sign(String hash) throws Exception
        { 
            // Hash
            this.hash=hash;
            
            // Signature address
            CAddress adr=UTILS.WALLET.getAddress(this.signer);
	    this.sign=adr.sign(hash);
        }
        
        public boolean preCheck() throws Exception
        {
              // Statement
              Statement s=UTILS.DB.getStatement();
       
              // Load
              ResultSet rs=s.executeQuery("SELECT * "
                                          + "FROM blocks "
                                         + "WHERE hash='"+hash+"'");
              
              // Has data ?
              if (UTILS.DB.hasData(rs))
              {
                  // Close
                  s.close();
                  
                  // Return
                  return false;
              }
              
              // Close
              s.close();
              
             // Hash
             if (!UTILS.MINER_UTILS.checkHash(this.prev_hash, 
                                              this.block, 
                                              this.payload_hash, 
                                              this.signer, 
                                              this.signer_balance, 
                                              this.tstamp, 
                                              this.nonce,
                                              this.hash,
                                              this.net_dif,
                                              this.tab_1,
                                              this.tab_2,
                                              this.tab_3,
                                              this.tab_4,
                                              this.tab_5,
                                              this.tab_6,
                                              this.tab_7,
                                              this.tab_8,
                                              this.tab_9,
                                              this.tab_10))
                 return false;
             else
                 return true;
        }
        
	// Check 
	public CResult check() throws Exception
	{
             // Check type
	     if (!this.tip.equals("ID_BLOCK")) 
	   	return new CResult(false, "Invalid packet type", "CBlockPacket", 39);
           
	     // Deserialize transaction data
	     CBlockPayload block_payload=(CBlockPayload) UTILS.SERIAL.deserialize(payload);
	     
             // Super class
	      CResult res=super.check(block_payload);
	      if (res.passed==false) 
	      {
	   	 return res;
	      }
	      else
	      {
	   	 res=block_payload.check();
	   	 if (res.passed==false) return res;
	      }
              
              // Check signature
	      CECC ecc=new CECC(this.signer);
	      if (!ecc.checkSig(hash, this.sign))
                   return new CResult(false, "Invalid signature", "CBlockPacket", 39);
              
                
	      // Return 
	      return new CResult(true, "Ok", "CBlockPacket", 42);
	}
	   
	public CResult commit()  throws Exception
	{
               System.out.println("Commiting block "+this.block+" ("+this.hash+")");
                
                 
                // Deserialize transaction data
	   	CBlockPayload block_payload=(CBlockPayload) UTILS.SERIAL.deserialize(payload);
	   	
	   	// Superclass
	   	CResult res=super.commit(block_payload);
	   	if (res.passed==false) return res;
                
	   	// Commit payload
	   	res=block_payload.commit();
	   	if (res.passed==false) return res;
                
                 // Insert the block
                 Statement s=UTILS.DB.getStatement();
                 
                 // Result set
                 ResultSet rs=s.executeQuery("SELECT * "
                                             + "FROM agents "
                                            + "WHERE run_period>0 "
                                              + "AND status='ID_ONLINE'");
                
                if (UTILS.DB.hasData(rs))
                {
                    // Next
                    rs.next();
                    
                    // Run ?
                    if (UTILS.NET_STAT.last_block%rs.getLong("run_period")==0)
                    {
                       // Load VM
                       CAgent AGENT=new CAgent(rs.getLong("aID"), false, this.block);
                       
                       // Load block
                       AGENT.loadBlock(hash, 
                                       this.block, 
                                       this.nonce);
                    
                       // Execute
                       AGENT.execute("#block#", false, this.block);
                    }
                }
                
                // Refresh tables
                UTILS.NET_STAT.refreshTables(this.block);
                
                // Sync ?
                if (UTILS.STATUS.engine_status.equals("ID_SYNC"))
                    this.addBlock(this);
                
                // New block
                UTILS.CBLOCK.newBlock(this.block, this.prev_hash, this.hash, this.tstamp);
                
                // Close
                s.close();
	   	
		// Return 
	   	return new CResult(true, "Ok", "CBlockPacket", 55);
	}
        
   public void addBlock(CBlockPacket block) throws Exception
   {
      // Deserialize payload
       CBlockPayload block_payload=(CBlockPayload) UTILS.SERIAL.deserialize(block.payload);
                
       // Insert
       UTILS.DB.executeUpdate("INSERT INTO blocks(hash, "
                                                        + "block, "
        		                                + "prev_hash, "
                                                        + "signer, "
                                                        + "packets, "
                                                        + "tstamp, "
                                                        + "nonce, "
                                                        + "net_dif, "
                                                        + "signer_balance, "
                                                        + "tab_1, "
                                                        + "tab_2, "
                                                        + "tab_3, "
                                                        + "tab_4, "
                                                        + "tab_5, "
                                                        + "tab_6, "
                                                        + "tab_7, "
                                                        + "tab_8, "
                                                        + "tab_9, "
                                                        + "tab_10, "
                                                        + "payload_hash, "
                                                        + "size) "
        		                + "VALUES('"+block.hash+"', '"+
                                                     String.valueOf(block.block)+"', '"+
        		                             block.prev_hash+"', '"+
                                                     block.signer+"', '"+
                                                     block_payload.packets.size()+"', '"+
                                                     block.tstamp+"', '"+
                                                     block.nonce+"', '"+
                                                     block.net_dif+"', '"+
                                                     block.signer_balance+"', '"+
                                                     block.tab_1+"', '"+
                                                     block.tab_2+"', '"+
                                                     block.tab_3+"', '"+
                                                     block.tab_4+"', '"+
                                                     block.tab_5+"', '"+
                                                     block.tab_6+"', '"+
                                                     block.tab_7+"', '"+
                                                     block.tab_8+"', '"+
                                                     block.tab_9+"', '"+
                                                     block.tab_10+"', '"+
                                                     block.payload_hash+"', '"+
        		                             block.payload.length+"')");
      
   }
}