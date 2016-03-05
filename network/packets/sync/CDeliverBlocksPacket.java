package wallet.network.packets.sync;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import wallet.network.packets.blocks.CBlockPacket;

import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CPacket;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trans.*;

public class CDeliverBlocksPacket extends CPacket
{
    // Start 
    public long start;
	
    // End
    public long end;
		
    // More data
    boolean more=false;
		
    // Blocks
    ArrayList blocks=new ArrayList();
		
    // Timer
    Timer timer;
		
    public CDeliverBlocksPacket(long start, long end)  throws Exception
    {
	super("ID_DELIVER_BLOCKS_PACKET");
			
	// Start
	this.start=start;
        
        // End
        this.end=end;
			
	// Hash
        this.hash=UTILS.BASIC.hash(this.start+
				   this.end+
                                   String.valueOf(UTILS.SERIAL.serialize(this.blocks)));
   }
	    
    public void addBlock(CBlockPacket block) throws Exception
    {
        // Add block
	blocks.add(block);
			
	// Hash
	this.hash=UTILS.BASIC.hash(this.start+
			           this.end+
			           String.valueOf(UTILS.SERIAL.serialize(this.blocks)));
    }
		
    public CResult commit(CBlockPayload block) throws Exception
    { 
        // Start timer
	timer = new Timer();  
	timer.schedule(new RemindTask(this.start, this.end, this.blocks), 0, 1000);
        
        // Return 
        return new CResult(true, "Ok", "CNewFeedPayload", 67);
    }
	   
    class RemindTask extends TimerTask  
    {    
	// Start
        long start;
		   
	// End
        long end;
        
        // Blocks
        ArrayList blocks;
		   
	public RemindTask(long start, long end, ArrayList blocks)
        {
            // Start
	    this.start=start;
            
            // End
            this.end=end;
            
            // Blocks
	    this.blocks=blocks;
	}
		   
	@Override
	public void run() 
        {  
            try
            {    
	    	// Resultset
                ResultSet rs;
                   
                // Statement
                Statement s=UTILS.DB.getStatement();
	    	   
	    	// Smallest start ?
	    	rs=s.executeQuery("SELECT * "
				  + "FROM sync "
			         + "WHERE type='ID_BLOCKS' "
				   + "AND start<"+this.start);
	    	     
	    	   
	    		   
			// No data
                        if (!UTILS.DB.hasData(rs))
		        {
		            // Commit
			    for (int a=0; a<=this.blocks.size()-1; a++)
			    {
				CBlockPacket block=(CBlockPacket)this.blocks.get(a);
				CResult res=block.commit();
				
                                // Report error
                                if (!res.passed) res.report();
					 
				UTILS.DB.executeUpdate("UPDATE status "
                                                        + "SET last_blocks_block='"+(this.start+a)+"'");
			    }
				   
		            // Delete from sync
			    UTILS.DB.executeUpdate("DELETE FROM sync "
					 	       + "WHERE type='ID_BLOCKS' "
					 		 + "AND start='"+this.start+"'");
					 
					
			    // Stop timer
			    this.cancel();
			}
	       }
               catch (Exception ex) 
       	       {  
       		UTILS.LOG.log("SQLException", ex.getMessage(), "CBuyDomainPayload.java", 57);
               }
	   }
    }
}
		
