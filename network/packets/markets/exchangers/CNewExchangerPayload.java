package wallet.network.packets.markets.exchangers;

import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.blocks.*;

public class CNewExchangerPayload extends CPayload 
{
    // Section
    String section;
    
    // Section country
    String section_country;
    
    // Title
    String title;
    
    // Description
    String description;
    
    // InternalID
    String internalID;
    
    // We page
    String web_page;
    
    // Pic
    String pic;
    
    // Market Bid
    double mkt_bid;
    
    // Market days
    long mkt_day;
    
    // Offer Type
    String ex_tip;
    
    // Currency
    String ex_cur;
    
    // Price
    double fixed_price;
    
    // Variable price feed
    String var_price_from_feed;
    
    // Variable price feed component
    String var_price_from_feed_component;
    
    // Variable price market
    String var_price_from_market;
    
    // Accept escrowers
    String acceept_escrowers;
    
    // Escrower
    String esc_1;
    
    // Escrower
    String esc_2;
    
    // Escrower
    String esc_3;
    
    // Escrower
    String esc_4;
    
    // Escrower
    String esc_5;
	
    public CNewExchangerPayload(String adr,
                                String title,
                                String description,
                                String web_page,
                                String ex_tip,
                                String ex_asset,
                                String ex_cur,
                                String ex_price_type,
                                double fixed_price,
                                String feed_symbol,
                                String feed_branch_symbol,
                                double mkt_bid,
                                long mkt_days)
  {
      // Constructor
      super(adr);
	  
      // Section
      this.section=section;
    
      // Section country
      this.section_country=section_country;
    
      // Title
      this.title=title;
    
      // Description
      this.description=description;
    
      // InternalID
      this.internalID=internalID;
    
      // We page
      this.web_page=web_page;
    
      // Pic
      this.pic=pic;
    
      // Market Bid
      this.mkt_bid=mkt_bid;
    
      // Market days
      this.mkt_day=mkt_days;
    
      // Offer Type
      this.ex_tip=ex_tip;
    
      // Currency
      this.ex_cur=ex_cur;
    
      // Price
      this.fixed_price=fixed_price;
    
      // Variable price feed
      this.var_price_from_feed=var_price_from_feed;
    
      // Variable price feed component
      this.var_price_from_feed_component=var_price_from_feed_component;
    
      // Variable price market
      this.var_price_from_market=var_price_from_market;
    
      // Accept escrowers
      this.acceept_escrowers=acceept_escrowers;
    
      // Escrower
      this.esc_1=esc_1;
    
      // Escrower
      this.esc_2=esc_2;
    
      // Escrower
      this.esc_3=esc_3;
    
      // Escrower
      this.esc_4=esc_4;
    
      // Escrower
      this.esc_5=esc_5;
      
      // Hash
      this.hash=UTILS.BASIC.hash(adr+
                                 section+
                                 section_country+
                                 title+
                                 description+
                                 internalID+
                                 web_page+
                                 pic+
                                 String.valueOf(mkt_bid)+
                                 String.valueOf(mkt_days)+
                                 ex_tip+
                                 ex_cur+
                                 String.valueOf(fixed_price)+
                                 var_price_from_feed+
                                 var_price_from_feed_component+
                                 var_price_from_market+
                                 acceept_escrowers+
                                 esc_1+esc_2+esc_3+esc_4+esc_5+
                                 String.valueOf(mkt_bid)+
                                 String.valueOf(mkt_days));
      
      // Sign
      this.sign();
  }
  
  public CResult check(CBlockPayload block)
  {
     // Super class
     CResult res=super.check(block);
     if (res.passed==false) return res;
  	 
   
     
	  // Return
	  return new CResult(true, "Ok", "CNewExchangerPayload", 77);
  }
  
  public CResult commit(CBlockPayload block)
  {
	   CResult res=this.check(block);
	   if (res.passed==false) return res;
	  
      // Superclass
      super.commit(block);
      
     
      
      
  	// Return 
  	return new CResult(true, "Ok", "CAddNewAssetPayload.java", 149);
   }
}
