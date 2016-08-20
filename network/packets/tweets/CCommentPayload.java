// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets.tweets;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.trans.*;
import wallet.network.packets.blocks.*;


public class CCommentPayload extends CPayload 
{
   // Parent type
   String parent_type;
   
   // Parent ID
   long parentID;
   
   // Message
   String mes;
   
   // Comment ID
   long comID;
   
   // Serial
   private static final long serialVersionUID = 100L;
   	
   public CCommentPayload(String adr, 
                           String parent_type,
		           long parentID,
                           String mes) throws Exception
   {
	  // Superclass
	   super(adr);
	   
	   // Parent type
           this.parent_type=parent_type;
   
           // Parent ID
           this.parentID=parentID;
           
           // Message
           this.mes=mes;
           
           // Row ID
           this.comID=UTILS.BASIC.getID();
   
	   // Hash
 	   hash=UTILS.BASIC.hash(this.getHash()+
 			         this.parent_type+
                                 this.parentID+
                                 this.comID+
                                 this.mes);
 	   
 	   // Sign
 	   this.sign();
   }
   
   public void check(CBlockPayload block) throws Exception
   {
      // Super class
      super.check(block);
             
      // Check Message
      if (!UTILS.BASIC.isDesc(mes, 1000))
	  throw new Exception("Invalid message - CTweetMesPayload.java");
      
      //  CommentID valid
      if (!UTILS.BASIC.validID(this.comID))
         throw new Exception("Invalid message ID - CTweetMesPayload.java");
      
      // Parent type
      if (!this.parent_type.equals("ID_POST") && 
          !this.parent_type.equals("ID_COM"))
      throw new Exception("Invalid parent type - CTweetMesPayload.java");
      
      // Already commented ?
      ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                         + "FROM comments "
                                        + "WHERE parent_type='"+this.parent_type+"' "
                                          + "AND parentID='"+this.parentID+"' "
                                          + "AND adr='"+this.target_adr+"'");
          
      if (UTILS.DB.hasData(rs))
         throw new Exception("Already commented - CTweetMesPayload.java");
      
      // Target valid
      if (!UTILS.BASIC.targetValid(this.parent_type, this.parentID))
          throw new Exception("Invalid target type - CTweetMesPayload.java");
            
      // Check Hash
      String h=UTILS.BASIC.hash(this.getHash()+
 			        this.parent_type+
                                this.parentID+
                                this.comID+
                                this.mes);
	  
      if (!h.equals(this.hash)) 
   	 throw new Exception("Invalid hash - CTweetMesPayload.java");
     
   }
   
   public void commit(CBlockPayload block) throws Exception
   {
      // Superclass
      super.commit(block);
      
       // Commit
       UTILS.DB.executeUpdate("INSERT INTO comments "
                                    + "SET adr='"+this.target_adr+"',"
                                        + "parent_type='"+this.parent_type+"', "
                                        + "parentID='"+this.parentID+"', "
                                        + "comID='"+this.comID+"', "
   		   		        + "mes='"+UTILS.BASIC.base64_encode(this.mes)+"', "
                                        + "expire='"+(this.block+43200)+"', "
   		   		        + "block='"+this.block+"'");
       
       // Post ?
       if (this.parent_type.equals("ID_POST"))
           UTILS.DB.executeUpdate("UPDATE tweets "
                                   + "SET comments=comments+1 "
                                 + "WHERE tweetID='"+this.parentID+"'");
       
    }
}
