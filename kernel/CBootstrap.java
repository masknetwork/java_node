// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.kernel;

import wallet.kernel.net_stat.tables.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.net_stat.*;

public class CBootstrap 
{
     public CBootstrap()  throws Exception
     {
        // Check tables
        this.checkTables();
        
         // Delete active peers
        UTILS.DB.executeUpdate("DELETE FROM peers");
        
        // Delete transaction pool
        UTILS.DB.executeUpdate("DELETE FROM trans_pool");
     }  
     
     public void createTable(String tab) throws Exception
     {
         System.out.println("Creating table "+tab+"...");
         
         if (tab.equals("adr"))
         {
             CAdrTable adr=new CAdrTable();
             adr.create(); 
         }
         
        
        
        // ---------------------------------- Ads --------------------------------------
	if (tab.equals("ads"))
        {
	    CAdsTable domains=new CAdsTable();
            domains.create();
        }
        
          // ------------------------------- Blocks Pool --------------------------------------
	 if (tab.equals("blocks_pool"))
         {
	    UTILS.DB.executeUpdate("CREATE TABLE blocks_pool(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
			 	 		          + "hash VARCHAR(100), "
			 	 	 	          + "block BIGINT NOT NULL DEFAULT 0, "
			 	 	 	          + "tstamp BIGINT NOT NULL DEFAULT 0)");
				    
	    UTILS.DB.executeUpdate("CREATE UNIQUE INDEX blocks_pool_hash ON blocks_pool(hash)");
	    UTILS.DB.executeUpdate("CREATE INDEX blocks_pool_block ON blocks_pool(block)");
         }
         
         // ------------------------------------- Votes stats ------------------------------------------------
         if (tab.equals("votes_stats"))
         {
             UTILS.DB.executeUpdate("CREATE TABLE votes_stats(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                                +"target_type VARCHAR(50) NOT NULL DEFAULT '', "
                                                                +"targetID BIGINT NOT NULL DEFAULT 0, "
                                                                +"upvotes_24 BIGINT NOT NULL DEFAULT 0, "
                                                                +"upvotes_power_24 FLOAT(9,2) NOT NULL DEFAULT 0, "
                                                                +"downvotes_24 BIGINT NOT NULL DEFAULT 0, "
                                                                +"downvotes_power_24 FLOAT(9,2) NOT NULL DEFAULT 0, "
                                                                +"tstamp BIGINT NOT NULL DEFAULT 0, "
                                                                +"pay FLOAT(9,4) NOT NULL DEFAULT '0')");
             
              UTILS.DB.executeUpdate("CREATE INDEX votes_stats_target_type ON votes_stats(target_type)");
              UTILS.DB.executeUpdate("CREATE INDEX votes_stats_targetID ON votes_stats(targetID)");
         }
         
         // ------------------------------------- Votes stats ------------------------------------------------
         if (tab.equals("votes_power"))
         {
             UTILS.DB.executeUpdate("CREATE TABLE votes_power(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                             +"voteID BIGINT NOT NULL DEFAULT 0, "
                                                             +"vote_power FLOAT(20, 4) NOT NULL DEFAULT 0, "
                                                             +"vote_pay FLOAT(20, 4) NOT NULL DEFAULT 0)");
             
              UTILS.DB.executeUpdate("CREATE INDEX votes_power_voteID ON votes_power(voteID)");
         }
         
         // ------------------------------- Blocks --------------------------------------
	 if (tab.equals("blocks"))
         {
	    UTILS.DB.executeUpdate("CREATE TABLE blocks(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
			 	 		     + "hash VARCHAR(100), "
			 	 	 	     + "block BIGINT NOT NULL DEFAULT 0, "
			 	 	 	     + "prev_hash VARCHAR(250) NOT NULL DEFAULT '', "
			 	 	 	     + "signer VARCHAR(250) NOT NULL DEFAULT '', "
                                                     + "packets BIGINT NOT NULL DEFAULT 0, "
                                                     + "tstamp BIGINT NOT NULL DEFAULT 0, "
                                                     + "nonce BIGINT NOT NULL DEFAULT 0, "
                                                     + "size BIGINT NOT NULL DEFAULT 0, "
                                                     + "net_dif VARCHAR(100) NOT NULL DEFAULT '0', "
                                                     + "commited BIGINT NOT NULL DEFAULT 0, "
                                                     + "confirmations BIGINT NOT NULL DEFAULT 0, "
                                                     + "reward FLOAT(20, 8) NOT NULL DEFAULT 0, "
                                                     + "payload_hash VARCHAR(250) NOT NULL DEFAULT '')");
				    
	    UTILS.DB.executeUpdate("CREATE UNIQUE INDEX blocks_hash ON blocks(hash)");
	    UTILS.DB.executeUpdate("CREATE INDEX blocks_block ON blocks(block)");
	    UTILS.DB.executeUpdate("CREATE INDEX blocks_signer ON blocks(signer)");
            UTILS.DB.executeUpdate("CREATE INDEX blocks_prev_hash ON blocks(prev_hash)");
     
            
            UTILS.DB.executeUpdate("INSERT INTO blocks "
                                         + "SET hash='0000000000000000000000000000000000000000000000000000000000000000', "
                                             + "block='0', "
                                             + "prev_hash='0000000000000000000000000000000000000000000000000000000000000000', "
                                             + "signer='default', "
                                             + "packets='0', "
                                             + "tstamp='0', "
                                             + "nonce='0', "
                                             + "size='0', "
                                             + "commited='0', "
                                             + "confirmations='0', "
                                             + "net_dif='0000000fdc8eb4c424a4ab9659f606c254071192c1abd28ca94ee63f88323bbf', "
                                             + "payload_hash='0000000000000000000000000000000000000000000000000000000000000000'");
         }
         
         // ---------------------------------- Connection Log--------------------------------------
	 if (tab.equals("con_log"))
         {
	    UTILS.DB.executeUpdate("CREATE TABLE con_log(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
			 	 		       + "IP VARCHAR(30) NOT NULL DEFAULT '', "
                                                       + "port BIGINT NOT NULL DEFAULT 0, "
			 	 	 	       + "tstamp BIGINT NOT NULL DEFAULT 0)");
	    
            UTILS.DB.executeUpdate("CREATE INDEX con_log_ip ON con_log(ip)");
	 }
         
         // ------------------------------- Domains --------------------------------------
	if (tab.equals("domains"))
	{
	   CDomainsTable domains=new CDomainsTable();
           domains.create();
	}
        
         // ------------------------------- Assets mkts --------------------------------------
	if (tab.equals("assets_mkts"))
	{
	   CAssetsMktsTable assets_mkts=new CAssetsMktsTable();
           assets_mkts.create();
	}
        
         // ------------------------------- Assets mkts pos --------------------------------------
	if (tab.equals("assets_mkts_pos"))
	{
	   CAssetsMktsPosTable assets_mkts_pos=new CAssetsMktsPosTable();
           assets_mkts_pos.create();
	}
        
        // ------------------------------------- Tweets ------------------------------------------------
         if (tab.equals("tweets"))
         {
             CTweetsTable tweets=new CTweetsTable();
             tweets.create();
         }
         
         // ------------------------------------- Tweets Comments ------------------------------------------------
         if (tab.equals("comments"))
         {
             CCommentsTable comments=new CCommentsTable();
             comments.create();
            
         }
         
         // ------------------------------------- Tweets Follow ------------------------------------------------
         if (tab.equals("tweets_follow"))
         {
             CTweetsFollowTable tweets_follow=new CTweetsFollowTable();
             tweets_follow.create();
         }
         
         // ------------------------------------- Tweets Likes ------------------------------------------------
         if (tab.equals("votes"))
         {
             CVotesTable votes=new CVotesTable();
             votes.create();
         }
     
        
         // ---------------------------------- Error Log--------------------------------------
	 if (tab.equals("err_log"))
         {
	    UTILS.DB.executeUpdate("CREATE TABLE err_log(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
			 	 		       + "type VARCHAR(250) NOT NULL DEFAULT '', "
			 	 	 	       + "mes LONGTEXT, "
			 	 	 	       + "file VARCHAR(100) NOT NULL DEFAULT '', "
                                                       + "line BIGINT NOT NULL DEFAULT 0, "
			 	 	 	       + "tstamp BIGINT NOT NULL DEFAULT 0)");
				    
	    UTILS.DB.executeUpdate("CREATE INDEX err_log_type ON err_log(type)");
            UTILS.DB.executeUpdate("CREATE INDEX err_log_file ON err_log(file)");
	}
         
         // ---------------------------------- Escrowed--------------------------------------
	 if (tab.equals("escrowed"))
         {
	    CEscrowedTable escrowed=new CEscrowedTable();
            escrowed.create();
	 }
         
         // ---------------------------------- packets--------------------------------------
	 if (tab.equals("packets"))
         {
	    UTILS.DB.executeUpdate("CREATE TABLE packets(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
			 	 		       + "packet_hash VARCHAR(250) NOT NULL DEFAULT '', "
			 	 	 	       + "par_1_name VARCHAR(100) NOT NULL DEFAULT '', "
			 	 	 	       + "par_1_val TEXT, "
                                                       + "par_2_name VARCHAR(100) NOT NULL DEFAULT '', "
			 	 	 	       + "par_2_val TEXT, "
                                                       + "par_3_name VARCHAR(100) NOT NULL DEFAULT '', "
			 	 	 	       + "par_3_val TEXT, "
                                                       + "par_4_name VARCHAR(100) NOT NULL DEFAULT '', "
			 	 	 	       + "par_4_val TEXT, "
                                                       + "par_5_name VARCHAR(100) NOT NULL DEFAULT '', "
			 	 	 	       + "par_5_val TEXT, "
                                                       + "par_6_name VARCHAR(100) NOT NULL DEFAULT '', "
			 	 	 	       + "par_6_val TEXT, "
                                                       + "par_7_name VARCHAR(100) NOT NULL DEFAULT '', "
			 	 	 	       + "par_7_val TEXT, "
                                                       + "par_8_name VARCHAR(100) NOT NULL DEFAULT '', "
			 	 	 	       + "par_8_val TEXT, "
                                                       + "par_9_name VARCHAR(100) NOT NULL DEFAULT '', "
			 	 	 	       + "par_9_val TEXT, "
                                                       + "par_10_name VARCHAR(100) NOT NULL DEFAULT '', "
			 	 	 	       + "par_10_val TEXT, "
                                                       + "par_11_name VARCHAR(100) NOT NULL DEFAULT '', "
			 	 	 	       + "par_11_val TEXT, "
                                                       + "par_12_name VARCHAR(100) NOT NULL DEFAULT '', "
			 	 	 	       + "par_12_val TEXT, "
                                                       + "par_13_name VARCHAR(100) NOT NULL DEFAULT '', "
			 	 	 	       + "par_13_val TEXT, "
                                                       + "par_14_name VARCHAR(100) NOT NULL DEFAULT '', "
			 	 	 	       + "par_14_val TEXT, "
                                                       + "par_15_name VARCHAR(100) NOT NULL DEFAULT '', "
			 	 	 	       + "par_15_val TEXT, "
                                                       + "par_16_name VARCHAR(100) NOT NULL DEFAULT '', "
			 	 	 	       + "par_16_val TEXT, "
                                                       + "par_17_name VARCHAR(100) NOT NULL DEFAULT '', "
			 	 	 	       + "par_17_val TEXT, "
                                                       + "par_18_name VARCHAR(100) NOT NULL DEFAULT '', "
			 	 	 	       + "par_18_val TEXT, "
                                                       + "par_19_name VARCHAR(100) NOT NULL DEFAULT '', "
			 	 	 	       + "par_19_val TEXT, "
                                                       + "par_20_name VARCHAR(100) NOT NULL DEFAULT '', "
			 	 	 	       + "par_20_val TEXT, "
                                                       + "block BIGINT NOT NULL DEFAULT 0, "
                                                       + "tstamp BIGINT NOT NULL DEFAULT 0, "
                                                       + "confirms BIGINT NOT NULL DEFAULT 0, "
                                                       + "block_hash VARCHAR(100) NOT NULL DEFAULT '', "
                                                       + "payload_hash VARCHAR(100) NOT NULL DEFAULT '', "
                                                       + "payload_size BIGINT NOT NULL DEFAULT 0, "
                                                       + "packet_type VARCHAR(100) NOT NULL DEFAULT '', "
                                                       + "fee_src VARCHAR(250) NOT NULL DEFAULT '', "
                                                       + "fee_amount DOUBLE(20, 8) NOT NULL DEFAULT 0, "
                                                       + "fee_hash VARCHAR(250) NOT NULL DEFAULT '')");
				    
	    UTILS.DB.executeUpdate("CREATE INDEX packets_packet_hash ON packets(packet_hash)");
            UTILS.DB.executeUpdate("CREATE INDEX packets_block ON packets(block)");
            UTILS.DB.executeUpdate("CREATE INDEX packets_block_hash ON packets(block_hash)");
            UTILS.DB.executeUpdate("CREATE INDEX packets_payload_hash ON packets(payload_hash)");
            UTILS.DB.executeUpdate("CREATE INDEX packets_fee_hash ON packets(fee_hash)");
        }
	     
	
         // ---------------------------------- Messages --------------------------------------
	 if (tab.equals("mes"))
         {
	    UTILS.DB.executeUpdate("CREATE TABLE mes(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                  + "from_adr VARCHAR(250) NOT NULL DEFAULT '', "
                                                  + "to_adr VARCHAR(250) NOT NULL DEFAULT '', "
                                                  + "subject VARCHAR(250) NOT NULL DEFAULT '', "
                                                  + "mes VARCHAR(5000) NOT NULL DEFAULT '', "
                                                  + "status VARCHAR(20) NOT NULL DEFAULT '', "
                                                  + "tstamp BIGINT NOT NULL DEFAULT 0, "
                                                  + "tgt VARCHAR(250))");
				    
	    UTILS.DB.executeUpdate("CREATE INDEX mes_from_adr ON mes(from_adr)");
         }
         
          
         // ---------------------------------- My addresses --------------------------------------
	 if (tab.equals("my_adr"))
         {
	    UTILS.DB.executeUpdate("CREATE TABLE my_adr(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
				    		     + "userID BIGINT NOT NULL DEFAULT 0, "
				    		     + "adr VARCHAR(250) NOT NULL DEFAULT '',"
				    		     + "description VARCHAR(100) NOT NULL DEFAULT 'No description provided')");
				    
	    UTILS.DB.executeUpdate("CREATE INDEX my_adr_adr ON my_adr(adr)");
	    UTILS.DB.executeUpdate("CREATE INDEX my_adr_userID ON my_adr(userID)");
         }
         
         // ---------------------------------- Net stat --------------------------------------
	 if (tab.equals("net_stat"))
         {
             UTILS.DB.executeUpdate("CREATE TABLE net_stat(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
				    		       + "last_block BIGINT NOT NULL DEFAULT 0, "
				    		       + "last_block_hash VARCHAR(250) NOT NULL DEFAULT '',"
                                                       + "block_confirm_min_balance DOUBLE(20,4) DEFAULT 1,"
                                                       + "net_dif VARCHAR(100) NOT NULL DEFAULT '0',"
                                                       + "delegate VARCHAR(500) NOT NULL DEFAULT '',"
                                                       + "sql_log_status VARCHAR(100) NOT NULL DEFAULT '',"
                                                       + "sync_target BIGINT NOT NULL DEFAULT 0, "
                                                       + "sync_start BIGINT NOT NULL DEFAULT 0, "
                                                       + "last_tstamp BIGINT NOT NULL DEFAULT 0)");
            
            UTILS.DB.executeUpdate("INSERT INTO net_stat "
                                         + "SET last_block='0', "
                                             + "last_block_hash='0000000000000000000000000000000000000000000000000000000000000000', "
                                             + "net_dif='0000000fdc8eb4c424a4ab9659f606c254071192c1abd28ca94ee63f88323bbf'");
	}
        
        // ----------------------------------- My Trans --------------------------------------
	if (tab.equals("my_trans"))
	{
	    UTILS.DB.executeUpdate("CREATE TABLE my_trans(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
	                                               + "userID BIGINT NOT NULL DEFAULT 0, "
                                                       + "adr VARCHAR(250) NOT NULL DEFAULT '', "
                                                       + "adr_assoc VARCHAR(250) NOT NULL DEFAULT '', "
	 	 	                               + "amount DOUBLE(20, 8) NOT NULL DEFAULT 0, "
                                                       + "invested DOUBLE(20, 8) NOT NULL DEFAULT 0, "
	 	 	 			       + "cur VARCHAR(10) NOT NULL DEFAULT '', "
	 	 	 			       + "expl VARCHAR(250) NOT NULL DEFAULT '', "
	 	 	 			       + "escrower VARCHAR(250) NOT NULL DEFAULT '', "
	 	 	 			       + "hash VARCHAR(100) NOT NULL DEFAULT '', "
                                                       + "tID BIGINT NOT NULL DEFAULT 0, "
                                                       + "block BIGINT NOT NULL DEFAULT 0, "
                                                       + "block_hash VARCHAR(100) NOT NULL DEFAULT '', "
                                                       + "tstamp BIGINT NOT NULL DEFAULT 0, "
	 	 	 			       + "cartID BIGINT NOT NULL DEFAULT 0, "
                                                       + "mes VARCHAR(2000) NOT NULL DEFAULT '')");
	 	 	 	   
	    UTILS.DB.executeUpdate("CREATE INDEX mt_userID ON my_trans(userID)");
	    UTILS.DB.executeUpdate("CREATE INDEX mt_adr ON my_trans(adr)");
            UTILS.DB.executeUpdate("CREATE INDEX mt_hash ON my_trans(hash)");
            UTILS.DB.executeUpdate("CREATE INDEX mt_block ON my_trans(block)");
            UTILS.DB.executeUpdate("CREATE INDEX mt_cur ON my_trans(cur)");
            UTILS.DB.executeUpdate("CREATE INDEX mt_block_hash ON my_trans(block_hash)");
	}
         
         // --------------------------------IPN Log -------------------------------------
	 if (tab.equals("ipn_log"))
	 {
	    	 UTILS.DB.executeUpdate("CREATE TABLE ipn_log (ID BIGINT AUTO_INCREMENT PRIMARY KEY,"
                                          +"ipnID BIGINT NOT NULL DEFAULT 0,"
                                          +"adr VARCHAR(250) NOT NULL DEFAULT '',"
                                          +"tip VARCHAR(10) NOT NULL DEFAULT 'ID_WEB',"
                                          +"data VARCHAR(1000) NOT NULL DEFAULT '',"
                                          +"amount DOUBLE NOT NULL DEFAULT 0,"
                                          +"cur VARCHAR(10) NOT NULL DEFAULT 'MSK',"
                                          +"txid VARCHAR(100) NOT NULL DEFAULT '',"
                                          +"status VARCHAR(10) NOT NULL DEFAULT 'ID_OK',"
                                          +"tstamp BIGINT NOT NULL DEFAULT 0)");
	    			 
	    	 UTILS.DB.executeUpdate("CREATE INDEX ipn_id ON ipn_log(ipnID)");
	    	 UTILS.DB.executeUpdate("CREATE INDEX ipn_iadr ON ipn_log(adr)");
	}
         
         // ------------------------------------- IPN ------------------------------------
	 if (tab.equals("ipn"))
	 {
	    UTILS.DB.executeUpdate("CREATE TABLE ipn(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
	 	 				  + "adr VARCHAR(250) NOT NULL DEFAULT '', "
	 	 				  + "email VARCHAR(100), "
	 	 				  + "web_pass VARCHAR(100), "
	 	 				  + "web_min DOUBLE(10,4) NOT NULL DEFAULT 0, "
	 	 				  + "email_min DOUBLE(10,4) NOT NULL DEFAULT 0, "
	 	 				  + "web_link VARCHAR(1000), "
	 	 				  + "web_only_confirmed VARCHAR(10), "
	 	 				  + "web_mes VARCHAR(10), "
	 	 				  + "email_only_confirmed VARCHAR(10), "
	 	 				  + "email_mes VARCHAR(10), "
	 	 				  + "web_status VARCHAR(25), "
	 	 				  + "email_status VARCHAR(25), "
	 	 				  + "tstamp BIGINT NOT NULL DEFAULT 0)");
	 	 	   
	    UTILS.DB.executeUpdate("CREATE INDEX ipn_adr ON ipn(adr)");
	}
         
          
         
          // ----------------------------------- Assets Markets Trades --------------------------------------
         if (tab.equals("assets_mkts_trades"))
	 {
	    UTILS.DB.executeUpdate("CREATE TABLE assets_mkts_trades(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
	 					                 + "mktID BIGINT NOT NULL DEFAULT 0, "
                                                                 + "orderID BIGINT NOT NULL DEFAULT 0, "
	 					                 + "buyer VARCHAR(250) NOT NULL DEFAULT '', "
	 					                 + "seller VARCHAR(250) NOT NULL DEFAULT '', "
                                                                 + "qty DOUBLE(20, 8) NOT NULL DEFAULT 0, "
	 					                 + "price DOUBLE(20, 8) NOT NULL DEFAULT 0, "
                                                                 + "block BIGINT NOT NULL DEFAULT 0)");
	    
            UTILS.DB.executeUpdate("CREATE INDEX assets_mkts_trades_mktID ON assets_mkts_trades(mktID)");
            UTILS.DB.executeUpdate("CREATE INDEX assets_mkts_trades_orderID ON assets_mkts_trades(orderID)");
            UTILS.DB.executeUpdate("CREATE INDEX assets_mkts_trades_block ON assets_mkts_trades(block)");
	}
        
         
         
         
         // ----------------------------------- SQL Log --------------------------------------
         if (tab.equals("sql_log"))
	 {
	    UTILS.DB.executeUpdate("CREATE TABLE sql_log(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
	 					       + "query VARCHAR(2500) NOT NULL DEFAULT '', "
	 					       + "block BIGINT NOT NULL DEFAULT 0, "
	 					       + "hash VARCHAR(100))");
	    
            UTILS.DB.executeUpdate("CREATE INDEX sql_log_block ON sql_log(block)");
            UTILS.DB.executeUpdate("CREATE INDEX sql_log_hash ON sql_log(hash)");
	}
         
          // ----------------------------------- Packets --------------------------------------
         if (tab.equals("rec_packets"))
	 {
	    UTILS.DB.executeUpdate("CREATE TABLE rec_packets(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
	 					       + "tip VARCHAR(100) NOT NULL DEFAULT '', "
	 					       + "fromIP  VARCHAR(100) NOT NULL DEFAULT '', "
	 					       + "tstamp  BIGINT NOT NULL DEFAULT 0, "
                                                       + "hash VARCHAR(100) NOT NULL DEFAULT '')");
	    
            UTILS.DB.executeUpdate("CREATE INDEX rec_packets_hash ON rec_packets(hash)");
	}
         
         // ---------------------------------- Peers ---------------------------------------
	 if (tab.equals("peers"))
	 {
	    UTILS.DB.executeUpdate("CREATE TABLE peers(ID BIGINT AUTO_INCREMENT PRIMARY KEY," 
                                                      +"peer VARCHAR(30),"
                                                      +"port BIGINT NOT NULL DEFAULT 0,"
                                                      +"in_traffic BIGINT NOT NULL DEFAULT 0,"
                                                      +"out_traffic BIGINT NOT NULL DEFAULT 0,"
                                                      +"last_seen BIGINT NOT NULL DEFAULT 0,"
                                                      +"ver VARCHAR(10) NOT NULL DEFAULT '',"
                                                      +"tstamp BIGINT NOT NULL DEFAULT 0)");
	  }
         
         
         // ---------------------------------- Peers Pool -----------------------------------
	 if (tab.equals("peers_pool"))
	 {
	    UTILS.DB.executeUpdate("CREATE TABLE peers_pool(ID BIGINT AUTO_INCREMENT PRIMARY KEY," 
                                                           +"peer VARCHAR(30),"
                                                           +"port BIGINT NOT NULL DEFAULT 0,"
                                                           +"con_att_no BIGINT NOT NULL DEFAULT 0,"
                                                           +"con_att_last BIGINT NOT NULL DEFAULT 0,"
                                                           +"accept_con VARCHAR(10) NOT NULL DEFAULT 'ID_NO',"
                                                           +"last_seen BIGINT NOT NULL DEFAULT 0,"
                                                           +"banned BIGINT NOT NULL DEFAULT 0)");
            
            UTILS.DB.executeUpdate("CREATE INDEX peers_pool_peer ON peers_pool(peer)");
	  }
         
        
         
         // ----------------------------------- Trans --------------------------------------  
	if (tab.equals("trans"))
	{
	    UTILS.DB.executeUpdate("CREATE TABLE trans(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
	                                            + "src VARCHAR(250) NOT NULL DEFAULT '', "
	 	 	 			    + "amount DOUBLE(20,8) NOT NULL DEFAULT 0, "
                                                    + "invested DOUBLE(20,8) NOT NULL DEFAULT 0, "
	 	 	 			    + "cur VARCHAR(10) NOT NULL DEFAULT '', "
	 	 	 			    + "escrower VARCHAR(250) NOT NULL DEFAULT '', "
	 	 	 			    + "hash VARCHAR(100) NOT NULL DEFAULT '', "
                                                    + "tID BIGINT NOT NULL DEFAULT 0, "
	 	 	 			    + "block BIGINT NOT NULL DEFAULT 0, "
                                                    + "block_hash VARCHAR(100) NOT NULL DEFAULT '', "
                                                    + "status VARCHAR(20) NOT NULL DEFAULT '', "
	 	 	 			    + "tstamp BIGINT NOT NULL DEFAULT 0)");
	 	 	 	   
	    UTILS.DB.executeUpdate("CREATE INDEX trans_src ON trans(src)");
            UTILS.DB.executeUpdate("CREATE INDEX trans_block ON trans(block)");
            UTILS.DB.executeUpdate("CREATE INDEX trans_hash ON trans(hash)");
	}
         
         // ---------------------------------- Web log --------------------------------------
	 if (tab.equals("web_ops"))
	 {
	    UTILS.DB.executeUpdate("CREATE TABLE web_ops(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
			 	 		        + "user VARCHAR(50), "
			 	 			+ "fee_adr VARCHAR(250), "
			 	 			+ "target_adr VARCHAR(250), "
			 	 			+ "op VARCHAR(50), "
			 	 			+ "bid DOUBLE(10, 4) NOT NULL DEFAULT 0, "
			 	 			+ "days BIGINT NOT NULL DEFAULT 0, "
			 	 			+ "par_1 TEXT, "
			 	 			+ "par_2 TEXT, "
			 	 			+ "par_3 TEXT, "
			 	 			+ "par_4 TEXT, "
			 	 			+ "par_5 TEXT, "
			 	 			+ "par_6 TEXT, "
			 	 			+ "par_7 TEXT, "
			 	 			+ "par_8 TEXT, "
			 	 			+ "par_9 TEXT, "
			 	 			+ "par_10 TEXT, "
                                                        + "par_11 TEXT, "
                                                        + "par_12 TEXT, "
                                                        + "par_13 TEXT, "
                                                        + "par_14 TEXT, "
                                                        + "par_15 TEXT, "
                                                        + "par_16 TEXT, "
                                                        + "par_17 TEXT, "
                                                        + "par_18 TEXT, "
                                                        + "par_19 TEXT, "
                                                        + "par_20 TEXT, "
                                                        + "par_21 TEXT, "
                                                        + "par_22 TEXT, "
                                                        + "par_23 TEXT, "
                                                        + "par_24 TEXT, "
                                                        + "par_25 TEXT, "
                                                        + "resp_1 TEXT, "
                                                        + "resp_2 TEXT, "
			 	 			+ "amount DOUBLE, "
			 	 			+ "cur VARCHAR(100), "
			 	 			+ "packet_sign VARCHAR(250) NOT NULL DEFAULT '', "
			 	 			+ "payload_sign VARCHAR(250) NOT NULL DEFAULT '', "
			 	 			+ "website_3 VARCHAR(250), "
			 	 			+ "website_4 VARCHAR(250), "
			 	 			+ "pic_1 VARCHAR(250), "
			 	 			+ "pic_2 VARCHAR(250), "
			 	 			+ "pic_3 VARCHAR(250), "
			 	 			+ "pic_4 VARCHAR(250), "
			 	 			+ "pic_5 VARCHAR(250), "
			 	 			+ "status VARCHAR(50), "
			 	 			+ "response VARCHAR(500), "
			 	 			+ "tstamp BIGINT NOT NULL DEFAULT 0, "
			 	 			+ "tID VARCHAR(50) NOT NULL DEFAULT '', "
			 	 	 	 	+ "refs BIGINT NOT NULL DEFAULT 0)");
				    
	    UTILS.DB.executeUpdate("CREATE INDEX wop_user ON web_ops(user)");
	    UTILS.DB.executeUpdate("CREATE INDEX wop_op ON web_ops(op)");
	    UTILS.DB.executeUpdate("CREATE INDEX wop_status ON web_ops(status)");			    
        }
        
        
        
        // ----------------------- Trans pool --------------------------------------
	if (tab.equals("trans_pool"))
        {
	    UTILS.DB.executeUpdate("CREATE TABLE trans_pool(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
			 	 		   + "src VARCHAR(250) NOT NULL DEFAULT '', "
			 	 	 	   + "amount DOUBLE(10,4) NOT NULL DEFAULT 0, "
			 	 	 	   + "src_balance DOUBLE(10,4) NOT NULL DEFAULT 0, "
			 	 	 	   + "block BIGINT NOT NULL DEFAULT 0, "
			 	 	 	   + "cur VARCHAR(10), "
			 	 	 	   + "hash VARCHAR(100))");
				    
	    UTILS.DB.executeUpdate("CREATE INDEX trans_pool_src ON trans_pool(src)");
        }
        
          // ------------------------------------- Web Sys Data ------------------------------------------------
         if (tab.equals("web_sys_data"))
         {
             UTILS.DB.executeUpdate("CREATE TABLE web_sys_data(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                             +"status VARCHAR(50) NOT NULL DEFAULT '', "
                                                             +"last_ping BIGINT NOT NULL DEFAULT '0', "
                                                             +"free_memory BIGINT NOT NULL DEFAULT '0', "
                                                             +"total_memory BIGINT NOT NULL DEFAULT '0', "
                                                             +"max_memory BIGINT NOT NULL DEFAULT '0', "
                                                             +"procs BIGINT NOT NULL DEFAULT '0', "
                                                             +"threads_no BIGINT NOT NULL DEFAULT '0', "
                                                             +"uptime BIGINT NOT NULL DEFAULT '0', "
                                                             +"mining BIGINT NOT NULL DEFAULT '0', "
                                                             +"hashing_power BIGINT NOT NULL DEFAULT '0', "
                                                             +"mining_threads BIGINT NOT NULL DEFAULT '0', "
                                                             +"new_acc_reward_adr VARCHAR(500) NOT NULL DEFAULT '', "
                                                             +"new_acc_reward FLOAT(9,4) NOT NULL DEFAULT '0', "
                                                             +"root_whitelist_ip VARCHAR(5000) NOT NULL DEFAULT '', "
                                                             +"cpu_1_power FLOAT(9,2) NOT NULL DEFAULT '0', "
                                                             +"cpu_2_power FLOAT(9,2) NOT NULL DEFAULT '0', "
                                                             +"cpu_3_power FLOAT(9,2) NOT NULL DEFAULT '0', "
                                                             +"cpu_4_power FLOAT(9,2) NOT NULL DEFAULT '0', "
                                                             +"cpu_5_power FLOAT(9,2) NOT NULL DEFAULT '0', "
                                                             +"cpu_6_power FLOAT(9,2) NOT NULL DEFAULT '0', "
                                                             +"cpu_7_power FLOAT(9,2) NOT NULL DEFAULT '0', "
                                                             +"cpu_8_power FLOAT(9,2) NOT NULL DEFAULT '0', "
                                                             +"cpu_9_power FLOAT(9,2) NOT NULL DEFAULT '0', "
                                                             +"cpu_10_power FLOAT(9,2) NOT NULL DEFAULT '0', "
                                                             +"cpu_11_power FLOAT(9,2) NOT NULL DEFAULT '0', "
                                                             +"cpu_12_power FLOAT(9,2) NOT NULL DEFAULT '0', "
                                                             +"cpu_13_power FLOAT(9,2) NOT NULL DEFAULT '0', "
                                                             +"cpu_14_power FLOAT(9,2) NOT NULL DEFAULT '0', "
                                                             +"cpu_15_power FLOAT(9,2) NOT NULL DEFAULT '0', "
                                                             +"cpu_16_power FLOAT(9,2) NOT NULL DEFAULT '0', "
                                                             +"cpu_17_power FLOAT(9,2) NOT NULL DEFAULT '0', "
                                                             +"cpu_18_power FLOAT(9,2) NOT NULL DEFAULT '0', "
                                                             +"cpu_19_power FLOAT(9,2) NOT NULL DEFAULT '0', "
                                                             +"cpu_20_power FLOAT(9,2) NOT NULL DEFAULT '0', "
                                                             +"cpu_21_power FLOAT(9,2) NOT NULL DEFAULT '0', "
                                                             +"cpu_22_power FLOAT(9,2) NOT NULL DEFAULT '0', "
                                                             +"cpu_23_power FLOAT(9,2) NOT NULL DEFAULT '0', "
                                                             +"cpu_24_power FLOAT(9,2) NOT NULL DEFAULT '0', "
                                                             +"version VARCHAR(20) NOT NULL DEFAULT '0.0.1', "
                                                             +"engine_status VARCHAR(20) NOT NULL DEFAULT 'ID_ONLINE', "
                                                             +"MSK_price DOUBLE(9,4) NOT NULL DEFAULT 0)");
             
             UTILS.DB.executeUpdate("INSERT INTO web_sys_data(status, MSK_price) VALUES('ID_OFFLINE', '1')");
         }
         
          // ------------------------------------- Sync ------------------------------------------------
         if (tab.equals("sync"))
         {
             UTILS.DB.executeUpdate("CREATE TABLE sync(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                       +"status VARCHAR(50) NOT NULL DEFAULT '', "
                                                       +"peer VARCHAR(20) NOT NULL DEFAULT '', "
                                                       +"type VARCHAR(50) NOT NULL DEFAULT '', "
                                                       +"tab VARCHAR(100) NOT NULL DEFAULT '', "
                                                       +"start BIGINT NOT NULL DEFAULT '0', "
                                                       +"end BIGINT NOT NULL DEFAULT '0', "
                                                       +"tstamp BIGINT NOT NULL DEFAULT '0')");
             
            
         }
        
         
         // ------------------------------------- Profiles ------------------------------------------------
         if (tab.equals("profiles"))
         {
             CProfilesTable profiles=new CProfilesTable();
             profiles.create();
         }
         
          // ------------------------------------- Web Users ------------------------------------------------
         if (tab.equals("web_users"))
         {
             UTILS.DB.executeUpdate("CREATE TABLE web_users(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                             +"user VARCHAR(50) NOT NULL DEFAULT '', "
                                                             +"pass VARCHAR(100) NOT NULL DEFAULT '', "
                                                             +"email VARCHAR(100) NOT NULL DEFAULT '', "
                                                             +"IP VARCHAR(20) NOT NULL DEFAULT '', "
                                                             +"status VARCHAR(50) NOT NULL DEFAULT '', "
                                                             +"api_key VARCHAR(100) NOT NULL DEFAULT '', "
                                                             +"ref_adr BIGINT NOT NULL DEFAULT 0, "
                                                             +"tstamp BIGINT NOT NULL DEFAULT 0, "
                                                             + "unread_esc BIGINT NOT NULL DEFAULT 0, "
                                                             + "unread_mes BIGINT NOT NULL DEFAULT 0, "
                                                             + "unread_trans BIGINT NOT NULL DEFAULT 0)");
             
             UTILS.DB.executeUpdate("CREATE INDEX web_users_user ON web_users(user)");
             UTILS.DB.executeUpdate("CREATE INDEX web_users_email ON web_users(email)");
             
             // Insert default user
             UTILS.DB.executeUpdate("INSERT INTO web_users(user, "
                                                        + "pass, "
                                                        + "email, "
                                                        + "status, "
                                                        + "tstamp) VALUES("
                                                        + "'root', '"
                                                        +UTILS.BASIC.hash("root")+"', "
                                                        + "'', "
                                                        + "'', '"
                                                        +UTILS.BASIC.tstamp()+"')");
         }
         
         
         // ------------------------------------- Feeds Sources ------------------------------------------------
         if (tab.equals("feeds_sources"))
         {
              UTILS.DB.executeUpdate("CREATE TABLE feeds_sources(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                                 +"feed_symbol VARCHAR(100) NOT NULL DEFAULT '', "
                                                                 + "url VARCHAR(500) NOT NULL DEFAULT '', "
                                                                 + "adr VARCHAR(250) NOT NULL DEFAULT '', "
                                                                 + "ping_interval BIGINT DEFAULT 30, "
                                                                 + "next_run BIGINT NOT NULL DEFAULT 0)");
             
             UTILS.DB.executeUpdate("CREATE INDEX feeds_sources ON feeds_sources(feed_symbol)");
         }
         
         // ------------------------------------- Feeds Data ------------------------------------------------
         if (tab.equals("feeds_data"))
         {
             UTILS.DB.executeUpdate("CREATE TABLE feeds_data(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                            +"feed VARCHAR(10) NOT NULL DEFAULT '', "
                                                            +"feed_branch VARCHAR(20) NOT NULL DEFAULT '', "
                                                            +"val DOUBLE(20, 8) NOT NULL DEFAULT 0, "
                                                            +"mkt_status VARCHAR(20) NOT NULL DEFAULT 'ID_OPEN', "
                                                            +"tstamp BIGINT NOT NULL DEFAULT 0, "
                                                            +"block BIGINT NOT NULL DEFAULT 0)");
             
             UTILS.DB.executeUpdate("CREATE INDEX feeds_data_feed ON feeds_data(feed)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_data_feed_branch ON feeds_data(feed_branch)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_data_block ON feeds_data(block)");
         }
         
         // ------------------------------------- Feeds Pos Data ------------------------------------------------
         if (tab.equals("feeds_pos_data"))
         {
             UTILS.DB.executeUpdate("CREATE TABLE feeds_pos_data(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                                +"pos_type VARCHAR(30) NOT NULL DEFAULT '', "
                                                                +"posID BIGINT NOT NULL DEFAULT 0, "
                                                                +"pos_symbol VARCHAR(10) NOT NULL DEFAULT '', "
                                                                +"val DOUBLE(20,8) NOT NULL DEFAULT 0, "
                                                                +"block BIGINT NOT NULL DEFAULT 0)");
             
             UTILS.DB.executeUpdate("CREATE INDEX feeds_pos_posID ON feeds_pos_data(posID)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_pos_pos_symbol ON feeds_pos_data(pos_symbol)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_pos_data_block ON feeds_pos_data(block)");
         }
         
         // ------------------------------------- Feeds Sources Res ------------------------------------------------
         if (tab.equals("feeds_sources_res"))
         {
             UTILS.DB.executeUpdate("CREATE TABLE feeds_sources_res(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                                  +"feed VARCHAR(10) NOT NULL DEFAULT '', "
                                                                  +"result VARCHAR(10) NOT NULL DEFAULT '', "
                                                                  +"err_mes VARCHAR(1000) NOT NULL DEFAULT '', "
                                                                  +"tstamp BIGINT NOT NULL DEFAULT 0)");
             
             UTILS.DB.executeUpdate("CREATE INDEX feeds_sources_res_feed ON feeds_sources_res(feed)");
            
         }
         
         // ------------------------------------- Feeds Sources Res ------------------------------------------------
         if (tab.equals("checkpoints"))
         {
             UTILS.DB.executeUpdate("CREATE TABLE checkpoints(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                              +"block BIGINT NOT NULL DEFAULT 0, "
                                                              +"hash VARCHAR(100) NOT NULL DEFAULT '')");
             
             UTILS.DB.executeUpdate("CREATE INDEX checkpoints_block ON checkpoints(block)");
             UTILS.DB.executeUpdate("CREATE INDEX checkpoints_hash ON checkpoints(hash)");
         }
         
         // ------------------------------------- Assets ------------------------------------------------
         if (tab.equals("assets"))
         {
             CAssetsTable assets=new CAssetsTable();
             assets.create(); 
         }
         
         // Speculative markets
         if (tab.equals("feeds_spec_mkts"))
         {
             CFeedsSpecMktsTable spec_mkts=new CFeedsSpecMktsTable();
             spec_mkts.create(); 
         }
         
         // ------------------------------------- Assets Owners ------------------------------------------------
         if (tab.equals("assets_owners"))
         {
             CAssetsOwnersTable assets_owners=new CAssetsOwnersTable();
             assets_owners.create(); 
         }
         
          // ------------------------------------- Feeds ------------------------------------------------
         if (tab.equals("feeds"))
         {
             CFeedsTable feeds=new CFeedsTable();
             feeds.create(); 
         }
         
          // ------------------------------------- Feeds branches ------------------------------------------------
         if (tab.equals("feeds_branches"))
         {
             CFeedsBranchesTable feeds_branches=new CFeedsBranchesTable();
             feeds_branches.create(); 
         }
         
         
         // ------------------------------------- Delegates votes  ------------------------------------------------
         if (tab.equals("del_votes"))
         {
             CDelVotesTable del_votes=new CDelVotesTable();
             del_votes.create(); 
         }
         
         // ----------------------------------- Speculative Markets -----------------------------------------------
         if (tab.equals("feeds_spec_mkts_pos"))
         {
            CFeedsSpecMktsPosTable spec_mkts_pos=new CFeedsSpecMktsPosTable();
            spec_mkts_pos.create();
         }
         
         // ------------------------------------- Feeds bets  ------------------------------------------------
         if (tab.equals("feeds_bets"))
         {
             CFeedsBetsTable feeds_bets=new CFeedsBetsTable();
             feeds_bets.create(); 
         }
         
         // ------------------------------------- Feeds bets pos  ------------------------------------------------
         if (tab.equals("feeds_bets_pos"))
         {
             CFeedsBetsPosTable feeds_bets_pos=new CFeedsBetsPosTable();
             feeds_bets_pos.create(); 
         }
         
         // ------------------------------------- Address atributes  ------------------------------------------------
         if (tab.equals("adr_attr"))
         {
             CAdrAttrTable adr_attr=new CAdrAttrTable();
             adr_attr.create(); 
         }
         
         
         // ------------------------------------- Agents Emails ------------------------------------------------
         if (tab.equals("out_emails"))
         {
              UTILS.DB.executeUpdate("CREATE TABLE out_emails(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                                +"format VARCHAR(50) NOT NULL DEFAULT '', "
                                                                +"sender VARCHAR(250) NOT NULL DEFAULT '', "
                                                                +"dest VARCHAR(250) NOT NULL DEFAULT '', "
                                                                +"subject VARCHAR(1000) NOT NULL DEFAULT '', "
                                                                +"message VARCHAR(2500) NOT NULL DEFAULT '', "
                                                                +"adr VARCHAR(500) NOT NULL DEFAULT '', "
                                                                +"tstamp BIGINT NOT NULL DEFAULT 0, "
                                                                +"status VARCHAR(25) NOT NULL DEFAULT 'ID_PENDING')");
         }
         
         // ------------------------------------- Status Log ------------------------------------------------
         if (tab.equals("status_log"))
         {
              UTILS.DB.executeUpdate("CREATE TABLE status_log(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                                +"total_mem BIGINT NOT NULL DEFAULT 0, "
                                                                +"free_mem BIGINT NOT NULL DEFAULT 0, "
                                                                +"tstamp BIGINT NOT NULL DEFAULT 0, "
                                                                +"threads BIGINT NOT NULL DEFAULT 0)");
         }
         
         // ------------------------------------- Delegates Log ------------------------------------------------
         if (tab.equals("delegates_log"))
         {
              UTILS.DB.executeUpdate("CREATE TABLE delegates_log(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                                +"delegate VARCHAR(500) NOT NULL DEFAULT '', "
                                                                +"power BIGINT NOT NULL DEFAULT 0, "
                                                                +"block BIGINT NOT NULL DEFAULT 0)");
              
              UTILS.DB.executeUpdate("CREATE INDEX delegates_log_delegate ON delegates_log(delegate)");
              UTILS.DB.executeUpdate("CREATE INDEX delegates_log_block ON delegates_log(block)");
         }
         
         // ------------------------------------- Rewards ------------------------------------------------
         if (tab.equals("rewards"))
         {
              UTILS.DB.executeUpdate("CREATE TABLE rewards(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                         +"adr VARCHAR(500) NOT NULL DEFAULT '', "
                                                         +"target_type VARCHAR(50) NOT NULL DEFAULT '', "
                                                         +"targetID BIGINT NOT NULL DEFAULT 0, "
                                                         +"reward VARCHAR(100) NOT NULL DEFAULT '', "
                                                         +"amount FLOAT(9,4) NOT NULL DEFAULT '0', "
                                                         +"block BIGINT NOT NULL DEFAULT '0')");
             
             UTILS.DB.executeUpdate("CREATE INDEX rewards_adr ON rewards(adr)");
             UTILS.DB.executeUpdate("CREATE INDEX rewards_block ON rewards(block)");
             UTILS.DB.executeUpdate("CREATE INDEX rewards_targetID ON rewards(targetID)");
         }
         
         // ------------------------------------- Rewards ------------------------------------------------
         if (tab.equals("web_actions"))
         {
              UTILS.DB.executeUpdate("CREATE TABLE web_actions(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                              +"userID BIGINT NOT NULL DEFAULT '0', "
                                                              +"tip VARCHAR(100) NOT NULL DEFAULT '', "
                                                              +"par_1 VARCHAR(1000) NOT NULL DEFAULT '', "
                                                              +"par_2 VARCHAR(1000) NOT NULL DEFAULT '', "
                                                              +"par_3 VARCHAR(1000) NOT NULL DEFAULT '', "
                                                              +"tstamp BIGINT NOT NULL DEFAULT '0', "
                                                              +"IP VARCHAR(100) NOT NULL DEFAULT '')");
             
             UTILS.DB.executeUpdate("CREATE INDEX web_actions_userID ON web_actions(userID)");
         }
         
          // ------------------------------------- Affiliates stats ------------------------------------------------
         if (tab.equals("ref_stats"))
         {
              UTILS.DB.executeUpdate("CREATE TABLE ref_stats(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                              +"userID BIGINT NOT NULL DEFAULT '0', "
                                                              +"year BIGINT NOT NULL DEFAULT 0, "
                                                              +"month BIGINT NOT NULL DEFAULT 0, "
                                                              +"day BIGINT NOT NULL DEFAULT 0, "
                                                              +"hits BIGINT NOT NULL DEFAULT 0, "
                                                              +"signups BIGINT NOT NULL DEFAULT 0, "
                                                              +"ctr BIGINT NOT NULL DEFAULT 0, "
                                                              +"tstamp BIGINT NOT NULL DEFAULT 0)");
             
             UTILS.DB.executeUpdate("CREATE INDEX ref_stats_userID ON ref_stats(userID)");
             UTILS.DB.executeUpdate("CREATE INDEX ref_stats_year ON ref_stats(year)");
             UTILS.DB.executeUpdate("CREATE INDEX ref_stats_month ON ref_stats(month)");
             UTILS.DB.executeUpdate("CREATE INDEX ref_stats_day ON ref_stats(day)");
         }
         
          // ------------------------------------- Delegates ------------------------------------------------
         if (tab.equals("delegates"))
         {
              CDelegatesTable delegates=new CDelegatesTable();
              delegates.create(); 
         }
         
        // Done
        System.out.print("Done.");
     }
     
     public boolean tableExist(String tab) throws Exception
     {
	ResultSet rs;
      
	rs=UTILS.DB.executeQuery("SELECT * "
                                       + "FROM INFORMATION_SCHEMA.TABLES "
                                      + "WHERE TABLE_NAME='"+tab+"' "
	   		                + "AND TABLE_SCHEMA='"+UTILS.SETTINGS.db_name+"'");
	   
	   
        if (UTILS.DB.hasData(rs))
            return true;
        else
            return false;
    }
     
     public void checkTables() throws Exception
     {
         if (this.tableExist("adr")==false)
            this.createTable("adr");
        
        if (this.tableExist("ads")==false)
            this.createTable("ads");
        
        if (this.tableExist("blocks")==false)
            this.createTable("blocks");
        
        if (this.tableExist("blocks_pool")==false)
            this.createTable("blocks_pool");
        
        if (this.tableExist("con_log")==false)
            this.createTable("con_log");
        
        if (this.tableExist("domains")==false)
            this.createTable("domains");
        
        if (this.tableExist("checkpoints")==false)
            this.createTable("checkpoints");
        
        if (this.tableExist("err_log")==false)
            this.createTable("err_log");
        
        if (this.tableExist("escrowed")==false)
            this.createTable("escrowed");
        
        if (this.tableExist("packets")==false)
            this.createTable("packets");
        
        if (this.tableExist("mes")==false)
            this.createTable("mes");
        
        if (this.tableExist("net_stat")==false)
            this.createTable("net_stat");
        
        if (this.tableExist("ipn")==false)
            this.createTable("ipn");
        
        if (this.tableExist("ipn_log")==false)
            this.createTable("ipn_log");
        
        if (this.tableExist("my_adr")==false)
            this.createTable("my_adr");
        
        if (this.tableExist("my_trans")==false)
            this.createTable("my_trans");
        
        if (this.tableExist("rec_packets")==false)
            this.createTable("rec_packets");
        
        if (this.tableExist("peers")==false)
            this.createTable("peers");
        
        if (this.tableExist("peers_pool")==false)
            this.createTable("peers_pool");
        
        if (this.tableExist("trans")==false)
            this.createTable("trans");
       
        if (this.tableExist("trans_pool")==false)
            this.createTable("trans_pool");
        
        if (this.tableExist("web_ops")==false)
            this.createTable("web_ops");
        
        if (this.tableExist("web_sys_data")==false)
            this.createTable("web_sys_data");
        
        if (this.tableExist("web_users")==false)
            this.createTable("web_users");
        
        if (this.tableExist("profiles")==false)
            this.createTable("profiles");
        
        if (this.tableExist("assets")==false)
            this.createTable("assets");
         
         if (this.tableExist("assets_owners")==false)
            this.createTable("assets_owners");
         
         if (this.tableExist("assets_mkts")==false)
            this.createTable("assets_mkts");
         
         if (this.tableExist("assets_mkts_pos")==false)
            this.createTable("assets_mkts_pos");
         
         if (this.tableExist("assets_mkts_trades")==false)
            this.createTable("assets_mkts_trades");
         
         if (this.tableExist("sync")==false)
            this.createTable("sync");
         
        if (this.tableExist("out_emails")==false)
            this.createTable("out_emails");
          
          if (this.tableExist("delegates")==false)
            this.createTable("delegates");
          
           if (this.tableExist("rewards")==false)
            this.createTable("rewards");

           if (this.tableExist("web_actions")==false)
            this.createTable("web_actions");
           
            if (this.tableExist("ref_stats")==false)
              this.createTable("ref_stats");
            
            if (this.tableExist("feeds_sources")==false)
              this.createTable("feeds_sources");
            
            if (this.tableExist("feeds_data")==false)
              this.createTable("feeds_data");
            
            if (this.tableExist("feeds_pos_data")==false)
              this.createTable("feeds_pos_data");
            
            if (this.tableExist("feeds_sources_res")==false)
              this.createTable("feeds_sources_res");
            
             if (this.tableExist("feeds")==false)
              this.createTable("feeds");
             
              if (this.tableExist("feeds_branches")==false)
              this.createTable("feeds_branches");
              
              if (this.tableExist("feeds_bets")==false)
            this.createTable("feeds_bets");
         
         if (this.tableExist("feeds_bets_pos")==false)
            this.createTable("feeds_bets_pos");
         
         if (this.tableExist("feeds_spec_mkts_pos")==false)
            this.createTable("feeds_spec_mkts_pos");
         
         
        if (this.tableExist("status_log")==false)
            this.createTable("status_log");
        
        if (this.tableExist("feeds_spec_mkts")==false)
            this.createTable("feeds_spec_mkts");
        
        if (this.tableExist("tweets")==false)
            this.createTable("tweets");
        
        if (this.tableExist("comments")==false)
            this.createTable("comments");
        
        if (this.tableExist("tweets_follow")==false)
            this.createTable("tweets_follow");
        
        if (this.tableExist("votes")==false)
            this.createTable("votes");
        
        if (this.tableExist("votes_stats")==false)
            this.createTable("votes_stats");
        
        if (this.tableExist("votes_power")==false)
            this.createTable("votes_power");
        
        if (this.tableExist("delegates_log")==false)
            this.createTable("delegates_log");
        
        if (this.tableExist("adr_attr")==false)
            this.createTable("adr_attr");
                   
          if (this.tableExist("del_votes")==false)
          {
            this.createTable("del_votes");
            this.createInitAdr();
          }
    }
   
    public void createInitAdr() throws Exception
    {
        // Default
        UTILS.DB.executeUpdate("INSERT INTO adr SET adr='default', balance='20000000', created='0', block='0'");
        
        
        // Main addresses
        UTILS.DB.executeUpdate("INSERT INTO adr SET adr='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEVLlfDKbAbXy4pVUwirSVpLt3R2FkZirCzM5nkG50hb7HKt1dhZGpd6YtoYsf0bLLMjvKT2MEXjs=', balance='5000', created='0', block='0'");
        UTILS.DB.executeUpdate("INSERT INTO adr SET adr='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEUTU9+ZAA+SHf88XGBPRXIJegV4GyP0VRHeEyTTC4NEBcvufT3sXJLQkbkZpAwvKFiYqcR2BMRWw=', balance='5000', created='0', block='0'");
        UTILS.DB.executeUpdate("INSERT INTO adr SET adr='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEaGY5tE3iGD2TVrtYWuU75TIGAH18u7oSCV4XqMUh4O97FfSc4Ce76lm6O7llN9GPtFVp4YgtDC0=', balance='5000', created='0', block='0'");
        UTILS.DB.executeUpdate("INSERT INTO adr SET adr='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAExnpgueKLCKlYaDxVaY6E9fOWqO3FObD1yP/VlKyqcGyaliFvkOWoAGmsqI/xgyE+CkZ1B3mFCoE=', balance='5000', created='0', block='0'");
        UTILS.DB.executeUpdate("INSERT INTO adr SET adr='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEW9QtP5O+jDkEtqMH4EzvT/gpAaxsdOxmzoGzC/lQeDxrut3SiyJnq2ty3t8kdkBJsPOXw67QquI=', balance='5000', created='0', block='0'");
        UTILS.DB.executeUpdate("INSERT INTO adr SET adr='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAE32vMj5rmIlxmuBUxV9zJoRcrVjFUppF3mbs4AUHp66y4UVs8s4IIJdn4pVJcnYrxzFnMIomtdno=', balance='5000', created='0', block='0'");
        UTILS.DB.executeUpdate("INSERT INTO adr SET adr='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAE9wIHV2QyIMCZ8rDsvWxOeibLWb21WawBp/xx+vtQ8XL61xb9ieATWheRzowVfYEE/tnuIRlrcgA=', balance='5000', created='0', block='0'");
        UTILS.DB.executeUpdate("INSERT INTO adr SET adr='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEnO6qdAakxvlDQIl6dE2W+Q5QzC28m/tUBST3RSCTFySEdTQtaVtdH0jlGlFPntyHx3tv4aQHbN4=', balance='5000', created='0', block='0'");
        UTILS.DB.executeUpdate("INSERT INTO adr SET adr='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEaNpz2lU2bRvsn8maSbZHH/zsKzknofJ7t6mhopsdkyGY8bOiUxuwMA8rSc6OJc6xxHVS5s+ZYSw=', balance='5000', created='0', block='0'");
        UTILS.DB.executeUpdate("INSERT INTO adr SET adr='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEwGuPjLPELwp1mJYnd+jskQGUEYN3QWoPXe/uUEsrBHNcKOFZAN/kShEV3H6oQbUr5R9d0IKTZ7c=', balance='5000', created='0', block='0'");
        
        // Delegate 1
        UTILS.DB.executeUpdate("INSERT INTO del_votes "
                                     + "SET adr='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEVLlfDKbAbXy4pVUwirSVpLt3R2FkZirCzM5nkG50hb7HKt1dhZGpd6YtoYsf0bLLMjvKT2MEXjs=', "
                                         + "delegate='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEw/i+NP1xdpTMe2zhworPH3undpx0ReIHqgWsKAbeSlq+H8qQz6NwOr67pXZPB236PcdCiJUfuOM=', "
                                         + "type='ID_UP', "
                                         + "block='0'");
        
        UTILS.DB.executeUpdate("INSERT INTO del_votes "
                                     + "SET adr='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEUTU9+ZAA+SHf88XGBPRXIJegV4GyP0VRHeEyTTC4NEBcvufT3sXJLQkbkZpAwvKFiYqcR2BMRWw=', "
                                         + "delegate='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEw/i+NP1xdpTMe2zhworPH3undpx0ReIHqgWsKAbeSlq+H8qQz6NwOr67pXZPB236PcdCiJUfuOM=', "
                                         + "type='ID_UP', "
                                         + "block='0'");
        
        // Delegate 2
        UTILS.DB.executeUpdate("INSERT INTO del_votes "
                                     + "SET adr='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEaGY5tE3iGD2TVrtYWuU75TIGAH18u7oSCV4XqMUh4O97FfSc4Ce76lm6O7llN9GPtFVp4YgtDC0=', "
                                         + "delegate='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEmkqBbYFVGb5gck1P1ZBDb0HhcgRRgZPvmhNFxqm0FQXuAtZgxh7pJcPBvBLn5QDTGQkbmSzAe1c=', "
                                         + "type='ID_UP', "
                                         + "block='0'");
        
        UTILS.DB.executeUpdate("INSERT INTO del_votes "
                                     + "SET adr='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAExnpgueKLCKlYaDxVaY6E9fOWqO3FObD1yP/VlKyqcGyaliFvkOWoAGmsqI/xgyE+CkZ1B3mFCoE=', "
                                         + "delegate='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEmkqBbYFVGb5gck1P1ZBDb0HhcgRRgZPvmhNFxqm0FQXuAtZgxh7pJcPBvBLn5QDTGQkbmSzAe1c=', "
                                         + "type='ID_UP', "
                                         + "block='0'");
        
        // Delegate 3
        UTILS.DB.executeUpdate("INSERT INTO del_votes "
                                     + "SET adr='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEW9QtP5O+jDkEtqMH4EzvT/gpAaxsdOxmzoGzC/lQeDxrut3SiyJnq2ty3t8kdkBJsPOXw67QquI=', "
                                         + "delegate='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAE8hJcN8OPeFBsGPZbFL7d6Y3UpSl/7UOal/H+PCEgBcZouH6qhkgKHmFKP9OvBWUIMwH1NHHnYAU=', "
                                         + "type='ID_UP', "
                                         + "block='0'");
        
        UTILS.DB.executeUpdate("INSERT INTO del_votes "
                                     + "SET adr='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAE32vMj5rmIlxmuBUxV9zJoRcrVjFUppF3mbs4AUHp66y4UVs8s4IIJdn4pVJcnYrxzFnMIomtdno=', "
                                         + "delegate='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAE8hJcN8OPeFBsGPZbFL7d6Y3UpSl/7UOal/H+PCEgBcZouH6qhkgKHmFKP9OvBWUIMwH1NHHnYAU=', "
                                         + "type='ID_UP', "
                                         + "block='0'");
        
        // Delegate 4
        UTILS.DB.executeUpdate("INSERT INTO del_votes "
                                     + "SET adr='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAE9wIHV2QyIMCZ8rDsvWxOeibLWb21WawBp/xx+vtQ8XL61xb9ieATWheRzowVfYEE/tnuIRlrcgA=', "
                                         + "delegate='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEAlecggPYFhh9XuL4U78Z7qbGrRQB/wOIzOfvP+1Xong3AuYiLHXCrru/qTrmmECGxEKG4AuVxms=', "
                                         + "type='ID_UP', "
                                         + "block='0'");
        
        UTILS.DB.executeUpdate("INSERT INTO del_votes "
                                     + "SET adr='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEnO6qdAakxvlDQIl6dE2W+Q5QzC28m/tUBST3RSCTFySEdTQtaVtdH0jlGlFPntyHx3tv4aQHbN4=', "
                                         + "delegate='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEAlecggPYFhh9XuL4U78Z7qbGrRQB/wOIzOfvP+1Xong3AuYiLHXCrru/qTrmmECGxEKG4AuVxms=', "
                                         + "type='ID_UP', "
                                         + "block='0'");
        
        // Delegate 
        UTILS.DB.executeUpdate("INSERT INTO del_votes "
                                     + "SET adr='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEaNpz2lU2bRvsn8maSbZHH/zsKzknofJ7t6mhopsdkyGY8bOiUxuwMA8rSc6OJc6xxHVS5s+ZYSw=', "
                                         + "delegate='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEJVc8AvNJRlXEpxmbmppX7RK5BkaMAcZKKPsl83naU7wMtN/N5ghv22QO7gdh0AIeNKGSTvtrymA=', "
                                         + "type='ID_UP', "
                                         + "block='0'");
        
        UTILS.DB.executeUpdate("INSERT INTO del_votes "
                                     + "SET adr='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEwGuPjLPELwp1mJYnd+jskQGUEYN3QWoPXe/uUEsrBHNcKOFZAN/kShEV3H6oQbUr5R9d0IKTZ7c=', "
                                         + "delegate='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEJVc8AvNJRlXEpxmbmppX7RK5BkaMAcZKKPsl83naU7wMtN/N5ghv22QO7gdh0AIeNKGSTvtrymA=', "
                                         + "type='ID_UP', "
                                         + "block='0'");
    }
}
