// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.trade.speculative;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CPayload;
import wallet.network.packets.assets.CIssueAssetPayload;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.CTransPayload;

public class CNewSpecMarketPayload extends CPayload
{
    // Symbol
    long mktID; 
    
    // Feed 1
    String feed; 
    String branch; 
    
    // Currency
    String cur; 
    
    // Max leverage
    long max_leverage;
    
    // Max leverage
    double max_total_margin;
    
    // Spread
    double spread;
    
    // Title
    String title;
    
    // Description
    String desc;
    
    // Max feed down
    long max_down;
    
    // Max feed up
    long max_up;
    
    // Days
    long days;
   
                                         
    public CNewSpecMarketPayload(String mkt_adr, 
                                 String feed, 
			         String branch, 
			         String cur, 
				 long max_leverage,
                                 double max_total_margin,
			         double spread,
				 String title,
				 String desc,
                                 long max_down,
                                 long max_up,
				 long days) throws Exception
    {
         // Constructor
        super(mkt_adr);
        
        // Symbol
        this.mktID=UTILS.BASIC.getID(); 
        
        // Feed 1
        this.feed=feed; 
        this.branch=branch; 
	
        // Currency
        this.cur=cur;
        
	// Max leverage
        this.max_leverage=max_leverage;
        
        // Max leverage
        this.max_total_margin=max_total_margin;
        
        // Spread
        this.spread=spread;
        
        // Title
	this.title=title;
        
        // Description
        this.desc=desc;
        
        // Days
        this.days=days;
        
        // Max down
        this.max_down=max_down;
        
        // Max up
        this.max_up=max_up;
        
        // Hash
        this.hash=UTILS.BASIC.hash(this.getHash() + 
                                   this.mktID + 
			           this.feed + 
			           this.branch + 
				   this.cur + 
				   this.max_leverage +
			           this.max_total_margin +
			           this.spread +
				   this.title+
				   this.desc+
                                   this.days+
                                   this.max_down+
				   this.max_up);
        
        // Sign
        this.sign();
     
    }
    
    public void check(CBlockPayload block) throws Exception
    {
        // Super class
   	super.check(block);
          
        // Valid ID
        if (UTILS.BASIC.existID(mktID))
           throw new Exception("Invalid ID - CNewFeedMarketPayload.java"); 
           
        // Another market using the same address and a diffrent currency ?
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM feeds_spec_mkts "
                                          + "WHERE adr='"+this.target_adr+"' "
                                            + "AND cur<>'"+this.cur+"'");
        
        // Has data ?
        if (UTILS.DB.hasData(rs))
            throw new Exception("Invalid currency - CNewFeedMarketPayload.java"); 
           
        // Feed 1
        if (!UTILS.BASIC.isBranch(this.feed, this.branch))
            throw new Exception("Invalid feed 1 - CNewFeedMarketPayload.java"); 
           
        // Currency
        if (!this.cur.equals("MSK"))
           if (!UTILS.BASIC.isAsset(this.cur))
             throw new Exception("Invalid currency - CNewFeedMarketPayload.java"); 
        
	// Maximum leverage
        if (max_leverage<1 || this.max_leverage>10000)
            throw new Exception("Invalid max leverage - CNewFeedMarketPayload.java"); 
           
        // Maximum total margin
        if (max_total_margin<0.0001 || this.max_total_margin>25)
           throw new Exception("Invalid max total margin - CNewFeedMarketPayload.java"); 
        
        // Spread
        if (spread<0)
          throw new Exception("Invalid spread - CNewFeedMarketPayload.java"); 
        
	// Days
        if (this.days<100)
            throw new Exception("Invalid days - CNewFeedMarketPayload.java");              
                                         
        // Title
        if (!UTILS.BASIC.isTitle(this.title))
           throw new Exception("Invalid title - CNewFeedMarketPayload.java"); 
    
        // Description
        if (!UTILS.BASIC.isDesc(this.desc))
            throw new Exception("Invalid description - CNewFeedMarketPayload.java"); 
        
        // Max down
        if (this.max_down>100 || this.max_down<0)
            throw new Exception("Invalid max down parameter - CNewFeedMarketPayload.java"); 
        
        // Max up
        if (this.max_up<1 || this.max_up>100000)
            throw new Exception("Invalid max up parameter - CNewFeedMarketPayload.java"); 
        
        // Market address is a feed ?
        rs=UTILS.DB.executeQuery("SELECT * "
                                 + "FROM feeds "
                                + "WHERE adr='"+this.target_adr+"'");
        
        // Has data ?
        if (UTILS.DB.hasData(rs))
           throw new Exception("A data feed is attached to address - CNewFeedMarketPayload.java");
        
        // Hash
        String h=UTILS.BASIC.hash(this.getHash() + 
                                  this.mktID + 
			          this.feed + 
			          this.branch + 
             	                  this.cur + 
				  this.max_leverage +
			          this.max_total_margin +
			          this.spread +
				  this.title+
				  this.desc+
				  this.days+
                                  this.max_down+
                                  this.max_up);
            
        // Check hash
        if (!h.equals(this.hash))
           throw new Exception("Invalid hash - CNewFeedMarketPayload.java"); 
    }
    
    public void commit(CBlockPayload block) throws Exception
    {
        // Super
        super.commit(block);
        
        // Clear transactions
        UTILS.ACC.clearTrans(this.hash, "ID_ALL", this.block);
        
        // Insert market
        UTILS.DB.executeUpdate("INSERT INTO feeds_spec_mkts "
                                     + "SET adr='"+this.target_adr+"', "
                                         + "feed='"+this.feed+"', "
                                         + "branch='"+this.branch+"', "
                                         + "cur='"+this.cur+"', "
                                         + "mktID='"+this.mktID+"', "
                                         + "max_leverage='"+this.max_leverage+"', "
                                         + "max_total_margin='"+this.max_total_margin+"', "
                                         + "spread='"+this.spread+"', "
                                         + "title='"+UTILS.BASIC.base64_encode(this.title)+"', "
                                         + "description='"+UTILS.BASIC.base64_encode(this.desc)+"', "
                                         + "max_down='"+this.max_down+"', "
                                         + "max_up='"+this.max_up+"', "
                                         + "status='ID_ONLINE', "
                                         + "expire='"+(this.block+(this.days*1440))+"'");
    }  
}
