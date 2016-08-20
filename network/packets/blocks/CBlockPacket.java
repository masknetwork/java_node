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
import wallet.agents.CAgent;

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
        
        // Balance
        public long signer_balance;
        
        // Sign
        public String sign;
        
        // Timestamp
        public long tstamp;
        
        // Nonce
        public long nonce;
        
        // Dificulty
        public String net_dif;
        
        // Adress table hash
        public String tab_1="";
        
        // Adress table hash
        public String tab_2="";
        
        // Adress table hash
        public String tab_3="";
        
        // Adress table hash
        public String tab_4="";
        
        // Adress table hash
        public String tab_5="";
        
        // Adress table hash
        public String tab_6="";
        
        // Adress table hash
        public String tab_7="";
        
        // Adress table hash
        public String tab_8="";
        
        // Adress table hash
        public String tab_9="";
        
        // Adress table hash
        public String tab_10="";
        
        // Adress table hash
        public String tab_11="";
        
        // Adress table hash
        public String tab_12="";
        
        // Adress table hash
        public String tab_13="";
        
        // Adress table hash
        public String tab_14="";
        
        // Adress table hash
        public String tab_15="";
        
        // Adress table hash
        public String tab_16="";
        
        // Adress table hash
        public String tab_17="";
        
        // Adress table hash
        public String tab_18="";
        
        // Adress table hash
        public String tab_19="";
        
        // Adress table hash
        public String tab_20="";
        
        // Adress table hash
        public String tab_21="";
        
        // Adress table hash
        public String tab_22="";
        
        // Adress table hash
        public String tab_23="";
        
        // Adress table hash
        public String tab_24="";
        
        // Adress table hash
        public String tab_25="";
        
        // Adress table hash
        public String tab_26="";
        
        // Adress table hash
        public String tab_27="";
        
        // Adress table hash
        public String tab_28="";
        
        // Adress table hash
        public String tab_29="";
        
        // Adress table hash
        public String tab_30="";
        
        // Serial
        private static final long serialVersionUID = 100L;
        
	public CBlockPacket(String signer, long signer_balance) throws Exception
        {
	   // Constructor
	   super("ID_BLOCK");
           
           // Signer
           this.signer=signer;
              
           // Load signer balance
           this.signer_balance=signer_balance;
           
           // Block
           this.block=UTILS.NET_STAT.last_block+1;
           
           // Tstamp
           this.tstamp=UTILS.BASIC.tstamp();
           
           // Prev hash
           this.prev_hash=UTILS.NET_STAT.last_block_hash;
           
           // Dificulty
           this.net_dif=UTILS.BASIC.formatDif(UTILS.NET_STAT.net_dif.toString(16));
           
           // Checkpoint ?
           if (this.block%UTILS.SETTINGS.chk_blocks==0)
           {
               // Adr table hash
               this.tab_1=UTILS.NET_STAT.getHash("adr");
               
               // Ads table hash
               this.tab_2=UTILS.NET_STAT.getHash("ads");
              
               // Agents table hash
               this.tab_3=UTILS.NET_STAT.getHash("agents");
               
               // Agents feeds
               this.tab_4=UTILS.NET_STAT.getHash("agents_feeds");
               
               // Assets table hash
               this.tab_5=UTILS.NET_STAT.getHash("assets");
               
               // Assets owners table hash
               this.tab_6=UTILS.NET_STAT.getHash("assets_owners");
               
               // Assets markets table hash
               this.tab_7=UTILS.NET_STAT.getHash("assets_mkts");
               
               // Assets markets pos table hash
               this.tab_8=UTILS.NET_STAT.getHash("assets_mkts_pos");
               
               // Comments table hash
               this.tab_9=UTILS.NET_STAT.getHash("comments");
               
               // Delegates votes table hash
               this.tab_10=UTILS.NET_STAT.getHash("del_votes");
               
               // Domains table hash
               this.tab_11=UTILS.NET_STAT.getHash("domains");
               
               // Escrowed table hash
               this.tab_12=UTILS.NET_STAT.getHash("escrowed");
               
               // Feeds table hash
               this.tab_13=UTILS.NET_STAT.getHash("feeds");
               
               // Feeds branches table hash
               this.tab_14=UTILS.NET_STAT.getHash("feeds_branches");
               
               // Feeds bets table hash
               this.tab_15=UTILS.NET_STAT.getHash("feeds_bets");
               
               // Feeds bets pos table hash
               this.tab_16=UTILS.NET_STAT.getHash("feeds_bets_pos");
               
               // Profiles table hash
               this.tab_17=UTILS.NET_STAT.getHash("profiles");
               
               // Storage table hash
               this.tab_18=UTILS.NET_STAT.getHash("storage");
               
               // Tweets table hash
               this.tab_19=UTILS.NET_STAT.getHash("tweets");
               
               // Tweets follow table hash
               this.tab_20=UTILS.NET_STAT.getHash("tweets_follow");
               
               // Votes table hash
               this.tab_21=UTILS.NET_STAT.getHash("votes");
            }
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
		return false;
            
            // Is delegate
            if (!UTILS.DELEGATES.isDelegate(this.signer))
               return false;
            
            // Balance
            if (this.signer_balance>1000000)
                return false;
            
             // Prev hash
             if (!UTILS.BASIC.isHash(prev_hash))
                return false;
             
              // Payload hash
              if (!UTILS.BASIC.isHash(payload_hash))
                return false;
        
	      // Signer
              if (!UTILS.BASIC.isAdr(this.signer))
                return false;
        
              // Net dif
              if (!UTILS.BASIC.isHash(this.net_dif))
                 return false;
              
              // Tabels
              if (!this.tab_1.equals(""))
                 if (!UTILS.BASIC.isHash(this.tab_1))
                    return false;
              
              if (!this.tab_2.equals(""))
                 if (!UTILS.BASIC.isHash(this.tab_2))
                    return false;
              
              if (!this.tab_3.equals(""))
                 if (!UTILS.BASIC.isHash(this.tab_3))
                    return false;
              
              if (!this.tab_4.equals(""))
                 if (!UTILS.BASIC.isHash(this.tab_4))
                    return false;
              
              if (!this.tab_5.equals(""))
                 if (!UTILS.BASIC.isHash(this.tab_5))
                    return false;
              
              if (!this.tab_6.equals(""))
                 if (!UTILS.BASIC.isHash(this.tab_6))
                    return false;
              
              if (!this.tab_7.equals(""))
                 if (!UTILS.BASIC.isHash(this.tab_7))
                    return false;
              
              if (!this.tab_8.equals(""))
                 if (!UTILS.BASIC.isHash(this.tab_8))
                    return false;
              
              if (!this.tab_9.equals(""))
                 if (!UTILS.BASIC.isHash(this.tab_9))
                    return false;
              
              if (!this.tab_10.equals(""))
                 if (!UTILS.BASIC.isHash(this.tab_10))
                    return false;
              
              if (!this.tab_11.equals(""))
                 if (!UTILS.BASIC.isHash(this.tab_11))
                    return false;
              
              if (!this.tab_12.equals(""))
                 if (!UTILS.BASIC.isHash(this.tab_12))
                    return false;
              
              if (!this.tab_13.equals(""))
                 if (!UTILS.BASIC.isHash(this.tab_13))
                    return false;
              
              if (!this.tab_14.equals(""))
                 if (!UTILS.BASIC.isHash(this.tab_14))
                    return false;
              
              if (!this.tab_15.equals(""))
                 if (!UTILS.BASIC.isHash(this.tab_15))
                    return false;
              
              if (!this.tab_16.equals(""))
                 if (!UTILS.BASIC.isHash(this.tab_16))
                    return false;
              
              if (!this.tab_17.equals(""))
                 if (!UTILS.BASIC.isHash(this.tab_17))
                    return false;
              
              if (!this.tab_18.equals(""))
                 if (!UTILS.BASIC.isHash(this.tab_18))
                    return false;
              
              if (!this.tab_19.equals(""))
                 if (!UTILS.BASIC.isHash(this.tab_19))
                    return false;
              
              if (!this.tab_20.equals(""))
                 if (!UTILS.BASIC.isHash(this.tab_20))
                    return false;
              
              if (!this.tab_21.equals(""))
                 if (!UTILS.BASIC.isHash(this.tab_21))
                    return false;
              
              if (!this.tab_22.equals(""))
                 if (!UTILS.BASIC.isHash(this.tab_22))
                    return false;
              
              if (!this.tab_23.equals(""))
                 if (!UTILS.BASIC.isHash(this.tab_23))
                    return false;
              
              if (!this.tab_24.equals(""))
                 if (!UTILS.BASIC.isHash(this.tab_24))
                    return false;
              
              if (!this.tab_25.equals(""))
                 if (!UTILS.BASIC.isHash(this.tab_25))
                    return false;
              
              if (!this.tab_26.equals(""))
                 if (!UTILS.BASIC.isHash(this.tab_26))
                    return false;
              
              if (!this.tab_27.equals(""))
                 if (!UTILS.BASIC.isHash(this.tab_27))
                    return false;
              
              if (!this.tab_28.equals(""))
                 if (!UTILS.BASIC.isHash(this.tab_28))
                    return false;
              
              if (!this.tab_29.equals(""))
                 if (!UTILS.BASIC.isHash(this.tab_29))
                    return false;
              
              if (!this.tab_30.equals(""))
                 if (!UTILS.BASIC.isHash(this.tab_30))
                    return false;
              
              // Load
              ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                                 + "FROM blocks "
                                                + "WHERE hash='"+hash+"'");
              
              // Has data ?
              if (UTILS.DB.hasData(rs))
                 return false;
                
            // Hash
            if (!UTILS.MINER_UTILS.checkHash(this.prev_hash, 
                                              this.block, 
                                              this.payload_hash, 
                                              this.signer, 
                                              this.signer_balance, 
                                              this.tstamp, 
                                              this.nonce,
                                              this.hash,
                                              this.net_dif,
                                              this.tab_1,
                                              this.tab_2,
                                              this.tab_3,
                                              this.tab_4,
                                              this.tab_5,
                                              this.tab_6,
                                              this.tab_7,
                                              this.tab_8,
                                              this.tab_9,
                                              this.tab_10,
                                              this.tab_11,
                                              this.tab_12,
                                              this.tab_13,
                                              this.tab_14,
                                              this.tab_15,
                                              this.tab_16,
                                              this.tab_17,
                                              this.tab_18,
                                              this.tab_19,
                                              this.tab_20,
                                              this.tab_21,
                                              this.tab_22,
                                              this.tab_23,
                                              this.tab_24,
                                              this.tab_25,
                                              this.tab_26,
                                              this.tab_27,
                                              this.tab_28,
                                              this.tab_29,
                                              this.tab_30))
            return false;
            
            // Payload size
            if (this.payload.length>250000)
                return false;
            
            // Return
            return true;
        }
        
	// Check 
	public void check() throws Exception
	{
            // Balance
            if (this.signer_balance!=UTILS.DELEGATES.getPower(this.signer))
                throw new Exception("Invalid signer balance - CBlockPacket.java");
            
             // Precheck
            if (!this.preCheck())
                throw new Exception("Invalid block - CBlockPacket.java");
                    
             // Check type
	     if (!this.tip.equals("ID_BLOCK")) 
	   	throw new Exception("Invalid packet type - CBlockPacket.java");
             
              // Timestamp
              ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                                 + "FROM blocks "
                                                + "WHERE hash='"+prev_hash+"'");
              
              // Next
              rs.next();
              
              // Prev timestamp
              long prev_tstamp=rs.getLong("tstamp");
                      
              // Timestamp ?
              if (this.tstamp<rs.getLong("tstamp"))
                     throw new Exception("Invalid timestamp");
            
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
              
              // Table hashes
              if (this.block%UTILS.SETTINGS.chk_blocks==0)
              {
                  // Adr
                  if (!UTILS.NET_STAT.getHash("adr").equals(this.tab_1))
                      throw new Exception("Invalid adr table hash");
                  
                  // Ads
                  if (!UTILS.NET_STAT.getHash("ads").equals(this.tab_2))
                      throw new Exception("Invalid ads table hash");
                  
                  // Domains
                  if (!UTILS.NET_STAT.getHash("domains").equals(this.tab_11))
                      throw new Exception("Invalid domains table hash");
              }
              
	}
	   
	public void commit()  throws Exception
	{
               //System.out.println("Commiting block "+this.block+" ("+this.hash+")");
                
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
	   	 
                 // Result set
                 ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                             + "FROM agents "
                                            + "WHERE run_period>0 "
                                              + "AND status='ID_ONLINE'");
                
                if (UTILS.DB.hasData(rs))
                {
                    // Next
                    rs.next();
                    
                    // Run ?
                    if (this.block%rs.getLong("run_period")==0)
                    {
                       // Load VM
                       CAgent AGENT=new CAgent(rs.getLong("aID"), false, this.block);
                       
                       // Load block
                       AGENT.VM.SYS.EVENT.loadBlock(hash, 
                                                    this.block, 
                                                    this.nonce);
                    
                       // Execute
                       AGENT.execute("#block#", false, this.block);
                    }
                }
                
                // After block
                this.afterBlock(block);
                
                // Refresh tables
                UTILS.NET_STAT.refreshTables(this.block);
                
                // Sync ?
                if (UTILS.STATUS.engine_status.equals("ID_SYNC"))
                    this.addBlock(this);
                
                // New block
                UTILS.CBLOCK.newBlock(this.block, 
                                      this.prev_hash, 
                                      this.hash, 
                                      this.tstamp);
                
                // Update confirmations
                this.updateConfirms();
                
                
	}
        
   public void addBlock(CBlockPacket block) throws Exception
   {
      // Deserialize payload
       CBlockPayload block_payload=(CBlockPayload) UTILS.SERIAL.deserialize(block.payload);
       
       // Result set
       ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                          + "FROM blocks "
                                         + "WHERE hash='"+block.hash+"'");
       
       // Has sdata
       if (UTILS.DB.hasData(rs))
           return;
                
       // Insert
       UTILS.DB.executeUpdate("INSERT INTO blocks "
                                    + "SET hash='"+block.hash+"', "
                                        + "block='"+block.block+"', "
        		                + "prev_hash='"+block.prev_hash+"', "
                                        + "signer='"+block.signer+"', "
                                        + "packets='"+block_payload.packets.size()+"', "
                                        + "tstamp='"+block.tstamp+"', "
                                        + "nonce='"+block.nonce+"', "
                                        + "net_dif='"+block.net_dif+"', "
                                        + "signer_balance='"+block.signer_balance+"', "
                                        + "tab_1='"+block.tab_1+"', "
                                        + "tab_2='"+block.tab_2+"', "
                                        + "tab_3='"+block.tab_3+"', "
                                        + "tab_4='"+block.tab_4+"', "
                                        + "tab_5='"+block.tab_5+"', "
                                        + "tab_6='"+block.tab_6+"', "
                                        + "tab_7='"+block.tab_7+"', "
                                        + "tab_8='"+block.tab_8+"', "
                                        + "tab_9='"+block.tab_9+"', "
                                        + "tab_10='"+block.tab_10+"', "
                                        + "tab_11='"+block.tab_11+"', "
                                        + "tab_12='"+block.tab_12+"', "
                                        + "tab_13='"+block.tab_13+"', "
                                        + "tab_14='"+block.tab_14+"', "
                                        + "tab_15='"+block.tab_15+"', "
                                        + "tab_16='"+block.tab_16+"', "
                                        + "tab_17='"+block.tab_17+"', "
                                        + "tab_18='"+block.tab_18+"', "
                                        + "tab_19='"+block.tab_19+"', "
                                        + "tab_20='"+block.tab_20+"', "
                                        + "tab_21='"+block.tab_21+"', "
                                        + "tab_22='"+block.tab_22+"', "
                                        + "tab_23='"+block.tab_23+"', "
                                        + "tab_24='"+block.tab_24+"', "
                                        + "tab_25='"+block.tab_25+"', "
                                        + "tab_26='"+block.tab_26+"', "
                                        + "tab_27='"+block.tab_27+"', "
                                        + "tab_28='"+block.tab_28+"', "
                                        + "tab_29='"+block.tab_29+"', "
                                        + "tab_30='"+block.tab_30+"', "
                                        + "payload_hash='"+block.payload_hash+"', "
                                        + "commited='"+UTILS.BASIC.tstamp()+"', "
                                        + "size='"+block.payload.length+"'");
   }
   
   public void afterBlock(long block) throws Exception
   {
       // Adr fee
       this.adrFee(block);
       
       // Cleanup
       this.cleanup(block);
       
       // Pay block reward
       this.payReward(this.signer);
       
        // Pay rewrds
        UTILS.REWARD.payRewards(block);
       
       // Delegates
       UTILS.DELEGATES.refresh(block);
   }
   
   public void payReward(String adr) throws Exception
   {
            UTILS.ACC.newTransfer("default", 
                                  adr,
                                  UTILS.BASIC.getReward("ID_MINER"), 
                                  false,
                                  "MSK", 
                                  "Block reward", 
                                  "", 
                                  hash, 
                                  this.block,
                                  (CBlockPayload)UTILS.SERIAL.deserialize(this.payload),
                                  0);
            
            UTILS.ACC.clearTrans(hash, "ID_ALL", this.block);
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
        
        // Ads
        UTILS.NET_STAT.table_ads.expired(block);
        
        // Agents
        UTILS.NET_STAT.table_agents.expired(block);
        
        // Assets
        UTILS.NET_STAT.table_assets.expired(block);
        
        // Domains
        UTILS.NET_STAT.table_domains.expired(block);
        
        // Escrowed
        UTILS.NET_STAT.table_escrowed.expired(block);
        
        // Profiles
        UTILS.NET_STAT.table_profiles.expired(block);
        
        // Storage
        UTILS.NET_STAT.table_storage.expired(block);
        
        // Tweets
        UTILS.NET_STAT.table_tweets.expired(block);
        
        // Tweets comments
        UTILS.NET_STAT.table_comments.expired(block);
        
        // Tweets likes
        UTILS.NET_STAT.table_votes.expired(block);
        
        // Tweets follow
        UTILS.NET_STAT.table_tweets_follow.expired(block);
   }
   
   public void adrFee(long block) throws Exception
   {
       // 100th block
       if (block%1000!=0) return;
       
       // Load all addresses havin the balance over 0.0001
       ResultSet rs=UTILS.DB.executeQuery("SELECT COUNT(*) AS total "
                                          + "FROM adr "
                                         + "WHERE balance>=0.0001");
       
       // Next
       rs.next();
       
       // Total
       long total=rs.getLong("total");
       
       // Total fee
       double total_fee=total*0.0001;
       
       // Decrease addresses balances
       UTILS.DB.executeUpdate("UPDATE adr "
                               + "SET balance=balance-0.0001 "
                             + "WHERE balance>=0.0001");
       
       // Move funds to default
       UTILS.DB.executeUpdate("UPDATE adr "
                               + "SET balance=balance+"+total_fee+" "
                             + "WHERE adr='default'");
       
       // Remove addresses
       rs=UTILS.DB.executeQuery("SELECT * "
                                + "FROM adr "
                               + "WHERE balance<0.0001");
       
       // Cleanup
       while (rs.next())
         UTILS.NET_STAT.table_adr.expired(this.block);
   }
   
   
}