package wallet.kernel.net_stat;

import java.sql.ResultSet;
import java.sql.Statement;
import wallet.kernel.UTILS;

public class CAdrTable extends CTable
{
    public CAdrTable()
    {
        // Constructor
        super("adr");
    }
    
    public void fromDB() throws Exception
    {
        System.out.println("Flushing...");
        
       int a=0;
       
       // Statement
       Statement s=UTILS.DB.getStatement();
       
       // Load data
       ResultSet rs=s.executeQuery("SELECT * FROM adr");
       
       // Parse
       if (UTILS.DB.hasData(rs))
       {
           while (rs.next())
           {
               // Pos
               a++;
               
               // Start tag
               if (a==1) 
                   this.json=this.json+"{";
               else
                   this.json=this.json+", {";
               
               // Adr
               this.addRow("adr", rs.getString("adr"));
               
               // Balance
               this.addRow("balance", UTILS.FORMAT_8.format(rs.getDouble("balance")));
               
               // Total received
               this.addRow("total_received", UTILS.FORMAT_8.format(rs.getDouble("total_received")));
               
               // Total spent
               this.addRow("total_spent", UTILS.FORMAT_8.format(rs.getDouble("total_spent")));
               
               // Trans no
               this.addRow("trans_no", rs.getLong("trans_no"));
               
               // Created
               this.addRow("created", rs.getLong("created"));
               
               // Last interest
               this.addRow("last_interest", rs.getLong("last_interest"));
               
               // Tweets
               this.addRow("tweets", rs.getLong("tweets"));
               
               // Following
               this.addRow("following", rs.getLong("following"));
               
               // Followers
               this.addRow("followers", rs.getLong("followers"));
               
               // Rowhash
               this.addRow("rowhash", rs.getString("rowhash"));
               
               // Block
               this.addRow("block", rs.getLong("block"));
               
               // End tag
               this.json=this.json+"}";
           }
       }
       
       // Format
       this.json=this.json.replace(", }", "}");
               
       // Close json
       this.json=this.json+"]}";
       
       // Close
       rs.close();
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
}
