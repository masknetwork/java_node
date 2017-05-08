package wallet.kernel.net_stat.tables;

import java.sql.ResultSet;
import java.sql.Statement;
import org.json.JSONArray;
import org.json.JSONObject;
import wallet.kernel.UTILS;
import wallet.kernel.net_stat.CTable;

public class CDomainsTable extends CTable
{
    public CDomainsTable()
    {
        super("domains");
    }
    
    // Create
    public void create() throws Exception
    {
        UTILS.DB.executeUpdate("CREATE TABLE domains(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
	 	 			           + "adr VARCHAR(250) NOT NULL DEFAULT '', "
	 	 		                   + "domain VARCHAR(100) NOT NULL DEFAULT '', "
	 	 			           + "expire BIGINT NOT NULL DEFAULT 0, "
	 	 			           + "sale_price DOUBLE(10,4) NOT NULL DEFAULT 0, "
	 	 			           + "block BIGINT NOT NULL DEFAULT 0)");
	 	 	 	 	    
	UTILS.DB.executeUpdate("CREATE INDEX dom_adr ON domains(adr)");
	UTILS.DB.executeUpdate("CREATE INDEX dom_domain ON domains(domain)");
	UTILS.DB.executeUpdate("CREATE INDEX dom_block ON domains(block)");
    }
    
    public void expired(long block) throws Exception
    {
       UTILS.DB.executeUpdate("DELETE FROM domains "
                                     + "WHERE expire<="+block);
    }
    
    
     public void loadCheckpoint(String hash) throws Exception
     {
        // Drop table
        UTILS.DB.executeUpdate("DROP TABLE domains");
        
        // Create table
        this.create();
        
        // From file
        this.fromFile(hash);
    }
     
    
}

