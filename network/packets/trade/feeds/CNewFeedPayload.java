// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.trade.feeds;

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
    
    
    public CNewFeedPayload(String adr,
                           String title, 
                           String description, 
                           String website,
                           String symbol, 
                           long mkt_days) throws Exception
    {
       // Constructor
       super(adr);
        
       // Title
       this.title=title;
        
       // Description
       this.description=description;
       
       // Website
       this.website=website;
        
       // Symbol
       this.symbol=symbol;
        
       // Market days
       this.mkt_days=mkt_days;
       
       // Hash
       hash=UTILS.BASIC.hash(this.getHash()+
			     title+
                             description+  
                             website+
		             symbol+
                             String.valueOf(mkt_days));
       
       // Sign
       this.sign();
    }
    
    public CResult check(CBlockPayload block) throws Exception
    {  
         // Super class
         CResult res=super.check(block);
         if (res.passed==false) return res;
           
         // Title
         if (!UTILS.BASIC.titleValid(this.title))
             return new CResult(false, "Invalid title", "CNewFeedPayload", 67); 
         
         // Description
         if (!UTILS.BASIC.descriptionValid(this.description))
             return new CResult(false, "Invalid description", "CNewFeedPayload", 67); 
         
         // Symbol
         if (!UTILS.BASIC.symbolValid(this.symbol))
             return new CResult(false, "Invalid symbol", "CNewFeedPayload", 67); 
         
         // Website valid ?
         if (this.website.length()>0)
            if (!UTILS.BASIC.isLink(this.website))
              return new CResult(false, "Invalid website", "CNewFeedPayload", 67); 
         
         // Symbol already exist ?
         if (this.feedExist(symbol))
             return new CResult(false, "Feed already exist", "CNewFeedPayload", 67); 
         
         // Market Days
         if (!UTILS.BASIC.mktDays(this.mkt_days))
             return new CResult(false, "Invalid market days", "CNewFeedPayload", 67); 
         
         // Hash
         String h=UTILS.BASIC.hash(this.getHash()+
			           title+
                                   description+  
                                   website+
		                   symbol+
                                   String.valueOf(mkt_days));
         
         if (!h.equals(this.hash))
             return new CResult(false, "Invalid hash", "CNewFeedPayload", 67); 
         
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
        
        // Feed already exist ?
        if (res.passed)
        {
            // Expire
            String expire=String.valueOf(this.block+(1440*this.mkt_days));
                    
            // Insert feed
            UTILS.DB.executeUpdate("INSERT INTO feeds(adr,"
                                                   + "name, "
                                                   + "description, "
                                                   + "website, "
                                                   + "symbol, "
                                                   + "expire, "
                                                   + "block) VALUES('"+
                                                   this.target_adr+"', '"+
                                                   UTILS.BASIC.base64_encode(this.title)+"', '"+
                                                   UTILS.BASIC.base64_encode(this.description)+"', '"+
                                                   UTILS.BASIC.base64_encode(this.website)+"', '"+
                                                   this.symbol+"', '"+
                                                   expire+"', '"+
                                                   this.block+"')");
            
            // My address ?
            if (UTILS.WALLET.isMine(this.target_adr))
            UTILS.DB.executeUpdate("INSERT INTO feeds_sources (feed_symbol, "
                                                             + "url, "
                                                             + "next_run, "
                                                             + "ping_interval, "
                                                             + "adr) VALUES('"
                                                             +this.symbol+"', '"
                                                             +UTILS.BASIC.base64_encode(this.website)+"', "
                                                             + "'0' ,"
                                                             + "'30', '"
                                                             +this.target_adr+"')");
           
        }
        else return res;
        
        // Return
	return new CResult(true, "Ok", "CNewFeedPayload", 67); 
    }
    
    public boolean feedExist(String symbol) throws Exception
    {
        try
        {      
           // Load data
           Statement s=UTILS.DB.getStatement();
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
