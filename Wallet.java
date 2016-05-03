package wallet;


import wallet.kernel.net_stat.CNetStat;
import java.math.BigInteger;
import java.security.Security;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import wallet.agents.CAgent;
import wallet.agents.VM.CCell;
import wallet.kernel.*;
import wallet.kernel.net_stat.CAdrTable;
import wallet.kernel.x34.SHA256;
import wallet.network.*;
import wallet.network.packets.CPayload;
import wallet.network.packets.assets.CIssueAssetPacket;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.domains.CBuyDomainPacket;
import wallet.network.packets.domains.CRenewDomainPacket;
import wallet.network.packets.domains.CTransferDomainPacket;
import wallet.network.packets.domains.CUpdatePriceDomainPacket;
import wallet.network.packets.sync.CBlockchain;
import wallet.network.packets.sync.CDeliverBlocksPacket;
import wallet.network.packets.trans.CTransPacket;
import wallet.network.packets.tweets.CLikePacket;
import wallet.network.packets.tweets.CNewTweetPacket;


public class Wallet 
{
    public static void main(String[] args)
    {
        try
        {
        // Security provider
	Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
                
        // Error log
        CErrorLog err_log=new CErrorLog();
        UTILS.LOG=err_log;
        
        // Console
        CConsole c=new CConsole();
        UTILS.CONSOLE=c;
        
        // Settings
        CSettings settings=new CSettings();
        UTILS.SETTINGS=settings;
        
        // DB
        CDB db=new CDB();
        UTILS.DB=db;
        //UTILS.DB.reset();
        
        // Utils
        CUtils utils=new CUtils();
        UTILS.BASIC=utils;
        
         // Serializer
        UTILS.SERIAL=new CSerializer();
        
        // Bootstrap sequence
        CBootstrap boot=new CBootstrap();
        
        UTILS.MINER_UTILS=new CCPUMinerUtils();
        
        // Status
        UTILS.STATUS=new CStatus();
        
        // AES
        UTILS.AES=new CAES();
        
        // ECC
        UTILS.ECC=new CECC();
        
        // Network
        UTILS.NETWORK=new CNetwork();
        UTILS.NETWORK.start();
        
        // Wallet
        UTILS.WALLET=new CWallet();
       
         // Net stat
         UTILS.NET_STAT=new CNetStat();
         UTILS.NET_STAT.refreshTables(0);
        
        // Current block
        CCurBlock block=new CCurBlock();
        UTILS.CBLOCK=block;
        
        UTILS.CBLOCK.startMiner(1);
        //UTILS.CBLOCK.startMiner(2);
        
        // Web operations
        UTILS.WEB_OPS=new CWebOps();
        
        CFeedsSources src=new CFeedsSources();
        
        // Binary Options Engine
        UTILS.CRONS=new CCrons();
       
        //CDeliverBlocksPacket packet=new CDeliverBlocksPacket(1, 139);
        //packet.commit(null);
       
        //CTestBattery bat=new CTestBattery();
        //bat.start();
        
        
       
      }
      catch (Exception e) 
      { 
         UTILS.LOG.log("Exception", e.getMessage(), "Wallet.java", 85);
      }
    }
    
    
}
