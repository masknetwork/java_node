// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.kernel.net_stat;

import java.math.BigInteger;
import wallet.kernel.net_stat.tables.CProfilesTable;
import wallet.kernel.net_stat.tables.CAdsTable;
import wallet.kernel.net_stat.tables.CAdrTable;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Timer;
import wallet.kernel.UTILS;
import wallet.kernel.net_stat.tables.*;

public class CNetStat 
{
    // Tables
    public ArrayList<String> tables=new ArrayList<String>();
    
    // Addresses
    public CAdrTable table_adr;
    
    // Addresses
    public CAdrAttrTable table_adr_attr;
    
    // Ads
    public CAdsTable table_ads;
    
    // Assets
    public CAssetsTable table_assets;
    
    // Assets markets
    public CAssetsMktsTable table_assets_mkts;
    
    // Assets
    public CAssetsMktsPosTable table_assets_mkts_pos;
    
    // Assets owners
    public CAssetsOwnersTable table_assets_owners;
    
    // Domains
    public CDomainsTable table_domains;
    
    // Delegates
    public CDelegatesTable table_delegates;
    
    // Delegates log
    public CDelegatesLogTable table_delegates_log;
    
    // Delegates votes
    public CDelVotesTable table_del_votes;
    
    // Escrowed
    public CEscrowedTable table_escrowed;
    
    // Profiles
    public CProfilesTable table_profiles;
    
    // Comments
    public CCommentsTable table_com;
    
    // Feeds
    public CFeedsTable table_feeds;
    
    // Feeds branches
    public CFeedsBranchesTable table_feeds_branches;
    
    // Feeds bets
    public CFeedsBetsTable table_feeds_bets;
    
    // Feeds bets pos
    public CFeedsBetsPosTable table_feeds_bets_pos;
    
    // Feeds spec mkts
    public CFeedsSpecMktsTable table_feeds_spec_mkts;
    
    // Feeds spec mkts pos
    public CFeedsSpecMktsPosTable table_feeds_spec_mkts_pos;
    
    // Tweets
    public CTweetsTable table_tweets;
    
    // Tweets follow
    public CTweetsFollowTable table_tweets_follow;
    
    // Votes
    public CVotesTable table_votes;
    
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
    
    // No checkpoints
    public boolean reorg=false;
    
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
        this.table_adr=new CAdrTable();
        
        // Adr attr
        this.table_adr_attr=new CAdrAttrTable();
           
        // Ads
        this.table_ads=new CAdsTable();
           
        // Assets
        this.table_assets=new CAssetsTable();
           
        // Assets owners
        this.table_assets_owners=new CAssetsOwnersTable();
        
        // Assets markets
        this.table_assets_mkts=new CAssetsMktsTable();
        
        // Assets markets_pos
        this.table_assets_mkts_pos=new CAssetsMktsPosTable();
           
        // Domains
        this.table_domains=new CDomainsTable();
           
        // Del votes
        this.table_del_votes=new CDelVotesTable();
        
        // Delegates
        this.table_delegates=new CDelegatesTable();
        
        // Delegates Log
        this.table_delegates_log=new CDelegatesLogTable();
           
        // Escrowed
        this.table_escrowed=new CEscrowedTable();
           
        // Profiles
        this.table_profiles=new CProfilesTable();
        
        // Comments
        this.table_com=new CCommentsTable();
        
        // Feeds
        this.table_feeds=new CFeedsTable();
        
        // Feeds branches
        this.table_feeds_branches=new CFeedsBranchesTable();
        
        // Feeds bets
        this.table_feeds_bets=new CFeedsBetsTable();
        
        // Feeds bets pos
        this.table_feeds_bets_pos=new CFeedsBetsPosTable();
        
        // Feeds spec mkts
        this.table_feeds_spec_mkts=new CFeedsSpecMktsTable();
        
        // Feeds spec mkts pos
        this.table_feeds_spec_mkts_pos=new CFeedsSpecMktsPosTable();
        
        // Tweets
        this.table_tweets=new CTweetsTable();
        
        // Tweets follow
        this.table_tweets_follow=new CTweetsFollowTable();
        
         // Votes
        this.table_votes=new CVotesTable();
           
        // Network time
        this.net_time=UTILS.BASIC.tstamp();
    }
    
        
    public void refreshTables(long block, String hash) throws Exception
    {
        // Adr
        this.table_adr.refresh(block, hash);
        
        // Adr attr
        this.table_adr_attr.refresh(block, hash);
        
        // Ads
        this.table_ads.refresh(block, hash);
        
        // Assets
        this.table_assets.refresh(block, hash);
        
        // Assets owners
        this.table_assets_owners.refresh(block, hash);
        
        // Assets markets
        this.table_assets_mkts.refresh(block, hash);
        
        // Assets markets pos
        this.table_assets_mkts_pos.refresh(block, hash);
         
        // Domains
        this.table_domains.refresh(block, hash);
        
        // Escrowed
        this.table_escrowed.refresh(block, hash);
        
        // Profiles
        this.table_profiles.refresh(block, hash);
        
        // Delegates votes
        this.table_del_votes.refresh(block, hash);
        
        // Delegates 
        this.table_delegates.refresh(block, hash);
        
        // Delegates Log
        this.table_delegates_log.refresh(block, hash);
        
        // Comments
        this.table_com.refresh(block, hash);
        
        // Feeds
        this.table_feeds.refresh(block, hash);
        
        // Feeds branches
        this.table_feeds_branches.refresh(block, hash);
        
        // Feeds bets
        this.table_feeds_bets.refresh(block, hash);
        
        // Feeds bets pos
        this.table_feeds_bets_pos.refresh(block, hash);
        
        // Feeds spec mkts
        this.table_feeds_spec_mkts.refresh(block, hash);
        
        // Feeds spec mkts pos
        this.table_feeds_spec_mkts_pos.refresh(block, hash);
        
        // Tweets
        this.table_tweets.refresh(block, hash);
        
        // Tweets follow
        this.table_tweets_follow.refresh(block, hash);
        
        // Votes
        this.table_votes.refresh(block, hash);
    }
     
     public void setDifficulty(BigInteger dif)
     {
         // Set
        this.net_dif=dif;
         
        // Debug
        System.out.println("New difficulty : "+UTILS.BASIC.formatDif(dif.toString(16)));
     }
     
     
}
