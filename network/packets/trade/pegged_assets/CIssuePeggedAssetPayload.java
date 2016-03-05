package wallet.network.packets.trade.pegged_assets;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CPayload;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.CFeePayload;
import wallet.network.packets.trans.CTransPayload;

public class CIssuePeggedAssetPayload extends CPayload
{
    // Feed 1
    String feed_1;
    
    // Branch 1
    String branch_1; 
				      
    // Feed 2
    String feed_2;
    
    // Branch 2
    String branch_2; 
				      
    // Feed 3
    String feed_3;
    
    // Branch 3
    String branch_3; 
				      
    // Currency
    String cur;
				      
    // Qty
    long qty;
				      
    // Spread
    double spread; 
				      
    // RL symbol
    String rl_symbol; 
			              
    // Decimals
    int decimals; 
				      
    // Days
    long days; 
				      
    // Interest interval
    long interest_interval; 
				      
    // Interest
    double interest; 
				      
    // Asset symbol
    String asset_symbol;
				      
    // Trans fee
    double trans_fee;
				      
    // Trans fee address
    String trans_fee_adr;
				      
    // Name
    String name; 
				      
    // Descripton
    String description; 
				      
    // Image
    String img;
    
    // Market ID
    long mktID;
    
    // Feeds fee
    CTransPayload feed_trans_1;
    CTransPayload feed_trans_2;
    CTransPayload feed_trans_3;
                                              
    public CIssuePeggedAssetPayload(String adr, 
                                      String feed_1, String branch_1, 
				      String feed_2, String branch_2, 
				      String feed_3, String branch_3, 
				      String cur,
				      long qty,
				      double spread, 
				      String rl_symbol, 
			              int decimals, 
				      long interest_interval, 
				      double interest, 
				      String asset_symbol,
				      double trans_fee,
				      String trans_fee_adr,
				      String name, 
				      String description, 
				      String img,
                                      long days) throws Exception
    {
        // Constructor
        super(adr);
        
        try
        {
           // Feed 1
           this.feed_1=feed_1;
    
           // Branch 1
           this.branch_1=branch_1; 
				      
           // Feed 2
           this.feed_2=feed_2;
    
           // Branch 2
           this.branch_2=branch_2; 
				      
           // Feed 3
           this.feed_3=feed_3;
    
           // Branch 3
           this.branch_3=branch_3; 
				      
           // Currency
           this.cur=cur;
				      
           // Qty
           this.qty=qty;
				      
           // Spread
           this.spread=spread; 
				      
           // RL symbol
           this.rl_symbol=rl_symbol; 
			              
           // Decimals
           this.decimals=decimals; 
				      
           // Days
           this.days=days; 
				      
           // Interest interval
           this.interest_interval=interest_interval; 
				      
           // Interest
           this.interest=interest; 
				      
           // Asset symbol
           this.asset_symbol=asset_symbol;
				      
           // Trans fee
           this.trans_fee=trans_fee;
				      
           // Trans fee address
           this.trans_fee_adr=trans_fee_adr;
				      
           // Name
           this.name=name; 
				      
           // Descripton
           this.description=description; 
				      
           // Image
           this.img=img;
           
           // Market ID
           this.mktID=Math.round(Math.random()*10000000000L);
           
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
                                                   feed_fee_1, 
                                                   "MSK", 
                                                   "Data feed fee for "+String.valueOf(this.days)+" days",
                                                   "", "",  "", "", "",  "", "", "", 0);
              
              // Close
              s.close();
           }
           
           if (!this.feed_2.equals(""))
           {
                // Feed 2
                s=UTILS.DB.getStatement();
           
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
                                                         feed_fee_2, 
                                                         "MSK", 
                                                         "Data feed fee for "+String.valueOf(this.days)+" days",
                                                         "", "",  "", "", "",  "", "", "", 0);
              }
              
               // Close
               s.close();
           }
          
           if (!this.feed_3.equals(""))
           {
              // Statement
              s=UTILS.DB.getStatement();
           
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
                                                         feed_fee_3, 
                                                         "MSK", 
                                                         "Data feed fee for "+String.valueOf(this.days)+" days",
                                                         "", "",  "", "", "",  "", "", "", 0);
              }
              
               // Close
               s.close();
           }
           
           // Transactions hashes
           String fee_trans_hash_1="";
           String fee_trans_hash_2="";
           String fee_trans_hash_3="";
           
           if (this.feed_trans_1!=null) fee_trans_hash_1=UTILS.BASIC.hash(this.feed_trans_1.hash);
           if (this.feed_trans_2!=null) fee_trans_hash_2=UTILS.BASIC.hash(this.feed_trans_2.hash);
           if (this.feed_trans_3!=null) fee_trans_hash_3=UTILS.BASIC.hash(this.feed_trans_3.hash);
           
           // Hash
           this.hash=UTILS.BASIC.hash(this.feed_1+
                                      this.branch_1+
                                      this.feed_2+
                                      this.branch_2+
                                      this.feed_3+
                                      this.branch_3+
                                      this.cur+
				      this.qty+
                                      this.spread+
                                      this.rl_symbol+ 
                                      this.decimals+ 
                                      this.days+
                                      this.interest_interval+ 
                                      this.interest+
                                      this.asset_symbol+
                                      this.trans_fee+
                                      this.trans_fee_adr+
                                      this.name+
                                      this.description+ 
                                      this.img+
                                      this.mktID+
                                      fee_trans_hash_1+
                                      fee_trans_hash_2+
                                      fee_trans_hash_3);
         
        }
        catch (Exception ex) 
       	{  
       	    UTILS.LOG.log("Exception", ex.getMessage(), "CIssuePeggedAssetPayload.java", 57);
        }
        
        // Sign
        this.sign();
    }
    
    public CResult check(CBlockPayload block) throws Exception
    {
        try
        {
            // Statement
           Statement s=UTILS.DB.getStatement();
           
           // Another market with the same ID ?
           ResultSet rs=s.executeQuery("SELECT * "
                                       + "FROM feeds_assets_mkts "
                                      + "WHERE mktID='"+this.mktID+"'");
           
           if (UTILS.DB.hasData(rs))
               return new CResult(false, "Market ID already exist", "CIssuePeggedAssetPayload.java", 74); 
           
           // Feed 1
           if (!UTILS.BASIC.feedValid(this.feed_1, this.branch_1))
              return new CResult(false, "Invalid feed", "CIssuePeggedAssetPayload.java", 74); 
           
           // Feed 2
           if (!feed_2.equals(""))
             if (!UTILS.BASIC.feedValid(this.feed_2, this.branch_2))
             return new CResult(false, "Invalid feed", "CIssuePeggedAssetPayload.java", 74); 
           
           // Feed 3
           if (!this.feed_3.equals(""))
             if (!UTILS.BASIC.feedValid(this.feed_3, this.branch_3))
               return new CResult(false, "Invalid feed", "CIssuePeggedAssetPayload.java", 74); 
           
	   // Currency
           if (!this.cur.equals("MSK"))
              if (!UTILS.BASIC.assetExist(this.cur))
                return new CResult(false, "Invalid currency", "CIssuePeggedAssetPayload.java", 74); 
        
	   // Spread
           if (this.spread<0)
               return new CResult(false, "Invalid spread", "CIssuePeggedAssetPayload.java", 74); 
           
           
           // Qty
           double feed_1_val=0;
           double feed_2_val=0;
           double feed_3_val=0;
           
           // Feed 1 val
           feed_1_val=UTILS.BASIC.getFeedVal(feed_1, branch_1);
           
           // Feed 2 val
           if (!this.feed_2.equals(""))
               feed_2_val=UTILS.BASIC.getFeedVal(feed_2, branch_2);
           
           // Feed 3 val
           if (!this.feed_3.equals(""))
               feed_3_val=UTILS.BASIC.getFeedVal(feed_3, branch_3);
           
           // Average
           double avg_price=feed_1_val;
           
           if (!this.feed_2.equals(""))
               avg_price=(feed_1_val+feed_2_val)/2;
           
            if (!this.feed_3.equals(""))
               avg_price=(avg_price+feed_3_val)/2;
            
            // Average price valid
            if (avg_price<=0)
               return new CResult(false, "Invalid average price", "CIssuePeggedAssetPayload.java", 74); 
            
            // Address balance
            if (UTILS.BASIC.getBalance(this.target_adr, this.cur, block)<(avg_price*this.qty*2))
                return new CResult(false, "Invalid average price", "CIssuePeggedAssetPayload.java", 74); 
           
            // Real world symbol
           if (!this.rl_symbol.equals(""))
              if (this.rl_symbol.length()>10)
                return new CResult(false, "Invalid real world symbol", "CIssuePeggedAssetPayload.java", 74); 
        
	    // Decimals
            if (this.decimals>8 || this.decimals<0)
               return new CResult(false, "Invalid decimals", "CIssuePeggedAssetPayload.java", 74); 
        
	   // Interest
           if (this.interest<-10000 || this.interest>10000)
            return new CResult(false, "Invalid long positions interest", "CIssuePeggedAssetPayload.java", 74); 
        
           // Interest interval
           if (interest_interval!=60 && 
               interest_interval!=1440 && 
               interest_interval!=10080 && 
               interest_interval!=302400 && 
               interest_interval!=907200 && 
               interest_interval!=1814400 && 
               interest_interval!=3628800)
           return new CResult(false, "Invalid interest interval", "CIssuePeggedAssetPayload.java", 74); 
        
	   // Asset symbol
           if (UTILS.BASIC.assetExist(this.asset_symbol))
              return new CResult(false, "Asset symbol lready exist", "CIssuePeggedAssetPayload.java", 74);
            
	    // Days
            if (this.days<1000)
               return new CResult(false, "Invalid days", "CIssuePeggedAssetPayload.java", 74);                  
            
            // Transaction Fee
            if (this.trans_fee<0 || this.trans_fee>10)
                return new CResult(false, "Invalid transaction fee", "CIssuePeggedAssetPayload.java", 74);       
            
            // Transaction fee address
            if (this.trans_fee>0)
            {
               if (!UTILS.BASIC.adressValid(this.trans_fee_adr))
                   return new CResult(false, "Invalid transaction fee address", "CIssuePeggedAssetPayload.java", 74); 
            }
            
            // Title
            this.name=UTILS.BASIC.base64_decode(this.name);
            if (!UTILS.BASIC.titleValid(this.name))
               return new CResult(false, "Invalid title", "CIssuePeggedAssetPayload.java", 74); 
    
            // Description
            this.description=UTILS.BASIC.base64_decode(this.description);
            if (!UTILS.BASIC.descriptionValid(this.description))
               return new CResult(false, "Invalid description", "CIssuePeggedAssetPayload.java", 74); 
            
            // Image
            if (!this.img.equals(""))
            {
               // Decode
               this.img=UTILS.BASIC.base64_decode(this.img);
               
               // Valid image
               if (!UTILS.BASIC.isLink(this.img))
                  return new CResult(false, "Invalid image", "CIssuePeggedAssetPayload.java", 74); 
            }
            
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
            String h=UTILS.BASIC.hash(this.feed_1+
                                      this.branch_1+
                                      this.feed_2+
                                      this.branch_2+
                                      this.feed_3+
                                      this.branch_3+
                                      this.cur+
				      this.qty+
                                      this.spread+
                                      this.rl_symbol+ 
                                      this.decimals+ 
                                      this.days+
                                      this.interest_interval+ 
                                      this.interest+
                                      this.asset_symbol+
                                      this.trans_fee+
                                      this.trans_fee_adr+
                                      UTILS.BASIC.base64_encode(this.name)+
                                      UTILS.BASIC.base64_encode(this.description)+ 
                                      UTILS.BASIC.base64_encode(this.img)+
                                      this.mktID+
                                      fee_trans_hash_1+
                                      fee_trans_hash_2+
                                      fee_trans_hash_3);
            
            
            // Hash
            if (!this.hash.equals(h))
                return new CResult(false, "Invalid hash", "CIssuePeggedAssetPayload.java", 74);
        }
        catch (SQLException ex) 
       	{  
       	    UTILS.LOG.log("SQLException", ex.getMessage(), "CIssuePeggedAssetPayload.java", 57);
        }
        
         // Return
	 return new CResult(true, "Ok", "CIssuePeggedAssetPayload", 67);
    }
    
    public CResult commit(CBlockPayload block) throws Exception
    {
        try
        {
           // Super
           CResult res=super.check(block);
           if (!res.passed) return res;
        
            // Check
            res=this.check(block);
            if (!res.passed) return res;
            
            // Qty
           double feed_1_val=0;
           double feed_2_val=0;
           double feed_3_val=0;
           
           // Feed 1 val
           feed_1_val=UTILS.BASIC.getFeedVal(feed_1, branch_1);
           
           // Feed 2 val
           if (!this.feed_2.equals(""))
               feed_2_val=UTILS.BASIC.getFeedVal(feed_2, branch_2);
           
           // Feed 3 val
           if (!this.feed_3.equals(""))
               feed_3_val=UTILS.BASIC.getFeedVal(feed_3, branch_3);
           
           // Average
           double avg_price=feed_1_val;
           
           if (!this.feed_2.equals(""))
               avg_price=(feed_1_val+feed_2_val)/2;
           
            if (!this.feed_3.equals(""))
               avg_price=(avg_price+feed_3_val)/2;
        
            // Insert asset
            UTILS.DB.executeUpdate("INSERT INTO assets (adr, "
                                                     + "symbol, "
                                                     + "title, "
                                                     + "description, "
                                                     + "pic, "
                                                     + "expire, "
                                                     + "qty, "
                                                     + "trans_fee_adr, "
                                                     + "trans_fee, "
                                                     + "can_increase, "
                                                     + "interest, "
                                                     + "interest_interval, "
                                                     + "block, "
                                                     + "linked_mktID) VALUES('"
                                                     +this.target_adr+"', '"
                                                     +this.asset_symbol+"', '"
                                                     +UTILS.BASIC.base64_encode(this.name)+"', '"
                                                     +UTILS.BASIC.base64_encode(this.description)+"', '"
                                                     +UTILS.BASIC.base64_encode(this.img)+"', '"
                                                     +UTILS.BASIC.getExpireBlock(this.days)+"', '"
                                                     +this.qty+"', '"
                                                     +this.trans_fee_adr+"', '"
                                                     +this.trans_fee+"', 'Y', '"
                                                     +this.interest+"', '"
                                                     +this.interest_interval+"', '"
                                                     +this.block+"', '"
                                                     +this.mktID+"')");
            
            // Owner
            UTILS.DB.executeUpdate("INSERT INTO assets_owners(owner, "
                                                           + "symbol, "
                                                           + "qty, "
                                                           + "invested, "
                                                           + "last_interest, "
                                                           + "block) VALUES('"
                                                           +this.target_adr+"', '"
                                                           +this.asset_symbol+"', '"
                                                           +this.qty+"', '"
                                                           +(avg_price*this.qty)+"', '"
                                                           +this.block+"', '"
                                                           +this.block+"')");

            // Insert market
            UTILS.DB.executeUpdate("INSERT INTO feeds_assets_mkts (adr, "
                                                                + "mktID, "
                                                                + "feed_1, "
                                                                + "branch_1, "
                                                                + "feed_2, "
                                                                + "branch_2, "
                                                                + "feed_3, "
                                                                + "branch_3, "
                                                                + "last_price, "
                                                                + "decimals, "
                                                                + "rl_symbol, "
                                                                + "asset_symbol, "
                                                                + "spread, "
                                                                + "cur, "
                                                                + "status, "
                                                                + "expire) VALUES('"
                                                                +this.target_adr+"', '"
                                                                +this.mktID+"', '"
                                                                +this.feed_1+"', '"
                                                                +this.branch_1+"', '"
                                                                +this.feed_2+"', '"
                                                                +this.branch_2+"', '"
                                                                +this.feed_3+"', '"
                                                                +this.branch_3+"', '"
                                                                +avg_price+"', '"
                                                                +this.decimals+"', '"
                                                                +this.rl_symbol+"', '"
                                                                +this.asset_symbol+"', '"
                                                                +this.spread+"', '"
                                                                +this.cur+"', '"
                                                                +"ID_OPEN', '"
                                                                +UTILS.BASIC.getExpireBlock(this.days)+"')");
        
       
            // Clear transactions
            UTILS.BASIC.clearTrans(this.hash, "ID_ALL");
        
            // Return
	    return new CResult(true, "Ok", "CIssuePeggedAssetPayload", 67); 
        }
        catch (Exception ex) 
       	{  
       	    UTILS.LOG.log("Exception", ex.getMessage(), "CIssuePeggedAssetPayload.java", 57);
        }
          
        
        
        // Return
	return new CResult(true, "Ok", "CNewFeedAssetMarketPayload", 67); 
    }  
}
    

