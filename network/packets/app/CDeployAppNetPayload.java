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
                                long days) throws Exception
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
        this.sign();
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
         
         // Insert temp agent
         UTILS.DB.executeUpdate("INSERT INTO agents_mine (adr, "
                                                       + "globals, "
                                                       + "interface, "
                                                       + "signals, "
                                                       + "code, "
                                                       + "run, "
                                                       + "exec_log, "
                                                       + "storage, "
                                                       + "name, "
                                                       + "block, "
                                                       + "url_pass, "
                                                       + "expire, "
                                                       + "status) VALUES('"
                                                       +this.target_adr+"', '"
                                                       +this.globals+"', '"
                                                       +this.iface+"', '"
                                                       +this.signals+"', '"
                                                       +this.code+"', "
                                                       + "'', '', '', '"
                                                       +this.name+"', '"
                                                       +this.block+"', '', '"
                                                       +UTILS.BASIC.blocskFromDays(this.days)+"', "
                                                       + "'ID_TEMP')");
         
         // Load agent
         rs=s.executeQuery("SELECT * "
                           + "FROM agents_mine "
                          + "WHERE status='ID_TEMP' "
                       + "ORDER BY ID DESC");
         
         // Next
         rs.next();
         
         // Agent ID
         long aID=rs.getLong("ID");
         
         // Agent
         CAgent agent=new CAgent(aID, true);
         
         // Parse
         String r=agent.parse();
         
         // Ok ?
         if (!r.equals("ID_OK"))
             throw new Exception("Syntax errors");
         
         // Delete
         UTILS.DB.executeUpdate("DELETE FROM agents_mine "
                                + "WHERE status='ID_TEMP'");
         
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
                                                       +this.globals+"', '"
                                                       +this.iface+"', '"
                                                       +this.signals+"', '"
                                                       +this.code+"', "
                                                       + "'', "
                                                       +"'', '"
                                                       +this.name+"', '"
                                                       +this.block+"', '', '"
                                                       +UTILS.BASIC.blocskFromDays(this.days)+"', "
                                                       + "'ID_ONLINE')");
           
           // Return
  	   return new CResult(true, "Ok", "CDeployAppNetPayload", 67);
     }
}
