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
    
    // Hash rate
    long hash=0;
    
    // ID
    long ID=0;
    
    // Active
    boolean active=true;
    
    public CCPUMiner(long ID)
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
       
       // ID
       this.ID=ID;
    }
    
    public void run() 
    {
        try
        {
        // Hash
        String hash;
        
         // Number
        BigInteger num=new BigInteger("0");
        
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
                 
        while (!Thread.currentThread().isInterrupted() && this.active==true)
        {
            if (!UTILS.NET_STAT.last_block_hash.equals("") && 
                UTILS.NETWORK.peers.peers.size()>=0 &&
                !UTILS.CBLOCK.signer.equals("") &&
                UTILS.STATUS.engine_status.equals("ID_ONLINE") &&
                UTILS.NETWORK.CONSENSUS.status.equals("ID_WAITING"))
            {
              // Nonce
              nonce++;
              
              // Tstamp
              ts=UTILS.BASIC.tstamp();
              
              // Hash
              hash=UTILS.BASIC.hash(UTILS.NET_STAT.last_block_hash+
                                    String.valueOf(UTILS.NET_STAT.last_block+1)+
                                    UTILS.CBLOCK.payload_hash+
                                    UTILS.CBLOCK.signer+
                                    String.valueOf(ts)+
                                    String.valueOf(nonce)+
                                    String.valueOf(UTILS.BASIC.formatDif(UTILS.NET_STAT.net_dif.toString(16))));
             
              ihash=hash;
              
              // Step
              step++;
              
              // Get hash
              hash=this.getHash(UTILS.NET_STAT.last_block_hash, hash);
             
              // Number
              num=new BigInteger(hash, 16);
              
              // Speed
              if (step%1000==0) 
              {
                  if (UTILS.BASIC.tstamp()>start) 
                       speed=Math.round(1000/(UTILS.BASIC.tstamp()-start));
                  else
                       speed=1000;
                  
                  // Start
                  start=UTILS.BASIC.tstamp();
                  
                  // Update
                  UTILS.DB.executeUpdate("UPDATE web_sys_data "
                                          + "SET mining='"+UTILS.BASIC.tstamp()+"', "
                                              + "cpu_"+this.ID+"_power='"+speed+"'");
              }
              
              // Found solution
              if (num.compareTo(UTILS.NET_STAT.net_dif.multiply(BigInteger.valueOf(UTILS.CBLOCK.signer_balance)))<0) 
              {  
                  // Timestamp
                  UTILS.CBLOCK.last_tstamp=ts;
                  
                  // Nonce
                  UTILS.CBLOCK.nonce=nonce;
                  
                  // Set nonce
                  UTILS.CBLOCK.setNonce(nonce);
                  
                  // Block hash
                  UTILS.CBLOCK.block_hash=UTILS.BASIC.formatDif(hash);
                  
                  // Broadcast
                  UTILS.CBLOCK.broadcast();
                  
                  // New nonce
                  nonce=Math.round(Math.random()*10000000000L);
              }
            }
          }
        
          System.out.println("Miner has stopped");
        }
        catch (Exception ex) 
       	      {  
       		UTILS.LOG.log("SQLException", ex.getMessage(), "CCPUMiner.java", 57);
              }
        
       
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
    
    public void stopMiner()
    {
        this.active=false;
    }
}
