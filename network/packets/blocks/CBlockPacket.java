// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.blocks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.zip.GZIPOutputStream;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.trans.*;
import wallet.kernel.*;


public class CBlockPacket extends CPacket 
{
        // Block
        public long block;
        
        // Prev hash
        public String prev_hash;
        
        // Prev hash
        public String payload_hash;
        
	// Signer
        public String signer;
        
        // Timestamp
        public long tstamp;
        
        // Nonce
        public long nonce;
        
        // Dificulty
        public String net_dif;
        
        // Serial
        private static final long serialVersionUID = 100L;
        
	public CBlockPacket(String signer) throws Exception
        {
	   // Constructor
	   super("ID_BLOCK");
           
           // Signer
           this.signer=signer;
              
           // Block
           this.block=UTILS.NET_STAT.last_block+1;
           
           // Tstamp
           this.tstamp=UTILS.BASIC.tstamp();
           
           // Prev hash
           this.prev_hash=UTILS.NET_STAT.last_block_hash;
           
           // Dificulty
           this.net_dif=UTILS.BASIC.formatDif(UTILS.NET_STAT.net_dif.toString(16));
        }
	
        // Sign
	public void sign(String hash) throws Exception
        { 
            // Hash
            this.hash=hash;
            
            // Signature address
            CAddress adr=UTILS.WALLET.getAddress(this.signer);
	    this.sign=adr.sign(hash);
        }
        
        public boolean preCheck() throws Exception
        {
            // Check signature
            CECC ecc=new CECC(this.signer);
            if (!ecc.checkSig(hash, this.sign))
            {
                System.out.println("Invalid block signature");
		return false;
            }
            
            String h=UTILS.BASIC.hash(this.payload);
            if (!this.payload_hash.equals(h))
            {
                System.out.println("Invalid payload hash");
                return false;
            }
            
            // More than 25 blocks back ? 
            if (this.block<UTILS.NET_STAT.last_block-25)
            {
                System.out.println("Invalid block number");
                return false;
            }
            
             // Prev hash
             if (!UTILS.BASIC.isHash(prev_hash))
             {
                System.out.println("Invalid prev hash");
                return false;
             }
             
              // Payload hash
              if (!UTILS.BASIC.isHash(payload_hash))
              {
                  System.out.println("Invalid payload signature");
                  return false;
              }
              
	      // Signer
              if (!UTILS.BASIC.isAdr(this.signer))
              {
                  System.out.println("Invalid signer");
                  return false;
              }
              
              // Net dif
              if (!UTILS.BASIC.isHash(this.net_dif))
              {
                 System.out.println("Invalid net dif");
                 return false;
              }
              
              // Load parent
              ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                                 + "FROM blocks "
                                                + "WHERE hash='"+prev_hash+"'");
              
              // Has data ?
              if (UTILS.DB.hasData(rs))
              {
                  // Next
                  rs.next();
              
                  // Check
                  if (rs.getLong("block")!=(this.block-1))
                  {
                      System.out.println("Prev hash not found");
                      return false;
                  }
                  
                  // Timestamp ?
                  if (this.tstamp<rs.getLong("tstamp"))
                     throw new Exception("Invalid timestamp");
              }
              
              // Hash
              if (!UTILS.MINER_UTILS.checkHash(this.prev_hash, 
                                               this.block, 
                                               this.payload_hash, 
                                               this.signer, 
                                               this.tstamp, 
                                               this.nonce,
                                               this.hash,
                                               this.net_dif))
              {
                 System.out.println("Invalid POW");
                 return false;
              }
              
            // Payload size
            if (this.payload.length>100000)
            {
                System.out.println("Invalid payload length - "+this.payload.length);
                return false;
            }
            
            // Return
            return true;
        }
        
	// Check 
	public void check() throws Exception
	{
             // Precheck
            if (!this.preCheck())
                throw new Exception("Invalid block - CBlockPacket.java");
                    
             // Check type
	     if (!this.tip.equals("ID_BLOCK")) 
	   	throw new Exception("Invalid packet type - CBlockPacket.java");
             
	     // Deserialize transaction data
	     CBlockPayload block_payload=(CBlockPayload) UTILS.SERIAL.deserialize(payload);
	     
             // Super class
	     super.check(block_payload);
	     
             // Check
             block_payload.check();
	   	
              // Check signature
	      CECC ecc=new CECC(this.signer);
	      if (!ecc.checkSig(hash, this.sign))
                   throw new Exception("Invalid signature - CBlockPacket.java");
        }
	   
	public void commit()  throws Exception
	{
                System.out.println("Commiting block "+this.block+" ("+this.hash+")");
                
                // Set block data
                UTILS.NET_STAT.actual_block_hash=this.hash;
                UTILS.NET_STAT.actual_block_no=this.block;
                
                // Delete
                UTILS.DB.executeUpdate("DELETE FROM my_trans WHERE block_hash='' OR block_hash='"+this.hash+"'");
                UTILS.DB.executeUpdate("DELETE FROM trans WHERE block_hash='' OR block_hash='"+this.hash+"'");
                        
                // Deserialize transaction data
	   	CBlockPayload block_payload=(CBlockPayload) UTILS.SERIAL.deserialize(payload);
	   	
	   	// Superclass
	   	super.commit(block_payload);
	   	
	   	// Commit payload
	   	block_payload.commit();
	   	 
                // After block
                this.afterBlock(block);
                
                // New block
                UTILS.CBLOCK.newBlock(this.block, 
                                      this.prev_hash, 
                                      this.hash, 
                                      this.tstamp);
                
                // Update confirmations
                this.updateConfirms();
        }
        
   public void afterBlock(long block) throws Exception
   {
       // Trans pool
       UTILS.NETWORK.TRANS_POOL.newBlock(this.block);
       
       // Reward
       UTILS.REWARD.reward(block);
                
       // Adr fee
       this.adrFee(block);
       
       // Pay block reward
       this.payReward(this.signer);
       
       // Delegates
       UTILS.DELEGATES.refresh(block);
       
       // Options
       CBlockPayload block_payload=(CBlockPayload) UTILS.SERIAL.deserialize(payload);
       
       // Options
       UTILS.OPTIONS.checkOptions(block, block_payload);
       
       // Spec positions
       UTILS.SPEC_POS.checkSpecPos(block, block_payload);
       
       // Pending orders
       UTILS.SPEC_POS.checkPendingOrders(block);
       
       // Check markets
       UTILS.SPEC_POS.checkMarkets(block);
       
       // Cleanup
       this.cleanup(block);
   }
   
   public void payReward(String adr) throws Exception
   {
        // Reward
        double reward=UTILS.BASIC.getReward("ID_MINER");
        
        // Insert trans
        UTILS.ACC.newTransfer("default", 
                                  adr,
                                  reward, 
                                  "MSK", 
                                  "Block reward", 
                                  "", 
                                  hash, 
                                  this.block);
        
        // Clear
        UTILS.ACC.clearTrans(hash, "ID_ALL", this.block);
        
        // Update reward
        UTILS.DB.executeUpdate("UPDATE blocks "
                                + "SET reward='"+reward+"' "
                              + "WHERE hash='"+this.hash+"'");
    }
   
   public void updateConfirms() throws Exception
   {
       // Confirms
       long confirms=0;
       
       // Block
       long block_no=1;
       
        
       // Prev hash
       String prev_hash=UTILS.NET_STAT.last_block_hash;
       
       while (confirms<25 && block_no>0)
       {
           // Confirms
           confirms++;
           
           // Load blocks
           ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                              + "FROM blocks "
                                             + "WHERE hash='"+prev_hash+"'");
           
           // Next 
           rs.next();
           
           // Block no
           block_no=rs.getLong("block");
       
           // Set confirms to zero
           UTILS.DB.executeUpdate("UPDATE packets "
                                   + "SET confirms=0 "
                                 + "WHERE block='"+block_no+"'");
           
           // Set confirms to zero for blocks
           UTILS.DB.executeUpdate("UPDATE blocks "
                                   + "SET confirmations=0 "
                                 + "WHERE hash='"+rs.getString("hash")+"'");
           
           // Update confirmations for packets
           UTILS.DB.executeUpdate("UPDATE packets "
                                   + "SET confirms='"+confirms+"' "
                                 + "WHERE block_hash='"+prev_hash+"'");
           
           // Update confirmations for blocks
           UTILS.DB.executeUpdate("UPDATE blocks "
                                   + "SET confirmations='"+confirms+"' "
                                 + "WHERE hash='"+rs.getString("hash")+"'");
       
           // New hash
           prev_hash=rs.getString("prev_hash");
       }
       
       // Close
       
   }
   
   public void cleanup(long block) throws Exception
   {
        // Adr
        UTILS.NET_STAT.table_adr.expired(block);
        
        // Adr attr
        UTILS.NET_STAT.table_adr_attr.expired(block);
        
        // Ads
        UTILS.NET_STAT.table_ads.expired(block);
        
        // Assets
        UTILS.NET_STAT.table_assets.expired(block);
        
        // Assets markets
        UTILS.NET_STAT.table_assets_mkts.expired(block);
        
        // Assets markets pos
        UTILS.NET_STAT.table_assets_mkts_pos.expired(block);
        
        // Comments
        UTILS.NET_STAT.table_com.expired(block);
        
        // Delegates votes
        UTILS.NET_STAT.table_votes.expired(block);
        
        // Domains
        UTILS.NET_STAT.table_domains.expired(block);
        
        // Delegates log
        UTILS.NET_STAT.table_delegates_log.expired(block);
        
        // Escrowed
        UTILS.NET_STAT.table_escrowed.expired(block);
        
        // Feeds
        UTILS.NET_STAT.table_feeds.expired(block);
        
        // Feeds branches
        UTILS.NET_STAT.table_feeds_branches.expired(block);
        
        // Feeds spec mkts
        UTILS.NET_STAT.table_feeds_spec_mkts.expired(block);
        
        // Feeds spec mkts pos
        UTILS.NET_STAT.table_feeds_spec_mkts_pos.expired(block);
        
        // Profiles
        UTILS.NET_STAT.table_profiles.expired(block);
        
        // Tweets
        UTILS.NET_STAT.table_tweets.expired(block);
        
        // Tweets follow
        UTILS.NET_STAT.table_tweets_follow.expired(block);
        
        // Votes
        UTILS.NET_STAT.table_votes.expired(block);
   }
   
   public void adrFee(long block) throws Exception
   {
       // 100th block
       if (block%1440!=0) return;
       
       // Load all addresses havin the balance over 0.0001
       ResultSet rs=UTILS.DB.executeQuery("SELECT COUNT(*) AS total "
                                          + "FROM adr "
                                         + "WHERE balance<0.1 "
                                           + "AND balance>=0.0001");
       
       // Next
       rs.next();
       
       // Total
       long total=rs.getLong("total");
       
       // Total fee
       double total_fee=total*0.0001;
       
       // Decrease addresses balances
       UTILS.DB.executeUpdate("UPDATE adr "
                               + "SET balance=balance-0.0001, "
                                   + "block='"+block+"' "
                             + "WHERE balance<0.1 "
                               + "AND balance>=0.0001");
       
       // Move funds to default
       UTILS.DB.executeUpdate("UPDATE adr "
                               + "SET balance=balance+"+total_fee+", "
                                   + "block='"+block+"' "
                             + "WHERE adr='default'");
       
       // Expired
       UTILS.NET_STAT.table_adr.expired(this.block);
   }
}