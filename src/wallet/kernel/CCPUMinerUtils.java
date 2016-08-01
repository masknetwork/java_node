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
                             long signer_balance, 
                             long timestamp, 
                             long nonce,
                             String check_block_hash,
                             String net_dif,
                             String tab_1,
                             String tab_2,
                             String tab_3,
                             String tab_4,
                             String tab_5,
                             String tab_6,
                             String tab_7,
                             String tab_8,
                             String tab_9,
                             String tab_10,
                             String tab_11,
                             String tab_12,
                             String tab_13,
                             String tab_14) throws Exception
    {
        String hash=UTILS.BASIC.hash(prev_hash+
                                    "ID_BLOCK"+
                                    String.valueOf(block_no)+
                                    payload_hash+
                                    block_signer+
                                    String.valueOf(Math.round(signer_balance))+
                                    String.valueOf(timestamp)+
                                    String.valueOf(nonce)+
                                    net_dif+
                                    tab_1+
                                    tab_2+
                                    tab_3+
                                    tab_4+
                                    tab_5+
                                    tab_6+
                                    tab_7+
                                    tab_8+
                                    tab_9+
                                    tab_10+
                                    tab_11+
                                    tab_12+
                                    tab_13+
                                    tab_14);

      
        // Get hash
        hash=this.getHash(prev_hash, hash);
              
        // Get number
        BigInteger num=new BigInteger(hash, 16);
              
        // Found solution
        if (num.compareTo(new BigInteger(net_dif, 16).multiply(BigInteger.valueOf(Math.round(UTILS.ACC.getBalance(block_signer, "MSK")))))<0 && 
            hash.equals(check_block_hash)) 
            return true;
        else
            return false;
    }
    
    public BigInteger hashToNum(String hash) throws Exception
    {
        int po=0;
              String snum="";
              
              while (po<64)
              {
                  // Character
                  char ch=hash.charAt(po);
                  
                  switch (ch)
                  {
                      case '0' : snum=snum+'0'; break; 
                      case '1' : snum=snum+'1'; break; 
                      case '2' : snum=snum+'2'; break; 
                      case '3' : snum=snum+'3'; break; 
                      case '4' : snum=snum+'4'; break; 
                      case '5' : snum=snum+'5'; break; 
                      case '6' : snum=snum+'6'; break; 
                      case '7' : snum=snum+'7'; break; 
                      case '8' : snum=snum+'8'; break; 
                      case '9' : snum=snum+'9'; break; 
                  }
                  
                   // Pos
                  po++;
              }
              
              // Number
              BigInteger num=new BigInteger(snum);
              
              // Return 
              return num;
        
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