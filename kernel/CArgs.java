package wallet.kernel;

public class CArgs 
{
    // Late op
    String late_op="";
    
    // Rhash
    String rhash="";
    
    public CArgs()
    {
        
            
    }
    
    public void load(String[] args) throws Exception
    {
        for (int a=0; a<=args.length-1; a++)
        {
            String arg[]=args[a].split("=");
            this.process(arg[0], arg[1]);
        }
    }
  
  
    public void process(String name, String val) throws Exception
    {
        switch (name)
        {
            // Port
            case "port" :  UTILS.SETTINGS.port=Integer.parseInt(val);
                           break;
                           
             // DB Name
            case "db_name" :  UTILS.SETTINGS.db_name=val;
                              break;
                           
             // DB User
            case "db_user" :  UTILS.SETTINGS.db_user=val;
                              break;
                           
             // DB Pass
            case "db_pass" :  UTILS.SETTINGS.db_pass=val;
                              break;
                           
             // DB Pass
            case "wallet_pass" :  UTILS.SETTINGS.wallet_pass=val;
                                  break;
                                  
            // Rhash
            case "rhash" : this.rhash=val;
                                  break;
                                  
            // DB debug
            case "db_debug" : UTILS.SETTINGS.db_debug=val;
                                  break;
                                  
            // Late op
            case "op" : this.late_op=val; break;
        }
    }
    
    public void lateOp() throws Exception
    {
        if (this.late_op.equals("drop_db"))
        {
            UTILS.DB.reset(); 
            System.out.println("DB dropped"); 
            System.exit(0); 
        }
        
        if (this.late_op.equals("reorg"))
        {
            UTILS.NETWORK.CONSENSUS.reorganize(this.rhash);
            System.out.println("Reorganize"); 
        }
        
        if (this.late_op.equals("reset"))
        {
            this.reset();
            System.out.println("Resetting"); 
        }
        
        if (this.late_op.equals("list_adr"))
        {
            UTILS.WALLET.list();
            System.out.println("Done."); 
            System.exit(0);
        }
    }
    
    public void reset() throws Exception
    {
        UTILS.DB.executeUpdate("DROP TABLE adr");
        UTILS.DB.executeUpdate("DROP TABLE ads");
        UTILS.DB.executeUpdate("DROP TABLE agents");
        UTILS.DB.executeUpdate("DROP TABLE assets");
        UTILS.DB.executeUpdate("DROP TABLE assets_owners");
        UTILS.DB.executeUpdate("DROP TABLE blocks");
        UTILS.DB.executeUpdate("DROP TABLE blocks_pool");
        UTILS.DB.executeUpdate("DROP TABLE checkpoints");
        UTILS.DB.executeUpdate("DROP TABLE comments");
        UTILS.DB.executeUpdate("DROP TABLE delegates");
        UTILS.DB.executeUpdate("DROP TABLE del_votes");
        UTILS.DB.executeUpdate("DROP TABLE domains");
        UTILS.DB.executeUpdate("DROP TABLE escrowed");
        UTILS.DB.executeUpdate("DROP TABLE net_stat");
        UTILS.DB.executeUpdate("DROP TABLE packets");
        UTILS.DB.executeUpdate("DROP TABLE profiles");
        UTILS.DB.executeUpdate("DROP TABLE rec_packets");
        UTILS.DB.executeUpdate("DROP TABLE rewards");
        UTILS.DB.executeUpdate("DROP TABLE storage");
        UTILS.DB.executeUpdate("DROP TABLE sync");
        UTILS.DB.executeUpdate("DROP TABLE trans");
        UTILS.DB.executeUpdate("DROP TABLE trans_pool");
        UTILS.DB.executeUpdate("DROP TABLE tweets");
        UTILS.DB.executeUpdate("DROP TABLE tweets_follow");
        UTILS.DB.executeUpdate("DROP TABLE tweets_trends");
        UTILS.DB.executeUpdate("DROP TABLE votes");
        UTILS.DB.executeUpdate("DROP TABLE votes_stats");

        
        System.out.println("DB reset done. Restart the node.");
        System.exit(0);
    }
    
   
}
