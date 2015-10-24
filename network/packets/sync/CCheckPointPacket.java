package wallet.network.packets.sync;

import java.sql.ResultSet;
import java.sql.SQLException;

import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.blocks.*;

public class CCheckPointPacket extends CBroadcastPacket 
{
	// Signer
	String signer;
	
	// Balance
	double balance;
	
	// Checkpoint
	CCheckPoint checkpoint;
	
   public CCheckPointPacket(String signer)
   {
	   super("ID_CHECKPOINT_PACKET");
	   
	   // Signer
	   this.signer=signer;
	   
	   // Balance
	   this.balance=UTILS.BLOCKS.getSignerBalance(signer);
	   
	   // Checkpoint
	   this.checkpoint=new CCheckPoint();
	   
	   // Hash
	   this.hash=UTILS.BASIC.hash(this.signer+
			                      this.checkpoint.hash+
			                      String.valueOf(this.balance));
				
	   // Sign packet
	   this.sign(signer);
    }
   
    public CResult check()
    {
	   // Super
	   CResult res=super.check((CBlockPayload)null);
	   if (res.passed==false) return res;
			   
	   // Check status
	   //if (UTILS.STATUS.netstat.indexOf("CONFIRM")<1)
	   //	return new CResult(false, "Invalid stage", "CBlockSignPacket", 204);
	   
	   // Check balance
	   if (this.balance!=UTILS.BLOCKS.getSignerBalance(this.signer))
		  return new CResult(false, "Invalid balance", "CBlockSignPacket", 204);
	   
	   if (this.balance<10)
		   return new CResult(false, "Invalid balance", "CBlockSignPacket", 204);
	   
	   // Already signed
	   if (UTILS.BLOCKS.checkpoints.findSigner(this.signer)==true)
		   return new CResult(false, "Already signed", "CBlockSignPacket", 204);
	   
	   //Check sig
	   if (this.checkSign(signer)==false)
		   return new CResult(false, "Invalid signature", "CBlockSignPacket", 204);
	   
	   // Add packwet
	   UTILS.BLOCKS.checkpoints.addSigner(this);
	   
	   // Return
	   return new CResult(true, "Ok", "CBlockSignPacket", 204);
   }
}
