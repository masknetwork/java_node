
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
   
   // Last block number
   public long prev_block_no;
   
    // Prev hash
   public String prev_hash;
   
   // Prev tstamp
   public long prev_tstamp;
   
   // Actual block hash
   public String hash;
   
   // Network Dificulty
   public BigInteger net_dif=null;
   
   // Dificulty
   public BigInteger dif=null;
   
   // Signer balance
   public long balance=1;
   
   // Signer
   public String signer="ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAESw6vT5Oz43xw/6Wa7tt0RrUQ9Bj4c7Qhr/gj5XZmMLp1ALqUG46+VOiLLII7ua5mzfuylwHaoLU=";
   
   // Nonce
   public long nonce=0;
   
   // CPU miner
   public CCPUMiner miner;
   
   // Block time
   public long block_time=60;
   
   // Retarget difficulty
   public long retarget=10;
   
   // Retarget difficulty step
   public long retarget_step=1;
   
   public CCurBlock() throws SQLException
   {
       // Load network status
       Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
       
       // Load
       ResultSet rs=s.executeQuery("SELECT * FROM net_stat");
       
       // Next
       rs.next();
       
       // Last block
       this.prev_block_no=rs.getLong("last_block");
       
       // Prev hash
       this.prev_hash=rs.getString("last_hash");
       
       // Network minimum dificulty
       this.net_dif=new BigInteger(rs.getString("net_dif"));
       
       // Last timestamp
       this.prev_tstamp=rs.getLong("last_tstamp");
      
       // Dificulty
       this.dif=this.net_dif.multiply(BigInteger.valueOf(this.balance));
       
       // Last timestamp
       this.dif=this.net_dif.multiply(BigInteger.valueOf(this.balance));
       
       // Start CPU miner
       miner=new CCPUMiner();
      
       // Payload
       payload=new CBlockPayload(prev_block_no, 
                                 prev_hash, 
                                 this.net_dif.toString());
   }
 
   
   public void addPacket(CPacket packet)
   {
       if (this.nonce==0) 
       {
           // Add packet
           payload.addPacket(packet);
           
           // New hash
           this.hash=this.payload.hash;
       }
   }
   
   public void setNonce(long nonce)
   {
      this.payload.nonce=nonce;
      this.payload.hash();
   }
   
   public void setSigner()
   {
       
   }
   
   public void broadcast()
   {
       // Payload timestamp
       payload.tstamp=UTILS.BASIC.tstamp();
       
       // Sign using the first address
       payload.sign(this.signer);
       
       // Load payload
       block=new CBlockPacket(this.signer);
       block.payload=UTILS.SERIAL.serialize(payload);
       
       // Sign
       block.sign();
       
       // Broadcast
       UTILS.NETWORK.broadcast(block);  
       
      
   }
   
   public void newBlock(String prev_hash, long prev_tstamp) throws SQLException
   {
       // New block
       this.prev_hash=prev_hash;
                
       // New block number
       this.prev_block_no++;
                
       // Last timestamp
       this.prev_tstamp=prev_tstamp;
       
       // Difficulty
       Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
       
       // Load
       ResultSet rs=s.executeQuery("SELECT COUNT(*) AS total "
                                   + "FROM blocks "
                                  + "WHERE tstamp>"+(this.prev_tstamp-(this.block_time*this.retarget)));
       
       // Next
       rs.next();
       
       // Total
       long blocks=rs.getLong("total");
       
       // Change dificulty ?
       if (blocks>(this.retarget+1))
       {
          this.net_dif=this.net_dif.subtract(this.net_dif.divide(BigInteger.valueOf(100)));
          System.out.println("dificulty increased to "+UTILS.BASIC.formatDif(this.net_dif.toString())+" ("+blocks+")");
       }
       else if (blocks<this.retarget)
       {
         this.net_dif=this.net_dif.add(this.net_dif.divide(BigInteger.valueOf(100)));
         System.out.println("dificulty decreased to "+UTILS.BASIC.formatDif(this.net_dif.toString())+" ("+blocks+")");
       }
       else System.out.println("dificulty stays the same to "+UTILS.BASIC.formatDif(this.net_dif.toString())+" ("+blocks+")");
      
       // Update net stat
       UTILS.DB.executeUpdate("UPDATE net_stat "
                                        + "SET last_block='"+this.prev_block_no + "', "
                                            + "last_hash='"+prev_hash+"', "
                                            + "last_tstamp='"+prev_tstamp+"', "
                                            + "net_dif='"+this.net_dif+"'");
       
       // New block
       this.payload=new CBlockPayload(this.prev_block_no, 
                                      this.prev_hash, 
                                      this.net_dif.toString());
       
       // Adjust dif
       this.dif=this.net_dif;
        
       // Nonce
       this.nonce=0;
   }
   
}
