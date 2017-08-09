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

    // Days
    long days;
    
    public CNewFeedComponentPayload(String feed_adr,
                                    String feed_symbol,
                                    String title,
                                    String description,
                                    String type,
                                    String symbol,
                                    String rl_symbol,
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
                             days);
       
       // Sign
       this.sign();
    }
    
    public void check(CBlockPayload block) throws Exception
    {
        // Super class
   	super.check(block);
          
        // Symbol valid
        if (!UTILS.BASIC.isSymbol(this.feed_symbol))
           throw new Exception("Invalid symbol - CNewFeedComponentPayload.java");
            
        // Feed symbol valid
        if (!UTILS.BASIC.isSymbol(this.symbol))
           throw new Exception("Invalid feed symbol - CNewFeedComponentPayload.java");
             
        // Check feed
        if (UTILS.BASIC.isBranch(this.feed_symbol, this.symbol))
           throw new Exception("Feed already exist - CNewFeedComponentPayload.java");
         
        // Title
        if (!UTILS.BASIC.isTitle(this.title))
           throw new Exception("Invalid title - CNewFeedComponentPayload.java");
         
        // Description
        if (!UTILS.BASIC.isDesc(this.description))
            throw new Exception("Invalid description - CNewFeedComponentPayload.java");
             
        // Type
        if (!this.type.equals("ID_CRYPTO") && 
            !this.type.equals("ID_FX") && 
            !this.type.equals("ID_COMM") && 
            !this.type.equals("ID_IND") && 
            !this.type.equals("ID_STOCKS") && 
            !this.type.equals("ID_OTHER"))
        throw new Exception("Invalid branch type, CNewFeedComponentPayload");
        
        // Days
        if (this.days<10)
            throw new Exception("Invalid days, CNewFeedComponentPayload");
        
        // Load feed data
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM feeds "
                                          + "WHERE symbol='"+this.feed_symbol+"' "
                                            + "AND adr='"+this.target_adr+"'");
        
        // No data ?
        if (!UTILS.DB.hasData(rs))
            throw new Exception("Invalid feed symbol, CNewFeedComponentPayload");
        
        // Next
        rs.next();
        
        // Feed expire block
        long feed_expire=rs.getLong("expire");
        
        // Branch expire block
        long branch_expire=this.block+this.days*1440;
        
        // Expire date
        if (branch_expire>feed_expire)
           throw new Exception("Invalid expire block, CNewFeedComponentPayload");
        
        // RL symbol not empty
        if (!this.rl_symbol.equals(""))
        {
            // RL symbol
            if (!UTILS.BASIC.isString(this.rl_symbol))
                throw new Exception("Invalid RL symbol, CNewFeedComponentPayload");
            
            // Length
            if (this.rl_symbol.length()<2 || this.rl_symbol.length()>20)
                throw new Exception("Invalid RL symbol, CNewFeedComponentPayload");
        }
        
        // Hash
        String h=UTILS.BASIC.hash(this.getHash()+
                                  feed_symbol+
		                  title+
                                  description+
                                  type+
                                  symbol+
                                  rl_symbol+
                                  days);
        
        if (!h.equals(hash))
            throw new Exception("Invalid hash - CNewFeedComponentPayload.java");
       
    }
    
    public void commit(CBlockPayload block) throws Exception
    {
        // Super class
        super.commit(block);
       
        // Insert feed
        UTILS.DB.executeUpdate("INSERT INTO feeds_branches "
                                     + "SET feed_symbol='"+this.feed_symbol+"', "
                                         + "name='"+UTILS.BASIC.base64_encode(this.title)+"', "
                                         + "description='"+UTILS.BASIC.base64_encode(this.description)+"', "
                                         + "type='"+this.type+"', "
                                         + "symbol='"+this.symbol+"', "
                                         + "expire='"+(this.block+(1440*this.days))+"', "
                                         + "rl_symbol='"+UTILS.BASIC.base64_encode(this.rl_symbol)+"', "
                                         + "block='"+this.block+"'");
    }
    
    
}
