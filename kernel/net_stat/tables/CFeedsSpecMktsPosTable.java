package wallet.kernel.net_stat.tables;

import java.sql.ResultSet;
import org.json.JSONArray;
import org.json.JSONObject;
import wallet.kernel.UTILS;
import wallet.kernel.net_stat.CTable;

public class CFeedsSpecMktsPosTable extends CTable
{
    public CFeedsSpecMktsPosTable()
    {
        super("feeds_spec_mkts_pos");
    }
    
    public void expired(long block)
    {
 
    }
    
    public void create() throws Exception
    {
         UTILS.DB.executeUpdate("CREATE TABLE feeds_spec_mkts_pos(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                                    +"mktID BIGINT DEFAULT 0, "
                                                                    +"posID BIGINT DEFAULT 0, "
                                                                    +"adr VARCHAR(250) DEFAULT '', "
                                                                    +"open DOUBLE(20, 8) DEFAULT 0, "
                                                                    +"sl DOUBLE(20, 8) DEFAULT 0, "
                                                                    +"tp DOUBLE(20, 8) DEFAULT 0, "
                                                                    +"leverage BIGINT DEFAULT 0, "
                                                                    +"qty DOUBLE(20, 4) DEFAULT 0, "
                                                                    +"status VARCHAR(20) DEFAULT '', "
                                                                    +"open_line VARCHAR(20) DEFAULT '', "
                                                                    +"tip VARCHAR(10) DEFAULT '', "
                                                                    +"pl DOUBLE(20,8) DEFAULT 0, "
                                                                    +"spread DOUBLE(20,8) DEFAULT 0, "
                                                                    +"margin DOUBLE(20,8) DEFAULT 0, "
                                                                    +"close_reason VARCHAR(20) DEFAULT '', "
                                                                    +"closed_pl DOUBLE(20,8) DEFAULT 0, "
                                                                    +"closed_margin DOUBLE(20,8) DEFAULT 0, "
                                                                    +"block_start BIGINT DEFAULT 0, "
                                                                    +"block_end BIGINT DEFAULT 0, "
                                                                    +"expire BIGINT DEFAULT 0)");
             
             UTILS.DB.executeUpdate("CREATE INDEX feeds_spec_mkts_pos_mktID ON feeds_spec_mkts_pos(mktID)");
             UTILS.DB.executeUpdate("CREATE UNIQUE INDEX feeds_spec_mkts_pos_posID ON feeds_spec_mkts_pos(posID)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_spec_mkts_pos_adr ON feeds_spec_mkts_pos(adr)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_spec_mkts_pos_block_start ON feeds_spec_mkts_pos(block_start)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_spec_mkts_pos_block_end ON feeds_spec_mkts_pos(block_end)");
    }
   
     public void loadCheckpoint(String hash) throws Exception
     {
        // Drop table
        UTILS.DB.executeUpdate("DROP TABLE feeds_spec_mkts_pos");
        
        // Create table
        this.create();
        
        // From file
        this.fromFile(hash);
    }
     
      
}
