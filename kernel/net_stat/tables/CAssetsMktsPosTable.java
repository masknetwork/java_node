package wallet.kernel.net_stat.tables;

import java.sql.ResultSet;
import java.sql.Statement;
import org.json.JSONArray;
import org.json.JSONObject;
import wallet.kernel.UTILS;
import wallet.kernel.net_stat.CTable;

public class CAssetsMktsPosTable extends CTable
{
    public CAssetsMktsPosTable()
    {
        super("assets_mkts_pos");
    }
    
    public void expired(long block) throws Exception
    {
        // Result
        ResultSet rs;
        
        rs=UTILS.DB.executeQuery("SELECT am.asset, "
                                       + "am.cur, "
                                       + "amp.* "
                                 + "FROM assets_mkts_pos AS amp "
                                 + "JOIN assets_mkts AS am ON amp.mktID=am.mktID "
                                + "WHERE amp.expire<="+block);
       
        
        while (rs.next())
        {
            // Market asset
            String asset=rs.getString("asset");
        
            // Market currency
            String cur=rs.getString("cur");
        
            // Order type
            String type=rs.getString("tip");
        
            // Order qty
            double qty=rs.getDouble("qty");
        
            // Order price
            double price=rs.getDouble("price");
        
            // Order owner
            String owner=rs.getString("adr");
        
            // Refund
            if (type.equals("ID_BUY"))
            {
               if (cur.equals("MSK"))
                   UTILS.DB.executeUpdate("UPDATE adr "
                                           + "SET balance=balance+"+UTILS.FORMAT_8.format(qty*price)+" "
                                         + "WHERE adr='"+owner+"'");
               else
                   UTILS.DB.executeUpdate("UPDATE assets_owners "
                                           + "SET qty=qty+"+UTILS.FORMAT_8.format(qty*price)+" "
                                         + "WHERE owner='"+owner+"' "
                                           + "AND symbol='"+asset+"'");
            }
            else
            {
                UTILS.DB.executeUpdate("UPDATE assets_owners "
                                       + "SET qty=qty+"+UTILS.FORMAT_8.format(qty)+" "
                                     + "WHERE owner='"+owner+"' "
                                       + "AND symbol='"+asset+"'");
           }
        }
        
        
        // Removes
        UTILS.DB.executeUpdate("DELETE FROM assets_mkts_pos "
                                   + "WHERE expire<="+block);
    }
    
    // Create
    public void create() throws Exception
    {
        UTILS.DB.executeUpdate("CREATE TABLE assets_mkts_pos(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
	 	 				              + "adr VARCHAR(250) DEFAULT '', "
	 	 				              + "mktID BIGINT DEFAULT 0, "
	 	 				              + "tip VARCHAR(10) DEFAULT '', "
	 	 				              + "qty DOUBLE(20, 8) DEFAULT 0, "
	 	 				              + "price DOUBLE(20, 8) DEFAULT 0, "
	 	 				              + "block BIGINT DEFAULT 0, "
                                                              + "orderID BIGINT DEFAULT 0, "
	 	 				              + "expire BIGINT DEFAULT 0)");
	 	 	   
	UTILS.DB.executeUpdate("CREATE INDEX assets_mkts_pos_adr ON assets_mkts_pos(adr)");
        UTILS.DB.executeUpdate("CREATE INDEX assets_mkts_pos_block ON assets_mkts_pos(block)");  
        UTILS.DB.executeUpdate("CREATE UNIQUE INDEX assets_mkts_pos_orderID ON assets_mkts_pos(orderID)");  
    }
   
    
    public void loadCheckpoint(String hash) throws Exception
    {
        // Drop table
        UTILS.DB.executeUpdate("DROP TABLE assets_mkts_pos");
        
        // Create table
        this.create();
        
        // From file
        this.fromFile(hash);
    }
    
}

