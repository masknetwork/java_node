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
import wallet.network.packets.trans.CFeePayload;



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
	
	public boolean exist(CBroadcastPacket packet) throws Exception
	{
            // Packet hash
            long no_packet_hashes=0;
            String packet_hash=packet.hash;
                 
            // Fee hash
            long no_fee_hashes=0;
            String fee_hash=UTILS.BASIC.hash(packet.fee_payload);
                 
            // Payload hash
            long no_payload_hashes=0;
            String payload_hash=UTILS.BASIC.hash(packet.payload);
                 
            // Parse packets
            for (int a=0; a<=this.packets.size()-1; a++)
            {
                // Load packet
                CBroadcastPacket p=this.packets.get(a);
                     
                // Packet hash
                if (packet_hash.equals(p.hash))
                    no_packet_hashes++;
                     
                // Packet hash
                if (fee_hash.equals(UTILS.BASIC.hash(p.fee_payload)))
                    no_fee_hashes++;
                     
                // Payload hash
                if (payload_hash.equals(UTILS.BASIC.hash(p.payload)))
                    no_payload_hashes++;
            }
                 
            // check duplicates
            if (no_packet_hashes>0 || 
                no_fee_hashes>0 || 
                no_payload_hashes>0)
            return true;
            
            // Not found
            return false;
	}
	
	public void addPacket(CBroadcastPacket packet) throws Exception
	{
            // Packet exist ?
	    if (this.exist(packet)) 
                throw new Exception("Packet already exist"); 
                
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
                    CBroadcastPacket p=(CBroadcastPacket)this.packets.get(a);
                       
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
            if (this.packets.size()<100 && (UTILS.SERIAL.serialize(this).length+UTILS.SERIAL.serialize(packet).length)<95000)
            {
	       this.packets.add(packet); 
            }
            else
            {
               for (int a=0; a<=this.packets.size()-1; a++)
               {
                   // Load packet
                   CBroadcastPacket p=(CBroadcastPacket)this.packets.get(a);
                   
                   // fee payload
                   CFeePayload fee=(CFeePayload) UTILS.SERIAL.deserialize(p.fee_payload);
                   
                   // Smaller net fee ?
                   if (fee.amount/p.payload.length<fee.amount/packet.payload.length && 
                       !this.exist(packet))
                   {
                       // Remove
                       this.packets.remove(a);
                       
                       // Add new packet
                       this.packets.add(packet);
                   }
               }
            }
           
            // Move feeds at the end
            for (int a=0; a<=this.packets.size()-1; a++)
            {
                // Load packet data
                CBroadcastPacket p=(CBroadcastPacket)this.packets.get(a);
                       
                // Feed packet ?
                if (p.tip.equals("ID_FEED_PACKET"))
                {
                    // Remove packet
                    this.packets.remove(a);
                    
                     // Add packet at the end
                    this.packets.add(p);
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
	
        
	// Compare two blocks and returns the accuracy
	public int compare(CBlockPayload block)
	{
		return 90;
	}
	
	// Checks the block integrity
	public void check() throws Exception
	{
            // Signer valid
            if (!UTILS.BASIC.isAdr(this.target_adr))
                throw new Exception("Invalid block signer");
           
            // Check pow
            for (int a=0; a<=this.packets.size()-1; a++)
	    {
                // Packet
		CBroadcastPacket packet=((CBroadcastPacket)this.packets.get(a));
		
                // Check
                packet.check(this);
            }
        }
        
        // Commits all transactions
	public void commit()  throws Exception
	{
            for (int a=0; a<=this.packets.size()-1; a++)
	    {
                // Packet
		CBroadcastPacket p=((CBroadcastPacket)this.packets.get(a));
                   
                // Commit
                p.commit(this);
            }
        }
}