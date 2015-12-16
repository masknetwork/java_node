package wallet.network.packets.domains;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.*;


public class CTransferDomainPacket extends CBroadcastPacket 
{
   public CTransferDomainPacket(String fee_adr, String domain, String toAdr)
   {
	super("ID_TRANSFER_DOMAIN_PACKET");
	
        String adr="";
        
         try
         {
           // Load seller address
           Statement s=UTILS.DB.getStatement();
           ResultSet rs=s.executeQuery("SELECT * "
  		                             + "FROM domains "
  		                            + "WHERE domain='"+domain+"'");
           // Next
           rs.next();
           
           // Address
           adr=rs.getString("adr");
           
           // Close
           s.close();
        }
        catch (SQLException ex)
        {
           UTILS.LOG.log("SQLException", ex.getMessage(), "CTransferDomainPacket.java", 60);
        }
         
	// Builds the payload class
	CTransferDomainPayload dec_payload=new CTransferDomainPayload(adr, 
	                                                              domain, 
	                                                              toAdr);
						
        // Build the payload
        this.payload=UTILS.SERIAL.serialize(dec_payload);
						
	// Network fee
	fee=new CFeePayload(fee_adr);
				   
	// Sign packet
	this.sign();
   }
			 
   // Check 
   public CResult check(CBlockPayload block)
   {
	// Super class
	CResult res=super.check(block);
	if (res.passed==false) return res;
			   	
	// Check type
	if (!this.tip.equals("ID_TRANSFER_DOMAIN_PACKET")) 
	   return new CResult(false, "Invalid packet type", "CTransferDomainPacket", 42);
        
         // Deserialize transaction data
	 CTransferDomainPayload dec_payload=(CTransferDomainPayload) UTILS.SERIAL.deserialize(payload);
	   
         // Check payoad
         res=dec_payload.check(block);
         if (!res.passed) return res;
			   	
	// Return 
	return new CResult(true, "Ok", "CTransferDomainPacket", 74);
   }
			   
   public CResult commit(CBlockPayload block)
   {
	// Superclass
	CResult res=super.commit(block);
	if (res.passed==false) return res;
			   	  
	// Deserialize transaction data
	CTransferDomainPayload ass_payload=(CTransferDomainPayload) UTILS.SERIAL.deserialize(payload);
        
        res=ass_payload.commit(block);	  
	if (res.passed==false) return res;
        
        
	// Return 
	return new CResult(true, "Ok", "CTransferDomainPacket", 9);   
   }
}
