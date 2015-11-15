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
                             + "WHERE tstamp<"+(UTILS.BASIC.tstamp()-86400));
        
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
                                                     + "dificulty VARCHAR(250) DEFAULT '')");
				    
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
				    
	    UTILS.DB.executeUpdate("CREATE INDEX imgs_stack_url ON imgs_stack(url)");
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
                                                    + "net_dif='12796879944219474945405226983986958825515335341711337340794840515119360', "
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
	    
            UTILS.DB.executeUpdate("CREATE INDEX packets_hash ON packets(hash)");
            UTILS.DB.executeUpdate("CREATE INDEX packets_tip ON packets(tip)");
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
                                                             +"msk_price FLOAT(9,4) DEFAULT 0)");
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
         
        // Done
        UTILS.CONSOLE.write("Done.");
     }
     
     public boolean tableExist(String tab)
     {
	ResultSet rs;
        
        
		
        try
        {
           Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
           
	   if (UTILS.SETTINGS.db.equals("hsql"))
	       rs=s.executeQuery("SELECT * "
	   		              + "FROM INFORMATION_SCHEMA.TABLES "
	   		             + "WHERE TABLE_NAME='"+tab+"'"
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
        
        if (this.tableExist("assets")==false)
            this.createTable("assets");
        
        if (this.tableExist("assets_markets")==false)
            this.createTable("assets_markets");
        
        if (this.tableExist("assets_markets_pos")==false)
            this.createTable("assets_markets_pos");
        
        if (this.tableExist("assets_owners")==false)
            this.createTable("assets_owners");
        
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
        
        if (this.tableExist("imgs")==false)
            this.createTable("imgs");
        
        if (this.tableExist("imgs_stack")==false)
            this.createTable("imgs_stack");
        
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
    }
   
    public void insertAdr(String adr, double balance)
    {
		UTILS.DB.executeUpdate("INSERT INTO adr(adr, "
                                                      + "balance, "
                                                      + "rating, "
                                                      + "stars_1, "
                                                      + "stars_2, "
                                                      + "stars_3, "
                                                      + "stars_4, "
                                                      + "stars_5, "
                                                      + "rowhash, "
                                                      + "block, "
                                                      + "last_interest) "
                                          + "VALUES('"+adr+"', "
                                                 + "'"+balance+"', "
                                                   + "'0', "
 	    + "'0', "
 	    + "'0', "
 	    + "'0', "
 	    + "'0', "
 	    + "'0', "
 	    + "'4EEACEDOgAEAZKrIWRrp7blp6pPgkmMpaFeBNwpNV9dd', "
         + "'14073262', "
         + "'0')");
    }
     
    public void fillTest()
    {
        this.insertAdr("default", 1000000);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEzCZiNDie9xEdt1ejKgFezoPHzQEWjUG+uUCKaTsQ1FsZ/JSV0VYHxPR+vnIwTZTLXtPuD15xcBY=", 1403);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEGKpJYlIqMbObwBhsMujBr9cNzj/3c8vXxKWZadaQ+La6eEEjm98F/IbnNvH7HiroJeSi8CBB+Q8=", 9791);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAE9kdxPKBXgQarZ4l9JLUC2ib4Bnq5J2Xot7YBINOhl4FEVqPMX/wtXWSWLqvdm54MbxSOnJuGzaA=", 6866);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEEyH7gcl1bsNdKUptBm8AqFE9M+NX64/05f5haKWVNGO1ll0f4peUHD3DuTNVIbPQO46BcMh5gV8=", 1065);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAE1saQACy36ePJZUlaiZzI0LZDkbDbAM9rHz7ewz4dqxw4ELK9/EX/XgX+4AwENLbe6Z2/5LEtHZU=", 9380);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAE4nt8iCKAeHK04Dj33tf9FU6uoRFSQFx3Wwk0j8zc6Vnj2ZUyyMlTNzG+rN8w8rgrJqn9ZDcwhYA=", 3226);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEPIBzceHM/V5TTh/GdpO0z5To8yLD5QkwFeFs3yyWGg7oPAe93DyAavEWj41bfrFMUuOA2UCBc5Y=", 5269);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAE6RKfG95NBrVDh1z3RugbjNjr1OynWfSdgzKS2DwvMRak9NFzwoNcDDTuzb/avSaXbntSaFLnYZ8=", 8228);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAE4y9067Tm257mCXqZedk866kzd+BZq9gspOMYPuVY7Pwum1VivTO5W+k5O+VWVYJq0VcvpCd/6pY=", 1003);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEMn/3Pg+D4ZUowC4YchL4HsYALs95ay/4D31SVv7F884zqtDZeCXIFpqcTgyKTySGx5uH/ruhIOs=", 6756);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEmzMuWmpif2JdeB1/UA7XQumglLj3o4/qF/CisxzJyXf7JoXXmGvBDJFbCSr8Si09zKtkfoA7jyU=", 1696);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEiBFsj/wAAe4DEC7G+llRrZyYUjHgibpZ194X3mWrPnOvuBbj7N8sGAi9Gfn9zM34bbrSEqhUocY=", 8760);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEcA5WMYD2IdOOG8rQo9fdm0tG/HUutaJYG5Ynk8acn5jV4PoNv47rY1+y04aQey7JrkGxRrrtlt8=", 4314);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEWyLBlyZy6RW3LluwEvotMFjpHbz+fs/6dxZrm0d7eUtTxqAooQHBA+2wIKc2NN6buwB913+D1YM=", 3519);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEVPjqkVieKVzBlnbPDqMxTsjtL6Kl0jN9kw37CyJQ/JDPTLledemeRU6JLeJGmYS7oizXSZr6lVg=", 1137);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAE0bNRS3kjh3JdhmbVvIpo4dyE5xV5wPLyrjcZxFlp+NakSM9NaoxNPhP8hBAExyvj8Hc01ZLSBCY=", 5823);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEtmhUY494QDXHn6nwPjHkSIoBhis3Ah78Z7hbL8ORNhGGVeaQqIutaEskx7f505sx0iz02LEW2Ik=", 5934);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEOLzk1rVuUNrTfDq9B8tanNG15cIMPgZYOWjJbuToTV+UFpA6rg9tCap3Z+EnPwAaFAhvNFwqD6g=", 9007);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEzQddAA1A1YQuyHYZw4aISO16shmIgP79RnxGelI8Jy3JpkHiFtclCeLNMhEEauP3PEFpqV8TgQ4=", 8612);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEi26kafXPuXkdGqBpIdgTHGFMjdQvi7beQpV96jBomXbOn0TkzE3uf/woHnYL4B5scW39VnI80pc=", 4919);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEDbzLqoRsNNTJJlaw8Mb5GZfmKPHz3A5zmIXjRhOuGbFqcfGKekMkVkMyMNNbvxs4cWP32v4JMio=", 3242);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEElmPquXZNkOVNLbGQeM7nKtqmIKNJ3To5ThG1b+FNUoTpIefYUu/a3s2QSTnVSKV4XB+FQP4tSs=", 4158);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAE736kepOG5NWZ4A2GD/suhTHygNSpAXBwgq+fg0tpFbafHkdEU+Tj+MrWOr/EX+77Kgf5nL1In/E=", 7735);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAE0eHqSgeobFGkeK/J6vzPA6cfuk2FJGmbJl8v53LjH+X8eDcOPWSpjydN9zIao8gwsg6tJIjXUYk=", 80);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEzWDdBUdDiJdkkAkx9WsM+7WZdarsFyDJqqkiqH4sD4ceJ97RO9WKWpIFfKUfOLpHx40Txb9OK4Q=", 5849);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEcedZ7Te+3aN081Ml+CKXW1BM1DVYcsadRt+YTsCrbUQQ+wzhF7OgAO0uw286o1RzXbwOICf+2Xk=", 9448);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAE1y+tj/26Tc+7j94gquG9NmVtLqSiakvWSP60RFFivWmfJZ0PJ1YSDqBghYmkkcvYPaoKD0jzevE=", 13);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEYgCFHpiCwQMnTMBZWMFkMFRvLVZgFQUvWqlFHO1wBQVjIokq8rfH6OGXbO4+mbOjK1p//+oco20=", 4828);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEOQHsApXDETIcsx9ucbJtbkatQLhiodbB/jrBniriuYK4da3h2yLEOHTkH13zeUUbtHKRAbPa2dY=", 3361);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEwQ2AzJcAsKEWgQJCgmJXuSbKWJJyForsrZjFK8MUr4yUHIpMwD4pgtDc+w7aGUzFKiKW8kF7ocI=", 4813);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEvA5ZQobWynRCWVONCkG/dIQWJyQHMtWTKawFmfCtl97tge/LQ5+x5+ZDjHY5a9gEUgWPUmhEHbA=", 1040);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEbTRhGxq3b3QNWReV6vtnyHWOw1L7sPBFYEfmq+1DwZkEz1A2NAn9QEgEBxyWIHUMd6hHiJMlH4Q=", 5968);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEGuKF6Pv3QMVAHwLh0PVWdZdZGWvAKtokmFxM7MEdeTicGAfRFj/j6YoQkt9B+4+ee7BZsJGKH/o=", 3140);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAE06hUEn+/YvAen6NE3p3+AQm5H/q+jXJUrNCP2VJaWtGKO4J+ABYHfqcGR1gPVlnHfZ8Qd1JRWwQ=", 4733);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAE+VxWaN62BEPaZslUvM/0dsFxBUxXOM8R0kzarFALtn/x+ojnHdvCDVqhEaO3D/JcDQuj/swULsc=", 377);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEqJU6Hc8uzaiL2fNbCKlIjA+n4x7Aml4L8vtMNueu9MQZ/mQ+wYfj+gwM/218WxL3kBhQBhdH8fU=", 5171);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAErmwoCg0382iso00Hq3S/c1PeZItojNZCMxWr8ftRNuZqi+DuLey2KsXNJbe1QZAGvZDU4Cx++qM=", 9048);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEp7JKrtmk1agBH8Ls3mrL2a7DbgoNkh8agMKzU4TDSTzMV6ujJ7sgytyTpOK7nBP+1moEnstNp/c=", 473);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEQG6w5cbGCnthrTD3bbSDX4+yT/uAx/Km0D1iRcOQ1qI+TbSUm7LB5TQENZlf99DDlQ3HmJ40E8Y=", 7657);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAE9KfSBh2zueQ3yJlFhS/ndDXVhIagk3vw8OE3ZQWq+yN61Kgt3AQzq4ACKqI3XSObNVU77YFWKPc=", 1950);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAE33GUk0VMtkOufgZ9nYvS+jsekb1DFWaYVFZDOqcTjKkToJhc+8KLVDL2o2SdZwce0lU/j4Uk784=", 4225);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEkprW16/C+WtSF9xqm6+gpWa9WYoWLgn0NK/EzSOM9OK/X75uaT7Ug1KQ+COJxIU4CIHdH7C/TpI=", 3330);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEwOIvH0Vp5Ch54Pckrc9oR3IysRkEgQxI8KJxPrFzsUf4+DC1paLHcnY9tHFGblsqiKMnCsClAng=", 792);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEhAxssBnju8p3ajoHyvgd6tHrf1xioyK2dlsBrPXq4mjRjytONjd0+I9ZPr4NNnI/Atlql00yyvM=", 8149);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEaRc2bZLglfJeuHWskUBbBuJ3OGrzYslqLHzYKynppzR5H+ScKyj4MI6AsMv5iaaQkE9wearAjU8=", 5466);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEhCfSxwG2uNs4QTyjrkTEVzxmQf1XqPkHEGDtY+bHyqo63G2Y5C0KRSFykzN9C059TCdCvTf8gS8=", 7309);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAE4t0PtTgiuqUVoHMbGB3wDmiE0laeSZJQVT99adYMCZ4aL86IP99qycL/oUkTEFpewtsiZlRXEMo=", 7213);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEJ2VfzMVNw/Br4CgY/hYfpW6T7e8TbxH/7qbMvIdKtZoTgO0OD+cRyj0VXgbenaOSWPAuIX9fOvo=", 1236);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAE3lUhjzs7A++AHti5Kh+3bzMdh+ao3ONnDxadLCf3GGyRHlc/u9ylsT+eRs2QkC3G9jSaVJeZ27k=", 7845);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEf5eiWDBvclp9fewsZH8QRYmbfV5Fs7byg4UkHl8bVVK5AZ6VOuk20xiLG8dAOyix7f/novqEaTk=", 477);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEgb/5vm2rtBhMH2gKAKmbsZwslUHG9mLoSpIKG1o5yfwLrGeImOzNnUqpih/MlBMJ2GCUiBHVmHw=", 9430);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEKIi7uRhJFXqos5YOiPTR43ViioFj3/4LOx4zs7MLxLz/0TZowcLQjFmWOOfsS9jdt0V+7v6wvSY=", 3641);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEAjS2j4PzfaUdbckkM2w6CSgZO0m/H2XDwfvueU7QkMv0NqHCigTTGgudP3w1W1IVcauP8NU1Gzc=", 8356);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEDEY7wb+sNcTFGBHaAD7KRROU1tymQWEH57dafv/j9HoccMjePjenkJZzwqMfsJ88+0Y2nZurvRw=", 777);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAE2X4AZvzp2Oi/dW+/tPWOxuM7zcCd879Gyec2gcBWysVMKIeCI12bXNTRmuUuOQ/6WBO4rkoiVIo=", 3284);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAETDRycMDAMl46gIFHykW7bcmbu9pQ4xc5fcKwBWC534tCTxtcB+Iv12nf4sKlIQHiwKZ3GVMPk5w=", 170);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEtY6ykWtjGXU9Hre2kH8l7lZT8bvqix562V7zuSZ4fTuvtfAqK75JVvaJlWZDsFHgLJI1+W6s7j4=", 5368);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEPJNPmlKMH0cA6TVt3JcUqQW/YVvkZwvQHukGmr9ZKfYER/U+5A3yz2QxxQnxwe4u2ZlvJsFiHAQ=", 7202);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAECFM25s+Dq+AZJkmK7wiEI04NglAO674gKgc5rtLS6vr1PzMu/1Ig5EJFBF1lQufZV+2nzbCUsTo=", 2131);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEXqL10glD+rtJPL5wXgM0S4x84vuruV8O6bH4Jt2MJgoo1K48jx1QmCObJVvq0vnbj3NS8l9PECQ=", 9433);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAE1U/uvAuMV2QHTzzOdGUYc0jnPDIle6Fg5SAksXgMdiwZXjaPZBNm/Z/UxmhjlWxTH7k4QFu9AkE=", 3589);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEZ9C/X9T5WljQIaeDw2FsWFTsAQIx1NW8erSRVhTIW+7Lug+j9jMer6Ri905bRV5ZasBl4MJCfrQ=", 8328);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEb+t+noMT91+dVelWfuFGP0XLayPE8jYsMWk4S1txu4uCcpPIxWDZcEjPke5zAWMLlss8DwWmg4M=", 5129);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAE1wy+EiJqfdDE0NoUOUcZzxnUWeGfkfV+VrQLvc4iQxUgd98TPj32J//qfRf01wzTXSMbJ+SKJp4=", 5047);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEPByppduqhtwk+KBiz4ZCkggppFkbgg5Odiz/EEPbQ94CrWu799u4MzrOOGV6CjL19gr/tprUELE=", 4024);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEt8JZ13A12dAZNfuJzxQDHVhTeSZSMYhFrNDk0zqGmdgFlqIssvkQtdIo1R7Ud/HX2A3nPm2g9qw=", 1206);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEjCyYRTiPnRXig48cqN+3ynmzBKn8xEK6ExtcRFGrNH9aVJWF5BtdOJx29J8VAbN/d9PijqKIeH4=", 3132);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEmSsvSG6e41Z8oJAOs80zvaD3uo4pWiVA4E+ZbtrjE9VVBfdYf85klOM9mpFsjQsG0QQ+LBVfdpQ=", 6247);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEBjj+34ZQ8i6/wfPTLnWChIrI6Sbq5UD5qDic5ohX4D4QjtEqsSOlCa8OfVSjyRIo0JoE8iolkqM=", 3695);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEmFfrysh05lhYIkImYmD0l1GX75rl+WAUXTS89BfZCVPlpDarg8zWg2+DSI0B2uHtpJf3OUwm+dY=", 4082);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAElsAov4lTjg5djzYNdwr55A7kl3GBFCAW5WLzBOgPjntDpir/+4wPvc//W2H0u0+1/bHIh/1Dvmc=", 8481);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAERmTNHdOhdwb5z6gWkrnblkLDbPnDHBaPqxUA7GQv7lsGI0WL08r3kzTD/Di8WRCczg2iNg6zKt0=", 5859);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEQr8zBVKFVnoL+04nAIVtLCHAtnbcBK1Cflr8xVUtqp2JSjDRmgFIBekGhr6OTrHrsuqiIazV77s=", 1774);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEKPSH5fGlOh7l5u9ip+byTSOJQj0sYP+cruQYwcHEBcVxTVd9RnI26/jbaKRWSPQvhWZbVT6EJME=", 4709);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAE//nNdq1Ex77+GhQmupHNQYpiPV66Klt1ZCcSvw6raFGY7ClKIo1E6TtpDacwDIGTub9k8bLXrdU=", 3408);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEaRHAKI3gfctyh8Lp/SDl1+rXUSusXGSlxfFVlo156IILI5CWb7whUJFKPTlsY2m39YO0U50bFRE=", 5202);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAESSzY6RtPIys8+mBzIVqKeD3yXfKP3tNrkFydsfjP2soOh9SzDjuRmAhWFujzAZ/gw/ECnb45ZG4=", 4152);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEGkHJW03X4i//N8ZFSpkX42yBYvqdmqRCQIe3J62cVyFdTzw/MAkB5SkfBMKF9UlsBcSKWcWaD+M=", 3795);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEbmo2bRXvXIZbwmdzLvurL5HBpDAVTqGQy1BsqdDxayMj3bWY+xUVGpE4MxEnjf9Fcb6YJTI5FmY=", 7801);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAExQmZ2TkW+9CkAO2ZPUKN63FsOzdGX+Etm8uve2EcUxOadBzbEBRNuTt0I4I44w3QVgKp8AX76qA=", 5851);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEP4bHdZEfDzjrZnpTp4Z4JBiJXbxtl8192wqVwQw4NJpWcLTu3CxyPx8LxExmuqGPSqZl7okTTpg=", 9951);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEYLIR61ieukQ5ueBJRudE80ltIUEE4F+UFS1zZQVsEscoOEhd07pkEdrV97iSuml7holWs8VlyPY=", 7040);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEFZ6F/3+Nc6JzsbL94RZr/lHu9zU67wWK6ndMSFmFimzYLJShvDJuayZjkSxSESx2VqZSo3TAzc4=", 3925);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEepihr9k9Z9+F2MF32EoU7nR7t1tOp7Hgd4y7QB+gXbTif/UnVdlg0yxcWSVgLLCcQOOzgTELsV4=", 3535);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEfPb37J6HWP+TnVHyTLV5riwT+D0buUlWU4hSeZJHdajEw+Mj394aOdf4jgBEybqiSzAukd057l4=", 8067);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAE1V9IBjsa8mF/9dspeezoqUgNjNYtlFQx6E1WHjHDdr/7cOU5yiW1/KXF85CdLSbysYV04av/hb4=", 6600);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAELHP/VUL4ICWQBBxRnZaDe+HgVWFUPPQClSp89FlpSBjwlK4VFECJdygMIvc4Gy2D+RL5xrupoWA=", 8597);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAE70BOh5wG121JDfI7jeIiC4t27jhBCxjUnVqxwaxG6nRrlnfivcH4fZR02KShit5aiJ+k2RMB+RU=", 2623);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEUa+IuXSNLn4zFD/M5xWf1wSqZibTFW586qkvUi3wa+qwWRS9vuFKtPeacWUVCZVsbyPw26ZhNCQ=", 3732);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAESNKaVG0vasj5O2rYqpo036IqEh95W6nsPZxUp8VP/PbSYOuZN5sVAmDv/uuSPA7f2n3sAEaPojk=", 5024);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEB2Hs0vpnIv19CaFdVTpTRFG5uIsven86FVU7aINxh7aNO615zqNiMG2nleUN/MZ7IbiX69qzhJw=", 4540);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEn72isMV7RjIASGgMKHgOy+7InQzRzpqlh/V/+qUyWe4p/yMvfzdk9/BPOM6HFBAf/WoQEMQT3XY=", 6280);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEnno3fpbAfTitVP/pHbqBiyAavRkfzi8XBFiyCRBmt5jyS9Axe/o4XamAmx3Y0Ngv9q7rUSEpIWo=", 1831);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAE39H2OZngTWW9/Oup/GRF1pp0q+vCsy7RpBev92cVHcOP/ZeXt9EWsc4DTbaO6nNKtm3QkZUb0R8=", 9877);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEzDSnqTlVG2nb/eslY314cL7jUC08LNcYU+dDtZTcfEKxID6Da/AKPupZ8ppAPG3Co+h1Rqft+TQ=", 6421);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEDdfAGWhOcO5S46yAt3Zi3i2LP7xtgcCjSpqKvMec2NT+bbRgdoObPiZ/clQV7KYhkep9KthlPt0=", 4175);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEP3Ovj4mgRBQSw1MqLfz8q5DRN1K899j7gQ1+azXsuct0l0aVcu/KQ8uENkLqzclNITIrvQAnPyA=", 9202);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEfSa7b1g9aUiSFGLsx+UJXuK0YhGJtfzBcSQaUlUFA8evOqA9U+nu1PBJkg6mKodkWZ+CwdWXcC4=", 6498);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEKys6TeWzqLDEoDrc87vFdC40V5BsfzZooe0XPAqLbXcMr37DMXfvXYGLU7OYcrwgWUJvHVcwA9s=", 5651);
		   this.insertAdr("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEZq5SoYSZorB0dk5aJ6N404alDPJMMnm4Cdxls382Pg8xH+IAfIJtAppfFS45Qv7z4KUkRKQQCkM=", 597);
    }
}
