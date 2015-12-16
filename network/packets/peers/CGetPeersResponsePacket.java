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
    
    public CGetPeersResponsePacket()
    {
        // Constructor
        super("ID_GET_PEERS_RESPONSE_PACKET");
        
        try
        {
             // Add peers
             Statement s=UTILS.DB.getStatement();
             ResultSet rs=s.executeQuery("SELECT * "
                                         + "FROM peers_pool "
                                        + "WHERE accept_con='ID_YES' "
                                     + "ORDER BY RAND() "
                                        + "LIMIT 0,10");
        
           // Hash
           this.hash=UTILS.BASIC.hash(UTILS.BASIC.mtstamp()+this.tip);
        
           // Load data
           try
           {
              while (rs.next())
                this.addPeer(rs.getString("peer"), rs.getInt("port"));
           }
           catch (SQLException ex)
           {
               UTILS.LOG.log("SQLException", ex.getMessage(), "CGetPeersResponsePacket.java", 36);
           }
        
           // Close
           s.close();
        }
        catch (SQLException ex)
        {
           UTILS.LOG.log("SQLException", ex.getMessage(), "CGetPeersResponsePacket.java", 50);
        }
    }
    
    public void addPeer(String IP, int port)
    {
        // Add peer
        CPeerData peer=new CPeerData(IP, port);
        this.peers.add(peer);
    }
    
    public CResult check(CBlockPayload block)
    {
         // Check data
        for (int a=0; a<=this.peers.size()-1; a++)
        {
            CPeerData pd=(CPeerData) this.peers.get(a);
            
            CResult r=pd.check();
            if (r.passed==false)
            {
                r.report();
                return new CResult(false, "Invalid peer data", "CGetPeersResponsePacket.java", 22);
            }
            else pd.commit();
            
            // Null
            pd=null;
        }
        
                
         // Return
 	 return new CResult(true, "Ok", "CGetPeersResponsePacket.java", 22);
    }
}
