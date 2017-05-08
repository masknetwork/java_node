package wallet.kernel;

import wallet.kernel.x34.Skein512;
import wallet.kernel.x34.Hamsi512;
import wallet.kernel.x34.Fugue224;
import wallet.kernel.x34.Fugue512;
import wallet.kernel.x34.Fugue384;
import wallet.kernel.x34.ECHO224;
import wallet.kernel.x34.BMW384;
import wallet.kernel.x34.BLAKE256;
import wallet.kernel.x34.SHA256;
import wallet.kernel.x34.Groestl256;
import wallet.kernel.x34.BLAKE384;
import wallet.kernel.x34.BMW256;
import wallet.kernel.x34.Luffa512;
import wallet.kernel.x34.SHA512;
import wallet.kernel.x34.Groestl512;
import wallet.kernel.x34.BMW512;
import wallet.kernel.x34.Groestl224;
import wallet.kernel.x34.ECHO512;
import wallet.kernel.x34.SHAvite512;
import wallet.kernel.x34.BLAKE224;
import wallet.kernel.x34.CubeHash224;
import wallet.kernel.x34.BMW224;
import wallet.kernel.x34.Shabal512;
import wallet.kernel.x34.Groestl384;
import wallet.kernel.x34.ECHO256;
import wallet.kernel.x34.CubeHash256;
import wallet.kernel.x34.BLAKE512;
import wallet.kernel.x34.HMAC;
import wallet.kernel.x34.SIMD512;
import wallet.kernel.x34.JH512;
import wallet.kernel.x34.Keccak512;
import wallet.kernel.x34.CubeHash512;
import wallet.kernel.x34.CubeHash384;
import wallet.kernel.x34.ECHO384;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Random;
import wallet.kernel.x34.SHA224;

public class CCPUMinerUtils
{
    // A
    BLAKE512 a;
    
    // B
    BMW512 b;
    
    // C
    CubeHash512 c;
    
    // D
    ECHO512 d;
    
    // E
    Fugue512 e;
    
    // F
    Groestl512 f;
    
    // 0
    Hamsi512 f0;
    
    // 1
    JH512 f1;
    
    // 2
    Keccak512 f2;
    
    // 3
    Luffa512 f3;
    
    // 4
    SHAvite512 f4;
    
    // 5
    SIMD512 f5;
    
    // 6
    Shabal512 f6;
    
    // 7 
    Skein512 f7;
    
    // 8
    SHA256 f8;
    
    // 9
    SHA512 f9;
    
    
    public CCPUMinerUtils()
    {
       // A
       a=new BLAKE512();
    
       // B
       b=new BMW512();
    
       // C
       c=new CubeHash512();
    
       // D
       d=new ECHO512();
    
       // E
       e=new Fugue512();
    
       // F
       f=new Groestl512();
    
       // 0
       f0=new Hamsi512();
    
       // 1
       f1=new JH512();
    
       // 2
       f2=new Keccak512();
    
       // 3
       f3=new Luffa512();
    
       // 4
       f4=new SHAvite512();
    
       // 5
       f5=new SIMD512();
    
       // 6
       f6=new Shabal512();
    
       // 7 
       f7=new Skein512();
    
       // 8
       f8=new SHA256();
    
       // 9
       f9=new SHA512();
        
    }
   
                                                      
    public boolean checkHash(String prev_hash, 
                             long block_no, 
                             String payload_hash, 
                             String block_signer, 
                             long timestamp, 
                             long nonce,
                             String check_block_hash,
                             String net_dif) throws Exception
    {
        String hash=UTILS.BASIC.hash(prev_hash+
                                     String.valueOf(block_no)+
                                     payload_hash+
                                     block_signer+
                                     String.valueOf(timestamp)+
                                     String.valueOf(nonce)+
                                     net_dif);
        
        // Get hash
        hash=this.getHash(prev_hash, hash);
           
        // Get number
        BigInteger num=new BigInteger(hash, 16);
       
        // Delegate power
        BigInteger del_power=BigInteger.valueOf(UTILS.DELEGATES.getPower(block_signer));
        
        // Check nonce
        if (num.compareTo(new BigInteger(net_dif, 16).multiply(del_power))<0 &&
            hash.equals(check_block_hash)) 
            return true;
        else
            return false;
    }
    
   
    
    public  String getHash(String ph, String ha) throws Exception
    {
        int pos;
        String hash=ha;
        String prev_hash=ph;
        
        // Load algorithms
        for (pos=0; pos<=63; pos++)
        {
            // Character
            char ch=prev_hash.charAt(pos);
                  
                  switch (ch)
                  {
                      case '0' : hash=UTILS.BASIC.base64_encode(f0.digest(hash.getBytes())); break;
                      case '1' : hash=UTILS.BASIC.base64_encode(f1.digest(hash.getBytes())); break;
                      case '2' : hash=UTILS.BASIC.base64_encode(f2.digest(hash.getBytes())); break;
                      case '3' : hash=UTILS.BASIC.base64_encode(f3.digest(hash.getBytes())); break;
                      case '4' : hash=UTILS.BASIC.base64_encode(f4.digest(hash.getBytes())); break;
                      case '5' : hash=UTILS.BASIC.base64_encode(f5.digest(hash.getBytes())); break;
                      case '6' : hash=UTILS.BASIC.base64_encode(f6.digest(hash.getBytes())); break;
                      case '7' : hash=UTILS.BASIC.base64_encode(f7.digest(hash.getBytes())); break;
                      case '8' : hash=UTILS.BASIC.base64_encode(f8.digest(hash.getBytes())); break;
                      case '9' : hash=UTILS.BASIC.base64_encode(f9.digest(hash.getBytes())); break;
                      case 'a' : hash=UTILS.BASIC.base64_encode(a.digest(hash.getBytes())); break;
                      case 'b' : hash=UTILS.BASIC.base64_encode(b.digest(hash.getBytes())); break;
                      case 'c' : hash=UTILS.BASIC.base64_encode(c.digest(hash.getBytes())); break;
                      case 'd' : hash=UTILS.BASIC.base64_encode(d.digest(hash.getBytes())); break;
                      case 'e' : hash=UTILS.BASIC.base64_encode(e.digest(hash.getBytes())); break;
                      case 'f' : hash=UTILS.BASIC.base64_encode(f.digest(hash.getBytes())); break;
                  }
              }
              
              // Last Hash
              hash=UTILS.BASIC.hash(hash);
              
              // Return 
              return hash;
    }
}