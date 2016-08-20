// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.kernel;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.SecretKey;
import javax.crypto.SealedObject;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;


public class CAES 
{
    // Salt
    private static String salt;
    
    // Iterations
    private static int pswdIterations = 1000;
    
    // Key size
    private static int keySize = 256;
    
    // IV Bytes
    private byte[] ivBytes;
    
    public CAES()
    {
		
    }
	
    public String encrypt(String plainText, String password) throws Exception
    {
        // Salt
        this.salt = UTILS.BASIC.hash(password);    
        
        // IV Btes
        this.ivBytes=UTILS.BASIC.hash(password).substring(0, 16).getBytes();
        
        // Salt bytes
        byte[] saltBytes = salt.getBytes("UTF-8");
         
        // Key factory
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        
        // Specifications
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(),
                                         saltBytes,
                                         pswdIterations,
                                         keySize
                                         );
 
        // Secret key
        SecretKey secretKey = factory.generateSecret(spec);
           
        // AES
        SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");
 
        // Cipher
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
           
        // Init
        cipher.init(Cipher.ENCRYPT_MODE, secret, new IvParameterSpec(ivBytes));
          
        // Encrypt
        byte[] encryptedTextBytes = cipher.doFinal(plainText.getBytes("UTF-8"));
        
        // Return
        return Base64.encodeBase64String(encryptedTextBytes);
    }
	
    public byte[] encryptData(byte[] plainText, String password) throws Exception
    {  
	// Salt
        this.salt = UTILS.BASIC.hash(password);    
           
        // IV Bytes
        this.ivBytes=UTILS.BASIC.hash(password).substring(0, 16).getBytes();
           
        // Salt
        byte[] saltBytes = salt.getBytes("UTF-8");
         
        // Key factory
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        
        // Key spec
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(),
                                         saltBytes,
                                         pswdIterations,
                                         keySize
                                         );
 
        // Secret
        SecretKey secretKey = factory.generateSecret(spec);
           
        // Secret spec
        SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");
 
        // Cipher
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
           
        // Init
        cipher.init(Cipher.ENCRYPT_MODE, secret, new IvParameterSpec(ivBytes));
          
        // Encrypted
        byte[] encryptedTextBytes = cipher.doFinal(plainText);
        
        // return
        return encryptedTextBytes;
    }
 
    public String decrypt(String encryptedText, String password)  throws Exception
    {
    	 byte[] decryptedTextBytes = null;
    	 
    	// Salt
        this.salt = UTILS.BASIC.hash(password);    
          
        // IV Bytes
        this.ivBytes=UTILS.BASIC.hash(password).substring(0, 16).getBytes();
        
        // Salt
        byte[] saltBytes = salt.getBytes("UTF-8");
        
        // Decode
        byte[] encryptedTextBytes = Base64.decodeBase64(encryptedText);
 
        // Key factory
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
           
        // Key spec
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(),
                                         saltBytes,
                                         pswdIterations,
                                         keySize
                                         );
 
        // Secret key
        SecretKey secretKey = factory.generateSecret(spec);
            
        // Key spec
        SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");
 
        // Cipher
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        
        // Init cipher
        cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(this.ivBytes));
     
        // Decrypt  
        decryptedTextBytes = cipher.doFinal(encryptedTextBytes);
    	
    	// Return
        return new String(decryptedTextBytes);
    }
    
    public byte[] decryptData(byte[] encryptedText, String password) throws Exception 
    {
        // Bytes
    	byte[] decryptedTextBytes = null;
    	 
        // Salt
    	this.salt = UTILS.BASIC.hash(password);    
        
        // IV Bytes
        this.ivBytes=UTILS.BASIC.hash(password).substring(0, 16).getBytes();
            
        // Salt bytes
        byte[] saltBytes = salt.getBytes("UTF-8");
        
        // Encrypted bytes
        byte[] encryptedTextBytes = encryptedText;
 
        // Key factory
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        
        // Key spec
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(),
                                         saltBytes,
                                         pswdIterations,
                                         keySize
                                         );
 
        // Secret key
        SecretKey secretKey = factory.generateSecret(spec);
           
        // Secret
        SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");
 
        // Cipher
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        
        // Init
        cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(this.ivBytes));
     
        // Decrypt  
        decryptedTextBytes = cipher.doFinal(encryptedTextBytes);
    	
    	// Return
        return decryptedTextBytes;
    }

}
