// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.assets.reg_mkts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CPayload;
import wallet.network.packets.blocks.CBlockPayload;

public class CNewRegMarketPayload extends CPayload
{   
    // Market address
    String adr;
    
    // Asset symbol
    String asset_symbol;
    
    // Currency symbol
    String cur_symbol; 
    
    // Title
    String title; 
    
    // Description
    String description;
    
    // Fee Address
    String fee_adr;
    
    // Fee
    double fee;
    
    // Decimals
    int decimals;
    
    // Bid
    double bid; 
    
    // Days
    long days;
    
    // Market ID
    long mktID;
                                        
    public CNewRegMarketPayload(String adr, 
                                String asset_symbol,
                                String cur_symbol,
                                int decimals,
                                String title, 
                                String description,
                                long days) throws Exception
    {
        super(adr);
        
        // Market ddress
        this.adr=adr;
        
        // Asset symbol
        this.asset_symbol=asset_symbol;
    
        // Currency symbol
        this.cur_symbol=cur_symbol;
    
        // Title
        this.title=title;
    
        // Description
        this.description=description;
    
        // Fee address
        this.decimals=decimals;
        
        // Fee
        this.days=days;
        
        // Market ID
        this.mktID=Math.round(Math.random()*10000000000L);
      
        // Hash
        hash=UTILS.BASIC.hash(this.getHash()+
                              this.asset_symbol+
                              this.cur_symbol+
                              this.decimals+
                              this.title+
                              this.description+
                              this.days+
                              this.mktID);
       
         // Sign
        this.sign();
    }

    
    public CResult check(CBlockPayload block) throws Exception
    {
         try
         {
             CResult res=super.check(block);
             if (!res.passed) return res;
         
             // Asset symbol
             if (!UTILS.BASIC.symbolValid(asset_symbol))
                return new CResult(false, "Invalid asset symbol", "CNewRegMarketPayload.java", 74);
         
             // Asset symbol exist
             if (UTILS.BASIC.assetExist(asset_symbol)==false)
                return new CResult(false, "Asset doesn't exist", "CNewRegMarketPayload.java", 74);
         
             // Currency symbol
             if (!this.cur_symbol.equals("MSK"))
                if (!UTILS.BASIC.symbolValid(cur_symbol))
                   return new CResult(false, "Invalid currency symbol", "CNewRegMarketPayload.java", 74);
         
             // Currency symbol exist
             if (!this.cur_symbol.equals("MSK"))
               if (UTILS.BASIC.assetExist(cur_symbol)==false)
                 return new CResult(false, "Currency doesn't exist", "CNewRegMarketPayload.java", 74);
         
             // Market ID
             Statement s=UTILS.DB.getStatement();
             ResultSet rs=s.executeQuery("SELECT * "
                                         + "FROM assets_mkts "
                                        + "WHERE mktID='"+this.mktID+"'");
         
             if (UTILS.DB.hasData(rs))
                return new CResult(false, "Invalid market symbol", "CNewRegMarketPayload.java", 74);
         
             // Title
             this.title=UTILS.BASIC.base64_decode(this.title);
             if (!UTILS.BASIC.titleValid(title))
                 return new CResult(false, "Invalid title", "CNewRegMarketPayload.java", 74);
             
             // Description
             this.description=UTILS.BASIC.base64_decode(this.description);
             if (!UTILS.BASIC.descriptionValid(description))
                  return new CResult(false, "Invalid description", "CNewRegMarketPayload.java", 74);
         
             // Market Days
             if (this.days<10)
                 return new CResult(false, "Invalid market days", "CRentDomainPayload.java", 61);
         
             // Decimals
             if (this.decimals<0 || this.decimals>8)
                  return new CResult(false, "Invalid decimals", "CRentDomainPayload.java", 61);
         
             // Hash code
             String h=UTILS.BASIC.hash(this.getHash()+
                                       this.asset_symbol+
                                       this.cur_symbol+
                                       this.decimals+
                                       UTILS.BASIC.base64_encode(this.title)+
                                       UTILS.BASIC.base64_encode(this.description)+
                                       this.days+
                                       this.mktID);
             
         
             if (!this.hash.equals(h))
                return new CResult(false, "Invalid hash", "CRentDomainPayload.java", 61);
        
         }
       	 catch (SQLException ex) 
       	 {  
       	    UTILS.LOG.log("SQLException", ex.getMessage(), "CBuyDomainPayload.java", 57);
         }
         
         
         // Return
	 return new CResult(true, "Ok", "CNewFeedPayload", 67);
    }
    
    public CResult commit(CBlockPayload block) throws Exception
    {
        // Super
        CResult res=super.commit(block);
        if (!res.passed) return res;
        
        // Check
        res=this.check(block);
        if (!res.passed) return res;
        
        // Insert market
        UTILS.DB.executeUpdate("INSERT INTO assets_mkts(adr, "
                                                        + "asset, "
                                                        + "cur, "
                                                        + "name, "
                                                        + "description, "
                                                        + "decimals, "
                                                        + "block, "
                                                        + "expire, "
                                                        + "rowhash, "
                                                        + "mktID) "
                                              + "VALUES('"+this.adr+"', '"+
                                                         this.asset_symbol+"', '"+
                                                         this.cur_symbol+"', '"+
                                                         UTILS.BASIC.base64_encode(this.title)+"', '"+
                                                         UTILS.BASIC.base64_encode(this.description)+"', '"+
                                                         this.decimals+"', '"+                       
                                                         this.block+"', '"+
                                                         UTILS.BASIC.getExpireBlock(this.days)+"', "+
                                                         "'', '"+
                                                         this.mktID+"')");
        
        
        // Return
	return new CResult(true, "Ok", "CNewFeedPayload", 67); 
    }        
}
