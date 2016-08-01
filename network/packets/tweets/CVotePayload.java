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


public class CVotePayload extends CPayload 
{
   // Target type
   String target_type;
   
   // Tweet ID
   long targetID;
   
   // Type
   String type;
   
   // Serial
   private static final long serialVersionUID = 100L;
   	
   public CVotePayload(String adr, 
                       String target_type,
                       long targetID,
                       String type) throws Exception
   {
	  // Superclass
	   super(adr);
	   
           // Target type
           this.target_type=target_type;
           
	   // Target ID
           this.targetID=targetID;
           
           // Type
           this.type=type;
           
           // Hash
 	   hash=UTILS.BASIC.hash(this.getHash()+
 			         this.target_type+
                                 this.targetID+
                                 this.type);
 	   
           // Sign
 	   this.sign();
   }
   
   public void check(CBlockPayload block) throws Exception
   {
       // Super class
       super.check(block);
       
       // Balance
       if (UTILS.ACC.getBalance(this.target_adr, "MSK", block)<1)
          throw new Exception("Invalid balance - CVotePayload.java");
       
       // Type
       if (!this.type.equals("ID_UP") && 
           !this.type.equals("ID_DOWN"))
       throw new Exception("Invalid type - CVotePayload.java");
       
       // Target type
       if (!this.target_type.equals("ID_POST") && 
           !this.target_type.equals("ID_COM"))
       throw new Exception("Invalid target type - CVotePayload.java");
       
        // Already liked ?
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                           + "FROM votes "
                                          + "WHERE adr='"+this.target_adr+"' "
                                            + "AND target_type='"+this.target_type+"' "
                                            + "AND targetID='"+this.targetID+"'");
            
        if (UTILS.DB.hasData(rs))
           throw new Exception("Already voted  - CVotePayload.java");
        
       // Load  data
       switch (this.target_type)
       {
           case "ID_POST" : rs=UTILS.DB.executeQuery("SELECT * "
                                                     + "FROM tweets "
                                                    + "WHERE tweetID='"+this.targetID+"'");
                            break;    
                            
           case "ID_COM" : rs=UTILS.DB.executeQuery("SELECT * "
                                                        + "FROM comments "
                                                       + "WHERE comID='"+this.targetID+"'");
                                break;    
       }
             
       // Like tweet exist ?
       if (!UTILS.DB.hasData(rs))
           throw new Exception("Invalid tweetID  - CVotePayload.java");
       
       // No more than 100 votes in 24 hours
       rs=UTILS.DB.executeQuery("SELECT COUNT(*) AS total "
                                + "FROM votes "
                               + "WHERE adr='"+this.target_adr+"' "
                                 + "AND block>"+(this.block-1440));
       rs.next();
       
       // Total
       if (rs.getLong("total")>=100)
           throw new Exception("Maximum votes reached");
       
        // Check Hash
        String h=UTILS.BASIC.hash(this.getHash()+
 			          this.target_type+
                                  this.targetID+
                                  this.type);
	  
   	if (!h.equals(this.hash)) 
   	    throw new Exception("Invalid hash  - CVotePayload.java");
   }
   
   public void commit(CBlockPayload block) throws Exception
   {
        // Superclass
        super.commit(block);
        
        // Like
        UTILS.DB.executeUpdate("INSERT INTO votes "
                                       + "SET adr='"+this.target_adr+"', "
                                           + "target_type='"+this.target_type+"', "
                                           + "targetID='"+this.targetID+"', "
                                           + "type='"+this.type+"', "
                                           + "block='"+this.block+"'");
    }
}
