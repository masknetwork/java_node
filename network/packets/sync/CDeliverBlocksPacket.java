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
import wallet.network.CPeer;
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
    
    // Serial
   private static final long serialVersionUID = 100L;
		
    public CDeliverBlocksPacket(long start, long end)  throws Exception
    {
        // Constructor
	super("ID_DELIVER_BLOCKS_PACKET");
        
        // Start
	this.start=start;
        
        // End
        this.end=end;
        
        for (long block=start; block<=end; block++)
	{
		// Finds the block
                String bhash=this.getHash(block);
                
                // File
	        File f = new File(UTILS.WRITEDIR+"blocks/"+bhash+".block");
		
                // Exist
                if (f.exists())
 		{
		        // Read image from disk
		        FileInputStream f_in = new FileInputStream(UTILS.WRITEDIR+"blocks/"+bhash+".block");

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
   
    public String getHash(long block) throws Exception
    {
        // Statement
        
        
        // Load block
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM blocks "
                                          + "WHERE block='"+block+"'");
        
        // Has data
        if (UTILS.DB.hasData(rs))
        {
            // Next
            rs.next();
            
            // Hash
            String hash=rs.getString("hash");
            
            // Return
            return hash;
        }
        
        // Return
        return "";
    }
    
    public void addBlock(CBlockPacket block) throws Exception
    {
        // Add block
	blocks.add(block);
    }
		
    public void check(CPeer sender) throws Exception
    { 
        CResult res=null;
        
        for (int a=0; a<=this.blocks.size()-1; a++)
        {
	    CBlockPacket block=(CBlockPacket)this.blocks.get(a);
	    if (UTILS.SYNC.blockchain.commited(block.hash)) 
                block.commit();
				
            System.out.println(".");
        }
				   
	// Delete from sync
	UTILS.DB.executeUpdate("DELETE FROM sync "
	                           + "WHERE type='ID_BLOCKS' "
			             + "AND start='"+this.start+"'");
                            
                            
        //System.out.println("Done.");
    }
    
    
   
}
		
