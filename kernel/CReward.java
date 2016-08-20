package wallet.kernel;

import java.sql.ResultSet;

public class CReward 
{
    public CReward()
    {
        
    }
    
    // Rewards
   public void payRewards(long block) throws Exception
   {
       // Daily ?
       if (block%1440!=0) return;
       
       // Hash
       String hash=UTILS.BASIC.hash(String.valueOf(block));
       
       // Pay content
       double paid_content=this.payContent(block);
       
       // Pay commenters
       double paid_com=this.payComments(block);
       
       // Pay voters
       double paid_voters=this.payVoters(block);
       
       // Pay app
       double paid_app=this.payApp(block);
       
       // Total paid
       double total=UTILS.BASIC.round(paid_content+paid_com+paid_voters+paid_app, 4);
       
       // Debit default address
       UTILS.ACC.newTrans("default", 
                          "", 
                          -total, 
                          true,
                          "MSK", 
                          "Content reward", 
                          "", 
                          hash, 
                          block,
                          null,
                          0);
       
       // Commit
       UTILS.ACC.clearTrans(hash, "ID_ALL", block);
   }
   
   public double payContent(long block) throws Exception
   {
       // Total paid
       double total_paid=0;
       
       // Hash
       String hash=UTILS.BASIC.hash(String.valueOf(block));
       
       // Amount
       double amount=UTILS.BASIC.getReward("ID_CONTENT");
       
       // Total voting power for content
       ResultSet rs=UTILS.DB.executeQuery("SELECT SUM(power) AS total "
                                          + "FROM votes "
                                         + "WHERE target_type<>'ID_COM' "
                                          + "AND block>"+(block-1440));
       
       // Next 
       rs.next();
       
       // Total votes
       double votes=rs.getDouble("total");
               
       // Load voted content
       rs=UTILS.DB.executeQuery("SELECT DISTINCT(targetID),target_type "
                                          + "FROM votes "
                                         + "WHERE block>="+(block-1440)+" "
                                           + "AND target_type<>'ID_COM'");
       
       // Parse
       while (rs.next())
       {
           // Power
           double power=this.getTargetPower(rs.getLong("targetID"), block);
           
           // Percent
           double p=power*100/votes;
           
           // Payment
           double pay=UTILS.BASIC.round(p/100*amount, 4);
           
           // Total paid
           total_paid=total_paid+pay;
           
           // Payment address
           String pay_adr=UTILS.BASIC.getTargetAdr(rs.getString("target_type"), rs.getLong("targetID"));
           
           // Transaction
           if (pay>=0.0001)
           {
               UTILS.ACC.newTrans(pay_adr, 
                                  "masknetwork", 
                                  pay, 
                                  true,
                                  "MSK", 
                                  "Content reward", 
                                  "", 
                                  hash, 
                                  block,
                                  null,
                                  0);
           
              // Record reward
              this.addReward(pay_adr, "ID_CONTENT", pay, block);
           }
       }
       
       // Return
       return UTILS.BASIC.round(total_paid, 4);
   }
   
   public double payComments(long block) throws Exception
   {
       // Total paid
       double total_paid=0;
       
       // Hash
       String hash=UTILS.BASIC.hash(String.valueOf(block));
       
       // Amount
       double amount=UTILS.BASIC.getReward("ID_COM");
       
       // Total voting power for content
       ResultSet rs=UTILS.DB.executeQuery("SELECT SUM(power) AS total "
                                          + "FROM votes "
                                         + "WHERE target_type='ID_COM' "
                                          + "AND block>"+(block-1440));
       
       // Next
       rs.next();
       
       // Total votes
       double votes=rs.getDouble("total");
       
       // Load voted content
       rs=UTILS.DB.executeQuery("SELECT DISTINCT(targetID) "
                                          + "FROM votes "
                                         + "WHERE block>="+(block-1440)+" "
                                           + "AND target_type='ID_COM'");
       
       // Parse
       while (rs.next())
       {
           // Power
           double power=this.getTargetPower(rs.getLong("targetID"), block);
           
           // Percent
           double p=power*100/votes;
           
           // Payment
           double pay=UTILS.BASIC.round(p/100*amount, 4);
           
           // Total paid
           total_paid=total_paid+pay;
           
           // Payment address
           String pay_adr=UTILS.BASIC.getTargetAdr("ID_COM", rs.getLong("targetID"));
           
           // Transaction
           UTILS.ACC.newTrans(pay_adr, 
                              "masknetwork", 
                              pay, 
                              true,
                              "MSK", 
                              "Content reward", 
                              "", 
                              hash, 
                              block,
                              null,
                              0);
           
           // Record reward
           this.addReward(pay_adr, "ID_COM", pay, block);
       }
       
       // Return
       return UTILS.BASIC.round(total_paid, 4);
   }
   
   // Reward votes
   public double payVoters(long block) throws Exception
   {
       // Total paid
       double total_paid=0;
       
       // Hash
       String hash=UTILS.BASIC.hash(String.valueOf(block));
       
       // Amount
       double amount=UTILS.BASIC.getReward("ID_VOTER");
       
       // Total voting power
       ResultSet rs=UTILS.DB.executeQuery("SELECT SUM(power) AS total "
                                          + "FROM votes "
                                         + "WHERE block>="+(block-1440));
       
       // Next
       rs.next();
       
       // Total
       double votes=rs.getDouble("total");
       
       // Load voters
       rs=UTILS.DB.executeQuery("SELECT DISTINCT(adr) "
                                + "FROM votes "
                               + "WHERE block>"+(block-1440));
       
       while (rs.next())
       {
           // Can pay ?
           if (this.canReward(rs.getString("adr"), block))
           {
              // Voting power
              double power=this.getVoterPower(rs.getString("adr"), block);
           
              // Percent
              double p=power*100/votes;
           
              // Payment
              double pay=UTILS.BASIC.round(p/100*amount, 4);
           
              // Total paid
              total_paid=total_paid+pay;
           
              // Transaction
              UTILS.ACC.newTrans(rs.getString("adr"), 
                                 "masknetwork", 
                                 pay, 
                                 true,
                                 "MSK", 
                                 "Voter reward", 
                                 "", 
                                 hash, 
                                 block,
                                 null,
                                 0);
              
              // Record reward
              this.addReward(rs.getString("adr"), "ID_VOTER", pay, block);
           }
       }
       
       // Return
       return UTILS.BASIC.round(total_paid, 4);
   }
   
   public double payApp(long block) throws Exception
   {
       // Minimum 10 apps in app store ?
       ResultSet rs=UTILS.DB.executeQuery("SELECT COUNT(*) AS total "
                                          + "FROM agents "
                                         + "WHERE app_store>0");
       
       // Next
       rs.next();
       
       // Minimum 10
       if (rs.getLong("total")<10) return 0;
       
       // Total paid
       double total_paid=0;
       
       // Hash
       String hash=UTILS.BASIC.hash(String.valueOf(block));
       
       // Amount
       double amount=UTILS.BASIC.getReward("ID_APP");
       
       // Under management
       rs=UTILS.DB.executeQuery("SELECT SUM(adr.balance) AS total "
                                          + "FROM agents AS ag "
                                          + "JOIN adr ON adr.adr=ag.adr "
                                         + "WHERE ag.owner<>ag.adr "
                                           + "AND ag.app_store=0");
       
       // Next
       rs.next();
       
       // Total
       double total=rs.getDouble("total");
       
       // Parse applications of app store
       rs=UTILS.DB.executeQuery("SELECT DISTINCT(aID) "
                               +  "FROM agents "
                               + "WHERE app_store>0");
       
       // Parse
       while (rs.next())
       {
            // Voting power
           double power=this.getAppPower(rs.getLong("aID"));
           
           // Percent
           double p=power*100/total;
           
           // Payment
           double pay=UTILS.BASIC.round(p/100*amount, 4);
           
           // Total paid
           total_paid=total_paid+pay;
           
           // Payment address
           String pay_adr=this.getAppAdr(rs.getLong("aID"));
           
           // Transaction
           UTILS.ACC.newTrans(pay_adr, 
                              "masknetwork", 
                              pay, 
                              true,
                              "MSK", 
                              "Voter reward", 
                              "", 
                              hash, 
                              block,
                              null,
                              0);
           
           // Record reward
           this.addReward(pay_adr, "ID_APP", pay, block);
       }
       
       // Return
       return UTILS.BASIC.round(total_paid, 4);
   }
   
   public double getVoterPower(String adr, long block) throws Exception
   {
       // ResultSet
       ResultSet rs=UTILS.DB.executeQuery("SELECT SUM(power) AS total "
                                          + "FROM votes "
                                         + "WHERE adr='"+adr+"' "
                                           + "AND block>"+(block-1440));
       
       // Next
       rs.next();
       
       // Return
       return rs.getDouble("total");
   }
   
   public double getTargetPower(long targetID, long block) throws Exception
   {
       // ResultSet
       ResultSet rs=UTILS.DB.executeQuery("SELECT SUM(power) AS total "
                                          + "FROM votes "
                                         + "WHERE targetID='"+targetID+"' "
                                           + "AND block>"+(block-1440));
       
       // Next
       rs.next();
       
       // Return
       return rs.getDouble("total");
   }
   
   public double getAppPower(long aID) throws Exception
   {
       // Load agent data
       ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                          + "FROM agents "
                                         + "WHERE aID='"+aID+"'");
       
       // Next
       rs.next();
       
       // Load power
       rs=UTILS.DB.executeQuery("SELECT SUM(adr.balance) AS total "
                                          + "FROM agents AS ag "
                                          + "JOIN adr ON adr.adr=ag.adr "
                                         + "WHERE ag.owner<>ag.adr "
                                           + "AND owner='"+rs.getString("adr")+"'");
       
       // Next
       rs.next();
       
       // Return
       return rs.getDouble("total");
   }
   
   public boolean canReward(String adr, long block) throws Exception
   {
       // Comments
       boolean com=false;
       boolean posts=false;
       boolean other=false;
       
       // Voted 5 comments ?
       ResultSet rs=UTILS.DB.executeQuery("SELECT COUNT(*) AS total "
                                          + "FROM votes "
                                         + "WHERE adr='"+adr+"' "
                                           + "AND target_type='ID_COM'"
                                           + "AND block>'"+(block-1440)+"'");
       
       // Next
       rs.next();
       
       // Voted ?
       if (rs.getLong("total")>=5)
           com=true;
       else
           com=false;
       
       // Voted 3 posts ?
       rs=UTILS.DB.executeQuery("SELECT COUNT(*) AS total "
                                + "FROM votes "
                               + "WHERE adr='"+adr+"' "
                                 + "AND target_type='ID_POST'"
                                 + "AND block>'"+(block-1440)+"'");
       
       // Next
       rs.next();
       
       // Voted ?
       if (rs.getLong("total")>=3)
           posts=true;
       else
           posts=false;
       
       // Voted 1 other content ?
       rs=UTILS.DB.executeQuery("SELECT COUNT(*) AS total "
                                + "FROM votes "
                               + "WHERE adr='"+adr+"' "
                                 + "AND (target_type<>'ID_COM' && target_type<>'ID_POST')"
                                 + "AND block>'"+(block-1440)+"'");
       
       // Next
       rs.next();
       
       // Voted ?
       if (rs.getLong("total")>=1)
           other=true;
       else
           other=false;
       
       // Approved
       if (com && posts && other)
           return true;
       else
           return false;
   }
   
   public void addReward(String adr, 
                         String reward, 
                         double amount, 
                         long block) throws Exception
   {
       UTILS.DB.executeUpdate("INSERT INTO rewards "
                                    + "SET adr='"+adr+"', "
                                        + "reward='"+reward+"', "
                                        + "amount='"+UTILS.FORMAT_4.format(amount)+"', "
                                        + "block='"+block+"'");
   }
   
   public String getAppAdr(long aID) throws Exception
   {
       ResultSet rs=UTILS.DB.executeQuery("SELECT * FROM agents WHERE aID='"+aID+"'");
       rs.next();
       return rs.getString("pay_adr");
   }
}
