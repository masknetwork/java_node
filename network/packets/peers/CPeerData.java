package wallet.network.packets.peers;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.UTILS;
import wallet.network.CResult;

public class CPeerData implements Serializable
{
   // IP
   public String IP;
   
   // Port
   public int port;
   
    public CPeerData(String IP, int port)
    {
        // IP
        this.IP=IP;
        
        // Port
        this.port=port;
    }
    
    public CResult check()
    {
        // Check ip
        if (UTILS.BASIC.IPValid(IP)==false)
           return new CResult(false, "Invalid IP format", "CPeerData.java", 22);
        
        // Check port
        if (port<0 || port>65000)
           return new CResult(false, "Invalid port", "CPeerData.java", 22);
        
        // Return
 	return new CResult(true, "Ok", "CGetPeersResponsePacket.java", 22);
    }
    
    public void commit()
    {
        try
        {
             // Already exist ?
             Statement s=UTILS.DB.getStatement();
             ResultSet rs=s.executeQuery("SELECT * "
                                      + "FROM peers_pool "
                                     + "WHERE peer='"+this.IP+"'");
        
            // Insert peer
            if (UTILS.DB.hasData(rs)==false)
            {
                UTILS.DB.executeUpdate("INSERT INTO peers_pool(peer, "
                                                 + "port, "
                                                 + "accept_con) "
                               + "VALUES('"+this.IP+"', '"
                                           +this.port+"', 'ID_PENDING')");
            }
        
           // CLose
           s.close();
        }
        catch (SQLException ex)
        {
            UTILS.LOG.log("SQLException", ex.getMessage(), "CPeerData.java", 64);
        }
    }
}
