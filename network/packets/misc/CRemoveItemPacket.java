// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.misc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.*;

public class CRemoveItemPacket extends CBroadcastPacket
{
    public CRemoveItemPacket(String fee_adr, 
                             String adr, 
                             String table, 
                             String rowhash,
                             String packet_sign,
                             String payload_sign) throws Exception
    {
       super("ID_REMOVE_ITEM_PACKET");
	
        // Builds the payload class
	CRemoveItemPayload dec_payload=new CRemoveItemPayload(adr, 
	                                                      table, 
	                                                      rowhash);
						
        // Build the payload
        this.payload=UTILS.SERIAL.serialize(dec_payload);
						
	// Network fee
	fee=new CFeePayload(fee_adr);
				   
	// Sign packet
	this.sign();
   }
			 
   // Check 
   public CResult check(CBlockPayload block) throws Exception
   {
	// Super class
	CResult res=super.check(block);
	if (res.passed==false) return res;
			   	
	// Check type
	if (!this.tip.equals("ID_REMOVE_ITEM_PACKET")) 
	   return new CResult(false, "Invalid packet type", "CRemoveItemPacket", 42);
        
         // Deserialize transaction data
	 CRemoveItemPayload dec_payload=(CRemoveItemPayload) UTILS.SERIAL.deserialize(payload);
	   
         // Check payoad
         res=dec_payload.check(block);
         if (!res.passed) return res;
			   	
	// Return 
	return new CResult(true, "Ok", "CRemoveItemPacket", 74);
   }
			   
   public CResult commit(CBlockPayload block) throws Exception
   {
	// Superclass
	CResult res=super.commit(block);
	if (res.passed==false) return res;
			   	  
	// Deserialize transaction data
	CRemoveItemPayload ass_payload=(CRemoveItemPayload) UTILS.SERIAL.deserialize(payload);
        
        res=ass_payload.commit(block);	  
	if (res.passed==false) return res;
        
        
	// Return 
	return new CResult(true, "Ok", "CRemoveItemPacket", 9);   
   }
}
