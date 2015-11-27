package wallet.network.packets.blocks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;


public class CBlockPayload implements java.io.Serializable
{		
	// Packets list
	public ArrayList packets=new ArrayList();
	
	// Hash
	public String hash;
	
	// Signer
	public String signer;
	
	// Sign
	public String sign;
	
	// Block
	public long block;
        
        // Tstamp
        public long tstamp;
        
        // Prev hash
        public String prev_block_hash;
	
	// Dificulty
	public String dificulty;
        
        // Nonce
        public long nonce=0;
		
	public CBlockPayload(long prev_block_no, String prev_block_hash, String dif) 
	{
	    // ID
	    this.block=prev_block_no+1;
            
            // Prev block hash
            this.prev_block_hash=prev_block_hash;
            
            // Dificulty
            this.dificulty=dif;
            
            // Hash
            this.hash();
	}
	
	public boolean exist(String hash)
	{
		for (int a=0; a<=this.packets.size()-1; a++)
			if (((CBroadcastPacket)this.packets.get(a)).hash.equals(hash))
				return true;
		
		return false;
	}
	
	public void addPacket(CPacket pack)
	{
		// Packet exist ?
		if (this.exist(pack.hash)) return;
		
		// Adds packet
		this.packets.add(pack);
		
                // Console
		//UTILS.CONSOLE.write("Packet "+pack.hash+" added to block "+this.hash+"+("+this.packets.size()+" packets)");
                
                // Recalculate hash
                this.hash();
	}
	
	// Hash
	public void hash()
	{
	   String hash=UTILS.BASIC.hash(this.signer+
                                        String.valueOf(this.block)+
                                        this.prev_block_hash+
                                        String.valueOf(this.tstamp)+
                                        String.valueOf(this.dificulty)+
                                        String.valueOf(this.nonce)+
                                        UTILS.SERIAL.serialize(this.packets));
           
           // Return
           this.hash=hash;
	}
	
	// Sign payload
	public void sign(String signer)
	{
		// Signer
		this.signer=signer;
		
		// Hash
		this.hash();
		
		// Get address
		CAddress adr=UTILS.WALLET.getAddress(signer);
		this.sign=adr.sign(this.hash);
	}
	
	// Commits all transactions
	public CResult commit() throws SQLException
	{
             //UTILS.CONSOLE.write("--------------- Committing block "+this.block+"--------------------");
			
		for (int a=0; a<=this.packets.size()-1; a++)
	        {
		   CResult res=((CPacket)this.packets.get(a)).commit(this);
		   if (res.passed==false) res.report();
	        }
		
		// Trans pool
		UTILS.NETWORK.TRANS_POOL.delBlock(this.block);
			
                // Insert the block
                UTILS.DB.executeUpdate("INSERT INTO blocks(hash, "
                                                        + "prev_hash,"
        		                                + "block, "
        		                                + "signer, "
        		                                + "packets, "
                                                        + "dificulty, "
                                                        + "nonce, "
                                                        + "tstamp) "
        		                + "VALUES('"+this.hash+"', '"+
                                                     this.prev_block_hash+"', '"+
        		                             String.valueOf(this.block)+"', '"+
        		                             this.signer+"', '"+
        		                             String.valueOf(this.packets.size())+"', '"+
                                                     this.dificulty+"', '"+
                                                     String.valueOf(this.nonce)+"', '"+
                                                     this.tstamp+"')");
                
               
                // New block
                UTILS.CBLOCK.newBlock(this.block, this.hash, this.tstamp, this.dificulty);
              
		// Ok
		return new CResult(true, "Ok", "CBlock", 68);

        }
	
	// Compare two blocks and returns the accuracy
	public int compare(CBlockPayload block)
	{
		return 90;
	}
	
	// Checks the block integrity
	public CResult check()
	{
            long check_index=0;
            
		for (int a=0; a<=this.packets.size()-1; a++)
	        {   
		    CBroadcastPacket packet=((CBroadcastPacket)this.packets.get(a));
		    CResult res=packet.check(this);
		   
		    if (res.passed==false) return res;
		    check_index++;
		}
		
		// Ok
		return new CResult(true, "Ok", "CBlock", 68);
	}

}