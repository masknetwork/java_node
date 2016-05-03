// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.trade.feeds;

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
    
    // Type
    String type;
    
    // Symbol
    String symbol;
    
    // Real Symbol
    String rl_symbol;

    // Fee
    double fee;
    
    // Days
    long days;
    
    public CNewFeedComponentPayload(String feed_adr,
                                    String feed_symbol,
                                    String title,
                                    String description,
                                    String type,
                                    String symbol,
                                    String rl_symbol,
                                    double fee,
                                    long days) throws Exception
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
       
       // Type
       this.type=type;
       
       // RL symbol
       this.rl_symbol=rl_symbol;
       
       // Fee
       this.fee=fee;
       
       // Days
       this.days=days;    
       
       // Hash
       hash=UTILS.BASIC.hash(this.getHash()+
			     feed_symbol+
		             title+
                             description+
                             type+
                             symbol+
                             rl_symbol+
                             String.valueOf(fee)+
			     String.valueOf(days));
       
       // Sign
       this.sign();
    }
    
    public CResult check(CBlockPayload block) throws Exception
    {
        try
        {
            // Symbol valid
            if (!UTILS.BASIC.isSymbol(this.symbol))
              return new CResult(false, "Invalid title", "CNewFeedComponentPayload", 67);
            
             // Feed symbol valid
             if (!UTILS.BASIC.isSymbol(this.symbol))
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
             
             // Type
             if (this.type.equals("ID_CRYPTO") && 
                 this.type.equals("ID_FX") && 
                 this.type.equals("ID_COMM") && 
                 this.type.equals("ID_IND") && 
                 this.type.equals("ID_STOCKS") && 
                 this.type.equals("ID_OTHER"))
             throw new Exception("Invalid branch type, CNewFeedComponentPayload");
             
             // Hash
             String h=UTILS.BASIC.hash(this.getHash()+
			               feed_symbol+
		                       title+
                                       description+
                                       type+
                                       symbol+
                                       rl_symbol+
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
    
    public CResult commit(CBlockPayload block) throws Exception
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
            String expire=String.valueOf(this.block+(1440*this.days));
                    
            // Insert feed
            UTILS.DB.executeUpdate("INSERT INTO feeds_branches(feed_symbol, "
                                                              + "name, "
                                                              + "description, "
                                                              + "type, "
                                                              + "symbol, "
                                                              + "expire, "
                                                              + "fee, "
                                                              + "rl_symbol, "
                                                              + "block) VALUES('"+
                                                              this.feed_symbol+"', '"+
                                                              UTILS.BASIC.base64_encode(this.title)+"', '"+
                                                              UTILS.BASIC.base64_encode(this.description)+"', '"+
                                                              this.type+"', '"+
                                                              this.symbol+"', '"+
                                                              expire+"', '"+
                                                              this.fee+"', '"+
                                                              this.rl_symbol+"', '"+
                                                              this.block+"')");
         
        }
        else return res;
        
        // Return
	return new CResult(true, "Ok", "CNewFeedComponentPayload", 67); 
    }
    
    public boolean checkFeed(String feed_symbol, String symbol) throws Exception
    {
        try
        {      
           // Load data
           Statement s=UTILS.DB.getStatement();
           
           ResultSet rs=s.executeQuery("SELECT * "
                                       + "FROM feeds "
                                      + "WHERE symbol='"+feed_symbol+"'"
                                        + "AND adr='"+this.target_adr+"'");
           // Return results
           if (!UTILS.DB.hasData(rs))
               return false;
           
           rs=s.executeQuery("SELECT * "
                             + "FROM feeds_branches "
                            + "WHERE feed_symbol='"+feed_symbol+"' "
                              + "AND symbol='"+symbol+"'");
           
           // Return results
           if (!UTILS.DB.hasData(rs))
               return false;
           
           // Close
           rs.close(); s.close();
           
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
