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
	
	// Last tables block
	public long last_tables_block;
	
	// Last blocks block
	public long last_blocks_block;
	
	// Version
	public String version="";
	
	// Backup email
	public String bkp_email;
	
	// Last backup
	long bkp_last_send;
	
	// Pending backup
        public String bkp_pending;
    
	// Update required
	public String update_required="";
	
	// Network status
	public String netstat="";
	
	// Difference
	long dif=0;
	
	// Last block hash
	public String last_block_hash; 
	
	// Last packet hash
	public String last_packet_hash;
        
        // IP
        public String IP;
        
        // Country
        public String country;
        
        // Timer
        Timer timer;
        
        // Task
        RemindTask task;
		
	public CStatus() 
	{
		refresh();
	}
    
	public void refresh()
	{
		try
		{
                     Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		    ResultSet rs=s.executeQuery("SELECT * FROM  status");
		    rs.next();
		    
		    // Engine status
		    this.engine_status=rs.getString("engine_status");
		    
		    // Last tables block
		    this.last_tables_block=rs.getLong("last_tables_block");
			
		    // Last blocks block
		    this.last_blocks_block=rs.getLong("last_blocks_block");
		    
		    // Backup email
			this.bkp_email=rs.getString("bkp_email");
			
			// Last backup
			this.bkp_last_send=rs.getLong("bkp_last_send");
			
			// Pending backup
		        this.bkp_pending=rs.getString("bkp_pending");
		    
			// Version
			this.version=rs.getString("version");
			
			// Update required
			this.update_required=rs.getString("update_required");
			
			// Last block hash
			this.last_block_hash=rs.getString("last_block_hash");
			
			// Last packet hash
			this.last_packet_hash=rs.getString("last_block_hash");
			
			// Timer
                        timer = new Timer();
                        task=new RemindTask();
                        task.parent=this;
                        timer.schedule(task, 0, 1000); 
                        
                        if (s!=null) s.close();
		}
		catch (SQLException ex) 
		{ 
			UTILS.LOG.log("SQLException", ex.getMessage(), "CStatus.java", 24); 
		}
	}
	
        class RemindTask extends TimerTask 
        {  
           public CStatus parent;
               
           @Override
           public void run() 
           {  
              parent.alive();
           }
        }
        
	public void setLastTablesBlock(long block)
	{
	   UTILS.DB.executeUpdate("UPDATE status SET last_tables_block='"+block+"'");	
	   this.last_tables_block=block;
	}
	
	public void setLastBlocksBlock(long block)
	{
	   UTILS.DB.executeUpdate("UPDATE status SET last_blocks_block='"+block+"'");	
	   this.last_blocks_block=block;
	}
	
	public void setLastSysBlock(long block)
	{
	   UTILS.DB.executeUpdate("UPDATE status SET last_blocks_block='"+block+"'");	
	   this.last_blocks_block=block;
	   this.last_tables_block=block;
	}
	
	
	public void setEngineStatus(String status)
	{
		UTILS.DB.executeUpdate("UPDATE status SET engine_status='"+status+"'");
		this.engine_status=status;
	}
	
	public String getStage(long dif)
	{
		if (dif<=2) return "ID_NEW_BLOCK";
		
		if (dif==3) return "ID_SEND_BLOCK_100000";
		if (dif==4) return "ID_SEND_BLOCK_80000";
		if (dif==5) return "ID_SEND_BLOCK_60000";
		if (dif==6) return "ID_SEND_BLOCK_40000";
		if (dif==7) return "ID_SEND_BLOCK_20000";
		if (dif==8) return "ID_SEND_BLOCK_10000";
		if (dif==9) return "ID_SEND_BLOCK_9500";
		if (dif==10) return "ID_SEND_BLOCK_9000";
		if (dif==11) return "ID_SEND_BLOCK_8500";
		if (dif==12) return "ID_SEND_BLOCK_8000";
		if (dif==13) return "ID_SEND_BLOCK_7500";
		if (dif==14) return "ID_SEND_BLOCK_7000";
		if (dif==15) return "ID_SEND_BLOCK_6500";
		if (dif==16) return "ID_SEND_BLOCK_6000";
		if (dif==17) return "ID_SEND_BLOCK_5500";
		if (dif==18) return "ID_SEND_BLOCK_5000";
		if (dif==19) return "ID_SEND_BLOCK_4500";
		if (dif==20) return "ID_SEND_BLOCK_4000";
		if (dif==21) return "ID_SEND_BLOCK_3500";
		if (dif==22) return "ID_SEND_BLOCK_3000";
		if (dif==23) return "ID_SEND_BLOCK_2700";
		if (dif==24) return "ID_SEND_BLOCK_2600";
		if (dif==25) return "ID_SEND_BLOCK_2500";
		if (dif==26) return "ID_SEND_BLOCK_2400";
		if (dif==27) return "ID_SEND_BLOCK_2300";
		if (dif==28) return "ID_SEND_BLOCK_2200";
		if (dif==29) return "ID_SEND_BLOCK_2100";
		if (dif==30) return "ID_SEND_BLOCK_2000";
		if (dif==31) return "ID_SEND_BLOCK_1900";
		if (dif==32) return "ID_SEND_BLOCK_1800";
		if (dif==33) return "ID_SEND_BLOCK_1700";
		if (dif==34) return "ID_SEND_BLOCK_1600";
		if (dif==35) return "ID_SEND_BLOCK_1500";

		
		if (dif>35 && dif<45) return "ID_LAST_BLOCKS"; 
		
		if (dif==46) return "ID_CONFIRM_100000";
		if (dif==47) return "ID_CONFIRM_90000";
		if (dif==48) return "ID_CONFIRM_80000";
		if (dif==49) return "ID_CONFIRM_60000";
		if (dif==50) return "ID_CONFIRM_40000";
		if (dif==51) return "ID_CONFIRM_20000";
		if (dif==52) return "ID_CONFIRM_10000";
		if (dif==53) return "ID_CONFIRM_9500";
		if (dif==54) return "ID_CONFIRM_9000";
		if (dif==55) return "ID_CONFIRM_8500";
		if (dif==56) return "ID_CONFIRM_8000";
		if (dif==57) return "ID_CONFIRM_7500";
		if (dif==58) return "ID_CONFIRM_7000";
		if (dif==59) return "ID_CONFIRM_6500";
		if (dif==60) return "ID_CONFIRM_6000";
		if (dif==61) return "ID_CONFIRM_5500";
		if (dif==62) return "ID_CONFIRM_5000";
		if (dif==63) return "ID_CONFIRM_4500";
		if (dif==64) return "ID_CONFIRM_4000";
		if (dif==65) return "ID_CONFIRM_3500";
		if (dif==66) return "ID_CONFIRM_3000";
		if (dif==67) return "ID_CONFIRM_2700";
		if (dif==68) return "ID_CONFIRM_2600";
		if (dif==69) return "ID_CONFIRM_2500";
		if (dif==70) return "ID_CONFIRM_2400";
		if (dif==71) return "ID_CONFIRM_2300";
		if (dif==72) return "ID_CONFIRM_2200";
		if (dif==73) return "ID_CONFIRM_2100";
		if (dif==74) return "ID_CONFIRM_2000";
		if (dif==75) return "ID_CONFIRM_1900";
		if (dif==76) return "ID_CONFIRM_1800";
		if (dif==77) return "ID_CONFIRM_1700";
		if (dif==79) return "ID_CONFIRM_1600";
		if (dif==80) return "ID_CONFIRM_1500";
		
		
		if (dif>80 && dif<90) return "ID_LAST_CONFIRM";
		
		if (dif>90) return "ID_COMMIT_BLOCK";
		
		return "";
	}
	
        public void alive()
        {
           UTILS.DB.executeUpdate("UPDATE status SET alive='"+UTILS.BASIC.tstamp()+"'");
        }
        
	public void tick()
	{
            // Alive
            this.alive();
            
		long base=UTILS.BASIC.block()*100;
		this.dif=UTILS.BASIC.tstamp()-base;

		if (!this.netstat.equals(this.getStage(this.dif))) 
		{
			this.netstat=this.getStage(this.dif);
			
			if (UTILS.SETTINGS.settings.containsKey("display_status") && 
					UTILS.SETTINGS.settings.getProperty("display_status").equals("true"))
				UTILS.CONSOLE.write(this.netstat);
		}
	}
	
	public long getMinBalance(String stage)
	{
		if (stage.equals("ID_SEND_BLOCK_100000") || stage.equals("ID_CONFIRM_100000")) return (long)100000;
		if (stage.equals("ID_SEND_BLOCK_80000") || stage.equals("ID_CONFIRM_80000")) return (long)80000;
		if (stage.equals("ID_SEND_BLOCK_60000") || stage.equals("ID_CONFIRM_60000")) return (long)60000;
		if (stage.equals("ID_SEND_BLOCK_40000") || stage.equals("ID_CONFIRM_40000")) return (long)40000;
		if (stage.equals("ID_SEND_BLOCK_20000") || stage.equals("ID_CONFIRM_20000")) return (long)20000;
		if (stage.equals("ID_SEND_BLOCK_10000") || stage.equals("ID_CONFIRM_10000")) return (long)10000;
		if (stage.equals("ID_SEND_BLOCK_9500") || stage.equals("ID_CONFIRM_9500")) return (long)9500;
		if (stage.equals("ID_SEND_BLOCK_9000") || stage.equals("ID_CONFIRM_9000")) return (long)9000;
		if (stage.equals("ID_SEND_BLOCK_8500") || stage.equals("ID_CONFIRM_8500")) return (long)8500;
		if (stage.equals("ID_SEND_BLOCK_8000") || stage.equals("ID_CONFIRM_8000")) return (long)8000;
		if (stage.equals("ID_SEND_BLOCK_7500") || stage.equals("ID_CONFIRM_7500")) return (long)7500;
		if (stage.equals("ID_SEND_BLOCK_7000") || stage.equals("ID_CONFIRM_7000")) return (long)7000;
		if (stage.equals("ID_SEND_BLOCK_6500") || stage.equals("ID_CONFIRM_6500")) return (long)6500;
		if (stage.equals("ID_SEND_BLOCK_6000") || stage.equals("ID_CONFIRM_6000")) return (long)6000;
		if (stage.equals("ID_SEND_BLOCK_5500") || stage.equals("ID_CONFIRM_5500")) return (long)5500;
		if (stage.equals("ID_SEND_BLOCK_5000") || stage.equals("ID_CONFIRM_5000")) return (long)5000;
		if (stage.equals("ID_SEND_BLOCK_4500") || stage.equals("ID_CONFIRM_4500")) return (long)4500;
		if (stage.equals("ID_SEND_BLOCK_4000") || stage.equals("ID_CONFIRM_4000")) return (long)4000;
		if (stage.equals("ID_SEND_BLOCK_3500") || stage.equals("ID_CONFIRM_3500")) return (long)3500;
		if (stage.equals("ID_SEND_BLOCK_3000") || stage.equals("ID_CONFIRM_3000")) return (long)3000;
		if (stage.equals("ID_SEND_BLOCK_2700") || stage.equals("ID_CONFIRM_2700")) return (long)2700;
		if (stage.equals("ID_SEND_BLOCK_2600") || stage.equals("ID_CONFIRM_2600")) return (long)2600;
		if (stage.equals("ID_SEND_BLOCK_2500") || stage.equals("ID_CONFIRM_2500")) return (long)2500;
		if (stage.equals("ID_SEND_BLOCK_2400") || stage.equals("ID_CONFIRM_2400")) return (long)2400;
		if (stage.equals("ID_SEND_BLOCK_2300") || stage.equals("ID_CONFIRM_2300")) return (long)2300;
		if (stage.equals("ID_SEND_BLOCK_2200") || stage.equals("ID_CONFIRM_2200")) return (long)2200;
		if (stage.equals("ID_SEND_BLOCK_2100") || stage.equals("ID_CONFIRM_2100")) return (long)2100;
		if (stage.equals("ID_SEND_BLOCK_2000") || stage.equals("ID_CONFIRM_2000")) return (long)2000;
		if (stage.equals("ID_SEND_BLOCK_1900") || stage.equals("ID_CONFIRM_1900")) return (long)1900;
		if (stage.equals("ID_SEND_BLOCK_1800") || stage.equals("ID_CONFIRM_1800")) return (long)1800;
		if (stage.equals("ID_SEND_BLOCK_1700") || stage.equals("ID_CONFIRM_17000")) return (long)1700;
		if (stage.equals("ID_SEND_BLOCK_1600") || stage.equals("ID_CONFIRM_1600")) return (long)1600;
		if (stage.equals("ID_SEND_BLOCK_1500") || stage.equals("ID_CONFIRM_1500")) return (long)1500;
		
		return (long)0;
	}
	
	public int getSecond(String stage)
	{
		if (stage.equals("ID_SEND_BLOCK_100000")) return 3;
		if (stage.equals("ID_SEND_BLOCK_80000")) return 4;
		if (stage.equals("ID_SEND_BLOCK_60000")) return 5;
		if (stage.equals("ID_SEND_BLOCK_40000")) return 6;
		if (stage.equals("ID_SEND_BLOCK_20000")) return 7;
		if (stage.equals("ID_SEND_BLOCK_10000")) return 8;
		if (stage.equals("ID_SEND_BLOCK_9500")) return 9;
		if (stage.equals("ID_SEND_BLOCK_9000")) return 10;
		if (stage.equals("ID_SEND_BLOCK_8500")) return 11;
		if (stage.equals("ID_SEND_BLOCK_8000")) return 12;
		if (stage.equals("ID_SEND_BLOCK_7500")) return 13;
		if (stage.equals("ID_SEND_BLOCK_7000")) return 14;
		if (stage.equals("ID_SEND_BLOCK_6500")) return 15;
		if (stage.equals("ID_SEND_BLOCK_6000")) return 16;
		if (stage.equals("ID_SEND_BLOCK_5500")) return 17;
		if (stage.equals("ID_SEND_BLOCK_5000")) return 18;
		if (stage.equals("ID_SEND_BLOCK_4500")) return 19;
		if (stage.equals("ID_SEND_BLOCK_4000")) return 20;
		if (stage.equals("ID_SEND_BLOCK_3500")) return 21;
		if (stage.equals("ID_SEND_BLOCK_3000")) return 22;
		if (stage.equals("ID_SEND_BLOCK_2700")) return 23;
		if (stage.equals("ID_SEND_BLOCK_2600")) return 24;
		if (stage.equals("ID_SEND_BLOCK_2500")) return 25;
		if (stage.equals("ID_SEND_BLOCK_2400")) return 26;
		if (stage.equals("ID_SEND_BLOCK_2300")) return 27;
		if (stage.equals("ID_SEND_BLOCK_2200")) return 28;
		if (stage.equals("ID_SEND_BLOCK_2100")) return 29;
		if (stage.equals("ID_SEND_BLOCK_2000")) return 30;
		if (stage.equals("ID_SEND_BLOCK_1900")) return 31;
		if (stage.equals("ID_SEND_BLOCK_1800")) return 32;
		if (stage.equals("ID_SEND_BLOCK_1700")) return 33;
		if (stage.equals("ID_SEND_BLOCK_1600")) return 34;
		if (stage.equals("ID_SEND_BLOCK_1500")) return 35;
		
		return (int)0;
	}
	
	public String getStage(double balance)
	{
		if (balance>100000) return "ID_SEND_BLOCK_100000";
		if (balance>80000) return "ID_SEND_BLOCK_80000";
		if (balance>60000) return "ID_SEND_BLOCK_60000";
		if (balance>40000) return "ID_SEND_BLOCK_40000";
		if (balance>30000) return "ID_SEND_BLOCK_20000";
		if (balance>10000) return "ID_SEND_BLOCK_10000";
		if (balance>9500) return "ID_SEND_BLOCK_9500";
		if (balance>9000) return "ID_SEND_BLOCK_9000";
		if (balance>8500) return "ID_SEND_BLOCK_8500";
		if (balance>8000) return "ID_SEND_BLOCK_8000";
		if (balance>7500) return "ID_SEND_BLOCK_7500";
		if (balance>7000) return "ID_SEND_BLOCK_7000";
		if (balance>6500) return "ID_SEND_BLOCK_6500";
		if (balance>6000) return "ID_SEND_BLOCK_6000";
		if (balance>5500) return "ID_SEND_BLOCK_5500";
		if (balance>5000) return "ID_SEND_BLOCK_5000";
		if (balance>4500) return "ID_SEND_BLOCK_4500";
		if (balance>4000) return "ID_SEND_BLOCK_4000";
		if (balance>3500) return "ID_SEND_BLOCK_3500";
		if (balance>3000) return "ID_SEND_BLOCK_3000";
		if (balance>2700) return "ID_SEND_BLOCK_2700";
		if (balance>2600) return "ID_SEND_BLOCK_2600";
		if (balance>2500) return "ID_SEND_BLOCK_2500";
		if (balance>2400) return "ID_SEND_BLOCK_2400";
		if (balance>2300) return "ID_SEND_BLOCK_2300";
		if (balance>2200) return "ID_SEND_BLOCK_2200";
		if (balance>2100) return "ID_SEND_BLOCK_2100";
		if (balance>2000) return "ID_SEND_BLOCK_2000";
		if (balance>1900) return "ID_SEND_BLOCK_1900";
		if (balance>1800) return "ID_SEND_BLOCK_1800";
		if (balance>1700) return "ID_SEND_BLOCK_1700";
		if (balance>1600) return "ID_SEND_BLOCK_1600";
		if (balance>1500) return "ID_SEND_BLOCK_1500";
		
		return "";
	}
}
