package wallet.kernel.stress_test;

import wallet.kernel.UTILS;
import wallet.network.packets.domains.CBuyDomainPacket;
import wallet.network.packets.domains.CRentDomainPacket;
import wallet.network.packets.domains.CSaleDomainPacket;
import wallet.network.packets.domains.CTransferDomainPacket;

public class CSTDomains extends CStressTest
{
    public CSTDomains()
    {
        super("domains");
    }
    
    
    public void runDomains() throws Exception
    {
        CRentDomainPacket packet=new CRentDomainPacket(this.randomAdr(), 
                                                       this.randomAdr(), 
                                                       "teste"+String.valueOf(Math.round(Math.random()*1000000)), 
                                                       100);
         UTILS.NETWORK.broadcast(packet);
    }
    
     public void runSaleDomain() throws Exception
    {
       CSaleDomainPacket packet=new CSaleDomainPacket("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEmzMuWmpif2JdeB1/UA7XQumglLj3o4/qF/CisxzJyXf7JoXXmGvBDJFbCSr8Si09zKtkfoA7jyU=", 
                                                       "ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEmzMuWmpif2JdeB1/UA7XQumglLj3o4/qF/CisxzJyXf7JoXXmGvBDJFbCSr8Si09zKtkfoA7jyU=", 
		                                       "teste677", 
                                                       1);
        packet.commit(null);
    }
    
    public void runBuyDomain() throws Exception
    {
        CBuyDomainPacket packet=new CBuyDomainPacket("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEmzMuWmpif2JdeB1/UA7XQumglLj3o4/qF/CisxzJyXf7JoXXmGvBDJFbCSr8Si09zKtkfoA7jyU=",
		                                     "ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEmzMuWmpif2JdeB1/UA7XQumglLj3o4/qF/CisxzJyXf7JoXXmGvBDJFbCSr8Si09zKtkfoA7jyU=", 
                                                     "teste677");
        packet.commit(null);
    }
    
    public void runTransferDomain() throws Exception
    {
        CTransferDomainPacket packet=new CTransferDomainPacket("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEmzMuWmpif2JdeB1/UA7XQumglLj3o4/qF/CisxzJyXf7JoXXmGvBDJFbCSr8Si09zKtkfoA7jyU=", 
                                                               "ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEmzMuWmpif2JdeB1/UA7XQumglLj3o4/qF/CisxzJyXf7JoXXmGvBDJFbCSr8Si09zKtkfoA7jyU=", 
                                                               "teste677", 
                                                               "ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAET9FqHJOz3KMcWtvSz/AdRsAp4NaM/ODEIva0j4xiZoVfZkC2ex1lx8qvljSuo7DLlkpQ6M7ts9g=");
        packet.commit(null);
    }
}
