// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.kernel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Timer;
import java.util.TimerTask;
import network.packets.market.escrowers.CNewEscrowerPacket;
import org.apache.commons.codec.binary.Hex;
import wallet.kernel.x34.SHA256;
import wallet.kernel.x34.SHA512;
import wallet.network.CPeer;
import wallet.network.CResult;
import wallet.network.packets.CPacket;
import wallet.network.packets.adr.CProfilePacket;
import wallet.network.packets.adr.CSealPacket;
import wallet.network.packets.ads.CNewAdPacket;
import wallet.network.packets.app.CDeployAppNetPacket;
import wallet.network.packets.app.CPublishAppPacket;
import wallet.network.packets.app.CRentAppPacket;
import wallet.network.packets.app.CUpdateAppPacket;
import wallet.network.packets.app.CUpdateSettingsPacket;
import wallet.network.packets.assets.CIssueAssetPacket;
import wallet.network.packets.domains.CBuyDomainPacket;
import wallet.network.packets.domains.CRentDomainPacket;
import wallet.network.packets.domains.CSaleDomainPacket;
import wallet.network.packets.domains.CTransferDomainPacket;
import wallet.network.packets.mes.CMesPacket;
import wallet.network.packets.misc.CDelVotePacket;
import wallet.network.packets.misc.CRenewPacket;
import wallet.network.packets.trans.CEscrowedTransSignPacket;
import wallet.network.packets.trans.CTransPacket;
import wallet.network.packets.tweets.CFollowPacket;
import wallet.network.packets.tweets.CVotePacket;
import wallet.network.packets.tweets.CNewTweetPacket;
import wallet.network.packets.tweets.CCommentPacket;
import wallet.network.packets.tweets.CUnfollowPacket;


public class CWebOps 
{
    // Timer
    Timer timer;
    
   
    public CWebOps()
    {
    
    }
     
     public void loadWebOps() throws Exception
     { 
         String op;
         
          
       try
        {
           // Broadcast
           //if (UTILS.BASIC.tstamp()%60==0) UTILS.CBLOCK.broadcast();
            
           // Statement
           
           
           
           ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                         + "FROM web_ops "
                                        + "WHERE status='ID_PENDING'");
      
           if (UTILS.DB.hasData(rs))
           {
               while (rs.next())
               {
                   op=rs.getString("op");
                   
                   // New account is created.
                   if (op.equals("ID_NEW_ACCOUNT"))
                   {
                       UTILS.WALLET.newAddress(rs.getString("user"), 
                                               rs.getString("par_1"), 
                                               UTILS.BASIC.base64_decode(rs.getString("par_2")));
                       
                       // UserID
                       long userID=rs.getLong("par_3");
                       
                       
                       // Load newly created adddress data
                       ResultSet rsn=UTILS.DB.executeQuery("SELECT * "
                                                           + "FROM my_adr "
                                                          + "WHERE userID='"+userID+"'");
                       
                       // Has data
                       if (UTILS.DB.hasData(rsn))
                       {
                           // Next
                           rsn.next();
                           
                           // Address
                           String adr=rsn.getString("adr");
                       
                           // Initial coins 
                           if (!UTILS.STATUS.new_acc_reward_adr.equals("") && UTILS.STATUS.new_acc_reward>0.0001)
                           {
                               CTransPacket packet=new CTransPacket(UTILS.STATUS.new_acc_reward_adr, 
			                                            UTILS.STATUS.new_acc_reward_adr,  
			                                            adr, 
			                                            UTILS.STATUS.new_acc_reward, 
			                                            "MSK",
			                                            "Welcome to MaskNetwork",
                                                                    "",
                                                                    rs.getLong("ID"));
                        
                               UTILS.NETWORK.broadcast(packet);
                           }
                       }
                       
                       // Close
                       rsn.close();
                  }
                   
                   // Add peer
                   if (op.equals("ID_ADD_PEER")) 
                       UTILS.NETWORK.connectTo(rs.getString("par_1"), rs.getInt("par_2")); 
                   
                   // Remove peer
                   if (op.equals("ID_REMOVE_PEER")) 
                       UTILS.NETWORK.removePeer(rs.getString("par_1"));
                   
                   // Broadcast the actual block
                   if (op.equals("ID_SEND_CBLOCK")) 
                       UTILS.CBLOCK.broadcast();
                   
                   // New tweet
                   if (op.equals("ID_NEW_TWEET"))
                   {
                        CNewTweetPacket packet=new CNewTweetPacket(rs.getString("fee_adr"), 
                                                                   rs.getString("target_adr"),
                                                                   UTILS.BASIC.base64_decode(rs.getString("par_1")), 
		                                                   UTILS.BASIC.base64_decode(rs.getString("par_2")), 
                                                                   rs.getLong("par_3"),
		                                                   UTILS.BASIC.base64_decode(rs.getString("par_4")),
                                                                   rs.getLong("days"));
                       
                        UTILS.NETWORK.broadcast(packet);
                   }
                   
                   
                   // New tweet
                   if (op.equals("ID_NEW_TWEET_COMMENT"))
                   {
                       CCommentPacket packet=new  CCommentPacket(rs.getString("fee_adr"),
                                                                 rs.getString("target_adr"),
		                                                 rs.getString("par_1"),
                                                                 rs.getLong("par_2"),
                                                                 UTILS.BASIC.base64_decode(rs.getString("par_3")));
                       
                       UTILS.NETWORK.broadcast(packet);
                   }
                   
                   
                    // Update comment status
                   if (op.equals("ID_VOTE"))
                   {
                       CVotePacket packet=new CVotePacket(rs.getString("fee_adr"),
                                                          rs.getString("target_adr"),
		                                          rs.getString("par_1"), 
                                                          rs.getLong("par_2"), 
                                                          rs.getString("par_3"));
                       
                       UTILS.NETWORK.broadcast(packet);
                   }
                   
                   // Follow
                   if (op.equals("ID_FOLLOW"))
                   {
                       CFollowPacket packet=new CFollowPacket(rs.getString("fee_adr"),
                                                              rs.getString("target_adr"),
		                                              rs.getString("par_1"),
                                                              rs.getLong("par_2"));
                       
                       UTILS.NETWORK.broadcast(packet);
                   }
                   
                   // Unfollow
                   if (op.equals("ID_UNFOLLOW"))
                   {
                       CUnfollowPacket packet=new CUnfollowPacket(rs.getString("fee_adr"),
                                                                  rs.getString("target_adr"),
		                                                  rs.getString("par_1"));
                       
                       UTILS.NETWORK.broadcast(packet);
                   }
                   
                   // New transaction
                   if (op.equals("ID_TRANSACTION")) 
                   {
                        CTransPacket packet=new CTransPacket(rs.getString("fee_adr"),
                                                             rs.getString("par_1"),
                                                             rs.getString("par_2"),
                                                             rs.getDouble("par_3"),
                                                             rs.getString("par_4"),
                                                             rs.getString("par_5"),
                                                             rs.getString("par_6"),
                                                             rs.getLong("ID"));
                        
                        UTILS.NETWORK.broadcast(packet);
                   }
                   
                   if (op.equals("ID_UPDATE_PROFILE"))
                   {
                       CProfilePacket packet=new CProfilePacket(rs.getString("fee_adr"),
                                                                rs.getString("target_adr"),
		                                                UTILS.BASIC.base64_decode(rs.getString("par_1")), 
		                                                UTILS.BASIC.base64_decode(rs.getString("par_2")),
                                                                UTILS.BASIC.base64_decode(rs.getString("par_3")), 
                                                                UTILS.BASIC.base64_decode(rs.getString("par_4")),
                                                                UTILS.BASIC.base64_decode(rs.getString("par_5")), 
                                                                UTILS.BASIC.base64_decode(rs.getString("par_6")), 
                                                                rs.getLong("days"));
                       
                     
                       UTILS.NETWORK.broadcast(packet);
                   }
                   
                  
                   
                   if (op.equals("ID_NEW_ADR")) 
                       UTILS.WALLET.newAddress(rs.getString("user"), 
                                               rs.getString("par_1"), 
                                               UTILS.BASIC.base64_decode(rs.getString("par_2")));
                    
                    if (op.equals("ID_REVEAL_PK"))
                    {
                        // Load address
                        CAddress adr=UTILS.WALLET.getAddress(rs.getString("target_adr"));
                        
                        // ID
                        long ID=rs.getLong("ID");
                                
                        // Write PK
                        UTILS.DB.executeUpdate("UPDATE web_ops "
                                                + "SET resp_1='"+adr.getPublic()+"', "
                                                    + "resp_2='"+adr.getPrivate()+"' "
                                              + "WHERE ID='"+ID+"'");
                    }
                    
                    // Escrowed sign
                    if (op.equals("ID_ESCROWED_SIGN"))
                    {
                        CEscrowedTransSignPacket packet=new CEscrowedTransSignPacket(rs.getString("fee_adr"),
                                                                                     rs.getString("par_1"),
                                                                                     rs.getString("par_2"),
                                                                                     rs.getString("par_3"));
                        
                        UTILS.NETWORK.broadcast(packet);
                    }
                    
                    
                   if (op.equals("ID_IMPORT_ADR"))
                   {
                       CAddress adr=new CAddress();
                       
                       adr.importAddress(rs.getString("user"), 
                                         rs.getString("par_1"),
                                         rs.getString("par_2"),
                                         rs.getString("par_3"));
                       
                       UTILS.CBLOCK.setSigner();
                   }
                   
                   if (op.equals("ID_RENEW"))
                   {
                       CRenewPacket packet=new CRenewPacket(rs.getString("fee_adr"), 
                                                            rs.getString("target_adr"), 
                                                            rs.getString("par_1"), 
                                                            rs.getLong("days"),
                                                            rs.getString("par_2"));
                       UTILS.NETWORK.broadcast(packet);
                   }
                   
                   if (op.equals("ID_SHUTDOWN"))
                   {
                       UTILS.DB.executeUpdate("UPDATE web_ops SET status='ID_EXECUTED' WHERE ID='"+rs.getLong("ID")+"'"); 
                       System.exit(0); 
                   }
                   
                   if (op.equals("ID_NEW_DOMAIN"))
                   {
                      CRentDomainPacket packet=new CRentDomainPacket(rs.getString("fee_adr"), 
                                                                     rs.getString("target_adr"), 
                                                                     rs.getString("par_1"), 
                                                                     rs.getLong("days"));     
                       UTILS.NETWORK.broadcast(packet);
                   }
                   
                    
                   if (op.equals("ID_TRANSFER_DOMAIN"))
                   {
                      CTransferDomainPacket packet=new CTransferDomainPacket(rs.getString("fee_adr"),
		                                                             rs.getString("target_adr"), 
                                                                             rs.getString("par_1"),
                                                                             rs.getString("par_2"));   
                       UTILS.NETWORK.broadcast(packet);
                   }
                   
                   if (op.equals("ID_SALE_DOMAIN"))
                   {
                      CSaleDomainPacket packet=new CSaleDomainPacket(rs.getString("fee_adr"),
                                                                     rs.getString("target_adr"),
		                                                     rs.getString("par_1"),
                                                                     rs.getDouble("par_2"));
                       UTILS.NETWORK.broadcast(packet);
                   }
                   
                   if (op.equals("ID_BUY_DOMAIN"))
                   {
                      CBuyDomainPacket packet=new CBuyDomainPacket(rs.getString("fee_adr"),
		                                                   rs.getString("target_adr"),
                                                                   rs.getString("par_1"));
                                                                   
                       UTILS.NETWORK.broadcast(packet);
                   }
                   
                   if (op.equals("ID_SEAL_ADR"))
                   {
                      CSealPacket packet=new  CSealPacket(rs.getString("fee_adr"),
		                                          rs.getString("target_adr"),
                                                          rs.getLong("days"));
                                                                   
                       UTILS.NETWORK.broadcast(packet);
                   }
                   
                   if (op.equals("ID_START_MINING")) 
                   { 
                       UTILS.CBLOCK.startMiners(rs.getInt("par_1")); 
                   }
                   
                   if (op.equals("ID_STOP_MINING")) 
                   { 
                       UTILS.CBLOCK.stopMiners();
                   }
                   
                   if (op.equals("ID_NEW_AD"))
                   {
                      CNewAdPacket packet=new CNewAdPacket(rs.getString("fee_adr"), 
		                                           rs.getString("fee_adr"), 
		                                           rs.getString("par_4"), 
		                                           rs.getLong("days"), 
		                                           rs.getDouble("bid"), 
		                                           UTILS.BASIC.base64_decode(rs.getString("par_1")), 
		                                           UTILS.BASIC.base64_decode(rs.getString("par_2")), 
		                                           UTILS.BASIC.base64_decode(rs.getString("par_3")));
                                                
                       UTILS.NETWORK.broadcast(packet);
                   }
                   
                   
                    // Send message
                   if (op.equals("ID_SEND_MES"))
                   {
                      CMesPacket packet=new CMesPacket(rs.getString("fee_adr"), 
                                                       rs.getString("target_adr"),
		                                       rs.getString("par_1"), 
                                                       UTILS.BASIC.base64_decode(rs.getString("par_2")), 
                                                       UTILS.BASIC.base64_decode(rs.getString("par_3")),
                                                       rs.getString("packet_sign"),
                                                       rs.getString("payload_sign"));
                                 
                      UTILS.NETWORK.broadcast(packet);
                   }  
                   
                   
                   
                   // New regular asset market
                   if (op.equals("ID_VOTE_DELEGATE"))
                   {
                       CDelVotePacket packet=new CDelVotePacket(rs.getString("fee_adr"), 
                                                                rs.getString("target_adr"),
                                                                rs.getString("par_1"),
                                                                rs.getString("par_2"));
                      
                       UTILS.NETWORK.broadcast(packet);
                   }
                   
                   
                   
                   
                   
                   
                   // Install to network
                   if (op.equals("ID_DEPLOY_APP_NET"))
                   {
                       CDeployAppNetPacket packet=new CDeployAppNetPacket(rs.getString("fee_adr"), 
                                                                          rs.getString("target_adr"),
                                                                          UTILS.BASIC.base64_decode(rs.getString("par_1")),
                                                                          UTILS.BASIC.base64_decode(rs.getString("par_2")),
                                                                          UTILS.BASIC.base64_decode(rs.getString("par_3")),
                                                                          UTILS.BASIC.base64_decode(rs.getString("par_4")),
                                                                          UTILS.BASIC.base64_decode(rs.getString("par_5")),
                                                                          rs.getLong("par_6"),
                                                                          rs.getLong("days"));
                       UTILS.NETWORK.broadcast(packet);
                   }    
                 
                   // Publish application
                   if (op.equals("ID_PUBLISH_APP"))
                   {
                       CPublishAppPacket packet=new CPublishAppPacket(rs.getString("fee_adr"), 
                                                                      rs.getString("target_adr"),
                                                                      rs.getString("par_1"), 
                                                                      rs.getLong("par_2"),
                                                                      rs.getString("par_3"),
                                                                      UTILS.BASIC.base64_decode(rs.getString("par_4")),
                                                                      UTILS.BASIC.base64_decode(rs.getString("par_5")),
                                                                      rs.getString("par_6"),
                                                                      UTILS.BASIC.base64_decode(rs.getString("par_7")),
                                                                      UTILS.BASIC.base64_decode(rs.getString("par_8")),
                                                                      UTILS.BASIC.base64_decode(rs.getString("par_9")),
                                                                      rs.getDouble("par_10"));
                       UTILS.NETWORK.broadcast(packet);
                   }   
                   
                   // Rent
                   if (op.equals("ID_RENT_APP"))
                   {
                       CRentAppPacket packet=new CRentAppPacket(rs.getString("fee_adr"), 
                                                                rs.getString("target_adr"),
                                                                rs.getLong("par_1"), 
                                                                rs.getLong("days"));
                       UTILS.NETWORK.broadcast(packet);
                   }  
                   
                   
                          
                   // Update app
                   if (op.equals("ID_UPDATE_APP"))
                   {
                       CUpdateAppPacket packet=new CUpdateAppPacket(rs.getString("fee_adr"), 
                                                                    rs.getString("target_adr"),
                                                                    rs.getLong("par_1"),
                                                                    rs.getString("par_2"),
                                                                    rs.getLong("days"));
                       UTILS.NETWORK.broadcast(packet);
                   }  
                   
                  
                   // Update settings
                   if (op.equals("ID_UPDATE_APP_SETTINGS"))
                   {
                       CUpdateSettingsPacket packet=new  CUpdateSettingsPacket(rs.getString("fee_adr"), 
                                                                               rs.getString("target_adr"),
                                                                               rs.getLong("par_1"),
                                                                               rs.getString("par_2"),
                                                                  rs.getString("packet_sign"),
                                                                  rs.getString("payload_sign"));
                       UTILS.NETWORK.broadcast(packet);
                   }  
                   
                   // Raaw packet
                   if (op.equals("ID_RAW_PACKET"))
                   {
                       // Load data
                       String data=rs.getString("par_1");
                       byte[] decoded=UTILS.BASIC.base64_decode_data(data);
                       CPacket packet=(CPacket) UTILS.SERIAL.deserialize(decoded);
                       
                       // Broadcast
                       UTILS.NETWORK.broadcast(packet);
                   }  
                   
                   if (op.equals("ID_ISSUE_ASSET"))
                   {
                     CIssueAssetPacket packet=new CIssueAssetPacket(rs.getString("fee_adr"), 
                                                                       rs.getString("target_adr"),
                                                                       rs.getString("par_7"),
                                                                       UTILS.BASIC.base64_decode(rs.getString("par_1")),
                                                                       UTILS.BASIC.base64_decode(rs.getString("par_2")),
                                                                       UTILS.BASIC.base64_decode(rs.getString("par_3")),
                                                                       UTILS.BASIC.base64_decode(rs.getString("par_4")),
                                                                       UTILS.BASIC.base64_decode(rs.getString("par_5")),
                                                                       UTILS.BASIC.base64_decode(rs.getString("par_6")),
                                                                       rs.getLong("days"),
                                                                       rs.getLong("par_8"),
                                                                       rs.getString("par_10"),
                                                                       rs.getDouble("par_9"));    
                        
                        UTILS.NETWORK.broadcast(packet);
                   }
                   
                   
                }
           }
           
        
           
           
        }
        catch (SQLException ex)
        {
            UTILS.LOG.log("SQLexception", ex.getMessage(), "CWebOps.java", 635);
        }
        catch (Exception e) 
	{ 
		UTILS.LOG.log("Exception", e.getMessage(), "CWebOps.java", 639); 
        }
        finally 
        {
            // Update web ops
            try 
            { 
                UTILS.DB.executeUpdate("UPDATE web_ops SET status='ID_EXECUTED'"); 
            } 
            catch (Exception ex) 
            {
                System.out.println(ex.getMessage());
            }
        }
        
       
     }
     
    
}
