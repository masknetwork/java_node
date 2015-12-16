package wallet.network.packets.assets;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.*;
import wallet.network.packets.*;
import wallet.network.packets.blocks.*;
import wallet.network.*;

public class CIssueAssetPayload extends CPayload 
{
	// Issuer address
	String adr;
	
	// Symbol
        String symbol;
        
        // Title
        String title;
        
        // Description
        String description;
        
        // Web page
        String web_page;
        
        // Pic
        String pic;
        
        // Market bid
        double mkt_bid;
        
        // Market days
        double mkt_days;
        
        // Qty
        long qty;
        
        // Transaction fee address
        String trans_fee_adr;
        
        // Transaction fee
        double trans_fee;
        
        // Can issue more assets
        String can_increase;
	
        public CIssueAssetPayload(String adr,
                                  String symbol,
                                  String title,
                                  String description,
                                  String web_page,
                                  String pic,
                                  double mkt_bid,
                                  long mkt_days,
                                  long qty,
                                  String trans_fee_adr,
                                  double trans_fee,
                                  String can_increase)
        {
	    super(adr);
	   
	    // Issuer address
	    this.adr=adr;
	
	    // Symbol
            this.symbol=symbol;
        
            // Title
            this.title=title;
        
            // Description
            this.description=description;
        
            // Web page
            this.web_page=web_page;
        
            // Pic
            this.pic=pic;
        
            // Market bid
            this.mkt_bid=mkt_bid;
        
            // Market days
            this.mkt_days=mkt_days;
        
            // Qty
            this.qty=qty;
        
            // Transaction fee address
            this.trans_fee_adr=trans_fee_adr;
        
            // Transaction fee
            this.trans_fee=trans_fee;
            
            // Can increase
            this.can_increase=can_increase;
	   
	   // Hash
 	   hash=UTILS.BASIC.hash(this.getHash()+
                                 this.adr+
                                 this.symbol+
                                 this.title+
                                 this.description+
                                 this.web_page+
                                 this.pic+
                                 UTILS.FORMAT.format(this.mkt_bid)+
                                 String.valueOf(this.mkt_days)+
                                 String.valueOf(this.qty)+
                                 this.trans_fee_adr+
                                 UTILS.FORMAT.format(this.trans_fee)+
                                 this.can_increase);
        
        // Sign
        this.sign();
   }
    
    public CResult check(CBlockPayload block)
    {
       // Super class
       CResult res=super.check(block);
       if (res.passed==false) return res;
    	
       // Qty
       if (this.qty<1000 || this.qty>1000000000)
    	   return new CResult(false, "Invalid qty", "CIssueAssetPayload.java", 79);
       
        // Issuer address
	if (UTILS.BASIC.adressValid(this.adr)==false)
	   return new CResult(false, "Invalid address", "CIssueAssetPayload.java", 79);
        
	// Symbol length
        if (!UTILS.BASIC.symbolValid(this.symbol))
           return new CResult(false, "Invalid symbol", "CIssueAssetPayload.java", 79);
        
       // Title
        if (!UTILS.BASIC.titleValid(this.title))
            return new CResult(false, "Invalid title", "CIssueAssetPayload.java", 79);
        
        // Description
        if (!UTILS.BASIC.descriptionValid(this.description))
            return new CResult(false, "Invalid description", "CIssueAssetPayload.java", 79);
        
       // Web page
        if (!this.web_page.equals(""))
          if (!UTILS.BASIC.isLink(this.web_page))
             return new CResult(false, "Invalid web page", "CIssueAssetPayload.java", 79);
        
        // Pic
        if (!this.pic.equals(""))
          if (!UTILS.BASIC.isLink(this.pic))
             return new CResult(false, "Invalid pic", "CIssueAssetPayload.java", 79);
        
        // Market bid
        if (this.mkt_bid<0.0001)
           return new CResult(false, "Invalid market bid", "CIssueAssetPayload.java", 79);
        
        // Market days
        if (mkt_days<1)
           return new CResult(false, "Invalid market days", "CIssueAssetPayload.java", 79);
        
        // Qty
        if (qty<100)
           return new CResult(false, "Invalid qty", "CIssueAssetPayload.java", 79);
        
        // Transaction fee address
        if (!this.trans_fee_adr.equals(""))
        {
            if (!UTILS.BASIC.adressValid(this.trans_fee_adr))
                return new CResult(false, "Invalid fee address", "CIssueAssetPayload.java", 79);
        }
        
        // Transaction fee
        if (this.trans_fee<0.01 || this.trans_fee>10)
            return new CResult(false, "Invalid transaction fee", "CIssueAssetPayload.java", 79);
       
        // Symbol exist
        try
        {
           Statement s=UTILS.DB.getStatement();
           ResultSet rs=s.executeQuery("SELECT * "
  		                       + "FROM assets "
  		                      + "WHERE symbol='"+this.symbol+"'");
           // Exist ?
           if (UTILS.DB.hasData(rs))
               return new CResult(false, "Asset symbol already exist", "CIssueAssetPayload.java", 79);
           
           // Close
           s.close();
        }
        catch (SQLException ex)
        {
               UTILS.LOG.log("SQLException", ex.getMessage(), "CRentDomainPayload.java", 84);
        }
        
        // Calculates hash
        String h=UTILS.BASIC.hash(this.getHash()+
                                  this.adr+
                                  this.symbol+
                                  this.title+
                                  this.description+
                                  this.web_page+
                                  this.pic+
                                  UTILS.FORMAT.format(this.mkt_bid)+
                                  String.valueOf(this.mkt_days)+
                                  String.valueOf(this.qty)+
                                  this.trans_fee_adr+
                                  UTILS.FORMAT.format(this.trans_fee)+
                                  this.can_increase);
        
        // Check hash
        if (!this.hash.equals(h))
           return new CResult(false, "Invalid hash", "CIssueAssetPayload.java", 79);
        
        // Signature
        if (this.checkSig()==false)
           return new CResult(false, "Invalid signature", "CIssueAssetPayload.java", 79);
        
        // Return
  	return new CResult(true, "Ok", "CNewAssetPayload", 67);
    }
    
    public CResult commit(CBlockPayload block)
    {
 	   CResult res=this.check(block);
 	   if (res.passed==false) return res;
 	  
           // Insert asset
           UTILS.DB.executeUpdate("INSERT INTO assets(adr, "
                                                   + "symbol, "
                                                   + "title, "
                                                   + "description, "
                                                   + "web_page, "
                                                   + "pic, "
                                                   + "mkt_bid, "
                                                   + "mkt_days, "
                                                   + "qty, "
                                                   + "trans_fee_adr, "
                                                   + "trans_fee, "
                                                   + "can_increase, "
                                                   + "block) "
                                          + "VALUES('"+this.adr+"', '"+
                                                       this.symbol+"', '"+
                                                       UTILS.BASIC.base64_encode(this.title)+"', '"+
                                                       UTILS.BASIC.base64_encode(this.description)+"', '"+
                                                       UTILS.BASIC.base64_encode(this.web_page)+"', '"+
                                                       UTILS.BASIC.base64_encode(this.pic)+"', '"+
                                                       UTILS.FORMAT.format(this.mkt_bid)+"', '"+
                                                       String.valueOf(this.mkt_days)+"', '"+
                                                       String.valueOf(this.qty)+"', '"+
                                                       this.trans_fee_adr+"', '"+
                                                       UTILS.FORMAT.format(this.trans_fee)+"', '"+
                                                       this.can_increase+"', '"+
                                                       String.valueOf(UTILS.BASIC.block())+"')");
           
           // Hash
           UTILS.ROWHASH.updateLastID("assets");
           
           // Update 
           UTILS.DB.executeUpdate("INSERT INTO assets_owners(owner, "
                                                           + "symbol, "
                                                           + "qty, "
                                                           + "block) "
                                                 + "VALUES('"+this.adr+"', '"+
                                                              this.symbol+"', '"+
                                                              String.valueOf(this.qty)+"', '"+
                                                              String.valueOf(this.block)+"')");
           
           // Hash
           UTILS.ROWHASH.updateLastID("assets_owners");
           
            // Superclass
           super.commit(block);
           
    	   // Return 
    	   return new CResult(true, "Ok", "CAddNewAssetPayload.java", 149);
     }
 }