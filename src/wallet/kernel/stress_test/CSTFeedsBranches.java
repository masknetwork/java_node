package wallet.kernel.stress_test;

import wallet.kernel.UTILS;
import wallet.network.packets.trade.feeds.CNewFeedComponentPacket;

public class CSTFeedsBranches extends CStressTest 
{
    public CSTFeedsBranches()
    {
        super("feeds_branches");
    }
    
     public void runBranches() throws Exception
    {
        CNewFeedComponentPacket packet=new CNewFeedComponentPacket(this.randomAdr(),
                                                                   this.randomFeed(),
                                                                   "Test branch", 
                                                                   "Test description", 
                                                                   "ID_FOREX",
                                                                   this.randomSymbol(), 
                                                                   this.randomSymbol(), 
                                                                   0.0001,
                                                                   this.randomDays());
        UTILS.NETWORK.broadcast(packet);
    }
}
