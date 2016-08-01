package wallet.kernel.stress_test;

import wallet.kernel.UTILS;
import wallet.network.packets.trade.feeds.CNewFeedPacket;

public class CSTFeeds extends CStressTest
{
    public CSTFeeds()
    {
        super("feeds");
    }
    
    public void runFeeds() throws Exception
    {
        CNewFeedPacket packet=new CNewFeedPacket(this.randomAdr(), 
                                                 this.randomAdr(), 
                                                 "Test feed",
                                                 "Test feed",
                                                 "http://www.yahoo.com",
                                                  this.randomSymbol(),
                                                  this.randomDays());
        UTILS.NETWORK.broadcast(packet);
    }
}
