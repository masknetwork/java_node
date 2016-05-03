package wallet.kernel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import wallet.network.CPeer;
import wallet.network.packets.sync.CReqDataPacket;

public class CSync extends Thread 
{
	public CSync() 
	{
		
	}
	
	public void start()
	{
            try
            {
           UTILS.DB.executeUpdate("INSERT INTO sync(type, "
                                           + "status, "
                                           + "tstamp) "
                               + "VALUES('ID_GET_BLOCKCHAIN', "
                                     + "'ID_PENDING', "
                                     + "'"+UTILS.BASIC.tstamp()+"')");
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
	    try
	    {
                // Statement
                Statement s=UTILS.DB.getStatement();
                
		// Get the number of jobs
		ResultSet rs=s.executeQuery("SELECT * FROM sync");
			
		if (UTILS.DB.hasData(rs)==false)
		{
		    // Set online
                    UTILS.STATUS.setEngineStatus("ID_ONLINE");
                            
		    // Done
		    return;
		}
			
			
		  // Get the number of downloads
		  rs=s.executeQuery("SELECT COUNT(*) AS total "
				    + "FROM sync "
				   + "WHERE status='ID_DOWNLOADING'");
		  
                  // Next
                  rs.next();
		  
                  if (UTILS.NETWORK.peers.no>rs.getInt("total"))
		  {
		    for (int a=0; a<=UTILS.NETWORK.peers.no-1; a++)
	            {
		        CPeer peer=(CPeer)UTILS.NETWORK.peers.peers.get(a);
				  
			if (this.isBusy(peer.adr)==false)
			{
			    rs=s.executeQuery("SELECT * "
			  		        + "FROM sync "
			  		       + "WHERE type='ID_BLOCKS' "
                                                 + "AND status='ID_PENDING' "
                                            + "ORDER BY start ASC");
						  
					if (UTILS.DB.hasData(rs))
				        {
				  	    System.out.println("---------- Requesting blocks download ------------");
					    rs.next();
						  
						 // Send packet
					         CReqDataPacket packet=new CReqDataPacket("ID_BLOCKS", 
					    		                                   rs.getLong("start"), 
					    		                                   rs.getLong("end")); 
					        
                                                 UTILS.NETWORK.sendToPeer(peer.adr, packet);
					  
					         // Update db
					         UTILS.DB.executeUpdate("UPDATE sync "
					  		                   + "SET status='ID_DOWNLOADING', "
					  		                        + "peer='"+peer.adr+"' "
					  		                 + "WHERE type='ID_BLOCKS' "
					  		                   + "AND start='"+rs.getLong("start")+"'");
					}
                                        else
                                        {
                                            rs=s.executeQuery("SELECT * "
			  		                      + "FROM sync "
			  		                     + "WHERE type='ID_GET_BLOCKCHAIN' "
                                                               + "AND status='ID_PENDING'");
                                            
                                            if (UTILS.DB.hasData(rs))
                                            {
                                                System.out.println("---------- Requesting blockchain download ------------");
                                                
                                                rs.next();
                                                
                                                // Send packet
					         CReqDataPacket packet=new CReqDataPacket("ID_BLOCKCHAIN", 
					    		                                   rs.getLong("start"),
                                                                                           0); 
					         UTILS.NETWORK.sendToPeer(peer.adr, packet);
					  
					         // Update db
					         UTILS.DB.executeUpdate("UPDATE sync "
					  		                 + "SET status='ID_DOWNLOADING', "
					  		                     + "peer='"+peer.adr+"' "
					  		               + "WHERE type='ID_GET_BLOCKCHAIN'");
                                            }
                                        }
				  }
			    }
		   }
                  
                  rs.close();
                  s.close();
		}
		
		catch (SQLException ex)
		{
			UTILS.LOG.log("SQLException", ex.getMessage(), "CSync", 64);
		}
	}

}
