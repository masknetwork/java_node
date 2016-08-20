package wallet.kernel.stress_test;

import wallet.kernel.UTILS;
import wallet.network.packets.misc.CDelVotePacket;

public class CSTDelegates extends CStressTest 
{
    public CSTDelegates()
    {
        super("delegates");
    }
    
    public void runDelegates() throws Exception
    {
        CDelVotePacket packet=new CDelVotePacket(this.randomAdr(),
                                                 this.randomAdr(), 
                                                 this.randomAdr(),
                                                 this.randomType());
        UTILS.NETWORK.broadcast(packet);
    }
    
    public String randomType()
    {
        long l=Math.round(Math.random()*10);
        if (l<8) return "ID_UP"; else return "ID_DOWN";
    }
}
