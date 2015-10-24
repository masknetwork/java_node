package wallet.kernel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CRowHash 
{
	public CRowHash() 
	{
		// TODO Auto-generated constructor stub
	}
	
	public void update(String table, String col, String val)
	{
		try
		{
		   // ResultSet
		   ResultSet rs;
		   
                   // Finds row
                   Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		   rs=s.executeQuery("SELECT * FROM "+table+" WHERE "+col+"='"+val+"'");
		   rs.next();
		   
                   // Finds ID
                   long ID=rs.getLong("ID");
	            		  
		   // Update ID
                   this.hash(table, ID);
                   
                   // Null
                   if (s!=null) s.close();
	    } 
		catch (SQLException ex) 
		{  
			UTILS.LOG.log("SQLException", ex.getMessage(), "CRowHash.java", 42);
		}
	}
        
        public void update(String table, String col, String val, String col_2, String val_2)
	{
		try
		{
		   // ResultSet
		   ResultSet rs;
		   
                   // Finds row
                   Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		   rs=s.executeQuery("SELECT * FROM "+table+" WHERE "+col+"='"+val+"' AND "+col_2+"='"+val_2+"'");
		   
                   if (UTILS.DB.hasData(rs))
                   {
                     // Next
                     rs.next();
		   
                     // Finds ID
                     long ID=rs.getLong("ID");
	            		  
		     // Update ID
                     this.hash(table, ID);
                   }
                   
                   // Null
                   if (s!=null) s.close();
	    } 
		catch (SQLException ex) 
		{  
			UTILS.LOG.log("SQLException", ex.getMessage(), "CRowHash.java", 42);
		}
	}
        
        public void updateLastID(String table)
	{
		try
		{
		   // ResultSet
		   ResultSet rs;
		   
                   // Finds row
                   Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		   rs=s.executeQuery("SELECT last_insert_id()");
		   rs.next();
		   
                   // Finds ID
                   long ID=rs.getLong(1);
	            		  
		   // Update ID
                   this.hash(table, ID);
                   
                   // Null
                   if (s!=null) s.close();
	    } 
		catch (SQLException ex) 
		{  
			UTILS.LOG.log("SQLException", ex.getMessage(), "CRowHash.java", 42);
		}
	}
	
	public void hash(String table, long ID)
	{
	 	String hash="";
	          
		try
		{
                     Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                     ResultSet rs=s.executeQuery("SELECT * FROM "+table+" WHERE ID='"+ID+"'");
		     rs.next();
                
		    // ----------------------------------- ADR ----------------------------
		    if (table.equals("adr"))
		       hash=UTILS.BASIC.hash(rs.getString("adr")+
				                       String.valueOf(rs.getDouble("balance"))+
				                       String.valueOf(rs.getLong("block"))+
				                       String.valueOf(rs.getLong("stars_1"))+
				                       String.valueOf(rs.getLong("stars_2"))+
				                       String.valueOf(rs.getLong("stars_3"))+
				                       String.valueOf(rs.getLong("stars_4"))+
				                       String.valueOf(rs.getLong("stars_5"))+
                                                       String.valueOf(rs.getLong("total_received"))+
                                                       String.valueOf(rs.getLong("total_spent"))+
                                                       String.valueOf(rs.getLong("trans_no"))+
				                       String.valueOf(rs.getDouble("rating"))+
				                       String.valueOf(rs.getLong("last_interest")));
		    
		    // ----------------------------------- ADR OPTIONS----------------------------
		    if (table.equals("adr_options"))
		       hash=UTILS.BASIC.hash(rs.getString("adr")+
				                       rs.getString("op_type")+
				                       rs.getString("par_1")+
				                       rs.getString("par_2")+
				                       rs.getString("par_3")+
				                       rs.getString("par_4")+
				                       rs.getLong("expires")+
				                       rs.getLong("block"));
		    
		    // ----------------------------------- ADS----------------------------
		    if (table.equals("ads"))
		       hash=UTILS.BASIC.hash(rs.getString("adr")+
				             rs.getString("country")+
				             rs.getString("title")+
				             rs.getString("message")+
				             rs.getString("link")+
				             String.valueOf(rs.getLong("expires"))+
				             String.valueOf(rs.getLong("mkt_bid"))+
				             String.valueOf(rs.getLong("block")));
		    
		    // ----------------------------------- DOMAINS ----------------------------
		    if (table.equals("domains"))
		       hash=UTILS.BASIC.hash(rs.getString("adr")+
				                     rs.getString("domain")+
				                     String.valueOf(rs.getLong("expires"))+
				                     String.valueOf(rs.getDouble("sale_price"))+
				                     String.valueOf(rs.getDouble("market_bid"))+
				                     String.valueOf(rs.getDouble("market_expires"))+
				                     String.valueOf(rs.getLong("block")));
                    
                    // ----------------------------------- ASSETS ----------------------------
		    if (table.equals("assets"))
		       hash=UTILS.BASIC.hash(rs.getString("adr")+
				             rs.getString("symbol")+
                                             rs.getString("title")+
                                             rs.getString("description")+
                                             rs.getString("web_page")+
                                             rs.getString("pic")+
				             String.valueOf(rs.getLong("mkt_bid"))+
				             String.valueOf(rs.getDouble("mkt_days"))+
				             String.valueOf(rs.getDouble("qty"))+
                                             rs.getString("trans_fee_adr")+
				             String.valueOf(rs.getDouble("trans_fee"))+
				             String.valueOf(rs.getLong("block"))+
                                             rs.getString("can_increase"));
                    
                    // ----------------------------------- ASSETS OWNERS ----------------------------
		    if (table.equals("assets_owners"))
		       hash=UTILS.BASIC.hash(rs.getString("owner")+
				             rs.getString("symbol")+
				             String.valueOf(rs.getLong("qty"))+
				             String.valueOf(rs.getDouble("block")));
		  
		    // Update
		    UTILS.DB.executeUpdate("UPDATE "+table
		  		              + " SET rowhash='"+hash+"' "
		  		            + "WHERE ID='"+rs.getLong("ID")+"'");
							
			s.close();
		}
		catch (SQLException ex)
		{
			UTILS.LOG.log("SQLException", ex.getMessage(), "CRowHash.java", 65);
		}
	}
}
