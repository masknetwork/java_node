package wallet.network.packets.sync;

import java.io.File;
import org.apache.commons.io.FileUtils;
import wallet.kernel.UTILS;
import wallet.kernel.net_stat.CTable;
import wallet.kernel.net_stat.tables.*;
import wallet.kernel.net_stat.tables.CTweetsTable;
import wallet.network.CPeer;
import wallet.network.CResult;
import wallet.network.packets.CPacket;
import wallet.network.packets.blocks.CBlockPayload;

public class CDeliverTablePacket extends CPacket
{
    // Table
    String table;
    
    // Checkpoint
    String checkpoint;
    
    // Data
    byte[] data=new byte[1];
    
    // Serial
   private static final long serialVersionUID = 100L;
    
    public CDeliverTablePacket(String table, String checkpoint) throws Exception
    {
        // Constructor
        super ("ID_DELIVER_TABLE_PACKET");
        
        // Table
        this.table=table;
        
        // Data
        File file = new File(UTILS.WRITEDIR+"checkpoints/"+checkpoint+"/"+table+"+table");
        if (file.exists()) 
            this.data = FileUtils.readFileToByteArray(file);
        
        // Hash
        this.hash=UTILS.BASIC.hash(this.hash()+UTILS.BASIC.hash(this.data));
    }
    
    public void process(CPeer sender) throws Exception
    {
        // Table
        CTable tab=null;
        
        // Sync ?
        if (!UTILS.STATUS.engine_status.equals("ID_SYNC"))
            return;
        
        // Table name
        String tab_name="";
        
        // Load data
        switch (this.table)
        {
            // Address
            case "adr" : tab=new CAdrTable(); 
                         tab_name="adr"; 
                         break;
            
            // Ads
            case "ads" : tab=new CAdsTable(); 
                         tab_name="ads"; 
                         break;
            
            // Agents
            case "agents" : tab=new CAgentsTable(); 
                            tab_name="agents"; 
                            break;
            
            // Assets
            case "assets" : tab=new CAssetsTable(); 
                            tab_name="assets"; 
                            break;
            
            // Assets owners
            case "assets_owners" : tab=new CAssetsOwnersTable(); 
                                   tab_name="assets_owners"; 
                                   break;
            
            // Assets markets
            case "assets_mkts" : tab=new CAssetsMktsTable(); 
                                 tab_name="assets_mkts"; 
                                 break;
            
            // Assets markets pos
            case "assets_mkts_pos" : tab=new CAssetsMktsPosTable(); 
                                     tab_name="assets_mkts_pos"; 
                                     break;
            
            // Domains
            case "domains" : tab=new CDomainsTable(); 
                             tab_name="domains"; 
                             break;
            
            // Escrowed
            case "escrowed" : tab=new CEscrowedTable(); 
                              tab_name="escrowed"; 
                              break;
            
            // Profiles
            case "profiles" : tab=new CProfilesTable(); 
                              tab_name="profiles"; 
                              break;
            
            // Tweets
            case "tweets" : tab=new CTweetsTable(); 
                            tab_name="tweets"; 
                            break;
            
            // Tweets likes
            case "upvotes" : tab=new CVotesTable(); 
                                  tab_name="upvotes"; 
                                  break;
            
            // Tweets follow
            case "tweets_follow" : tab=new CTweetsFollowTable(); 
                                   tab_name="tweets_follow"; 
                                   break;
            
            // Tweets comments
            case "comments" : tab=new CCommentsTable(); 
                                     tab_name="comments"; 
                                     break;
        }
        
        // Has data
        if (this.data.length>10)
        {
            // Load table
            tab.fromJSON(new String(UTILS.BASIC.decompress(this.data)), UTILS.SYNC.getTableCRC(table));
        
            // Write to DB
            tab.toDB();
        }
        else
        {
            UTILS.SYNC.removeTable(tab_name);
        }
    }
}
