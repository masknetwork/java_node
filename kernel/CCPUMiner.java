package wallet.kernel;

import java.math.BigInteger;
import java.util.Random;

public class CCPUMiner extends Thread
{
    public CCPUMiner()
    {
        System.out.println("Mining started...");
    }
    
    public void run()
    {
        
        // Hash
        byte[] hash;
        
        // Number
        BigInteger num=UTILS.CBLOCK.dif;
        
        // Nonce
        long nonce=Math.round(Math.random()*1000000000000000000L);
        
        while (true)
        {
            if (UTILS.CBLOCK.nonce==0 && 
                !UTILS.CBLOCK.prev_hash.equals("") && 
                !UTILS.CBLOCK.signer.equals(""))
            {
              // Nonce
              nonce++;
            
              // Hash
              hash=org.apache.commons.codec.digest.DigestUtils.sha256(UTILS.CBLOCK.prev_hash+nonce);
           
              // Result
              num=new BigInteger(hash).abs();
            
              // Found solution
              if (num.compareTo(UTILS.CBLOCK.dif)<0) 
              {
                  UTILS.CBLOCK.nonce=nonce;
                  System.out.println("Block found : "+nonce+", num : "+num.toString());
                  UTILS.CBLOCK.setNonce(nonce);
                  UTILS.CBLOCK.broadcast();
                  nonce=Math.round(Math.random()*1000000000000000000L);
              }
            }
        }
    
    }
}
