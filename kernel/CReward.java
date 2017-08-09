package wallet.kernel;

import java.sql.ResultSet;
import java.util.ArrayList;

public class CReward 
{
    double total_reward=0;
    
    public CReward()
    {
        
    }
    
    public void reward(long block) throws Exception
    {
       // Block ?
       if (block%1440!=0) 
           return; 
           
       // Blog posts
       payRewards("ID_POST", block);
       
       // Comments
       payRewards("ID_COM", block);
       
       // Data feeds
       payRewards("ID_FEED", block);
       
       // Assets
       payRewards("ID_ASSET", block);
       
       // Bets
       payRewards("ID_BET", block);
       
       // Margin markets
       payRewards("ID_MARGIN_MKT", block);    
    }
    
    // Rewards
    public void payRewards(String content_type, long block) throws Exception
    {
       // List
       ArrayList<String> list=new ArrayList();
            
       // Hash
       String hash=UTILS.BASIC.hash(String.valueOf(block));
       
       // Updates votes
       this.updateVotes(block);
       
       // Load distinct voted content
       ResultSet rs=UTILS.DB.executeQuery("SELECT DISTINCT(targetID), target_type "
                                          + "FROM votes "
                                         + "WHERE block>"+(block-1440)+" "
                                           + "AND target_type='"+content_type+"'");
       
       // Exist ?
       if (!UTILS.DB.hasData(rs))
           return;
       
       // Load in list
       while (rs.next())
           list.add(rs.getString("target_type")+","+rs.getString("targetID"));
       
       // Parse list
       for (int a=0; a<=list.size()-1; a++)
       {
           // Split
           String[] v=list.get(a).split(",");
           
           // Target type
           String target_type=v[0];
           
           // TargetID
           long targetID=Long.parseLong(v[1]);
                   
           // Target power
           double power=this.getContentPower(target_type, targetID, block);
           
           // Total votes
           rs=UTILS.DB.executeQuery("SELECT SUM(power) AS total "
                                    + "FROM votes "
                                   + "WHERE votes.target_type='"+content_type+"' "
                                     + "AND votes.type='ID_UP' "
                                     + "AND votes.block>"+(block-1440));
	    // Next
            rs.next();
        
            // Upvotes
	    double total_votes=rs.getDouble("total");
           
           // Percent
           double p=power*100/total_votes;
           
           // Network reward
           double reward_pool=this.getRewardPool(content_type);
           
           // Content reward
           double reward=p*reward_pool/100;
           
           // Blogs, comments or bets ?
           if (content_type.equals("ID_POST") || 
               content_type.equals("ID_COM") || 
               content_type.equals("ID_BET"))
           reward=UTILS.BASIC.round(reward/2, 4);
               
            // Reward votes
            if (reward>=0.0001)
            {
                // Payment address
                String adr=this.getTargetAdr(target_type, targetID);
                   
                // Make payment
                UTILS.ACC.newTransfer("default",
                                          adr, 
                                          reward, 
                                          "MSK", 
                                          "Content reward ("+target_type+", "+targetID+")", 
                                          "", 
                                          hash, 
                                          block);
                    
                // Add reward
                this.addReward("ID_CONTENT", adr, target_type, targetID, reward, block);
                    
                // Pay comments
                if (content_type.equals("ID_POST") || 
                    content_type.equals("ID_COM") || 
                    content_type.equals("ID_BET"))
                this.rewardVotes(target_type, targetID, reward, block);
            }
           
       }
       
       // Commit
       UTILS.ACC.clearTrans(hash, "ID_ALL", block);
   }
   
   public void rewardVotes(String target_type, 
                           long targetID, 
                           double amount,
                           long block) throws Exception
   {
       // List
       ArrayList<String> list=new ArrayList();
       
       // Hash
       String hash=UTILS.BASIC.hash(String.valueOf(block));
               
       // Load votes
       ResultSet rs=UTILS.DB.executeQuery("SELECT SUM(power) AS total "
                                          + "FROM votes "
                                         + "WHERE target_type='"+target_type+"' "
                                           + "AND targetID='"+targetID+"'");
       
       // Next
       rs.next();
       
       // Total
       double total_power=rs.getDouble("total");
       
       // Load all votes
       rs=UTILS.DB.executeQuery("SELECT * "
                                + "FROM votes "
                               + "WHERE target_type='"+target_type+"' "
                                 + "AND targetID='"+targetID+"'");
       
       while (rs.next())
           list.add(rs.getString("adr")+","+rs.getString("power"));
       
       // Parse list
       for (int a=0; a<=list.size()-1; a++)
       {
           // Split
           String[] v=list.get(a).split(",");
           
           // Target type
           String adr=v[0];
           
           // TargetID
           double power=Double.parseDouble(v[1]);
           
           // Percent
           double p=UTILS.BASIC.round(power*100/total_power, 2);
           
           // Percent
           if (p<0) p=0;
           if (p>100) p=100;
           
           // Pay ?
           if (p>0)
           {
               // Payment
               double pay=UTILS.BASIC.round(p*amount/100, 8);
               
               // Minimum ?
               if (p>=0.00000001)
               {
                    // Pay
                    UTILS.ACC.newTrans("default",
                                       adr, 
                                       pay, 
                                       "MSK", 
                                       "Content reward ("+target_type+", "+targetID+")", 
                                       "", 
                                       hash, 
                                       block);
               
                    // Add reward
                    this.addReward("ID_VOTERS", adr, target_type, targetID, pay, block);
               }
           }
       }
   }
   
   public String getTargetAdr(String target_type, long targetID) throws Exception
   {
      String query="";
      
      switch (target_type)
      {
          // Posts
	  case "ID_POST" : query="SELECT * "
                                 + "FROM tweets "
                                + "WHERE tweetID='"+targetID+"'"; 
                           break;
			
	  // Comments
	  case "ID_COM" : query="SELECT * "
                                 + "FROM comments "
                                + "WHERE comID='"+targetID+"'"; 
                           break;
			
	  // Feeds
	  case "ID_FEED" : query="SELECT * "
                                  + "FROM feeds "
                                 + "WHERE feedID='"+targetID+"'"; 
                           break;
			
	  // Assets
	  case "ID_ASSET" : query="SELECT * "
                                   + "FROM assets "
                                  + "WHERE assetID='"+targetID+"'"; 
                           break;
			
	  // Bets
	  case "ID_BET" : query="SELECT * "
                                 + "FROM feeds_bets "
                                + "WHERE betID='"+targetID+"'"; 
                           break;
			
	  // Margin
	  case "ID_MARGIN_MKT" : query="SELECT * "
                                       + "FROM feeds_spec_mkts "
                                      + "WHERE mktID='"+targetID+"'"; 
                           break;
	}
      
        // Load data
        ResultSet rs=UTILS.DB.executeQuery(query);
        
        // Next
        rs.next();
        
        // Return
        return rs.getString("adr");
   }
  
   
   public void addReward(String content, 
                         String adr, 
                         String target_type,
                         long targetID,
                         double amount, 
                         long block) throws Exception
   {
       UTILS.DB.executeUpdate("INSERT INTO rewards "
                                    + "SET adr='"+adr+"', "
                                        + "reward='"+content+"', "
                                        + "amount='"+UTILS.FORMAT_4.format(amount)+"', "
                                        + "target_type='"+target_type+"', "
                                        + "targetID='"+targetID+"', "
                                        + "block='"+block+"'");
       
       System.out.println(content+" - "+adr+","+target_type+","+targetID+","+amount+","+block);
       this.total_reward=this.total_reward+amount;
   }
   
   public double getRewardPool(String categ) throws Exception
   {
        // Reward
       double reward=0;
       
	// Undistributed coins
	double balance=UTILS.ACC.getBalance("default", "MSK");
		
        // Balance
	double unspent=balance/365/20; 
		
        // Reward
	switch (categ)
	{
	    // Posts
	    case "ID_POST" : reward=unspent*20/100; break;
			
	    // Comments
	    case "ID_COM" : reward=unspent*10/100; break;
			
	    // Feeds
	    case "ID_FEED" : reward=unspent*5/100; break;
			
	    // Assets
	    case "ID_ASSET" : reward=unspent*5/100; break;
			
	    // Bets
	    case "ID_BET" : reward=unspent*10/100; break;
			
	    // Margin
	    case "ID_MARGIN_MKT" : reward=unspent*10/100; break;
			
	    // Miners
	    case "ID_MINERS" : reward=unspent*40/100; break;
	}
		
	// Return
	return Math.round(reward);
    }
    
    public long getCreationBlock(String target_type, long targetID) throws Exception
    {
        String query="";
        
	switch (target_type)
	{
            // Posts
	    case "ID_POST" : query="SELECT * "
                                   + "FROM tweets "
                                  + "WHERE tweetID='"+targetID+"'"; 
	    break;
		  
	    // Comments		
	    case "ID_COM" : query="SELECT * "
                                  + "FROM comments "
                                 + "WHERE comID='"+targetID+"'"; 
	    break;
							
	    // Comments		
	    case "ID_BET" : query="SELECT * "
                                   + "FROM feeds_bets "
                                  + "WHERE betID='"+targetID+"'"; 
	    break;
	}
	
        // Execute
	ResultSet rs=UTILS.DB.executeQuery(query);
        
        // Next
	rs.next();
        
        // Return
        return rs.getLong("block");
    }
	
	public double getVotePower(String adr, 
                                   String target_type, 
                                   long targetID, 
                                   long actual_block) throws Exception 
	{
	    // Count votes
	    ResultSet rs=UTILS.DB.executeQuery("SELECT COUNT(*) AS total "
                                               + "FROM votes "
                                              + "WHERE adr='"+adr+"' "
                                                + "AND block>"+(actual_block-1440)); 
	    
            // Next
            rs.next();
	    
            // No
            long no=rs.getLong("total");
		
	    // Return power
	    double power=UTILS.BASIC.round(UTILS.ACC.getBalance(adr, "MSK")/no, 2); 
		
	    // Distance from posting
	    if (target_type.equals("ID_POST") || 
		target_type.equals("ID_COM") || 
		target_type.equals("ID_BET"))
	    {
		// Block
		long block=this.getCreationBlock(target_type, targetID);
			
		// Percent
		double p=0.07*(actual_block-block); 
                
                // Minimum 1%
                if (p>=99) p=99;
			
		// Power
		power=UTILS.BASIC.round(power-p*power/100, 2);
	    }
		
	    return power;
	}
        
        public void updateVotes(long block) throws Exception
        {
            // List
            ArrayList<String> list=new ArrayList();
            
            // Load votes
            ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                               + "FROM votes "
                                              + "WHERE block>"+(block-1440));
            
            while (rs.next())
               list.add(rs.getLong("ID")+","+rs.getString("adr")+","+rs.getString("target_type")+","+rs.getLong("targetID")+","+rs.getLong("block"));
            
            // Vote
            for (int a=0; a<=list.size()-1; a++)
            {
                String[] v=list.get(a).split(",");
                
                double power=this.getVotePower(v[1], 
                                               v[2], 
                                               Long.parseLong(v[3]), 
                                               Long.parseLong(v[4]));
                
                // Update
                UTILS.DB.executeUpdate("UPDATE votes "
                                        + "SET power='"+power+"' "
                                      + "WHERE ID='"+v[0]+"'");
            }
        }
        
        public double getContentPower(String target_type, long targetID, long block) throws Exception
        {
            // Upvotes
            System.out.println();
            ResultSet rs=UTILS.DB.executeQuery("SELECT SUM(power) AS total "
                                               + "FROM votes "
                                              + "WHERE target_type='"+target_type+"' "
                                                + "AND targetID='"+targetID+"' "
                                                + "AND type='ID_UP' "
                                                + "AND block>"+(block-1440));
            rs.next();
            double upvotes=UTILS.BASIC.round(rs.getDouble("total"), 2);
            
            // Downvotes
            rs=UTILS.DB.executeQuery("SELECT SUM(power) AS total "
                                     + "FROM votes "
                                    + "WHERE target_type='"+target_type+"' "
                                      + "AND targetID='"+targetID+"' "
                                      + "AND type='ID_DOWN' "
                                      + "AND block>"+(block-1440));
            rs.next();
            double downvotes=UTILS.BASIC.round(rs.getDouble("total"), 2);
            
            // Net
            double net=UTILS.BASIC.round(upvotes-downvotes, 2);
            
            // Return
            return net;
        }
}
