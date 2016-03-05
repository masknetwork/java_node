// Author : Vlad Cristian
// Contact : vcris@gmx.com

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

public class CCPUMiner extends Thread
{
    // A
    BLAKE224 a;
    
    // B
    BLAKE256 b;
    
    // C
    BLAKE384 c;
    
    // D
    BLAKE512 d;
    
    // E
    BMW224 e;
    
    // F
    BMW256 f;
    
    // G
    BMW384 g;
    
    // H
    BMW512 h;
    
    // I
    CubeHash224 i;
    
    // J
    CubeHash256 j;
    
    // K
    CubeHash384 k;
    
    // L
    CubeHash512 l;
    
    // M
    ECHO224 m;
    
    // N
    ECHO256 n;
    
    // O
    ECHO384 o;
    
    // P
    ECHO512 p;
    
    // R
    Fugue224 r;
    
    // S
    Fugue384 s;
    
    // T
    Fugue384 t;
    
    // U
    Fugue512 u;
    
    // V
    Groestl224 v;
    
    // X
    Groestl256 x;
    
    // Y
    Groestl384 y;
    
    // Z
    Groestl512 z;
    
    // W
    SHA224 w;
    
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
    
    
    public CCPUMiner()
    {
       // A
       a=new BLAKE224();
    
       // B
       b=new BLAKE256();
    
       // C
       c=new BLAKE384();
    
       // D
       d=new BLAKE512();
    
       // E
       e=new BMW224();
    
       // F
       f=new BMW256();
    
       // G
       g=new BMW384();
    
       // H
       h=new BMW512();
    
       // I
       i=new CubeHash224();
    
       // J
       j=new CubeHash256();
    
       // K
       k=new CubeHash384();
    
       // L
       l=new CubeHash512();
    
       // M
       m=new ECHO224();
    
       // N
       n=new ECHO256();
    
       // O
       o=new ECHO384();
    
       // P
       p=new ECHO512();
    
       // R
       r=new Fugue224();
    
       // S
       s=new Fugue384();
    
       // T
       t=new Fugue384();
    
       // U
       u=new Fugue512();
    
       // V
       v=new Groestl224();
    
       // X
       x=new Groestl256();
    
       // Y
       y=new Groestl384();
    
       // Z
       z=new Groestl512();
    
       // W
       w=new SHA224();
    
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
    
    public void run() 
    {
        try
        {
        // Hash
        String hash;
        
         // Number
        long num=0;
        
        // Nonce
        long nonce=Math.round(Math.random()*10000000000L);
        
        // Step
        long step=0;
        
        // Start
        long start=UTILS.BASIC.tstamp();
        
        // Hash speed
        long speed;
        
        // Num
        String snum="";
        String ihash="";
        
        int po=0;
        
        long ts=0;
                 
        while (!Thread.currentThread().isInterrupted())
        {
            if (!UTILS.NET_STAT.last_block_hash.equals("") && 
                UTILS.NETWORK.peers.peers.size()>-1 &&
                !UTILS.CBLOCK.signer.equals("") &&
                UTILS.STATUS.engine_status.equals("ID_ONLINE"))
            {
              // Nonce
              nonce++;
              
              // Tstamp
              ts=UTILS.BASIC.tstamp();
              
              // Hash
              hash=UTILS.BASIC.hash(UTILS.NET_STAT.last_block_hash+
                                    "ID_BLOCK"+
                                    String.valueOf(UTILS.NET_STAT.last_block+1)+
                                    UTILS.CBLOCK.payload_hash+
                                    UTILS.CBLOCK.signer+
                                    String.valueOf(UTILS.CBLOCK.signer_balance)+
                                    String.valueOf(ts)+
                                    String.valueOf(nonce)+
                                    String.valueOf(UTILS.NET_STAT.net_dif));
              
              ihash=hash;
              
              // Step
              step++;
              
              // Get hash
              hash=this.getHash(UTILS.NET_STAT.last_block_hash, hash);
             
              // Number
              num=this.hashToNum(hash);
              
              // Speed
              if (step%10000==0) 
              {
                  speed=Math.round(10000/(UTILS.BASIC.tstamp()-start));
                  start=UTILS.BASIC.tstamp();
              }
              
              // Found solution
              if (num<UTILS.NET_STAT.net_dif) 
              {   
                  
                  UTILS.CBLOCK.last_tstamp=ts;
                  UTILS.CBLOCK.nonce=nonce;
                  UTILS.CBLOCK.setNonce(nonce);
                  UTILS.CBLOCK.block_hash=hash;
                  UTILS.CBLOCK.broadcast();
                  nonce=Math.round(Math.random()*10000000000L);
                  
                  
              }
            }
        }
        }
        catch (Exception ex) 
       	      {  
       		UTILS.LOG.log("SQLException", ex.getMessage(), "CBuyDomainPayload.java", 57);
              }
        
        System.out.println("CPUMiner has stopped !!!");
    }
    
    public long hashToNum(String hash) throws Exception
    {
        int po=0;
              String snum="";
              
              while (snum.length()<18 && po<64)
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
              long num=Long.parseLong(snum);
              
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
                      case 'g' : hash=UTILS.BASIC.base64_encode(g.digest(hash.getBytes())); break;
                      case 'h' : hash=UTILS.BASIC.base64_encode(h.digest(hash.getBytes())); break;
                      case 'i' : hash=UTILS.BASIC.base64_encode(i.digest(hash.getBytes())); break;
                      case 'j' : hash=UTILS.BASIC.base64_encode(j.digest(hash.getBytes())); break;
                      case 'k' : hash=UTILS.BASIC.base64_encode(k.digest(hash.getBytes())); break;
                      case 'l' : hash=UTILS.BASIC.base64_encode(l.digest(hash.getBytes())); break;
                      case 'm' : hash=UTILS.BASIC.base64_encode(m.digest(hash.getBytes())); break;
                      case 'n' : hash=UTILS.BASIC.base64_encode(n.digest(hash.getBytes())); break;
                      case 'o' : hash=UTILS.BASIC.base64_encode(o.digest(hash.getBytes())); break;
                      case 'p' : hash=UTILS.BASIC.base64_encode(p.digest(hash.getBytes())); break;
                      case 'r' : hash=UTILS.BASIC.base64_encode(r.digest(hash.getBytes())); break;
                      case 's' : hash=UTILS.BASIC.base64_encode(s.digest(hash.getBytes())); break;
                      case 't' : hash=UTILS.BASIC.base64_encode(t.digest(hash.getBytes())); break;
                      case 'u' : hash=UTILS.BASIC.base64_encode(u.digest(hash.getBytes())); break;
                      case 'v' : hash=UTILS.BASIC.base64_encode(v.digest(hash.getBytes())); break;
                      case 'x' : hash=UTILS.BASIC.base64_encode(x.digest(hash.getBytes())); break;
                      case 'y' : hash=UTILS.BASIC.base64_encode(y.digest(hash.getBytes())); break;
                      case 'z' : hash=UTILS.BASIC.base64_encode(z.digest(hash.getBytes())); break;
                      case 'w' : hash=UTILS.BASIC.base64_encode(w.digest(hash.getBytes())); break;
                  }
              }
              
              // Last Hash
              hash=UTILS.BASIC.hash(hash);
              
              // Return 
              return hash;
    }
}
