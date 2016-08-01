package wallet.kernel.stress_test;

import java.sql.ResultSet;
import wallet.kernel.UTILS;
import wallet.network.packets.app.CDeployAppNetPacket;
import wallet.network.packets.app.CPublishAppPacket;
import wallet.network.packets.app.CRentAppPacket;
import wallet.network.packets.app.CUpdateAppPacket;

public class CSTAgents extends CStressTest
{
    public CSTAgents()
    {
        super("agents");
    }
    
     public void runDeployApp() throws Exception
    {
        CDeployAppNetPacket packet=new CDeployAppNetPacket(this.randomAdr(), 
                                                           this.randomAdr(), 
                                                           "Test app",
                                                           "",
                                                           "",
                                                           "",
                                                           "#start#\nmov r1, 0\nmov r2, 1",
                                                           0, 
                                                           25);
        UTILS.NETWORK.broadcast(packet);
    }
    
    public void runPublishApp() throws Exception
    {
        String target="";
        
        if (UTILS.BASIC.mtstamp()%2==0) 
            target="ID_DIR";
        else
            target="ID_STORE";
        

        // Random app
        ResultSet rs=UTILS.DB.executeQuery("SELECT * FROM agents ORDER BY RAND()");
        
        if (UTILS.DB.hasData(rs))
        {
           rs.next();
           long appID=rs.getLong("aID");
            
           CPublishAppPacket packet=new CPublishAppPacket(this.randomAdr(), 
                                                          rs.getString("adr"), 
                                                          target, 
                                                          appID, 
                                                          "ID_BUSINESS",
                                                          "App name", 
                                                          "App description", 
                                                          this.randomAdr(),
                                                          "http://www.yahoo.com", 
                                                          "http://russia-insider.com/sites/insider/files/styles/s400/public/Gold_Bar.jpg", 
                                                          "0.0.1", 
                                                           Math.random());
           UTILS.NETWORK.broadcast(packet);
        }
    }
    
    public void runRentApp() throws Exception
    {
        CRentAppPacket packet=new CRentAppPacket(this.randomAdr(), 
                                                 this.randomAdr(), 
                                                 this.randomAgent(), 
                                                 Math.round(Math.random()*10));
        UTILS.NETWORK.broadcast(packet);
    }
    
    public void runUpdateApp() throws Exception
    {
        String op="";
        
        op="ID_UNINSTALL";
        if (UTILS.BASIC.mtstamp()%2==0) op="ID_UNINSTALL";
        if (UTILS.BASIC.mtstamp()%3==0) op="ID_REMOVE_STORE";
        if (UTILS.BASIC.mtstamp()%4==0) op="ID_REMOVE_DIR";
        
        CUpdateAppPacket packet=new CUpdateAppPacket(this.randomAdr(), 
                                                     this.randomAdr(), 
                                                     this.randomAgent(), 
                                                     op, 
                                                     this.randomDays());
                          
        UTILS.NETWORK.broadcast(packet);
    }
}
