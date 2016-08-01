// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.trade.feeds;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.network.packets.*;
import wallet.network.*;
import wallet.kernel.*;
import wallet.network.packets.blocks.CBlockPayload;


public class CNewFeedComponentPayload extends CPayload
{
    // Feed Symbol
    String feed_symbol;
    
    // Title
    String title;
    
    // Description
    String description;
    
    // Type
    String type;
    
    // Symbol
    String symbol;
    
    // Real Symbol
    String rl_symbol;

    // Fee
    double fee;
    
    // Days
    long days;
    
    public CNewFeedComponentPayload(String feed_adr,
                                    String feed_symbol,
                                    String title,
                                    String description,
                                    String type,
                                    String symbol,
                                    String rl_symbol,
                                    double fee,
                                    long days) throws Exception
    {
       // Super
       super(feed_adr);
    
       // Feed Symbol
       this.feed_symbol=feed_symbol;
    
       // Title
       this.title=title;
    
       // Description
       this.description=description;
    
       // Symbol
       this.symbol=symbol;
       
       // Type
       this.type=type;
       
       // RL symbol
       this.rl_symbol=rl_symbol;
       
       // Fee
       this.fee=fee;
       
       // Days
       this.days=days;    
       
       // Hash
       hash=UTILS.BASIC.hash(this.getHash()+
			     feed_symbol+
		             title+
                             description+
                             type+
                             symbol+
                             rl_symbol+
                             String.valueOf(fee)+
			     String.valueOf(days));
       
       // Sign
       this.sign();
    }
    
    public void check(CBlockPayload block) throws Exception
    {
        // Symbol valid
        if (!UTILS.BASIC.isSymbol(this.feed_symbol))
           throw new Exception("Invalid symbol - CNewFeedComponentPayload.java");
            
        // Feed symbol valid
        if (!UTILS.BASIC.isSymbol(this.symbol))
           throw new Exception("Invalid feec symbol - CNewFeedComponentPayload.java");
             
        // Check feed
        if (UTILS.BASIC.isBranch(this.feed_symbol, this.symbol))
           throw new Exception("Feed already exist - CNewFeedComponentPayload.java");
         
        // Fee
        if (this.fee<0)
           throw new Exception("Invalid fee - CNewFeedComponentPayload.java");
             
        // Title
        if (!UTILS.BASIC.isTitle(this.title))
           throw new Exception("Invalid title - CNewFeedComponentPayload.java");
         
        // Description
        if (!UTILS.BASIC.isDesc(this.description))
            throw new Exception("Invalid description - CNewFeedComponentPayload.java");
             
        // Type
        if (this.type.equals("ID_CRYPTO") && 
            this.type.equals("ID_FX") && 
            this.type.equals("ID_COMM") && 
            this.type.equals("ID_IND") && 
            this.type.equals("ID_STOCKS") && 
            this.type.equals("ID_OTHER"))
        throw new Exception("Invalid branch type, CNewFeedComponentPayload");
             
        // Hash
        String h=UTILS.BASIC.hash(this.getHash()+
			          feed_symbol+
		                  title+
                                  description+
                                  type+
                                  symbol+
                                  rl_symbol+
                                  String.valueOf(fee)+
			          String.valueOf(days));
        
        if (!h.equals(hash))
            throw new Exception("Invalid hash - CNewFeedComponentPayload.java");
       
    }
    
    public void commit(CBlockPayload block) throws Exception
    {
        // Super class
        super.commit(block);
       
        // Insert feed
        UTILS.DB.executeUpdate("INSERT INTO feeds_branches(feed_symbol, "
                                                              + "name, "
                                                              + "description, "
                                                              + "type, "
                                                              + "symbol, "
                                                              + "expire, "
                                                              + "fee, "
                                                              + "rl_symbol, "
                                                              + "block) VALUES('"+
                                                              this.feed_symbol+"', '"+
                                                              UTILS.BASIC.base64_encode(this.title)+"', '"+
                                                              UTILS.BASIC.base64_encode(this.description)+"', '"+
                                                              this.type+"', '"+
                                                              this.symbol+"', '"+
                                                              (this.block+(1440*this.days))+"', '"+
                                                              this.fee+"', '"+
                                                              this.rl_symbol+"', '"+
                                                              this.block+"')");
         
       
    }
    
    
}
