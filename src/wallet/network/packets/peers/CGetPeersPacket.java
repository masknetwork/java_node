// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.peers;

import wallet.kernel.UTILS;
import wallet.network.CPeer;
import wallet.network.CResult;
import wallet.network.packets.*;

public class CGetPeersPacket extends CPacket
{
    // Serial
   private static final long serialVersionUID = 100L;
   
    public CGetPeersPacket() throws Exception
    {
        super("ID_GET_PEERS_PACKET");
        
        // Hash
        this.hash=UTILS.BASIC.hash(UTILS.BASIC.mtstamp()+this.tip);
    }
    
     public CResult checkWithPeer(CPeer peer) throws Exception
    {
         // Create response
         CGetPeersResponsePacket packet=new CGetPeersResponsePacket();
         peer.writePacket(packet);
         
         // Return
 	 return new CResult(true, "Ok", "CGetPeersResponsePacket.java", 22);
    }
}
