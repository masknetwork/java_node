package wallet.kernel.stress_test;

import wallet.kernel.UTILS;
import wallet.network.packets.adr.CProfilePacket;

public class CSTProfiles extends CStressTest
{
    public CSTProfiles()
    {
        super("profiles");
    }
    
    public void runProfiles() throws Exception
    {
         CProfilePacket packet=new CProfilePacket(this.randomAdr(),
                                                  this.randomAdr(),
                                                  "Vlad Cristian - "+Math.round(Math.random()*100), 
		                                  "Founder & tech lead for MaskNetwork",
                                                  "vcris444@gmail.com", 
                                                  "http://www.yahoo.com", 
                                                   "https://www.exceptionnotfound.net/content/images/2015/04/the-coder.jpg", 
                                                   "https://www.exceptionnotfound.net/content/images/2015/04/the-coder.jpg", 
		                                   100);
        UTILS.NETWORK.broadcast(packet);
    }
}
