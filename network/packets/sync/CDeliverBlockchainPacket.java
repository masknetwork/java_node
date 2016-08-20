package wallet.network.packets.sync;

import wallet.kernel.UTILS;
import wallet.network.CPeer;
import wallet.network.CResult;
import wallet.network.packets.CPacket;

public class CDeliverBlockchainPacket extends CPacket
{
    // Blockchain
    CBlockchain blockchain;
    
    // Serial
    private static final long serialVersionUID = 100L;
    
    public CDeliverBlockchainPacket(long start) throws Exception
    {
        // Constructor
        super("ID_DELIVER_BLOCKCHAIN_PACKET");
        
        // Load blockchain
        this.blockchain=new CBlockchain(start);
        
        // Hash
        this.hash=this.hash();
    }
    
    
    
    public void check(CPeer sender) throws Exception
    {
        // Sync ?
        if (!UTILS.STATUS.engine_status.equals("ID_SYNC"))
            return;
            
        // Check blockchain
        CResult res=this.blockchain.check();
        
        // Not passed
        if (res.passed) 
            UTILS.SYNC.loadBlockchain(blockchain);
        else
            System.out.println(res.reason);
    }
}
