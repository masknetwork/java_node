package wallet.kernel;

import java.sql.*;
import java.sql.Statement;

import org.apache.commons.codec.digest.*;
import org.hsqldb.*;
import org.hsqldb.util.DatabaseManager;

import java.util.Date;
import java.util.Random;


public class CDB 
{
	// Connection
	public Connection con;
  
	
        
        
   public CDB()
   {
       
	   try 
	   {
	      if (UTILS.SETTINGS.db.equals("hsql"))
	      {
  	         Class.forName("org.hsqldb.jdbcDriver");
  	         con = DriverManager.getConnection("jdbc:hsqldb:file:"+UTILS.WRITEDIR+"wallet", "SA", "");
                
	      }
		   
	      if (UTILS.SETTINGS.db.equals("mysql"))
	      {
	         Class.forName("com.mysql.jdbc.Driver").newInstance();
	         con = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+UTILS.SETTINGS.db_name,
					         UTILS.SETTINGS.db_user,
					         UTILS.SETTINGS.db_pass);
                 
                 
              }
  	   } 
	   catch (SQLException ex) 
	   { 
		   UTILS.LOG.log("SQLException", ex.getMessage(), "CDB.java", 28);
                   System.exit(0);
	   }
	   catch (Exception ex) 
	   {
		   UTILS.LOG.log("Exception", ex.getMessage(), "CDB.java", 28); 
                   System.exit(0);
  	   }
     
	   UTILS.CONSOLE.write("DB initialized...");
   }
   
   // Check connection
   public boolean checkConnection()
   {
       try
       {
          if (this.con.isClosed())
          {
             if (UTILS.SETTINGS.db.equals("hsql"))
	      {
  	         Class.forName("org.hsqldb.jdbcDriver");
  	         con = DriverManager.getConnection("jdbc:hsqldb:file:"+UTILS.WRITEDIR+"wallet", "SA", "");
                
	      }
		   
	      if (UTILS.SETTINGS.db.equals("mysql"))
	      {
	         Class.forName("com.mysql.jdbc.Driver").newInstance();
	         con = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+UTILS.SETTINGS.db_name,
					         UTILS.SETTINGS.db_user,
					         UTILS.SETTINGS.db_pass);
                 
                 
              } 
          }
          else
          {
              return true;
          }
       }
       catch (SQLException ex) 
       { 
	    UTILS.LOG.log("SQLException", ex.getMessage(), "CDB.java", 28);
            System.exit(0);
       }
       catch (Exception ex) 
       {
            UTILS.LOG.log("Exception", ex.getMessage(), "CDB.java", 28); 
            System.exit(0);
       }
       
       return false;
   }
   
   // Get a connection
   public Statement getStatement()
   {
       Statement s=null;
       
       // Check connection
       this.checkConnection();
       
       try
       {
           s=con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
           return s;
       }
       catch (SQLException ex) 
       { 
	    UTILS.LOG.log("SQLException", ex.getMessage(), "CDB.java", 28);
            System.exit(0);
       }
       catch (Exception ex) 
       {
            UTILS.LOG.log("Exception", ex.getMessage(), "CDB.java", 28); 
            System.exit(0);
       }
       
       return s;
   }
   
    // Checks if result set contains any data 
    public boolean hasData(ResultSet rs)
    {
        if (rs==null) return false;
			
	try
	{
	    if (!rs.isBeforeFirst())
	       return false;
	    else
	       return true;
        }
	catch (SQLException ex) 
	{ 
	   UTILS.CONSOLE.write(ex.getMessage()); 
           return false; 
	}
        catch (Exception ex) 
	{
            UTILS.LOG.log("Exception", ex.getMessage(), "CDB.java", 28); 
            return false; 
  	}
    }
	
   public Statement executeUpdate(String query)
   {
       // Check connection
       this.checkConnection();
       
       try
       {
	      Statement s=con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
              PreparedStatement p=con.prepareStatement(query);
              p.execute();
              p.close();
              s.close();
              
              return null;     
	   }
	   catch (SQLException e) 
	   { 
		   UTILS.CONSOLE.write("SQL error - " + e.getMessage()+", "+query); 
	   }
           catch (Exception e) 
	   { 
		   UTILS.CONSOLE.write("SQL Exception - " + e.getMessage()+", "+query); 
	   }
	   
	   
	   return null;
   }
  
}