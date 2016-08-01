package wallet.network.packets.sync;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import wallet.kernel.UTILS;
import wallet.network.packets.CPacket;
import wallet.network.packets.blocks.CBlockPacket;

public class CPutBlockPacket extends CPacket
{
    // Block
    CBlockPacket block=null;
    
    public CPutBlockPacket(String block_hash) throws Exception
    {
        super("ID_PUT_BLOCK_PACKET");
        
        // File
	File f = new File(UTILS.WRITEDIR+"blocks/"+block_hash+".block");
		
        // Exist
        if (f.exists())
 	{
            // Read image from disk
	    FileInputStream f_in = new FileInputStream(UTILS.WRITEDIR+"blocks/"+block_hash+".block");

	    // Read object using ObjectInputStream
	    ObjectInputStream obj_in = new ObjectInputStream (f_in);

	    // Read an object
	    CBlockPacket packet = (CBlockPacket)obj_in.readObject();
	}
    }
    
    public void process() throws Exception
    {
        if (block!=null)
            UTILS.CONSENSUS.blockReceived(block);
    }
}
