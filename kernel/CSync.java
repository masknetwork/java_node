package wallet.kernel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.network.packets.sync.CReqDataPacket;

import wallet.network.CPeer;
import wallet.network.packets.sync.CBlockchain;
import wallet.network.packets.sync.CChainBlock;

public class CSync extends Thread 
{
    // Blockchain
    public CBlockchain blockchain;
    
    // Last table hash
    public String adr;
    public String ads;
    public String domains;
    
	public CSync() 
	{
		
	}
        
        public String getFreePeer() throws Exception
        {
            // Statement
            Statement s=UTILS.DB.getStatement();  
            
            // Load peers
            ResultSet rs=s.executeQuery("SELECT * FROM peers");
            
            // Has data
            if (UTILS.DB.hasData(rs))
            {
                while (rs.next())
                  if (!this.isBusy(rs.getString("peer")))
                  {
                      // Return
                      String peer=rs.getString("peer");
                      
                      // Close 
                      s.close();
                      
                      // Return
                      return peer;
                  }
            }
            else
            {
                // Close
                s.close();
                
                // Return local
                return "0.0.0.0";
            }
            
            // Return
            return "0.0.0.0";
        }
        
        public void findPeers() throws Exception
        {
            // Statement
            Statement s=UTILS.DB.getStatement();
            
            // Blockchain
            ResultSet rs=s.executeQuery("SELECT * "
			              + "FROM sync "
			  	     + "WHERE type='ID_GET_BLOCKCHAIN' "
                                       + "AND peer='' "
                                       + "AND status='ID_PENDING'");
                                            
            if (UTILS.DB.hasData(rs))
            {
                // Get free peer
                String peer=this.getFreePeer();
                
                // Found ?
                if (!peer.equals("0.0.0.0"))
                    UTILS.DB.executeUpdate("UPDATE sync "
                                            + "SET peer='"+peer+"' "
                                          + "WHERE peer='' "
                                            + "AND status='ID_PENDING' "
                                            + "AND type='ID_GET_BLOCKCHAIN'");
            }
            else
            {
                // Tables
                rs=s.executeQuery("SELECT * "
			          + "FROM sync "
			         + "WHERE type='ID_GET_TABLE' "
                                   + "AND peer='' "
                                   + "AND status='ID_PENDING'");
                                            
                if (UTILS.DB.hasData(rs))
                {
                    // Get free peer
                    String peer=this.getFreePeer();
                
                    // Found ?
                    if (!peer.equals("0.0.0.0"))
                        UTILS.DB.executeUpdate("UPDATE sync "
                                                + "SET peer='"+peer+"' "
                                              + "WHERE peer='' "
                                                + "AND status='ID_PENDING' "
                                                + "AND type='ID_GET_TABLE'");
                }
                else
                {
                    // Blocks
                    rs=s.executeQuery("SELECT * "
			              + "FROM sync "
			  	     + "WHERE type='ID_BLOCKS' "
                                       + "AND peer='' "
                                       + "AND status='ID_PENDING'");
                                            
                    if (UTILS.DB.hasData(rs))
                    {
                       // Get free peer
                       String peer=this.getFreePeer();
                
                       // Found ?
                       if (!peer.equals("0.0.0.0"))
                           UTILS.DB.executeUpdate("UPDATE sync "
                                                   + "SET peer='"+peer+"' "
                                                 + "WHERE peer='' "
                                                   + "AND status='ID_PENDING' "
                                                   + "AND type='ID_BLOCKS'");
                    }
                }
            }
            
            // Close
            s.close();
        }
                
       public void insertTable(String table) throws Exception
       {
           // Deletes from table
           UTILS.DB.executeUpdate("DELETE FROM "+table);
        
           // Insert table into sync stack
           UTILS.DB.executeUpdate("INSERT INTO sync(status, "
                                                 + "peer, "
                                                 + "type, "
                                                 + "tab, "
                                                 + "start, "
                                                 + "end, "
                                                 + "tstamp) "
                                      + "VALUES('ID_PENDING', "
                                             + "'', "
                                             + "'ID_GET_TABLE', "
                                             + "'"+table+"', "
                                             + "'0', "
                                             + "'0', "
                                             + "'0')");
       }
        
        public void flushBlockchain(long max_block) throws Exception
        {
            CChainBlock block=null;
            
            for (int a=0; a<=blockchain.blocks.size()-1; a++)
            {
                // Load block
                block=blockchain.blocks.get(a);
                
                // Max block
                if (block.block<=max_block && !this.blockExist(block.block_hash))
                    UTILS.DB.executeUpdate("INSERT INTO blocks (hash, "
                                                             + "block, "
                                                             + "prev_hash, "
                                                             + "signer, "
                                                             + "tstamp, "
                                                             + "nonce, "
                                                             + "net_dif, "
                                                             + "payload_hash, "
                                                             + "signer_balance, "
                                                             + "commited, "
                                                             + "tab_1, "
                                                             + "tab_2, "
                                                             + "tab_3, "
                                                             + "tab_4, "
                                                             + "tab_5, "
                                                             + "tab_6, "
                                                             + "tab_7, "
                                                             + "tab_8, "
                                                             + "tab_9, "
                                                             + "tab_10) VALUES('"
                                                             +block.block_hash+"', '"
                                                             +block.block+"', '"
                                                             +block.prev_hash+"', '"
                                                             +block.signer+"', '"
                                                             +block.tstamp+"', '"
                                                             +block.nonce+"', '"
                                                             +block.net_dif+"', '"
                                                             +block.payload_hash+"', '"
                                                             +block.signer_balance+"', '"
                                                             +block.commited+"', '"
                                                             +block.tab_1+"', '"
                                                             +block.tab_2+"', '"
                                                             +block.tab_3+"', '"
                                                             +block.tab_4+"', '"
                                                             +block.tab_5+"', '"
                                                             +block.tab_6+"', '"
                                                             +block.tab_7+"', '"
                                                             +block.tab_8+"', '"
                                                             +block.tab_9+"', '"
                                                             +block.tab_10+"')");
            }
            
            if (block!=null && block.commited>0)
            UTILS.DB.executeUpdate("UPDATE net_stat "
                                    + "SET last_block='"+block.block+"', "
                                        + "last_block_hash='"+block.block_hash+"', "
                                        + "net_dif='"+block.net_dif+"'");
        }
        
        public boolean blockExist(String hash) throws Exception
        {
            // Statement
            Statement s=UTILS.DB.getStatement();
            
            // Check
            ResultSet rs=s.executeQuery("SELECT * "
                                        + "FROM blocks "
                                       + "WHERE hash='"+hash+"'");
            
            // Has data
            if (UTILS.DB.hasData(rs))
            {
                 // Close
                 s.close();
                 
                 // Return
                 return true;
            }
            
            // Close
            s.close();
            
            // Not found
            return false;
        }
        
        public void loadBlockchain(CBlockchain blockchain) throws Exception
        {
           // Load
           this.blockchain=blockchain; 
           
           // No more blocks ?
           if (this.blockchain.blocks.size()==0)
           {
               UTILS.STATUS.setEngineStatus("ID_ONLINE");
               
               // Delete from sync
               UTILS.DB.executeUpdate("DELETE FROM sync WHERE type='ID_GET_BLOCKCHAIN'");
               
               return;
           }
           
            // Full resync ?
            if (this.blockchain.last_block>(UTILS.NET_STAT.last_block+5))
            {
                // Tables
                this.insertTable("adr");
                this.insertTable("ads");
                this.insertTable("domains");
                
                // Start
                long start=Math.round(Math.floor(this.blockchain.last_block/10))*10;
                
                // Flush blockchain
                this.flushBlockchain(start-1);
                
                // Blocks
                if (this.blockchain.last_block>=start)
                {
                    UTILS.DB.executeUpdate("INSERT INTO sync(status, "
                                                          + "peer, "
                                                          + "type, "
                                                          + "tab, "
                                                          + "start, "
                                                          + "end, "
                                                          + "tstamp) "
                                                 + "VALUES('ID_PENDING', "
                                                         + "'', "
                                                         + "'ID_BLOCKS', "
                                                         + "'', "
                                                         + "'"+start+"', "
                                                         + "'"+this.blockchain.last_block+"', "
                                                         + "'0')");
                }
                
            }
            else
            {
                UTILS.DB.executeUpdate("INSERT INTO sync(status, "
                                                      + "peer, "
                                                      + "type, "
                                                      + "tab, "
                                                      + "start, "
                                                      + "end, "
                                                      + "tstamp) "
                                             + "VALUES('ID_PENDING', "
                                                      + "'', "
                                                      + "'ID_BLOCKS', "
                                                      + "'',"
                                                      + "'"+(UTILS.NET_STAT.last_block+1)+"', "
                                                      + "'"+this.blockchain.last_block+"', "
                                                      + "'0')");
            }
            
            
            // Delete from sync
            UTILS.DB.executeUpdate("DELETE FROM sync WHERE type='ID_GET_BLOCKCHAIN'");
          
        }
	
	public void run()
	{
            try
            {
                 // Deletes sync
                 UTILS.DB.executeUpdate("DELETE FROM sync");
                 
                 
                 // Sync status
                 UTILS.STATUS.setEngineStatus("ID_SYNC");
            }
            catch (Exception ex) 
       	      {  
       		UTILS.LOG.log("SQLException", ex.getMessage(), "CBuyDomainPayload.java", 57);
              }
		
	}
	
	
	public boolean isBusy(String peer) throws Exception
	{
            // Statement
            Statement s=UTILS.DB.getStatement();
            
	   // Get the number of active jobs
	   ResultSet rs=s.executeQuery("SELECT * "
				       + "FROM sync "
				      + "WHERE peer='"+peer+"'");
	    
           boolean hasData=UTILS.DB.hasData(rs);
           
           rs.close(); 
           s.close();
           
            if (hasData==true) 
	       return true;
	    else
	       return false;
	}
	
	public void tick() throws Exception
	{
            // Synced ?
            if (UTILS.STATUS.engine_status.equals("ID_ONLINE") || UTILS.NETWORK.peers.peers.size()<0) return;
                
                // Free peers
                this.findPeers();
            
	        // Statement
                Statement s=UTILS.DB.getStatement();
                
		// Get the number of jobs
		ResultSet rs=s.executeQuery("SELECT * FROM sync");
	        
                
                // ------------------------- No records on sync stack ----------------------------------------------------
		if (UTILS.DB.hasData(rs)==false)
		{
		    // Inserts blockchain 
                    UTILS.DB.executeUpdate("INSERT INTO sync(type, "
                                                        + "status, "
                                                        + "peer, "
                                                        + "start, "
                                                        + "tstamp) "
                                            + "VALUES('ID_GET_BLOCKCHAIN', "
                                                    + "'ID_PENDING', "
                                                    + "'', "
                                                    + "'"+UTILS.NET_STAT.last_block+"', "
                                                    + "'"+UTILS.BASIC.tstamp()+"')");
		}
                
                // ------------------------- Data on sync stack ----------------------------------------------------
		else
                {
                    // Blockchain
                    rs=s.executeQuery("SELECT * "
			              + "FROM sync "
			  	     + "WHERE type='ID_GET_BLOCKCHAIN' "
                                       + "AND peer<>'' "
                                       + "AND status='ID_PENDING'");
                                            
                    if (UTILS.DB.hasData(rs))
                    {
                       // Next
                       rs.next();
                                                
                       // Request data packet
		       CReqDataPacket packet=new CReqDataPacket("ID_BLOCKCHAIN", 
					    		     rs.getLong("start"),
                                                             0); 
		                
                       // Send to peer
                       UTILS.NETWORK.sendToPeer(rs.getString("peer"), packet);
					  
	               // Update db
		       UTILS.DB.executeUpdate("UPDATE sync "
					       + "SET status='ID_DOWNLOADING' "
					     + "WHERE type='ID_GET_BLOCKCHAIN'");
                    }
                    
                    // ------------------------- No blockchain to download ----------------------------------------------------
                    else
                    {
                        // Tables
                        rs=s.executeQuery("SELECT * "
			                  + "FROM sync "
			                 + "WHERE type='ID_GET_TABLE' "
                                           + "AND peer<>'' "
                                           + "AND (status='ID_PENDING' "
                                                + "OR status='ID_DOWNLOADING')");
                        
                        // ------------------------- Tables to download / downloading ----------------------------------------------------
                        if (UTILS.DB.hasData(rs))
                        {
                            rs=s.executeQuery("SELECT * "
			                      + "FROM sync "
			                     + "WHERE type='ID_GET_TABLE' "
                                               + "AND peer<>''"
                                               + "AND (status='ID_PENDING')");
                            
                             if (UTILS.DB.hasData(rs))
                             {
                                    // Next
                                    rs.next();
                                                
                                    // Request data packet
                                    long block_no=Math.round(Math.floor(this.blockchain.last_block/10))*10-1;
                                    String bhash=this.getBlockHash(block_no);
		                    CReqDataPacket packet=new CReqDataPacket("ID_GET_TABLE", 
					    		                     rs.getString("tab"),
                                                                             bhash); 
		                
                                     // Send to peer
                                     UTILS.NETWORK.sendToPeer(rs.getString("peer"), packet);
					  
	                             // Update db
	                             UTILS.DB.executeUpdate("UPDATE sync "
	                                                     + "SET status='ID_DOWNLOADING', "
					                         + "peer='"+rs.getString("peer")+"' "
					                   + "WHERE type='ID_GET_TABLE'");
                             }
                        }
                        
                        // ------------------------- No tables to download ----------------------------------------------------
                        else
                        {
		             // Blocks
		             rs=s.executeQuery("SELECT * "
			                       + "FROM sync "
			                      + "WHERE type='ID_BLOCKS' "
                                                + "AND status='ID_PENDING' "
                                                + "AND peer<>'' "
                                           + "ORDER BY start ASC");
	   
	                    if (UTILS.DB.hasData(rs))
	                    {
                                // Next
		                rs.next();
						  
		                // Request data packet
			        CReqDataPacket packet=new CReqDataPacket("ID_BLOCKS", 
					    		                 rs.getLong("start"), 
					    		                 rs.getLong("end")); 
			        
                                // Send packet
                                UTILS.NETWORK.sendToPeer(rs.getString("peer"), packet);
					  
		                // Update db
			        UTILS.DB.executeUpdate("UPDATE sync "
					                + "SET status='ID_DOWNLOADING' "
					              + "WHERE type='ID_BLOCKS' "
					                + "AND start='"+rs.getLong("start")+"'");
	                }
                    }
                }
        }
                                           
                // Close
                s.close();
	    
        }
		
        public String getBlockHash(long block)
        {
            for (int a=0; a<=this.blockchain.blocks.size()-1; a++)
            {
                CChainBlock b=this.blockchain.blocks.get(a);
                if (b.block==block) return b.block_hash;
            }
            
            return "";
        }
        
	public void removeTable(String table) throws Exception
        {
           UTILS.DB.executeUpdate("DELETE FROM sync WHERE tab='"+table+"'");
        }
        
        public String getTableCRC(String table)
        {
            if (table.equals("adr")) return this.adr;
            if (table.equals("ads")) return this.ads;
            if (table.equals("domains")) return this.domains;
            
            return "";
        }

}
