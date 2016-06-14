package wallet.network.packets.app;

import java.sql.ResultSet;
import java.sql.Statement;
import wallet.agents.CAgent;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CPayload;
import wallet.network.packets.blocks.CBlockPayload;

public class CDeployAppNetPayload extends CPayload
{
    // App ID
    long appID;
    
    // Globals
    String globals;
    
    // Interface
    String iface;
    
    // Signals
    String signals;
    
    // Code
    String code;
    
    // Run period
    long run_period;
    
    // Days
    long days;
    
    // Name
    String name;
    
    
    public CDeployAppNetPayload(String adr, 
                                long appID, 
                                long run_period, 
                                long days,
                                String sig) throws Exception
    {
        // Constructor
        super(adr);
        
        // Statement 
        Statement s=UTILS.DB.getStatement();
        
        // Result
        ResultSet rs=s.executeQuery("SELECT * "
                                    + "FROM agents_mine "
                                   + "WHERE ID='"+appID+"'");
        
        // No app ?
        if (!UTILS.DB.hasData(rs))
            throw new Exception ("Invalid app ID");
        
        // Next
        rs.next();
        
        // App ID
        this.appID=UTILS.BASIC.getID();
        
        // Globals
        this.globals=rs.getString("globals");
        
        // Interface
        this.iface=rs.getString("interface");
        
        // Signals
        this.signals=rs.getString("signals");
        
        // Code
        this.code=rs.getString("code");
        
        // Run period
        this.run_period=run_period;
        
        // Days
        this.days=days;
        
        // Name
        this.name=rs.getString("name");
        
        // Hash
        hash=UTILS.BASIC.hash(this.getHash()+
                              this.appID+
                              this.globals+
                              this.iface+
                              this.signals+
                              this.code+
                              this.run_period+
                              this.days+
                              this.name);
        
        // Sign
        this.sign(sig);
    }
    
     public CResult check(CBlockPayload block) throws Exception
     {
         // Super class
         CResult res=super.check(block);
         if (res.passed==false) return res;
         
         // Statement
         Statement s=UTILS.DB.getStatement();
         
         // Result
         ResultSet rs=s.executeQuery("SELECT * "
                                     + "FROM agents "
                                    + "WHERE aID='"+this.appID+"'");
         
         // Already exist
         if (UTILS.DB.hasData(rs))
             throw new Exception ("Invalid signature (CDeployAppNetPayload.java)");
         
         // Sealed app installed ?
         rs=s.executeQuery("SELECT * "
                           + "FROM agents "
                          + "WHERE adr='"+this.target_adr+"' "
                            + "AND sealed>"+UTILS.NET_STAT.last_block);
         
         if (UTILS.DB.hasData(rs))
            throw new Exception ("Sealed application already installed");
         
         // Globals
         if (this.globals.length()>25000)
             throw new Exception ("Invalid globals");
         
         // Interface
         if (this.iface.length()>25000)
             throw new Exception ("Invalid globals");
         
         // Signals
         if (this.signals.length()>25000)
             throw new Exception ("Invalid globals");
         
         // Code
         if (this.code.length()>25000)
             throw new Exception ("Invalid globals");
         
         // Run period
         if (this.run_period<0)
             throw new Exception ("Invalid run period");
         
         // Days
         if (this.days<1)
             throw new Exception ("Invalid days");
         
         // Name
         if (!UTILS.BASIC.isTitle(this.name))
             throw new Exception ("Invalid name");
         
         // Hash
         String h=UTILS.BASIC.hash(this.getHash()+
                                   this.appID+
                                   this.globals+
                                   this.iface+
                                   this.signals+
                                   this.code+
                                   this.run_period+
                                   this.days+
                                   this.name);
         
         // Hash valid
         if (!this.hash.equals(h))
             throw new Exception("Invalid hash (CDeployAppNetPayload.java)");
         
         // Return
  	return new CResult(true, "Ok", "CDeployAppNetPayload", 67);
     }
     
      public CResult commit(CBlockPayload block) throws Exception
      {
           // Commit parent
 	   CResult res=super.commit(block);
 	   if (res.passed==false) return res;
 	   
           // Remove any app
           UTILS.DB.executeUpdate("DELETE FROM agents "
                                      + "WHERE adr='"+this.target_adr+"'");
           
           // Insert 
           UTILS.DB.executeUpdate("INSERT INTO agents (aID,"
                                                       + "adr, "
                                                       + "owner, "
                                                       + "globals, "
                                                       + "interface, "
                                                       + "signals, "
                                                       + "code, "
                                                       + "exec_log, "
                                                       + "storage, "
                                                       + "name, "
                                                       + "block, "
                                                       + "url_pass, "
                                                       + "expire, "
                                                       + "status) VALUES('"
                                                       +this.appID+"', '"
                                                       +this.target_adr+"', '"
                                                       +this.target_adr+"', '"
                                                       +UTILS.BASIC.base64_encode(this.globals)+"', '"
                                                       +UTILS.BASIC.base64_encode(this.iface)+"', '"
                                                       +UTILS.BASIC.base64_encode(this.signals)+"', '"
                                                       +UTILS.BASIC.base64_encode(this.code)+"', "
                                                       + "'', "
                                                       +"'', '"
                                                       +UTILS.BASIC.base64_encode(this.name)+"', '"
                                                       +this.block+"', '', '"
                                                       +UTILS.BASIC.blocskFromDays(this.days)+"', "
                                                       + "'ID_ONLINE')");
           
           // Return
  	   return new CResult(true, "Ok", "CDeployAppNetPayload", 67);
     }
}
