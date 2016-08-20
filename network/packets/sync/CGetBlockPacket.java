package wallet.network.packets.sync;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import wallet.kernel.UTILS;
import wallet.network.CPeer;
import wallet.network.packets.CPacket;
import wallet.network.packets.blocks.CBlockPacket;

public class CGetBlockPacket extends CPacket
{
    // Block hash
    String block_hash;
    
    public CGetBlockPacket(String block_hash) throws Exception
    {
        // Constructor
        super("ID_GET_BLOCK_PACKET");
        
        // Block hash
        this.block_hash=block_hash;
        
        // Hash
        this.hash=UTILS.BASIC.hash(this.hash()+this.block_hash);
    }
    
    public void check(CPeer sender) throws Exception
    {
        // Response
        CPutBlockPacket packet=new CPutBlockPacket(this.block_hash);
        
        // Broadcast
        UTILS.NETWORK.broadcast(packet);
    }
}
