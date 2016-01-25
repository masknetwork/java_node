package wallet.kernel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CBootstrap 
{
     public CBootstrap() 
     {
        // Check tables
        this.checkTables();
        
        // Delete packets
         UTILS.DB.executeUpdate("DELETE FROM packets "
                                    + "WHERE tstamp<"+(UTILS.BASIC.tstamp()-144000));
        
        // Delete active peers
        UTILS.DB.executeUpdate("DELETE FROM peers");
        
        // Delete transaction pool
        UTILS.DB.executeUpdate("DELETE FROM trans_pool");
     }  
     
     public void createTable(String tab)
     {
         UTILS.CONSOLE.write("Creating table "+tab+"...");
         
         // ----------------------- Addresses Table --------------------------------------
	 if (tab.equals("adr"))
	 {
            UTILS.DB.executeUpdate("CREATE TABLE adr(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
				                     + "adr VARCHAR(500), "
				                     + "balance DOUBLE(20,8) DEFAULT 0, "
				                     + "block BIGINT DEFAULT 0, "
				                     + "rowhash VARCHAR(100) DEFAULT '', "
                                                     + "total_received FLOAT(25, 8) DEFAULT 0, "
                                                     + "total_spent FLOAT(25, 8) DEFAULT 0, "
                                                     + "trans_no VARCHAR(100) DEFAULT 0, "
                                                     + "tweets VARCHAR(100) DEFAULT 0, "
                                                     + "following VARCHAR(100) DEFAULT 0, "
                                                     + "followers VARCHAR(100) DEFAULT 0, "
				                     + "last_interest BIGINT DEFAULT 0)");
		   
            UTILS.DB.executeUpdate("CREATE INDEX adr ON adr(adr)");
	    UTILS.DB.executeUpdate("CREATE INDEX block ON adr(block)");
	    UTILS.DB.executeUpdate("CREATE INDEX rowhash ON adr(block)");
            
            this.fillTest();
	}
         
        // ----------------------- Addresses Options --------------------------------------
	if (tab.equals("adr_options"))
	{
            UTILS.DB.executeUpdate("CREATE TABLE adr_options(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
						             + "adr VARCHAR(500), "
						             + "op_type VARCHAR(100), "
						             + "par_1 VARCHAR(1000), "
						             + "par_2 VARCHAR(1000), "
						             + "par_3 VARCHAR(1000), "
						             + "par_4 VARCHAR(1000), "
                                                             + "par_5 VARCHAR(1000), "
                                                             + "par_6 VARCHAR(1000), "
						             + "expires BIGINT DEFAULT 0, "
						             + "rowhash VARCHAR(100), "
						             + "block BIGINT DEFAULT 0)");
            
	    UTILS.DB.executeUpdate("CREATE INDEX ao_adr ON adr_options(adr)");
	    UTILS.DB.executeUpdate("CREATE INDEX ao_op ON adr_options(op_type)");
	    UTILS.DB.executeUpdate("CREATE INDEX ao_block ON adr_options(block)");
	    UTILS.DB.executeUpdate("CREATE INDEX ao_rowhash ON adr_options(rowhash)");
	}
        
        // ---------------------------------- Ads --------------------------------------
	if (tab.equals("ads"))
        {
	    UTILS.DB.executeUpdate("CREATE TABLE ads(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                     + "country VARCHAR(2) DEFAULT '', "
                                                     + "adr VARCHAR(250) DEFAULT '', "
				    		     + "title VARCHAR(250) DEFAULT '', "
				    		     + "message VARCHAR(1000) DEFAULT '',"
                                                     + "link VARCHAR(500) DEFAULT '',"
                                                     + "mkt_bid FLOAT(9,4) DEFAULT 0,"
                                                     + "expires BIGINT DEFAULT 0,"
                                                     + "block BIGINT DEFAULT 0,"
				    		     + "rowhash VARCHAR(100) DEFAULT '')");
				    
	    UTILS.DB.executeUpdate("CREATE INDEX ads_adr ON ads(adr)");
	    UTILS.DB.executeUpdate("CREATE INDEX ads_rowhash ON ads(rowhash)");
            UTILS.DB.executeUpdate("CREATE INDEX ads_block ON ads(block)");
        }
        
         
         // ------------------------------- Autoresponders --------------------------------------
	 if (tab.equals("autoresp"))
         {
	    UTILS.DB.executeUpdate("CREATE TABLE autoresp(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
			 	 		       + "userID BIGINT, "
			 	 	 	       + "net_fee_adr VARCHAR(250) DEFAULT '', "
			 	 	 	       + "adr VARCHAR(250) DEFAULT '', "
			 	 	 	       + "subject VARCHAR(250) DEFAULT '', "
			 	 	 	       + "mes VARCHAR(500) DEFAULT '', "
                                                       + "send_when VARCHAR(45), "
                                                       + "tstamp BIGINT DEFAULT 0)");
				    
	    UTILS.DB.executeUpdate("CREATE INDEX resp_userID ON autoresp(userID)");
	    UTILS.DB.executeUpdate("CREATE INDEX resp_adr ON autoresp(adr)");
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
                                                     + "payload_hash VARCHAR(250) DEFAULT '', "
                                                     + "signer_balance DOUBLE(20, 8) DEFAULT 0)");
				    
	    UTILS.DB.executeUpdate("CREATE INDEX blocks_hash ON blocks(block)");
	    UTILS.DB.executeUpdate("CREATE INDEX blocks_block ON blocks(signer)");
	    UTILS.DB.executeUpdate("CREATE INDEX blocks_signer ON blocks(hash)");
         }
         
         // ---------------------------------- Console--------------------------------------
	 if (tab.equals("console"))
	 {
	    UTILS.DB.executeUpdate("CREATE TABLE console(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
			 	 			+ "mes VARCHAR(1000) DEFAULT '', "
			 	 	 	        + "tstamp BIGINT DEFAULT 0)");
				    
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
	    UTILS.DB.executeUpdate("CREATE TABLE domains(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
	 	 			              + "adr VARCHAR(250) DEFAULT '', "
	 	 		                      + "domain VARCHAR(100), "
	 	 			              + "expires BIGINT DEFAULT 0, "
	 	 			              + "sale_price DOUBLE(10,4) DEFAULT 0, "
	 	 			              + "market_bid DOUBLE(10,4) DEFAULT 0, "
	 	 			              + "market_expires BIGINT DEFAULT 0, "
	 	 		                      + "block BIGINT DEFAULT 0, "
	 	 	 	 	 	      + "rowhash VARCHAR(250) DEFAULT '')");
	 	 	 	 	    
	    UTILS.DB.executeUpdate("CREATE INDEX dom_adr ON domains(adr)");
	    UTILS.DB.executeUpdate("CREATE INDEX dom_domain ON domains(domain)");
	    UTILS.DB.executeUpdate("CREATE INDEX dom_block ON domains(block)");
	    UTILS.DB.executeUpdate("CREATE INDEX dom_rowhash ON domains(rowhash)");
	}
         
         // ---------------------------------- Error Log--------------------------------------
	 if (tab.equals("err_log"))
         {
	    UTILS.DB.executeUpdate("CREATE TABLE err_log(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
			 	 		       + "type VARCHAR(250) DEFAULT '', "
			 	 	 	       + "mes VARCHAR(1000) DEFAULT '', "
			 	 	 	       + "file VARCHAR(100) DEFAULT '', "
                                                       + "line BIGINT DEFAULT 0, "
			 	 	 	       + "tstamp BIGINT DEFAULT 0)");
				    
	    UTILS.DB.executeUpdate("CREATE INDEX err_log_type ON err_log(type)");
            UTILS.DB.executeUpdate("CREATE INDEX err_log_file ON err_log(file)");
	}
         
         // ---------------------------------- Escrowed--------------------------------------
	 if (tab.equals("escrowed"))
         {
	    UTILS.DB.executeUpdate("CREATE TABLE escrowed(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
			 	 		       + "trans_hash VARCHAR(250) DEFAULT '', "
			 	 	 	       + "sender_adr VARCHAR(250) DEFAULT '', "
			 	 	 	       + "rec_adr VARCHAR(250) DEFAULT '', "
                                                       + "escrower VARCHAR(250) DEFAULT '', "
                                                       + "amount BIGINT DEFAULT 0, "
                                                       + "cur VARCHAR(10) DEFAULT '', "
                                                       + "block BIGINT DEFAULT 0, "
                                                       + "rowhash VARCHAR(250) DEFAULT '')");
				    
	    UTILS.DB.executeUpdate("CREATE INDEX escrowed_trans_hash ON escrowed(trans_hash)");
            UTILS.DB.executeUpdate("CREATE INDEX escrowed_sender_adr ON escrowed(sender_adr)");
            UTILS.DB.executeUpdate("CREATE INDEX escrowed_rec_adr ON escrowed(rec_adr)");
            UTILS.DB.executeUpdate("CREATE INDEX escrowed_escrower ON escrowed(escrower)");
            UTILS.DB.executeUpdate("CREATE INDEX escrowed_block ON escrowed(block)");
            UTILS.DB.executeUpdate("CREATE INDEX escrowed_rowhash ON escrowed(rowhash)");
	}
         
         // ---------------------------------- Footprints--------------------------------------
	 if (tab.equals("footprints"))
         {
	    UTILS.DB.executeUpdate("CREATE TABLE footprints(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
			 	 		       + "packet_hash VARCHAR(250) DEFAULT '', "
			 	 	 	       + "par_1_name VARCHAR(100) DEFAULT '', "
			 	 	 	       + "par_1_val VARCHAR(1000) DEFAULT '', "
                                                       + "par_2_name VARCHAR(100) DEFAULT '', "
			 	 	 	       + "par_2_val VARCHAR(1000) DEFAULT '', "
                                                       + "par_3_name VARCHAR(100) DEFAULT '', "
			 	 	 	       + "par_3_val VARCHAR(1000) DEFAULT '', "
                                                       + "par_4_name VARCHAR(100) DEFAULT '', "
			 	 	 	       + "par_4_val VARCHAR(1000) DEFAULT '', "
                                                       + "par_5_name VARCHAR(100) DEFAULT '', "
			 	 	 	       + "par_5_val VARCHAR(1000) DEFAULT '', "
                                                       + "block BIGINT DEFAULT 0, "
                                                       + "tstamp BIGINT DEFAULT 0, "
                                                       + "payload_hash VARCHAR(100) DEFAULT '', "
                                                       + "packet_type VARCHAR(100) DEFAULT '', "
                                                       + "fee_src VARCHAR(250) DEFAULT '', "
                                                       + "fee_amount FLOAT(20, 8) DEFAULT 0, "
                                                       + "fee_hash VARCHAR(250) DEFAULT '')");
				    
	    UTILS.DB.executeUpdate("CREATE INDEX foot_packet_hash ON footprints(packet_hash)");
            UTILS.DB.executeUpdate("CREATE INDEX foot_block ON footprints(block)");
        }
	     
	
         // ---------------------------------- Images --------------------------------------
	 if (tab.equals("imgs"))
         {
	    UTILS.DB.executeUpdate("CREATE TABLE imgs(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
				    		     + "hash VARCHAR(100), "
				    		     + "img BLOB)");
				    
	    UTILS.DB.executeUpdate("CREATE INDEX imgs_hash ON imgs(hash)");
         }
         
         // ---------------------------------- Images Stack--------------------------------------
	 if (tab.equals("imgs_stack"))
         {
	    UTILS.DB.executeUpdate("CREATE TABLE imgs_stack(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
				    		         + "url VARCHAR(1000))");
				    
	   
         }
         
         // ---------------------------------- Interest --------------------------------------
	 if (tab.equals("interest"))
         {
	    UTILS.DB.executeUpdate("CREATE TABLE interest(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                       + "adr VARCHAR(250), "
                                                       + "net_fee_adr VARCHAR(250), "
                                                       + "to_adr VARCHAR(250), "
				    		       + "last_interest BIGINT)");
				    
	    UTILS.DB.executeUpdate("CREATE INDEX interest_adr ON interest(adr)");
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
         
         // ---------------------------------- Multisignatures --------------------------------------
	 if (tab.equals("multisig"))
         {
	    UTILS.DB.executeUpdate("CREATE TABLE multisig(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                       + "trans_hash VARCHAR(250) DEFAULT '', "
                                                       + "sender_adr VARCHAR(250) DEFAULT '', "
                                                       + "rec_adr VARCHAR(250) DEFAULT '', "
                                                       + "signer_1 VARCHAR(250) DEFAULT '', "
                                                       + "sign_1 VARCHAR(500) DEFAULT '', "
                                                       + "signer_2 VARCHAR(250) DEFAULT '', "
                                                       + "sign_2 VARCHAR(500) DEFAULT '', "
                                                       + "signer_3 VARCHAR(250) DEFAULT '', "
                                                       + "sign_3 VARCHAR(500) DEFAULT '', "
                                                       + "signer_4 VARCHAR(250) DEFAULT '', "
                                                       + "sign_4 VARCHAR(500) DEFAULT '', "
                                                       + "signer_5 VARCHAR(250) DEFAULT '', "
                                                       + "sign_5 VARCHAR(500) DEFAULT '', "
                                                       + "amount FLOAT(20, 8) DEFAULT 0, "
                                                       + "cur VARCHAR(10) DEFAULT 'MSK', "
                                                       + "required INT DEFAULT 1, "
                                                       + "block BIGINT DEFAULT 0, "
                                                       + "rowhash VARCHAR(250))");
				    
	    UTILS.DB.executeUpdate("CREATE INDEX multisig_hash ON multisig(trans_hash)");
            UTILS.DB.executeUpdate("CREATE INDEX multisig_block ON multisig(block)");
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
                                                     + "mine BIGINT DEFAULT 0,"
				    		     + "last_mine BIGINT DEFAULT 0)");
				    
	    UTILS.DB.executeUpdate("CREATE INDEX my_adr_adr ON my_adr(adr)");
	    UTILS.DB.executeUpdate("CREATE INDEX my_adr_userID ON my_adr(userID)");
         }
         
         // ---------------------------------- Net stat --------------------------------------
	 if (tab.equals("net_stat"))
         {
	    UTILS.DB.executeUpdate("CREATE TABLE net_stat(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
				    		     + "last_block BIGINT DEFAULT 0, "
				    		     + "last_hash VARCHAR(250) DEFAULT '',"
				    		     + "net_dif VARCHAR(100) DEFAULT '',"
				    		     + "last_tstamp BIGINT DEFAULT 0)");
            
            UTILS.DB.executeUpdate("INSERT INTO net_stat(last_block, "
                                                      + "last_hash, "
                                                      + "net_dif, "
                                                      + "last_tstamp) "
                                            + "VALUES('0', "
                                                    + "'c1d709b7eac7d14f040b95aea35895ce7c2f0cd059d7a157674dbffd598d7872', "
                                                    + "net_dif='10000000000000', "
                                                    + "last_tstamp='1447522627')");
	}
        
        // ----------------------------------- My Trans --------------------------------------
	if (tab.equals("my_trans"))
	{
	    UTILS.DB.executeUpdate("CREATE TABLE my_trans(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
	                                               + "userID BIGINT DEFAULT 0, "
                                                       + "adr VARCHAR(250) DEFAULT '', "
                                                       + "adr_assoc VARCHAR(250) DEFAULT '', "
	 	 	                               + "amount DOUBLE(20, 8) DEFAULT 0, "
	 	 	 			       + "cur VARCHAR(10) DEFAULT '', "
	 	 	 			       + "expl VARCHAR(250) DEFAULT '', "
	 	 	 			       + "escrower VARCHAR(250) DEFAULT '', "
	 	 	 			       + "hash VARCHAR(100) DEFAULT '', "
                                                       + "block BIGINT DEFAULT 0, "
                                                       + "tstamp BIGINT DEFAULT 0, "
	 	 	 			       + "status VARCHAR(20) DEFAULT '', "
                                                       + "field_1 VARCHAR(1000) DEFAULT '', "
                                                       + "field_2 VARCHAR(1000) DEFAULT '', "
                                                       + "field_3 VARCHAR(1000) DEFAULT '', "
                                                       + "field_4 VARCHAR(1000) DEFAULT '', "
                                                       + "field_5 VARCHAR(1000) DEFAULT '', "
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
         
          // ----------------------------------- Packets --------------------------------------
         if (tab.equals("packets"))
	 {
	    UTILS.DB.executeUpdate("CREATE TABLE packets(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
	 					       + "tip VARCHAR(100), "
	 					       + "tstamp BIGINT DEFAULT 0, "
	 					       + "hash VARCHAR(500), "
                                                       + "fromIP VARCHAR(500), "
	 					       + "sig VARCHAR(500))");
	    
            UTILS.DB.executeUpdate("CREATE INDEX packets_hash ON packets(hash)");
            UTILS.DB.executeUpdate("CREATE INDEX packets_tip ON packets(tip)");
	}
         
         // ----------------------------------- Pending addresses --------------------------------------
         if (tab.equals("pending_adr"))
	 {
	    UTILS.DB.executeUpdate("CREATE TABLE pending_adr(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
	 					       + "share_adr VARCHAR(250) DEFAULT '', "
	 					       + "pub_key  VARCHAR(250) DEFAULT '', "
	 					       + "priv_key  VARCHAR(250) DEFAULT '', "
                                                       + "parsed BIGINT DEFAULT 0)");
	    
            UTILS.DB.executeUpdate("CREATE INDEX pending_hash ON pending_adr(share_adr)");
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
	 	 	 			    + "amount DOUBLE(10,4) DEFAULT 0, "
	 	 	 			    + "cur VARCHAR(10) DEFAULT '', "
	 	 	 			    + "escrower VARCHAR(250) DEFAULT '', "
	 	 	 			    + "hash VARCHAR(100) DEFAULT '', "
	 	 	 			    + "block BIGINT DEFAULT 0, "
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
			 	 			+ "par_1 VARCHAR(1000) DEFAULT '', "
			 	 			+ "par_2 VARCHAR(1000) DEFAULT '', "
			 	 			+ "par_3 VARCHAR(1000) DEFAULT '', "
			 	 			+ "par_4 VARCHAR(1000) DEFAULT '', "
			 	 			+ "par_5 VARCHAR(1000) DEFAULT '', "
			 	 			+ "par_6 VARCHAR(1000) DEFAULT '', "
			 	 			+ "par_7 VARCHAR(1000) DEFAULT '', "
			 	 			+ "par_8 VARCHAR(1000) DEFAULT '', "
			 	 			+ "par_9 VARCHAR(1000) DEFAULT '', "
			 	 			+ "par_10 VARCHAR(1000) DEFAULT '', "
                                                        + "par_11 VARCHAR(1000) DEFAULT '', "
                                                        + "par_12 VARCHAR(1000) DEFAULT '', "
                                                        + "par_13 VARCHAR(1000) DEFAULT '', "
                                                        + "par_14 VARCHAR(1000) DEFAULT '', "
                                                        + "par_15 VARCHAR(1000) DEFAULT '', "
                                                        + "par_16 VARCHAR(1000) DEFAULT '', "
                                                        + "par_17 VARCHAR(1000) DEFAULT '', "
                                                        + "par_18 VARCHAR(1000) DEFAULT '', "
                                                        + "par_19 VARCHAR(1000) DEFAULT '', "
                                                        + "par_20 VARCHAR(1000) DEFAULT '', "
                                                        + "par_21 VARCHAR(1000) DEFAULT '', "
                                                        + "par_22 VARCHAR(1000) DEFAULT '', "
                                                        + "par_23 VARCHAR(1000) DEFAULT '', "
                                                        + "par_24 VARCHAR(1000) DEFAULT '', "
                                                        + "par_25 VARCHAR(1000) DEFAULT '', "
                                                        + "resp_1 VARCHAR(1000) DEFAULT '', "
                                                        + "resp_2 VARCHAR(1000) DEFAULT '', "
			 	 			+ "amount DOUBLE, "
			 	 			+ "cur VARCHAR(100), "
			 	 			+ "website_1 VARCHAR(250), "
			 	 			+ "website_2 VARCHAR(250), "
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
                                                             +"msk_price FLOAT(9,4) DEFAULT 0)");
             
             UTILS.DB.executeUpdate("INSERT INTO web_sys_data(status, msk_price) VALUES('ID_OFFLINE', '1')");
         }
         
         // ------------------------------------- Tweets ------------------------------------------------
         if (tab.equals("tweets"))
         {
             UTILS.DB.executeUpdate("CREATE TABLE tweets(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                        +"tweetID BIGINT DEFAULT '0', "
                                                        +"adr VARCHAR(250) DEFAULT '', "
                                                        +"target_adr VARCHAR(250) DEFAULT '', "
                                                        +"mes VARCHAR(1000) DEFAULT '', "
                                                        +"pic_1 VARCHAR(250) DEFAULT '', "
                                                        +"pic_2 VARCHAR(250) DEFAULT '', "
                                                        +"pic_3 VARCHAR(250) DEFAULT '', "
                                                        +"pic_4 VARCHAR(250) DEFAULT '', "
                                                        +"pic_5 VARCHAR(250) DEFAULT '', "
                                                        +"video VARCHAR(250) DEFAULT '', "
                                                        +"rowhash VARCHAR(100) DEFAULT '', "
                                                        +"block BIGINT DEFAULT '0', "
                                                        +"status VARCHAR(50) DEFAULT 'ID_APROVE', "
                                                        +"retweet VARCHAR(2) DEFAULT 'N', "
                                                        +"retweet_tweet_ID BIGINT DEFAULT '0', "
                                                        +"received BIGINT DEFAULT '0', "
                                                        +"likes BIGINT DEFAULT '0', "
                                                        +"comments BIGINT DEFAULT '0', "
                                                        +"retweets BIGINT DEFAULT '0')");
             
              UTILS.DB.executeUpdate("CREATE INDEX tweets_tweetID ON tweets(tweetID)");
              UTILS.DB.executeUpdate("CREATE INDEX tweets_adr ON tweets(adr)");
              UTILS.DB.executeUpdate("CREATE INDEX tweets_target_adr ON tweets(target_adr)");
              UTILS.DB.executeUpdate("CREATE INDEX tweets_status ON tweets(status)");
              UTILS.DB.executeUpdate("CREATE INDEX tweets_retweet_tweet_ID ON tweets(retweet_tweet_ID)");
         }
         
         // ------------------------------------- Tweets Comments ------------------------------------------------
         if (tab.equals("tweets_comments"))
         {
             UTILS.DB.executeUpdate("CREATE TABLE tweets_comments(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                                +"tweetID BIGINT DEFAULT '0', "
                                                                +"mes VARCHAR(1000) DEFAULT '', "
                                                                +"rowhash VARCHAR(100) DEFAULT '', "
                                                                +"block BIGINT DEFAULT '0', "
                                                                +"status VARCHAR(25) DEFAULT '', "
                                                                +"rowID BIGINT DEFAULT '', "
                                                                +"comID BIGINT DEFAULT '', "
                                                                +"adr VARCHAR(250) DEFAULT '')");
             
              UTILS.DB.executeUpdate("CREATE INDEX tweets_comments_tweetID ON tweets_comments(tweetID)");
              UTILS.DB.executeUpdate("CREATE INDEX tweets_comments_rowhash ON tweets_comments(rowhash)");
              UTILS.DB.executeUpdate("CREATE INDEX tweets_comments_block ON tweets_comments(block)");
              UTILS.DB.executeUpdate("CREATE INDEX tweets_comments_status ON tweets_comments(status)");
              UTILS.DB.executeUpdate("CREATE INDEX tweets_comments_rowID ON tweets_comments(rowID)");
              UTILS.DB.executeUpdate("CREATE INDEX tweets_comments_comID ON tweets_comments(comID)");
         }
         
         // ------------------------------------- Tweets Follow ------------------------------------------------
         if (tab.equals("tweets_follow"))
         {
             UTILS.DB.executeUpdate("CREATE TABLE tweets_follow(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                                +"adr VARCHAR(250) DEFAULT '', "
                                                                +"follows VARCHAR(250) DEFAULT '', "
                                                                +"block BIGINT DEFAULT '0', "
                                                                +"rowhash VARCHAR(100) DEFAULT '')");
             
              UTILS.DB.executeUpdate("CREATE INDEX tweets_follow_adr ON tweets_follow(adr)");
              UTILS.DB.executeUpdate("CREATE INDEX tweets_follow_follows ON tweets_follow(follows)");
              UTILS.DB.executeUpdate("CREATE INDEX tweets_follow_block ON tweets_follow(block)");
              UTILS.DB.executeUpdate("CREATE INDEX tweets_follow_rowhash ON tweets_follow(rowhash)");
         }
         
         // ------------------------------------- Tweets Likes ------------------------------------------------
         if (tab.equals("tweets_likes"))
         {
             UTILS.DB.executeUpdate("CREATE TABLE tweets_likes(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                                +"tweetID BIGINT DEFAULT '0', "
                                                                +"adr VARCHAR(250) DEFAULT '', "
                                                                +"block BIGINT DEFAULT '0', "
                                                                +"rowhash VARCHAR(100) DEFAULT '')");
             
              UTILS.DB.executeUpdate("CREATE INDEX tweets_likes_tweetID ON tweets_likes(tweetID)");
              UTILS.DB.executeUpdate("CREATE INDEX tweets_likes_adr ON tweets_likes(adr)");
              UTILS.DB.executeUpdate("CREATE INDEX tweets_likes_block ON tweets_likes(block)");
              UTILS.DB.executeUpdate("CREATE INDEX tweets_likes_rowhash ON tweets_likes(rowhash)");
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
         
         // ------------------------------------- Profiles ------------------------------------------------
         if (tab.equals("profiles"))
         {
             UTILS.DB.executeUpdate("CREATE TABLE profiles(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                         +"adr VARCHAR(250) DEFAULT '', "
                                                         +"name VARCHAR(50) DEFAULT '', "
                                                         +"pic_back VARCHAR(250) DEFAULT '', "
                                                         +"pic VARCHAR(250) DEFAULT '', "
                                                         +"description VARCHAR(500) DEFAULT '', "
                                                         +"website VARCHAR(250) DEFAULT '', "
                                                         +"email VARCHAR(200) DEFAULT '', "
                                                         +"expire BIGINT DEFAULT '0', "
                                                         +"block BIGINT DEFAULT '0')");
             
             UTILS.DB.executeUpdate("CREATE INDEX prof_adr ON profiles(adr)");
         }
         
          // ------------------------------------- Web Users ------------------------------------------------
         if (tab.equals("web_users"))
         {
             UTILS.DB.executeUpdate("CREATE TABLE web_users(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                             +"user VARCHAR(50) DEFAULT '', "
                                                             +"pass VARCHAR(100) DEFAULT '', "
                                                             +"email VARCHAR(100) DEFAULT '', "
                                                             +"status VARCHAR(50) DEFAULT '', "
                                                             +"tstamp BIGINT DEFAULT 0, "
                                                             + "pending_adr BIGINT DEFAULT 0, "
                                                             + "unread_esc BIGINT DEFAULT 0, "
                                                             + "unread_mes BIGINT DEFAULT 0, "
                                                             + "unread_multisig BIGINT DEFAULT 0, "
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
              UTILS.DB.executeUpdate("CREATE TABLE assets(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                             +"adr VARCHAR(250) DEFAULT '', "
                                                             +"symbol VARCHAR(10) DEFAULT '', "
                                                             +"title VARCHAR(250) DEFAULT '', "
                                                             +"description VARCHAR(1000) DEFAULT '', "
                                                             +"how_buy VARCHAR(1000) DEFAULT '', "
                                                             +"how_sell VARCHAR(1000) DEFAULT '', "
                                                             +"web_page VARCHAR(250) DEFAULT '', "
                                                             +"pic VARCHAR(250) DEFAULT '', "
                                                             +"expire BIGINT DEFAULT 0, "
                                                             +"qty BIGINT DEFAULT 0, "
                                                             +"trans_fee_adr VARCHAR(250), "
                                                             +"trans_fee FLOAT(9,2), "
                                                             +"can_increase VARCHAR(2) DEFAULT 'Y', "
                                                             +"interest DOUBLE(9,2) DEFAULT 0, "
                                                             +"interest_interval BIGINT DEFAULT 0, "
                                                             +"rowhash VARCHAR(100) DEFAULT '', "
                                                             +"block BIGINT DEFAULT 0)");
             
             UTILS.DB.executeUpdate("CREATE INDEX assets_adr ON assets(adr)");
             UTILS.DB.executeUpdate("CREATE INDEX assets_symbol ON assets(symbol)");
             UTILS.DB.executeUpdate("CREATE INDEX assets_block ON assets(block)");
         }
         
         // ------------------------------------- Profiles ------------------------------------------------
         if (tab.equals("assets_owners"))
         {
              UTILS.DB.executeUpdate("CREATE TABLE assets_owners(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                               +"owner VARCHAR(250) DEFAULT '', "
                                                               +"symbol VARCHAR(10) DEFAULT '', "
                                                               +"qty FLOAT(20,8) DEFAULT 0, "
                                                               +"last_interest BIGINT DEFAULT 0, "
                                                               +"rowhash VARCHAR(100) DEFAULT '', "
                                                               +"block BIGINT DEFAULT 0)");
             
             UTILS.DB.executeUpdate("CREATE INDEX assets_owners_owner ON assets_owners(owner)");
             UTILS.DB.executeUpdate("CREATE INDEX assets_owners_symbol ON assets_owners(symbol)");
             UTILS.DB.executeUpdate("CREATE INDEX assets_owners_block ON assets_owners(block)");
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
             
             UTILS.DB.executeUpdate("CREATE INDEX feeds_adr ON feeds(owner)");
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
                                                                 +"rl_symbol VARCHAR(20) DEFAULT '', "
                                                                 +"fee FLOAT(9, 4) DEFAULT 0.0001, "
                                                                 +"expire BIGINT DEFAULT 0, "
                                                                 +"val FLOAT(20,8) DEFAULT 0, "
                                                                 +"mkt_status VARCHAR(50) DEFAULT '', "
                                                                 +"rowhash VARCHAR(100) DEFAULT '', "
                                                                 +"block BIGINT DEFAULT 0)");
             
             UTILS.DB.executeUpdate("CREATE INDEX feeds_branches_feed_symbol ON feeds_branches(feed_symbol)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_branches_symbol ON feeds_branches(symbol)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_branches_rowhash ON feeds_branches(rowhash)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_branches_block ON feeds_branches(block)");
         }
         
         // ------------------------------------- Feeds Sources ------------------------------------------------
         if (tab.equals("feeds_sources"))
         {
              UTILS.DB.executeUpdate("CREATE TABLE feeds_sources(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                                 +"feed_symbol VARCHAR(100) DEFAULT ''");
             
             UTILS.DB.executeUpdate("CREATE INDEX feeds_sources ON feeds_sources(feed_symbol)");
         }
         
         // ------------------------------------- Feeds Data ------------------------------------------------
         if (tab.equals("feeds_data"))
         {
             UTILS.DB.executeUpdate("CREATE TABLE feeds_data(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                            +"feed VARCHAR(10) DEFAULT '', "
                                                            +"feed_branch VARCHAR(10) DEFAULT '', "
                                                            +"val FLOAT(20, 8) DEFAULT 0, "
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
                                                                +"pos_type VARCHAR(10) DEFAULT '', "
                                                                +"posID BIGINT DEFAULT 0, "
                                                                +"pos_symbol VARCHAR(10) DEFAULR '', "
                                                                +"val DOUBLE(20,8) DEFAULT 0, "
                                                                +"block BIGINT DEFAULT 0)");
             
             UTILS.DB.executeUpdate("CREATE INDEX feeds_pos_posID ON feeds_pos_data(posID)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_pos_pos_symbol ON feeds_pos_data(pos_symbol)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_pos_data_block ON feeds_pos_data(block)");
         }
         
         // ------------------------------------- Feeds Bets ------------------------------------------------
         if (tab.equals("feeds_bets"))
         {
             UTILS.DB.executeUpdate("CREATE TABLE feeds_bets(ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                                                            +"uid BIGINT DEFAULT 0, "
                                                            +"adr VARCHAR(250) DEFAULT '', "
                                                            +"feed_symbol_1 VARCHAR(10) DEFAULT '', "
                                                            +"branch_symbol_1 VARCHAR(10) DEFAULT '', "
                                                            +"price_1 DOUBLE(20,8) DEFAULT 0, "
                                                            +"feed_symbol_2 VARCHAR(10) DEFAULT '', "
                                                            +"branch_symbol_2 VARCHAR(10) DEFAULT '', "
                                                            +"price_2 DOUBLE(20,8) DEFAULT 0, "
                                                            +"feed_symbol_3 VARCHAR(10) DEFAULT '', "
                                                            +"branch_symbol_3 VARCHAR(10) DEFAULT '', "
                                                            +"price_3 DOUBLE(20,8) DEFAULT 0, "
                                                            +"tip VARCHAR(10) DEFAULT 'ID_TOUCH', "
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
                                                            +"status VARCHAR(10) DEFAULT 'ID_PENDING', "
                                                            +"rowhash VARCHAR(100) DEFAULT '', "
                                                            +"block BIGINT DEFAULT 0)");
             
             UTILS.DB.executeUpdate("CREATE INDEX feeds_bets_uid ON feeds_bets(uid)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_bets_feed_symbol ON feeds_bets(feed_symbol)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_bets_feed_component ON feeds_bets(feed_component)");
             UTILS.DB.executeUpdate("CREATE INDEX feeds_bets_block ON feeds_bets(block)");
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
         
        // Done
        UTILS.CONSOLE.write("Done.");
     }
     
     public boolean tableExist(String tab)
     {
	ResultSet rs;
        
        
		
        try
        {
           Statement s=UTILS.DB.getStatement();
           
	   if (UTILS.SETTINGS.db.equals("hsql"))
	       rs=s.executeQuery("SELECT * "
	   		              + "FROM INFORMATION_SCHEMA.TABLES "
	   		             + "w TABLE_NAME='"+tab+"'"
	   		               + "AND TABLE_SCHEMA='PUBLIC'");	
	   else
	       rs=s.executeQuery("SELECT * "
                                       + "FROM INFORMATION_SCHEMA.TABLES "
                                      + "WHERE TABLE_NAME='"+tab+"' "
	   		                + "AND TABLE_SCHEMA='"+UTILS.SETTINGS.db_name+"'");
	   
	   // Return
	   if (UTILS.DB.hasData(rs)==false) 
           {
               if (s!=null) s.close();
	       return false;
           }
	   else
           {
               if (s!=null) s.close();
               return true;
           }
        }
        catch (SQLException ex)
        {
            UTILS.LOG.log("SQLException", ex.getMessage(), "CBootstrap.java", 229);
        }
        
        return false;
    }
     
     public void checkTables()
     {
	if (this.tableExist("adr")==false)
            this.createTable("adr");
        
        if (this.tableExist("adr_options")==false)
            this.createTable("adr_options");
        
        if (this.tableExist("ads")==false)
            this.createTable("ads");
        
        if (this.tableExist("blocks")==false)
            this.createTable("blocks");
        
         if (this.tableExist("autoresp")==false)
            this.createTable("autoresp");
        
        if (this.tableExist("con_log")==false)
            this.createTable("con_log");
        
        if (this.tableExist("console")==false)
            this.createTable("console");
        
        if (this.tableExist("domains")==false)
            this.createTable("domains");
        
        if (this.tableExist("err_log")==false)
            this.createTable("err_log");
        
        if (this.tableExist("escrowed")==false)
            this.createTable("escrowed");
        
        if (this.tableExist("footprints")==false)
            this.createTable("footprints");
        
        if (this.tableExist("interest")==false)
            this.createTable("interest");
        
        if (this.tableExist("mes")==false)
            this.createTable("mes");
        
        if (this.tableExist("multisig")==false)
            this.createTable("multisig");
        
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
        
        if (this.tableExist("packets")==false)
            this.createTable("packets");
        
         if (this.tableExist("pending_adr")==false)
            this.createTable("pending_adr");
         
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
        
        if (this.tableExist("tweets_comments")==false)
            this.createTable("tweets_comments");
        
        if (this.tableExist("tweets_follow")==false)
            this.createTable("tweets_follow");
        
        if (this.tableExist("tweets_likes")==false)
            this.createTable("tweets_likes");
        
         if (this.tableExist("tweets_trends")==false)
            this.createTable("tweets_trends");
         
         if (this.tableExist("assets")==false)
            this.createTable("assets");
         
         if (this.tableExist("assets_owners")==false)
            this.createTable("assets_owners");
         
         if (this.tableExist("feeds_data")==false)
            this.createTable("feeds_data");
         
         if (this.tableExist("feeds_bets")==false)
            this.createTable("feeds_bets");
         
         if (this.tableExist("feeds_pos_data")==false)
            this.createTable("feeds_pos_data");
    }
   
    public void insertAdr(String adr, double balance)
    {
		UTILS.DB.executeUpdate("INSERT INTO adr(adr, "
                                                      + "balance, "
                                                      + "rowhash, "
                                                      + "block, "
                                                      + "last_interest) "
                                          + "VALUES('"+adr+"', "
                                                 + "'"+balance+"', "
                                                   + "'4EEACEDOgAEAZKrIWRrp7blp6pPgkmMpaFeBNwpNV9dd', "
                                                   + "'14073262', "
                                                   + "'0')");
    }
     
    public void fillTest()
    {
        this.insertAdr("default", 990000000);
	this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEmzMuWmpif2JdeB1/UA7XQumglLj3o4/qF/CisxzJyXf7JoXXmGvBDJFbCSr8Si09zKtkfoA7jyU=", 1000000);
		   
    }
}
