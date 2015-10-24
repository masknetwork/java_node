package wallet.network.packets.peers;

import wallet.kernel.UTILS;
import wallet.network.CPeer;
import wallet.network.CResult;
import wallet.network.packets.*;

public class CGetPeersPacket extends CPacket
{
    public CGetPeersPacket()
    {
        super("ID_GET_PEERS_PACKET");
        
        // Hash
        this.hash=UTILS.BASIC.hash(UTILS.BASIC.mtstamp()+this.tip);
    }
    
     public CResult checkWithPeer(CPeer peer)
    {
         // Create response
         CGetPeersResponsePacket packet=new CGetPeersResponsePacket();
         peer.writePacket(packet);
         
         // Return
 	 return new CResult(true, "Ok", "CGetPeersResponsePacket.java", 22);
    }
}
