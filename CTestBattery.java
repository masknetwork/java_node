package wallet;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import wallet.kernel.CAddress;
import wallet.kernel.CCrons;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.ads.CNewAdPacket;
import wallet.network.packets.blocks.CBlockPacket;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.domains.CRentDomainPacket;
import wallet.network.packets.sync.CDeliverBlocksPacket;
import wallet.network.packets.trans.CTransPacket;

public class CTestBattery 
{
    // Timer
    Timer timer;
		
    public CTestBattery()  throws Exception
    {
	
    }
    
    public void start()
    {
         // Start timer
	timer = new Timer();
        RemindTask task=new RemindTask();
        timer.schedule(task, 0, 1000);
    }
	    
       
    class RemindTask extends TimerTask  
    {    
	@Override
	public void run() 
        {  
            try
            {
                // Online ?
                if (!UTILS.STATUS.engine_status.equals("ID_ONLINE")) return;
        
               
               runDomains();
            }
            catch (Exception ex)
            {
                System.out.println("Error");
            }
        }
    }
    
    public void runTrans() throws Exception
    {
        // Online ?
        if (!UTILS.STATUS.engine_status.equals("ID_ONLINE")) return;
        
        String adr=UTILS.WALLET.newAddress("root", "secp224r1", "");
        
        CTransPacket packet=new CTransPacket("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEmzMuWmpif2JdeB1/UA7XQumglLj3o4/qF/CisxzJyXf7JoXXmGvBDJFbCSr8Si09zKtkfoA7jyU=", 
			                     "ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEmzMuWmpif2JdeB1/UA7XQumglLj3o4/qF/CisxzJyXf7JoXXmGvBDJFbCSr8Si09zKtkfoA7jyU=", 
			                     adr, 
			                     UTILS.BASIC.round(Math.random(), 4)*3, "MSK",  "", "", "", "", "");
        UTILS.NETWORK.broadcast(packet);
    }
    
    public void runAds() throws Exception
    {
        CNewAdPacket packet=new CNewAdPacket("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEmzMuWmpif2JdeB1/UA7XQumglLj3o4/qF/CisxzJyXf7JoXXmGvBDJFbCSr8Si09zKtkfoA7jyU=", 
		                             "ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEmzMuWmpif2JdeB1/UA7XQumglLj3o4/qF/CisxzJyXf7JoXXmGvBDJFbCSr8Si09zKtkfoA7jyU=", 
		                             "RO", 
		                             Math.round(Math.random()*10), 
		                             Math.random()/10, 
		                             "Ad test", 
		                             "This is a test ad. This is a test ad. This is a test ad. ", 
		                             "http://www.yahoo.com",
                                             "",
                                             "");
         UTILS.NETWORK.broadcast(packet);
    }
    
    public void runDomains() throws Exception
    {
        CRentDomainPacket packet=new CRentDomainPacket("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEmzMuWmpif2JdeB1/UA7XQumglLj3o4/qF/CisxzJyXf7JoXXmGvBDJFbCSr8Si09zKtkfoA7jyU=", 
                                                       "ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEmzMuWmpif2JdeB1/UA7XQumglLj3o4/qF/CisxzJyXf7JoXXmGvBDJFbCSr8Si09zKtkfoA7jyU=", 
                                                       "teste"+String.valueOf(Math.round(Math.random()*1000)), 
                                                       100,
                                                       "",
                                                       "");
         UTILS.NETWORK.broadcast(packet);
    }
}
		
