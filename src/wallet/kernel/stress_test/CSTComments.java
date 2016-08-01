package wallet.kernel.stress_test;

import wallet.network.packets.tweets.CCommentPacket;

public class CSTComments extends CStressTest 
{
    public CSTComments()
    {
        super("comments");
    }
    
    public void runComments() throws Exception
    {
         CCommentPacket mes=new CCommentPacket("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEmzMuWmpif2JdeB1/UA7XQumglLj3o4/qF/CisxzJyXf7JoXXmGvBDJFbCSr8Si09zKtkfoA7jyU=",
                                               "ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEmzMuWmpif2JdeB1/UA7XQumglLj3o4/qF/CisxzJyXf7JoXXmGvBDJFbCSr8Si09zKtkfoA7jyU=", 
		                               "ID_TWEET",
                                                847198311,
		                               "Nice tweet");
       //mes.commit(null);
    }
}
