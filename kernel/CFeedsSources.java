// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.kernel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Timer;
import java.util.TimerTask;
import wallet.network.CPeers;

public class CFeedsSources 
{
    // Timer
    Timer timer;
    
    public CFeedsSources() throws Exception
    {
         // Reset 
        UTILS.DB.executeUpdate("UPDATE feeds_sources SET next_run='"+UTILS.BASIC.tstamp()+"'");
        
         // Timer
         timer = new Timer();
         RemindTask task=new RemindTask();
         timer.schedule(task, 0, 10000); 
    }
    
    class RemindTask extends TimerTask 
    {  
       @Override
       public void run() 
       {  
         try
         {
             // Load data
             ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                                + "FROM feeds_sources "
                                               + "WHERE next_run<"+UTILS.BASIC.tstamp());
             
             while (rs.next())
             {
                 // Update 
                 UTILS.DB.executeUpdate("UPDATE feeds_sources "
                                         + "SET next_run="+UTILS.BASIC.tstamp()+"+ping_interval "
                                       + "WHERE ID='"+rs.getLong("ID")+"'");
                 
                 // Load
                 CFeedSource feed=new CFeedSource(rs.getString("adr"), 
                                                  UTILS.BASIC.base64_decode(rs.getString("url")), 
                                                  rs.getString("feed_symbol"));
                 feed.start();
             }
          
         }
         catch (Exception e)
         {
            
         }
       }
   }
}
