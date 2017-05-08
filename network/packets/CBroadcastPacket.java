// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets;

import wallet.network.*;
import wallet.kernel.*;
import wallet.network.packets.trans.*;
import wallet.network.packets.*;
import wallet.network.packets.blocks.*;

public class CBroadcastPacket extends CPacket 
{	
	 // Net fee
	 public byte[] fee_payload=null;
           
	 // Block
	 public Long block;
         
	   
	public CBroadcastPacket(String tip)  throws Exception
	{
	    // Constructor
	    super(tip);
	    
            // Block
            this.block=UTILS.NET_STAT.last_block+1;
	}
	
	public void check(CBlockPayload block) throws Exception
	{
            // Parent check
            super.check(block);
            
            // Payload size
            if (this.payload.length>100000)
               throw new Exception("Invalid payload size");
            
             // Check hash
             String h=UTILS.BASIC.hash(this.tip+
                                       this.tstamp+
                                       String.valueOf(this.block)+
				       UTILS.BASIC.hash(this.fee_payload)+
				       UTILS.BASIC.hash(this.payload));
	     
             if (!h.equals(this.hash))
	        throw new Exception("Invalid hash - CBroadcastPacket.java");
	    
             // Block number
             if (block!=null)
             {
               if (this.block!=block.block)
                 throw new Exception("Invalid block number - CBroadcastPacket.java");
             }
             else
             {
                // For actual block ?
                if (this.block!=(UTILS.NET_STAT.last_block+1))
                    throw new Exception("Invalid block number - CBroadcastPacket.java");
             }
             
             // Check duplicates
             if (block!=null)
             {
                 // Packet hash
                 long no_packet_hashes=0;
                 String packet_hash=this.hash;
                 
                 // Fee hash
                 long no_fee_hashes=0;
                 String fee_hash=UTILS.BASIC.hash(this.fee_payload);
                 
                 // Payload hash
                 long no_payload_hashes=0;
                 String payload_hash=UTILS.BASIC.hash(this.payload);
                 
                 // Parse packets
                 for (int a=0; a<=block.packets.size()-1; a++)
                 {
                     // Load packet
                     CBroadcastPacket packet=block.packets.get(a);
                     
                     // Packet hash
                     if (packet_hash.equals(packet.hash))
                         no_packet_hashes++;
                     
                     // Packet hash
                     if (fee_hash.equals(UTILS.BASIC.hash(packet.fee_payload)))
                         no_fee_hashes++;
                     
                     // Payload hash
                     if (payload_hash.equals(UTILS.BASIC.hash(packet.payload)))
                         no_payload_hashes++;
                 }
                 
                // check duplicates
                if (no_packet_hashes>1 || 
                     no_fee_hashes>1 || 
                     no_payload_hashes>1)
                throw new Exception("Duplicate found - CBroadcastPacket.java");
             }
             
            // Check fee
            CFeePayload fee=(CFeePayload) UTILS.SERIAL.deserialize(fee_payload);
            fee.check(block);
            
	    // Check signature
	    if (this.checkSign()==false)
		throw new Exception("Invalid signature - CBroadcastPacket.java");	
	}
	
	
	public void commit(CBlockPayload block) throws Exception
	{
            // Deserialize transaction data
            CPayload dec_payload=(CPayload) UTILS.SERIAL.deserialize(payload);
            
            // Deserialize payload
            CFeePayload fee=(CFeePayload) UTILS.SERIAL.deserialize(fee_payload);
            
            try
            {
                // Check
                this.check(block);
                
                // Commit fee
	        if (!this.tip.equals("ID_BLOCK"))
                    fee.commit(block);
            }
            catch (Exception ex)
            {
               System.out.println(ex.getMessage());
               return;
            }
            
            try
            {
                // Commit
	        dec_payload.commit(block);
                
                // Commited
                UTILS.BASIC.commited(UTILS.NET_STAT.actual_block_hash, 
                                     UTILS.NET_STAT.actual_block_no, 
                                     this.hash, 
                                     dec_payload.hash, 
                                     fee.hash);
            }
            catch (Exception ex)
            {
               System.out.println(ex.getMessage());
            }
	}
	
	public void sign(String sig) throws Exception
	{
            // Packet hash
            this.hash=UTILS.BASIC.hash(this.tip+
                                       this.tstamp+
				       String.valueOf(this.block)+
				       UTILS.BASIC.hash(this.fee_payload)+
				       UTILS.BASIC.hash(this.payload));
		   
            // Signature address
            if (sig.equals(""))
            {
                // Deserialize payload
                CFeePayload fee=(CFeePayload) UTILS.SERIAL.deserialize(fee_payload);
                
                // Address
                CAddress adr=UTILS.WALLET.getAddress(fee.target_adr);
	        
                // Sign
                this.sign=adr.sign(this.hash);
            }
            else 
                this.sign=sig;
        }
        
        public void sign() throws Exception
	{
            // Packet hash
            this.hash=UTILS.BASIC.hash(this.tip+
                                       this.tstamp+
				       String.valueOf(this.block)+
				       UTILS.BASIC.hash(this.fee_payload)+
				       UTILS.BASIC.hash(this.payload));
            
            // Deserialize payload
            CFeePayload fee=(CFeePayload) UTILS.SERIAL.deserialize(fee_payload);
                    
            // Signature address
            CAddress adr=UTILS.WALLET.getAddress(fee.target_adr);
	      this.sign=adr.sign(this.hash);
           
        }
	
	
	public boolean checkSign() throws Exception
	{
            // Deserialize payload
            CFeePayload fee=(CFeePayload) UTILS.SERIAL.deserialize(fee_payload);
                    
	    CECC ecc=new CECC(fee.target_adr);
		return (ecc.checkSig(hash, this.sign));
	}
	
	
}
