package wallet.agents.VM.instructions.hash;

import java.util.ArrayList;
import org.apache.commons.codec.binary.Hex;
import wallet.agents.CToken;
import wallet.agents.VM.CCell;
import wallet.agents.VM.VM;
import wallet.agents.VM.instructions.CInstruction;
import wallet.kernel.x34.SHA224;
import wallet.kernel.x34.SHA256;
import wallet.kernel.x34.SHA384;
import wallet.kernel.x34.SHA512;

public class CHASH extends CInstruction
{
   // Target
    CToken target=null;
    
    // Function
    CToken function=null;
    
    public CHASH(VM VM, ArrayList<CToken>tokens) throws Exception
    {
        // Contrutor
        super(VM, "HASH");
        
        // Haystack
        this.target=tokens.get(1);
        
        // Niddle
        this.function=tokens.get(3);
    }
   
    public  void execute() throws Exception
    {
       String hash="";
       
       // Out 
       VM.RUNLOG.add(VM.REGS.RCI, "HASH "+this.target.cel.val+", "+this.function.cel.val);
        
       switch (this.function.cel.val)
       {
            case "SHA224" : SHA224 sha224=new SHA224(); 
                            hash=Hex.encodeHexString(sha224.digest(this.target.cel.val.getBytes()));
                            this.target.cel.copy(new CCell(hash));
                            break;
                           
            case "SHA256" : SHA256 sha256=new SHA256(); 
                            hash=Hex.encodeHexString(sha256.digest(this.target.cel.val.getBytes()));
                            this.target.cel.copy(new CCell(hash));
                            break;
                            
            case "SHA384" : SHA384 sha384=new SHA384(); 
                            hash=Hex.encodeHexString(sha384.digest(this.target.cel.val.getBytes()));
                            this.target.cel.copy(new CCell(hash));
                            break;
                            
            case "SHA512" : SHA512 sha512=new SHA512(); 
                            hash=Hex.encodeHexString(sha512.digest(this.target.cel.val.getBytes()));
                            this.target.cel.copy(new CCell(hash));
                            break;
       }
       
      
    }
}