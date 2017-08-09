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
   
   // Hash
   public String hash;
   
   // Tstamp
   public long tstamp;
   
   // Signature
   public String sign="";
   
   // Payload 
   public byte[] payload=null;
   
   static final long serialVersionUID=1L;
   
   
   public CPacket(String tip)  throws Exception
   {
	// Tip
	this.tip=tip;
           
        // Time
        this.tstamp=UTILS.BASIC.tstamp();
           
    }
   
   public String hash()  throws Exception
   {
	return UTILS.BASIC.hash(this.tip+
                                this.tstamp);
        
   }
   
   public void check(CBlockPayload block) throws Exception
   {  
       // Basic check
       this.basicCheck();
   }
   
   public void check(CPeer peer) throws Exception
   {  
       // Basic check
       this.basicCheck();
   }
   
   public void basicCheck() throws Exception
   {
       // Tip
       if (!UTILS.BASIC.isStringID(this.tip))
           throw new Exception("Invalid packet type - CPacket.java, 65");
   
       // Hash
       if (!UTILS.BASIC.isHash(this.hash))
           throw new Exception("Invalid packet type - CPacket.java, 65");
   
       // Signature
       if (!sign.equals(""))
          if (!UTILS.BASIC.isBase64(this.sign))
           throw new Exception("Invalid packet type - CPacket.java, 65");
   }
   
   public void commit(CBlockPayload block) throws Exception
   {
	 
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
   
     public void process(CPeer sender) throws Exception
    {
        
    }
}