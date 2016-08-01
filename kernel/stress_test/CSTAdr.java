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
        ResultSet rs=UTILS.DB.executeQuery("SELECT COUNT(*) AS total FROM adr");
        rs.next();
        long total=rs.getLong("total");
        
        CTransPacket packet;
                
        if (total>1000)
        packet=new CTransPacket("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEmzMuWmpif2JdeB1/UA7XQumglLj3o4/qF/CisxzJyXf7JoXXmGvBDJFbCSr8Si09zKtkfoA7jyU=",
                                "ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEmzMuWmpif2JdeB1/UA7XQumglLj3o4/qF/CisxzJyXf7JoXXmGvBDJFbCSr8Si09zKtkfoA7jyU=",
                                this.randomAdr(),
			        UTILS.BASIC.round(Math.random(), 4), "MSK",  "", "");
        else
        {
            String adr=UTILS.WALLET.newAddress("root", "secp224r1", "");
            
            packet=new CTransPacket("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEmzMuWmpif2JdeB1/UA7XQumglLj3o4/qF/CisxzJyXf7JoXXmGvBDJFbCSr8Si09zKtkfoA7jyU=",
                                    "ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEmzMuWmpif2JdeB1/UA7XQumglLj3o4/qF/CisxzJyXf7JoXXmGvBDJFbCSr8Si09zKtkfoA7jyU=",
                                    adr,
			            UTILS.BASIC.round(Math.random(), 4), "MSK",  "", "");
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
