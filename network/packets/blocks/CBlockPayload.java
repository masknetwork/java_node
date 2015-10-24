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
	
	// Check index
	int check_index=0;
	
	// Hash
	public String hash;
	
	// Signer
	public String signer;
	
	// Sign
	public String sign;
	
	// Block
	public long block;
	
	// Dificulty
	long dificulty;
		
	public CBlockPayload() 
	{
		// ID
		this.block=UTILS.BASIC.block();
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
		UTILS.CONSOLE.write("Packet "+pack.hash+" added to block "+this.hash+"+("+this.packets.size()+" packets)");
                
                // Recalculate hash
                this.hash=hash();
	}
	
	// Hash
	public String hash()
	{
		String h="";
				
		for (int a=0; a<=this.packets.size()-1; a++)
		   {
			  CPacket packet=(CPacket)this.packets.get(a);
			  h=h+packet.hash;
		   }
		
		return UTILS.BASIC.hash(h);
	}
	
	// Sign payload
	public void sign(String signer)
	{
		// Signer
		this.signer=signer;
		
		// Hash
		String hash=this.hash();
		
		// Get address
		CAddress adr=UTILS.WALLET.getAddress(signer);
		this.sign=adr.sign(this.hash);
	}
	
	// Commits all transactions
	public CResult commit()
	{
             UTILS.CONSOLE.write("--------------- Committing block "+this.block+"--------------------");
			
		for (int a=0; a<=this.packets.size()-1; a++)
	        {
		   CResult res=((CPacket)this.packets.get(a)).commit(this);
		   if (res.passed==false) res.report();
	        }
		
		// Trans pool
		UTILS.NETWORK.TRANS_POOL.delBlock(this.block);
			
                // Insert the block
                UTILS.DB.executeUpdate("INSERT INTO blocks(hash, "
        		                          + "block, "
        		                          + "signer, "
        		                          + "packets) "
        		                + "VALUES('"+this.hash+"', '"+
        		                             String.valueOf(this.block)+"', '"+
        		                             this.signer+"', '"+
        		                             String.valueOf(this.packets.size())+"')");
     
        
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
		for (int a=0; a<=this.packets.size()-1; a++)
	        {   
		    CBroadcastPacket packet=((CBroadcastPacket)this.packets.get(a));
		    CResult res=packet.check(this);
		   
		    if (res.passed==false) return res;
		    this.check_index++;
		}
		
		// Ok
		return new CResult(true, "Ok", "CBlock", 68);
	}

}