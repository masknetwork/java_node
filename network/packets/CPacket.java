package wallet.network.packets;

import java.util.*;
import wallet.kernel.UTILS;
import wallet.network.*;
import org.apache.commons.codec.digest.*;
import wallet.network.packets.blocks.*;

public class CPacket implements java.io.Serializable
{
   // Tip
   public String tip;
   
   // Tstamp
   public Long tstamp;
   
   // Payload 
   public byte[] payload=null;
   
   // Hash
   public String hash;
   
   // Prev packet hash
   String prev_packet;
   
   // Next packet hash
   String next_packet;
   
   
   public CPacket(String tip)
   {
	   // Tip
	   this.tip=tip;
	   
	   // Tstamp
	   this.tstamp=System.currentTimeMillis(); 
   }
   
   public void hash()
   {
	// Hash
	this.hash=UTILS.BASIC.hash(this.tip+
			           String.valueOf(this.tstamp)+
			           UTILS.BASIC.hash(this.payload));
   }
   
   public CResult check(CBlockPayload block)
   {  
        // Ok
	return new CResult(true, "Ok", "CPacket", 72);
   }
   
   public CResult checkWithPeer(CPeer peer)
   {  
        // Ok
	return new CResult(true, "Ok", "CPacket", 72);
   }
   
   public CResult commit(CBlockPayload block)
	{
	   return new CResult(true, "Ok", "CPacket", 98);
	}
}