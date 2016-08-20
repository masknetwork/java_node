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
             adr.init();
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
				    
	    UTILS.DB.executeUpdate("CREATE INDEX blocks_pool_hash ON blocks_pool(block)");
	
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
                                                     + "payload_hash VARCHAR(250) NOT NULL DEFAULT '', "
                                                     + "tab_1 VARCHAR(100) NOT NULL DEFAULT '', "
                                                     + "tab_2 VARCHAR(100) NOT NULL DEFAULT '', "
                                                     + "tab_3 VARCHAR(100) NOT NULL DEFAULT '', "
                                                     + "tab_4 VARCHAR(100) NOT NULL DEFAULT '', "
                                                     + "tab_5 VARCHAR(100) NOT NULL DEFAULT '', "
                                                     + "tab_6 VARCHAR(100) NOT NULL DEFAULT '', "
                                                     + "tab_7 VARCHAR(100) NOT NULL DEFAULT '', "
                                                     + "tab_8 VARCHAR(100) NOT NULL DEFAULT '', "
                                                     + "tab_9 VARCHAR(100) NOT NULL DEFAULT '', "
                                                     + "tab_10 VARCHAR(100) NOT NULL DEFAULT '', "
                                                     + "tab_11 VARCHAR(100) NOT NULL DEFAULT '', "
                                                     + "tab_12 VARCHAR(100) NOT NULL DEFAULT '', "
                                                     + "tab_13 VARCHAR(100) NOT NULL DEFAULT '', "
                                                     + "tab_14 VARCHAR(100) NOT NULL DEFAULT '', "
                                                     + "tab_15 VARCHAR(100) NOT NULL DEFAULT '', "
                                                     + "tab_16 VARCHAR(100) NOT NULL DEFAULT '', "
                                                     + "tab_17 VARCHAR(100) NOT NULL DEFAULT '', "
                                                     + "tab_18 VARCHAR(100) NOT NULL DEFAULT '', "
                                                     + "tab_19 VARCHAR(100) NOT NULL DEFAULT '', "
                                                     + "tab_20 VARCHAR(100) NOT NULL DEFAULT '', "
                                                     + "tab_21 VARCHAR(100) NOT NULL DEFAULT '', "
                                                     + "tab_22 VARCHAR(100) NOT NULL DEFAULT '', "
                                                     + "tab_23 VARCHAR(100) NOT NULL DEFAULT '', "
                                                     + "tab_24 VARCHAR(100) NOT NULL DEFAULT '', "
                                                     + "tab_25 VARCHAR(100) NOT NULL DEFAULT '', "
                                                     + "tab_26 VARCHAR(100) NOT NULL DEFAULT '', "
                                                     + "tab_27 VARCHAR(100) NOT NULL DEFAULT '', "
                                                     + "tab_28 VARCHAR(100) NOT NULL DEFAULT '', "
                                                     + "tab_29 VARCHAR(100) NOT NULL DEFAULT '', "
                                                     + "tab_30 VARCHAR(100) NOT NULL DEFAULT '', "
                                                     + "signer_balance BIGINT NOT NULL DEFAULT 0)");
				    
	    UTILS.DB.executeUpdate("CREATE INDEX blocks_hash ON blocks(block)");
	    UTILS.DB.executeUpdate("CREATE INDEX blocks_block ON blocks(signer)");
	    UTILS.DB.executeUpdate("CREATE INDEX blocks_signer ON blocks(hash)");
            
            UTILS.DB.executeUpdate("INSERT INTO blocks "
                                         + "SET hash='0000000000000000000000000000000000000000000000000000000000000000', "
                                             + "block='0', "
                                             + "prev_hash='0000000000000000000000000000000000000000000000000000000000000000', "
                                             + "signer='default', "
                                             + "packets='0', "
                                             + "tstamp='"+UTILS.BASIC.tstamp()+"', "
                                             + "nonce='0', "
                                             + "size='0', "
                                             + "commited='"+UTILS.BASIC.tstamp()+"', "
                                             + "confirmations='0', "
                                             + "net_dif='0000000fffffffffffffffffffffffffffffffffffffffffffffffffffffffff', "
                                             + "payload_hash='0000000000000000000000000000000000000000000000000000000000000000', "
                                             + "signer_balance='0'");
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
        
        
        
        // ------------------------------- Storage --------------------------------------
	if (tab.equals("storage"))
	{
	   CStorageTable storage=new CStorageTable();
           storage.create();
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
                                                       + "sql_log_status VARCHAR(100) NOT NULL DEFAULT '',"
                                                       + "adr VARCHAR(100) NOT NULL DEFAULT '',"
                                                       + "ads VARCHAR(100) NOT NULL DEFAULT '',"
                                                       + "agents VARCHAR(100) NOT NULL DEFAULT '',"
                                                       + "agents_feeds VARCHAR(100) NOT NULL DEFAULT '',"
                                                       + "assets VARCHAR(100) NOT NULL DEFAULT '',"
                                                       + "assets_owners VARCHAR(100) NOT NULL DEFAULT '',"
                                                       + "assets_mkts VARCHAR(100) NOT NULL DEFAULT '',"
                                                       + "assets_mkts_pos VARCHAR(100) NOT NULL DEFAULT '',"
                                                       + "domains VARCHAR(100) NOT NULL DEFAULT '',"
                                                       + "escrowed VARCHAR(100) NOT NULL DEFAULT '',"
                                                       + "profiles VARCHAR(100) NOT NULL DEFAULT '',"
                                                       + "tweets VARCHAR(100) NOT NULL DEFAULT '',"
                                                       + "storage VARCHAR(100) NOT NULL DEFAULT '',"
                                                       + "comments VARCHAR(100) NOT NULL DEFAULT '',"
                                                       + "tweets_follow VARCHAR(100) NOT NULL DEFAULT '',"
                                                       + "votes VARCHAR(100) NOT NULL DEFAULT '',"
                                                       + "del_votes VARCHAR(100) NOT NULL DEFAULT '',"
                                                       + "feeds VARCHAR(100) NOT NULL DEFAULT '',"
                                                       + "feeds_branches VARCHAR(100) NOT NULL DEFAULT '',"
                                                       + "feeds_bets VARCHAR(100) NOT NULL DEFAULT '',"
                                                       + "feeds_bets_pos VARCHAR(100) NOT NULL DEFAULT '',"
                                                       + "delegate VARCHAR(500) NOT NULL DEFAULT '',"
                                                       + "last_tstamp BIGINT NOT NULL DEFAULT 0)");
            
            UTILS.DB.executeUpdate("INSERT INTO net_stat "
                                         + "SET last_block='0', "
                                             + "last_block_hash='0000000000000000000000000000000000000000000000000000000000000000', "
                                             + "net_dif='0000000fffffffffffffffffffffffffffffffffffffffffffffffffffffffff'");
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
                                                      +"tstamp BIGINT NOT NULL DEFAULT 0)");
	  }
         
         // ---------------------------------- Peers ---------------------------------------
	 if (tab.equals("req_data"))
	 {
	    UTILS.DB.executeUpdate("CREATE TABLE req_data(ID BIGINT AUTO_INCREMENT PRIMARY KEY," 
                                                        +"adr VARCHAR(250) NOT NULL DEFAULT '',"
                                                        +"field_1_name VARCHAR(250) NOT NULL DEFAULT '',"
                                                        +"field_1_min BIGINT NOT NULL DEFAULT 0,"
                                                        +"field_1_max BIGINT NOT NULL DEFAULT 0,"
                                                        +"field_2_name VARCHAR(250) NOT NULL DEFAULT '',"
                                                        +"field_2_min BIGINT NOT NULL DEFAULT 0,"
                                                        +"field_2_max BIGINT NOT NULL DEFAULT 0,"
                                                        +"field_3_name VARCHAR(250) NOT NULL DEFAULT '',"
                                                        +"field_3_min BIGINT NOT NULL DEFAULT 0,"
                                                        +"field_3_max BIGINT NOT NULL DEFAULT 0,"
                                                        +"field_4_name VARCHAR(250) NOT NULL DEFAULT '',"
                                                        +"field_4_min BIGINT NOT NULL DEFAULT 0,"
                                                        +"field_4_max BIGINT NOT NULL DEFAULT 0,"
                                                        +"field_5_name VARCHAR(250) NOT NULL DEFAULT '',"
                                                        +"field_5_min BIGINT NOT NULL DEFAULT 0,"
                                                        +"field_5_max BIGINT NOT NULL DEFAULT 0,"
                                                        +"block BIGINT NOT NULL DEFAULT 0,"
                                                        +"rowhash VARCHAR(250) NOT NULL DEFAULT '',"
                                                        +"mes VARCHAR(1000) NOT NULL DEFAULT '')");
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
                                                             +"msk_price DOUBLE(9,4) NOT NULL DEFAULT 0)");
             
             UTILS.DB.executeUpdate("INSERT INTO web_sys_data(status, msk_price) VALUES('ID_OFFLINE', '1')");
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
                                                                +"term BIGINT NOT NULL DEFAULT '0', "
                                                                +"type VARCHAR(250) NOT NULL DEFAULT '', "
                                                                +"tweets BIGINT NOT NULL DEFAULT '0', "
                                                                +"retweets BIGINT NOT NULL DEFAULT '0', "
                                                                +"likes BIGINT NOT NULL DEFAULT '0', "
                                                                +"comments BIGINT NOT NULL DEFAULT '0')");
             
              UTILS.DB.executeUpdate("CREATE INDEX tweets_trends_term ON tweets_trends(term)");
              UTILS.DB.executeUpdate("CREATE INDEX tweets_trends_type ON tweets_trends(type)");
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
                                                                +"upvotes_total BIGINT NOT NULL DEFAULT 0, "
                                                                +"upvotes_power_total FLOAT(9,2) NOT NULL DEFAULT 0, "
                                                                +"downvotes_total BIGINT NOT NULL DEFAULT 0, "
                                                                +"downvotes_power_total FLOAT(9,2) NOT NULL DEFAULT 0, "
                                                                +"tstamp BIGINT NOT NULL DEFAULT 0, "
                                                                +"pay FLOAT(9,4) NOT NULL DEFAULT '0')");
             
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
                                                             +"user VARCHAR(50) NOT NULL DEFAULT '', "
                                                             +"pass VARCHAR(100) NOT NULL DEFAULT '', "
                                                             +"email VARCHAR(100) NOT NULL DEFAULT '', "
                                                             +"IP VARCHAR(20) NOT NULL DEFAULT '', "
                                                             +"status VARCHAR(50) NOT NULL DEFAULT '', "
                                                             +"api_key VARCHAR(100) NOT NULL DEFAULT '', "
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
         
         // ------------------------------------- Assets ------------------------------------------------
         if (tab.equals("assets"))
         {
             CAssetsTable feeds_bets=new CAssetsTable();
             feeds_bets.create(); 
         }
         
         
         // ------------------------------------- Assets Owners ------------------------------------------------
         if (tab.equals("assets_owners"))
         {
             CAssetsOwnersTable feeds_bets_pos=new CAssetsOwnersTable();
             feeds_bets_pos.create(); 
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
                                                                +"format VARCHAR(50) NOT NULL DEFAULT '', "
                                                                +"sender VARCHAR(250) NOT NULL DEFAULT '', "
                                                                +"dest VARCHAR(250) NOT NULL DEFAULT '', "
                                                                +"subject VARCHAR(1000) NOT NULL DEFAULT '', "
                                                                +"message VARCHAR(2500) NOT NULL DEFAULT '', "
                                                                +"adr VARCHAR(500) NOT NULL DEFAULT '', "
                                                                +"tstamp BIGINT NOT NULL DEFAULT 0, "
                                                                +"status VARCHAR(25) NOT NULL DEFAULT 'ID_PENDING')");
             
             UTILS.DB.executeUpdate("CREATE INDEX agents_emails_agentID ON agents(adr)");
         }
         
         // ------------------------------------- Agents Globals ------------------------------------------------
         if (tab.equals("agents_globals"))
         {
              UTILS.DB.executeUpdate("CREATE TABLE agents_globals(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                                +"varID VARCHAR(50) NOT NULL DEFAULT '', "
                                                                +"appID BIGINT NOT NULL DEFAULT 0, "
                                                                +"name VARCHAR(100) NOT NULL DEFAULT '', "
                                                                +"data_type VARCHAR(100) NOT NULL DEFAULT 'ID_LONG', "
                                                                +"expl VARCHAR(1000) NOT NULL DEFAULT '', "
                                                                +"min FLOAT(20,8) NOT NULL DEFAULT '0', "
                                                                +"max FLOAT(20,8) NOT NULL DEFAULT '0', "
                                                                +"val VARCHAR(1000) NOT NULL DEFAULT 0)");
             
             UTILS.DB.executeUpdate("CREATE INDEX agents_globals_appID ON agents_globals(appID)");
         }
         
          // ------------------------------------- Checkpoints ------------------------------------------------
         if (tab.equals("checkpoints"))
         {
              UTILS.DB.executeUpdate("CREATE TABLE checkpoints(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                                +"block BIGINT NOT NULL DEFAULT '0', "
                                                                +"hash VARCHAR(100) NOT NULL DEFAULT '')");
             
             UTILS.DB.executeUpdate("CREATE INDEX checkpoints_block ON checkpoints(block)");
             UTILS.DB.executeUpdate("CREATE INDEX checkpoints_hash ON checkpoints(hash)");
         }
         
          // ------------------------------------- Delegates ------------------------------------------------
         if (tab.equals("delegates"))
         {
              UTILS.DB.executeUpdate("CREATE TABLE delegates(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                            +"delegate VARCHAR(500) NOT NULL DEFAULT '', "
                                                            +"power BIGINT NOT NULL DEFAULT 0)");
             
         }
         
         // ------------------------------------- Agents categs ------------------------------------------------
         if (tab.equals("agents_categs"))
         {
              UTILS.DB.executeUpdate("CREATE TABLE agents_categs(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                                +"categID VARCHAR(50) NOT NULL DEFAULT '', "
                                                                +"name VARCHAR(250) NOT NULL DEFAULT '', "
                                                                +"mkt_no BIGINT NOT NULL DEFAULT 0, "
                                                                +"dir_no BIGINT NOT NULL DEFAULT 0)");
             
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
                                                              +"userID BIGINT NOT NULL NOT NULL DEFAULT 0, "
                                                              +"adr VARCHAR(250) NOT NULL DEFAULT '', "
                                                              +"globals LONGTEXT, "
                                                              +"interface LONGTEXT, "
                                                              +"signals LONGTEXT, "
                                                              +"code LONGTEXT, "
                                                              +"storage LONGTEXT, "
                                                              +"used BIGINT NOT NULL NOT NULL DEFAULT 0, "
                                                              +"exec_log LONGTEXT NOT NULL, "
                                                              +"compiler VARCHAR(1000) NOT NULL DEFAULT'', "
                                                              +"compiler_globals VARCHAR(1000) NOT NULL DEFAULT'', "
                                                              +"compiler_interface VARCHAR(1000) NOT NULL DEFAULT'', "
                                                              +"compiler_signals VARCHAR(1000) NOT NULL DEFAULT'', "
                                                              +"ver VARCHAR(50) NOT NULL DEFAULT '', "
                                                              +"name VARCHAR(250) NOT NULL DEFAULT '', "
                                                              +"status VARCHAR(25) NOT NULL DEFAULT 'ID_ONLINE', "
                                                              +"description VARCHAR(250) NOT NULL DEFAULT '', "
                                                              +"block BIGINT NOT NULL NOT NULL DEFAULT 0, "
                                                              +"dir BIGINT NOT NULL NOT NULL DEFAULT 0, "
                                                              +"expire BIGINT NOT NULL NOT NULL DEFAULT 0, "
                                                              +"url_pass VARCHAR(250) NOT NULL DEFAULT '',"
                                                              +"trans_sender VARCHAR(250) NOT NULL DEFAULT '',"
                                                              +"trans_amount DOUBLE(20,8) NOT NULL NOT NULL DEFAULT 0,"
                                                              +"trans_cur VARCHAR(10) NOT NULL DEFAULT '',"
                                                              +"trans_mes VARCHAR(5000) NOT NULL DEFAULT '',"
                                                              +"trans_escrower VARCHAR(250) NOT NULL DEFAULT '',"
                                                              +"simulate_target VARCHAR(25) NOT NULL DEFAULT '',"
                                                              +"run LONGTEXT NOT NULL,"
                                                              +"mes_sender VARCHAR(250) NOT NULL DEFAULT '',"
                                                              +"mes_subj VARCHAR(1000) NOT NULL DEFAULT '',"
                                                              +"mes_mes VARCHAR(5000) NOT NULL DEFAULT '',"
                                                              +"block_hash VARCHAR(250) NOT NULL DEFAULT '',"
                                                              +"block_no BIGINT NOT NULL NOT NULL DEFAULT 0,"
                                                              +"block_nonce BIGINT NOT NULL NOT NULL DEFAULT 0)");
             
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
         
         if (this.tableExist("checkpoints")==false)
            this.createTable("checkpoints");
         
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
          {
            this.createTable("del_votes");
            
UTILS.DB.executeUpdate("INSERT INTO adr SET adr='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEChL8MuOMtsKFsPO94FTitGgb2egGSt+ogO5C0x15N1Q8ZdDg/G3XWSaoNzXWw7A/gpB2UZJ/elU=', balance=10000, block=1, created=0, sealed=0");
UTILS.DB.executeUpdate("INSERT INTO del_votes SET delegate='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAE0x6rL6zrSrJoFnwsRjuxn86/JGmvRPkrZDhD+a536Df28UB2OZB5Nc/scf5YQdTx7kkDb/yf++o=', adr='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEChL8MuOMtsKFsPO94FTitGgb2egGSt+ogO5C0x15N1Q8ZdDg/G3XWSaoNzXWw7A/gpB2UZJ/elU=', type='ID_UP', block=1");
UTILS.DB.executeUpdate("INSERT INTO adr SET adr='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEbbg/tI+KBUyz/tl/0wn0e0Il3xmeMBFGmsSGDL8AzAfZeTANTCEJlpFV/4IUpcRuQ0Ucvyx9tmI=', balance=10000, block=1, created=0, sealed=0");
UTILS.DB.executeUpdate("INSERT INTO del_votes SET delegate='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAE0x6rL6zrSrJoFnwsRjuxn86/JGmvRPkrZDhD+a536Df28UB2OZB5Nc/scf5YQdTx7kkDb/yf++o=', adr='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEbbg/tI+KBUyz/tl/0wn0e0Il3xmeMBFGmsSGDL8AzAfZeTANTCEJlpFV/4IUpcRuQ0Ucvyx9tmI=', type='ID_UP', block=1");

UTILS.DB.executeUpdate("INSERT INTO adr SET adr='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEFYpiA6utjRCs5jK7cHEuloveupOjwXJKww5QTFI9YedJuz4aOand6JtwuYMDNI7lxi7ewKA3pm8=', balance=10000, block=1, created=0, sealed=0");
UTILS.DB.executeUpdate("INSERT INTO del_votes SET delegate='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEGEAmoJEa25iVpzThVmJUIaAcOqLe0RJ5DUUBwAb8a7j0ijZJ4SwrlFcg2NoAyDuUDpchsLLcbIY=', adr='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEFYpiA6utjRCs5jK7cHEuloveupOjwXJKww5QTFI9YedJuz4aOand6JtwuYMDNI7lxi7ewKA3pm8=', type='ID_UP', block=1");
UTILS.DB.executeUpdate("INSERT INTO adr SET adr='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEGCE9Tte00n6xUkDpthPniV/0X92qDSCiEJ0+/mKrtbAkKuyrp8F9UgiHLcGsF8C1rYDqisPENPE=', balance=10000, block=1, created=0, sealed=0");
UTILS.DB.executeUpdate("INSERT INTO del_votes SET delegate='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEGEAmoJEa25iVpzThVmJUIaAcOqLe0RJ5DUUBwAb8a7j0ijZJ4SwrlFcg2NoAyDuUDpchsLLcbIY=', adr='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEGCE9Tte00n6xUkDpthPniV/0X92qDSCiEJ0+/mKrtbAkKuyrp8F9UgiHLcGsF8C1rYDqisPENPE=', type='ID_UP', block=1");

UTILS.DB.executeUpdate("INSERT INTO adr SET adr='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEvpCg0A0RQTbePSr6t+A8cvNxZ05BXsgawBpaOlngKTT4mVujv5LjNt1VOax7yp9Lyzx8a9SSS+A=', balance=10000, block=1, created=0, sealed=0");
UTILS.DB.executeUpdate("INSERT INTO del_votes SET delegate='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEn9KtEZw4cRyp1YcfT4l8/U3RcZshoTWhI7eTej2dvUiWciIgLKyb8HhTiBoe1Sx9JQJwwfL2GY4=', adr='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEvpCg0A0RQTbePSr6t+A8cvNxZ05BXsgawBpaOlngKTT4mVujv5LjNt1VOax7yp9Lyzx8a9SSS+A=', type='ID_UP', block=1");
UTILS.DB.executeUpdate("INSERT INTO adr SET adr='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEqefYhXpr3C/GAxyrmoMJEgy2zYfBjAuMP3YQJOZ+nO5seD/RJD/TsB7Y84+S7UoFen5QKNtyZd8=', balance=10000, block=1, created=0, sealed=0");
UTILS.DB.executeUpdate("INSERT INTO del_votes SET delegate='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEn9KtEZw4cRyp1YcfT4l8/U3RcZshoTWhI7eTej2dvUiWciIgLKyb8HhTiBoe1Sx9JQJwwfL2GY4=', adr='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEqefYhXpr3C/GAxyrmoMJEgy2zYfBjAuMP3YQJOZ+nO5seD/RJD/TsB7Y84+S7UoFen5QKNtyZd8=', type='ID_UP', block=1");

UTILS.DB.executeUpdate("INSERT INTO adr SET adr='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEO1Nps/C8MQ2zV0tVrolGBJjJA1Z2bleM6J+OmVMR9hQLRmEgOFDKiGif3kaYhQwji0jze/P2QlU=', balance=10000, block=1, created=0, sealed=0");
UTILS.DB.executeUpdate("INSERT INTO del_votes SET delegate='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAE/zDLDPsmfWNs0M86sutC+VbzkJmm7mTr+wuqd+wxecBc2/PZrpaAakOvYDBA/G3KxutOB1C3Ux4=', adr='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEO1Nps/C8MQ2zV0tVrolGBJjJA1Z2bleM6J+OmVMR9hQLRmEgOFDKiGif3kaYhQwji0jze/P2QlU=', type='ID_UP', block=1");
UTILS.DB.executeUpdate("INSERT INTO adr SET adr='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEHVSI2pGsXvX34nkEvq7kDIt5VsCfQGIhQBR1E5BtZMdX46n/8KA2dTdDsmZ2kGKMgu8d9nxpjg4=', balance=10000, block=1, created=0, sealed=0");
UTILS.DB.executeUpdate("INSERT INTO del_votes SET delegate='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAE/zDLDPsmfWNs0M86sutC+VbzkJmm7mTr+wuqd+wxecBc2/PZrpaAakOvYDBA/G3KxutOB1C3Ux4=', adr='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEHVSI2pGsXvX34nkEvq7kDIt5VsCfQGIhQBR1E5BtZMdX46n/8KA2dTdDsmZ2kGKMgu8d9nxpjg4=', type='ID_UP', block=1");

UTILS.DB.executeUpdate("INSERT INTO adr SET adr='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEbn8E3+dARiv9ykwIlPuXEVNWhHl1xbOpC8cNWP691kT3qEEzAYbc8CqDXMnZCFHodfGXJq9YJjM=', balance=10000, block=1, created=0, sealed=0");
UTILS.DB.executeUpdate("INSERT INTO del_votes SET delegate='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEBJ5f5hTkShrGym2HU1A/yZnen2x/BRHQjCcTd4KFmxlVQuM1/QClnSNJ+BVpbx2iu/ahJ1FzZFM=', adr='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEbn8E3+dARiv9ykwIlPuXEVNWhHl1xbOpC8cNWP691kT3qEEzAYbc8CqDXMnZCFHodfGXJq9YJjM=', type='ID_UP', block=1");
UTILS.DB.executeUpdate("INSERT INTO adr SET adr='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEStwemRWstVzpEnyYaJit2Xn5I0yVhlOX2OKC7gVTPl1Vnp1Rfbetq44PK5G1qcuyzphKqPLRKZw=', balance=10000, block=1, created=0, sealed=0");
UTILS.DB.executeUpdate("INSERT INTO del_votes SET delegate='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEBJ5f5hTkShrGym2HU1A/yZnen2x/BRHQjCcTd4KFmxlVQuM1/QClnSNJ+BVpbx2iu/ahJ1FzZFM=', adr='ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEStwemRWstVzpEnyYaJit2Xn5I0yVhlOX2OKC7gVTPl1Vnp1Rfbetq44PK5G1qcuyzphKqPLRKZw=', type='ID_UP', block=1");

          }
    }
   
    
}
