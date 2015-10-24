package wallet.network.packets.feeds;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.network.packets.*;
import wallet.kernel.*;
import wallet.network.CResult;
import wallet.network.packets.blocks.CBlockPayload;

public class CNewFeedPayload extends CPayload
{
    // Title
    String title;
    
    // Description
    String description;
    
    // Pic
    String pic;
    
    // Website
    String website;
    
    // Symbol
    String symbol;
    
    // Market days
    long mkt_days;
    
    // Market bid
    double mkt_bid;
    
    public CNewFeedPayload(String adr,
                           String title, 
                           String description, 
                           String website,
                           String symbol, 
                           double mkt_bid, 
                           long mkt_days)
    {
       // Constructor
       super(adr);
        
       // Title
       this.title=title;
        
       // Description
       this.description=description;
       
       // Pic
       this.pic=pic;
       
       // Website
       this.website=website;
        
       // Symbol
       this.symbol=symbol;
        
       // Market bid
       this.mkt_bid=mkt_bid;
        
       // Market days
       this.mkt_days=mkt_days;
       
       // Hash
       hash=UTILS.BASIC.hash(this.getHash()+
			     title+
                             description+  
                             pic+
                             website+
		             symbol+
                             String.valueOf(mkt_bid)+
			     String.valueOf(mkt_days));
       
       // Sign
       this.sign();
    }
    
    public CResult check(CBlockPayload block)
    {  
         // Super class
         CResult res=super.check(block);
         if (res.passed==false) return res;
           
         // Title
         if (!UTILS.BASIC.titleValid(this.title))
             return new CResult(false, "Invalid title", "CNewFeedPayload", 67); 
         
         // Description
         if (!UTILS.BASIC.titleValid(this.description))
             return new CResult(false, "Invalid description", "CNewFeedPayload", 67); 
         
         // Symbol
         if (!UTILS.BASIC.titleValid(this.symbol))
             return new CResult(false, "Invalid symbol", "CNewFeedPayload", 67); 
         
         // Pic valid
         if (this.pic.length()>0)
            if (!UTILS.BASIC.isLink(this.pic))
              return new CResult(false, "Invalid picture", "CNewFeedPayload", 67); 
          
         // Website valid ?
         if (this.website.length()>0)
            if (!UTILS.BASIC.isLink(this.website))
              return new CResult(false, "Invalid website", "CNewFeedPayload", 67); 
         
         // Symbol already exist ?
         if (this.feedExist(symbol))
             return new CResult(false, "Feed already exist", "CNewFeedPayload", 67); 
         
         // Market bid
         if (!UTILS.BASIC.mktBidValid(this.mkt_bid))
             return new CResult(false, "Invalid market bid", "CNewFeedPayload", 67); 
         
         // Market Days
         if (!UTILS.BASIC.mktDays(this.mkt_days))
             return new CResult(false, "Invalid market days", "CNewFeedPayload", 67); 
         
         // Hash
         String h=UTILS.BASIC.hash(this.getHash()+
			           title+
                                   description+  
                                   pic+
                                   website+
		                   symbol+
                                   String.valueOf(mkt_bid)+
			           String.valueOf(mkt_days));
         
         if (!h.equals(this.hash))
             return new CResult(false, "Invalid hash", "CNewFeedPayload", 67); 
         
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
        
        // Feed already exist ?
        if (res.passed)
        {
            // Expire
            String expire=String.valueOf(this.block+(864*this.mkt_days));
                    
            // Insert feed
            UTILS.DB.executeUpdate("INSERT INTO feeds(adr,"
                                                   + "title, "
                                                   + "description, "
                                                   + "pic, "
                                                   + "website, "
                                                   + "symbol, "
                                                   + "mkt_bid, "
                                                   + "mkt_days, "
                                                   + "expire, "
                                                   + "block) VALUES('"+
                                                   this.target_adr+"', '"+
                                                   UTILS.BASIC.base64_encode(this.title)+"', '"+
                                                   UTILS.BASIC.base64_encode(this.description)+"', '"+
                                                   UTILS.BASIC.base64_encode(this.pic)+"', '"+
                                                   UTILS.BASIC.base64_encode(this.website)+"', '"+
                                                   this.symbol+"', '"+
                                                   String.valueOf(this.mkt_bid)+"', '"+
                                                   String.valueOf(this.mkt_days)+"', '"+
                                                   expire+"', '"+
                                                   this.block+"')");
            
            // Rowhash
            UTILS.ROWHASH.updateLastID("feeds");
        }
        else return res;
        
        // Return
	return new CResult(true, "Ok", "CNewFeedPayload", 67); 
    }
    
    public boolean feedExist(String symbol)
    {
        try
        {      
           // Load data
           Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
           ResultSet rs=s.executeQuery("SELECT * "
                                       + "FROM feeds "
                                      + "WHERE symbol='"+symbol+"'");
           
           // Return results
           if (UTILS.DB.hasData(rs))
               return true;
           else 
               return false;
        }
        catch (SQLException ex)
        {
           UTILS.LOG.log("SQLException", ex.getMessage(), "CBuyDomainPayload.java", 60);
        }
        
        return false;
    }
}
