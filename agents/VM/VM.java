package wallet.agents.VM;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import wallet.agents.VM.storage.CStorage;
import wallet.agents.VM.sys.agent.vars.VARS;
import wallet.agents.VM.sys.SYS;
import wallet.kernel.UTILS;

public class VM  implements java.io.Serializable
{
   // Registers
   public CRegisters REGS;  
   
   // VM Utils
   public CVMUtils vm_utils;
   
   // Ops code
   public COpCode CODE;
   
   // Stack
   public CStack STACK;
   
   // Memory
   public CMemory MEM;
   
   // Storage 
   public CStorage STOR;
   
   // Last cmp
   public long last_cmp=0;
   
   // Tags
   public CTags TAGS;
   
   // Global variables
   public SYS SYS;
   
   // Agent ID
   public long agentID;
   
   // Sandbox
   public boolean sandbox;
   
   // Run log
   public CRunLog RUNLOG;
   
   // Block
   public long block;
   
   // App balance
   public double balance;
   
   // Heap
   public ArrayList HEAP=new ArrayList();

    
   public VM(long agentID, boolean sandbox) throws Exception
   {
       // Agent ID
       this.agentID=agentID;
       
       // Sandbox
       this.sandbox=sandbox;
       
       // Registers
       this.REGS=new CRegisters();
       
       // Utils
       this.vm_utils=new CVMUtils();
       
       // Op code
       this.CODE=new COpCode(this);
       
       // Stack
       this.STACK=new CStack(this);
       
       // Memory
       this.MEM=new CMemory(this);
       
       // Storage
       this.STOR=new CStorage(this, sandbox);
       
       // Tags
       this.TAGS=new CTags();
       
       // Globals
       this.SYS=new SYS(agentID, sandbox);
       
       // Run log
       this.RUNLOG=new CRunLog(this, sandbox);
   }
   
 
}
