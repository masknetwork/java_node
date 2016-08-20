package wallet.kernel.stress_test;

import java.sql.ResultSet;
import wallet.kernel.UTILS;
import wallet.network.packets.adr.CSealPacket;
import wallet.network.packets.trans.CTransPacket;

public class CSTAdr  extends CStressTest
{
    public CSTAdr()
    {
        super("adr");
    }
    
    public void runTrans() throws Exception
    {
        if (!UTILS.WALLET.isMine("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEFYpiA6utjRCs5jK7cHEuloveupOjwXJKww5QTFI9YedJuz4aOand6JtwuYMDNI7lxi7ewKA3pm8="))
            return;
        
        ResultSet rs=UTILS.DB.executeQuery("SELECT COUNT(*) AS total FROM my_adr");
        rs.next();
        long total=rs.getLong("total");
        
        CTransPacket packet;
        
        
                
        if (total>10)
        {
            String rand=this.randomAdr();
            packet=new CTransPacket("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEFYpiA6utjRCs5jK7cHEuloveupOjwXJKww5QTFI9YedJuz4aOand6JtwuYMDNI7lxi7ewKA3pm8=",
                                "ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEFYpiA6utjRCs5jK7cHEuloveupOjwXJKww5QTFI9YedJuz4aOand6JtwuYMDNI7lxi7ewKA3pm8=",
                                this.randomAdr(),
			        UTILS.BASIC.round(Math.random(), 4), "MSK",  "", "", 1);
        }
        else
        {
            String adr=UTILS.WALLET.newAddress("root", "secp224r1", "");
            
            packet=new CTransPacket("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEFYpiA6utjRCs5jK7cHEuloveupOjwXJKww5QTFI9YedJuz4aOand6JtwuYMDNI7lxi7ewKA3pm8=",
                                    "ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEFYpiA6utjRCs5jK7cHEuloveupOjwXJKww5QTFI9YedJuz4aOand6JtwuYMDNI7lxi7ewKA3pm8=",
                                    this.randomAdr(),
			            UTILS.BASIC.round(Math.random(), 4), "MSK",  "", "", 1);
        }
        
        UTILS.NETWORK.broadcast(packet);
       
    }
    
    public void runSeal() throws Exception
    {
        CSealPacket packet=new CSealPacket(this.randomAdr(), 
		                           this.randomAdr(), 
		                           10);
        
        UTILS.NETWORK.broadcast(packet);
    }
}
