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
    String globals="";
    
    // Interface
    String iface="";
    
    // Signals
    String signals="";
    
    // Code
    String code="";
    
    // Run period
    long run_period=0;
    
    // Days
    long days=0;
    
    // Name
    String name="";
    
    
    public CDeployAppNetPayload(String adr, 
                                String name,
                                String globals,
                                String iface,
                                String signals,
                                String code,
                                long run_period, 
                                long days) throws Exception
    {
        // Constructor
        super(adr);
        
        // App ID
        this.appID=UTILS.BASIC.getID();
        
        // Globals
        this.globals=globals;
        
        // Interface
        this.iface=iface;
        
        // Signals
        this.signals=signals;
        
        // Code
        this.code=code;
        
        // Run period
        this.run_period=run_period;
        
        // Days
        this.days=days;
        
        // Name
        this.name=name;
        
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
    
     public void check(CBlockPayload block) throws Exception
     {
         // Super class
         super.check(block);
        
         // Already exist
         if (!UTILS.BASIC.validID(this.appID))
             throw new Exception ("Invalid app ID (CDeployAppNetPayload.java)");
         
         // Sealed address ?
         if (UTILS.BASIC.isSealed(this.target_adr))
            throw new Exception ("Sealed address");
         
         // Globals
         if (this.globals.length()>100000)
             throw new Exception ("Invalid globals");
         
         // Interface
         if (this.iface.length()>100000)
             throw new Exception ("Invalid globals");
         
         // Signals
         if (this.signals.length()>100000)
             throw new Exception ("Invalid globals");
         
         // Code
         if (this.code.length()>100000)
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
         
     }
     
      public void commit(CBlockPayload block) throws Exception
      {
           // Commit parent
 	   super.commit(block);
 	   
           // Remove any app
           UTILS.DB.executeUpdate("DELETE FROM agents "
                                      + "WHERE adr='"+this.target_adr+"'");
           
           // Insert 
           UTILS.DB.executeUpdate("INSERT INTO agents "
                                        + "SET aID='"+this.appID+"',"
                                            + "adr='"+this.target_adr+"', "
                                            + "owner='"+this.target_adr+"', "
                                            + "globals='"+UTILS.BASIC.base64_encode(this.globals)+"', "
                                            + "interface='', "
                                            + "signals='', "
                                            + "code='"+UTILS.BASIC.base64_encode(this.code)+"', "
                                            + "exec_log='', "
                                            + "name='"+UTILS.BASIC.base64_encode(this.name)+"', "
                                            + "block='"+this.block+"', "
                                            + "expire='"+(this.block+this.days*1440)+"', "
                                            + "status='ID_ONLINE'");
        
     }
}
