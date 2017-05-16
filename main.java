package wallet;

import wallet.kernel.CStatus;
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
import wallet.kernel.*;
import wallet.kernel.net_stat.consensus.CConsensus;
import wallet.kernel.net_stat.tables.CAdrTable;
import wallet.kernel.net_stat.tables.CAdsTable;
import wallet.kernel.net_stat.tables.CAssetsMktsPosTable;
import wallet.kernel.net_stat.tables.CAssetsMktsTable;
import wallet.kernel.net_stat.tables.CAssetsOwnersTable;
import wallet.kernel.net_stat.tables.CAssetsTable;
import wallet.kernel.net_stat.tables.CCommentsTable;
import wallet.kernel.net_stat.tables.CDelVotesTable;
import wallet.kernel.net_stat.tables.CDelegatesTable;
import wallet.kernel.net_stat.tables.CDomainsTable;
import wallet.kernel.net_stat.tables.CEscrowedTable;
import wallet.kernel.net_stat.tables.CFeedsBetsPosTable;
import wallet.kernel.net_stat.tables.CFeedsBetsTable;
import wallet.kernel.net_stat.tables.CFeedsBranchesTable;
import wallet.kernel.net_stat.tables.CFeedsSpecMktsPosTable;
import wallet.kernel.net_stat.tables.CFeedsSpecMktsTable;
import wallet.kernel.net_stat.tables.CFeedsTable;
import wallet.kernel.net_stat.tables.CProfilesTable;
import wallet.kernel.net_stat.tables.CTweetsFollowTable;
import wallet.kernel.net_stat.tables.CTweetsTable;
import wallet.kernel.net_stat.tables.CVotesTable;
import wallet.kernel.x34.SHA256;
import wallet.network.*;
import wallet.network.packets.CPacket;
import wallet.network.packets.CPayload;
import wallet.network.packets.adr.CProfilePacket;
import wallet.network.packets.ads.CNewAdPacket;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.domains.CBuyDomainPacket;
import wallet.network.packets.domains.CRentDomainPacket;
import wallet.network.packets.domains.CSaleDomainPacket;
import wallet.network.packets.domains.CTransferDomainPacket;
import wallet.network.packets.misc.CDelVotePacket;
import wallet.network.packets.sync.CDeliverBlocksPacket;
import wallet.network.packets.sync.CGetBlockPacket;
import wallet.network.packets.sync.CPing;
import wallet.network.packets.trans.CEscrowedTransSignPacket;
import wallet.network.packets.trans.CTransPacket;


public class main 
{
    public static void main(String[] args) throws Exception
    {
          
        
        // Security provider
	Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        
        // Settings
        CSettings settings=new CSettings();
        UTILS.SETTINGS=settings;
        
        // Error log
        CErrorLog err_log=new CErrorLog();
        UTILS.LOG=err_log;
        
        // Args
        UTILS.ARGS=new CArgs();
        UTILS.ARGS.load(args);
        
        // DB
        CDB db=new CDB();
        UTILS.DB=db;
        UTILS.DB.loadFileLoc();
        
        // Utils
        CUtils utils=new CUtils();
        UTILS.BASIC=utils;
        
        // Accounting
        CAcc acc=new CAcc();
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
        
        // Delegates
        UTILS.DELEGATES=new CDelegates();
        
        // Wallet
        UTILS.WALLET=new CWallet();
       
        // Network
        UTILS.NETWORK=new CNetwork();
        UTILS.NETWORK.start();
        
        // Current block
        CCurBlock block=new CCurBlock();
        UTILS.CBLOCK=block;
        
        // Web operations
        UTILS.WEB_OPS=new CWebOps();
        
        // Binary Options Engine
        UTILS.CRONS=new CCrons();
    
        // Rewards
        UTILS.REWARD=new CReward();
        
        // Feeds sources
        CFeedsSources fs=new CFeedsSources();
        
        // Late operations
        UTILS.ARGS.lateOp();
        
        // Sync
        UTILS.SYNC=new CSync();
        UTILS.SYNC.start();
        //UTILS.STATUS.setEngineStatus("ID_ONLINE");
        
        
        System.out.println("Wallet is up an running...");
    }
    
    
}
