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
        System.out.println(this.json);
    }
    
    public void checkpoint() throws Exception
    {
        // Statement
        Statement s=UTILS.DB.getStatement();
        
        // Load data
        ResultSet rs=s.executeQuery("SELECT * "
                                    + "FROM checkpoints "
                                   + "WHERE hash='"+UTILS.CONSENSUS.block_hash+"'");
        
        // Hash data
        if (UTILS.DB.hasData(rs))
        {
            s.close();
            return;
        }
        else
        {
            UTILS.DB.executeUpdate("INSERT INTO checkpoints(block, hash) "
                                      + "VALUES('"+UTILS.CONSENSUS.block_number+"', '"+UTILS.CONSENSUS.block_hash+"')");
        }
        
        // Close
        s.close();
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
        Statement s=UTILS.DB.getStatement();
        
        // Result set
        ResultSet rs=s.executeQuery("SELECT * FROM net_stat");
        
        // Next
        rs.next();
        
        switch (this.name)
        {
            case "adr" : UTILS.NET_STAT.adr=rs.getString("adr"); break;
            case "ads" : UTILS.NET_STAT.adr=rs.getString("ads"); break;
        }
        
        // Close
        s.close();
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
}
