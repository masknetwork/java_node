package wallet.network.packets.sync;

import wallet.kernel.UTILS;
import wallet.network.CPeer;
import wallet.network.packets.CPacket;

public class CNetStatPacket extends CPacket
{
    // Last block
    public long last_block;
    
    // Last block hash
    public String last_block_hash;
    
    // Time
    public long local_time;
    
    // Version
    public long ver;
    
    public CNetStatPacket() throws Exception
    {
        super("ID_NETSTAT_PACKET");
        
        // Last block
        this.last_block=UTILS.NET_STAT.last_block;
        
        // Last block hash
        this.last_block_hash=UTILS.NET_STAT.last_block_hash;
        
        // Local time
        this.local_time=UTILS.BASIC.tstamp();
        
        // Version
        this.ver=UTILS.STATUS.version;
        
        // Hash
        this.hash=UTILS.BASIC.hash(this.hash+
                                   this.last_block+
                                   this.last_block_hash+
                                   this.local_time+this.ver);
    }
    
    public void check(CPeer peer) throws Exception
    {
        // Sync
        if (!UTILS.STATUS.engine_status.equals("ID_SYNC"))
           return;
           
        // Load web stat
        UTILS.SYNC.loadNetstat(this);
    }
}
