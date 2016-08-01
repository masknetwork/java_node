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
    
    public CGetBlockPacket() throws Exception
    {
        super("ID_GET_BLOCK_PACKET");
    }
    
    public void process(CPeer sender) throws Exception
    {
        CPutBlockPacket packet=new CPutBlockPacket(this.block_hash);
        UTILS.NETWORK.broadcast(packet);
    }
}
