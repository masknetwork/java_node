// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.assets.reg_mkts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.agents.CAgent;
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
         
        // Asset symbol valid
        if (!UTILS.BASIC.isSymbol(asset_symbol))
            throw new Exception("Invalid asset symbol - CNewRegMarketPayload.java");
         
        // Currency symbol valid
        if (!cur_symbol.equals("MSK"))
          if (UTILS.BASIC.isSymbol(cur_symbol)==false)
            throw new Exception("Invalid currency symbol - CNewRegMarketPayload.java");
        
        // Asset symbol doesn't exist
        if (!UTILS.BASIC.isAsset(asset_symbol))
           throw new Exception("Invalid asset symbol - CNewRegMarketPayload.java");
        
        // Currency symbol
        if (!this.cur_symbol.equals("MSK"))
           if (!UTILS.BASIC.isAsset(cur_symbol))
             throw new Exception("Invalid currency symbol - CNewRegMarketPayload.java");
         
        // Market ID
        
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                    + "FROM assets_mkts "
                                   + "WHERE mktID='"+this.mktID+"'");
         
        if (UTILS.DB.hasData(rs))
            throw new Exception("Invalid market ID - CNewRegMarketPayload.java");
        
        // Duplicate market ?
        rs=UTILS.DB.executeQuery("SELECT * "
                          + "FROM assets_mkts "
                         + "WHERE adr='"+this.target_adr+"' "
                           + "AND asset='"+this.asset_symbol+"' "
                           + "AND cur='"+this.cur_symbol+"'");
        
        if (UTILS.DB.hasData(rs))
            throw new Exception("Duplicated market - CNewRegMarketPayload.java");
         
        // Title
        if (!UTILS.BASIC.isTitle(title))
           throw new Exception("Invalid title - CNewRegMarketPayload.java");
             
        // Description
        if (!UTILS.BASIC.isDesc(description))
           throw new Exception("Invalid description - CNewRegMarketPayload.java");
         
        // Market Days
        if (this.days<1000)
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
        
        if (!this.cur_symbol.equals("MSK"))
        {
           // Asset has contract attached ?
           long asset_aID=UTILS.BASIC.getAssetContract(this.asset_symbol);
        
           // Has contract ?
           if (asset_aID>0)
           {
               // Asset has contract attached ?
               long cur_aID=UTILS.BASIC.getAssetContract(this.asset_symbol);
        
               // Has contract ?
               if (asset_aID>0)
               {
                   // Load message
                   CAgent AGENT=new CAgent(cur_aID, false, this.block);
                    
                   // Set Message 
                   AGENT.VM.SYS.EVENT.loadOpenAssetMarket(this.target_adr, 
                                                       this.mktID, 
                                                       this.asset_symbol, 
                                                       this.cur_symbol, 
                                                       this.title, 
                                                       this.description, 
                                                       this.decimals, 
                                                       this.days);
                    
                   // Execute
                   AGENT.execute("#new_asset_market#", false, this.block);
            
                   // Aproved ?
                   if (!AGENT.VM.SYS.EVENT.OPEN_MARKET.APPROVED)
                       throw new Exception("rejected by application - CNewRegMarketPayload.java");
            }
          }
        }
        
      
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
