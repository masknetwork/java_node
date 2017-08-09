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
            
            // Min peers
            case "min_peers" : UTILS.SETTINGS.min_peers=Integer.valueOf(val); 
                               break;
            
            // No sync
            case "seed_mode" : UTILS.SETTINGS.seed_mode=Boolean.valueOf(val); 
                               if (UTILS.SETTINGS.seed_mode) System.out.println("Seed mode is ON.");
                               break;
                             
            // Start mining
            case "start_mining" : UTILS.SETTINGS.start_mining=Boolean.valueOf(val); 
                                  break;
                                  
            // Sync start block
            case "sync_start_block" : UTILS.SETTINGS.sync_start_block=Long.parseLong(val); 
                                      break;
        }
    }
    
    public void lateOp() throws Exception
    {
        if (this.late_op.equals("reorg"))
        {
            UTILS.NETWORK.CONSENSUS.reorganize(this.rhash);
            System.out.println("Reorganize"); 
        }
        
        if (this.late_op.equals("list_adr"))
        {
            UTILS.WALLET.list();
            System.out.println("Done."); 
            System.exit(0);
        }
    }
    
    
}
