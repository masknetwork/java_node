// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets;

import wallet.network.*;
import wallet.kernel.*;
import wallet.network.packets.trans.*;
import wallet.network.packets.*;
import wallet.network.packets.blocks.*;
import wallet.network.packets.tweets.CFollowPayload;

public class CBroadcastPacket extends CPacket 
{	
	 // Net fee
	 public CFeePayload fee=null;
	   
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
                                       String.valueOf(this.block)+
				       UTILS.BASIC.hash(UTILS.SERIAL.serialize(this.fee))+
				       UTILS.BASIC.hash(UTILS.SERIAL.serialize(this.payload)));
	     
             if (!h.equals(this.hash))
	        throw new Exception("Invalid hash - CBroadcastPacket.java");
	    
             // Block number
             if (block!=null)
               if (this.block!=block.block)
                 throw new Exception("Invalid block number - CBroadcastPacket.java");
             
            // Check fee
            this.fee.check(block);
            
	    // Check signature
	    if (this.checkSign()==false)
		throw new Exception("Invalid signature - CBroadcastPacket.java");	
	}
	
	
	public void commit(CBlockPayload block) throws Exception
	{
	    // Commit
	    super.commit(block);
            
            // Commit fee
	    if (!this.tip.equals("ID_BLOCK"))
                 this.fee.commit(block);
	    
            // Check
            this.check(block);
            
            try
            {
                // Begin
                UTILS.DB.begin();
                
                // Deserialize transaction data
                CPayload dec_payload=(CPayload) UTILS.SERIAL.deserialize(payload);
                
                UTILS.BASIC.commited(UTILS.NET_STAT.actual_block_hash, 
                                     UTILS.NET_STAT.actual_block_no, 
                                     this.hash, 
                                     dec_payload.hash, 
                                     this.fee.hash);
                
	        // Commit
	        dec_payload.commit(block);
	    
                // Commit
                UTILS.DB.commit();
            }
            catch (Exception ex)
            {
                // Rollback
                UTILS.DB.rollback();
                
                // Exception
                throw new Exception(ex.getMessage());
            }
	 
           
	}
	
	public void sign(String sig) throws Exception
	{
            // Packet hash
            this.hash=UTILS.BASIC.hash(this.tip+
				       String.valueOf(this.block)+
				       UTILS.BASIC.hash(UTILS.SERIAL.serialize(this.fee))+
				       UTILS.BASIC.hash(UTILS.SERIAL.serialize(this.payload)));
		   
            // Signature address
            if (sig.equals(""))
            {
              CAddress adr=UTILS.WALLET.getAddress(this.fee.target_adr);
	      this.sign=adr.sign(this.hash);
            }
            else 
                this.sign=sig;
        }
        
        public void sign() throws Exception
	{
            // Packet hash
            this.hash=UTILS.BASIC.hash(this.tip+
				       String.valueOf(this.block)+
				       UTILS.BASIC.hash(UTILS.SERIAL.serialize(this.fee))+
				       UTILS.BASIC.hash(UTILS.SERIAL.serialize(this.payload)));
		   
            // Signature address
            CAddress adr=UTILS.WALLET.getAddress(this.fee.target_adr);
	      this.sign=adr.sign(this.hash);
           
        }
	
	
	public boolean checkSign() throws Exception
	{
	    CECC ecc=new CECC(this.fee.target_adr);
		return (ecc.checkSig(hash, this.sign));
	}
	
	
}
