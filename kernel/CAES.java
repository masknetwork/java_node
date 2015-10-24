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
    private static String salt;
    private static int pswdIterations = 65536  ;
    private static int keySize = 128;
    private byte[] ivBytes;
    
	public String generateSalt() 
	{
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[20];
        random.nextBytes(bytes);
        String s = new String(bytes);
        return s;
    }
	
	public CAES()
	{
		
	}
	
	public String encrypt(String plainText, String password) 
	{  
		try
		{
           this.salt = UTILS.BASIC.hash(password);    
           this.ivBytes=UTILS.BASIC.hash(password).substring(0, 16).getBytes();
           
           byte[] saltBytes = salt.getBytes("UTF-8");
         
           // Derive the key
           SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
           PBEKeySpec spec = new PBEKeySpec(password.toCharArray(),
                                            saltBytes,
                                            pswdIterations,
                                            keySize
                                            );
 
           SecretKey secretKey = factory.generateSecret(spec);
           SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");
 
           // Encrypt
           Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
           cipher.init(Cipher.ENCRYPT_MODE, secret, new IvParameterSpec(ivBytes));
          
           
           byte[] encryptedTextBytes = cipher.doFinal(plainText.getBytes("UTF-8"));
           return Base64.encodeBase64String(encryptedTextBytes);
		}
		catch (UnsupportedEncodingException ex)
		{
			UTILS.LOG.log("UnsupportedEncodingException", ex.getMessage(), "CAES.java", 69);
		}
		catch (NoSuchAlgorithmException ex)
		{
			UTILS.LOG.log("NoSuchAlgorithmException", ex.getMessage(), "CAES.java", 69);
		}
		catch (InvalidKeySpecException ex)
		{
			UTILS.LOG.log("InvalidKeySpecException", ex.getMessage(), "CAES.java", 69);
		}
		catch (NoSuchPaddingException ex)
		{
			UTILS.LOG.log("NoSuchPaddingException", ex.getMessage(), "CAES.java", 69);
		}
		catch (InvalidAlgorithmParameterException ex)
		{
			UTILS.LOG.log("InvalidParameterSpecException", ex.getMessage(), "CAES.java", 69);
		}
		catch (InvalidKeyException ex)
		{
			UTILS.LOG.log("InvalidKeyException", ex.getMessage(), "CAES.java", 69);
		}
		
		catch (BadPaddingException ex)
		{
			UTILS.LOG.log("BadPaddingException", ex.getMessage(), "CAES.java", 69);
		}
		catch (IllegalBlockSizeException ex)
		{
			UTILS.LOG.log("IllegalBlockSizeException", ex.getMessage(), "CAES.java", 69);
		}
		
		return "";
    }
	
	public byte[] encryptData(byte[] plainText, String password) 
	{  
		try
		{
           this.salt = UTILS.BASIC.hash(password);    
           this.ivBytes=UTILS.BASIC.hash(password).substring(0, 16).getBytes();
           
           byte[] saltBytes = salt.getBytes("UTF-8");
         
           // Derive the key
           SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
           PBEKeySpec spec = new PBEKeySpec(password.toCharArray(),
                                            saltBytes,
                                            pswdIterations,
                                            keySize
                                            );
 
           SecretKey secretKey = factory.generateSecret(spec);
           SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");
 
           // Encrypt
           Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
           cipher.init(Cipher.ENCRYPT_MODE, secret, new IvParameterSpec(ivBytes));
          
           
           byte[] encryptedTextBytes = cipher.doFinal(plainText);
           return encryptedTextBytes;
		}
		catch (UnsupportedEncodingException ex)
		{
			UTILS.LOG.log("UnsupportedEncodingException", ex.getMessage(), "CAES.java", 69);
		}
		catch (NoSuchAlgorithmException ex)
		{
			UTILS.LOG.log("NoSuchAlgorithmException", ex.getMessage(), "CAES.java", 69);
		}
		catch (InvalidKeySpecException ex)
		{
			UTILS.LOG.log("InvalidKeySpecException", ex.getMessage(), "CAES.java", 69);
		}
		catch (NoSuchPaddingException ex)
		{
			UTILS.LOG.log("NoSuchPaddingException", ex.getMessage(), "CAES.java", 69);
		}
		catch (InvalidAlgorithmParameterException ex)
		{
			UTILS.LOG.log("InvalidParameterSpecException", ex.getMessage(), "CAES.java", 69);
		}
		catch (InvalidKeyException ex)
		{
			UTILS.LOG.log("InvalidKeyException", ex.getMessage(), "CAES.java", 69);
		}
		
		catch (BadPaddingException ex)
		{
			UTILS.LOG.log("BadPaddingException", ex.getMessage(), "CAES.java", 69);
		}
		catch (IllegalBlockSizeException ex)
		{
			UTILS.LOG.log("IllegalBlockSizeException", ex.getMessage(), "CAES.java", 69);
		}
		
		return "".getBytes();
    }
 
    public String decrypt(String encryptedText, String password) 
    {
    	 byte[] decryptedTextBytes = null;
    	 
    	try
    	{
          this.salt = UTILS.BASIC.hash(password);    
          this.ivBytes=UTILS.BASIC.hash(password).substring(0, 16).getBytes();
             
           byte[] saltBytes = salt.getBytes("UTF-8");
           byte[] encryptedTextBytes = Base64.decodeBase64(encryptedText);
 
           // Derive the key
           SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
           PBEKeySpec spec = new PBEKeySpec(password.toCharArray(),
                                            saltBytes,
                                            pswdIterations,
                                            keySize
                                             );
 
            SecretKey secretKey = factory.generateSecret(spec);
            SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");
 
            // Decrypt the message
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(this.ivBytes));
     
          
            decryptedTextBytes = cipher.doFinal(encryptedTextBytes);
    	}
    	catch (UnsupportedEncodingException ex)
		{
			UTILS.LOG.log("UnsupportedEncodingException", ex.getMessage(), "CAES.java", 69);
		}
    	catch (NoSuchAlgorithmException ex)
		{
			UTILS.LOG.log("NoSuchAlgorithmException", ex.getMessage(), "CAES.java", 69);
		}
    	catch (InvalidKeySpecException ex)
		{
			UTILS.LOG.log("InvalidKeySpecException", ex.getMessage(), "CAES.java", 69);
		}
    	catch (NoSuchPaddingException ex)
		{
			UTILS.LOG.log("NoSuchPaddingException", ex.getMessage(), "CAES.java", 69);
		}
    	catch (InvalidAlgorithmParameterException ex)
		{
			UTILS.LOG.log("NoSuchPaddingException", ex.getMessage(), "CAES.java", 69);
		}
    	catch (InvalidKeyException ex)
		{
			UTILS.LOG.log("InvalidKeyException", ex.getMessage(), "CAES.java", 69);
		}
    	catch (BadPaddingException ex)
		{
			UTILS.LOG.log("BadPaddingException", ex.getMessage(), "CAES.java", 69);
		}
		catch (IllegalBlockSizeException ex)
		{
			UTILS.LOG.log("IllegalBlockSizeException", ex.getMessage(), "CAES.java", 69);
		}
    	
        return new String(decryptedTextBytes);
    }
    
    public byte[] decryptData(byte[] encryptedText, String password) 
    {
    	 byte[] decryptedTextBytes = null;
    	 
    	try
    	{
          this.salt = UTILS.BASIC.hash(password);    
          this.ivBytes=UTILS.BASIC.hash(password).substring(0, 16).getBytes();
             
           byte[] saltBytes = salt.getBytes("UTF-8");
           byte[] encryptedTextBytes = encryptedText;
 
           // Derive the key
           SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
           PBEKeySpec spec = new PBEKeySpec(password.toCharArray(),
                                            saltBytes,
                                            pswdIterations,
                                            keySize
                                             );
 
            SecretKey secretKey = factory.generateSecret(spec);
            SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");
 
            // Decrypt the message
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(this.ivBytes));
     
          
            decryptedTextBytes = cipher.doFinal(encryptedTextBytes);
    	}
    	catch (UnsupportedEncodingException ex)
		{
			UTILS.LOG.log("UnsupportedEncodingException", ex.getMessage(), "CAES.java", 69);
		}
    	catch (NoSuchAlgorithmException ex)
		{
			UTILS.LOG.log("NoSuchAlgorithmException", ex.getMessage(), "CAES.java", 69);
		}
    	catch (InvalidKeySpecException ex)
		{
			UTILS.LOG.log("InvalidKeySpecException", ex.getMessage(), "CAES.java", 69);
		}
    	catch (NoSuchPaddingException ex)
		{
			UTILS.LOG.log("NoSuchPaddingException", ex.getMessage(), "CAES.java", 69);
		}
    	catch (InvalidAlgorithmParameterException ex)
		{
			UTILS.LOG.log("NoSuchPaddingException", ex.getMessage(), "CAES.java", 69);
		}
    	catch (InvalidKeyException ex)
		{
			UTILS.LOG.log("InvalidKeyException", ex.getMessage(), "CAES.java", 69);
		}
    	catch (BadPaddingException ex)
		{
			UTILS.LOG.log("BadPaddingException", ex.getMessage(), "CAES.java", 69);
		}
		catch (IllegalBlockSizeException ex)
		{
			UTILS.LOG.log("IllegalBlockSizeException", ex.getMessage(), "CAES.java", 69);
		}
    	
        return decryptedTextBytes;
    }

}
