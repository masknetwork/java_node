// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets;

import java.util.*;
import wallet.kernel.UTILS;
import wallet.network.*;
import org.apache.commons.codec.digest.*;
import wallet.kernel.CAddress;
import wallet.kernel.CECC;
import wallet.network.packets.blocks.*;

public class CPacket implements java.io.Serializable
{
   // Tip
   public String tip;
   
   // Payload 
   public byte[] payload=null;
   
   // Hash
   public String hash;
   
   // Prev packet hash
   String prev_packet_hash;
   
   // Next packet hash
   String next_packet_hash;
   
   // Tstamp
   long tstamp;
   
   // Signature
   public String sign;
   
   static final long serialVersionUID=2569233717176233596L;
   
   
   public CPacket(String tip)  throws Exception
   {
	   // Tip
	   this.tip=tip;
           
           // Time
           this.tstamp=UTILS.BASIC.mtstamp();
           
    }
   
   public String hash()  throws Exception
   {
	// Hash
        if (this.payload!=null)
	return UTILS.BASIC.hash(this.tip+
                                this.tstamp+
			        UTILS.BASIC.hash(this.payload));
        else
        return UTILS.BASIC.hash(this.tip+
                                this.tstamp);
        
   }
   
   public CResult check(CBlockPayload block) throws Exception
   {  
        // Ok
	return new CResult(true, "Ok", "CPacket", 72);
   }
   
   public void process(CPeer sender) throws Exception
   {  
        
   }
   
   public CResult checkWithPeer(CPeer peer)  throws Exception
   {  
        // Ok
	return new CResult(true, "Ok", "CPacket", 72);
   }
   
   public CResult commit(CBlockPayload block) throws Exception
	{
	   return new CResult(true, "Ok", "CPacket", 98);
	}
   
   public void sign(String address) throws Exception
   {
        // Signature address
        CAddress adr=UTILS.WALLET.getAddress(address);
	this.sign=adr.sign(this.hash);
           
   }
   
   public boolean checkSign(String signer) throws Exception
   {
        CECC ecc=new CECC(signer);
	return (ecc.checkSig(hash, this.sign));
    }
}