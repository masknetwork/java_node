package wallet.kernel.stress_test;

import wallet.network.packets.trans.CEscrowedTransSignPacket;

public class CSTEscrowed extends CStressTest
{
    public CSTEscrowed()
    {
        super("escrowed");
    }
    
    public void runNew()
    {
        
    }
    
    public void runSign() throws Exception
    {
       CEscrowedTransSignPacket packet=new CEscrowedTransSignPacket("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEmzMuWmpif2JdeB1/UA7XQumglLj3o4/qF/CisxzJyXf7JoXXmGvBDJFbCSr8Si09zKtkfoA7jyU=",
                                                                     "d73b83aec3a15b2cb413f04980f4e3cdb697a6cd35651db6a4c3a408704cbf3b", 
                                                                     "ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEBePpthduYln32RlxFoHzWnQCMHCyeLDNsPVMlp4whoau6VKdGpqj+rTr9OOl/fv8HpnoD1ZRk4Q=",
                                                                     "ID_RETURN");
        packet.commit(null);
    }
}
