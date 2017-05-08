package wallet.kernel.net_stat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import wallet.kernel.UTILS;

public class CTable 
{
    // Name
    public String name;
    
    public CTable(String name)
    {
       // Name
       this.name=name;
    }
    
    public void refresh(long block, String hash) throws Exception
    {
        // Save ?
        if ((block+1)%UTILS.SETTINGS.chk_blocks==0) 
        {
            // Debug
            System.out.println("Refreshing "+this.name+"(block : "+hash+")");
        
            // Flush
            this.flush(hash);
        }
    }
    
    
    public void flush(String block_hash) throws Exception
    {
           // Load file ?
           UTILS.DB.executeUpdate("SELECT * FROM "+this.name+" INTO OUTFILE '"+UTILS.DB.fileLoc+"chk_"+this.name+"_"+block_hash+".table'");
        
           // Checkpoint
           this.checkpoint(block_hash);
    }
   
    public void checkpoint(String block_hash) throws Exception
    {
        // Load data
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM checkpoints "
                                          + "WHERE hash='"+block_hash+"'");
        
        // Hash data
        if (UTILS.DB.hasData(rs))
            return;
        else
            UTILS.DB.executeUpdate("INSERT INTO checkpoints(block, hash) "
                                      + "VALUES('"+UTILS.NETWORK.CONSENSUS.block_number+"', '"+block_hash+"')");
    }
    
   
    
   public void fromFile(String hash) throws Exception
   {
        // Write to file
        UTILS.DB.executeUpdate("LOAD data INFILE '"+UTILS.DB.fileLoc+"chk_"+this.name+"_"+hash+".table' INTO TABLE "+this.name);
   }
}
