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
             adr.create(true); 
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
			 	 	 	          + "block BIGINT DEFAULT 0, "
			 	 	 	          + "tstamp BIGINT DEFAULT 0)");
				    
	    UTILS.DB.executeUpdate("CREATE INDEX blocks_pool_hash ON blocks_pool(block)");
	
         }
         
         // ------------------------------- Blocks --------------------------------------
	 if (tab.equals("blocks"))
         {
	    UTILS.DB.executeUpdate("CREATE TABLE blocks(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
			 	 		     + "hash VARCHAR(100), "
			 	 	 	     + "block BIGINT DEFAULT 0, "
			 	 	 	     + "prev_hash VARCHAR(250) DEFAULT '', "
			 	 	 	     + "signer VARCHAR(250) DEFAULT '', "
                                                     + "packets BIGINT DEFAULT 0, "
                                                     + "tstamp BIGINT DEFAULT 0, "
                                                     + "nonce BIGINT DEFAULT 0, "
                                                     + "size BIGINT DEFAULT 0, "
                                                     + "net_dif VARCHAR(100) DEFAULT '0', "
                                                     + "commited BIGINT DEFAULT 0, "
                                                     + "confirmations BIGINT DEFAULT 0, "
                                                     + "payload_hash VARCHAR(250) DEFAULT '', "
                                                     + "tab_1 VARCHAR(100) DEFAULT '', "
                                                     + "tab_2 VARCHAR(100) DEFAULT '', "
                                                     + "tab_3 VARCHAR(100) DEFAULT '', "
                                                     + "tab_4 VARCHAR(100) DEFAULT '', "
                                                     + "tab_5 VARCHAR(100) DEFAULT '', "
                                                     + "tab_6 VARCHAR(100) DEFAULT '', "
                                                     + "tab_7 VARCHAR(100) DEFAULT '', "
                                                     + "tab_8 VARCHAR(100) DEFAULT '', "
                                                     + "tab_9 VARCHAR(100) DEFAULT '', "
                                                     + "tab_10 VARCHAR(100) DEFAULT '', "
                                                     + "tab_11 VARCHAR(100) DEFAULT '', "
                                                     + "tab_12 VARCHAR(100) DEFAULT '', "
                                                     + "tab_13 VARCHAR(100) DEFAULT '', "
                                                     + "tab_14 VARCHAR(100) DEFAULT '', "
                                                     + "tab_15 VARCHAR(100) DEFAULT '', "
                                                     + "signer_balance BIGINT DEFAULT 0)");
				    
	    UTILS.DB.executeUpdate("CREATE INDEX blocks_hash ON blocks(block)");
	    UTILS.DB.executeUpdate("CREATE INDEX blocks_block ON blocks(signer)");
	    UTILS.DB.executeUpdate("CREATE INDEX blocks_signer ON blocks(hash)");
            
            UTILS.DB.executeUpdate("INSERT INTO blocks(hash, "
                                                    + "block, "
                                                    + "prev_hash, "
                                                    + "signer, "
                                                    + "packets, "
                                                    + "tstamp, "
                                                    + "nonce, "
                                                    + "size, "
                                                    + "commited, "
                                                    + "confirmations, "
                                                    + "net_dif, "
                                                    + "payload_hash, "
                                                    + "signer_balance) VALUES("
                                                    + "'0000000000000000000000000000000000000000000000000000000000000000', "
                                                    + "'0', "
                                                    + "'0000000000000000000000000000000000000000000000000000000000000000', "
                                                    + "'default', "
                                                    + "'0', '"
                                                    +UTILS.BASIC.tstamp()+"', "
                                                    + "'0', "
                                                    + "'0', "
                                                    + "'"+UTILS.BASIC.tstamp()+"',"
                                                    + "'0', "
                                                    + "'000fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff', "
                                                    + "'0000000000000000000000000000000000000000000000000000000000000000', "
                                                    + "'0')");
         }
         
         // ---------------------------------- Connection Log--------------------------------------
	 if (tab.equals("con_log"))
         {
	    UTILS.DB.executeUpdate("CREATE TABLE con_log(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
			 	 		       + "IP VARCHAR(30) DEFAULT '', "
                                                       + "port BIGINT DEFAULT 0, "
			 	 	 	       + "tstamp BIGINT DEFAULT 0)");
	    
            UTILS.DB.executeUpdate("CREATE INDEX con_log_ip ON con_log(ip)");
	 }
         
         // ------------------------------- Domains --------------------------------------
	if (tab.equals("domains"))
	{
	   CDomainsTable domains=new CDomainsTable();
           domains.create();
	}
        
        // ------------------------------- Agents feeds --------------------------------------
	if (tab.equals("agents_feeds"))
	{
	   CAgentsFeedsTable agents_feeds=new CAgentsFeedsTable();
           agents_feeds.create();
	}
        
        // ------------------------------- Storage --------------------------------------
	if (tab.equals("storage"))
	{
	   CAgentsFeedsTable storage=new CAgentsFeedsTable();
           storage.create();
	}
         
         // ---------------------------------- Error Log--------------------------------------
	 if (tab.equals("err_log"))
         {
	    UTILS.DB.executeUpdate("CREATE TABLE err_log(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
			 	 		       + "type VARCHAR(250) DEFAULT '', "
			 	 	 	       + "mes LONGTEXT, "
			 	 	 	       + "file VARCHAR(100) DEFAULT '', "
                                                       + "line BIGINT DEFAULT 0, "
			 	 	 	       + "tstamp BIGINT DEFAULT 0)");
				    
	    UTILS.DB.executeUpdate("CREATE INDEX err_log_type ON err_log(type)");
            UTILS.DB.executeUpdate("CREATE INDEX err_log_file ON err_log(file)");
	}
         
         // ---------------------------------- Escrowed--------------------------------------
	 if (tab.equals("escrowed"))
         {
	    CEscrowedTable escrowed=new CEscrowedTable();
            escrowed.create();
	 }
         
         // ---------------------------------- Exchangers--------------------------------------
	 if (tab.equals("exchangers"))
         {
	    UTILS.DB.executeUpdate("CREATE TABLE exchangers(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
			 	 		         + "adr VARCHAR(250) DEFAULT '', "
			 	 	 	         + "title VARCHAR(250) DEFAULT '', "
			 	 	 	         + "description VARCHAR(2500) DEFAULT '', "
                                                         + "webpage VARCHAR(1000) DEFAULT '', "
                                                         + "type VARCHAR(20) DEFAULT '', "
                                                         + "asset VARCHAR(10) DEFAULT '', "
                                                         + "cur VARCHAR(10) DEFAULT '', "
                                                         + "pay_method VARCHAR(100) DEFAULT '', "
                                                         + "pay_details VARCHAR(100) DEFAULT '', "
                                                         + "price_type VARCHAR(100) DEFAULT '', "
                                                         + "price DOUBLE DEFAULT 0, "
                                                         + "price_feed VARCHAR(10) DEFAULT '', "
                                                         + "price_branch VARCHAR(10) DEFAULT '', "
                                                         + "price_margin DOUBLE(10,2) DEFAULT 0, "
                                                         + "country VARCHAR(10) DEFAULT '', "
                                                         + "town_type VARCHAR(20) DEFAULT '', "
                                                         + "town VARCHAR(250) DEFAULT '', "
                                                         + "escrowers VARCHAR(2500) DEFAULT '', "
                                                         + "block BIGINT DEFAULT 0, "
                                                         + "expire BIGINT DEFAULT 0, "
                                                         + "rowhash VARCHAR(100) DEFAULT '')");
				    
	    UTILS.DB.executeUpdate("CREATE INDEX exchangers_adr ON exchangers(adr)");
            UTILS.DB.executeUpdate("CREATE INDEX exchangers_asset ON exchangers(asset)");
            UTILS.DB.executeUpdate("CREATE INDEX exchangers_feed ON exchangers(price_feed)");
            UTILS.DB.executeUpdate("CREATE INDEX exchangers_branch ON exchangers(price_branch)");
            UTILS.DB.executeUpdate("CREATE INDEX exchangers_block ON exchangers(block)");
            UTILS.DB.executeUpdate("CREATE INDEX exchangers_rowhash ON exchangers(rowhash)");
	}
         
         // ---------------------------------- packets--------------------------------------
	 if (tab.equals("packets"))
         {
	    UTILS.DB.executeUpdate("CREATE TABLE packets(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
			 	 		       + "packet_hash VARCHAR(250) DEFAULT '', "
			 	 	 	       + "par_1_name VARCHAR(100) DEFAULT '', "
			 	 	 	       + "par_1_val TEXT, "
                                                       + "par_2_name VARCHAR(100) DEFAULT '', "
			 	 	 	       + "par_2_val TEXT, "
                                                       + "par_3_name VARCHAR(100) DEFAULT '', "
			 	 	 	       + "par_3_val TEXT, "
                                                       + "par_4_name VARCHAR(100) DEFAULT '', "
			 	 	 	       + "par_4_val TEXT, "
                                                       + "par_5_name VARCHAR(100) DEFAULT '', "
			 	 	 	       + "par_5_val TEXT, "
                                                       + "par_6_name VARCHAR(100) DEFAULT '', "
			 	 	 	       + "par_6_val TEXT, "
                                                       + "par_7_name VARCHAR(100) DEFAULT '', "
			 	 	 	       + "par_7_val TEXT, "
                                                       + "par_8_name VARCHAR(100) DEFAULT '', "
			 	 	 	       + "par_8_val TEXT, "
                                                       + "par_9_name VARCHAR(100) DEFAULT '', "
			 	 	 	       + "par_9_val TEXT, "
                                                       + "par_10_name VARCHAR(100) DEFAULT '', "
			 	 	 	       + "par_10_val TEXT, "
                                                       + "par_11_name VARCHAR(100) DEFAULT '', "
			 	 	 	       + "par_11_val TEXT, "
                                                       + "par_12_name VARCHAR(100) DEFAULT '', "
			 	 	 	       + "par_12_val TEXT, "
                                                       + "par_13_name VARCHAR(100) DEFAULT '', "
			 	 	 	       + "par_13_val TEXT, "
                                                       + "par_14_name VARCHAR(100) DEFAULT '', "
			 	 	 	       + "par_14_val TEXT, "
                                                       + "par_15_name VARCHAR(100) DEFAULT '', "
			 	 	 	       + "par_15_val TEXT, "
                                                       + "par_16_name VARCHAR(100) DEFAULT '', "
			 	 	 	       + "par_16_val TEXT, "
                                                       + "par_17_name VARCHAR(100) DEFAULT '', "
			 	 	 	       + "par_17_val TEXT, "
                                                       + "par_18_name VARCHAR(100) DEFAULT '', "
			 	 	 	       + "par_18_val TEXT, "
                                                       + "par_19_name VARCHAR(100) DEFAULT '', "
			 	 	 	       + "par_19_val TEXT, "
                                                       + "par_20_name VARCHAR(100) DEFAULT '', "
			 	 	 	       + "par_20_val TEXT, "
                                                       + "block BIGINT DEFAULT 0, "
                                                       + "tstamp BIGINT DEFAULT 0, "
                                                       + "confirms BIGINT DEFAULT 0, "
                                                       + "block_hash VARCHAR(100) DEFAULT '', "
                                                       + "payload_hash VARCHAR(100) DEFAULT '', "
                                                       + "payload_size BIGINT DEFAULT 0, "
                                                       + "packet_type VARCHAR(100) DEFAULT '', "
                                                       + "fee_src VARCHAR(250) DEFAULT '', "
                                                       + "fee_amount DOUBLE(20, 8) DEFAULT 0, "
                                                       + "fee_hash VARCHAR(250) DEFAULT '')");
				    
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
                                                  + "from_adr VARCHAR(250) DEFAULT '', "
                                                  + "to_adr VARCHAR(250) DEFAULT '', "
                                                  + "subject VARCHAR(250) DEFAULT '', "
                                                  + "mes VARCHAR(5000) DEFAULT '', "
                                                  + "status VARCHAR(20) DEFAULT '', "
                                                  + "tstamp BIGINT DEFAULT 0, "
                                                  + "tgt VARCHAR(250))");
				    
	    UTILS.DB.executeUpdate("CREATE INDEX mes_from_adr ON mes(from_adr)");
         }
         
          
         // ---------------------------------- My addresses --------------------------------------
	 if (tab.equals("my_adr"))
         {
	    UTILS.DB.executeUpdate("CREATE TABLE my_adr(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
				    		     + "userID BIGINT DEFAULT 0, "
				    		     + "adr VARCHAR(250) DEFAULT '',"
				    		     + "last_interest BIGINT DEFAULT 0,"
				    		     + "description VARCHAR(100) DEFAULT 'No description provided',"
                                                     + "tstamp BIGINT DEFAULT 0 DEFAULT 0,"
                                                     + "mine BIGINT DEFAULT 1,"
				    		     + "last_mine BIGINT DEFAULT 0)");
				    
	    UTILS.DB.executeUpdate("CREATE INDEX my_adr_adr ON my_adr(adr)");
	    UTILS.DB.executeUpdate("CREATE INDEX my_adr_userID ON my_adr(userID)");
         }
         
         // ---------------------------------- Net stat --------------------------------------
	 if (tab.equals("net_stat"))
         {
             UTILS.DB.executeUpdate("CREATE TABLE net_stat(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
				    		       + "last_block BIGINT DEFAULT 0, "
				    		       + "last_block_hash VARCHAR(250) DEFAULT '',"
                                                       + "block_confirm_min_balance DOUBLE(20,4) DEFAULT 1,"
                                                       + "net_dif VARCHAR(100) DEFAULT '0',"
                                                       + "sql_log_status VARCHAR(100) DEFAULT '',"
                                                       + "adr VARCHAR(100) NOT NULL DEFAULT '',"
                                                       + "ads VARCHAR(100) NOT NULL  DEFAULT '',"
                                                       + "agents VARCHAR(100) NOT NULL  DEFAULT '',"
                                                       + "assets VARCHAR(100) NOT NULL  DEFAULT '',"
                                                       + "assets_owners VARCHAR(100) NOT NULL  DEFAULT '',"
                                                       + "assets_mkts VARCHAR(100) NOT NULL  DEFAULT '',"
                                                       + "assets_mkts_pos VARCHAR(100) NOT NULL  DEFAULT '',"
                                                       + "domains VARCHAR(100) NOT NULL  DEFAULT '',"
                                                       + "escrowed VARCHAR(100) NOT NULL  DEFAULT '',"
                                                       + "profiles VARCHAR(100) NOT NULL  DEFAULT '',"
                                                       + "tweets VARCHAR(100) NOT NULL  DEFAULT '',"
                                                       + "comments VARCHAR(100) NOT NULL  DEFAULT '',"
                                                       + "tweets_follow VARCHAR(100) NOT NULL  DEFAULT '',"
                                                       + "votes VARCHAR(100) NOT NULL  DEFAULT '',"
                                                       + "feeds VARCHAR(100) NOT NULL  DEFAULT '',"
                                                       + "feeds_branches VARCHAR(100) NOT NULL  DEFAULT '',"
                                                       + "feeds_bets VARCHAR(100) NOT NULL  DEFAULT '',"
                                                       + "feeds_bets_pos VARCHAR(100) NOT NULL  DEFAULT '',"
                                                       + "feeds_spec_mkts VARCHAR(100) NOT NULL  DEFAULT '',"
                                                       + "feeds_spec_mkts_pos VARCHAR(100) NOT NULL  DEFAULT '',"
                                                       + "last_tstamp BIGINT NOT NULL  DEFAULT 0)");
            
            UTILS.DB.executeUpdate("INSERT INTO net_stat(last_block, "
                                                      + "last_block_hash, "
                                                      + "net_dif) "
                                            + "VALUES('0', "
                                                    + "'0000000000000000000000000000000000000000000000000000000000000000', "
                                                    + "'000fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff')");
	}
        
        // ----------------------------------- My Trans --------------------------------------
	if (tab.equals("my_trans"))
	{
	    UTILS.DB.executeUpdate("CREATE TABLE my_trans(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
	                                               + "userID BIGINT DEFAULT 0, "
                                                       + "adr VARCHAR(250) DEFAULT '', "
                                                       + "adr_assoc VARCHAR(250) DEFAULT '', "
	 	 	                               + "amount DOUBLE(20, 8) DEFAULT 0, "
                                                       + "invested DOUBLE(20, 8) DEFAULT 0, "
	 	 	 			       + "cur VARCHAR(10) DEFAULT '', "
	 	 	 			       + "expl VARCHAR(250) DEFAULT '', "
	 	 	 			       + "escrower VARCHAR(250) DEFAULT '', "
	 	 	 			       + "hash VARCHAR(100) DEFAULT '', "
                                                       + "tID BIGINT DEFAULT 0, "
                                                       + "block BIGINT DEFAULT 0, "
                                                       + "block_hash VARCHAR(100) DEFAULT '', "
                                                       + "tstamp BIGINT DEFAULT 0, "
	 	 	 			       + "cartID BIGINT DEFAULT 0, "
                                                       + "mes VARCHAR(2000) DEFAULT '')");
	 	 	 	   
	    UTILS.DB.executeUpdate("CREATE INDEX mt_userID ON my_trans(userID)");
	    UTILS.DB.executeUpdate("CREATE INDEX mt_adr ON my_trans(adr)");
            UTILS.DB.executeUpdate("CREATE INDEX mt_hash ON my_trans(hash)");
            UTILS.DB.executeUpdate("CREATE INDEX mt_block ON my_trans(block)");
	}
         
         // --------------------------------IPN Log -------------------------------------
	 if (tab.equals("ipn_log"))
	 {
	    	 UTILS.DB.executeUpdate("CREATE TABLE ipn_log (ID BIGINT AUTO_INCREMENT PRIMARY KEY,"
                                          +"ipnID BIGINT DEFAULT 0,"
                                          +"adr VARCHAR(250) DEFAULT '',"
                                          +"tip VARCHAR(10) DEFAULT 'ID_WEB',"
                                          +"data VARCHAR(1000) DEFAULT '',"
                                          +"amount DOUBLE DEFAULT 0,"
                                          +"cur VARCHAR(10) DEFAULT 'MSK',"
                                          +"txid VARCHAR(100) DEFAULT '',"
                                          +"status VARCHAR(10) DEFAULT 'ID_OK',"
                                          +"tstamp BIGINT DEFAULT 0)");
	    			 
	    	 UTILS.DB.executeUpdate("CREATE INDEX ipn_id ON ipn_log(ipnID)");
	    	 UTILS.DB.executeUpdate("CREATE INDEX ipn_iadr ON ipn_log(adr)");
	}
         
         // ------------------------------------- IPN ------------------------------------
	 if (tab.equals("ipn"))
	 {
	    UTILS.DB.executeUpdate("CREATE TABLE ipn(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
	 	 				  + "adr VARCHAR(250) DEFAULT '', "
	 	 				  + "email VARCHAR(100), "
	 	 				  + "web_pass VARCHAR(100), "
	 	 				  + "web_min DOUBLE(10,4) DEFAULT 0, "
	 	 				  + "email_min DOUBLE(10,4) DEFAULT 0, "
	 	 				  + "web_link VARCHAR(1000), "
	 	 				  + "web_only_confirmed VARCHAR(10), "
	 	 				  + "web_mes VARCHAR(10), "
	 	 				  + "email_only_confirmed VARCHAR(10), "
	 	 				  + "email_mes VARCHAR(10), "
	 	 				  + "web_status VARCHAR(25), "
	 	 				  + "email_status VARCHAR(25), "
	 	 				  + "tstamp BIGINT DEFAULT 0)");
	 	 	   
	    UTILS.DB.executeUpdate("CREATE INDEX ipn_adr ON ipn(adr)");
	}
         
         // ------------------------------------- Assets Markets ------------------------------------
	 if (tab.equals("assets_mkts"))
	 {
	    CAssetsMktsTable assets_mkts=new CAssetsMktsTable();
            assets_mkts.create(); 
	 }
         
         // ------------------------------------- Assets Markets Positions ------------------------------------
	 if (tab.equals("assets_mkts_pos"))
	 {
	    CAssetsMktsPosTable assets_mkts_pos=new CAssetsMktsPosTable();
            assets_mkts_pos.create();
	}
         
         
          // ----------------------------------- Assets Markets Trades --------------------------------------
         if (tab.equals("assets_mkts_trades"))
	 {
	    UTILS.DB.executeUpdate("CREATE TABLE assets_mkts_trades(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
	 					                 + "mktID BIGINT DEFAULT 0, "
                                                                 + "orderID BIGINT DEFAULT 0, "
	 					                 + "buyer VARCHAR(250) DEFAULT '', "
	 					                 + "seller VARCHAR(250) DEFAULT '', "
                                                                 + "qty DOUBLE(20, 8) DEFAULT 0, "
	 					                 + "price DOUBLE(20, 8) DEFAULT 0, "
                                                                 + "block BIGINT DEFAULT 0)");
	    
            UTILS.DB.executeUpdate("CREATE INDEX assets_mkts_trades_mktID ON assets_mkts_trades(mktID)");
            UTILS.DB.executeUpdate("CREATE INDEX assets_mkts_trades_orderID ON assets_mkts_trades(orderID)");
            UTILS.DB.executeUpdate("CREATE INDEX assets_mkts_trades_block ON assets_mkts_trades(block)");
	}
        
         
         // ----------------------------------- Escrowers --------------------------------------
         if (tab.equals("escrowers"))
	 {
	    UTILS.DB.executeUpdate("CREATE TABLE escrowers(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
	 					        + "adr VARCHAR(250) DEFAULT '', "
                                                        + "title VARCHAR(250) DEFAULT '', "
	 					        + "description VARCHAR(2500) DEFAULT '', "
                                                        + "web_page VARCHAR(2500) DEFAULT '', "
	 					        + "fee DOUBLE(10,2) DEFAULT 0, "
                                                        + "expire BIGINT DEFAULT 0, "
	 					        + "block BIGINT DEFAULT 0, "
                                                        + "rowhash VARCHAR(250) DEFAULT '')");
	    
            UTILS.DB.executeUpdate("CREATE INDEX escrowers_adr ON escrowers(adr)");
            UTILS.DB.executeUpdate("CREATE INDEX escrowers_block ON escrowers(block)");
	}
         
         // ----------------------------------- SQL Log --------------------------------------
         if (tab.equals("sql_log"))
	 {
	    UTILS.DB.executeUpdate("CREATE TABLE sql_log(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
	 					       + "query VARCHAR(2500) DEFAULT '', "
	 					       + "block BIGINT DEFAULT 0, "
	 					       + "hash VARCHAR(100))");
	    
            UTILS.DB.executeUpdate("CREATE INDEX sql_log_block ON sql_log(block)");
            UTILS.DB.executeUpdate("CREATE INDEX sql_log_hash ON sql_log(hash)");
	}
         
          // ----------------------------------- Packets --------------------------------------
         if (tab.equals("rec_packets"))
	 {
	    UTILS.DB.executeUpdate("CREATE TABLE rec_packets(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
	 					       + "tip VARCHAR(100) DEFAULT '', "
	 					       + "fromIP  VARCHAR(100) DEFAULT '', "
	 					       + "tstamp  BIGINT DEFAULT 0, "
                                                       + "hash VARCHAR(100) DEFAULT '')");
	    
            UTILS.DB.executeUpdate("CREATE INDEX rec_packets_hash ON rec_packets(hash)");
	}
         
         // ---------------------------------- Peers ---------------------------------------
	 if (tab.equals("peers"))
	 {
	    UTILS.DB.executeUpdate("CREATE TABLE peers(ID BIGINT AUTO_INCREMENT PRIMARY KEY," 
                                                      +"peer VARCHAR(30),"
                                                      +"port BIGINT DEFAULT 0,"
                                                      +"in_traffic BIGINT DEFAULT 0,"
                                                      +"out_traffic BIGINT DEFAULT 0,"
                                                      +"last_seen BIGINT DEFAULT 0,"
                                                      +"tstamp BIGINT DEFAULT 0)");
	  }
         
         // ---------------------------------- Peers ---------------------------------------
	 if (tab.equals("req_data"))
	 {
	    UTILS.DB.executeUpdate("CREATE TABLE req_data(ID BIGINT AUTO_INCREMENT PRIMARY KEY," 
                                                        +"adr VARCHAR(250) DEFAULT '',"
                                                        +"field_1_name VARCHAR(250) DEFAULT '',"
                                                        +"field_1_min BIGINT DEFAULT 0,"
                                                        +"field_1_max BIGINT DEFAULT 0,"
                                                        +"field_2_name VARCHAR(250) DEFAULT '',"
                                                        +"field_2_min BIGINT DEFAULT 0,"
                                                        +"field_2_max BIGINT DEFAULT 0,"
                                                        +"field_3_name VARCHAR(250) DEFAULT '',"
                                                        +"field_3_min BIGINT DEFAULT 0,"
                                                        +"field_3_max BIGINT DEFAULT 0,"
                                                        +"field_4_name VARCHAR(250) DEFAULT '',"
                                                        +"field_4_min BIGINT DEFAULT 0,"
                                                        +"field_4_max BIGINT DEFAULT 0,"
                                                        +"field_5_name VARCHAR(250) DEFAULT '',"
                                                        +"field_5_min BIGINT DEFAULT 0,"
                                                        +"field_5_max BIGINT DEFAULT 0,"
                                                        +"block BIGINT DEFAULT 0,"
                                                        +"rowhash VARCHAR(250) DEFAULT '',"
                                                        +"mes VARCHAR(1000) DEFAULT '')");
	  }
         
         
         // ---------------------------------- Peers Pool -----------------------------------
	 if (tab.equals("peers_pool"))
	 {
	    UTILS.DB.executeUpdate("CREATE TABLE peers_pool(ID BIGINT AUTO_INCREMENT PRIMARY KEY," 
                                                           +"peer VARCHAR(30),"
                                                           +"port BIGINT DEFAULT 0,"
                                                           +"con_att_no BIGINT DEFAULT 0,"
                                                           +"con_att_last BIGINT DEFAULT 0,"
                                                           +"accept_con VARCHAR(10) DEFAULT 'ID_NO',"
                                                           +"last_seen BIGINT DEFAULT 0,"
                                                           +"banned BIGINT DEFAULT 0)");
            
            UTILS.DB.executeUpdate("CREATE INDEX peers_pool_peer ON peers_pool(peer)");
	  }
         
         // ---------------------------------- Web log --------------------------------------
	if (tab.equals("status"))
	{
            UTILS.DB.executeUpdate("CREATE TABLE status(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
			 	 	              + "engine_status VARCHAR(25), "
			 	 	 	      + "last_tables_block BIGINT DEFAULT 0, "
			 	 	 	      + "last_blocks_block BIGINT DEFAULT 0, "
			 	 	 	      + "version VARCHAR(10),"
			 	 	 	      + "alive BIGINT DEFAULT 0,"
			 	 	 	      + "netstat VARCHAR(30),"
			 	 	 	      + "bkp_email VARCHAR(10),"
			 	 	 	      + "bkp_last_send BIGINT DEFAULT 0,"
			 	 	 	      + "bkp_pending VARCHAR(5),"
			 	 	 	      + "last_block_hash VARCHAR(100),"
			 	 	 	      + "last_packet_hash VARCHAR(100),"
			 	 	 	      + "update_required VARCHAR(5))");
            
            UTILS.DB.executeUpdate("INSERT INTO status(engine_status, "
                                              + "last_tables_block, "
                                              + "last_blocks_block, "
                                              + "version, "
                                              + "bkp_email, "
                                              + "bkp_last_send, "
                                              + "bkp_pending, "
                                              + "update_required)"
                                       + "VALUES('ID_ONLINE', "
                                       + "'0', "
                                       + "'0', "
                                       + "'0.0.0', "
                                       + "'', "
                                       + "'0', "
                                       + "'YES', "
                                       + "'NO')");
        }
         
         // ----------------------------------- Trans --------------------------------------  
	if (tab.equals("trans"))
	{
	    UTILS.DB.executeUpdate("CREATE TABLE trans(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
	                                            + "src VARCHAR(250) DEFAULT '', "
	 	 	 			    + "amount DOUBLE(20,8) DEFAULT 0, "
                                                    + "invested DOUBLE(20,8) DEFAULT 0, "
	 	 	 			    + "cur VARCHAR(10) DEFAULT '', "
	 	 	 			    + "escrower VARCHAR(250) DEFAULT '', "
	 	 	 			    + "hash VARCHAR(100) DEFAULT '', "
                                                    + "tID BIGINT DEFAULT 0, "
	 	 	 			    + "block BIGINT DEFAULT 0, "
                                                    + "block_hash VARCHAR(100) DEFAULT '', "
                                                    + "status VARCHAR(20) DEFAULT '', "
	 	 	 			    + "tstamp BIGINT DEFAULT 0)");
	 	 	 	   
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
			 	 			+ "bid DOUBLE(10, 4) DEFAULT 0, "
			 	 			+ "days BIGINT DEFAULT 0, "
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
			 	 			+ "tstamp BIGINT DEFAULT 0, "
			 	 			+ "tID VARCHAR(50) DEFAULT '', "
			 	 	 	 	+ "refs BIGINT DEFAULT 0)");
				    
	    UTILS.DB.executeUpdate("CREATE INDEX wop_user ON web_ops(user)");
	    UTILS.DB.executeUpdate("CREATE INDEX wop_op ON web_ops(op)");
	    UTILS.DB.executeUpdate("CREATE INDEX wop_status ON web_ops(status)");			    
        }
        
        
        
        // ----------------------- Trans pool --------------------------------------
	if (tab.equals("trans_pool"))
        {
	    UTILS.DB.executeUpdate("CREATE TABLE trans_pool(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
			 	 		   + "src VARCHAR(250) DEFAULT '', "
			 	 	 	   + "amount DOUBLE(10,4) DEFAULT 0, "
			 	 	 	   + "src_balance DOUBLE(10,4) DEFAULT 0, "
			 	 	 	   + "block BIGINT DEFAULT 0, "
			 	 	 	   + "cur VARCHAR(10), "
			 	 	 	   + "hash VARCHAR(100))");
				    
	    UTILS.DB.executeUpdate("CREATE INDEX trans_pool_src ON trans_pool(src)");
        }
        
          // ------------------------------------- Web Sys Data ------------------------------------------------
         if (tab.equals("web_sys_data"))
         {
             UTILS.DB.executeUpdate("CREATE TABLE web_sys_data(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                             +"status VARCHAR(50) DEFAULT '', "
                                                             +"last_ping BIGINT DEFAULT '0', "
                                                             +"free_memory BIGINT DEFAULT '0', "
                                                             +"total_memory BIGINT DEFAULT '0', "
                                                             +"max_memory BIGINT DEFAULT '0', "
                                                             +"procs BIGINT DEFAULT '0', "
                                                             +"threads_no BIGINT DEFAULT '0', "
                                                             +"uptime BIGINT DEFAULT '0', "
                                                             +"mining BIGINT DEFAULT '0', "
                                                             +"hashing_power BIGINT DEFAULT '0', "
                                                             +"mining_threads BIGINT DEFAULT '0', "
                                                             +"version VARCHAR(20) DEFAULT '0.0.1', "
                                                             +"msk_price DOUBLE(9,4) DEFAULT 0)");
             
             UTILS.DB.executeUpdate("INSERT INTO web_sys_data(status, msk_price) VALUES('ID_OFFLINE', '1')");
         }
         
          // ------------------------------------- Sync ------------------------------------------------
         if (tab.equals("sync"))
         {
             UTILS.DB.executeUpdate("CREATE TABLE sync(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                       +"status VARCHAR(50) DEFAULT '', "
                                                       +"peer VARCHAR(20) DEFAULT '', "
                                                       +"type VARCHAR(50) DEFAULT '', "
                                                       +"tab VARCHAR(100) DEFAULT '', "
                                                       +"start BIGINT DEFAULT '0', "
                                                       +"end BIGINT DEFAULT '0', "
                                                       +"tstamp BIGINT DEFAULT '0')");
             
            
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
         
         // ------------------------------------- Tweets trends ------------------------------------------------
         if (tab.equals("tweets_trends"))
         {
             UTILS.DB.executeUpdate("CREATE TABLE tweets_trends(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                                +"term BIGINT DEFAULT '0', "
                                                                +"type VARCHAR(250) DEFAULT '', "
                                                                +"tweets BIGINT DEFAULT '0', "
                                                                +"retweets BIGINT DEFAULT '0', "
                                                                +"likes BIGINT DEFAULT '0', "
                                                                +"comments BIGINT DEFAULT '0')");
             
              UTILS.DB.executeUpdate("CREATE INDEX tweets_trends_term ON tweets_trends(term)");
              UTILS.DB.executeUpdate("CREATE INDEX tweets_trends_type ON tweets_trends(type)");
         }
         
         // ------------------------------------- Votes stats ------------------------------------------------
         if (tab.equals("votes_stats"))
         {
             UTILS.DB.executeUpdate("CREATE TABLE votes_stats(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                                +"target_type VARCHAR(50) DEFAULT '', "
                                                                +"targetID BIGINT DEFAULT 0, "
                                                                +"upvotes_24 BIGINT DEFAULT 0, "
                                                                +"upvotes_power_24 FLOAT(9,2) DEFAULT 0, "
                                                                +"downvotes_24 BIGINT DEFAULT 0, "
                                                                +"downvotes_power_24 FLOAT(9,2) DEFAULT 0, "
                                                                +"upvotes_total BIGINT DEFAULT 0, "
                                                                +"upvotes_power_total FLOAT(9,2) DEFAULT 0, "
                                                                +"downvotes_total BIGINT DEFAULT 0, "
                                                                +"downvotes_power_total FLOAT(9,2) DEFAULT 0, "
                                                                +"tstamp BIGINT DEFAULT 0, "
                                                                +"pay FLOAT(9,4) DEFAULT '0', "
                                                                +"comments BIGINT DEFAULT '0')");
             
              UTILS.DB.executeUpdate("CREATE INDEX votes_stats_target_type ON votes_stats(target_type)");
              UTILS.DB.executeUpdate("CREATE INDEX votes_stats_targetID ON votes_stats(targetID)");
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
                                                             +"user VARCHAR(50) DEFAULT '', "
                                                             +"pass VARCHAR(100) DEFAULT '', "
                                                             +"email VARCHAR(100) DEFAULT '', "
                                                             +"IP VARCHAR(20) DEFAULT '', "
                                                             +"status VARCHAR(50) DEFAULT '', "
                                                             +"api_key VARCHAR(100) NOT NULL DEFAULT '', "
                                                             +"tstamp BIGINT DEFAULT 0, "
                                                             + "unread_esc BIGINT DEFAULT 0, "
                                                             + "unread_mes BIGINT DEFAULT 0, "
                                                             + "unread_trans BIGINT DEFAULT 0)");
             
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
         
         // ------------------------------------- Profiles ------------------------------------------------
         if (tab.equals("assets"))
         {
             CAssetsTable assets=new CAssetsTable();
             assets.create(); 
         }
         
         // ------------------------------------- Profiles ------------------------------------------------
         if (tab.equals("assets_owners"))
         {
             CAssetsOwnersTable assets_owners=new CAssetsOwnersTable();
             assets_owners.create();
         }
         
         // ------------------------------------- Feeds ------------------------------------------------
         if (tab.equals("feeds"))
         {
              UTILS.DB.executeUpdate("CREATE TABLE feeds(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                       +"adr VARCHAR(250) DEFAULT '', "
                                                       +"name VARCHAR(100) DEFAULT '', "
                                                       +"description VARCHAR(1000) DEFAULT '', "
                                                       +"datasource VARCHAR(250) DEFAULT '', "
                                                       +"website VARCHAR(250) DEFAULT '', "
                                                       +"symbol VARCHAR(10) DEFAULT '', "
                                                       +"expire BIGINT DEFAULT 0, "
                                                       +"branches BIGINT DEFAULT 0, "
                                                       +"rowhash VARCHAR(100) DEFAULT '', "
                                                       +"block BIGINT DEFAULT 0)");
             
             UTILS.DB.executeUpdate("CREATE INDEX feeds_adr ON feeds(adr)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_symbol ON feeds(symbol)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_rowhash ON feeds(rowhash)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_block ON feeds(block)");
         }
         
         // ------------------------------------- Feeds Branches ------------------------------------------------
         if (tab.equals("feeds_branches"))
         {
              UTILS.DB.executeUpdate("CREATE TABLE feeds_branches(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                                 +"feed_symbol VARCHAR(100) DEFAULT '', "
                                                                 +"symbol VARCHAR(10) DEFAULT '', "
                                                                 +"name VARCHAR(250) DEFAULT '', "
                                                                 +"description VARCHAR(500) DEFAULT '', "
                                                                 +"type VARCHAR(50) DEFAULT '', "
                                                                 +"rl_symbol VARCHAR(20) DEFAULT '', "
                                                                 +"fee DOUBLE(9, 4) DEFAULT 0.0001, "
                                                                 +"expire BIGINT DEFAULT 0, "
                                                                 +"val DOUBLE(20,8) DEFAULT 0, "
                                                                 +"mkt_status VARCHAR(50) DEFAULT '', "
                                                                 +"rowhash VARCHAR(100) DEFAULT '', "
                                                                 +"block BIGINT DEFAULT 0)");
             
             UTILS.DB.executeUpdate("CREATE INDEX feeds_branches_feed_symbol ON feeds_branches(feed_symbol)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_branches_symbol ON feeds_branches(symbol)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_branches_type ON feeds_branches(type)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_branches_rowhash ON feeds_branches(rowhash)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_branches_block ON feeds_branches(block)");
         }
         
         // ------------------------------------- Feeds Sources ------------------------------------------------
         if (tab.equals("feeds_sources"))
         {
              UTILS.DB.executeUpdate("CREATE TABLE feeds_sources(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                                 +"feed_symbol VARCHAR(100) DEFAULT '', "
                                                                 + "url VARCHAR(500) DEFAULT '', "
                                                                 + "adr VARCHAR(250) DEFAULT '', "
                                                                 + "ping_interval BIGINT DEFAULT 30, "
                                                                 + "next_run BIGINT DEFAULT 0)");
             
             UTILS.DB.executeUpdate("CREATE INDEX feeds_sources ON feeds_sources(feed_symbol)");
         }
         
         // ------------------------------------- Feeds Data ------------------------------------------------
         if (tab.equals("feeds_data"))
         {
             UTILS.DB.executeUpdate("CREATE TABLE feeds_data(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                            +"feed VARCHAR(10) DEFAULT '', "
                                                            +"feed_branch VARCHAR(20) DEFAULT '', "
                                                            +"val DOUBLE(20, 8) DEFAULT 0, "
                                                            +"mkt_status VARCHAR(20) DEFAULT 'ID_OPEN', "
                                                            +"tstamp BIGINT DEFAULT 0, "
                                                            +"block BIGINT DEFAULT 0)");
             
             UTILS.DB.executeUpdate("CREATE INDEX feeds_data_feed ON feeds_data(feed)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_data_feed_branch ON feeds_data(feed_branch)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_data_block ON feeds_data(block)");
         }
         
         // ------------------------------------- Feeds Pos Data ------------------------------------------------
         if (tab.equals("feeds_pos_data"))
         {
             UTILS.DB.executeUpdate("CREATE TABLE feeds_pos_data(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                                +"pos_type VARCHAR(30) DEFAULT '', "
                                                                +"posID BIGINT DEFAULT 0, "
                                                                +"pos_symbol VARCHAR(10) DEFAULT '', "
                                                                +"val DOUBLE(20,8) DEFAULT 0, "
                                                                +"block BIGINT DEFAULT 0)");
             
             UTILS.DB.executeUpdate("CREATE INDEX feeds_pos_posID ON feeds_pos_data(posID)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_pos_pos_symbol ON feeds_pos_data(pos_symbol)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_pos_data_block ON feeds_pos_data(block)");
         }
         
         // ------------------------------------- Feeds Sources Res ------------------------------------------------
         if (tab.equals("feeds_sources_res"))
         {
             UTILS.DB.executeUpdate("CREATE TABLE feeds_sources_res(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                                  +"feed VARCHAR(10) DEFAULT '', "
                                                                  +"result VARCHAR(10) DEFAULT '', "
                                                                  +"err_mes VARCHAR(1000) DEFAULT '', "
                                                                  +"tstamp BIGINT DEFAULT 0)");
             
             UTILS.DB.executeUpdate("CREATE INDEX feeds_sources_res_feed ON feeds_sources_res(feed)");
            
         }
         
         // ------------------------------------- Feeds Bets ------------------------------------------------
         if (tab.equals("feeds_bets"))
         {
             UTILS.DB.executeUpdate("CREATE TABLE feeds_bets(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                            +"mktID BIGINT DEFAULT 0, "
                                                            +"adr VARCHAR(250) DEFAULT '', "
                                                            +"feed_1 VARCHAR(10) DEFAULT '', "
                                                            +"branch_1 VARCHAR(10) DEFAULT '', "
                                                            +"feed_2 VARCHAR(10) DEFAULT '', "
                                                            +"branch_2 VARCHAR(10) DEFAULT '', "
                                                            +"feed_3 VARCHAR(10) DEFAULT '', "
                                                            +"branch_3 VARCHAR(10) DEFAULT '', "
                                                             +"last_price DOUBLE(20,8) DEFAULT 0, "
                                                            +"tip VARCHAR(30) DEFAULT 'ID_TOUCH', "
                                                            +"val_1 DOUBLE(20, 8) DEFAULT 0, "
                                                            +"val_2 DOUBLE(20, 8) DEFAULT 0, "
                                                            +"title VARCHAR(150) DEFAULT '', "
                                                            +"description VARCHAR(250) DEFAULT '', "
                                                            +"budget DOUBLE(9,2) DEFAULT 0, "
                                                            +"win_multiplier INT DEFAULT 0, "
                                                            +"start_block BIGINT DEFAULT 0, "
                                                            +"end_block BIGINT DEFAULT 0, "
                                                            +"accept_block BIGINT DEFAULT 0, "
                                                            +"cur VARCHAR(10) DEFAULT '', "
                                                            +"bets BIGINT DEFAULT 0, "
                                                            +"invested DOUBLE(20,8) DEFAULT 0, "
                                                            +"status VARCHAR(10) DEFAULT 'ID_PENDING', "
                                                            +"rowhash VARCHAR(100) DEFAULT '', "
                                                            +"block BIGINT DEFAULT 0)");
             
             UTILS.DB.executeUpdate("CREATE INDEX feeds_bets_uid ON feeds_bets(mktID)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_bets_block ON feeds_bets(block)");
         }
         
          // ------------------------------------- Feeds Bets ------------------------------------------------
         if (tab.equals("feeds_spec_mkts"))
         {
             UTILS.DB.executeUpdate("CREATE TABLE feeds_spec_mkts(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                                +"adr VARCHAR(250) DEFAULT '', "
                                                                +"feed_1 VARCHAR(10) DEFAULT '', "
                                                                +"branch_1 VARCHAR(10) DEFAULT '', "
                                                                +"feed_2 VARCHAR(10) DEFAULT '', "
                                                                +"branch_2 VARCHAR(10) DEFAULT '', "
                                                                +"feed_3 VARCHAR(10) DEFAULT '', "
                                                                +"branch_3 VARCHAR(10) DEFAULT '', "
                                                                +"last_price DOUBLE(20,8) DEFAULT 0, "
                                                                +"cur VARCHAR(10) DEFAULT '', "
                                                                +"max_leverage BIGINT DEFAULT 0, "
                                                                +"spread DOUBLE(20,8) DEFAULT 0, "
                                                                +"real_symbol VARCHAR(10) DEFAULT '', "
                                                                +"pos_type VARCHAR(50) DEFAULT '', "
                                                                +"title VARCHAR(250) DEFAULT '', "
                                                                +"description VARCHAR(2500) DEFAULT '', "
                                                                +"max_margin DOUBLE(20,2) DEFAULT 0, "
                                                                +"expire BIGINT DEFAULT 0, "
                                                                +"block BIGINT DEFAULT 0, "
                                                                +"status VARCHAR(20) DEFAULT 'ID_CLOSED', "
                                                                +"rowhash VARCHAR(100) DEFAULT '', "
                                                                +"mktID BIGINT DEFAULT 0)");
             
             UTILS.DB.executeUpdate("CREATE INDEX feeds_spec_mkts_mktID ON feeds_spec_mkts(mktID)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_spec_mkts_block ON feeds_spec_mkts(block)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_spec_mkts_rowhash ON feeds_spec_mkts(rowhash)");
         }
         
         // ------------------------------------- Feeds Spec Mkts Pos ------------------------------------------------
         if (tab.equals("feeds_spec_mkts_pos"))
         {
             UTILS.DB.executeUpdate("CREATE TABLE feeds_spec_mkts_pos(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                                    +"mktID BIGINT DEFAULT 0, "
                                                                    +"posID BIGINT DEFAULT 0, "
                                                                    +"adr VARCHAR(250) DEFAULT '', "
                                                                    +"open DOUBLE(20, 8) DEFAULT 0, "
                                                                    +"sl DOUBLE(20, 8) DEFAULT 0, "
                                                                    +"tp DOUBLE(20, 8) DEFAULT 0, "
                                                                    +"leverage BIGINT DEFAULT 0, "
                                                                    +"qty DOUBLE(20, 4) DEFAULT 0, "
                                                                    +"status VARCHAR(20) DEFAULT '', "
                                                                    +"open_line VARCHAR(20) DEFAULT '', "
                                                                    +"tip VARCHAR(10) DEFAULT '', "
                                                                    +"pl DOUBLE(20,8) DEFAULT 0, "
                                                                    +"spread DOUBLE(20,8) DEFAULT 0, "
                                                                    +"margin DOUBLE(20,8) DEFAULT 0, "
                                                                    +"close_reason VARCHAR(20) DEFAULT '', "
                                                                    +"closed_pl DOUBLE(20,8) DEFAULT 0, "
                                                                    +"closed_margin DOUBLE(20,8) DEFAULT 0, "
                                                                    +"block BIGINT DEFAULT 0, "
                                                                    +"last_block BIGINT DEFAULT 0, "
                                                                    +"rowhash VARCHAR(100) DEFAULT '')");
             
             UTILS.DB.executeUpdate("CREATE INDEX feeds_spec_mkts_pos_mktID ON feeds_bets_pos(bet_uid)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_spec_mkts_pos_posID ON feeds_bets_pos(adr)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_spec_mkts_pos_block ON feeds_bets_pos(block)");
         
         }
         
         // Feeds assets markets
         if (tab.equals("feeds_assets_mkts"))
         {
              UTILS.DB.executeUpdate("CREATE TABLE feeds_assets_mkts(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                                   +"mktID BIGINT DEFAULT 0, "
                                                                   +"feed_1 VARCHAR(10) DEFAULT '', "
                                                                   +"branch_1 VARCHAR(10) DEFAULT '', "
                                                                   +"feed_2 VARCHAR(10) DEFAULT '', "
                                                                   +"branch_2 VARCHAR(10) DEFAULT '', "
                                                                   +"feed_3 VARCHAR(10) DEFAULT '', "
                                                                   +"branch_3 VARCHAR(10) DEFAULT '', "
                                                                   +"last_price DOUBLe(20,8) DEFAULT 0, "
                                                                   +"decimals INT DEFAULT 0, "
                                                                   +"rl_symbol VARCHAR(10) DEFAULT '', "
                                                                   +"spread DOUBLE(20,8) DEFAULT 0, "
                                                                   +"cur VARCHAR(20) DEFAULT '', "
                                                                   +"adr VARCHAR(250) DEFAULT '', "
                                                                   +"status VARCHAR(25) DEFAULT '', "
                                                                   +"block BIGINT DEFAULT 0, "
                                                                   +"expire BIGINT DEFAULT 0, "
                                                                   +"rowhash VARCHAR(100) DEFAULT '', "
                                                                   +"asset_symbol VARCHAR(20) DEFAULT '')");
             
             UTILS.DB.executeUpdate("CREATE INDEX feeds_assets_mkts_mktID ON feeds_assets_mkts(mktID)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_assets_mkts_block ON feeds_assets_mkts(block)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_assets_mkts_rowhash ON feeds_assets_mkts(rowhash)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_assets_mkts_asset_symbol ON feeds_assets_mkts(asset_symbol)");
         }
         
         // Feeds assets transactions
         if (tab.equals("feeds_assets_mkts_trans"))
         {
              UTILS.DB.executeUpdate("CREATE TABLE feeds_assets_mkts_trans(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                                          +"mktID BIGINT DEFAULT 0, "
                                                                          +"tip VARCHAR(20) DEFAULT '', "
                                                                          +"adr VARCHAR(250) DEFAULT '', "
                                                                          +"qty DOUBLE(20, 8) DEFAULT 0, "
                                                                          +"block BIGINT DEFAULT 0, "
                                                                          +"price DOUBLE(20,8) DEFAULT 0)");
             
             UTILS.DB.executeUpdate("CREATE INDEX feeds_assets_mkts_trans_mktID ON feeds_assets_mkts_trans(mktID)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_assets_mkts_trans_block ON feeds_assets_mkts_trans(block)");
         }
         
         
         // ------------------------------------- Feeds Bets ------------------------------------------------
         if (tab.equals("feeds_bets_pos"))
         {
             UTILS.DB.executeUpdate("CREATE TABLE feeds_bets_pos(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                               +"bet_uid BIGINT DEFAULT 0, "
                                                               +"adr VARCHAR(250) DEFAULT '', "
                                                               +"amount DOUBLE(9,4) DEFAULT 0, "
                                                               +"block BIGINT DEFAULT 0, "
                                                               +"rowhash VARCHAR(100) DEFAULT '')");
             
             UTILS.DB.executeUpdate("CREATE INDEX feeds_bets_pos_uid ON feeds_bets_pos(bet_uid)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_bets_pos_adr ON feeds_bets_pos(adr)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_bets_pos_block ON feeds_bets_pos(block)");
         }
         
         // ------------------------------------- Stores  ------------------------------------------------
         if (tab.equals("shop_stores"))
         {
              UTILS.DB.executeUpdate("CREATE TABLE shop_stores(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                              +"adr VARCHAR(250) DEFAULT '', "
                                                              +"title VARCHAR(250) DEFAULT '', "
                                                              +"description VARCHAR(1000) DEFAULT '', "
                                                              +"website VARCHAR(1000) DEFAULT '', "
                                                              +"pic VARCHAR(1000) DEFAULT '', "
                                                              +"esc_policy VARCHAR(25) DEFAULT '', "
                                                              +"expire BIGINT DEFAULT 0, "
                                                              +"UID BIGINT DEFAULT 0, "
                                                              +"block BIGINT DEFAULT 0, "
                                                              +"rowhash VARCHAR(100) DEFAULT '')");
             
             UTILS.DB.executeUpdate("CREATE INDEX shops_stores_adr ON shop_stores(adr)");
             UTILS.DB.executeUpdate("CREATE INDEX shops_stores_title ON shop_stores(title)");
             UTILS.DB.executeUpdate("CREATE INDEX shops_block ON shop_stores(block)");
             UTILS.DB.executeUpdate("CREATE INDEX shops_rowhash ON shop_stores(rowhash)");
         }
         
         // Stores categs
         if (tab.equals("shop_stores_categs"))
         {
              UTILS.DB.executeUpdate("CREATE TABLE shop_stores_categs(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                                      +"storeID BIGINT DEFAULT 0, "
                                                                      +"parent VARCHAR(250) DEFAULT '', "
                                                                      +"name VARCHAR(250) DEFAULT '', "
                                                                      +"expire BIGINT DEFAULT 0, "
                                                                      +"block BIGINT DEFAULT 0, "
                                                                      +"rowhash VARCHAR(100) DEFAULT '')");
             
             UTILS.DB.executeUpdate("CREATE INDEX shop_stores_categs_storeID ON shop_stores_categs(storeID)");
             UTILS.DB.executeUpdate("CREATE INDEX shop_stores_categs_parent ON shop_stores_categs(parent)");
             UTILS.DB.executeUpdate("CREATE INDEX shop_stores_categs_name ON shop_stores_categs(name)");
         }
         
         // Stores escrowers
         if (tab.equals("shop_stores_esc"))
         {
              UTILS.DB.executeUpdate("CREATE TABLE shop_stores_esc(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                                      +"storeUID BIGINT DEFAULT 0, "
                                                                      +"parent VARCHAR(50) DEFAULT '', "
                                                                      +"name VARCHAR(50) DEFAULT '', "
                                                                      +"block BIGINT DEFAULT 0, "
                                                                      +"rowhash VARCHAR(100) DEFAULT '', "
                                                                      +"expire BIGINT DEFAULT 0)");
             
             UTILS.DB.executeUpdate("CREATE INDEX shop_stores_esc_storeUID ON shop_stores_esc(storeUID)");
             UTILS.DB.executeUpdate("CREATE INDEX shop_stores_esc_parent ON shop_stores_esc(parent)");
             UTILS.DB.executeUpdate("CREATE INDEX shop_stores_esc_name ON shop_stores_esc(name)");
         }
         
         // Stores payment
         if (tab.equals("shop_stores_payment"))
         {
              UTILS.DB.executeUpdate("CREATE TABLE shop_stores_payment(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                                      +"storeUID BIGINT DEFAULT 0, "
                                                                      +"asset VARCHAR(50) DEFAULT '', "
                                                                      +"price_type VARCHAR(50) DEFAULT '', "
                                                                      +"price_feed VARCHAR(10) DEFAULT '', "
                                                                      +"price_branch VARCHAR(10) DEFAULT '', "
                                                                      +"block BIGINT DEFAULT 0, "
                                                                      +"rowhash VARCHAR(100) DEFAULT '', "
                                                                      +"expire BIGINT DEFAULT 0)");
             
             UTILS.DB.executeUpdate("CREATE INDEX shop_stores_payment_storeUID ON shop_stores_payment(storeUID)");
             UTILS.DB.executeUpdate("CREATE INDEX shop_stores_payment_asset ON shop_stores_payment(asset)");
             UTILS.DB.executeUpdate("CREATE INDEX shop_stores_payment_block ON shop_stores_payment(block)");
             UTILS.DB.executeUpdate("CREATE INDEX shop_stores_payment_rowhash ON shop_stores_payment(rowhash)");
             UTILS.DB.executeUpdate("CREATE INDEX shop_stores_payment_expire ON shop_stores_payment(expire)");
         }
         
        
         
         // ------------------------------------- Agents  ------------------------------------------------
         if (tab.equals("agents"))
         {
             CAgentsTable agents=new CAgentsTable();
             agents.create(); 
         }
         
         // ------------------------------------- Delegates votes  ------------------------------------------------
         if (tab.equals("del_votes"))
         {
             CDelVotesTable del_votes=new CDelVotesTable();
             del_votes.create(); 
         }
         
         // ------------------------------------- Agents Emails ------------------------------------------------
         if (tab.equals("out_emails"))
         {
              UTILS.DB.executeUpdate("CREATE TABLE out_emails(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                                +"format VARCHAR(50) DEFAULT '', "
                                                                +"sender VARCHAR(250) DEFAULT '', "
                                                                +"dest VARCHAR(250) DEFAULT '', "
                                                                +"subject VARCHAR(1000) DEFAULT '', "
                                                                +"message VARCHAR(2500) DEFAULT '', "
                                                                +"adr VARCHAR(500) DEFAULT '', "
                                                                +"tstamp BIGINT DEFAULT 0, "
                                                                +"status VARCHAR(25) DEFAULT 'ID_PENDING')");
             
             UTILS.DB.executeUpdate("CREATE INDEX agents_emails_agentID ON agents(adr)");
         }
         
         // ------------------------------------- Agents Globals ------------------------------------------------
         if (tab.equals("agents_globals"))
         {
              UTILS.DB.executeUpdate("CREATE TABLE agents_globals(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                                +"varID VARCHAR(50) DEFAULT '', "
                                                                +"appID BIGINT DEFAULT 0, "
                                                                +"name VARCHAR(100) DEFAULT '', "
                                                                +"data_type VARCHAR(100) DEFAULT 'ID_LONG', "
                                                                +"expl VARCHAR(1000) DEFAULT '', "
                                                                +"min FLOAT(20,8) DEFAULT '0', "
                                                                +"max FLOAT(20,8) DEFAULT '0', "
                                                                +"val VARCHAR(1000) DEFAULT 0)");
             
             UTILS.DB.executeUpdate("CREATE INDEX agents_globals_appID ON agents_globals(appID)");
         }
         
          // ------------------------------------- Checkpoints ------------------------------------------------
         if (tab.equals("checkpoints"))
         {
              UTILS.DB.executeUpdate("CREATE TABLE checkpoints(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                                +"block BIGINT DEFAULT '0', "
                                                                +"hash VARCHAR(100) DEFAULT '')");
             
             UTILS.DB.executeUpdate("CREATE INDEX checkpoints_block ON checkpoints(block)");
             UTILS.DB.executeUpdate("CREATE INDEX checkpoints_hash ON checkpoints(hash)");
         }
         
          // ------------------------------------- Delegates ------------------------------------------------
         if (tab.equals("delegates"))
         {
              UTILS.DB.executeUpdate("CREATE TABLE delegates(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                                +"delegate VARCHAR(500) DEFAULT '', "
                                                                +"power BIGINT DEFAULT 0)");
             
             UTILS.DB.executeUpdate("CREATE INDEX delegates_adr ON delegates(block)");
         }
         
         // ------------------------------------- Agents categs ------------------------------------------------
         if (tab.equals("agents_categs"))
         {
              UTILS.DB.executeUpdate("CREATE TABLE agents_categs(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                                +"categID VARCHAR(50) DEFAULT '', "
                                                                +"name VARCHAR(250) DEFAULT '', "
                                                                +"mkt_no BIGINT DEFAULT 0, "
                                                                +"dir_no BIGINT DEFAULT 0)");
             
             UTILS.DB.executeUpdate("INSERT INTO agents_categs(categID, name) VALUES('ID_ALL', 'All')");
             UTILS.DB.executeUpdate("INSERT INTO agents_categs(categID, name) VALUES('ID_BUSINESS', 'Business')");
             UTILS.DB.executeUpdate("INSERT INTO agents_categs(categID, name) VALUES('ID_EDUCATION', 'Education')");
             UTILS.DB.executeUpdate("INSERT INTO agents_categs(categID, name) VALUES('ID_ENTERTAINMENT', 'Entertainment')");
             UTILS.DB.executeUpdate("INSERT INTO agents_categs(categID, name) VALUES('ID_FINANCE', 'Finance')");
             UTILS.DB.executeUpdate("INSERT INTO agents_categs(categID, name) VALUES('ID_GAMES', 'Games')");
             UTILS.DB.executeUpdate("INSERT INTO agents_categs(categID, name) VALUES('ID_GAMBLING', 'Gambling')");
             UTILS.DB.executeUpdate("INSERT INTO agents_categs(categID, name) VALUES('ID_PRODUCTIVITY', 'Productivity')");
             UTILS.DB.executeUpdate("INSERT INTO agents_categs(categID, name) VALUES('ID_SHOPPING', 'Shopping')");
             UTILS.DB.executeUpdate("INSERT INTO agents_categs(categID, name) VALUES('ID_TRADING', 'Trading')");
             UTILS.DB.executeUpdate("INSERT INTO agents_categs(categID, name) VALUES('ID_UTILITIES', 'Utilities')");
         }
         
         // ------------------------------------- My Agents ------------------------------------------------
         if (tab.equals("agents_mine"))
         {
              UTILS.DB.executeUpdate("CREATE TABLE agents_mine(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                              +"userID BIGINT NOT NULL DEFAULT 0, "
                                                              +"adr VARCHAR(250) NOT NULL DEFAULT '', "
                                                              +"globals LONGTEXT, "
                                                              +"interface LONGTEXT, "
                                                              +"signals LONGTEXT, "
                                                              +"code LONGTEXT, "
                                                              +"storage LONGTEXT, "
                                                              +"used BIGINT NOT NULL DEFAULT 0, "
                                                              +"exec_log LONGTEXT NOT NULL, "
                                                              +"compiler VARCHAR(1000) NOT NULL DEFAULT'', "
                                                              +"compiler_globals VARCHAR(1000) NOT NULL DEFAULT'', "
                                                              +"compiler_interface VARCHAR(1000) NOT NULL DEFAULT'', "
                                                              +"compiler_signals VARCHAR(1000) NOT NULL DEFAULT'', "
                                                              +"ver VARCHAR(50) NOT NULL DEFAULT '', "
                                                              +"name VARCHAR(250) NOT NULL DEFAULT '', "
                                                              +"status VARCHAR(25) NOT NULL DEFAULT 'ID_ONLINE', "
                                                              +"description VARCHAR(250) NOT NULL DEFAULT '', "
                                                              +"block BIGINT NOT NULL DEFAULT 0, "
                                                              +"dir BIGINT NOT NULL DEFAULT 0, "
                                                              +"expire BIGINT NOT NULL DEFAULT 0, "
                                                              +"url_pass VARCHAR(250) NOT NULL DEFAULT '',"
                                                              +"trans_sender VARCHAR(250) NOT NULL DEFAULT '',"
                                                              +"trans_amount DOUBLE(20,8) NOT NULL DEFAULT 0,"
                                                              +"trans_cur VARCHAR(10) NOT NULL DEFAULT '',"
                                                              +"trans_mes VARCHAR(5000) NOT NULL DEFAULT '',"
                                                              +"trans_escrower VARCHAR(250) NOT NULL DEFAULT '',"
                                                              +"simulate_target VARCHAR(25) NOT NULL DEFAULT '',"
                                                              +"run LONGTEXT NOT NULL,"
                                                              +"mes_sender VARCHAR(250) NOT NULL DEFAULT '',"
                                                              +"mes_subj VARCHAR(1000) NOT NULL DEFAULT '',"
                                                              +"mes_mes VARCHAR(5000) NOT NULL DEFAULT '',"
                                                              +"block_hash VARCHAR(250) NOT NULL DEFAULT '',"
                                                              +"block_no BIGINT NOT NULL DEFAULT 0,"
                                                              +"block_nonce BIGINT NOT NULL DEFAULT 0)");
             
             UTILS.DB.executeUpdate("CREATE INDEX agents_mine_userID ON agents_mine(userID)");
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
        
          if (this.tableExist("req_data")==false)
            this.createTable("req_data");
        
        if (this.tableExist("peers")==false)
            this.createTable("peers");
        
        if (this.tableExist("peers_pool")==false)
            this.createTable("peers_pool");
        
        if (this.tableExist("status")==false)
            this.createTable("status");
        
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
        
        if (this.tableExist("tweets")==false)
            this.createTable("tweets");
        
        if (this.tableExist("comments")==false)
            this.createTable("comments");
        
        if (this.tableExist("tweets_follow")==false)
            this.createTable("tweets_follow");
        
        if (this.tableExist("votes")==false)
            this.createTable("votes");
        
         if (this.tableExist("tweets_trends")==false)
            this.createTable("tweets_trends");
         
         if (this.tableExist("assets")==false)
            this.createTable("assets");
         
         if (this.tableExist("assets_mkts")==false)
            this.createTable("assets_mkts");
         
         if (this.tableExist("assets_mkts_pos")==false)
            this.createTable("assets_mkts_pos");
         
         if (this.tableExist("assets_mkts_trades")==false)
            this.createTable("assets_mkts_trades");
         
         if (this.tableExist("assets_owners")==false)
            this.createTable("assets_owners");
         
         if (this.tableExist("sync")==false)
            this.createTable("sync");
         
         if (this.tableExist("agents")==false)
            this.createTable("agents");
         
         if (this.tableExist("agents_mine")==false)
            this.createTable("agents_mine");
         
         if (this.tableExist("out_emails")==false)
            this.createTable("out_emails");
         
         if (this.tableExist("agents_categs")==false)
            this.createTable("agents_categs");
         
         if (this.tableExist("agents_globals")==false)
            this.createTable("agents_globals");
         
         if (this.tableExist("shop_stores")==false)
            this.createTable("shop_stores");
         
         if (this.tableExist("shop_stores_categs")==false)
            this.createTable("shop_stores_categs");
         
         if (this.tableExist("checkpoints")==false)
            this.createTable("checkpoints");
         
         if (this.tableExist("feeds")==false)
            this.createTable("feeds");
         
          if (this.tableExist("feeds_sources_res")==false)
            this.createTable("feeds_sources_res");
         
          if (this.tableExist("feeds_branches")==false)
            this.createTable("feeds_branches");

         if (this.tableExist("feeds_data")==false)
            this.createTable("feeds_data");
         
         if (this.tableExist("feeds_bets")==false)
            this.createTable("feeds_bets");
         
         if (this.tableExist("feeds_bets_pos")==false)
            this.createTable("feeds_bets_pos");
         
         if (this.tableExist("feeds_pos_data")==false)
            this.createTable("feeds_pos_data");
         
         if (this.tableExist("feeds_sources")==false)
            this.createTable("feeds_sources");
         
          if (this.tableExist("agents_feeds")==false)
            this.createTable("agents_feeds");
          
          if (this.tableExist("storage")==false)
            this.createTable("storage");
          
          if (this.tableExist("votes_stats")==false)
            this.createTable("votes_stats");
          
          if (this.tableExist("delegates")==false)
            this.createTable("delegates");
          
          if (this.tableExist("del_votes")==false)
            this.createTable("del_votes");
    }
   
    
}
