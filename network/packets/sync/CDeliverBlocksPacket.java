package wallet.network.packets.sync;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
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
        
        for (long block=start; block<=end; block++)
	{
		// Finds the block
	        File f = new File(UTILS.WRITEDIR+"blocks/block_"+block+".block");
		if (f.exists())
 		{
		    // Read image from disk
		        FileInputStream f_in = new FileInputStream(UTILS.WRITEDIR+"blocks/block_"+block+".block");

		        // Read object using ObjectInputStream
		        ObjectInputStream obj_in = new ObjectInputStream (f_in);

		        // Read an object
			CBlockPacket obj = (CBlockPacket)obj_in.readObject();
				     
			// Add block
			this.addBlock(obj);
		}
                else throw new Exception("Could not find block "+block);
        }
			
	// Hash
        this.hash=UTILS.BASIC.hash(this.start+
				   this.end+
                                   String.valueOf(UTILS.SERIAL.serialize(this.blocks)));
   }
	    
    public void addBlock(CBlockPacket block) throws Exception
    {
        // Add block
	blocks.add(block);
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
                             // Stop timer
			    this.cancel();
                            
		            // Commit
			    for (int a=0; a<=this.blocks.size()-1; a++)
			    {
				CBlockPacket block=(CBlockPacket)this.blocks.get(a);
				CResult res=block.commit();
				
                                // Report error
                                if (!res.passed) res.report();
			        
                               UTILS.DB.executeUpdate("UPDATE status "
                                                        + "SET last_blocks_block='"+(this.start+a)+"'");
                               
                               System.out.print(".");
                            }
				   
		            // Delete from sync
			    UTILS.DB.executeUpdate("DELETE FROM sync "
					 	       + "WHERE type='ID_BLOCKS' "
					 		 + "AND start='"+this.start+"'");
                            
                            System.out.println("");
                            System.out.println("Done.");
					 
			}
	       }
               catch (Exception ex) 
       	       {  
       		UTILS.LOG.log("SQLException", ex.getMessage(), "CBuyDomainPayload.java", 57);
               }
	   }
    }
}
		
