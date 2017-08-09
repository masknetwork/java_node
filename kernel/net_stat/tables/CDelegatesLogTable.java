package wallet.kernel.net_stat.tables;

import wallet.kernel.UTILS;
import wallet.kernel.net_stat.CTable;

public class CDelegatesLogTable extends CTable
{
    public CDelegatesLogTable()
    {
        // Constructor
        super("delegates_log");
    }
    
    // Create
    public void create() throws Exception
    {
       // Create table
       UTILS.DB.executeUpdate("CREATE TABLE delegates_log(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
				                       + "delegate VARCHAR(500) NOT NULL DEFAULT '', "
				                       + "power BIGINT NOT NULL DEFAULT 0, "
                                                       + "block BIGINT NOT NULL DEFAULT 0)");
		   
        UTILS.DB.executeUpdate("CREATE INDEX del_delegate ON delegates_log(delegate)");
        UTILS.DB.executeUpdate("CREATE INDEX del_block ON delegates_log(block)");
    }
    
    public void expired(long block) throws Exception
    {
        // Remove delegates 50 blocks old
        UTILS.DB.executeUpdate("DELETE FROM delegates_log "
                                  + "WHERE block<'"+(block-101)+"'");
    }
    
    
    public void loadCheckpoint(String hash) throws Exception
    {
        // Drop table
        UTILS.DB.executeUpdate("DROP TABLE delegates_log");
        
        // Create table
        this.create();
        
        // From file
        this.fromFile(hash);
    }
}
