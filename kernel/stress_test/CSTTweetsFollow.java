package wallet.kernel.stress_test;

import wallet.network.packets.tweets.CFollowPacket;
import wallet.network.packets.tweets.CUnfollowPacket;

public class CSTTweetsFollow extends CStressTest
{
    public CSTTweetsFollow()
    {
        super("tweets_follow");
    }
    
    public void runFollow() throws Exception
    {
        CFollowPacket packet=new CFollowPacket("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEBePpthduYln32RlxFoHzWnQCMHCyeLDNsPVMlp4whoau6VKdGpqj+rTr9OOl/fv8HpnoD1ZRk4Q=",
                                               "ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEBePpthduYln32RlxFoHzWnQCMHCyeLDNsPVMlp4whoau6VKdGpqj+rTr9OOl/fv8HpnoD1ZRk4Q=", 
		                               "ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEmzMuWmpif2JdeB1/UA7XQumglLj3o4/qF/CisxzJyXf7JoXXmGvBDJFbCSr8Si09zKtkfoA7jyU=",
                                               12);
        //packet.commit(null);
    }
    
    public void runTweetUnfollow() throws Exception
    {
        CUnfollowPacket packet=new CUnfollowPacket("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEBePpthduYln32RlxFoHzWnQCMHCyeLDNsPVMlp4whoau6VKdGpqj+rTr9OOl/fv8HpnoD1ZRk4Q=",
                                                  "ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEBePpthduYln32RlxFoHzWnQCMHCyeLDNsPVMlp4whoau6VKdGpqj+rTr9OOl/fv8HpnoD1ZRk4Q=", 
		                                  "ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEmzMuWmpif2JdeB1/UA7XQumglLj3o4/qF/CisxzJyXf7JoXXmGvBDJFbCSr8Si09zKtkfoA7jyU=");
        packet.commit(null);
    }
}
