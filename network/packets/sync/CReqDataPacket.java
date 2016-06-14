package wallet.network.packets.sync;

import wallet.kernel.UTILS;
import wallet.network.CPeer;
import wallet.network.packets.CPacket;

public class CReqDataPacket extends CPacket
{
    // Tip
    String tip;
    
    // Table
    String table;
    
    // Checkpoint
    String checkpoint;
    
    // Start
    long start;
    
    // End
    long end;
    
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
                                   this.table+
                                   this.checkpoint+
                                   this.start+
                                   this.end);
    }
    
    public CReqDataPacket(String tip, String table, String checkpoint) throws Exception
    {
        // Constructor
        super ("ID_REQ_DATA_PACKET");
        
        // Tip
        this.tip=tip;
        
        // Table
        this.table=table;
        
        // Checkpoint
        this.checkpoint=checkpoint;
        
        // Hash
        this.hash=UTILS.BASIC.hash(this.hash()+
                                   this.tip+
                                   this.table+
                                   this.checkpoint+
                                   this.start+
                                   this.end);
    }
    
    public CReqDataPacket(String tip, long start) throws Exception
    {
        // Constructor
        super ("ID_REQ_DATA_PACKET");
        
        // Tip
        this.tip=tip;
        
        // Start
        this.start=start;
        
        // Hash
        this.hash=UTILS.BASIC.hash(this.hash()+
                                   this.tip+
                                   this.table+
                                   this.checkpoint+
                                   this.start+
                                   this.end);
    }
    
    public void process(CPeer sender) throws Exception
    {
        // Check type
        if (!this.tip.equals("ID_BLOCKCHAIN") && 
            !this.tip.equals("ID_BLOCKS") && 
            !this.tip.equals("ID_GET_TABLE"))
        throw new Exception("Invalid request type");
        
        // Table
        if (this.tip.equals("ID_GET_TABLE"))
        {
            CDeliverTablePacket packet=new CDeliverTablePacket(this.table, this.checkpoint);
            UTILS.NETWORK.sendToPeer(sender.adr, packet);
        }
        
        // Table
        if (this.tip.equals("ID_BLOCKCHAIN"))
        {
            CDeliverBlockchainPacket packet=new CDeliverBlockchainPacket(this.start);
            UTILS.NETWORK.sendToPeer(sender.adr, packet);
        }
        
        // Blocks
        if (this.tip.equals("ID_BLOCKS"))
        {
            CDeliverBlocksPacket packet=new CDeliverBlocksPacket(this.start, this.end);
            UTILS.NETWORK.sendToPeer(sender.adr, packet);
        }
        
    }
}
