package wallet.network.packets.sync;

import wallet.kernel.UTILS;
import wallet.network.CPeer;
import wallet.network.packets.CPacket;

public class CPing extends CPacket
{
    // Time
    long local_time;
    
    // Software version
    long ver;
    
    public CPing() throws Exception
    {
       // Constructor
       super("ID_PING_PACKET");
       
       // Time
       this.local_time=UTILS.BASIC.tstamp();
       
       // Version
       this.ver=UTILS.STATUS.version;
       
       // Hash
       this.hash=UTILS.BASIC.hash(this.hash()+this.ver);
    }
    
    public void check(CPeer sender) throws Exception
    {
        // Sender
        if (!UTILS.BASIC.isIP(sender.adr))
            throw new Exception("Invalid sender");
        
        // Update
        UTILS.DB.executeUpdate("UPDATE peers "
                                + "SET last_seen='"+this.tstamp+"', "
                                    + "ver='"+this.ver+"' "
                              + "WHERE peer='"+sender.adr+"'");
    }
}
