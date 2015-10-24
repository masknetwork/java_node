package wallet.network.packets.blocks;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;

public class CBlockSignPacket extends CBroadcastPacket 
{
	// Signer
	public String signer;
	
	// Block hash
	public String block_hash;
		
        // Signature
	public String sign;
		
	// Balance
	public double balance=0;
	
	// Accuracy
	int accuracy=0;
	
    public CBlockSignPacket(String signer, String block_hash, int accuracy)
    {
	   super("ID_BLOCK_SIGN");
	   
	   // Signer
	   this.signer=signer;
		
	   // Block hash
	   this.block_hash=block_hash;
	   
	   // Accuracy
	   this.accuracy=accuracy;
	   
	   // Hash
	   this.hash=UTILS.BASIC.hash(this.signer+
			                      this.block_hash);
				
	   // Sign packet
	   CAddress adr=UTILS.WALLET.getAddress(signer);
	   this.sign=adr.sign(this.hash);
    }
   
    public CResult check(CBlockPayload block)
    {	   
	   // Check status
	   //if (UTILS.STATUS.netstat.indexOf("CONFIRM")<1)
	   //	return new CResult(false, "Invalid stage", "CBlockSignPacket", 204);
	   
	   // Check balance
	   this.balance=UTILS.BLOCKS.getSignerBalance(signer);
	   
	   if (this.balance<10)
		   return new CResult(false, "Invalid balance", "CBlockSignPacket", 204);
	   
	   // Already signed
	   if (UTILS.BLOCKS.blocksigners.findSigner(this.signer)==true)
		   return new CResult(false, "Already signed", "CBlockSignPacket", 204);
	   
	   // Check hash
	   String h=UTILS.BASIC.hash(this.signer+
                                 this.block_hash);
	   if (!h.equals(this.hash))
		   return new CResult(false, "Invalid hash", "CBlockSignPacket", 204);
	   
	   //Check sig
	   CECC ecc=new CECC(this.signer);
	   if (!ecc.checkSig(this.hash, this.sign))
		   return new CResult(false, "Invalid signature", "CBlockSignPacket", 204);
	   
	   // Add to signatures
	   try
	   {
               Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	       ResultSet rs=s.executeQuery("SELECT * "
	   		                             + "FROM adr "
	   		                            + "WHERE adr='"+this.signer+"'");
	       rs.next();
	       double balance=rs.getDouble("balance");
	       
               // Close
               s.close();
               
	       // Add
	       UTILS.BLOCKS.blocksigners.addSigner(this);
	   }
	   catch (SQLException ex)
	   {
		   UTILS.LOG.log("SQLException", ex.getMessage(), "CBlockSignPayload.java", 68);
	   }
	   
	   // Return
	   return new CResult(true, "Ok", "CBlockSignPacket", 204);
   }
}
