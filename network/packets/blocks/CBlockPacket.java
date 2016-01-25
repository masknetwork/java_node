package wallet.network.packets.blocks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.zip.GZIPOutputStream;

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
        
	public CBlockPacket(String signer)
        {
	   // Constructor
	   super("ID_BLOCK");
            
           // Signer
           this.signer=signer;
           
           // Block
           this.block=UTILS.NET_STAT.last_block+1;
           
           // Tstamp
           this.tstamp=UTILS.BASIC.tstamp();
           
           // Prev hash
           this.prev_hash=UTILS.NET_STAT.last_block_hash;
           
           // Block
           this.block=UTILS.NET_STAT.last_block+1;
        }
	
        // Sign
	public void sign(String hash)
        { 
            // Hash
            this.hash=hash;
            
            // Signature address
            CAddress adr=UTILS.WALLET.getAddress(this.signer);
	    this.sign=adr.sign(hash);
        }
        
        
	// Check 
	public CResult check()
	{
            // Check type
	     if (!this.tip.equals("ID_BLOCK")) 
	   	return new CResult(false, "Invalid packet type", "CBlockPacket", 39);
           
	     // Deserialize transaction data
	     CBlockPayload block_payload=(CBlockPayload) UTILS.SERIAL.deserialize(payload);
	     
             String h=UTILS.BASIC.hash(this.prev_hash+
                                      "ID_BLOCK"+
                                      this.block+
                                      this.payload_hash+
                                      this.signer+
                                      this.signer_balance+
                                      this.tstamp+
                                      String.valueOf(this.nonce)); 
            
             // Hash
             if (!UTILS.CBLOCK.miner_1.checkHash(h))
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
	   
	public CResult commit() throws SQLException
	{
		// Deserialize transaction data
	   	CBlockPayload block_payload=(CBlockPayload) UTILS.SERIAL.deserialize(payload);
	   	
                 // Insert the block
                UTILS.DB.executeUpdate("INSERT INTO blocks(hash, "
                                                        + "block, "
        		                                + "prev_hash, "
                                                        + "signer, "
                                                        + "packets, "
                                                        + "tstamp, "
                                                        + "nonce, "
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
                                                     this.signer_balance+"', '"+
                                                     this.payload_hash+"', '"+
        		                             this.payload.length+"')");
                
	   	// Superclass
	   	CResult res=super.commit(block_payload);
	   	if (res.passed==false) return res;
                
	   	// Commit payload
	   	res=block_payload.commit();
	   	if (res.passed==false) return res;
                
                 // New block
                UTILS.CBLOCK.newBlock(this.block, this.hash, this.tstamp);
                
                // Record block ?
                try
                { 
                   FileOutputStream fout = new FileOutputStream(new File(UTILS.WRITEDIR+"blocks/block_"+block_payload.block+".block"));
                   ObjectOutputStream oos = new ObjectOutputStream(fout);
                   oos.writeObject(this);
                }
                catch (IOException ex)
                {
        	   UTILS.LOG.log("IOException", ex.getMessage(), "CBlockPayload.java", 76);
                }
	   	
		// Return 
	   	return new CResult(true, "Ok", "CBlockPacket", 55);
	}
}