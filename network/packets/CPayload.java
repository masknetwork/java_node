// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.network.packets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import wallet.kernel.*;
import wallet.network.*;
import wallet.network.packets.*;
import wallet.network.packets.trans.*;
import wallet.network.packets.blocks.*;

public class CPayload  implements java.io.Serializable
{
	// Hash
	public String hash="";
	
	// Block target address
        public String target_adr="";
	
	// Signature
	public String sign="";
	
	// Tstamp
	public long tstamp=0;
	
	// Tstamp
        public long block=0;
        
	// Constructor
        public CPayload() throws Exception
	{
           // Time
           this.tstamp=UTILS.BASIC.mtstamp();
           
           // Block
	   this.block=UTILS.NET_STAT.last_block+1;
           
           // Hash
           this.hash=this.getHash();
	}
    
        //  Hash
        public String getHash() throws Exception
        {	
            String data=this.target_adr+
                        String.valueOf(this.tstamp)+
                        String.valueOf(this.block);
       
    	    String h=UTILS.BASIC.hash(data);
    	
            return h;	
        }
    
        // Constructor
        public CPayload(String adr) throws Exception
        {
    	    // Time
            this.tstamp=UTILS.BASIC.mtstamp();
    	
           // Target address
    	   this.target_adr=adr;
    	
	   // Block
	   this.block=UTILS.NET_STAT.last_block+1;
	   
	   // Hash
	   this.hash=this.getHash();
        }
    
        // Check
        public CResult check(CBlockPayload block) throws Exception
        {
           // Hash
           if (!UTILS.BASIC.isSHA256(this.hash)) 
                throw new Exception("Invalid hash - CPayload");
            
           // Target adr
            if (!this.target_adr.equals(""))
               if (!UTILS.BASIC.adressValid(this.target_adr))
                  throw new Exception("Invalid target address - CPayload");
            
           // Check sig
           if (!this.checkSig())
              throw new Exception("Invalid signature - CPayload");
           
 	   return new CResult(true, "Ok", "CPayload", 183);
        }
        
        
        public void sign(String sig) throws Exception
        {
            if (!this.target_adr.equals(""))
            {
                if (sig.equals(""))
                {
    	           CAddress adr=UTILS.WALLET.getAddress(this.target_adr);
    	           this.sign=adr.sign(this.hash);
                }
                else this.sign=sig;
            }
        }
        
        public void sign() throws Exception
        {
            if (!this.target_adr.equals(""))
            {
               CAddress adr=UTILS.WALLET.getAddress(this.target_adr);
    	       this.sign=adr.sign(this.hash);
                
            }
        }
    
        public boolean checkSig() throws Exception
        {
    	   CECC ecc=new CECC(this.target_adr);
    	
    	   if (ecc.checkSig(this.hash, this.sign)==true)
    		return true;
    	   else
    		return false;
        }
    
        public CResult commit(CBlockPayload block) throws Exception
        {
           return new CResult(true, "Ok", "CPayload", 240);
        }
        
        public void footprint(String packet_hash) throws Exception
        {
        
        }
}