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
    // Feed ID
    long feedID;
    
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
    long days;
    
    
    public CNewFeedPayload(String adr,
                           String title, 
                           String description, 
                           String website,
                           String symbol, 
                           long days) throws Exception
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
       this.days=days;
       
       // Feed ID
       this.feedID=UTILS.BASIC.getID();
       
       // Hash
       hash=UTILS.BASIC.hash(this.getHash()+
			     feedID+
                             title+
                             description+  
                             website+
		             symbol+
                             String.valueOf(days));
       
       // Sign
       this.sign();
    }
    
    public void check(CBlockPayload block) throws Exception
    {  
         // Super class
         super.check(block);
         
         
         // Title
         if (!UTILS.BASIC.isTitle(this.title))
             throw new Exception("Invalid title - CNewFeedPayload.java"); 
         
         // Description
         if (!UTILS.BASIC.isDesc(this.description))
             throw new Exception("Invalid description - CNewFeedPayload.java"); 
         
         // Symbol
         if (!UTILS.BASIC.isSymbol(this.symbol))
             throw new Exception("Invalid symbol - CNewFeedPayload.java"); 
         
         // Website valid ?
         if (this.website.length()>0)
            if (!UTILS.BASIC.isLink(this.website))
              throw new Exception("Invalid website - CNewFeedPayload.java"); 
         
         // Symbol already exist ?
         if (UTILS.BASIC.isFeed(symbol))
             throw new Exception("Feed already exist - CNewFeedPayload.java"); 
         
         // Hash
         String h=UTILS.BASIC.hash(this.getHash()+
                                   feedID+
			           title+
                                   description+  
                                   website+
		                   symbol+
                                   String.valueOf(days));
         
         if (!h.equals(this.hash))
             throw new Exception("Invalid hash - CNewFeedPayload.java"); 
    }
    
    public void commit(CBlockPayload block) throws Exception
    {
        // Check
        this.check(block);
        
        // Insert feed
        UTILS.DB.executeUpdate("INSERT INTO feeds "
                                     + "SET feedID='"+this.feedID+"', "
                                          + "adr='"+this.target_adr+"',"
                                          + "name='"+UTILS.BASIC.base64_encode(this.title)+"', "
                                          + "description='"+UTILS.BASIC.base64_encode(this.description)+"', "
                                          + "website='"+UTILS.BASIC.base64_encode(this.website)+"', "
                                          + "symbol='"+this.symbol+"', "
                                          + "expire='"+(this.block+(1440*this.days))+"', "
                                          + "block='"+this.block+"'"); 
    }
  
}
