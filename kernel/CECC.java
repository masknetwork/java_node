// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.kernel;


import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.ECPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.interfaces.ECPrivateKey;


public class CECC 
{
	// Public and private key
    public byte[] public_key;
    public byte[] private_key;
    
    // EC public key
 	ECPublicKey ecPublicKey;
 	
 	// EC private key
 	ECPrivateKey ecPrivateKey;
 	
 	// Curve
 	String curve;
 	
 	 // Can broadcast ?
    boolean canBroadcast=false;
    
    // Last broadcast
    long last_broadcast;

    public CECC()
    {
    	
    }
    
    // Construct using public key
    public CECC(String pubKey) throws Exception
    {
    	try
		{
		  Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		  KeyFactory keyFactory = KeyFactory.getInstance("EC", "BC");
		  X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.decodeBase64(pubKey));
		  this.ecPublicKey = (ECPublicKey) keyFactory.generatePublic(publicKeySpec);
		}
		catch (NoSuchAlgorithmException ex) 
		{ 
			UTILS.LOG.log("NoSuchAlgorithmException", ex.getMessage(), "CECC.java", 83); 
		}
		catch (InvalidKeySpecException ex) 
		{ 
			UTILS.LOG.log("InvalidKeySpecException", ex.getMessage(), "CECC.java", 87); 
			UTILS.BASIC.stackTrace();
		}
		catch (NoSuchProviderException ex) 
		{ 
			UTILS.LOG.log("NoSuchProviderException", ex.getMessage(), "CECC.java", 91); 
	    }
		
		// Public key
		this.public_key=Base64.decodeBase64(public_key);
    }
    
    public String encrypt(String data) throws Exception
    {
       try
	   {
	     javax.crypto.Cipher c = javax.crypto.Cipher.getInstance("ECIESwithAES/DHAES/PKCS7Padding", "BC"); 
	     c.init(c.ENCRYPT_MODE, this.ecPublicKey, new SecureRandom());   
	     byte[] encrypted=c.doFinal(data.getBytes(), 0, (data.getBytes()).length);
	     return Base64.encodeBase64String(encrypted);
	   }
	   catch (NoSuchAlgorithmException ex) 
	   { 
		   UTILS.LOG.log("NoSuchAlgorithmException", ex.getMessage(), "CECC.java", 109); 
	   }
	   catch (NoSuchPaddingException ex) 
	   { 
		   UTILS.LOG.log("NoSuchPaddingException", ex.getMessage(), "CECC.java", 113); 
	   }
	   catch (NoSuchProviderException ex) 
	   { 
		   UTILS.LOG.log("NoSuchProviderException", ex.getMessage(), "CECC.java", 117); 
	   }
	   catch (InvalidKeyException ex) 
	   { 
		   UTILS.LOG.log("InvalidKeyException", ex.getMessage(), "CECC.java", 121); 
	   }
	   catch (BadPaddingException ex) 
	   { 
		   UTILS.LOG.log("BadPaddingException", ex.getMessage(), "CECC.java", 125); 
	   }
	   catch (IllegalBlockSizeException ex) 
	   { 
		   UTILS.LOG.log("IllegalBlockSizeException", ex.getMessage(), "CECC.java", 129); 
	   }
	
	return "";
    }
    
    public boolean checkSig(String data, String sig)
	{
		try
		{
		  Signature signature = Signature.getInstance("ECDSA", "BC");	
		  signature.initVerify(this.ecPublicKey);
		  signature.update(data.getBytes());
		
		   if (signature.verify(Base64.decodeBase64(sig)))
			   return true;
		   else
			  return false;
		}
		catch (NoSuchAlgorithmException ex) 
		{ 
			 UTILS.LOG.log("NoSuchAlgorithmException", ex.getMessage(), "CECC.java", 149); 
	    }
		catch (SignatureException ex) 
		{ 
			 UTILS.LOG.log("SignatureException", ex.getMessage(), "CECC.java", 153); 
		}
		catch (NoSuchProviderException ex) 
		{ 
			 UTILS.LOG.log("NoSuchProviderException", ex.getMessage(), "CECC.java", 157); 
		}
		catch (InvalidKeyException ex) 
		{ 
			 UTILS.LOG.log("InvalidKeyException", ex.getMessage(), "CECC.java", 161); 
		}
		
		return false;
	}
}