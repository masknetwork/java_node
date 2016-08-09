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
    
    // Assets
    public CAssetsTable table_assets;
    
    // Assets owners
    public CAssetsOwnersTable table_assets_owners;
    
    // Assets Markets
    public CAssetsMktsTable table_assets_mkts;
    
    // Assets Mkts Pos
    public CAssetsMktsPosTable table_assets_mkts_pos;
    
    // Feeds
    public CFeedsTable table_feeds;
    
    // Feeds branches
    public CFeedsBranchesTable table_feeds_branches;
    
    // Feeds bets
    public CFeedsBetsTable table_feeds_bets;
    
    // Feeds bets pos
    public CFeedsBetsPosTable table_feeds_bets_pos;
    
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
    
    // Assets markets
    String assets_mkts;
    
    // Assets markets pos
    String assets_mkts_pos;
    
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
    
    // Feeds
    String feeds;
    
    // Feeds branches
    String feeds_branches;
    
    // Feeds bets
    String feeds_bets;
    
    // Feeds bets pos
    String feeds_bets_pos;
    
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
    
    // Last hash
    public String actual_block_hash="";
    
    // Last tstamp
    public long actual_block_no=0;
    
    public CNetStat() throws Exception
    {
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
    
        // Assets
        this.assets=rs.getString("assets");
    
        // Assets owners
        this.assets_owners=rs.getString("assets_owners");
           
        // Assets markets
        this.assets_mkts=rs.getString("assets_mkts");
           
        // Assets mkts pos
        this.assets_mkts_pos=rs.getString("assets_mkts_pos");
           
        // Agents
        this.agents=rs.getString("agents");
           
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
           
           // Feeds
           this.feeds=rs.getString("feeds");
           
           // Feeds branches
           this.feeds_branches=rs.getString("feeds_branches");
      
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
           
           // Assets markets
           this.table_assets_mkts=new CAssetsMktsTable();
           
           // Assets markets pos
           this.table_assets_mkts_pos=new CAssetsMktsPosTable();
           
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
           
           // Feeds table
           this.table_feeds=new CFeedsTable();
           
           // Feeds branches table
           this.table_feeds_branches=new CFeedsBranchesTable();
           
           // Feeds bets
           this.table_feeds_bets=new CFeedsBetsTable();
           
           // Feeds bets pos
           this.table_feeds_bets_pos=new CFeedsBetsPosTable();
           
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
        
        // Assets Owners
        this.table_assets_owners.refresh(block);
        
        // Assets Markets
        this.table_assets_mkts.refresh(block);
        
        // Assets Markets Pos
        this.table_assets_mkts_pos.refresh(block);
        
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
        
        // Feeds
        this.table_feeds.refresh(block);
        
        // Feeds branches
        this.table_feeds_branches.refresh(block);
        
        // Feeds bets
        //this.table_feeds_bets.refresh(block);
        
        // Feeds bets pos
        //this.table_feeds_bets_pos.refresh(block);
        
        // Delegates votes
        this.table_del_votes.refresh(block);
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
            
            // Assets markets
            case "assets_mkts" : this.assets_mkts=hash; break;
            
            // Assets markets positions
            case "assets_mkts_pos" : this.assets_mkts_pos=hash; break;
            
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
            
            // Feeds
            case "feeds" : this.feeds=hash; break;
            
            // Feeds branches
            case "feeds_branches" : this.feeds_branches=hash; break;
            
             // Feeds bets
            case "feeds_bets" : this.feeds_bets=hash; break;
            
             // Feeds bets pos
            case "feeds_bets_pos" : this.feeds_bets_pos=hash; break;
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
        
        // Assets markets
        if (tab.equals("assets_mkts")) return this.assets_mkts;
        
        // Assets markets pos
        if (tab.equals("assets_mkts_pos")) return this.assets_mkts_pos;
        
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
        
        // Feeds
        if (tab.equals("feeds")) return this.feeds;
        
        // Feeds branches
        if (tab.equals("feeds_branches")) return this.feeds_branches;
        
        // Feeds bets
        if (tab.equals("feeds_bets")) return this.feeds_bets;
        
        // Feeds bets pos
        if (tab.equals("feeds_bets_pos")) return this.feeds_bets_pos;
        
        return "";
    }
    
     
     public void setDifficulty(BigInteger dif)
     {
         // Set
        this.net_dif=dif;
         
        // Debug
        System.out.println("New difficulty : "+UTILS.BASIC.formatDif(dif.toString(16)));
     }
     
     public void expired(long block) throws Exception
     {
         // Ads
        this.table_ads.expired(block, adr);
        
        // Agents
        this.table_agents.expired(block, adr);
        
        // Assets
        this.table_assets.expired(block, adr);
        
        // Assets Owners
        this.table_assets_owners.removeByAdr(adr);
        
        // Assets Markets
        this.table_assets_mkts.removeByAdr(adr);
        
        // Assets Markets Pos
        this.table_assets_mkts_pos.expired(block, adr);
        
        // Domains
        this.table_domains.expired(block, adr);
        
        // Escrowed
        this.table_escrowed.expired(block);
        
        // Profiles
        this.table_profiles.expired(block, adr);
        
        // Tweets
        this.table_tweets.removeByAdr(adr);
        
        // Tweets comments
        this.table_comments.removeByAdr(adr);
        
        // Tweets likes
        this.table_votes.removeByAdr(adr);
        
        // Tweets follow
        this.table_tweets_follow.expired(block, adr);
        
        // Feeds 
        this.table_feeds.expired(block, adr);
        
        // Feeds branches
        this.table_feeds_branches.expired(block);
     }
     
}
