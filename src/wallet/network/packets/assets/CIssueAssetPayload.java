// Author : Vlad Cristian
// Contact : vcris@gmx.com

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
	// Symbol
        String symbol;
        
        // Title
        String title;
        
        // Description
        String description;
        
        // How to buy
        String how_buy;
        
        // How to sell
        String how_sell;
        
        // Web page
        String web_page;
        
        // Pic
        String pic;
        
         // Market days
        double days;
        
        // Qty
        long qty;
        
        // Transaction fee address
        String trans_fee_adr;
        
        // Transaction fee
        double trans_fee;
        
	
        public CIssueAssetPayload(String adr,
                                  String symbol,
                                  String title,
                                  String description,
                                  String how_buy,
                                  String how_sell,
                                  String web_page,
                                  String pic,
                                  long days,
                                  long qty,
                                  String trans_fee_adr,
                                  double trans_fee) throws Exception
        {
	    super(adr);
	   
	    // Symbol
            this.symbol=symbol;
        
            // Title
            this.title=title;
        
            // Description
            this.description=description;
            
            // How to buy
            this.how_buy=how_buy;
            
            // How to sell
            this.how_sell=how_sell;
        
            // Web page
            this.web_page=web_page;
        
            // Pic
            this.pic=pic;
        
            // Market days
            this.days=days;
        
            // Qty
            this.qty=qty;
        
            // Transaction fee address
            this.trans_fee_adr=trans_fee_adr;
        
            // Transaction fee
            this.trans_fee=trans_fee;
            
	    // Hash
 	    hash=UTILS.BASIC.hash(this.getHash()+
                                 this.symbol+
                                 this.title+
                                 this.description+
                                 this.how_buy+
                                 this.how_sell+
                                 this.web_page+
                                 this.pic+
                                 this.days+
                                 this.qty+
                                 this.trans_fee_adr+
                                 UTILS.FORMAT_2.format(this.trans_fee));
        
        // Sign
        this.sign();
   }
    
    public void check(CBlockPayload block) throws Exception
    {
        // Super class
        super.check(block);
        
        // Symbol length
        if (!UTILS.BASIC.isSymbol(this.symbol))
           throw new Exception("Invalid symbol - CIssueAssetPayload.java");
        
        // Same symbol
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
  		                           + "FROM assets "
  		                          + "WHERE symbol='"+this.symbol+"'");
        
        if (UTILS.DB.hasData(rs))
           throw new Exception("Asset symbol already exist - CIssueAssetPayload.java");
            
        // Qty
        if (this.qty<1000 || this.qty>10000000000L)
    	   throw new Exception("Invalid qty - CIssueAssetPayload.java");
        
        // Title
        if (!UTILS.BASIC.isTitle(this.title))
            throw new Exception("Invalid title - CIssueAssetPayload.java");
        
        // Description
        if (!UTILS.BASIC.isDesc(this.description))
            throw new Exception("Invalid description - CIssueAssetPayload.java");
        
        // Web page
        if (!this.web_page.equals(""))
          if (!UTILS.BASIC.isLink(this.web_page))
             throw new Exception("Invalid web page - CIssueAssetPayload.java");
        
        // How buy
        if (!this.how_buy.equals(""))
          if (!UTILS.BASIC.isDesc(this.how_buy))
             throw new Exception("Invalid how to buy - CIssueAssetPayload.java");
        
        // How sell
        if (!this.how_sell.equals(""))
          if (!UTILS.BASIC.isDesc(this.how_sell))
             throw new Exception("Invalid to sell - CIssueAssetPayload.java");
        
        // Pic
        if (!this.pic.equals(""))
          if (!UTILS.BASIC.isPic(this.pic))
             throw new Exception("Invalid pic - CIssueAssetPayload.java");
        
       // Days
       if (this.days<1000)
          throw new Exception("Invalid days - CIssueAssetPayload.java");
        
        // Transaction fee address
        if (!UTILS.BASIC.adressValid(this.trans_fee_adr))
            throw new Exception("Invalid transaction fee address - CIssueAssetPayload.java");
        
        // Transaction fee
        if (this.trans_fee<0.01 || this.trans_fee>10)
            throw new Exception("Invalid transaction fee - CIssueAssetPayload.java");
       
        // Sealed ? 
        if (UTILS.BASIC.isSealed(this.target_adr))
           throw new Exception("Sealed address - CIssueAssetPayload.java");
        
         // Mkt address ?
        if (UTILS.BASIC.isMktAdr(this.target_adr) || 
            UTILS.BASIC.isContractAdr(this.target_adr) ||
            UTILS.BASIC.isSpecMktAdr(this.target_adr))
           throw new Exception("Market address - CIssueAssetPayload.java");
            
        // Calculates hash
        String h=UTILS.BASIC.hash(this.getHash()+
                                 this.symbol+
                                 this.title+
                                 this.description+
                                 this.how_buy+
                                 this.how_sell+
                                 this.web_page+
                                 this.pic+
                                 days+
                                 this.qty+
                                 this.trans_fee_adr+
                                 UTILS.FORMAT_2.format(this.trans_fee));
        
        // Check hash
        if (!this.hash.equals(h))
           throw new Exception("Invalid hash - CIssueAssetPayload.java");
    }
    
    public void commit(CBlockPayload block) throws Exception
    {
 	// Superclass
        super.commit(block);
 	   
        // Insert asset
        UTILS.DB.executeUpdate("INSERT INTO assets "
                                     + "SET adr='"+this.target_adr+"', "
                                         + "symbol='"+this.symbol+"', "
                                         + "title='"+UTILS.BASIC.base64_encode(this.title)+"', "
                                         + "description='"+UTILS.BASIC.base64_encode(this.description)+"', "
                                         + "how_buy='"+UTILS.BASIC.base64_encode(this.how_buy)+"', "
                                         + "how_sell='"+UTILS.BASIC.base64_encode(this.how_sell)+"', "
                                         + "web_page='"+UTILS.BASIC.base64_encode(this.web_page)+"', "
                                         + "pic='"+UTILS.BASIC.base64_encode(this.pic)+"', "
                                         + "expire='"+(this.block+(1440*this.days))+"', "
                                         + "qty='"+this.qty+"', "
                                         + "trans_fee_adr='"+this.trans_fee_adr+"', "
                                         + "trans_fee='"+UTILS.FORMAT_2.format(this.trans_fee)+"', "
                                         + "block='"+this.block+"'");
        
           // Update 
           UTILS.DB.executeUpdate("INSERT INTO assets_owners "
                                        + "SET owner='"+this.target_adr+"', "
                                            + "symbol='"+this.symbol+"', "
                                            + "qty='"+this.qty+"', "
                                            + "block='"+this.block+"'");
     }
 }