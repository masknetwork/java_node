package wallet.network.packets.sync;

import wallet.kernel.UTILS;
import wallet.network.CPeer;
import wallet.network.packets.CPacket;

public class CReqDataPacket extends CPacket
{
    // Tip
    String tip;
   
    // Start
    long start;
    
    // End
    long end;
    
    // Serial
    private static final long serialVersionUID = 100L;
    
    public CReqDataPacket(String tip, 
                          long start, 
                          long end) throws Exception
    {
        // Constructor
        super ("ID_REQ_DATA_PACKET");
        
        // Tip
        this.tip=tip;
        
        // Start
        this.start=start;
        
        // End
        this.end=end;
        
        // Hash
        this.hash=UTILS.BASIC.hash(this.hash()+
                                   this.tip+
                                   this.start+
                                   this.end);
    }
    
    public CReqDataPacket(String tip) throws Exception
    {
        // Constructor
        super ("ID_REQ_DATA_PACKET");
        
        // Tip
        this.tip=tip;
        
        // Start
        this.start=start;
        
        // End
        this.end=end;
        
        // Hash
        this.hash=UTILS.BASIC.hash(this.hash()+
                                   this.tip+
                                   this.start+
                                   this.end);
    }
    
    public void check(CPeer sender) throws Exception
    {
        // Check type
        if (!this.tip.equals("ID_NETSTAT") && 
            !this.tip.equals("ID_BLOCKS"))
        throw new Exception("Invalid request type");
        
        // Blocks
        if (this.tip.equals("ID_BLOCKS"))
        {
            CDeliverBlocksPacket packet=new CDeliverBlocksPacket(this.start, this.end);
            UTILS.NETWORK.sendToPeer(sender.adr, packet);
        }
        
        // Net stat
        if (this.tip.equals("ID_NETSTAT"))
        {
            CNetStatPacket packet=new CNetStatPacket();
            UTILS.NETWORK.sendToPeer(sender.adr, packet);
        }
        
    }
}
