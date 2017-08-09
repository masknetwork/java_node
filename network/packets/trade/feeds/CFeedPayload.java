// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.trade.feeds;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import wallet.network.packets.*;
import wallet.network.*;
import wallet.kernel.*;
import wallet.network.packets.blocks.CBlockPayload;

public class CFeedPayload extends CPayload
{
    // Feed
    public String feed_symbol;
    
    // Values
    ArrayList values=new ArrayList();
    
   public CFeedPayload(String feed_adr, String feed_symbol) throws Exception
   {
       // Constructor
       super(feed_adr);
       
       // Feed symbol
       this.feed_symbol=feed_symbol;
   }
   
   public void doSeal() throws Exception
   {
       // Hash
       hash=UTILS.BASIC.hash(this.getHash()+
			     feed_symbol+
		             UTILS.BASIC.hash(UTILS.SERIAL.serialize(this.values)));
       
       // Sign
       this.sign();
   }
   
   public void addVal(String symbol, double val, String mkt_status) throws Exception
   {
       CFeedComponent fc=new CFeedComponent(symbol, val, mkt_status);
       this.values.add(fc);
   }
   
   public void check(CBlockPayload block) throws Exception
   {
        // Super class
   	super.check(block);
          
        // Feed symbol
        if (!UTILS.BASIC.isSymbol(this.feed_symbol))
            throw new Exception("Invalid feed symbol - CFeedPayload.java");
            
        // Check if feed exist
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM feeds "
                                          + "WHERE symbol='"+this.feed_symbol+"' "
                                            + "AND adr='"+this.target_adr+"'");
           
        // Return results
        if (!UTILS.DB.hasData(rs))
            throw new Exception("Invalid feed - CFeedPayload.java");
       	   
        // Check feed components
        for (int a=0; a<=this.values.size()-1; a++)
        {
            CFeedComponent fc=(CFeedComponent) this.values.get(a);
            fc.check(feed_symbol);
        }
           
        // Hash
        String h=UTILS.BASIC.hash(this.getHash()+
			              feed_symbol+
		                      UTILS.BASIC.hash(UTILS.SERIAL.serialize(this.values)));
            
        if (!h.equals(this.hash))
	   throw new Exception("Invalid hash - CFeedPayload.java");
   }
    
    public void commit(CBlockPayload block) throws Exception
    {
        // Remove data
        UTILS.DB.executeUpdate("DELETE FROM feeds_data "
                                   + "WHERE block="+this.block+" "
                                     + "AND feed='"+this.feed_symbol+"'");
        
        for (int a=0; a<=this.values.size()-1; a++)
        {
            CFeedComponent fc=(CFeedComponent) this.values.get(a);
            fc.commit(this.feed_symbol, this.block);
        }
    }    
}
