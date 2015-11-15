package wallet.network.packets;

import wallet.network.*;
import wallet.kernel.*;
import wallet.network.packets.trans.*;
import wallet.network.packets.*;
import wallet.network.packets.blocks.*;

public class CBroadcastPacket extends CPacket 
{	
	 // Net fee
	 public CFeePayload fee=null;
	   
	 // Block
	 public Long block;
         
         // Signature
         public String sign;
	   
	public CBroadcastPacket(String tip) 
	{
	    // Constructor
	    super(tip);
		
	    // Block
	    this.block=UTILS.BASIC.block();
	}
	
	public CResult check(CBlockPayload block)
	{
	    // Parent check
            CResult res_parent=super.check(block);
            if (res_parent.passed==false) return res_parent;
        
             // Check hash
             String h=UTILS.BASIC.hash(this.tip+
				       String.valueOf(this.tstamp)+
                                       String.valueOf(this.block)+
				       new String(UTILS.BASIC.hash(UTILS.SERIAL.serialize(this.fee)))+
				       new String(UTILS.BASIC.hash(UTILS.SERIAL.serialize(this.payload))));
	     
             if (!h.equals(this.hash))
	        return new CResult(false, "Invalid hash", "CBroadcastPacket", 30);
	    
            // Check fee
            CResult res=this.fee.check(block);
            if (!res.passed) 
                return res;
            
	    // Check signature
	    if (this.checkSign()==false)
		return new CResult(false, "Invalid signature", "CBroadcastPacket", 30);
		
	    // Return
	    return new CResult(true, "Ok", "CBroadcastPacket", 30);
	}
	
	
	public CResult commit(CBlockPayload block)
	{
	    // Commit
	    CResult res=super.commit(block);
	    if (res.passed==false) return res;
		
	    // Commit fee
	    if (!this.tip.equals("ID_BLOCK"))
            {
		CResult res_fee=this.fee.commit(block);
		if (res_fee.passed==false) return res_fee;
            }
		
            // Return
	    return new CResult(true, "Ok", "CBroadcastPacket", 30);
	}
	
	public void sign()
	{
            // Block
	    this.block=UTILS.BASIC.block(); 
		   
            // Packet hash
            this.hash=UTILS.BASIC.hash(this.tip+
				       String.valueOf(this.tstamp)+
                                       String.valueOf(this.block)+
				       new String(UTILS.BASIC.hash(UTILS.SERIAL.serialize(this.fee)))+
				       new String(UTILS.BASIC.hash(UTILS.SERIAL.serialize(this.payload))));
		   
            // Signature address
            CAddress adr=UTILS.WALLET.getAddress(this.fee.target_adr);
	    this.sign=adr.sign(this.hash);
	}
	
	public void sign(String signer)
	{
		 // Tstamp
		   this.tstamp=System.currentTimeMillis(); 
		   
		   // Block
		   this.block=UTILS.BASIC.block(); 
		   
		   // Packet hash
		   this.hash=UTILS.BASIC.hash(this.tip+
				                      String.valueOf(this.tstamp)+
				                      new String(UTILS.BASIC.hash(UTILS.SERIAL.serialize(this.fee)))+
				                      new String(UTILS.BASIC.hash(UTILS.SERIAL.serialize(this.payload))));
		   
		   // Signature address
		   CAddress adr=UTILS.WALLET.getAddress(signer);
		   this.sign=adr.sign(this.hash);
	}
	
	public boolean checkSign()
	{
	    CECC ecc=new CECC(this.fee.target_adr);
		return (ecc.checkSig(hash, this.sign));
	}
	
	public boolean checkSign(String signer)
	{
		 CECC ecc=new CECC(signer);
		 return (ecc.checkSig(hash, this.sign));
	}
}