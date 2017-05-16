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
    
    // Decimals
    int decimals;
    
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
                              this.title+
                              this.description+
                              this.decimals+
                              this.days+
                              this.mktID);
       
         // Sign
        this.sign();
    }

    
    public void check(CBlockPayload block) throws Exception
    {
        // Parent
        super.check(block);
         
        // Asset symbol 
        if (!UTILS.BASIC.isAsset(asset_symbol))
           throw new Exception("Invalid asset symbol - CNewRegMarketPayload.java");
        
        // Currency symbol
        if (!this.cur_symbol.equals("MSK"))
           if (!UTILS.BASIC.isAsset(cur_symbol))
             throw new Exception("Invalid currency symbol - CNewRegMarketPayload.java");
         
        // Market ID
        if (UTILS.BASIC.existID(mktID))
            throw new Exception("Invalid market ID - CNewRegMarketPayload.java");
        
        // Title
        if (!UTILS.BASIC.isTitle(title))
           throw new Exception("Invalid title - CNewRegMarketPayload.java");
             
        // Description
        if (!UTILS.BASIC.isDesc(description))
           throw new Exception("Invalid description - CNewRegMarketPayload.java");
         
        // Market Days
        if (this.days<100)
           throw new Exception("Invalid days - CNewRegMarketPayload.java");
         
        // Decimals
        if (this.decimals<0 || this.decimals>8)
            throw new Exception("Invalid decimals - CNewRegMarketPayload.java");
        
        // Hash code
        String h=UTILS.BASIC.hash(this.getHash()+
                                  this.asset_symbol+
                                  this.cur_symbol+
                                  this.title+
                                  this.description+
                                  this.decimals+
                                  this.days+
                                  this.mktID);
             
        // Check hash
        if (!this.hash.equals(h))
             throw new Exception("Invalid hash - CNewRegMarketPayload.java");
        
    }
    
    public void commit(CBlockPayload block) throws Exception
    {
        // Super
        super.commit(block);
        
        // Insert market
        UTILS.DB.executeUpdate("INSERT INTO assets_mkts "
                                     + "SET adr='"+this.adr+"', "
                                         + "asset='"+this.asset_symbol+"', "
                                         + "cur='"+this.cur_symbol+"', "
                                         + "name='"+UTILS.BASIC.base64_encode(this.title)+"', "
                                         + "description='"+UTILS.BASIC.base64_encode(this.description)+"', "
                                         + "decimals='"+this.decimals+"', "
                                         + "block='"+this.block+"', "
                                         + "expire='"+(this.block+(this.days*1440))+"', "
                                         + "mktID='"+this.mktID+"'");
    }        
}
