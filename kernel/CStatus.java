package wallet.kernel;

import java.sql.ResultSet;

public class CStatus 
{
    // Engine status
    public String engine_status;
    
    // Version
    public long version=1;
    
    // New accounts reward address
    public String new_acc_reward_adr;
    
    // New accounts reward
    public double new_acc_reward;
    
    public CStatus() throws Exception
    {
        // Load 
        ResultSet rs=UTILS.DB.executeQuery("SELECT * FROM web_sys_data");
        
        // Next
        rs.next();
        
        // Status
        this.engine_status=rs.getString("engine_status");
        
        // New accounts reward address
        this.new_acc_reward_adr=rs.getString("new_acc_reward_adr");
    
        // New accounts reward
        this.new_acc_reward=rs.getDouble("new_acc_reward");
    }
    
    public void setEngineStatus(String status) throws Exception
    {
        // Print
        System.out.println("New engine status : "+status);
        
        // Status
        this.engine_status=status;
        
        // Db
        UTILS.DB.executeUpdate("UPDATE web_sys_data SET status='"+status+"'");
    }
}
