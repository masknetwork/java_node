package wallet.network.packets.feeds;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
                                  String symbol, 
                                  String rl_symbol, 
                                  double branch_fee,
                                  long days)
    {
         // Constructor
        super("ID_NEW_FEED_BRANCH_PACKET");
        
        try
        {
            // Load feed data
            Statement s=UTILS.DB.getStatement();
            ResultSet rs=s.executeQuery("SELECT * "
                                        + "FROM feeds "
                                       + "WHERE symbol='"+feed_symbol+"'");
            rs.next();
        
            // Payload
            CNewFeedComponentPayload dec_payload=new CNewFeedComponentPayload(rs.getString("adr"),
                                                                              feed_symbol,
                                                                              title,
                                                                              description,
                                                                              symbol,
                                                                              rl_symbol,
                                                                              branch_fee,
                                                                              days);
            
            // Close
            s.close();
        
	    // Build the payload
	    this.payload=UTILS.SERIAL.serialize(dec_payload);
					
            // Network fee
	    fee=new CFeePayload(fee_adr, days*0.0001);
        }
        catch (SQLException ex)
        {
           UTILS.LOG.log("SQLException", ex.getMessage(), "CNewFeedComponentPacket.java", 60);
        }
        
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
	    if (!this.tip.equals("ID_NEW_FEED_BRANCH_PACKET")) 
	       return new CResult(false, "Invalid packet type", "CNewFeedComponentPacket.java", 42);
		   
	    // Deserialize transaction data
	    CNewFeedComponentPayload dec_payload=(CNewFeedComponentPayload) UTILS.SERIAL.deserialize(payload);
		   
	    // Check payload
	    res=dec_payload.check(block);
            if (!res.passed) return res;
            
            // Check fee
            if (this.fee.amount<(0.0001*dec_payload.days))
               return new CResult(false, "Invalid price", "CNewFeedComponentPacket.java", 42);
            
	    // Return 
	    return new CResult(true, "Ok", "CNewFeedComponentPacket.java", 74);
	}
		   
	public CResult commit(CBlockPayload block)
	{
	    // Superclass
            CResult res=super.commit(block);
	    if (res.passed==false) return res;
		   	  
	    // Deserialize transaction data
	    CNewFeedComponentPayload dec_payload=(CNewFeedComponentPayload) UTILS.SERIAL.deserialize(payload);

	    // Commit payload
	    res=dec_payload.commit(block);	  
	    if (res.passed==false) return res;
	    		  
	    // Return 
            return new CResult(true, "Ok", "CNewFeedComponentPacket.java", 9);
      }
}
