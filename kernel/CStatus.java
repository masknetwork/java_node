// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.kernel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Timer;
import java.util.TimerTask;
import wallet.network.CPeer;


public class CStatus extends CTicker
{
	// Status
	public String engine_status;
	
        // Version
        public String version;
        
        // IP
        public String IP;
        
        // Country
        public String country;
        
        // Timer
        Timer timer;
        
        // Task
        RemindTask task;
		
	public CStatus() throws Exception
	{
	     // Alive
             UTILS.DB.executeUpdate("UPDATE web_sys_data "
                                     + "SET uptime='"+UTILS.BASIC.tstamp()+"'");
                    
             Statement s=UTILS.DB.getStatement();
             ResultSet rs=s.executeQuery("SELECT * FROM  status");
	     rs.next();
		    
	     // Engine status
	     if (UTILS.SETTINGS.sync) 
                this.setEngineStatus("ID_SYNC");
             else
                this.setEngineStatus("ID_ONLINE");
		   
		    // Timer
                    timer = new Timer();
                    task=new RemindTask();
                    task.parent=this;
                    timer.schedule(task, 0, 1000); 
                        
                    if (s!=null) rs.close(); s.close();
	}
    
	
        public void setEngineStatus(String status) throws Exception
        {
            // Output new status
            System.out.println("New system status is "+status);
            
            UTILS.DB.executeUpdate("UPDATE status "
                                    + "SET engine_status='"+status+"'");
            this.engine_status=status;
        }
	
        class RemindTask extends TimerTask
        {  
           public CStatus parent;
               
           @Override
           public void run() 
           {  
               try
               {
                   parent.alive();
               }
               catch (Exception ex) 
       	      {  
       		
              }
           }
        }
        
        public void alive() throws Exception
        {
           // Get runtime
           Runtime runtime = Runtime.getRuntime();
           
           // Low memory ?
           if (runtime.freeMemory()<1) 
           {
               UTILS.LOG.log("ID_ERROR", "Exit virtual machine (run out of memory)", "CStatus.java", 146);
               System.exit(0);
           }
           
           // Update
           UTILS.DB.executeUpdate("UPDATE web_sys_data "
                                   + "SET last_ping='"+UTILS.BASIC.tstamp()+"', "
                                       + "max_memory='"+runtime.maxMemory()+"', "
                                       + "version='0.9.0', "
                                       + "free_memory='"+runtime.freeMemory()+"', "
                                       + "total_memory='"+runtime.totalMemory()+"', "
                                       + "procs='"+runtime.availableProcessors()+"', "
                                       + "threads_no='"+Thread.getAllStackTraces().size()+"'");
        }
        
	
}
