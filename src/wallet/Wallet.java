package wallet;

import wallet.network.packets.assets.*;
import wallet.kernel.net_stat.CNetStat;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.Security;
import java.sql.ResultSet;
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
import wallet.kernel.stress_test.CTestBattery;
import wallet.kernel.x34.SHA256;
import wallet.network.*;
import wallet.network.packets.CPacket;
import wallet.network.packets.CPayload;
import wallet.network.packets.adr.CProfilePacket;
import wallet.network.packets.ads.CNewAdPacket;
import wallet.network.packets.app.CDeployAppNetPacket;
import wallet.network.packets.assets.*;
import wallet.network.packets.assets.CIssueMoreAssetsPacket;
import wallet.network.packets.assets.reg_mkts.CCloseRegMarketPosPacket;
import wallet.network.packets.assets.reg_mkts.CNewRegMarketPacket;
import wallet.network.packets.assets.reg_mkts.CNewRegMarketPosPacket;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.domains.CBuyDomainPacket;
import wallet.network.packets.domains.CRentDomainPacket;
import wallet.network.packets.domains.CSaleDomainPacket;
import wallet.network.packets.domains.CTransferDomainPacket;
import wallet.network.packets.sync.CBlockchain;
import wallet.network.packets.sync.CDeliverBlocksPacket;
import wallet.network.packets.sync.CDeliverTablePacket;
import wallet.network.packets.trans.CEscrowedTransSignPacket;
import wallet.network.packets.trans.CTransPacket;
import wallet.network.packets.tweets.CFollowPacket;
import wallet.network.packets.tweets.CVotePacket;
import wallet.network.packets.tweets.CNewTweetPacket;
import wallet.network.packets.tweets.CCommentPacket;
import wallet.network.packets.tweets.CCommentPayload;
import wallet.network.packets.tweets.CUnfollowPacket;


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
        
        // Settings
        CSettings settings=new CSettings();
        UTILS.SETTINGS=settings;
        
        // Args
        UTILS.ARGS=new CArgs();
        UTILS.ARGS.load(args);
        
        // DB
        CDB db=new CDB();
        UTILS.DB=db;
     
        
        // Utils
        CUtils utils=new CUtils();
        UTILS.BASIC=utils;
        
        // Accounting
        CAccounting acc=new CAccounting();
        UTILS.ACC=acc;
        
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
        
        // Network
        UTILS.NETWORK=new CNetwork();
        UTILS.NETWORK.start();
        
        // Wallet
        UTILS.WALLET=new CWallet();
        
        // Current block
        CCurBlock block=new CCurBlock();
        UTILS.CBLOCK=block;
        
        UTILS.CBLOCK.startMiner(1);
        //UTILS.CBLOCK.startMiner(2);
        //UTILS.CBLOCK.startMiner(3);
        //UTILS.CBLOCK.startMiner(4);
        
        // Web operations
        UTILS.WEB_OPS=new CWebOps();
        
        // Binary Options Engine
        UTILS.CRONS=new CCrons();
        
        // Blocks
        UTILS.CONSENSUS=new CConsensus();
        UTILS.CONSENSUS.start();
        
        // Feeds sources
        CFeedsSources feed_src=new CFeedsSources();
        
        
        // Sync
        //UTILS.SYNC=new CSync();
        //UTILS.SYNC.start();
        UTILS.STATUS.setEngineStatus("ID_ONLINE");
        
        UTILS.ARGS.lateOp();
        
        CTestBattery bat=new CTestBattery();
        //bat.start();
        //bat.runFeeds();
        
        
        
        }
      catch (Exception e) 
      { 
         UTILS.LOG.log("Exception", e.getMessage(), "Wallet.java", 85);
      }
    }
    
    
}
