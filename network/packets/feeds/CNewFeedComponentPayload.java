package wallet.network.packets.feeds;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.network.packets.*;
import wallet.network.*;
import wallet.kernel.*;
import wallet.network.packets.blocks.CBlockPayload;


public class CNewFeedComponentPayload extends CPayload
{
    // Feed Symbol
    String feed_symbol;
    
    // Title
    String title;
    
    // Description
    String description;
    
    // Symbol
    String symbol;

    // Fee
    double fee;
    
    // Days
    long days;
    
    public CNewFeedComponentPayload(String feed_adr,
                                    String feed_symbol,
                                    String title,
                                    String description,
                                    String symbol,
                                    double fee,
                                    long days)
    {
       // Super
       super(feed_adr);
    
       // Feed Symbol
       this.feed_symbol=feed_symbol;
    
       // Title
       this.title=title;
    
       // Description
       this.description=description;
    
       // Symbol
       this.symbol=symbol;
       
       // Fee
       this.fee=fee;
       
       // Days
       this.days=days;    
       
       // Hash
       hash=UTILS.BASIC.hash(this.getHash()+
			     feed_symbol+
		             title+
                             description+
                             symbol+
                             String.valueOf(fee)+
			     String.valueOf(days));
       
       // Sign
       this.sign();
    }
    
    public CResult check(CBlockPayload block)
    {
        try
        {
            // Symbol valid
            if (!UTILS.BASIC.symbolValid(this.symbol))
              return new CResult(false, "Invalid title", "CNewFeedComponentPayload", 67);
            
             // Feed symbol valid
             if (!UTILS.BASIC.symbolValid(this.symbol))
                return new CResult(false, "Invalid feed symbol", "CNewFeedComponentPayload", 67);
             
             // Check feed
             if (this.checkFeed(this.feed_symbol, this.symbol))
              return new CResult(false, "Feed already exist", "CNewFeedComponentPayload", 67);
         
             // Fee
             if (this.fee<0)
                return new CResult(false, "Invalid fee", "CNewFeedComponentPayload", 67);
             
             // Title
             if (!UTILS.BASIC.titleValid(this.title))
                 return new CResult(false, "Invalid title", "CNewFeedComponentPayload", 67);
         
             // Description
             if (!UTILS.BASIC.descriptionValid(this.description))
                return new CResult(false, "Invalid description", "CNewFeedComponentPayload", 67);
         
             // Days
             if (!UTILS.BASIC.mktDays(days))
                 return new CResult(false, "Invalid days", "CNewFeedComponentPayload", 67);
             
             // Hash
             String h=UTILS.BASIC.hash(this.getHash()+
			               feed_symbol+
		                       title+
                                       description+
                                       symbol+
                                       String.valueOf(fee)+
			               String.valueOf(days));
             if (!h.equals(hash))
                return new CResult(false, "Invalid hash", "CNewFeedComponentPayload", 67);
        }
        catch (Exception ex)
        {
           UTILS.LOG.log("SQLException", ex.getMessage(), "CNewFeedComponentPayload.java", 60);
        }
        
        // Return
	return new CResult(true, "Ok", "CNewFeedPayload", 67);
    }
    
    public CResult commit(CBlockPayload block)
    {
        // Super class
        CResult res=super.check(block);
        if (res.passed==false) return res;
        
        // Check
        res=this.check(block);
        
        // Feed exist ?
        if (res.passed)
        {
           // Expire
            String expire=String.valueOf(this.block+(864*this.days));
                    
            // Insert feed
            UTILS.DB.executeUpdate("INSERT INTO feeds_components(feed_symbol, "
                                                              + "title, "
                                                              + "description, "
                                                              + "symbol, "
                                                              + "val, "
                                                              + "expire, "
                                                              + "fee, "
                                                              + "block) VALUES('"+
                                                              this.feed_symbol+"', '"+
                                                              UTILS.BASIC.base64_encode(this.title)+"', '"+
                                                              UTILS.BASIC.base64_encode(this.description)+"', '"+
                                                              this.symbol+"', '0', '"+
                                                              expire+"', '"+
                                                              this.fee+"', '"+
                                                              this.block+"')");
            
            // Rowhash
            UTILS.ROWHASH.updateLastID("feeds_components");    
        }
        else return res;
        
        // Return
	return new CResult(true, "Ok", "CNewFeedComponentPayload", 67); 
    }
    
    public boolean checkFeed(String feed_symbol, String symbol)
    {
        try
        {      
           // Load data
           Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
           
           ResultSet rs=s.executeQuery("SELECT * "
                                       + "FROM feeds "
                                      + "WHERE symbol='"+feed_symbol+"'"
                                        + "AND adr='"+this.target_adr+"'");
           // Return results
           if (!UTILS.DB.hasData(rs))
               return false;
           
           rs=s.executeQuery("SELECT * "
                             + "FROM feeds_components "
                            + "WHERE feed_symbol='"+feed_symbol+"' "
                              + "AND symbol='"+symbol+"'");
           
           // Return results
           if (!UTILS.DB.hasData(rs))
               return false;
           
           // Close
           s.close();
           
           // Return 
           return true;
        }
        catch (SQLException ex)
        {
           UTILS.LOG.log("SQLException", ex.getMessage(), "CNewFeedComponentPayload.java", 187);
        }
        
        return false;
    }
}
