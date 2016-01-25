package wallet.kernel;

import java.text.DecimalFormat;
import wallet.network.*;

public class UTILS 
{
    // Write directory
    public static String WRITEDIR;    
    
    // Error log
    public static CErrorLog LOG;
    
    // Database
    public static CDB DB;
    
    // Settings
    public static CSettings SETTINGS;
    
    // Console
    public static CConsole CONSOLE;
    
    // Utils
    public static CUtils BASIC;
    
    // Wallet
    public static CWallet WALLET;
    
   // AES
    public static CAES AES;
    
    // ECC
    public static CECC ECC;
    
    // Network
    public static CNetwork NETWORK;
    
    // Serializer
    public static CSerializer SERIAL;
    
    // Status
    public static CStatus STATUS;
    
    // Current block
    public static CCurBlock CBLOCK;
    
    // Emailer
    public static CEmail EMAIL;
    
    // Net stat
    public static CNetStat NET_STAT;
    
    // Log queries
    public static boolean LOG_QUERIES=false;
    
    // Binary options engine
    public static COptions BIN_OPTIONS;
    
    // Formatter
    public static DecimalFormat FORMAT = new DecimalFormat("#.####");
    public static DecimalFormat FORMAT_8 = new DecimalFormat("#.########");
    
    // Root
    public static ThreadGroup ROOT;
 
}
