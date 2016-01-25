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
import wallet.network.packets.feeds.CFeedPayload;


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
        
        
		
	public CBlockPayload(long prev_block_no) 
	{
	    // ID
	    this.block=prev_block_no+1;
            
       
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
	
	public void addPacket(CPacket packet)
	{
            try
            {
		// Packet exist ?
		if (this.exist(packet.hash)) return;
		
                // Data feed packet ?
                if (packet.tip.equals("ID_FEED_PACKET"))
                {
                   // Deserialize payload
                   CFeedPayload dec_payload=(CFeedPayload) UTILS.SERIAL.deserialize(packet.payload);
                   
                   // Feed symbol
                   String feed=dec_payload.feed_symbol;
                           
                   // Already in block
                   for (int a=0; a<=this.packets.size()-1; a++)
                   {
                       // Load packet data
                       CPacket p=(CPacket)this.packets.get(a);
                       
                       // Feed packet ?
                       if (p.tip.equals("ID_FEED_PACKET"))
                       {
                           // Deserialize
                           dec_payload=(CFeedPayload) UTILS.SERIAL.deserialize(p.payload);
                           
                           // Remove if same feed ?
                           if (dec_payload.feed_symbol.equals(feed))
                               this.packets.remove(a);
                       }
                   }
                }
                
		// Adds packet
		this.packets.add(packet);
		
                // Console
		//UTILS.CONSOLE.write("Packet "+pack.hash+" added to block "+this.hash+"+("+this.packets.size()+" packets)");
                
                // Recalculate hash
                this.hash();
            }
            catch (Exception ex) 
       	    {  
       		UTILS.LOG.log("Exception", ex.getMessage(), "SQLException.java", 57);
            }
            
	}
	
	// Hash
	public void hash()
	{
	   String hash=UTILS.BASIC.hash(this.signer+
                                        String.valueOf(this.block)+
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
		if (adr!=null) this.sign=adr.sign(this.hash);
	}
	
	// Commits all transactions
	public CResult commit() throws SQLException
	{
                // Start logging
                UTILS.LOG_QUERIES=true;
			
		for (int a=0; a<=this.packets.size()-1; a++)
	        {
		   CResult res=((CPacket)this.packets.get(a)).commit(this);
		   if (res.passed==false) res.report();
	        }
		
		// Trans pool
		UTILS.NETWORK.TRANS_POOL.newBlock(this.block);
		 
                // Delete expired items
                this.delExpired();
                
                // Check options
                UTILS.BIN_OPTIONS.checkOptions(block);
                
                // Refresh tables
                UTILS.NET_STAT.refreshTables(block);
                
		// Ok
		return new CResult(true, "Ok", "CBlock", 68);

        }
	
        public void delExpired()
        {
            // Expired address options
            UTILS.DB.executeUpdate("DELETE FROM adr_options WHERE expires<"+this.block);
            
            // Expired ads
            UTILS.DB.executeUpdate("DELETE FROM ads WHERE expires<"+this.block);
            
            // Expired domains
            UTILS.DB.executeUpdate("DELETE FROM domains WHERE expires<"+this.block);
            
            // Expired domains listings
            UTILS.DB.executeUpdate("UPDATE domains SET market_expires=0, market_bid=0 WHERE expires<"+this.block);
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
            
            // Check pow
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