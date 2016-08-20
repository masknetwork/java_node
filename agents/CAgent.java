package wallet.agents;

import java.util.ArrayList;
import wallet.agents.VM.CCell;
import wallet.agents.VM.VM;
import wallet.agents.VM.sys.agent.vars.VARS;
import wallet.kernel.UTILS;
import wallet.network.packets.CPayload;

public class CAgent implements java.io.Serializable
{
   // Agent ID
   long ID;
   
   // Sandbox ?
   boolean sandbox;
   
   // VM
   public VM VM;
   
   // Parser
   public CParser PARSER;
   
   public CAgent(long ID, boolean sandbox, long block) throws Exception
   {
       // VM utils
       VM=new VM(ID, sandbox);
       
       // Sandboxed
       this.sandbox=sandbox;
       
       // ID
       this.ID=ID;
   
       // Block
       VM.block=block;
   }
   
   
   public void execute(String tag, boolean sandbox, long block) throws Exception
   {
       boolean parsed=false;
       
       try
       {
           // Line
           long line=0;
       
           // Load parser
           PARSER=new CParser(new CGrammar(), VM);
       
           // Load code
           PARSER.loadCode(this.ID, this.sandbox);
       
           // Parse
           PARSER.parse(this.sandbox);
           
           // Parsed
           parsed=true;
       
           if (!tag.equals("#install#"))
           {
               if (VM.TAGS.hasTag("#start#")) 
                  VM.CODE.execute(VM.TAGS.getLine("#start#"));
       
               // Start tag ?
               if (!tag.equals("") && !tag.equals("#start#")) 
               {
                   if (!VM.TAGS.hasTag(tag)) 
                       return;
                   else
                       VM.CODE.execute(VM.TAGS.getLine(tag));
               }
           
               if (VM.TAGS.hasTag("#cleanup#")) 
                  VM.CODE.execute(VM.TAGS.getLine("#cleanup#"));
           }
           else
           {
              if (VM.TAGS.hasTag("#install#")) 
                  VM.CODE.execute(VM.TAGS.getLine("#install#")); 
           }
        }
        catch (Exception ex)
        {
            VM.RUNLOG.add(VM.REGS.RCI, ex.getMessage());
            System.out.println(ex.getMessage());
        }
        finally
        {
            if (VM.CODE.steps>0)
            {
                if (!VM.sandbox)
                {
                    UTILS.ACC.newTransfer(VM.SYS.getVar("SYS.AGENT.GENERAL.OWNER").val, 
                                            "default", 
                                            VM.CODE.fee, 
                                            false,
                                            "MSK", 
                                            "VM execution payment", 
                                            "MSK", 
                                            UTILS.BASIC.hash(VM.SYS.getVar("SYS.AGENT.GENERAL.OWNER").val), 
                                            UTILS.NET_STAT.last_block,
                                            null, 
                                            0);
                
                    UTILS.ACC.clearTrans(UTILS.BASIC.hash(VM.SYS.getVar("SYS.AGENT.GENERAL.OWNER").val), "ID_ALL", block);
                }
                 
                 if (VM.CODE.fee<VM.SYS.AGENT.GENERAL.balance)
                 {
                    // Flush run log
                    VM.RUNLOG.flush();
                 }
                 else
                 {
                     // Run out of funds
                     this.uninstall();
                 }
                
                 // Success
                 System.out.println("Executed "+VM.CODE.steps+" steps ("+UTILS.FORMAT_8.format(VM.CODE.fee)+" MSK) !!!");
            }
            
            // Syntax errors on live execution
            if (parsed==false)
               if (!VM.sandbox) 
                   this.uninstall();
        }
   }
   
   public void uninstall() throws Exception
   {
       System.out.println("Syntax error. Uninstalling agent .");
       UTILS.DB.executeUpdate("DELETE FROM agents WHERE aID='"+this.ID+"'");
       UTILS.DB.executeUpdate("DELETE FROM storage WHERE aID='"+this.ID+"'");
   }
   
   
   public String parse() throws Exception
   {
       // Line
       long line=0;
       
       // Load parser
       PARSER=new CParser(new CGrammar(), VM);
       
       // Load code
       PARSER.loadCode(this.ID, this.sandbox);
       
       // Parse
       return PARSER.parse(this.sandbox);
   }
   
   public boolean transAproved()
   {
       return VM.SYS.EVENT.TRANS.aproved;
   }
}
