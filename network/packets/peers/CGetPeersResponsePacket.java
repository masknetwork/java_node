// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.peers;

import java.util.ArrayList;
import wallet.kernel.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.network.CPeer;
import wallet.network.CResult;
import wallet.network.packets.*;
import wallet.network.packets.blocks.CBlockPayload;

public class CGetPeersResponsePacket extends CPacket
{
    // Peers
    public ArrayList peers=new ArrayList(); 
    
    // Serial
   private static final long serialVersionUID = 100L;
    
    public CGetPeersResponsePacket()  throws Exception
    {
        // Constructor
        super("ID_GET_PEERS_RESPONSE_PACKET");
        
        // Add peers
             
             ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                                + "FROM peers_pool "
                                               + "WHERE accept_con='ID_YES' "
                                            + "ORDER BY RAND() "
                                               + "LIMIT 0,10");
        
           // Hash
           this.hash=UTILS.BASIC.hash(UTILS.BASIC.mtstamp()+this.tip);
        
           while (rs.next())
              this.addPeer(rs.getString("peer"), rs.getInt("port"));
          
    }
    
    public void addPeer(String IP, int port)
    {
        // Add peer
        CPeerData peer=new CPeerData(IP, port);
        this.peers.add(peer);
    }
    
    public void check(CBlockPayload block) throws Exception
    {
        if (this.peers.size()>25)
            throw new Exception("Invalid peers size");
            
         // Check data
        for (int a=0; a<=this.peers.size()-1; a++)
        {
            // Peer data
            CPeerData pd=(CPeerData) this.peers.get(a);
            
            // Commit
            pd.commit();
        }
    }
}
