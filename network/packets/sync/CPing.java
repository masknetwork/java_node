package wallet.network.packets.sync;

import wallet.kernel.UTILS;
import wallet.network.CPeer;
import wallet.network.packets.CPacket;

public class CPing extends CPacket
{
    // Time
    long local_time;
    
    // Software version
    String ver;
    
    public CPing() throws Exception
    {
       // Constructor
       super("ID_PING_PACKET");
       
       // Time
       this.local_time=UTILS.BASIC.tstamp();
       
       // Version
       this.ver=UTILS.STATUS.version;
       
       // Hash
       this.hash=UTILS.BASIC.hash(this.local_time+this.ver+UTILS.BASIC.mtstamp());
    }
    
    public void check(CPeer sender) throws Exception
    {
        // Update
        UTILS.DB.executeUpdate("UPDATE peers "
                                + "SET tstamp='"+this.tstamp+"', "
                                    + "ver='"+this.ver+"' "
                              + "WHERE peer='"+sender.adr+"'");
    }
}
