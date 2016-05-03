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
   
   public CAgent(long ID, boolean sandbox) throws Exception
   {
       // VM utils
       VM=new VM(ID, sandbox);
       
       // Sandboxed
       this.sandbox=sandbox;
       
       // ID
       this.ID=ID;
   
       
   }
   
   
   public void loadTrans(String sender, 
                         double amount, 
                         String cur, 
                         String mes, 
                         String escrower, 
                         String hash) throws Exception
   {
       // Load transaction
       this.VM.SYS.OP.addTrans(sender, 
                               amount, 
                               cur, 
                               mes, 
                               escrower, 
                               hash);
   }
   
   public void loadMes(String sender, 
                       String subject, 
                       String mes, 
                       String hash) throws Exception
   {
       // Load transaction
       this.VM.SYS.OP.addMessage(sender, 
                                 subject, 
                                 mes, 
                                 hash);
   }
   
   public void loadBlock(String hash, 
                         long no, 
                         long nonce) throws Exception
   {
       // Load transaction
       this.VM.SYS.OP.addBlock(hash, 
                               no, 
                               nonce);
   }
   
   public void execute(String tag, boolean sandbox) throws Exception
   {
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
       
        }
        catch (Exception ex)
        {
            throw new Exception("Error on line "+VM.REGS.RCI+" ("+ex.getMessage()+")");
        }
        finally
        {
            if (VM.CODE.steps>0)
            {
                UTILS.BASIC.newTransfer(VM.SYS.getVar("SYS.AGENT.GENERAL.OWNER").val, 
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
                
                  UTILS.BASIC.clearTrans(UTILS.BASIC.hash(VM.SYS.getVar("SYS.AGENT.GENERAL.OWNER").val), "ID_ALL");
                
                 // Flush storage
                 VM.STOR.flush(sandbox);
       
                 // Flush run log
                 VM.RUNLOG.flush();
                
                 // Success
                 System.out.println("Executed "+VM.CODE.steps+" steps ("+UTILS.FORMAT_8.format(VM.CODE.fee)+" MSK) !!!");
            }
        }
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
       return VM.SYS.OP.TRANS.aproved;
   }
}
