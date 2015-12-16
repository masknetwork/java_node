package wallet.network.packets.feeds;

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
    String feed_symbol;
    
    // Values
    ArrayList values=new ArrayList();
    
   public CFeedPayload(String feed_adr, String feed_symbol)
   {
       // Constructor
       super(feed_adr);
       
       // Feed symbol
       this.feed_symbol=feed_symbol;
   }
   
   public void doSeal()
   {
       // Hash
       hash=UTILS.BASIC.hash(this.getHash()+
			     feed_symbol+
		             UTILS.BASIC.hash(UTILS.SERIAL.serialize(this.values)));
       
       // Sign
       this.sign();
   }
   
   public void addVal(String symbol, double val)
   {
       CFeedComponent fc=new CFeedComponent(symbol, val);
       this.values.add(fc);
   }
   
   public CResult check(CBlockPayload block)
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
	       s.close();
               return new CResult(true, "Ok", "CNewFeedPayload", 67);
           }
		   
           // Check feed components
           for (int a=0; a<=this.values.size()-1; a++)
           {
                CFeedComponent fc=(CFeedComponent) this.values.get(a);
                CResult res=fc.check(feed_symbol);
                
                if (!res.passed) 
	        {
		    s.close();
		    return res;
		}
           }
           
            // Hash
            String h=UTILS.BASIC.hash(this.getHash()+
			              feed_symbol+
		                      UTILS.BASIC.hash(UTILS.SERIAL.serialize(this.values)));
            
            if (!h.equals(this.hash))
	    {
		s.close();
                return new CResult(false, "Invalid hash", "CNewFeedPayload", 67);
	    }
			
	    s.close();
        }
        catch (SQLException ex)
        {
           UTILS.LOG.log("SQLException", ex.getMessage(), "CNewFeedPayload.java", 60);
        }
         // Return
	 return new CResult(true, "Ok", "CNewFeedPayload", 67);
    }
    
    public CResult commit(CBlockPayload block)
    {
        // Commit
        CResult res=this.check(block);
        if (res.passed)
        {
            for (int a=0; a<=this.values.size()-1; a++)
            {
                CFeedComponent fc=(CFeedComponent) this.values.get(a);
                fc.commit(this.feed_symbol);
            }
        }
        
        // Return
	return new CResult(true, "Ok", "CNewFeedPayload", 67); 
    }    
}
