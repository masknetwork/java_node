package wallet.kernel;

public class CInsert 
{
   // Query
   private String query;
     

   public void CInsert(String table)
   {
       query="INSERT INTO "+table+" SET ";
   }
   
   public void add(String col, String val)
   {
     
   }
   
   public void execute()
   {
       UTILS.DB.executeUpdate(query);
   }
}
