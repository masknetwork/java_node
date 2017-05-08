// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.trade.feeds;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.CPackets;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CBroadcastPacket;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.CFeePayload;

public class CNewFeedComponentPacket extends CBroadcastPacket
{
   public CNewFeedComponentPacket(String fee_adr,
                                  String feed_symbol,
                                  String title, 
                                  String description, 
                                  String type,
                                  String symbol, 
                                  String rl_symbol, 
                                  double branch_fee,
                                  long days) throws Exception
    {
         // Constructor
        super("ID_NEW_FEED_BRANCH_PACKET");
        
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM feeds "
                                          + "WHERE symbol='"+feed_symbol+"'");
        rs.next();
        
        // Payload
        CNewFeedComponentPayload dec_payload=new CNewFeedComponentPayload(rs.getString("adr"),
                                                                              feed_symbol,
                                                                              title,
                                                                              description,
                                                                              type,
                                                                              symbol,
                                                                              rl_symbol,
                                                                              branch_fee,
                                                                              days);
            
        // Build the payload
	this.payload=UTILS.SERIAL.serialize(dec_payload);
					
        // Network fee
        CFeePayload fee=new CFeePayload(fee_adr,  0.0001*days);
	this.fee_payload=UTILS.SERIAL.serialize(fee);
        
	// Sign packet
	this.sign();
    }
    
    // Check  
    public void check(CBlockPayload block) throws Exception
    {
	// Super class
	super.check(block);
	    	   	
	// Check type
	if (!this.tip.equals("ID_NEW_FEED_BRANCH_PACKET")) 
	   throw new Exception("Invalid packet type - CNewFeedComponentPacket.java");
		   
	// Deserialize transaction data
	CNewFeedComponentPayload dec_payload=(CNewFeedComponentPayload) UTILS.SERIAL.deserialize(payload);
		   
	// Check payload
	dec_payload.check(block);
        
        // Deserialize payload
        CFeePayload fee=(CFeePayload) UTILS.SERIAL.deserialize(fee_payload);
     
        // Check fee
        if (fee.amount<(0.0001*dec_payload.days))
            throw new Exception("Invalid price - CNewFeedComponentPacket.java");
        
        // Footprint
        CPackets foot=new CPackets(this);
        foot.add("Feed Symbol", dec_payload.feed_symbol);
        foot.add("Title", dec_payload.feed_symbol);
        foot.add("Description", dec_payload.feed_symbol);
        foot.add("Type", dec_payload.feed_symbol);
        foot.add("Symbol", dec_payload.feed_symbol);
        foot.add("RL Symbol", dec_payload.feed_symbol);
        foot.add("Branch Fee", dec_payload.feed_symbol);
        foot.add("Days", dec_payload.days);
        foot.write();
    }
		   
	
}
