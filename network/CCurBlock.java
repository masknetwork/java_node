
package wallet.network;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Timer;
import java.util.TimerTask;
import wallet.kernel.CAddress;
import wallet.kernel.CCPUMiner;
import wallet.kernel.CStatus;
import wallet.kernel.UTILS;
import wallet.network.packets.CBroadcastPacket;
import wallet.network.packets.CPacket;
import wallet.network.packets.blocks.CBlockPacket;
import wallet.network.packets.blocks.CBlockPayload;

public class CCurBlock 
{
   // Block payload
   public CBlockPayload payload;
   
   // Bock
   public CBlockPacket block;
   
   // Timer
   public Timer timer;
   
   // Block hash
   public String block_hash;
   
   // Actual block hash
   public String payload_hash;
   
  // Dificulty
   public BigInteger dif=null;
   
   // Last tstamp
   public long last_tstamp;
   
   // Signer balance
   public long balance=1;
   
   // Signer
   public String signer="";
   
   // Signer balance
   public long signer_balance=0;
   
   // Nonce
   public long nonce=0;
   
   // CPU miner
   public CCPUMiner miner_1;
   public CCPUMiner miner_2;
   public CCPUMiner miner_3;
   public CCPUMiner miner_4;
   public CCPUMiner miner_5;
   public CCPUMiner miner_6;
   public CCPUMiner miner_7;
   public CCPUMiner miner_8;
   
   // Block time
   public long block_time=60;
   
   // Retarget difficulty
   public long retarget=10;
   
   // Retarget difficulty step
   public long retarget_step=1;
   
   // Peers number
   public long peers=0;
 
   
   public CCurBlock() throws SQLException
   {
       // Load network status
       Statement s=UTILS.DB.getStatement();
       
       // Load
       ResultSet rs=s.executeQuery("SELECT * FROM net_stat");
       
       // Next
       rs.next();
       
       // Start CPU miner
       miner_1=new CCPUMiner();
       miner_2=new CCPUMiner();
       miner_3=new CCPUMiner();
       miner_4=new CCPUMiner();
       miner_5=new CCPUMiner();
       miner_6=new CCPUMiner();
       miner_7=new CCPUMiner();
       miner_8=new CCPUMiner();
      
       // Payload
       payload=new CBlockPayload(UTILS.NET_STAT.last_block);
       
       // New hash
       this.payload_hash=UTILS.BASIC.hash(UTILS.SERIAL.serialize(this.payload));
       
       // Set signer
       this.setSigner();
       
       // Close
       s.close();
   }
 
   
   public void addPacket(CPacket packet)
   {
       if (this.nonce==0) 
       {
           // Add packet
           payload.addPacket(packet);
           
           // Sign using the first address
           payload.sign(this.signer);
           
           // New hash
           this.payload_hash=UTILS.BASIC.hash(UTILS.SERIAL.serialize(this.payload));
       }
   }
   
   public void setNonce(long nonce)
   {
      this.payload.hash();
      this.nonce=nonce;
   }
   
   public void setSigner() throws SQLException
   {
       // Difficulty
       Statement s=UTILS.DB.getStatement();
       
       // Load
       ResultSet rs=s.executeQuery("SELECT * "
                                   + "FROM my_adr "
                                  + "WHERE mine>0");
       
       // Next
       if (UTILS.DB.hasData(rs)) 
       {
           // Next
           rs.next();
           
           // Set miner
           this.signer=rs.getString("adr");
       }
       
       // Close
       s.close();
   }
   
   public void broadcast()
   {
       
       try
       {
          // Load payload
          block=new CBlockPacket(this.signer);
         
          // Serialize
          block.payload=UTILS.SERIAL.serialize(payload);
          
          // Payload hash
          block.payload_hash=this.payload_hash;
          
          // Tstamp
          block.tstamp=this.last_tstamp;
          
          // Nonce
          block.nonce=this.nonce;
       
          // Sign
          block.sign(this.block_hash);
       
          // Broadcast
          UTILS.NETWORK.broadcast(block);  
       }
       catch (Exception e) 
       { 
		UTILS.LOG.log("Exception", e.getMessage(), "CCurBlock.java", 182); 
       }
   }
   
   public void newBlock(long block, 
                        String prev_hash,
                        long last_tstamp) throws SQLException
   {
       // New block
       UTILS.NET_STAT.last_block_hash=prev_hash;
                
       // New block number
       UTILS.NET_STAT.last_block=block;
       
       // Last tstamp
       UTILS.NET_STAT.last_block_tstamp=last_tstamp;
       
       // Payload hash
       this.payload_hash=UTILS.BASIC.hash(UTILS.SERIAL.serialize(this.payload));
       
       // Difficulty
       Statement s=UTILS.DB.getStatement();
       
       // Load
       ResultSet rs=s.executeQuery("SELECT *  "
                                   + "FROM blocks "
                                  + "WHERE block>="+(UTILS.NET_STAT.last_block-this.retarget)+" ORDER BY block ASC");
       
       // Next
       rs.next();
       
       // Total
       long tstamp=rs.getLong("tstamp");
       
       // Change dificulty ?
       if (tstamp>last_tstamp-(this.retarget*60))
           UTILS.NET_STAT.net_dif=UTILS.NET_STAT.net_dif-UTILS.NET_STAT.net_dif/100;
      
       else if (tstamp<last_tstamp-(this.retarget*60))
         UTILS.NET_STAT.net_dif=UTILS.NET_STAT.net_dif+UTILS.NET_STAT.net_dif/100;
       
       System.out.println(UTILS.NET_STAT.net_dif+", "+(last_tstamp-(this.retarget*60)));
       
       // Update net stat
       UTILS.DB.executeUpdate("UPDATE net_stat "
                                        + "SET last_block='"+UTILS.NET_STAT.last_block + "', "
                                            + "last_hash='"+UTILS.NET_STAT.last_block_hash+"', "
                                            + "net_dif='"+UTILS.NET_STAT.net_dif+"'");
       
       // New block
       this.payload=new CBlockPayload(UTILS.NET_STAT.last_block+1);
       
      
       // Nonce
       this.nonce=0;
       
       // Close
       s.close();
   }
   
   
}
