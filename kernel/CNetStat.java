// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.kernel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class CNetStat
{
    // Tables
    public ArrayList<String> tables=new ArrayList<String>();
    
    // Last block
    public long last_block;
    
    // Last hash
    public String last_block_hash;
    
    // Last tstamp
    public long last_block_tstamp;
    
    // Difficulty
    public long net_dif;
    
    // SQL log
    String sql_log_status;
    
    // Addresses
    public String adr;
    
    // Addresses Options
    public String options;
    
    // Ads
    public String ads;
    
    // Assets
    public String assets;
    
    // Assets owners
    public String assets_owners;
    
    // Blocks
    public String blocks;
    
    // Domains
    public String domains;
    
    // Escrowed
    public String escrowed;
    
    // Feeds
    public String feeds;
    
    // Bets
    public String feeds_bets;
    
    // Bets pos
    public String feeds_bets_pos;
    
    // Branches
    public String feeds_branches;
    
    // Multisig
    public String multisig;
    
    // Profiles
    public String profiles;
    
    // Req Data
    public String req_data;
    
    // Tweets
    public String tweets;
    
    // Tweets Comments
    public String tweets_comments;
    
    // Tweets Follow
    public String tweets_follow;
    
    // Tweets Links
    public String tweets_likes;
    
    // Blocks per day
    public long blocks_per_day=4320;
    
    
    public CNetStat() throws Exception
    {
        try
        {
           // Add tables
           this.tables.add("adr");
           this.tables.add("adr_options");
           this.tables.add("ads");
           this.tables.add("assets");
           this.tables.add("assets_owners");
           this.tables.add("blocks");
           this.tables.add("domains");
           this.tables.add("escrowed");
           this.tables.add("feeds");
           this.tables.add("feeds_bets");
           this.tables.add("feeds_bets_pos");
           this.tables.add("feeds_branches");
           this.tables.add("multisig");
           this.tables.add("profiles");
           this.tables.add("req_data");
           this.tables.add("tweets");
           this.tables.add("tweets_comments");
           this.tables.add("tweets_follow");
           this.tables.add("tweets_likes");
       
           // Statement
           Statement s=UTILS.DB.getStatement();
       
           // Load data
           ResultSet rs=s.executeQuery("SELECT * FROM net_stat");
           
           // Next
           rs.next();
           
           // Last block
           this.last_block=rs.getLong("last_block");
           
           // Last hash
           this.last_block_hash=rs.getString("last_block_hash");
           
           // Difficulty
           this.net_dif=rs.getLong("net_dif");
           
           // SQL log status
           this.sql_log_status=rs.getString("sql_log_status");
           
           // Addresses
           this.adr=rs.getString("adr");
    
           // Addresses Options
           this.options=rs.getString("adr_options");
    
           // Ads
           this.ads=rs.getString("ads");
    
           // Assets
           this.assets=rs.getString("assets");
    
           // Assets owners
           this.assets_owners=rs.getString("assets_owners");
    
           // Blocks
           this.blocks=rs.getString("blocks");
    
           // Domains
           this.domains=rs.getString("domains");
    
           // Escrowed
           this.escrowed=rs.getString("escrowed");
    
           // Feeds
           this.feeds=rs.getString("feeds");
    
           // Bets
           this.feeds_bets=rs.getString("feeds_bets");
    
           // Bets pos
           this.feeds_bets_pos=rs.getString("feeds_bets_pos");
    
           // Branches
           this.feeds_branches=rs.getString("feeds_branches");
    
           // Multisig
           this.multisig=rs.getString("multisig");
    
           // Profiles
           this.profiles=rs.getString("profiles");
    
           // Req Data
           this.req_data=rs.getString("req_data");
    
           // Tweets
           this.tweets=rs.getString("tweets");
    
           // Tweets Comments
           this.tweets_comments=rs.getString("tweets_comments");
    
           // Tweets Follow
           this.tweets_follow=rs.getString("tweets_follow");
    
           // Tweets Links
           this.tweets_likes=rs.getString("tweets_likes");
       }
        catch (SQLException ex)
        {
               UTILS.LOG.log("SQLException", ex.getMessage(), "CNetStat.java", 84);
        }
       
    }
    
    public void addQuery(String query) throws Exception
    {
        String table="";
        
       for (int a=0; a<=this.tables.size()-1; a++)
       {
           // Table
           table=this.tables.get(a);
           
           // Inserts
           if (query.indexOf("INSERT INTO "+table+" ")==0 || 
               query.indexOf("INSERT INTO "+table+"(")==0) 
           this.add(query);
           
           // Updates
           if (query.indexOf("UPDATE "+table+" ")==0) 
           this.add(query);
           
           // Deletes
           if (query.indexOf("DELETE FROM "+table+" ")==0) 
           this.add(query);
       }
    }
    
    public void add(String query) throws Exception
    {
        // Query hash
        String query_hash=UTILS.BASIC.hash(UTILS.BASIC.base64_encode(query));
        
        // New hash
        String hash=UTILS.BASIC.hash(this.sql_log_status+String.valueOf(this.last_block)+query_hash);
        
        // Insert
        UTILS.DB.executeUpdate("INSERT INTO sql_log(query, block, hash) "
                                 + "VALUES('"+UTILS.BASIC.base64_encode(query)+"', '"+this.last_block+"', '"+hash+"')");
        
        // Update
        UTILS.DB.executeUpdate("UPDATE net_stat SET sql_log_status='"+hash+"'");
        
        // Update
        this.sql_log_status=hash;
    }
    
    public void refreshTables(long block) throws Exception
    {
        // Init
        UTILS.DB.executeUpdate("SET group_concat_max_len = 1000000000000000");
        
        // Adr
        UTILS.DB.executeUpdate("UPDATE adr SET rowhash=SHA2(CONCAT(adr, "
                                                                     + "balance, "
                                                                     + "block, "
                                                                     + "total_received, "
                                                                     + "total_spent, "
                                                                     + "trans_no, "
                                                                     + "tweets, "
                                                                     + "following, "
                                                                     + "followers, "
                                                                     + "last_interest), 256) where block>0");
        
        UTILS.DB.executeUpdate("UPDATE net_stat SET adr=(SELECT SHA2(GROUP_CONCAT(rowhash), 256) AS st FROM adr)");
    }
    
   
}
