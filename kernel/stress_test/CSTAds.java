package wallet.kernel.stress_test;

import wallet.kernel.UTILS;
import wallet.network.packets.ads.CNewAdPacket;

public class CSTAds extends CStressTest
{
    public CSTAds()
    {
        super("ads");
    }
    
    public void runAds() throws Exception
    {
        CNewAdPacket packet=new CNewAdPacket(this.randomAdr(),
                                             this.randomAdr(),
                                             "RO", 
		                             Math.round(Math.random()*10), 
		                             Math.random()/10, 
		                             "Ad test", 
		                             "This is a test ad. This is a test ad. This is a test ad. ", 
		                             "http://www.yahoo.com");
         UTILS.NETWORK.broadcast(packet);
    }
}
