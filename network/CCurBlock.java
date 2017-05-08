
// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
   public ArrayList<CCPUMiner> miners;
   
   // Miners
   public long miners_no=0;
   
   // Block time
   public long block_time=20;
   
   // Retarget difficulty
   public long retarget=10;
   
   // Retarget difficulty step
   public long retarget_step=1;
   
   // Peers number
   public long peers=0;

   
   public CCurBlock() throws Exception
   {
       // Load
       ResultSet rs=UTILS.DB.executeQuery("SELECT * FROM net_stat");
       
       // Next
       rs.next();
       
       // Set signer
       this.setSigner();
       
       // Payload
       payload=new CBlockPayload(this.signer);
       
       // New hash
       this.payload_hash=UTILS.BASIC.hash(UTILS.SERIAL.serialize(this.payload));
       
       // Net dif
       UTILS.NET_STAT.setDifficulty(this.getNewDif(UTILS.NET_STAT.last_block_hash));
       
       // Reset
       UTILS.DB.executeUpdate("UPDATE web_sys_data "
                               + "SET mining_threads=0, "
                                   + "cpu_1_power=0, "
                                   + "cpu_2_power=0, "
                                   + "cpu_3_power=0, "
                                   + "cpu_4_power=0, "
                                   + "cpu_5_power=0, "
                                   + "cpu_6_power=0, "
                                   + "cpu_7_power=0, "
                                   + "cpu_8_power=0, "
                                   + "cpu_9_power=0, "
                                   + "cpu_10_power=0, "
                                   + "cpu_11_power=0, "
                                   + "cpu_12_power=0, "
                                   + "cpu_13_power=0, "
                                   + "cpu_14_power=0, "
                                   + "cpu_15_power=0, "
                                   + "cpu_16_power=0, "
                                   + "cpu_17_power=0, "
                                   + "cpu_18_power=0, "
                                   + "cpu_19_power=0, "
                                   + "cpu_20_power=0, "
                                   + "cpu_21_power=0, "
                                   + "cpu_22_power=0, "
                                   + "cpu_23_power=0, "
                                   + "cpu_24_power=0");
  
   }
 
   public void addMiner() throws Exception
   {
       // Miners number
       this.miners_no++;
       
       // New miner
       CCPUMiner miner=new CCPUMiner(this.miners_no);
       
       // Miners
       this.miners.add(miner);
       
       // Start
       miner.start();
       
       // Threads
       UTILS.DB.executeUpdate("UPDATE web_sys_data "
                               + "SET mining_threads=mining_threads+1");
   }
   
   public void addPacket(CBroadcastPacket packet) throws Exception
   {
       if (this.nonce==0 && !this.signer.equals("")) 
       {
           // Add packet
           payload.addPacket(packet);
           
           // Sign using the first address
           payload.sign(this.signer);
           
           // New hash
           this.payload_hash=UTILS.BASIC.hash(UTILS.SERIAL.serialize(this.payload));
       }
   }
   
   public void setNonce(long nonce) throws Exception
   {
      this.nonce=nonce;
   }
   
   public void setSigner()  throws Exception
   {
       // Load delegate
       ResultSet rs=UTILS.DB.executeQuery("SELECT * FROM net_stat");
              
       // Next
       rs.next();
              
       // Set miner
       this.signer=rs.getString("delegate");
              
       // Next
       if (!this.signer.equals("")) 
       {
            // Signer power
            this.signer_balance=UTILS.DELEGATES.getPower(signer);
              
            // Payload
            if (this.payload!=null) 
                this.payload.target_adr=signer;
       }
       else
       {
            this.signer="";
            this.signer_balance=0;
       }
   }
   
   public void broadcast() throws Exception
   {
       try
       {
          if (!this.signer.equals(""))
          {
             // Load payload
             block=new CBlockPacket(this.signer, this.signer_balance);
         
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
             
             // New signer
             this.setSigner();
          }
          else 
          {
              UTILS.LOG.log("Error : ", "No block signer available", "CCurBlock.java", 199);
              this.nonce=0;
          }
       }
       catch (Exception e) 
       { 
		UTILS.LOG.log("Exception", e.getMessage(), "CCurBlock.java", 182); 
       }
   }
   
   public void newBlock(long block, 
                        String prev_hash,
                        String hash,
                        long last_tstamp) throws Exception
   {
       // New block
       UTILS.NET_STAT.last_block_hash=hash;
                
       // New block number
       UTILS.NET_STAT.last_block=block;
       
       // Last tstamp
       UTILS.NET_STAT.last_block_tstamp=last_tstamp;
       
        // New block
       this.payload=new CBlockPayload(this.signer);
       
       // Payload hash
       this.payload_hash=UTILS.BASIC.hash(UTILS.SERIAL.serialize(this.payload));
       
       // Block tstamp
       UTILS.NET_STAT.setDifficulty(this.getNewDif(hash));
       
       // Update net stat
       UTILS.DB.executeUpdate("UPDATE net_stat "
                               + "SET last_block='"+UTILS.NET_STAT.last_block + "', "
                                   + "last_block_hash='"+UTILS.NET_STAT.last_block_hash+"', "
                                    + "net_dif='"+UTILS.BASIC.formatDif(UTILS.NET_STAT.net_dif.toString(16))+"'");
       
      
       // Nonce
       this.nonce=0;
       
       // Set block data
       UTILS.NET_STAT.actual_block_hash="";
       UTILS.NET_STAT.actual_block_no=0;
      
   }
   
   public BigInteger getNewDif(String hash) throws Exception
   {
       // First block
       if (hash.equals("0000000000000000000000000000000000000000000000000000000000000000")) 
           return UTILS.NET_STAT.net_dif;
           
       // Tstamp
       long tstamp=0;
            
       // Difficulty
       
       
       // Last hash
       String last_hash=hash;
       
       // Load
       ResultSet rs=UTILS.DB.executeQuery("SELECT *  "
                                          + "FROM blocks "
                                         + "WHERE hash='"+last_hash+"' "
                                      + "ORDER BY block ASC");
       
       if (UTILS.DB.hasData(rs)) 
       {
       // Next
       rs.next();
       
       // Difficulty
       BigInteger last_dif=new BigInteger(rs.getString("net_dif"), 16);
       
       // Last tstamp
       long last_tstamp=rs.getLong("tstamp");
       tstamp=last_tstamp;
       
       // Number of blocks generated in the last 100 minutes 
       long no=0;
       
       while (tstamp>last_tstamp-6000 && !last_hash.equals("0000000000000000000000000000000000000000000000000000000000000000"))
       {
            // Load
            rs=UTILS.DB.executeQuery("SELECT *  "
                                     + "FROM blocks "
                                    + "WHERE hash='"+last_hash+"' "
                                 + "ORDER BY block ASC");
       
            if (UTILS.DB.hasData(rs))
            {
                // Next
                rs.next();
       
                // Tstamp
                tstamp=rs.getLong("tstamp");
                
                // Valid tstamp
                if (tstamp>last_tstamp-6000)
                    no++;
                
                // Last hash
                last_hash=rs.getString("prev_hash"); 
            } 
            else
            {
                System.out.println("Corrupted blockchain");
                System.exit(0);
            }
       }
       
       // Number
       if (no==0) no=1;
       System.out.println("Generated blocks "+no);
       
       // New dif
       BigInteger new_dif=new BigInteger("0");
       
       // Change dificulty ?
       if (no>100)
           new_dif=last_dif.subtract(last_dif.divide(BigInteger.valueOf(100)));
       else if (no<100)
           new_dif=last_dif.add(last_dif.divide(BigInteger.valueOf(100)));
       else
         new_dif=last_dif;
       
       // Return
       return new_dif;
       }
       else
       {
           BigInteger new_dif=new BigInteger("0");
           return new_dif;
       }
   }
   
   public void stopMiners() throws Exception
   {
       for (int a=0; a<=this.miners.size()-1; a++)
       {
           CCPUMiner m=this.miners.get(a);
           m.stopMiner();
       }
       
       // Close array
       this.miners=new ArrayList<CCPUMiner>();
       
       // Reset
       UTILS.DB.executeUpdate("UPDATE web_sys_data "
                               + "SET mining_threads=0, "
                                   + "cpu_1_power=0, "
                                   + "cpu_2_power=0, "
                                   + "cpu_3_power=0, "
                                   + "cpu_4_power=0, "
                                   + "cpu_5_power=0, "
                                   + "cpu_6_power=0, "
                                   + "cpu_7_power=0, "
                                   + "cpu_8_power=0, "
                                   + "cpu_9_power=0, "
                                   + "cpu_10_power=0, "
                                   + "cpu_11_power=0, "
                                   + "cpu_12_power=0, "
                                   + "cpu_13_power=0, "
                                   + "cpu_14_power=0, "
                                   + "cpu_15_power=0, "
                                   + "cpu_16_power=0, "
                                   + "cpu_17_power=0, "
                                   + "cpu_18_power=0, "
                                   + "cpu_19_power=0, "
                                   + "cpu_20_power=0, "
                                   + "cpu_21_power=0, "
                                   + "cpu_22_power=0, "
                                   + "cpu_23_power=0, "
                                   + "cpu_24_power=0");
       
       // Miners no
       this.miners_no=0;
   }
   
   public void startMiners(int no) throws Exception
   {
       // Create array
       miners=new ArrayList<CCPUMiner>();
       
       for (int a=1; a<=no; a++)
           this.addMiner();
       
       this.setSigner();
   }
}
