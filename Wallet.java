package wallet;

import java.math.BigInteger;
import java.security.Security;
import java.sql.SQLException;
import java.util.Date;
import java.util.Random;
import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.adr.CReqInterestPacket;
import wallet.network.packets.assets.CIssueAssetPacket;
import wallet.network.packets.blocks.CBlockPayload;
import wallet.network.packets.domains.CBuyDomainPacket;
import wallet.network.packets.domains.CRenewDomainPacket;
import wallet.network.packets.domains.CTransferDomainPacket;
import wallet.network.packets.domains.CUpdatePriceDomainPacket;
import wallet.network.packets.trans.CTransPacket;
import wallet.network.packets.feeds.*;
import wallet.network.packets.markets.assets.automated.*;
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
        
        // Utils
        CUtils utils=new CUtils();
        UTILS.BASIC=utils;
        
       
        
        // Bootstrap sequence
        CBootstrap boot=new CBootstrap();
        
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
        
        // Serializer
        UTILS.SERIAL=new CSerializer();
        
         // Net stat
        UTILS.NET_STAT=new CNetStat();
        UTILS.NET_STAT.refreshTables(0);
        
        // Current block
        CCurBlock block=new CCurBlock();
        UTILS.CBLOCK=block;
        
        UTILS.CBLOCK.miner_1.start();
        
        
        // Web operations
        CWebOps ops=new CWebOps();
        
        CFeedsSources src=new CFeedsSources();
        
        // Binary Options Engine
        UTILS.BIN_OPTIONS=new COptions();
        
        
      }
      catch (Exception e) 
      { 
         UTILS.LOG.log("Exception", e.getMessage(), "Wallet.java", 85);
      }
    }
    
    
}
