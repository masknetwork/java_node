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
           
           // Block
           this.block=UTILS.NET_STAT.last_block+1;
           
           // Dificulty
           this.net_dif=UTILS.NET_STAT.net_dif;
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
        
        
	// Check 
	public CResult check() throws Exception
	{
            // Check type
	     if (!this.tip.equals("ID_BLOCK")) 
	   	return new CResult(false, "Invalid packet type", "CBlockPacket", 39);
           
	     // Deserialize transaction data
	     CBlockPayload block_payload=(CBlockPayload) UTILS.SERIAL.deserialize(payload);
	     
             // Block number
             if (this.block<=UTILS.NET_STAT.last_block)
                return new CResult(false, "Invalid block number", "CBlockPacket", 39);
             
             // Hash
             if (!UTILS.MINER_UTILS.checkHash(this.prev_hash, 
                                              this.block, 
                                              this.payload_hash, 
                                              this.signer, 
                                              this.signer_balance, 
                                              this.tstamp, 
                                              this.nonce,
                                              this.hash,
                                              this.net_dif))
             return new CResult(false, "Invalid hash", "CBlockPacket", 39);
             
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
                // Deserialize transaction data
	   	CBlockPayload block_payload=(CBlockPayload) UTILS.SERIAL.deserialize(payload);
	   	
	   	// Superclass
	   	CResult res=super.commit(block_payload);
	   	if (res.passed==false) return res;
                
	   	// Commit payload
	   	res=block_payload.commit();
	   	if (res.passed==false) return res;
                
                 // Insert the block
                UTILS.DB.executeUpdate("INSERT INTO blocks(hash, "
                                                        + "block, "
        		                                + "prev_hash, "
                                                        + "signer, "
                                                        + "packets, "
                                                        + "tstamp, "
                                                        + "nonce, "
                                                        + "net_dif, "
                                                        + "signer_balance, "
                                                        + "payload_hash, "
                                                        + "size) "
        		                + "VALUES('"+this.hash+"', '"+
                                                     String.valueOf(this.block)+"', '"+
        		                             this.prev_hash+"', '"+
                                                     this.signer+"', '"+
                                                     block_payload.packets.size()+"', '"+
                                                     this.tstamp+"', '"+
                                                     this.nonce+"', '"+
                                                     this.net_dif+"', '"+
                                                     this.signer_balance+"', '"+
                                                     this.payload_hash+"', '"+
        		                             this.payload.length+"')");
                
                FileOutputStream fout = new FileOutputStream(new File(UTILS.WRITEDIR+"blocks/block_"+this.block+".block"));
                ObjectOutputStream oos = new ObjectOutputStream(fout);
                oos.writeObject(this);
                
                // Run contracts
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
                       CAgent AGENT=new CAgent(rs.getLong("aID"), false);
                       
                       // Load block
                       AGENT.loadBlock(hash, 
                                       this.block, 
                                       this.nonce);
                    
                       // Execute
                       AGENT.execute("#block#", false);
                    }
                }
                
                // Refresh tables
                UTILS.NET_STAT.refreshTables(this.block);
                
                // New block
                UTILS.CBLOCK.newBlock(this.block, this.hash, this.tstamp);
                
                // Close
                rs.close();
                s.close();
	   	
		// Return 
	   	return new CResult(true, "Ok", "CBlockPacket", 55);
	}
}