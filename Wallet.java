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
import wallet.kernel.net_stat.consensus.CConsensus;
import wallet.kernel.net_stat.tables.CAdrTable;
import wallet.kernel.x34.SHA256;
import wallet.network.*;
import wallet.network.packets.CPacket;
import wallet.network.packets.CPayload;
import wallet.network.packets.adr.CProfilePacket;
import wallet.network.packets.ads.CNewAdPacket;
import wallet.network.packets.assets.CIssueAssetPacket;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.domains.CBuyDomainPacket;

import wallet.network.packets.domains.CRentDomainPacket;
import wallet.network.packets.domains.CTransferDomainPacket;

import wallet.network.packets.shop.goods.CNewStorePacket;
import wallet.network.packets.sync.CBlockchain;
import wallet.network.packets.sync.CDeliverBlocksPacket;
import wallet.network.packets.sync.CDeliverTablePacket;
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
        //UTILS.DB.reset(); System.exit(0);
        
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
        
        // Net stat
        UTILS.NET_STAT=new CNetStat();
        UTILS.NET_STAT.start(); 
        
        // Network
        UTILS.NETWORK=new CNetwork();
        UTILS.NETWORK.start();
        
        // Wallet
        UTILS.WALLET=new CWallet();
        
        // Current block
        CCurBlock block=new CCurBlock();
        UTILS.CBLOCK=block;
        
        //UTILS.CBLOCK.startMiner(1);
        //UTILS.CBLOCK.startMiner(2);
        //UTILS.CBLOCK.startMiner(3);
        //UTILS.CBLOCK.startMiner(4);
        
        // Web operations
        UTILS.WEB_OPS=new CWebOps();
        
        CFeedsSources src=new CFeedsSources();
        
        // Binary Options Engine
        UTILS.CRONS=new CCrons();
        
        // Blocks
        UTILS.CONSENSUS=new CConsensus();
        UTILS.CONSENSUS.start();
        
        // Sync
        UTILS.SYNC=new CSync();
        //UTILS.SYNC.start();
        
       
        CTestBattery bat=new CTestBattery();
        //bat.start();
        
        CSyntaxGen sg=new CSyntaxGen("ID_INS-DIV", 2);
        sg.par_1.add("ID_REG");
        sg.par_1.add("ID_VAR");
        sg.par_2.add("ID_REG");
        sg.par_2.add("ID_VAR");
        sg.par_2.add("ID_LONG");
        sg.par_2.add("ID_DOUBLE");
        sg.generate();
      }
      catch (Exception e) 
      { 
         UTILS.LOG.log("Exception", e.getMessage(), "Wallet.java", 85);
      }
    }
    
    
}
