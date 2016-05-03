package wallet.network.packets.sync;

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
    }
    
    public CResult check() throws Exception
    {
        // Check blockchain
        CResult res=this.blockchain.check();
        if (!res.passed) 
            return res;
        else
            return new CResult(true, "Ok", "CDeliverBlockchainPacket", 67);
    }
}
