package wallet.network.packets.sync;

import java.io.File;
import org.apache.commons.io.FileUtils;
import wallet.kernel.UTILS;
import wallet.kernel.net_stat.CTable;
import wallet.kernel.net_stat.tables.CAdrTable;
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
    byte[] data;
    
    public CDeliverTablePacket(String table, String checkpoint) throws Exception
    {
        // Constructor
        super ("ID_DELIVER_TABLE_PACKET");
        
        // Table
        this.table=table;
        
        // Data
        File file = new File(UTILS.WRITEDIR+"checkpoints/"+checkpoint+"/"+table+".table");
        this.data = FileUtils.readFileToByteArray(file);
        
        // Hash
        this.hash=UTILS.BASIC.hash(this.hash()+UTILS.BASIC.hash(this.data));
    }
    
    public void process(CPeer sender) throws Exception
    {
        // Table
        CTable tab=null;
        
        // Load data
        switch (this.table)
        {
            // Address
            case "adr" : tab=new CAdrTable(); break;
            
            // Tweets
            case "tweets" : tab=new CTweetsTable(); break;
        }
        
        // Load table
        tab.fromJSON(new String(UTILS.BASIC.decompress(this.data)), UTILS.SYNC.getTableCRC(table));
        
        // Write to DB
        tab.toDB();
    }
}
