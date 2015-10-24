package wallet.network.packets.markets.feeds;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CPayload;
import wallet.network.packets.assets.CIssueAssetPayload;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.CTransPayload;

public class CNewFeedMarketPayload extends CPayload
{
    // Tip
    String tip="";
    
    // Market address
    String mkt_adr; 
    
    // Market symbol
    String mkt_symbol; 
    
    // Feed
    String feed; 
    
    // Branch
    String branch; 
    
    // Asset
    String asset; 
    
    // Asset qty
    long asset_qty; 
    
    // Currency
    String cur; 
    
    // Name
    String name; 
    
    // Description
    String desc;
    
    // Fee address
    String fee_adr; 
    
    // Market fee
    double mkt_fee;
    
    // Decimals
    int decimals;
    
    // Maximum leverage
    long max_leverage;
    
    // Minimum hold time
    long min_hold;
    
    // Bid
    double bid;   
    
    // Days
    long days;
    
    // Initial deposit
    CTransPayload trans;
                                         
    public CNewFeedMarketPayload(String tip,
                                 String mkt_adr, 
			         String mkt_symbol, 
			         String feed, 
			         String branch, 
			         String asset, 
				 long asset_qty, 
			         String cur, 
			         String name, 
			         String desc, 
			         String fee_adr, 
			         double mkt_fee,
				 int decimals,
                                 long max_leverage,
                                 long min_hold,
				 double bid, 
			         long days)
    {
        // Constructor
        super(mkt_adr);
        
        // Tip
        this.tip=tip;
    
        // Title
        this.name=name;
    
        // Description
        this.desc=desc;
    
        // Symbol
        this.mkt_symbol=mkt_symbol;
    
        // Asset symbol
        this.asset=asset;
        
        // Asset qty
        this.asset_qty=asset_qty;

        // Currency symbol
        this.cur=cur;
    
        // Feed symbols
        this.feed=feed;
    
        // Feed component symbol
        this.branch=branch;
    
        // Market fee
        this.mkt_fee=mkt_fee;
    
        // Market fee address
        this.fee_adr=fee_adr;
        
        // Market withdraw limit
        this.decimals=decimals;
        
        // Maximum leverage
        this.max_leverage=max_leverage;
        
        // Minimum hold time
        this.min_hold=min_hold;
    
        // Max leverage
        this.bid=bid;
    
        // Market bid
        this.days=days;
        
        // Finds feed value
        double val=UTILS.BASIC.getFeedVal(feed, branch);
        
         // Hash
        hash=UTILS.BASIC.hash(this.getHash()+
                              this.tip+
                              this.mkt_symbol+ 
			      this.feed+ 
			      this.branch+ 
			      this.asset+ 
			      String.valueOf(this.asset_qty)+ 
			      this.cur+ 
			      this.name+ 
			      this.desc+ 
			      this.fee_adr+ 
			      String.valueOf(this.mkt_fee)+
			      String.valueOf(this.decimals)+
                              String.valueOf(this.max_leverage)+
                              String.valueOf(this.min_hold)+
			      String.valueOf(this.bid)+ 
			      String.valueOf(this.days));
        
        // Sign
        this.sign();
    }
    
   
    
    public boolean feedSymbolValid(String symbol)
    {
         try
         {
            Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs=s.executeQuery("SELECT * FROM feeds WHERE symbol='"+symbol+"'");
           
            if (UTILS.DB.hasData(rs))
            {
              s.close();
              return true; 
            }
         
            // Close
            s.close();
         }
         catch (SQLException ex) 
       	 {  
       	    UTILS.LOG.log("SQLException", ex.getMessage(), "CFeedMarketgMarketPayload.java", 57);
         }
         
         // Return
         return false;
    }
    
    public boolean feedComponentValid(String feed_symbol, String symbol)
    {
         try
         {
            Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs=s.executeQuery("SELECT * "
                                       + "FROM feeds_components "
                                      + "WHERE feed_symbol='"+feed_symbol+"' "
                                        + "AND symbol='"+symbol+"'");
           
            if (UTILS.DB.hasData(rs))
            {
              s.close();
              return true; 
            }
         
            // Close
            s.close();
         }
         catch (SQLException ex) 
       	 {  
       	    UTILS.LOG.log("SQLException", ex.getMessage(), "CNewFeedMarketPayload.java", 57);
         }
         
         // Return
         return false;
    }
    
    public CResult check(CBlockPayload block)
    {
        try
        {
           // Tip
           if (!tip.equals("ID_REGULAR") && !tip.equals("ID_SPECULATIVE"))
               return new CResult(false, "Invalid market type", "CNewFeedMarketPayload.java", 74); 
        
           // Title
           if (!UTILS.BASIC.titleValid(this.name))
               return new CResult(false, "Invalid title", "CNewFeedMarketPayload.java", 74); 
    
           // Description
           if (!UTILS.BASIC.descriptionValid(this.desc))
               return new CResult(false, "Invalid description", "CNewFeedMarketPayload.java", 74); 
    
           // Symbol
           if (!UTILS.BASIC.symbolValid(this.mkt_symbol))
               return new CResult(false, "Invalid market symbol", "CNewFeedMarketPayload.java", 74); 
           
           // Used in another market ?
           if (UTILS.BASIC.marketSymbolUsed(this.mkt_symbol))
              return new CResult(false, "MArket symbol already used", "CNewFeedMarketPayload.java", 74); 
           
           // Statement
           Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
           
           // Asset symbol
           if (tip.equals("ID_REGULAR"))
           {
              // Asset symbol
              if (!UTILS.BASIC.symbolValid(this.asset))
                  return new CResult(false, "Invalid asset symbol", "CNewFeedMarketPayload.java", 74);
        
              // Asset exist ?
              if (UTILS.BASIC.assetExist(this.asset))
                 return new CResult(false, "Asset symbol already exist", "CNewFeedMarketPayload.java", 74); 
           }
        
           // Currency symbol
           if (!this.cur.equals("MSK"))
           {
              if (!UTILS.BASIC.symbolValid(this.cur))
                 return new CResult(false, "Invalid currency symbol", "CNewFeedMarketPayload.java", 74);
        
               // Currency exist ?
               if (!UTILS.BASIC.assetExist(this.cur))
                  return new CResult(false, "Invalid currency symbol", "CNewFeedMarketPayload.java", 74); 
           }
        
           // Feed symbol valid
           if (!UTILS.BASIC.symbolValid(this.feed))
                 return new CResult(false, "Invalid feed symbol 1", "CNewFeedMarketPayload.java", 74);
              
           // Feed symbol exists
           if (!this.feedSymbolValid(this.feed))
                 return new CResult(false, "Invalid feed symbol 1", "CNewFeedMarketPayload.java", 74);
              
           // Feed component symbol valid
           if (!UTILS.BASIC.symbolValid(this.branch))
                 return new CResult(false, "Invalid feed component symbol 1", "CNewFeedMarketPayload.java", 74);
              
           // Feed component symbol exist
           if (!this.feedComponentValid(this.feed, this.branch))
                 return new CResult(false, "Invalid feed component symbol 1", "CNewFeedMarketPayload.java", 74);
           
           // Market fee
           if (this.mkt_fee>10) 
              return new CResult(false, "Invalid market fee", "CFeedMarketgMarketPayload.java", 74);
        
           // Market fee address
           if (!UTILS.BASIC.adressValid(this.fee_adr))
               return new CResult(false, "Invalid market fee address", "CFeedMarketgMarketPayload.java", 74);
    
           // Max leverage
           if (this.max_leverage>1000)
               return new CResult(false, "Invalid maximum leverage", "CFeedMarketgMarketPayload.java", 74);
           
           // Min hold
           if (this.min_hold>100000)
               return new CResult(false, "Invalid minimum hold", "CFeedMarketgMarketPayload.java", 74);
    
           // Market bid
           if (this.bid<0.0001)
               return new CResult(false, "Invalid market bid", "CFeedMarketgMarketPayload.java", 74);
    
           // Days
           if (this.days<1)
               return new CResult(false, "Invalid market bid", "CFeedMarketgMarketPayload.java", 74);
           
           // Asset qty
           if (this.asset_qty<1)
              return new CResult(false, "Invalid asset qty", "CFeedMarketgMarketPayload.java", 74);
           
           // Finds feed value
           double val=UTILS.BASIC.getFeedVal(feed, branch);
           
           // Required initial balance
           double req=val*this.asset_qty;
        
           // Transaction amount
           if (UTILS.BASIC.getBalance(this.target_adr, this.cur)<req)
              return new CResult(false, "Invalid initial deposit amount", "CFeedMarketgMarketPayload.java", 74);
           
           // Hash
           String h=UTILS.BASIC.hash(this.getHash()+
                                     this.tip+
                                     this.mkt_symbol+ 
			             this.feed+ 
			             this.branch+ 
			             this.asset+ 
			             String.valueOf(this.asset_qty)+ 
			             this.cur+ 
			             this.name+ 
			             this.desc+ 
			             this.fee_adr+ 
			             String.valueOf(this.mkt_fee)+
			             String.valueOf(this.decimals)+
                                     String.valueOf(this.max_leverage)+
                                     String.valueOf(this.min_hold)+
			             String.valueOf(this.bid)+ 
			             String.valueOf(this.days));
           
           // Hash
           if (!this.hash.equals(h))
               return new CResult(false, "Invalid hash", "CFeedMarketgMarketPayload.java", 74);
           
           // Close
           s.close();
        }
        catch (SQLException ex) 
       	{  
       	    UTILS.LOG.log("SQLException", ex.getMessage(), "CFeedMarketgMarketPayload.java", 57);
        }
        
         // Return
	 return new CResult(true, "Ok", "CFeedMarketgMarketPayload", 67);
    }
    
    public CResult commit(CBlockPayload block)
    {
        // Super
        CResult res=super.check(block);
        if (!res.passed) return res;
        
        // Check
        res=this.check(block);
        if (!res.passed) return res;
        
        // Insert market
        UTILS.DB.executeUpdate("INSERT INTO feeds_markets (tip,"
                                                        + "adr, "
                                                        + "mkt_symbol, "
                                                        + "feed_1, "
                                                        + "branch_1, "
                                                        + "asset, "
                                                        + "asset_qty, "
                                                        + "cur, "
                                                        + "name, "
                                                        + "description, "
                                                        + "fee_adr, "
                                                        + "mkt_fee, "
                                                        + "decimals, "
                                                        + "max_leverage, "
                                                        + "min_hold, "
                                                        + "bid, "
                                                        + "days,"
                                                        + "block) VALUES('"+
                                                        this.tip+"', '"+
                                                        this.target_adr+"', '"+
                                                        this.mkt_symbol+"', '"+
                                                        this.feed+"', '"+
                                                        this.branch+"', '"+
                                                        this.asset+"', '"+
                                                        this.asset_qty+"', '"+
                                                        this.cur+"', '"+
                                                        UTILS.BASIC.base64_encode(this.name)+"', '"+
                                                        UTILS.BASIC.base64_encode(this.desc)+"', '"+
                                                        this.fee_adr+"', '"+
                                                        this.mkt_fee+"', '"+
                                                        this.decimals+"', '"+
                                                        this.max_leverage+"', '"+
                                                        this.min_hold+"', '"+
                                                        UTILS.FORMAT.format(this.bid)+"', '"+
                                                        this.days+"', '"+
                                                        UTILS.BASIC.block()+"')");
        
        // Update
        UTILS.ROWHASH.updateLastID("feeds_markets");
        
        if (this.tip.equals("ID_REGULAR"))
        {
           // Insert asset
           UTILS.DB.executeUpdate("INSERT INTO assets(mkt_adr, "
                                                + "symbol, "
                                                + "title, "
                                                + "description, "
                                                + "qty, "
                                                + "trans_fee_adr, "
                                                + "trans_fee, "
                                                + "can_increase, "
                                                + "mkt_symbol, "
                                                + "mkt_bid, "
                                                + "mkt_days, "
                                                + "block) VALUES('"+
                                                this.target_adr+"', '"+
                                                this.asset+"', '"+
                                                UTILS.BASIC.base64_encode(this.name)+"', '"+
                                                UTILS.BASIC.base64_encode(this.desc)+"', '"+
                                                this.asset_qty+"', '"+
                                                this.fee_adr+"', '"+
                                                this.mkt_fee+"', '"+
                                                 "Y', '"+
                                                this.mkt_symbol+"', '"+
                                                UTILS.FORMAT.format(this.bid)+"', '"+
                                                this.days+"', '"+                                
                                                UTILS.BASIC.block()+"')");
        
           // Update
           UTILS.ROWHASH.updateLastID("assets");
        
           // Asset owner
           UTILS.DB.executeUpdate("INSERT INTO assets_owners(owner, "
                                                       + "symbol, "
                                                       + "qty, "
                                                       + "block) VALUES('"+
                                                       this.target_adr+"', '"+
                                                       this.asset+"', '"+
                                                       this.asset_qty+"', '"+
                                                       UTILS.BASIC.block()+"')");
        
           // Update
           UTILS.ROWHASH.updateLastID("assets_owners");
        }
        
        // Return
	return new CResult(true, "Ok", "CFeedMarketgMarketPayload", 67); 
    }  
}
