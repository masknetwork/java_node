package wallet.network.packets.sync;

import wallet.kernel.UTILS;
import wallet.network.CPeer;
import wallet.network.CResult;
import wallet.network.packets.CPacket;

public class CDeliverBlockchainPacket extends CPacket
{
    // Blockchain
    CBlockchain blockchain;
    
    public CDeliverBlockchainPacket(long start) throws Exception
    {
        // Constructor
        super("ID_DELIVER_BLOCKCHAIN_PACKET");
        
        // Load blockchain
        this.blockchain=new CBlockchain(start);
        
        // Hash
        this.hash=this.hash();
    }
    
    
    
    public void process(CPeer sender) throws Exception
    {
        // Check blockchain
        CResult res=this.blockchain.check();
        
        // Not passed
        if (res.passed) 
            UTILS.SYNC.loadBlockchain(blockchain);
        else
            System.out.println(res.reason);
    }
}
