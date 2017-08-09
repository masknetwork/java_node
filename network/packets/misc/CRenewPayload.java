// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.misc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.*;

public class CRenewPayload extends CPayload
{
    // Address
    String adr;
    
    // Days
    long days;
    
    // Table
    String table;
    
    // ID long
    long longID;
    
    // ID String
    String stringID;
    
    // ID String 2
    String stringID_2;
    
    // Serial
   private static final long serialVersionUID = 100L;
                                           
    public CRenewPayload(String adr, 
                         String table,
                         long days,
                         long longID,
                         String stringID,
                         String stringID_2) throws Exception
    {
        // Constructor
        super(adr);
       
        // Address
        this.adr=adr;
    
        // Days
        this.days=days;
    
        // Table
        this.table=table;
    
        // Long ID
        this.longID=longID;
        
        // String ID
        this.stringID=stringID;
        
        // Hash
        hash=UTILS.BASIC.hash(this.getHash()+
			      String.valueOf(days)+
			      table+
                              this.longID+
                              this.stringID);
		
	// Sign
	this.sign();
    }
    
     public void check(CBlockPayload block) throws Exception
     {
        // Constructor
        super.check(block);
        
        // Result set
        ResultSet rs;
        
        // Fee
        double fee=0;
        
        // Table valid ?
        if (!this.table.equals("assets") && 
            !this.table.equals("assets_mkts") && 
            !this.table.equals("domains") && 
            !this.table.equals("feeds") && 
            !this.table.equals("feeds_branches") && 
            !this.table.equals("feeds_spec_mkts") && 
            !this.table.equals("feeds_spec_mkts_pos") && 
            !this.table.equals("tweets_follow"))
        throw new Exception("Invalid table");
        
        // ID valid ?
        switch (this.table)
        {
            // Assets
            case "assets" : rs=UTILS.DB.executeQuery("SELECT * "
                                                     + "FROM assets "
                                                    + "WHERE assetID='"+this.longID+"' "
                                                      + "AND adr='"+this.target_adr+"'");
                            
                            if (!UTILS.DB.hasData(rs))
                               throw new Exception("Invalid asset ID - CRenewPayload.java");
                            
                            break;  
                            
            // Assets markets
            case "assets_mkts" : rs=UTILS.DB.executeQuery("SELECT * "
                                                          + "FROM assets_mkts "
                                                         + "WHERE mktID='"+this.longID+"' "
                                                           + "AND adr='"+this.target_adr+"'");
                                 
                                 // Market ID
                                 if (!UTILS.DB.hasData(rs))
                                     throw new Exception("Invalid market ID - CRenewPayload.java");
                                 
                                 // Next
                                 rs.next();
                                 
                                 // Expiration
                                 if (!rs.getString("cur").equals("MSK"))
                                    if (rs.getLong("expire")+this.days*1440>UTILS.BASIC.getAssetExpireBlock(rs.getString("cur")))
                                        throw new Exception("Invalid days - CRenewPayload.java");
                                 
                                 // Fee
                                 fee=this.days*0.0001;
                                 
                                break;
                                      
            // Domains
            case "domains" :   // Domain
                               if (!UTILS.BASIC.isDomain(this.stringID))
                                  throw new Exception("Invalid domain - CRenewPayload.java");
                               
                                rs=UTILS.DB.executeQuery("SELECT * "
                                                         + "FROM domains "
                                                        + "WHERE domain='"+this.stringID+"' "
                                                          + "AND adr='"+this.target_adr+"'");
                                
                                // HAs data ?
                                if (!UTILS.DB.hasData(rs))
                                   throw new Exception("Invalid domain - CRenewPayload.java");
                                
                                // Fee
                                fee=this.days*0.0001;
                            
                                break;
                              
            // feeds
            case "feeds" :  // Symbol
                            if (!UTILS.BASIC.isSymbol(this.stringID))
                                 throw new Exception("Invalid symbol - CRenewPayload.java");
                            
                            rs=UTILS.DB.executeQuery("SELECT * "
                                                     + "FROM feeds "
                                                    + "WHERE symbol='"+this.stringID+"' "
                                                      + "AND adr='"+this.target_adr+"'");
                                
                            // Has data ?
                            if (!UTILS.DB.hasData(rs))
                               throw new Exception("Invalid domain - CRenewPayload.java");
                            
                            // Fee
                            fee=this.days*0.0001;
                            
                            break;
                              
            // Feeds branches
            case "feeds_branches" :  // Symbol
                                     if (!UTILS.BASIC.isSymbol(this.stringID) || 
                                         !UTILS.BASIC.isSymbol(this.stringID_2))
                                         throw new Exception("Invalid symbols - CRenewPayload.java");
                            
                                     rs=UTILS.DB.executeQuery("SELECT * "
                                                              + "FROM feeds_branches "
                                                             + "WHERE feed_symbol='"+this.stringID+"' "
                                                               + "AND symbol='"+this.stringID_2+"' "
                                                               + "AND adr='"+this.target_adr+"'");
                                
                                    // Has data ?
                                    if (!UTILS.DB.hasData(rs))
                                        throw new Exception("Invalid domain - CRenewPayload.java");
                                    
                                    // Check feed expire
                                    if (rs.getLong("expire")+this.days*1440>UTILS.BASIC.getFeedExpireBlock(this.stringID))
                                        throw new Exception("Invalid expire date - CRenewPayload.java");
                                    
                                    // Fee
                                    fee=this.days*0.0001;
                            
                                    break;
                              
            // Feeds spec mkts
            case "feeds_spec_mkts" :  rs=UTILS.DB.executeQuery("SELECT * "
                                                               + "FROM feeds_spec_mkts "
                                                              + "WHERE mktID='"+this.longID+"' "
                                                                + "AND adr='"+this.target_adr+"'");
            
                                       // Has data ?
                                       if (!UTILS.DB.hasData(rs))
                                           throw new Exception("Invalid domain - CRenewPayload.java");
                                       
                                       // Next
                                       rs.next();
                                       
                                       // Check with feed expiration
                                       if (rs.getLong("expire")+this.days*1440>UTILS.BASIC.getFeedExpireBlock(rs.getString("feed"), rs.getString("branch")))
                                          throw new Exception("Invalid expire block - CRenewPayload.java");
                                       
                                       // Fee
                                       fee=this.days*1440;
                                       
                                       // Break
                                       break;
                              
            // Feeds spec mkts pos
            case "feeds_spec_mkts_pos" :  rs=UTILS.DB.executeQuery("SELECT * "
                                                                   + "FROM feeds_spec_mkts_pos "
                                                                  + "WHERE posID='"+this.longID+"' "
                                                                    + "AND adr='"+this.target_adr+"'");
            
                                       // Has data ?
                                       if (!UTILS.DB.hasData(rs))
                                           throw new Exception("Invalid domain - CRenewPayload.java");
                                       
                                       // Next
                                       rs.next();
                                       
                                       // Expire
                                       long pos_expire=rs.getLong("expire");
                                       
                                       // Load market data
                                       rs=UTILS.DB.executeQuery("SELECT * "
                                                                + "FROM feeds_spec_mkts "
                                                               + "WHERE mktID='"+rs.getLong("mktID")+"'");
                                       
                                       // Check with feed expiration
                                       if (pos_expire+this.days*1440>rs.getLong("expire"))
                                          throw new Exception("Invalid expire block - CRenewPayload.java");
                                       
                                       // Fee
                                       fee=this.days*0.0001;
                                       
                                       break;
                              
            // Tweets follow
            case "tweets_follow" :  if (!UTILS.BASIC.isAdr(this.stringID))
                                        throw new Exception("Invalid ID - CRenewPayload.java");
            
                                    // Query
                                    rs=UTILS.DB.executeQuery("SELECT * "
                                                        + "FROM tweets_follow "
                                                       + "WHERE adr=='"+this.target_adr+"' "
                                                         + "AND follows='"+this.stringID +"'");
            
                                    // Has data ?
                                    if (!UTILS.DB.hasData(rs))
                                        throw new Exception("Invalid tweetID - CRenewPayload.java");
                                       
                                    // Fee
                                    fee=this.days*0.0001;       
                            
                                    break;
                            
                              
                            
        }
           
        // Can spend ?
        if (!UTILS.BASIC.canSpend(this.target_adr))
            throw new Exception("Address can't spend funds - CRenewPayload.java");
        
        // Fee
        
        // Check hash
        String h=UTILS.BASIC.hash(this.getHash()+
			             String.valueOf(days)+
			             table+
                                     this.longID+
                                     this.stringID+
                                     this.stringID_2);
           
        if (!h.equals(hash))
           throw new Exception("Invalid hash - CRenewPayload.java");
         
    }
	 
    public void commit(CBlockPayload block) throws Exception
    {
        // Blocks
        long blocks=this.days*1440;
        
        switch (this.table)
        {
            // Assets
            case "assets" : UTILS.DB.executeUpdate("UPDATE assets "
                                                    + "SET expire=expire+"+blocks+" "
                                                  + "WHERE assetID='"+this.longID+"'"); 
                            break;
            
            // Assets markets
            case "assets_mkts" : UTILS.DB.executeUpdate("UPDATE assets_mkts "
                                                         + "SET expire=expire+"+blocks+" "
                                                       + "WHERE mktID='"+this.longID+"'"); 
                                 break;
            
            // Domains
            case "domains" : UTILS.DB.executeUpdate("UPDATE domains "
                                                     + "SET expire=expire+"+blocks+" "
                                                   + "WHERE domain='"+this.stringID+"'"); 
                             break;
            
            // Feeds
            case "feeds" : UTILS.DB.executeUpdate("UPDATE feeds "
                                                     + "SET expire=expire+"+blocks+" "
                                                   + "WHERE symbol='"+this.stringID+"'"); 
                            break;
            
            // Feeds branches
            case "feeds_branches" : UTILS.DB.executeUpdate("UPDATE feeds_branches "
                                                            + "SET expire=expire+"+blocks+" "
                                                          + "WHERE feed_symbol='"+this.stringID+"' "
                                                            + "AND symbol='"+this.stringID_2+"'"); 
                                    break;
            
            // Feeds spec mkts
            case "feeds_spec_mkts" : UTILS.DB.executeUpdate("UPDATE feeds_spec_mkts "
                                                            + "SET expire=expire+"+blocks+" "
                                                          + "WHERE mktID='"+this.longID+"'"); 
                                    break;
            
            // Feeds spec mkts pos
            case "feeds_spec_mkts_pos" : UTILS.DB.executeUpdate("UPDATE feeds_spec_mkts_pos "
                                                                 + "SET expire=expire+"+blocks+" "
                                                               + "WHERE posID='"+this.longID+"'"); 
                                         break;
            
            // Tweets follow
            case "tweets_follow" : UTILS.DB.executeUpdate("UPDATE tweets_follow "
                                                           + "SET expire=expire+"+blocks+" "
                                                         + "WHERE adr='"+this.target_adr+"' "
                                                           + "AND follows='"+this.stringID+"'"); 
                                    break;
        }
        
        // Clear
        UTILS.ACC.clearTrans(this.hash, "ID_ALL", this.block);
    }
}


