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
   
   public CResult check(CBlockPayload block) throws Exception
    {
        try
        {
           // Check if feed exist
           Statement s=UTILS.DB.getStatement();
           ResultSet rs=s.executeQuery("SELECT * "
                                       + "FROM feeds "
                                      + "WHERE symbol='"+this.feed_symbol+"' "
                                        + "AND adr='"+this.target_adr+"'");
           
           // Return results
           if (!UTILS.DB.hasData(rs))
           {
	       rs.close(); s.close();
               return new CResult(true, "Ok", "CNewFeedPayload", 67);
           }
		   
           // Check feed components
           for (int a=0; a<=this.values.size()-1; a++)
           {
                CFeedComponent fc=(CFeedComponent) this.values.get(a);
                CResult res=fc.check(feed_symbol);
                
                if (!res.passed) 
	        {
		    rs.close(); s.close();
		    return res;
		}
           }
           
            // Hash
            String h=UTILS.BASIC.hash(this.getHash()+
			              feed_symbol+
		                      UTILS.BASIC.hash(UTILS.SERIAL.serialize(this.values)));
            
            if (!h.equals(this.hash))
	    {
		rs.close(); s.close();
                return new CResult(false, "Invalid hash", "CNewFeedPayload", 67);
	    }
			
	    rs.close(); s.close();
        }
        catch (SQLException ex)
        {
           UTILS.LOG.log("SQLException", ex.getMessage(), "CNewFeedPayload.java", 60);
        }
         // Return
	 return new CResult(true, "Ok", "CNewFeedPayload", 67);
    }
    
    public CResult commit(CBlockPayload block) throws Exception
    {
        for (int a=0; a<=this.values.size()-1; a++)
        {
                CFeedComponent fc=(CFeedComponent) this.values.get(a);
                fc.commit(this.feed_symbol);
        }
        
        // Return
	return new CResult(true, "Ok", "CNewFeedPayload", 67); 
    }    
}
