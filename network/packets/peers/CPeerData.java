// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.peers;

import java.io.Serializable;
import java.net.InetAddress;
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
   
   // Serial
   private static final long serialVersionUID = 100L;
   
    public CPeerData(String IP, int port)
    {
        // IP
        this.IP=IP;
        
        // Port
        this.port=port;
    }
    
   public void commit() throws Exception
    {
        // Check ip
        if (UTILS.BASIC.isIP(IP)==false)
           throw new Exception("Invalid IP format - CPeerData.java");
        
        // Check port
        if (port<0 || port>65000)
           throw new Exception("Invalid prt - CPeerData.java");
        
         // Already exist ?
         ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                          + "FROM peers_pool "
                                         + "WHERE peer='"+this.IP+"'");
        
          // Insert peer
          if (UTILS.DB.hasData(rs)==false)
                UTILS.DB.executeUpdate("INSERT INTO peers_pool(peer, "
                                                 + "port, "
                                                 + "accept_con) "
                               + "VALUES('"+this.IP+"', '"
                                           +this.port+"', 'ID_PENDING')");
    }
}
