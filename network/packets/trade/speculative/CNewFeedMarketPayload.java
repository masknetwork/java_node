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

public class CNewFeedMarketPayload extends CPayload
{
    // Symbol
    long mktID; 
    
    // Feed 1
    String feed_1; 
    String branch_1; 
    
    // Feed 2
    String feed_2; 
    String branch_2; 
    
    // Feed 3
    String feed_3; 
    String branch_3; 
    
    // Currency
    String cur; 
    
    // Minimum hold time
    long min_hold;
    
    // Maximum hold time
    long max_hold; 
    
    // Min leverage
    long min_leverage;
    
    // Max leverage
    long max_leverage;
    
    // Spread
    double spread;
    
    // String 
    String real_symbol;
    
    // Decimals
    int decimals;
    
    // Position type
    String pos_type;
    
    // Lng positions interest
    double long_int;
    
    // Short positions interest
    double short_int;
    
    // Interest interval
    long interest_interval;
    
    
    // Title
    String title;
    
    // Description
    String desc;
    
    // Max position size
    double max_margin;
    
    // Days
    long days;
    
    // Feed payments
    CTransPayload feed_trans_1;
    CTransPayload feed_trans_2;
    CTransPayload feed_trans_3;
                                         
    public CNewFeedMarketPayload(String mkt_adr, 
			         long mktID, 
			         String feed_1, 
			         String branch_1, 
				 String feed_2, 
				 String branch_2, 
			 	 String feed_3, 
				 String branch_3, 
				 String cur, 
				 long min_hold,
				 long max_hold, 
			 	 long min_leverage,
				 long max_leverage,
			         double spread,
				 String real_symbol,
			         int decimals,
			         String pos_type,
				 double long_int,
			         double short_int,
				 long interest_interval,
			         String title,
				 String desc,
				 double max_margin,
				 long days) throws Exception
    {
         // Constructor
        super(mkt_adr);
        
        try
        {
           // Symbol
           this.mktID=mktID; 
        
           // Feed 1
           this.feed_1=feed_1; 
           this.branch_1=branch_1; 
	
           // Feed 2
           this.feed_2=feed_2;
	   this.branch_2=branch_2; 
			 	 
           // Feed 3
           this.feed_3=feed_3;
	   this.branch_3=branch_3; 
				 
           // Currency
           this.cur=cur;
        
	   // Minimum hold time
           this.min_hold=min_hold;
        
           // Maximum hold
	   this.max_hold=max_hold; 
			 	 
           // Min leverage
           this.min_leverage=min_leverage;
        
           // Max leverage
           this.max_leverage=max_leverage;
        
           // Spread
           this.spread=spread;
        
           // Real symbol
	   this.real_symbol=real_symbol;
        
           // Decimals
           this.decimals=decimals;
			         
           // Position type
           this.pos_type=pos_type;
				 
           // Long positions interest
           this.long_int=long_int;
			         
           // Short int
           this.short_int=short_int;
				 
           // Interest interval
           this.interest_interval=interest_interval;
        
           // Title
	   this.title=title;
        
           // Description
           this.desc=desc;
        
           // Max position size
	   this.max_margin=max_margin;
        
           // Days
           this.days=days;
        
           // Feed 1
           Statement s=UTILS.DB.getStatement();
           ResultSet rs=s.executeQuery("SELECT fb.fee, fe.adr "
                                    + "FROM feeds_branches AS fb "
                                    + "JOIN feeds AS fe ON fe.symbol=fb.feed_symbol "
                                   + "WHERE fb.feed_symbol='"+this.feed_1
                                    +"' AND fb.symbol='"+this.branch_1+"'");
           
           if (UTILS.DB.hasData(rs))
           {
              // Next
              rs.next();
              
              double feed_fee_1=rs.getDouble("fee");
              String feed_adr_1=rs.getString("adr"); 
           
              if (feed_fee_1>0)
               this.feed_trans_1=new CTransPayload(this.target_adr, 
                                                   feed_adr_1, 
                                                   feed_fee_1*days, 
                                                   "MSK", 
                                                   "",
                                                   "", "");
           }
           
           // Close
           s.close();
        
           // Feed 2
           s=UTILS.DB.getStatement();
           if (!this.feed_2.equals(""))
           {
              rs=s.executeQuery("SELECT fb.fee, fe.adr "
                                    + "FROM feeds_branches AS fb "
                                    + "JOIN feeds AS fe ON fe.symbol=fb.feed_symbol "
                                   + "WHERE fb.feed_symbol='"+this.feed_2
                                    +"' AND fb.symbol='"+this.branch_2+"'");
              
              if (UTILS.DB.hasData(rs))
              {
                 // Next
                 rs.next();
                 
                 double feed_fee_2=rs.getDouble("fee");
                 String feed_adr_2=rs.getString("adr"); 
        
                 if (feed_fee_2>0)
                     this.feed_trans_2=new CTransPayload(this.target_adr, 
                                                         feed_adr_2, 
                                                         feed_fee_2*days, 
                                                         "MSK", 
                                                         "",
                                                         "", "");
              }
           }
           
           s=UTILS.DB.getStatement();
           if (!this.feed_3.equals(""))
           {
              // Feed 3
              rs=s.executeQuery("SELECT fb.fee, fe.adr "
                                    + "FROM feeds_branches AS fb "
                                    + "JOIN feeds AS fe ON fe.symbol=fb.feed_symbol "
                                   + "WHERE fb.feed_symbol='"+this.feed_3
                                    +"' AND fb.symbol='"+this.branch_3+"'");
              
              if (UTILS.DB.hasData(rs))
              {
                  // Next
                  rs.next();
                  
                  double feed_fee_3=rs.getDouble("fee");
                  String feed_adr_3=rs.getString("adr"); 
        
                  if (feed_fee_3>0)
                     this.feed_trans_3=new CTransPayload(this.target_adr, 
                                                         feed_adr_3, 
                                                         feed_fee_3*days, 
                                                         "MSK", 
                                                         "",
                                                         "", "");
              }
           }
           
           // Transactions hashes
           String fee_trans_hash_1="";
           String fee_trans_hash_2="";
           String fee_trans_hash_3="";
           
           if (this.feed_trans_1!=null) fee_trans_hash_1=UTILS.BASIC.hash(this.feed_trans_1.hash);
           if (this.feed_trans_2!=null) fee_trans_hash_2=UTILS.BASIC.hash(this.feed_trans_2.hash);
           if (this.feed_trans_3!=null) fee_trans_hash_3=UTILS.BASIC.hash(this.feed_trans_3.hash);
           
           // Hash
           this.hash=UTILS.BASIC.hash(this.getHash() + 
                                      this.mktID + 
			              this.feed_1 + 
			              this.branch_1 + 
				      this.feed_2 + 
				      this.branch_2 + 
			 	      this.feed_3 + 
				      this.branch_3 + 
				      this.cur + 
				      this.min_hold +
				      this.max_hold +
			 	      this.min_leverage +
				      this.max_leverage +
			              this.spread +
				      this.real_symbol+
			              this.decimals+
			              this.pos_type+
				      this.long_int+
			              this.short_int+
				      this.interest_interval+
			              this.title+
				      this.desc+
				      this.max_margin+
				      this.days+
                                      fee_trans_hash_1+
                                      fee_trans_hash_2+
                                      fee_trans_hash_3);
        
           // Sign
           this.sign();
        }
        catch (SQLException ex) 
       	{  
       		UTILS.LOG.log("SQLException", ex.getMessage(), "CBuyDomainPayload.java", 57);
        }
    }
    
    public CResult check(CBlockPayload block) throws Exception
    {
        try
        {
           // Statement
           Statement s=UTILS.DB.getStatement();
           
           // Another market with the same ID ?
           ResultSet rs=s.executeQuery("SELECT * "
                                       + "FROM feeds_spec_mkts "
                                      + "WHERE mktID='"+this.mktID+"'");
           
           if (UTILS.DB.hasData(rs))
               return new CResult(false, "Market ID already exist", "CNewFeedMarketPayload.java", 74); 
           
           // Feed 1
           if (!UTILS.BASIC.feedValid(this.feed_1, this.branch_1))
              return new CResult(false, "Invalid feed", "CNewFeedMarketPayload.java", 74); 
           
           // Feed 2
           if (!feed_2.equals(""))
             if (!UTILS.BASIC.feedValid(this.feed_2, this.branch_2))
             return new CResult(false, "Invalid feed", "CNewFeedMarketPayload.java", 74); 
           
           // Feed 3
           if (!this.feed_3.equals(""))
             if (!UTILS.BASIC.feedValid(this.feed_3, this.branch_3))
               return new CResult(false, "Invalid feed", "CNewFeedMarketPayload.java", 74); 
           
	   // Currency
           if (!this.cur.equals("MSK"))
              if (!UTILS.BASIC.isAsset(this.cur))
                return new CResult(false, "Invalid currency", "CNewFeedMarketPayload.java", 74); 
        
	   // Minimum hold time
           if (min_hold<1)
               return new CResult(false, "Invalid minimum hold time", "CNewFeedMarketPayload.java", 74); 
        
	   // Max hold time
           if (max_hold<10)
               return new CResult(false, "Invalid maximum hold time", "CNewFeedMarketPayload.java", 74); 
        
           // Min leverage
           if (min_leverage<1)
               return new CResult(false, "Invalid miniimum leverage", "CNewFeedMarketPayload.java", 74); 
        
	   // Maximum leverage
           if (max_leverage<1 || this.max_leverage>10000)
               return new CResult(false, "Invalid minimum leverage", "CNewFeedMarketPayload.java", 74); 
        
	
           // Spread
           if (spread<0)
               return new CResult(false, "Invalid spread", "CNewFeedMarketPayload.java", 74); 
        
	   // Real world symbol
           if (!this.real_symbol.equals(""))
              if (this.real_symbol.length()>10)
                return new CResult(false, "Invalid real world symbol", "CNewFeedMarketPayload.java", 74); 
        
	    // Decimals
            if (this.decimals>8 || this.decimals<0)
               return new CResult(false, "Invalid decimals", "CNewFeedMarketPayload.java", 74); 
        
	    // Position type
            if (!this.pos_type.equals("ID_LONG_SHORT") && 
               !this.pos_type.equals("ID_LONG_ONLY") && 
               !this.pos_type.equals("ID_SHORT_ONLY"))
            return new CResult(false, "Invalid position type", "CNewFeedMarketPayload.java", 74); 
            
	   // Long positions interest
           if (this.long_int<-10000 || this.long_int>10000)
            return new CResult(false, "Invalid long positions interest", "CNewFeedMarketPayload.java", 74); 
        
           // Short positions interest
           if (this.short_int<-10000 || this.short_int>10000)
              return new CResult(false, "Invalid long positions interest", "CNewFeedMarketPayload.java", 74); 
        
	   // Interest interval
           if (interest_interval!=60 && 
               interest_interval!=1440 && 
               interest_interval!=10080 && 
               interest_interval!=302400 && 
               interest_interval!=907200 && 
               interest_interval!=1814400 && 
               interest_interval!=3628800)
           return new CResult(false, "Invalid interest interval", "CNewFeedMarketPayload.java", 74); 
        
	   // Maximum position size
           if (this.max_margin<1 || this.max_margin>10)
              return new CResult(false, "Invalid margin size", "CNewFeedMarketPayload.java", 74);
            
	    // Days
            if (this.days<100)
               return new CResult(false, "Invalid days", "CNewFeedMarketPayload.java", 74);                  
                                         
            // Title
            if (!UTILS.BASIC.titleValid(this.title))
               return new CResult(false, "Invalid title", "CNewFeedMarketPayload.java", 74); 
    
            // Description
            if (!UTILS.BASIC.descriptionValid(this.desc))
               return new CResult(false, "Invalid description", "CNewFeedMarketPayload.java", 74); 
        
            // Result
            CResult res;
        
            // Check transactions
            if (this.feed_trans_1!=null) 
            {
               res=this.feed_trans_1.check(block);
               if (!res.passed) return res;
            }
        
            if (this.feed_trans_2!=null) 
            {
               res=this.feed_trans_2.check(block);
               if (!res.passed) return res;
            }
        
            if (this.feed_trans_3!=null) 
            {
              res=this.feed_trans_3.check(block);
              if (!res.passed) return res;
            }
           
            // Transactions hashes
            String fee_trans_hash_1="";
            String fee_trans_hash_2="";
            String fee_trans_hash_3="";
           
            if (this.feed_trans_1!=null) fee_trans_hash_1=UTILS.BASIC.hash(this.feed_trans_1.hash);
            if (this.feed_trans_2!=null) fee_trans_hash_2=UTILS.BASIC.hash(this.feed_trans_2.hash);
            if (this.feed_trans_3!=null) fee_trans_hash_3=UTILS.BASIC.hash(this.feed_trans_3.hash);
           
            // Hash
            String h=UTILS.BASIC.hash(this.getHash() + 
                                      this.mktID + 
			              this.feed_1 + 
			              this.branch_1 + 
				      this.feed_2 + 
				      this.branch_2 + 
			 	      this.feed_3 + 
				      this.branch_3 + 
				      this.cur + 
				      this.min_hold +
				      this.max_hold +
			 	      this.min_leverage +
				      this.max_leverage +
			              this.spread +
				      this.real_symbol+
			              this.decimals+
			              this.pos_type+
				      this.long_int+
			              this.short_int+
				      this.interest_interval+
			              this.title+
				      this.desc+
				      this.max_margin+
				      this.days+
                                      fee_trans_hash_1+
                                      fee_trans_hash_2+
                                      fee_trans_hash_3);
            
            
            // Hash
            if (!this.hash.equals(h))
                return new CResult(false, "Invalid hash", "CFeedMarketMarketPayload.java", 74);
          
         
        }
        catch (SQLException ex) 
       	{  
       	    UTILS.LOG.log("SQLException", ex.getMessage(), "CNewFeedMarketPayload.java", 57);
            return new CResult(false, "SQLException", "CFeedMarketgMarketPayload", 67);
        }
        catch (Exception ex) 
       	{  
       	    UTILS.LOG.log("Exception", ex.getMessage(), "CNewFeedMarketPayload.java", 57);
            return new CResult(false, "Exception", "CFeedMarketgMarketPayload", 67);
        }
        
         // Return
	 return new CResult(true, "Ok", "CFeedMarketgMarketPayload", 67);
    }
    
    public CResult commit(CBlockPayload block) throws Exception
    {
        // Super
        CResult res=super.check(block);
        if (!res.passed) return res;
        
        // Check
        res=this.check(block);
        if (!res.passed) return res;
        
        // Commit feed trans
        if (this.feed_trans_1!=null) this.feed_trans_1.commit(block);
        if (this.feed_trans_2!=null) this.feed_trans_1.commit(block);
        if (this.feed_trans_3!=null) this.feed_trans_1.commit(block);
        
        // Insert market
        UTILS.DB.executeUpdate("INSERT INTO feeds_spec_mkts (adr, "
                                                        + "feed_1, "
                                                        + "branch_1, "
                                                        + "feed_2, "
                                                        + "branch_2, "
                                                        + "feed_3, "
                                                        + "branch_3, "
                                                        + "cur, "
                                                        + "mktID, "
                                                        + "min_hold, "
                                                        + "max_hold, "
                                                        + "min_leverage, "
                                                        + "max_leverage, "
                                                        + "spread, "
                                                        + "real_symbol, "
                                                        + "decimals, "
                                                        + "pos_type, "
                                                        + "long_interest, "
                                                        + "short_interest, "
                                                        + "interest_interval, "
                                                        + "title, "
                                                        + "description, "
                                                        + "max_margin, "
                                                        + "status, "
                                                        + "expire) VALUES('"
                                                        +this.target_adr+"', '"
                                                        +this.feed_1+"', '"
                                                        +this.branch_1+"', '"
                                                        +this.feed_2+"', '"
                                                        +this.branch_2+"', '"
                                                        +this.feed_3+"', '"
                                                        +this.branch_3+"', '"
                                                        +this.cur+"', '"
                                                        +this.mktID+"', '"
                                                        +this.min_hold+"', '"
                                                        +this.max_hold+"', '"
                                                        +this.min_leverage+"', '"
                                                        +this.max_leverage+"', '"
                                                        +this.spread+"', '"
                                                        +this.real_symbol+"', '"
                                                        +this.decimals+"', '"
                                                        +this.pos_type+"', '"
                                                        +this.long_int+"', '"
                                                        +this.short_int+"', '"
                                                        +this.interest_interval+"', '"
                                                        +UTILS.BASIC.base64_encode(this.title)+"', '"
                                                        +UTILS.BASIC.base64_encode(this.desc)+"', '"
                                                        +this.max_margin+"', '"
                                                        +"ID_CLOSED', '"
                                                        +(this.block+(this.days*1440))+"')");
        
       
        // Clear transactions
        UTILS.BASIC.clearTrans(this.hash, "ID_ALL");
        
        // Return
	return new CResult(true, "Ok", "CFeedMarketgMarketPayload", 67); 
    }  
}
