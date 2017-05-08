// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.kernel;

import java.sql.*;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.commons.codec.digest.*;
import org.hsqldb.*;
import org.hsqldb.util.DatabaseManager;

import java.util.Date;
import java.util.Random;


public class CDB 
{
	// Connection
	public Connection con;
        
        // File location
        public String fileLoc;
        
	ArrayList<Statement> cons=new ArrayList<Statement>();
        int a=0;
        
        
   public CDB() throws Exception
   {
       
	   try 
	   {
	     con = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+UTILS.SETTINGS.db_name+"?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",
					         UTILS.SETTINGS.db_user,
					         UTILS.SETTINGS.db_pass);
                 
            
              
  	   } 
	   catch (SQLException ex) 
	   { 
		   System.out.println(ex.getMessage());
                   System.exit(0);
	   }
	   catch (Exception ex) 
	   {
		   System.out.println(ex.getMessage());
                   System.exit(0);
  	   }
     
           this.executeUpdate("SET GLOBAL TRANSACTION ISOLATION LEVEL SERIALIZABLE");
	   System.out.println("DB initialized...");
           
   }
   
   // Check connection
   public boolean checkConnection() throws Exception
   {
       try
       {
          if (this.con.isClosed())
          {
               Class.forName("com.mysql.jdbc.Driver").newInstance();
	       con = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+UTILS.SETTINGS.db_name,
					         UTILS.SETTINGS.db_user,
					         UTILS.SETTINGS.db_pass);
               
              // Abort
              if (con.isClosed()) System.exit(0);
          }
          else
          {
              return true;
          }
       }
       catch (SQLException ex) 
       { 
	    System.out.println(ex.getMessage());
            System.exit(0);
       }
       catch (Exception ex) 
       {
            System.out.println(ex.getMessage());
            System.exit(0);
       }
       
       return false;
   }
   
   
   public ResultSet executeQuery(String query) throws Exception
   {
       if (UTILS.SETTINGS.db_debug.equals("true"))
          System.out.println(query);
           
       // Check connection
       this.checkConnection();
       
      // Create statement
      Statement s=con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
      
      // Execute
      ResultSet rs=s.executeQuery(query);
      
      // Adds to pool
      cons.add(s);
      
      // Size limit ?
      if (UTILS.NETWORK!=null)
      {
         if (cons.size()>10000) 
         {
            for (int a=0; a<=100; a++)
            {
              // Close
              Statement st=cons.get(a);
              
              // Close
              if (st!=null) st.close();
              
              // Remove
              cons.remove(a);
            }
        }
      }
      
      return rs;
   }
   
   
    // Checks if result set contains any data 
    public boolean hasData(ResultSet rs) throws Exception
    {
        if (rs==null) return false;
			
	if (!rs.isBeforeFirst())
	       return false;
	    else
	       return true;
    }
	
   public void executeUpdate(String query) throws Exception
   {
      this.checkConnection();
      PreparedStatement p=con.prepareStatement(query);
      p.execute();
      p.close(); 
   } 
   
    public void executeUpdate(Connection c, String query) throws Exception
   {
       // Check connection
       this.checkConnection();
       
       try
       {
	      PreparedStatement p=c.prepareStatement(query);
              p.execute();
              p.close(); 
              
              
	}
	catch (Exception ex) 
	{ 
	      System.out.println(ex.getMessage());
              throw new Exception("Query error");
	}
   } 
   
   public void reset() throws Exception
   {
       this.executeUpdate("DROP database wallet");
       this.executeUpdate("CREATE database wallet");
       System.exit(0);
   }
   
   public void begin() throws Exception
   {
       this.executeUpdate("BEGIN");
   }
   
   public void commit() throws Exception
   {
       this.executeUpdate("COMMIT");
   }
   
   public void rollback() throws Exception
   {
       this.executeUpdate("ROLLBACK");
   }
   
   public void loadFileLoc() throws Exception
   {
       ResultSet rs=UTILS.DB.executeQuery("SHOW VARIABLES LIKE 'secure_file_priv'");
       
       if (!UTILS.DB.hasData(rs))
       {
           this.fileLoc="";
       }
       else
       {
           rs.next();
           this.fileLoc=rs.getString("Value");
       }
       
       // Print
       System.out.println("DB secure file set to "+this.fileLoc+".");
   }
  
}