// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.kernel.net_stat;
import wallet.kernel.net_stat.tables.CProfilesTable;
import wallet.kernel.net_stat.tables.CAdsTable;
import wallet.kernel.net_stat.tables.CAdrTable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import wallet.kernel.UTILS;
import wallet.kernel.net_stat.tables.CDomainsTable;
import wallet.kernel.net_stat.tables.CTweetsTable;

public class CNetStat extends Thread
{
    // Tables
    public ArrayList<String> tables=new ArrayList<String>();
    
    // Addresses
    public CAdrTable table_adr;
    
    // Ads
    public CAdsTable table_ads;
    
    // Domains
    public CDomainsTable table_domains;
    
    // Profiles
    public CProfilesTable table_profiles;
    
    // Tweets
    public CTweetsTable table_tweets;
    
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
    String adr;
    
    // Addresses Options
    String options;
    
    // Ads
    String ads;
    
    // Ads
    String agents;
    
    // Assets
    String assets;
    
    // Assets owners
    String assets_owners;
    
    // Blocks
    String blocks;
    
    // Domains
    String domains;
    
    // Escrowed
    String escrowed;
    
    // Feeds
    String feeds;
    
    // Bets
    String feeds_bets;
    
    // Bets pos
    String feeds_bets_pos;
    
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
    
    // Block confirmation minimum baalance
    public double block_conf_min_balance=1;
    
    // Network time
    public long net_time;
    
    // Process blocks ?
    public boolean consensus=true;
    
    // Timer
    Timer timer;
    
    
    public CNetStat()
    {
        
    }
    
    public void run()
    {
        try
        {
           // Add tables
           this.tables.add("adr");
           this.tables.add("adr_options");
           this.tables.add("ads");
           this.tables.add("assets");
           this.tables.add("agents");
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
           
           // Confirm min balance
           this.block_conf_min_balance=rs.getDouble("block_confirm_min_balance");
           
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
      
           // Addresses
           this.table_adr=new CAdrTable();
           
           // Ads
           this.table_ads=new CAdsTable();
           
           // Domains
           this.table_domains=new CDomainsTable();
           
           // Profiles
           this.table_profiles=new CProfilesTable();
           
           // Tweets
           this.table_tweets=new CTweetsTable();
           
           // Network time
           this.net_time=UTILS.BASIC.tstamp();
  
           
             // Timer
            timer = new Timer();
            RemindTask task=new RemindTask();
            timer.schedule(task, 0, 1000);
        }
        catch (Exception ex)
        {
            
        }
    }
    
        
    public void refreshTables(long block) throws Exception
    {
        // Init
        UTILS.DB.executeUpdate("SET group_concat_max_len = 1000000000000000");
        
        // Adr
        this.table_adr.refresh(block);
        
        // Ads
        this.table_ads.refresh(block);
        
        // Domains
        this.table_domains.refresh(block);
        
        // Profiles
        this.table_profiles.refresh(block);
        
        // Tweets
        this.table_tweets.refresh(block);
    }
    
    
    public void setHash(String tab, String hash) throws Exception
    {
        UTILS.DB.executeUpdate("UPDATE net_stat SET "+tab+"='"+hash+"'");
        
        switch (tab)
        {
            case "adr" : this.adr=hash; break;
            case "ads" : this.ads=hash; break;
            case "domains" : this.domains=hash; break;
        }
    }
    
    public String getHash(String tab) throws Exception
    {
        if (tab=="adr") return this.adr;
        if (tab=="ads") return this.ads;
        if (tab=="domains") return this.domains;
        
        return "";
    }
    
    public void tick()
    {
        this.net_time++;
    }
    
    public void setTime(long new_time)
    {
        this.net_time=new_time;
        System.out.println("New net time : "+new_time);
    }
    
    
     class RemindTask extends TimerTask 
     {  
       @Override
       public void run()
       {  
           try
           {
               tick();
           }
           catch (Exception ex)
           {
               System.out.println(ex.getMessage());
           }
       }
     }
     
     public void setDifficulty(long dif)
     {
        // Minimum dif
        if (dif>20000000000000L) dif=20000000000000L;
       
        // Set
        this.net_dif=dif;
         
        // Debug
        System.out.println("New difficulty : "+dif);
     }
}
