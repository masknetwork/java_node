// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.kernel.net_stat;
import java.math.BigInteger;
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
import wallet.kernel.net_stat.tables.*;

public class CNetStat 
{
    // Tables
    public ArrayList<String> tables=new ArrayList<String>();
    
    // Addresses
    public CAdrTable table_adr;
    
    // Ads
    public CAdsTable table_ads;
    
    // Assets
    public CAssetsTable table_assets;
    
    // Assets owners
    public CAssetsOwnersTable table_assets_owners;
    
    // Domains
    public CDomainsTable table_domains;
    
    // Delegates votes
    public CDelVotesTable table_del_votes;
    
    // Escrowed
    public CEscrowedTable table_escrowed;
    
    // Profiles
    public CProfilesTable table_profiles;
    
    // Tweets
    public CTweetsTable table_tweets;
    
    // Tweets comments
    public CCommentsTable table_comments;
    
    // Tweets likes
    public CVotesTable table_votes;
    
    // Tweets follow
    public CTweetsFollowTable table_tweets_follow;
    
    // Agents
    public CAgentsTable table_agents;
    
    // Storage
    public CStorageTable table_storage;
    
    // Last block
    public long last_block;
    
    // Last hash
    public String last_block_hash;
    
    // Last tstamp
    public long last_block_tstamp;
    
    // Difficulty
    public BigInteger net_dif;
    
    // SQL log
    String sql_log_status;
    
    // Addresses
    String adr;
    
   // Ads
    String ads;
    
    // Agents
    String agents;
    
    // Assets
    String assets;
    
    // Assets owners
    String assets_owners;
    
    // Domains
    String domains;
    
    // Delegates votes
    String del_votes;
    
    // Escrowed
    String escrowed;
    
    // Profiles
    String profiles;
    
    // Tweets
    String tweets;
    
    // Tweets Comments
    String comments;
    
    // Tweets Follow
    String tweets_follow;
    
    // Tweets Links
    String votes;
    
    // Storage
    String storage;
    
    // Block confirmation minimum baalance
    public double block_conf_min_balance=1;
    
    // Network time
    public long net_time;
    
    // Process blocks ?
    public boolean consensus=true;
    
    // Timer
    Timer timer;
    
    // Last hash
    public String actual_block_hash="";
    
    // Last tstamp
    public long actual_block_no=0;
    
    // Delegate
    public String delegate="";
    
    public CNetStat() throws Exception
    {
        // Online
        UTILS.DB.executeUpdate("UPDATE web_sys_data set uptime='"+UTILS.BASIC.tstamp()+"'");
        
        // Load data
        ResultSet rs=UTILS.DB.executeQuery("SELECT * FROM net_stat");
           
        // Next
        rs.next();
           
        // Last block
        this.last_block=rs.getLong("last_block");
           
        // Last hash
        this.last_block_hash=rs.getString("last_block_hash");
           
        // Difficulty
        this.net_dif=new BigInteger(rs.getString("net_dif"), 16);
           
        // SQL log status
        this.sql_log_status=rs.getString("sql_log_status");
           
        // Confirm min balance
        this.block_conf_min_balance=rs.getDouble("block_confirm_min_balance");
           
        // Addresses
        this.adr=rs.getString("adr");
    
        // Ads
        this.ads=rs.getString("ads");
    
        // Agents
        this.agents=rs.getString("agents");
        
        // Assets 
        this.assets=rs.getString("assets");
        
        // Assets owners
        this.assets_owners=rs.getString("assets_owners");
        
        // Domains
        this.domains=rs.getString("domains");
        
        // Delegates votes
        this.del_votes=rs.getString("del_votes");
    
        // Escrowed
        this.escrowed=rs.getString("escrowed");
    
        // Profiles
        this.profiles=rs.getString("profiles");
    
        // Tweets
        this.tweets=rs.getString("tweets");
    
        // Tweets Comments
        this.comments=rs.getString("comments");
    
        // Tweets Follow
        this.tweets_follow=rs.getString("tweets_follow");
    
        // Tweets Links
        this.votes=rs.getString("votes");
           
        // Delegates
        this.delegate=rs.getString("delegate");
        
        // Delegates
        this.storage=rs.getString("storage");
      
        // Addresses
        this.table_adr=new CAdrTable();
           
        // Ads
        this.table_ads=new CAdsTable();
           
        // Agents
        this.table_agents=new CAgentsTable();
           
        // Assets
        this.table_assets=new CAssetsTable();
           
        // Assets owners
        this.table_assets_owners=new CAssetsOwnersTable();
           
        // Domains
        this.table_domains=new CDomainsTable();
           
        // Del votes
        this.table_del_votes=new CDelVotesTable();
           
        // Escrowed
        this.table_escrowed=new CEscrowedTable();
           
        // Profiles
        this.table_profiles=new CProfilesTable();
           
        // Tweets
        this.table_tweets=new CTweetsTable();
           
        // Tweets comments
        this.table_comments=new CCommentsTable();
           
        // Tweets follows
        this.table_tweets_follow=new CTweetsFollowTable();
           
        // Tweets likes
        this.table_votes=new CVotesTable();
           
        // Feeds bets pos
        this.table_storage=new CStorageTable();
           
        // Network time
        this.net_time=UTILS.BASIC.tstamp();
    }
    
        
    public void refreshTables(long block) throws Exception
    {
        // Init
        UTILS.DB.executeUpdate("SET group_concat_max_len = 1000000000000000");
        
        // Adr
        this.table_adr.refresh(block);
        
        // Ads
        this.table_ads.refresh(block);
        
        // Agents
        this.table_agents.refresh(block);
        
        // Assets
        this.table_assets.refresh(block);
        
        // Assets owners
        this.table_assets_owners.refresh(block);
         
        // Domains
        this.table_domains.refresh(block);
        
        // Escrowed
        this.table_escrowed.refresh(block);
        
        // Profiles
        this.table_profiles.refresh(block);
        
        // Tweets
        this.table_tweets.refresh(block);
        
        // Tweets comments
        this.table_comments.refresh(block);
        
        // Tweets likes
        this.table_votes.refresh(block);
        
        // Tweets follow
        this.table_tweets_follow.refresh(block);
        
        // Delegates votes
        this.table_del_votes.refresh(block);
        
        // Storage
        this.table_storage.refresh(block);
    }
    
    
    public void setHash(String tab, String hash) throws Exception
    {
        UTILS.DB.executeUpdate("UPDATE net_stat SET "+tab+"='"+hash+"'");
        
        switch (tab)
        {
            // Addresses
            case "adr" : this.adr=hash; break;
            
            // Ads
            case "ads" : this.ads=hash; break;
            
            // Agents
            case "agents" : this.agents=hash; break;
            
            // Assets
            case "assets" : this.assets=hash; break;
            
            // Assets owners
            case "assets_owners" : this.assets_owners=hash; break;
            
            // Domains
            case "domains" : this.domains=hash; break;
            
            // Delegates votes
            case "del_votes" : this.del_votes=hash; break;
            
            // Escrowed
            case "escrowed" : this.escrowed=hash; break;
            
            // Profiles
            case "profiles" : this.profiles=hash; break;
            
            // Tweets
            case "tweets" : this.tweets=hash; break;
            
            // Tweets comments
            case "comments" : this.comments=hash; break;
            
            // Tweets likes
            case "votes" : this.votes=hash; break;
            
            // Tweets follow
            case "tweets_follow" : this.tweets_follow=hash; break;
            
            // Storage
            case "storage" : this.storage=hash; break;
          
        }
    }
    
    public String getHash(String tab) throws Exception
    {
        // Addresses
        if (tab.equals("adr")) return this.adr;
        
        // Ads
        if (tab.equals("ads")) return this.ads;
        
        // Agents
        if (tab.equals("agents")) return this.agents;
        
        // Assets
        if (tab.equals("assets")) return this.assets;
        
        // Assets owners
        if (tab.equals("assets_owners")) return this.assets_owners;
        
        // Domains
        if (tab.equals("domains")) return this.domains;
        
        // Delegates votes
        if (tab.equals("del_votes")) return this.del_votes;
        
        // Escrowed
        if (tab.equals("escrowed")) return this.escrowed;
        
        // Profiles
        if (tab.equals("profiles")) return this.profiles;
        
        // Tweets
        if (tab.equals("tweets")) return this.tweets;
        
        // Tweets comments
        if (tab.equals("comments")) return this.comments;
        
        // Tweets likes
        if (tab.equals("votes")) return this.votes;
        
        // Tweets follow
        if (tab.equals("tweets_follow")) return this.tweets_follow;
        
        // Storage
        if (tab.equals("storage")) return this.storage;
        
        
        return "";
    }
    
     
     public void setDifficulty(BigInteger dif)
     {
         // Set
        this.net_dif=dif;
         
        // Debug
        //System.out.println("New difficulty : "+UTILS.BASIC.formatDif(dif.toString(16)));
     }
     
     
}
