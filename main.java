package wallet;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import wallet.kernel.CStatus;
import wallet.kernel.net_stat.CNetStat;
import java.security.Security;
import java.sql.ResultSet;
import wallet.kernel.*;
import wallet.network.CCurBlock;
import wallet.network.CNetwork;
import wallet.network.CStressTest;
import wallet.network.packets.CBroadcastPacket;
import wallet.network.packets.blocks.CBlockPacket;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.trade.bets.CBuyBetPacket;
import wallet.network.packets.trade.bets.CNewBetPacket;
import wallet.network.packets.trade.feeds.CNewFeedComponentPacket;
import wallet.network.packets.trade.feeds.CNewFeedPacket;
import wallet.network.packets.trade.speculative.CCloseMarketPacket;
import wallet.network.packets.trade.speculative.CClosePosPacket;
import wallet.network.packets.trade.speculative.CNewMarginMarketPacket;
import wallet.network.packets.trade.speculative.CNewSpecMarketPosPacket;
import wallet.network.packets.trade.speculative.CWthFundsPacket;

public class main 
{
    public static void main(String[] args) throws Exception
    {
        try
        {
            // Security provider
	    Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        
            // Constants
            UTILS.CONSTANTS=new CConstants();
            
            // Settings
            CSettings settings=new CSettings();
            UTILS.SETTINGS=settings;
        
            // Args
            UTILS.ARGS=new CArgs();
            UTILS.ARGS.load(args);
        
            // Open port ?
            UTILS.CONSTANTS.portBusy();
            
            // DB
            CDB db=new CDB();
            UTILS.DB=db;
            UTILS.DB.loaddFileLoc();
        
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
        
            // Spec positions
            UTILS.SPEC_POS=new CSpecPos();
        
            // Binary options
            UTILS.OPTIONS=new COptions();
        
            // Feeds sources
            CFeedsSources fs=new CFeedsSources();
        
            // Late operations
            UTILS.ARGS.lateOp();
        
            // Sync
            UTILS.SYNC=new CSync();
        
            if (UTILS.SETTINGS.seed_mode)
                UTILS.STATUS.setEngineStatus("ID_ONLINE");
            else
                UTILS.SYNC.start();
        
            if (UTILS.SETTINGS.start_mining)
               UTILS.CBLOCK.startMiners(1);
            
            System.out.println("Wallet is up an running...");
        }
        catch (Exception ex)
        {
            System.out.println("Exception durint startup ("+ex.getMessage()+"). Exiting !!!");
            System.exit(0);
        }
    }
    
}
