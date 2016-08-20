package wallet.kernel.stress_test;

import wallet.*;
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
import wallet.network.packets.adr.CProfilePacket;
import wallet.network.packets.adr.CSealPacket;
import wallet.network.packets.ads.CNewAdPacket;
import wallet.network.packets.app.CDeployAppNetPacket;
import wallet.network.packets.app.CPublishAppPacket;
import wallet.network.packets.app.CRentAppPacket;
import wallet.network.packets.app.CUpdateAppPacket;
import wallet.network.packets.blocks.CBlockPacket;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.domains.CBuyDomainPacket;
import wallet.network.packets.domains.CRentDomainPacket;
import wallet.network.packets.domains.CSaleDomainPacket;
import wallet.network.packets.domains.CTransferDomainPacket;
import wallet.network.packets.sync.CDeliverBlocksPacket;
import wallet.network.packets.trans.CEscrowedTransSignPacket;
import wallet.network.packets.trans.CTransPacket;
import wallet.network.packets.tweets.CFollowPacket;
import wallet.network.packets.tweets.CVotePacket;
import wallet.network.packets.tweets.CNewTweetPacket;
import wallet.network.packets.tweets.CCommentPacket;
import wallet.network.packets.tweets.CUnfollowPacket;

public class CTestBattery 
{
    // Timer
    Timer timer;
    
     // Adr
    CSTAdr adr;
    
    // Ads
    CSTAds ads;
    
    // Agents
    CSTAgents agents;
    
    // delegates
    CSTDelegates delegates;
		
    public CTestBattery()  throws Exception
    {
        // Adr
        adr=new CSTAdr();
	
        // Ads
        ads=new CSTAds();
        
        // Agents
        agents=new CSTAgents();
        
        // delegates
        delegates=new CSTDelegates();
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
                adr.runTrans();
                
                // Online ?
                /*if (!UTILS.STATUS.engine_status.equals("ID_ONLINE")) return;
        
                // Run adr       
                if (UTILS.BASIC.tstamp()%2==0) 
                {
                    long r=Math.round(Math.random()*5);
                    if (r==4) 
                        adr.runSeal();
                    else
                        adr.runTrans();
                }
                
                // Run ads
                if (UTILS.BASIC.tstamp()%3==0) ads.runAds();
                
                // Run agents
                if (UTILS.BASIC.tstamp()%4==0) 
                {
                    // Random
                    long r=Math.round(Math.random()*5);
                    
                    // Subtests
                    if (r==1) agents.runDeployApp();
                    if (r==2) agents.runPublishApp();
                    if (r==3) agents.runRentApp();
                    if (r==4) agents.runUpdateApp();
                }
                
                //if (UTILS.BASIC.tstamp()%9==0) runPublishApp();
                //if (UTILS.BASIC.tstamp()%10==0) runRentApp();
                //if (UTILS.BASIC.tstamp()%11==0) runUpdateApp();
                //if (UTILS.BASIC.tstamp()%12==0) runTweet();
                //if (UTILS.BASIC.tstamp()%25==0) runSeal();*/
                
                
            }
            catch (Exception ex)
            {
                System.out.println(ex.getMessage());
            }
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
   
    
    
    
    
    
   
    
   
    
}
		
