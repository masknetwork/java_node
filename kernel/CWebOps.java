package wallet.kernel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Timer;
import java.util.TimerTask;
import wallet.network.CPeer;
import wallet.network.CResult;
import wallet.network.packets.adr.CAddSignPacket;
import wallet.network.packets.adr.CFrozeAdrPacket;
import wallet.network.packets.adr.COTPPacket;
import wallet.network.packets.adr.CProfilePacket;
import wallet.network.packets.adr.CReqDataPacket;
import wallet.network.packets.adr.CRestrictRecipientsPacket;
import wallet.network.packets.adr.CSealAdrPacket;
import wallet.network.packets.adr.CShareAdrPacket;
import wallet.network.packets.ads.CNewAdPacket;
import wallet.network.packets.assets.CIssueAssetPacket;
import wallet.network.packets.assets.CIssueAssetPayload;
import wallet.network.packets.domains.CBuyDomainPacket;
import wallet.network.packets.domains.CRenewDomainPacket;
import wallet.network.packets.domains.CRentDomainPacket;
import wallet.network.packets.domains.CSaleDomainPacket;
import wallet.network.packets.domains.CTransferDomainPacket;
import wallet.network.packets.domains.CUpdatePriceDomainPacket;
import wallet.network.packets.feeds.CFeedPacket;
import wallet.network.packets.feeds.CFeedPayload;
import wallet.network.packets.feeds.CNewFeedComponentPacket;
import wallet.network.packets.feeds.CNewFeedPacket;
import wallet.network.packets.markets.assets.automated.CNewAutoMarketPacket;
import wallet.network.packets.markets.assets.automated.CNewAutoMarketTradePacket;
import wallet.network.packets.markets.assets.regular.CCloseRegMarketPosPacket;
import wallet.network.packets.markets.assets.regular.CNewRegMarketOrderPacket;
import wallet.network.packets.markets.assets.regular.CNewRegMarketPacket;
import wallet.network.packets.markets.assets.regular.CNewRegMarketPosPacket;
import wallet.network.packets.markets.feeds.CNewFeedMarketPacket;
import wallet.network.packets.markets.feeds.CNewFeedMarketPayload;
import wallet.network.packets.mes.CMesPacket;
import wallet.network.packets.misc.CIncreaseMktBidPacket;
import wallet.network.packets.misc.CRemoveItemPacket;
import wallet.network.packets.trans.CEscrowedTransSignPacket;
import wallet.network.packets.trans.CMultisigTransSignPacket;
import wallet.network.packets.trans.CTransPacket;


public class CWebOps 
{
    // Timer
    Timer timer;
    
    // Task
    RemindTask task;
    
    public CWebOps()
    {
       // Timer
       timer = new Timer();
       task=new RemindTask();
       timer.schedule(task, 0, 1000); 
    }
     
     class RemindTask extends TimerTask 
     {  
       public CPeer parent;
               
       @Override
       public void run() 
       {  
           String op="";
        
        try
        {
           // Statement
           Statement s=UTILS.DB.getStatement();
           
           // Pending images
           ResultSet rs=s.executeQuery("SELECT * FROM imgs_stack");
           
           // Has data
           if (UTILS.DB.hasData(rs))
           {
              while (rs.next())
              {
                  CWebImageLoader img=new CWebImageLoader(rs.getString("url"));
                  img.start();
              }
           }
           
           rs=s.executeQuery("SELECT * "
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
                       rs=s.executeQuery("SELECT * "
                                         + "FROM my_adr "
                                        + "WHERE userID='"+userID+"'");
                       
                       // Has data
                       if (UTILS.DB.hasData(rs))
                       {
                           // Next
                           rs.next();
                           
                           // Address
                           String adr=rs.getString("adr");
                       
                           // Initial coins 
                           CTransPacket packet=new CTransPacket("ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEmzMuWmpif2JdeB1/UA7XQumglLj3o4/qF/CisxzJyXf7JoXXmGvBDJFbCSr8Si09zKtkfoA7jyU=", 
			                                       "ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAEmzMuWmpif2JdeB1/UA7XQumglLj3o4/qF/CisxzJyXf7JoXXmGvBDJFbCSr8Si09zKtkfoA7jyU=", 
			                                       adr, 
			                                       5, 
			                                       "MSK",
			                                       "Welcome to MaskNetwork",
                                                               "",
                                                               "",
                                                               "",
                                                               "",
                                                               "",
                                                               "",
                                                               "",
                                                               "",
                                                               0);
                        
                           UTILS.NETWORK.broadcast(packet);
                       }
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
                                                             rs.getString("par_7"),
                                                             rs.getString("par_8"),
                                                             rs.getString("par_9"),
                                                             rs.getString("par_10"),
                                                             rs.getString("par_11"),
                                                             rs.getString("par_12"),
                                                             rs.getString("par_13"),
                                                             rs.getLong("par_14"));
                        
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
                                                                UTILS.BASIC.base64_decode(rs.getString("par_7")), 
		                                                rs.getLong("days"));
                       UTILS.NETWORK.broadcast(packet);
                   }
                   
                   if (op.equals("ID_REQ_DATA")) 
                   {
                        CReqDataPacket packet=new CReqDataPacket(rs.getString("fee_adr"),
                                                                 rs.getString("target_adr"),
                                                                 rs.getString("par_1"),
                                                                 rs.getString("par_2"),
                                                                 rs.getInt("par_3"),
                                                                 rs.getInt("par_4"),
                                                                 rs.getString("par_5"),
                                                                 rs.getInt("par_6"),
                                                                 rs.getInt("par_7"),
                                                                 rs.getString("par_8"),
                                                                 rs.getInt("par_9"),
                                                                 rs.getInt("par_10"),
                                                                 rs.getString("par_11"),
                                                                 rs.getInt("par_12"),
                                                                 rs.getInt("par_13"),
                                                                 rs.getString("par_14"),
                                                                 rs.getInt("par_15"),
                                                                 rs.getInt("par_16"),
                                                                 rs.getInt("days"));
                        
                        UTILS.NETWORK.broadcast(packet);
                   }
                   
                   if (op.equals("ID_MULTISIG_SIGN")) 
                   {
                        CMultisigTransSignPacket packet=new CMultisigTransSignPacket(rs.getString("fee_adr"),
                                                                                     rs.getString("par_1"),
                                                                                     rs.getString("par_2"));
                        
                        UTILS.NETWORK.broadcast(packet);
                   }
                   
                   if (op.equals("ID_NEW_ADR")) 
                       UTILS.WALLET.newAddress(rs.getString("user"), 
                                               rs.getString("par_1"), 
                                               UTILS.BASIC.base64_decode(rs.getString("par_2")));
                   
                   if (op.equals("ID_SHARE_ADR"))
                   {
                       CShareAdrPacket packet=new CShareAdrPacket(rs.getString("fee_adr"), 
			                                          rs.getString("target_adr"),
			                                          rs.getString("par_1"));
                       UTILS.NETWORK.broadcast(packet);
                   }
                   
                    if (op.equals("ID_FROZE_ADR"))
                    {
                       CFrozeAdrPacket packet=new CFrozeAdrPacket(rs.getString("fee_adr"), 
			                                          rs.getString("target_adr"),
			                                          rs.getInt("days"));
                       UTILS.NETWORK.broadcast(packet);
                    }
                    
                    if (op.equals("ID_SEAL_ADR"))
                    {
                       CSealAdrPacket packet=new CSealAdrPacket(rs.getString("fee_adr"), 
			                                        rs.getString("target_adr"),
			                                        rs.getInt("days"));
                       UTILS.NETWORK.broadcast(packet);
                    }
                    
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
                    
                    if (op.equals("ID_RESTRICT"))
                    {
                       CRestrictRecipientsPacket packet=new CRestrictRecipientsPacket(rs.getString("fee_adr"), 
			                                                              rs.getString("target_adr"),
		                                                                      rs.getString("par_1"),
                                                                                      rs.getString("par_2"),
                                                                                      rs.getString("par_3"),
                                                                                      rs.getString("par_4"),
                                                                                      rs.getString("par_5"),
                                                                                      rs.getInt("days"));
                       UTILS.NETWORK.broadcast(packet);
                       
                    }
                    
                    if (op.equals("ID_MULTISIG"))
                    {
                       CAddSignPacket packet=new CAddSignPacket(rs.getString("fee_adr"), 
			                                        rs.getString("target_adr"),
		                                                rs.getString("par_1"),
                                                                rs.getString("par_2"),
                                                                rs.getString("par_3"),
                                                                rs.getString("par_4"),
                                                                rs.getString("par_5"),
                                                                rs.getInt("par_6"),
                                                                rs.getLong("days"));
                       UTILS.NETWORK.broadcast(packet);
                    }
                    
                   
                    
                    if (op.equals("ID_OTP"))
                    {
                        COTPPacket packet=new COTPPacket(rs.getString("fee_adr"), 
			                                 rs.getString("target_adr"),
		                                         rs.getString("par_1"),
                                                         rs.getString("par_2"),
		                                         rs.getLong("days")); 
                         UTILS.NETWORK.broadcast(packet);
                    }
                   
                   if (op.equals("ID_IMPORT_ADR"))
                   {
                       CAddress adr=new CAddress();
                       
                       adr.importAddress(rs.getString("user"), 
                                         rs.getString("par_1"),
                                         rs.getString("par_2"),
                                         rs.getString("par_3"));
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
                   
                   if (op.equals("ID_RENEW_DOMAIN"))
                   {
                      CRenewDomainPacket packet=new CRenewDomainPacket(rs.getString("fee_adr"),
		                                                       rs.getString("par_1"), 
                                                                       rs.getLong("days"));   
                       UTILS.NETWORK.broadcast(packet);
                   }
                   
                   if (op.equals("ID_TRANSFER_DOMAIN"))
                   {
                      CTransferDomainPacket packet=new CTransferDomainPacket(rs.getString("fee_adr"),
		                                                             rs.getString("par_1"), 
                                                                             rs.getString("par_2"));   
                       UTILS.NETWORK.broadcast(packet);
                   }
                   
                   if (op.equals("ID_UPDATE_DOMAIN_PRICE"))
                   {
                      CUpdatePriceDomainPacket packet=new CUpdatePriceDomainPacket(rs.getString("fee_adr"),
		                                                                   rs.getString("par_1"),
                                                                                   rs.getDouble("par_2"));
                       UTILS.NETWORK.broadcast(packet);
                   }
                   
                   if (op.equals("ID_SALE_DOMAIN"))
                   {
                      CSaleDomainPacket packet=new CSaleDomainPacket(rs.getString("fee_adr"),
		                                                     rs.getString("par_1"),
                                                                     rs.getDouble("par_2"),
                                                                     rs.getDouble("bid"),
                                                                     rs.getLong("days"));
                       UTILS.NETWORK.broadcast(packet);
                   }
                   
                   if (op.equals("ID_BUY_DOMAIN"))
                   {
                      CBuyDomainPacket packet=new CBuyDomainPacket(rs.getString("fee_adr"),
		                                                   rs.getString("target_adr"),
                                                                   rs.getString("par_1"),
                                                                   rs.getString("par_2"));
                                                                   
                       UTILS.NETWORK.broadcast(packet);
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
                   
                   // Remove item
                   if (op.equals("ID_DEL_ITEM"))
                   {
                      CRemoveItemPacket packet=new CRemoveItemPacket(rs.getString("fee_adr"), 
                                                                     rs.getString("target_adr"), 
                                                                     rs.getString("par_1"), 
                                                                     rs.getString("par_2"));
                      UTILS.NETWORK.broadcast(packet);
                   }  
                   
                   // Increase bid
                   if (op.equals("ID_INCREASE_BID"))
                   {
                      CIncreaseMktBidPacket packet=new CIncreaseMktBidPacket(rs.getString("fee_adr"), 
                                                                             rs.getString("target_adr"), 
                                                                             rs.getDouble("par_3"), 
                                                                             rs.getString("par_1"), 
                                                                             rs.getString("par_2"));
                                 
                      UTILS.NETWORK.broadcast(packet);
                   }  
                   
                    // Send message
                   if (op.equals("ID_SEND_MES"))
                   {
                      CMesPacket packet=new CMesPacket(rs.getString("fee_adr"), 
                                                       rs.getString("target_adr"),
		                                       rs.getString("par_1"), 
                                                       UTILS.BASIC.base64_decode(rs.getString("par_2")), 
                                                       UTILS.BASIC.base64_decode(rs.getString("par_3")));
                                 
                      UTILS.NETWORK.broadcast(packet);
                   }  
                   
                   if (op.equals("ID_ISSUE_ASSET"))
                   {
                        CIssueAssetPacket packet=new CIssueAssetPacket(rs.getString("fee_adr"), 
                                                                       rs.getString("target_adr"),
                                                                       rs.getString("par_5"),
                                                                       UTILS.BASIC.base64_decode(rs.getString("par_1")),
                                                                       UTILS.BASIC.base64_decode(rs.getString("par_2")),
                                                                       UTILS.BASIC.base64_decode(rs.getString("par_3")),
                                                                       UTILS.BASIC.base64_decode(rs.getString("par_4")),
                                                                       rs.getDouble("bid"),
                                                                       rs.getLong("days"),
                                                                       rs.getLong("par_6"),
                                                                       rs.getString("par_8"),
                                                                       rs.getDouble("par_7"),
                                                                       rs.getString("par_9"));    
                        
                        UTILS.NETWORK.broadcast(packet);
                   }
                   
                   // New feed
                   if (op.equals("ID_NEW_FEED"))
                   {
                      CNewFeedPacket packet=new CNewFeedPacket(rs.getString("fee_adr"), 
                                                               rs.getString("target_adr"),
                                                               UTILS.BASIC.base64_decode(rs.getString("par_1")),
                                                               UTILS.BASIC.base64_decode(rs.getString("par_2")),
                                                               UTILS.BASIC.base64_decode(rs.getString("par_3")),
                                                               rs.getString("par_5"),
                                                               rs.getDouble("bid"),
                                                               rs.getLong("days"));
                                 
                      UTILS.NETWORK.broadcast(packet);
                   }  
                   
                   // New feed branch
                   if (op.equals("ID_NEW_FEED_BRANCH"))
                   {
                      CNewFeedComponentPacket packet=new CNewFeedComponentPacket(rs.getString("fee_adr"), 
                                                                                 rs.getString("par_3"),
                                                                                 UTILS.BASIC.base64_decode(rs.getString("par_1")),
                                                                                 UTILS.BASIC.base64_decode(rs.getString("par_2")),
                                                                                 rs.getString("par_4"),
                                                                                 rs.getDouble("par_5"),
                                                                                 rs.getLong("days"));
                                 
                      UTILS.NETWORK.broadcast(packet);
                   }
                   
                   // Inject feed value
                   if (op.equals("ID_INJECT_VALUE"))
                   {
                       // Build payload
                       CFeedPayload payload=new CFeedPayload(rs.getString("target_adr"),
                                                             rs.getString("par_1"));
                       
                       // Add value
                       payload.addVal(rs.getString("par_2"), rs.getDouble("par_3"));
                      
                       // Sign
                       payload.doSeal();
       
                       // Build packet
                       CFeedPacket packet=new CFeedPacket(rs.getString("fee_adr"),
                                                          rs.getString("par_1"));
        
                       // Add to packet
                       packet.addPayload(payload);
                       
                       // Broadcast
                       UTILS.NETWORK.broadcast(packet);
                   }  
                   
                   // New regular asset market
                   if (op.equals("ID_NEW_REGULAR_ASSET_MARKET"))
                   {
                       CNewRegMarketPacket packet=new CNewRegMarketPacket(rs.getString("fee_adr"), 
                                                                          rs.getString("target_adr"),
                                                                          rs.getString("par_1"),
                                                                          rs.getString("par_2"), 
                                                                          rs.getString("par_3"),
                                                                          UTILS.BASIC.base64_decode(rs.getString("par_4")),
                                                                          UTILS.BASIC.base64_decode(rs.getString("par_5")),
                                                                          rs.getString("par_6"),
                                                                          rs.getDouble("par_7"),
                                                                          rs.getInt("par_8"),
                                                                          rs.getDouble("bid"), 
                                                                          rs.getLong("days"));
                       
                       UTILS.NETWORK.broadcast(packet);
                   }
                   
                   // New auto asset market
                   if (op.equals("ID_NEW_AUTO_ASSET_MARKET")) 
                   {
                       CNewAutoMarketPacket packet=new CNewAutoMarketPacket(rs.getString("fee_adr"), 
                                                                            rs.getString("target_adr"),
                                                                            rs.getString("par_1"),
                                                                            rs.getString("par_2"), 
                                                                            rs.getString("par_3"),
                                                                            UTILS.BASIC.base64_decode(rs.getString("par_4")),
                                                                            UTILS.BASIC.base64_decode(rs.getString("par_5")),
                                                                            rs.getString("par_6"),
                                                                            rs.getDouble("par_7"),
                                                                            rs.getDouble("par_8"),
                                                                            rs.getDouble("par_9"),
                                                                            rs.getInt("par_10"),
                                                                            rs.getDouble("bid"), 
                                                                            rs.getLong("days"));
                       
                       
                       UTILS.NETWORK.broadcast(packet);
                   }
                   
                   // New regular asset market
                   if (op.equals("ID_NEW_REGULAR_MKT_POS"))
                   {
                       CNewRegMarketPosPacket packet=new CNewRegMarketPosPacket(rs.getString("fee_adr"), 
                                                                                rs.getString("target_adr"),
                                                                                rs.getString("par_4"), 
                                                                                rs.getString("par_3"),
                                                                                rs.getDouble("par_1"),
                                                                                rs.getDouble("par_2"),
                                                                                rs.getLong("days"));
                       
                       UTILS.NETWORK.broadcast(packet);
                   }
                   
                   // New regular asset market
                   if (op.equals("ID_NEW_REGULAR_MKT_ORDER"))
                   {
                       CNewRegMarketOrderPacket packet=new CNewRegMarketOrderPacket(rs.getString("fee_adr"), 
                                                                                    rs.getString("target_adr"),
                                                                                    rs.getString("par_1"), 
                                                                                    rs.getDouble("par_2"));
                       
                       UTILS.NETWORK.broadcast(packet);
                   }
                   
                   // New regular asset market
                   if (op.equals("ID_CLOSE_REGULAR_MKT_POS"))
                   {
                       CCloseRegMarketPosPacket packet=new CCloseRegMarketPosPacket(rs.getString("fee_adr"), 
                                                                                    rs.getString("target_adr"),
                                                                                    rs.getString("par_1"));
                       
                       UTILS.NETWORK.broadcast(packet);
                   }
                   
                   // New feed market
                   if (op.equals("ID_NEW_REGULAR_FEED_MARKET"))
                   {
                       CNewFeedMarketPacket packet=new CNewFeedMarketPacket(rs.getString("fee_adr"), 
                                                                            rs.getString("target_adr"),
                                                                            rs.getString("par_1"),
                                                                            rs.getString("par_2"),
			                                                    rs.getString("par_3"),
			                                                    rs.getString("par_4"),
			                                                    rs.getString("par_5"),
			                                                    rs.getLong("par_6"), 
			                                                    rs.getString("par_7"), 
			                                                    UTILS.BASIC.base64_decode(rs.getString("par_8")), 
			                                                    UTILS.BASIC.base64_decode(rs.getString("par_9")),
			                                                    rs.getString("par_10"),
			                                                    rs.getDouble("par_11"),
			                                                    rs.getInt("par_12"),
                                                                            rs.getLong("par_13"),
                                                                            rs.getLong("par_14"),
			                                                    rs.getDouble("bid"),
			                                                    rs.getLong("days"));
                       
                       UTILS.NETWORK.broadcast(packet);
                   }
                   
                   // New asset automated market position
                   if (op.equals("ID_NEW_AUTO_ASSET_MKT_POS"))
                   {
                       CNewAutoMarketTradePacket packet=new CNewAutoMarketTradePacket(rs.getString("fee_adr"), 
                                                                                      rs.getString("target_adr"),
                                                                                      rs.getString("par_1"),
                                                                                      rs.getString("par_2"),
			                                                              rs.getDouble("par_3"));
                        
                        UTILS.NETWORK.broadcast(packet);
                   }
                }
           }
           
           // Close
           s.close();
           
           // Update web ops
           UTILS.DB.executeUpdate("UPDATE web_ops SET status='ID_EXECUTED'");
        }
        catch (SQLException ex)
        {
            UTILS.LOG.log("SQLexception", ex.getMessage(), "CWebOps.java", 635);
        }
        catch (Exception e) 
	{ 
		UTILS.LOG.log("Exception", e.getMessage(), "CWebOps.java", 639); 
        }
       }
     }
     
    
}
