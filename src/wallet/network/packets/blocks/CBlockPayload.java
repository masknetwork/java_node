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
import java.util.ArrayList;

import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.trade.feeds.CFeedPayload;



public class CBlockPayload extends CPayload
{	
    // Serial
   private static final long serialVersionUID = 100L;
   
	// Packets list
	public ArrayList<CBroadcastPacket> packets=new ArrayList<CBroadcastPacket>();
	
	// Hash
	public String hash;
	
	// Sign
	public String sign;
	
	// Block
	public long block;
        
        
		
	public CBlockPayload(String adr)  throws Exception
	{
            // Constructor
            super(adr);
            
	    // ID
	    this.block=UTILS.NET_STAT.last_block+1;
            
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
	
	public void addPacket(CBroadcastPacket packet) throws Exception
	{
            // Packet exist ?
	    if (this.exist(packet.hash)) return;
                
            // Block number
            if (this.block!=packet.block)
               throw new Exception("Invalid bock number"); 
            
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
                
            // Add
            if (this.packets.size()<250 && (UTILS.SERIAL.serialize(this.packets).length+packet.payload.length)<250000)
            {
	       this.packets.add(packet); 
            }
            else
            {
               for (int a=0; a<=this.packets.size()-1; a++)
               {
                   // Load packet
                   CBroadcastPacket p=(CBroadcastPacket)this.packets.get(a);
                   
                   // Smaller net fee ?
                   if (p.fee.amount/p.payload.length<packet.fee.amount/packet.payload.length)
                   {
                       // Remove
                       this.packets.remove(a);
                       
                       // Add new packet
                       this.packets.add(packet);
                       
                       return;
                   }
               }
            }
           
            // Recalculate hash
            this.hash();
        }
	
	// Hash
	public void hash() throws Exception
	{
	   String hash=UTILS.BASIC.hash(this.target_adr+
                                        String.valueOf(this.block)+
                                        UTILS.SERIAL.serialize(this.packets));
           
           // Return
           this.hash=hash;
	}
	
	// Sign payload
	public void sign(String signer) throws Exception
	{
		// Signer
		this.target_adr=signer;
		
		// Hash
		this.hash();
		
		// Get address
		CAddress adr=UTILS.WALLET.getAddress(signer);
		if (adr!=null) this.sign=adr.sign(this.hash);
	}
	
        
        public void payReward(String adr) throws Exception
        {
            UTILS.ACC.newTransfer("default", 
                                    adr,
                                    0.25, 
                                    false,
                                    "MSK", 
                                    "Block reward", 
                                    "", 
                                    hash, 
                                    this.block,
                                    this,
                                    0);
            
            UTILS.ACC.clearTrans(hash, "ID_ALL", this.block);
        }
        
        public void delExpired() throws Exception
        {
            // Expired ads
            UTILS.DB.executeUpdate("DELETE FROM ads WHERE expire<"+this.block);
            
            // Expired domains
            UTILS.DB.executeUpdate("DELETE FROM domains WHERE expire<"+this.block);
           
        }
        
	// Compare two blocks and returns the accuracy
	public int compare(CBlockPayload block)
	{
		return 90;
	}
	
	// Checks the block integrity
	public CResult check() throws Exception
	{
            long check_index=0;
            
            // Signer valid
            if (!UTILS.BASIC.adressValid(this.target_adr))
                 return new CResult(false, "Invalid signer", "CBlockPayload.java", 239);
           
            // Check pow
            for (int a=0; a<=this.packets.size()-1; a++)
	        {   
		    CBroadcastPacket packet=((CBroadcastPacket)this.packets.get(a));
		    packet.check(this);
		   
		    
		    check_index++;
		}
           
		
            // Ok
	    return new CResult(true, "Ok", "CBlock", 68);
	}
        
        // Commits all transactions
	public void commit()  throws Exception
	{
            try
            {
                // Start logging
                UTILS.LOG_QUERIES=true;
                
               for (int a=0; a<=this.packets.size()-1; a++)
	        {
                   // Packet
		   CPacket p=((CPacket)this.packets.get(a));
                   
                   // Commit
                   p.commit(this);
		   
	        }
		
                // Pay block reward
                this.payReward(this.target_adr);
                 
                // Delete expired items
                this.delExpired();
                
                // Check options
                UTILS.CRONS.runCrons(block, this);
            }
            catch (Exception ex) 
       	      {  
       		UTILS.LOG.log("Exception", ex.getMessage(), "CBlockPayload.java", 57);
              }
                
                // Trans pool
		UTILS.NETWORK.TRANS_POOL.newBlock(this.block);
 
        }

}