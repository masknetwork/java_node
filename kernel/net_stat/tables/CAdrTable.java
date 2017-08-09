package wallet.kernel.net_stat.tables;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import wallet.kernel.UTILS;
import wallet.kernel.net_stat.CTable;

public class CAdrTable extends CTable
{
    public CAdrTable()
    {
        // Constructor
        super("adr");
    }
    
    // Create
    public void create() throws Exception
    {
       // Create table
       UTILS.DB.executeUpdate("CREATE TABLE adr(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
				              + "adr VARCHAR(500) NOT NULL DEFAULT '', "
				              + "balance DOUBLE(20,8) NOT NULL DEFAULT 0, "
		                              + "created BIGINT NOT NULL DEFAULT 0, "		              
                                              + "block BIGINT NOT NULL DEFAULT 0, "
                                              + "ref VARCHAR(500) NOT NULL DEFAULT '', "
                                              + "sealed BIGINT NOT NULL DEFAULT 0)");
		   
        UTILS.DB.executeUpdate("CREATE UNIQUE INDEX adr ON adr(adr)");
	UTILS.DB.executeUpdate("CREATE INDEX block ON adr(block)");
    }
    
    public boolean hasTableRecords(String table, String col, String adr) throws Exception
    {
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM "+table+" "
                                          + "WHERE "+col+"='"+adr+"'");
        
        if (!UTILS.DB.hasData(rs))
            return false;
        else
            return true;
    }
    
    public boolean hasRecords(String adr) throws Exception
    {
        // Ads ?
        if (this.hasTableRecords("ads", "adr", adr)) 
            return true;
        
        // Assets
        if (this.hasTableRecords("assets", "adr", adr)) 
            return true;
        
        // Assets owners
        if (this.hasTableRecords("assets_owners", "owner", adr)) 
            return true;
        
        // Del votes
        if (this.hasTableRecords("del_votes", "adr", adr)) 
            return true;
        
        // Domains
        if (this.hasTableRecords("domains", "adr", adr)) 
            return true;
        
        // Escrowed
        if (this.hasTableRecords("escrowed", "sender_adr", adr)) 
            return true;
        
        if (this.hasTableRecords("escrowed", "rec_adr", adr)) 
            return true;
        
        if (this.hasTableRecords("escrowed", "escrower", adr)) 
            return true;
        
        // Profiles
        if (this.hasTableRecords("profiles", "adr", adr)) 
            return true;
        
        // Assets markets
        if (this.hasTableRecords("assets_mkts", "adr", adr)) 
            return true;
        
        // Assets markets pos
        if (this.hasTableRecords("assets_mkts_pos", "adr", adr)) 
            return true;
        
        // Comments
        if (this.hasTableRecords("comments", "adr", adr)) 
            return true;
        
        // Votes
        if (this.hasTableRecords("votes", "adr", adr)) 
            return true;
        
        // Tweets
        if (this.hasTableRecords("tweets", "adr", adr)) 
            return true;
        
        // Tweets follow
        if (this.hasTableRecords("tweets_follow", "adr", adr)) 
            return true;
        
        // Feeds
        if (this.hasTableRecords("feeds", "adr", adr)) 
            return true;
        
        // Feeds bets
        if (this.hasTableRecords("feeds_bets", "adr", adr)) 
            return true;
        
        // Feeds bets pos
        if (this.hasTableRecords("feeds_bets_pos", "adr", adr)) 
            return true;
        
        // Feeds spec mkts
        if (this.hasTableRecords("feeds_spec_mkts", "adr", adr)) 
            return true;
        
        // Feeds spec mkts pos
        if (this.hasTableRecords("feeds_spec_mkts_pos", "adr", adr)) 
            return true;
        
        // Ok to delete
        return false;
    }
    
    public void expired(long block) throws Exception
    {
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM adr "
                                          + "WHERE balance<=0.0001");
        
        while (rs.next())
           if (!this.hasRecords(rs.getString("adr")))
               UTILS.DB.executeUpdate("DELETE FROM adr "
                                          + "WHERE adr='"+rs.getString("adr")+"'");
    }
    
    
    public void loadCheckpoint(String hash) throws Exception
    {
        // Drop table
        UTILS.DB.executeUpdate("DROP TABLE adr");
        
        // Create table
        this.create();
        
        // From file
        this.fromFile(hash);
    }
}
