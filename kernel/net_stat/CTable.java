package wallet.kernel.net_stat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.sql.Statement;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import wallet.kernel.UTILS;

public class CTable 
{
    // Name
    public String name;
    
    // Hash
    public String hash;
    
    // Json
    public String json;
    
    public CTable(String name)
    {
       // Name
       this.name=name;
       
       // Json
       this.json="{\"table\" : \""+name+"\", \"rows\" : [";
    }
    
    public void refresh(long block) throws Exception
    {
        // Save ?
        if ((block+1)%UTILS.SETTINGS.chk_blocks==0) 
        {
            // Debug
            System.out.println("Refreshing "+this.name);
        
            // Load from DB
            this.fromDB();
            
            // Flush
            this.flush();
        }
    }
    
    public void fromDB() throws Exception
    {
        this.json="{\"table\" : \""+name+"\", \"rows\" : [";
    }
    
    public void toDB() throws Exception
    {
       
    }
    
    public void fromJSON(String data, String crc) throws Exception
    {
        // Object
        JSONObject obj = new JSONObject(data); 
        
        // Check table
        if (!obj.getString("table").equals(this.name))
            throw new Exception("Invalid table name");
        
        // Copy
        this.json=data;
    }
    
    public void flush() throws Exception
    {
        // Load file
        File fout = new File(UTILS.WRITEDIR+"checkpoints/"+UTILS.CONSENSUS.block_hash+"/"+this.name+".table");
        
        // Write
        FileUtils.writeByteArrayToFile(fout, UTILS.BASIC.compress(this.json.getBytes())); 
        
        // Checkpoint
        this.checkpoint();
        
        // Debug
        //System.out.println(this.json);
    }
    
    public void checkpoint() throws Exception
    {
        // Statement
        
        
        // Load data
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                    + "FROM checkpoints "
                                   + "WHERE hash='"+UTILS.CONSENSUS.block_hash+"'");
        
        // Hash data
        if (UTILS.DB.hasData(rs))
        {
            
            return;
        }
        else
        {
            UTILS.DB.executeUpdate("INSERT INTO checkpoints(block, hash) "
                                      + "VALUES('"+UTILS.CONSENSUS.block_number+"', '"+UTILS.CONSENSUS.block_hash+"')");
        }
        
        // Close
        
    }
    
    public void addRow(String col, String val)
    {
        this.json=this.json+"\""+col+"\" : \""+val+"\", ";
    }
    
    public void addRow(String col, long val)
    {
        this.json=this.json+"\""+col+"\" : \""+val+"\", ";
    }
    
    public void addRow(String col, double val)
    {
        this.json=this.json+"\""+col+"\" : \""+val+"\", ";
    }
    
    public void loadHash() throws Exception
    {
        // Statement
        
        
        // Result set
        ResultSet rs=UTILS.DB.executeQuery("SELECT * FROM net_stat");
        
        // Next
        rs.next();
        
        switch (this.name)
        {
            // Addresses
            case "adr" : UTILS.NET_STAT.adr=rs.getString("adr"); break;
            
            // Ads
            case "ads" : UTILS.NET_STAT.ads=rs.getString("ads"); break;
            
            // agents
            case "agents" : UTILS.NET_STAT.agents=rs.getString("agents"); break;
            
            // Assets
            case "assets" : UTILS.NET_STAT.assets=rs.getString("assets"); break;
            
            // Assets owners
            case "assets_owners" : UTILS.NET_STAT.assets_owners=rs.getString("assets_owners"); break;
            
            // Assets markets
            case "assets_mkts" : UTILS.NET_STAT.assets_mkts=rs.getString("assets_mkts"); break;
            
            // Assets markets positions
            case "assets_mkts_pos" : UTILS.NET_STAT.assets_mkts_pos=rs.getString("assets_mkts_pos"); break;
            
            // Domains
            case "domains" : UTILS.NET_STAT.domains=rs.getString("domains"); break;
            
            // Escrowed
            case "escrowed" : UTILS.NET_STAT.escrowed=rs.getString("escrowed"); break;
            
            // Profiles
            case "profiles" : UTILS.NET_STAT.profiles=rs.getString("profiles"); break;
            
            // Tweets
            case "tweets" : UTILS.NET_STAT.tweets=rs.getString("tweets"); break;
            
            // Tweets comments
            case "comments" : UTILS.NET_STAT.comments=rs.getString("comments"); break;
            
            // Tweets likes
            case "votes" : UTILS.NET_STAT.votes=rs.getString("votes"); break;
            
            // Tweets follow
            case "tweets_follow" : UTILS.NET_STAT.tweets_follow=rs.getString("tweets_follow"); break;
        }
        
        // Close
        
    }
    
   public void fromFile(String hash, String f, String crc) throws Exception
   {
       // Load file
        File file = new File(UTILS.WRITEDIR+"/checkpoints/"+hash+"/"+f);
        
        // Get data
        byte[] data = FileUtils.readFileToByteArray(file);
        
        // From JSON
        fromJSON(new String(UTILS.BASIC.decompress(data)), crc);
        
        // To db
        this.toDB();
   }
   
   public void toConsole()
   {
       System.out.println(this.json);
   }
}
