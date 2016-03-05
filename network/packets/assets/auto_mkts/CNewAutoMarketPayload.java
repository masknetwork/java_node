// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.assets.auto_mkts;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CPayload;
import wallet.network.packets.blocks.CBlockPayload;

public class CNewAutoMarketPayload extends CPayload
{   
    // Market address
    String adr;
    
    // Asset symbol
    String asset_symbol;
    
    // Currency symbol
    String cur_symbol; 
    
    // Market symbol
    String mkt_symbol; 
    
    // Title
    String title; 
    
    // Description
    String description;
    
    // Fee Address
    String fee_adr;
    
    // Fee
    double fee;
    
    // Volatility
    double volatility;
    
    // Initial price
    double init_price;
    
    // Decimals
    int decimals;
    
    // Bid
    double bid; 
    
    // Days
    public long days;
                                        
    public CNewAutoMarketPayload(String adr, 
                                String asset_symbol,
                                String cur_symbol, 
                                String mkt_symbol, 
                                String title, 
                                String description,
                                String fee_adr,
                                double fee,
                                double volatility,
                                double init_price,
                                int decimals, 
                                double bid, 
                                long days) throws Exception
    {
        super(adr);
        
        // Market ddress
        this.adr=adr;
        
        // Asset symbol
        this.asset_symbol=asset_symbol;
    
        // Currency symbol
        this.cur_symbol=cur_symbol;
    
        // Market symbol
        this.mkt_symbol=mkt_symbol;
    
        // Title
        this.title=title;
    
        // Description
        this.description=description;
    
        // Fee address
        this.fee_adr=fee_adr;
        
        // Fee
        this.fee=fee;
        
        // Volatility
        this.volatility=volatility;
        
        // Initial price
        this.init_price=init_price;
        
        // Decimals
        this.decimals=decimals;
        
        // Market bid
        this.bid=bid;
        
        // Days
        this.days=days;
    
        // Hash
        hash=UTILS.BASIC.hash(this.getHash()+
                              this.adr+
                              this.asset_symbol+
                              this.cur_symbol+
                              this.mkt_symbol+
                              this.title+
                              this.description+
                              this.fee_adr+
                              String.valueOf(this.fee)+
                              String.valueOf(this.decimals)+
                              String.valueOf(this.bid)+
                              String.valueOf(this.days));
        
         // Sign
        this.sign();
    }

    
    public CResult check(CBlockPayload block) throws Exception
    {
         CResult res=super.check(block);
         if (!res.passed) return res;
         
         // Asset symbol
         if (!UTILS.BASIC.symbolValid(asset_symbol))
            return new CResult(false, "Invalid asset symbol", "CNewAutoMarketPayload.java", 74);
         
         // Asset symbol exist
         if (UTILS.BASIC.assetExist(asset_symbol)==false)
            return new CResult(false, "Asset doesn't exist", "CNewAutoMarketPayload.java", 74);
         
         // Currency symbol
         if (!this.cur_symbol.equals("MSK"))
            if (!UTILS.BASIC.symbolValid(cur_symbol))
               return new CResult(false, "Invalid currency symbol", "CNewAutoMarketPayload.java", 74);
         
         // Currency symbol exist
         if (!this.cur_symbol.equals("MSK"))
           if (UTILS.BASIC.assetExist(cur_symbol)==false)
            return new CResult(false, "Currency doesn't exist", "CNewAutoMarketPayload.java", 74);
         
         // Market symbol
         if (!UTILS.BASIC.symbolValid(mkt_symbol))
            return new CResult(false, "Invalid market symbol", "CNewAutoMarketPayload.java", 74);
         
         // Market symbol used in another market ?
         if (UTILS.BASIC.marketSymbolUsed(mkt_symbol)==true)
            return new CResult(false, "Market symbol is already used", "CNewAutoMarketPayload.java", 74);
         
         // Title
         if (!UTILS.BASIC.titleValid(title))
            return new CResult(false, "Invalid title", "CNewAutoMarketPayload.java", 74);
             
         // Description
         if (!UTILS.BASIC.descriptionValid(description))
            return new CResult(false, "Invalid description", "CNewAutoMarketPayload.java", 74);
         
         // Market fee
         if (this.fee<0.01 || this.fee>10)
            return new CResult(false, "Invalid market fee", "CNewAutoMarketPayload.java", 74);
         
         // Market fee address
         if (!UTILS.BASIC.adressValid(this.fee_adr))
              return new CResult(false, "Invalid market fee address", "CNewAutoMarketPayload.java", 74);
         
         // Market bid
         if (!UTILS.BASIC.mktBidValid(this.bid))
             return new CResult(false, "Invalid market days", "CNewAutoMarketPayload.java", 74);
         
         // Market Days
         if (!UTILS.BASIC.mktDaysValid(this.days))
            return new CResult(false, "Invalid market days", "CNewAutoMarketPayload.java", 61);
         
         // Decimals
         if (this.decimals<0 || this.decimals>8)
             return new CResult(false, "Invalid decimals", "CNewAutoMarketPayload.java", 61);
         
         // Volatility
         if (this.volatility<0.00000001)
             return new CResult(false, "Invalid volatility", "CNewAutoMarketPayload.java", 61);
         
         // Initial price
         if (this.init_price<0)
            return new CResult(false, "Invalid initial price", "CNewAutoMarketPayload.java", 61);
         
         // Hash code
         String h=UTILS.BASIC.hash(this.getHash()+
                                   this.adr+
                                   this.asset_symbol+
                                   this.cur_symbol+
                                   this.mkt_symbol+
                                   this.title+
                                   this.description+
                                   this.fee_adr+
                                   String.valueOf(this.fee)+
                                   String.valueOf(this.decimals)+
                                   String.valueOf(this.bid)+
                                   String.valueOf(this.days));
         
         if (!this.hash.equals(h))
            return new CResult(false, "Invalid hash", "CNewAutoMarketPayload.java", 61);
        
         
         // Return
	 return new CResult(true, "Ok", "CNewAutoMarketPayload", 67);
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
        UTILS.DB.executeUpdate("INSERT INTO assets_markets(tip, "
                                                        + "mkt_adr, "
                                                        + "mkt_symbol, "
                                                        + "title, "
                                                        + "description, "
                                                        + "asset_symbol, "
                                                        + "cur_symbol, "
                                                        + "mkt_fee, "
                                                        + "mkt_fee_adr, "
                                                        + "bid, "
                                                        + "ask, "
                                                        + "price, "
                                                        + "tmp_price, "
                                                        + "volatility, "
                                                        + "initial_price, "
                                                        + "price, "
                                                        + "tmp_price, "
                                                        + "mkt_bid, "
                                                        + "expires, "
                                                        + "block) "
                                              + "VALUES('ID_AUTO', '"+
                                                         this.adr+"', '"+
                                                         this.mkt_symbol+"', '"+
                                                         UTILS.BASIC.base64_encode(this.title)+"', '"+
                                                         UTILS.BASIC.base64_encode(this.description)+"', '"+
                                                         this.asset_symbol+"', '"+
                                                         this.cur_symbol+"', '"+
                                                         this.fee+"', '"+
                                                         this.fee_adr+"', "+
                                                         "'0', "+
                                                         "'0', "+
                                                         "'0', "+
                                                         "'0', '"+
                                                        UTILS.FORMAT.format(this.volatility)+"', '"+
                                                        UTILS.FORMAT.format(this.init_price)+"', '"+
                                                        UTILS.FORMAT.format(this.init_price)+"', '"+
                                                        UTILS.FORMAT.format(this.init_price)+"', '"+
                                                        UTILS.FORMAT.format(this.bid)+"', '"+
                                                        (this.block+this.days*1440)+"', '"+
                                                        this.block+"')");
        
        
        // Return
	return new CResult(true, "Ok", "CNewAutoMarketPayload", 67); 
    }        
}