
// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
   public CCPUMiner miner_1=null;
   public CCPUMiner miner_2=null;
   public CCPUMiner miner_3=null;
   public CCPUMiner miner_4=null;
   public CCPUMiner miner_5=null;
   public CCPUMiner miner_6=null;
   public CCPUMiner miner_7=null;
   public CCPUMiner miner_8=null;
   
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
       // Load network status
       
       
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
  
   }
 
   public void startMiner(int miner)
   {
       switch (miner)
       {
           case 1 : this.miner_1=new CCPUMiner(); 
                                 this.miner_1.start(); 
                                 break;
                                 
           case 2 : this.miner_2=new CCPUMiner(); 
                                 this.miner_2.start(); 
                                 break;
                                 
           case 3 : this.miner_3=new CCPUMiner(); 
                                 this.miner_3.start(); 
                                 break;
                                 
           case 4 : this.miner_4=new CCPUMiner(); 
                                 this.miner_4.start(); 
                                 break;
                                 
           case 5 : this.miner_5=new CCPUMiner(); 
                                 this.miner_5.start(); 
                                 break;
                                 
           case 6 : this.miner_6=new CCPUMiner(); 
                                 this.miner_6.start(); 
                                 break;
                                 
           case 7 : this.miner_7=new CCPUMiner(); 
                                 this.miner_7.start(); 
                                 break;
                                 
           case 8 : this.miner_8=new CCPUMiner(); 
                                 this.miner_8.start(); 
                                 break;
       }
   }
   
   public void addPacket(CBroadcastPacket packet) throws Exception
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
   
   public void setNonce(long nonce) throws Exception
   {
      this.payload.hash();
      this.nonce=nonce;
   }
   
   public void setSigner()  throws Exception
   {
          // Load
          ResultSet rs=UTILS.DB.executeQuery("SELECT ma.*, adr.balance "
                                             + "FROM my_adr AS ma "
                                             + "JOIN adr ON adr.adr=ma.adr "
                                            + "WHERE mine>0 AND last_mine<"+(UTILS.NET_STAT.last_block-1000)+" "
                                         + "ORDER BY adr.balance DESC LIMIT 0,1");
       
          // Next
          if (UTILS.DB.hasData(rs)) 
          {
              // Next
              rs.next();
           
              // Set miner
              this.signer=rs.getString("adr");
              
              // Signer balance
              this.signer_balance=Math.round(rs.getDouble("balance"));
              
              // Payload
              if (this.payload!=null) this.payload.target_adr=signer;
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
       
       // Payload hash
       this.payload_hash=UTILS.BASIC.hash(UTILS.SERIAL.serialize(this.payload));
       
       // Block tstamp
       UTILS.NET_STAT.setDifficulty(this.getNewDif(hash));
       
       // Update net stat
       UTILS.DB.executeUpdate("UPDATE net_stat "
                               + "SET last_block='"+UTILS.NET_STAT.last_block + "', "
                                   + "last_block_hash='"+UTILS.NET_STAT.last_block_hash+"', "
                                    + "net_dif='"+UTILS.BASIC.formatDif(UTILS.NET_STAT.net_dif.toString(16))+"'");
       
       // New block
       this.payload=new CBlockPayload(this.signer);
       
      
       // Nonce
       this.nonce=0;
       
       // Reset consensus
       if (UTILS.STATUS.engine_status.equals("ID_ONLINE"))
           UTILS.CONSENSUS.newBlock();
      
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
       }
       
       // Number
       if (no==0) no=1;
       System.out.println("Generated blocks "+no);
       
       // New dif
       BigInteger new_dif=new BigInteger("0");
       
       // Change dificulty ?
       if (no>100)
       {
           new_dif=last_dif.subtract(last_dif.divide(new BigInteger("100")));
       }
       else if (no<100)
       {
           new_dif=last_dif.add(last_dif.divide(new BigInteger("100")));
       }
       
       else
         new_dif=last_dif;
       
       // Return
       return new_dif;
   }
   
}
