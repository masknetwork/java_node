
package wallet.network;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Timer;
import java.util.TimerTask;
import wallet.kernel.CAddress;
import wallet.kernel.CStatus;
import wallet.kernel.UTILS;
import wallet.network.packets.CBroadcastPacket;
import wallet.network.packets.CPacket;
import wallet.network.packets.blocks.CBlockPacket;
import wallet.network.packets.blocks.CBlockPayload;

public class CCurBlock 
{
   // Block
   CBlockPayload payload;
   
   // Timer
   Timer timer;
   
   public CCurBlock()
   {
       payload=new CBlockPayload();
       
       // Timer
       timer = new Timer();
       RemindTask task=new RemindTask();
       task.parent=this;
       timer.schedule(task, 0, 100000); 
   }
   
    class RemindTask extends TimerTask 
        {  
           public CCurBlock parent;
               
           @Override
           public void run() 
           {  
              if (UTILS.SETTINGS.init_blocks)
                 if (parent.payload.hash!=null) 
                   parent.broadcast();
           }
        }
   
   public void addPacket(CPacket packet)
   {
       payload.addPacket(packet);
       
       // Delete packets
       UTILS.DB.executeUpdate("DELETE FROM cur_block");
       
       // Reload current block
       for (int a=0; a<=payload.packets.size()-1; a++)
       {
           CBroadcastPacket p=(CBroadcastPacket) payload.packets.get(a);
           
           UTILS.DB.executeUpdate("INSERT INTO cur_block(tip, "
                                                  + "hash, "
                                                  + "block, "
                                                  + "tstamp) "
                                         + "VALUES('"+p.tip+"', '"+
                                                      p.hash+"', '"+
                                                      p.block+"', '"+
                                                      UTILS.BASIC.tstamp()+"')");
       }
   }
   
   public void broadcast()
   {
       try
       {
          // Finds an address as signer
          Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
          ResultSet rs=s.executeQuery("SELECT * "
                                      + "FROM my_adr AS ma "
                                      + "JOIN adr ON ma.adr=adr.adr "
                                     + "WHERE adr.balance>1");
          if (UTILS.DB.hasData(rs))
          {
             rs.next();
             String adr=rs.getString("adr");
                  
             // Sign using the first address
             payload.sign(adr);
       
             // Load payload
             CBlockPacket block=new CBlockPacket(adr);
             block.payload=UTILS.SERIAL.serialize(payload);
       
             // Sign
             block.sign();
       
             // Broadcast
             UTILS.NETWORK.broadcast(block);
       
             // New block
             this.newBlock();
          }
          
          // Close
          s.close();
       }
            catch (SQLException ex) 
       	    {  
       	       UTILS.LOG.log("SQLException", ex.getMessage(), "CCurBlock.java", 57);
            }
   }
   
   public void newBlock()
   {
       // New payload
       payload=new CBlockPayload();
       
       // Delete packets
       UTILS.DB.executeUpdate("DELETE FROM cur_block");
   }
}
