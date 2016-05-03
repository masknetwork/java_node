// Author : Vlad Cristian
// Contact : vcris@gmx.com

// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.kernel;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.ECPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.math.ec.ECPoint;

public class CAddress 
{
	 
    // Public and private key
    public byte[] public_key;
    public byte[] private_key;
    
    // Balance
    public double balance=0;
    
    // Description
    public String description="";
    
    	
 	// Domain
 	public String domain="";
 	
 	
 	ECPublicKey ecPublicKey;
 	ECPrivateKey ecPrivateKey;
 	String curve;
 	
 	 // Can broadcast ?
    boolean canBroadcast=false;
    
    // Last broadcast
    long last_broadcast;
 	
	public CAddress() 
	{
		
	}

	
	public CAddress(String public_key, String private_key, String description, float balance)  throws Exception
	{
            // Key factory
            KeyFactory keyFactory = KeyFactory.getInstance("EC", "BC");
		  
            // Encoded key
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.decodeBase64(public_key));
		  
            // Public key
            this.ecPublicKey = (ECPublicKey) keyFactory.generatePublic(publicKeySpec);
		  
            // Encoded key spec
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(private_key));
		  
            // Private key
            this.ecPrivateKey = (ECPrivateKey) keyFactory.generatePrivate(privateKeySpec);
		
	    // Public key
	    this.public_key=Base64.decodeBase64(public_key);
		
            // Private key
            this.private_key=Base64.decodeBase64(private_key);
	}
	
	public boolean importAddress(String user, 
                                     String public_key, 
                                     String private_key, 
                                     String tag)  throws Exception
	{
		 Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		  KeyFactory keyFactory = KeyFactory.getInstance("EC", "BC");
		  X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.decodeBase64(public_key));
		  this.ecPublicKey = (ECPublicKey) keyFactory.generatePublic(publicKeySpec);
		  PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(private_key));
		  this.ecPrivateKey = (ECPrivateKey) keyFactory.generatePrivate(privateKeySpec);
		
		// Public key
		this.public_key=Base64.decodeBase64(public_key);
		this.private_key=Base64.decodeBase64(private_key);
		this.balance=0;
		this.description=tag;
		
		
		
		// Insert address
	        UTILS.DB.executeUpdate("INSERT INTO my_adr(userID, "
                                                        + "adr, "
                                                        + "description) "
				          + "VALUES('"+UTILS.BASIC.getUserID(user)
                                                        +"', '"+public_key
                                                        +"', '"+UTILS.BASIC.base64_encode(tag)+"')");
		
		this.domain=UTILS.BASIC.domainFromAdr(this.getPublic());
		
		UTILS.WALLET.add(this);
                UTILS.CBLOCK.setSigner();
		
		return true;
	}
	
	
	
	public void generate(String curve) throws Exception
	{
		KeyPairGenerator keyPairGenerator =KeyPairGenerator.getInstance("EC", "BC");
	           ECNamedCurveParameterSpec curveParameterSpec = ECNamedCurveTable.getParameterSpec(curve);
		   keyPairGenerator.initialize(curveParameterSpec, new SecureRandom()); 
		   this.curve=curve;
		   
		   KeyPair KeyPair = keyPairGenerator.generateKeyPair();
		   ecPublicKey = (ECPublicKey) KeyPair.getPublic();
		   ecPrivateKey = (ECPrivateKey) KeyPair.getPrivate();
		   
		   this.public_key=ecPublicKey.getEncoded();
		   this.private_key=ecPrivateKey.getEncoded();
	  
	
     	

	}
	
	public String getPublic() 
	{
        return Base64.encodeBase64String(this.public_key);
    }
	
	public String getPrivate() 
	{
        return Base64.encodeBase64String(this.private_key);
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
		catch (NoSuchAlgorithmException ex) { UTILS.CONSOLE.write(ex.getMessage()); }
		catch (NoSuchPaddingException ex) { UTILS.CONSOLE.write(ex.getMessage()); }
		catch (NoSuchProviderException ex) { UTILS.CONSOLE.write(ex.getMessage()); }
		catch (InvalidKeyException ex) { UTILS.CONSOLE.write(ex.getMessage()); }
		catch (BadPaddingException ex) { UTILS.CONSOLE.write(ex.getMessage()); }
		catch (IllegalBlockSizeException ex) { UTILS.CONSOLE.write(ex.getMessage()); }
		
		return "";
	}
	
	public String decrypt(String data) throws Exception
	{
		try
		{
			byte[] d=Base64.decodeBase64(data);
			javax.crypto.Cipher c = javax.crypto.Cipher.getInstance("ECIESwithAES/DHAES/PKCS7Padding", "BC"); 
			c.init(c.DECRYPT_MODE, this.ecPrivateKey, new SecureRandom());   
			byte[] decrypted=c.doFinal(d, 0, d.length);
			return new String(decrypted);
		}
		catch (NoSuchAlgorithmException ex) { UTILS.CONSOLE.write(ex.getMessage()); }
		catch (NoSuchPaddingException ex) { UTILS.CONSOLE.write(ex.getMessage()); }
		catch (NoSuchProviderException ex) { UTILS.CONSOLE.write(ex.getMessage()); }
		catch (InvalidKeyException ex) { UTILS.CONSOLE.write(ex.getMessage()); }
		catch (BadPaddingException ex) { UTILS.CONSOLE.write(ex.getMessage()); }
		catch (IllegalBlockSizeException ex) { UTILS.CONSOLE.write(ex.getMessage()); }
		
		return "";
	}
	
	public String sign(String data) throws Exception
	{
		try
		{
		   Signature signature = Signature.getInstance("ECDSA", "BC");
		   signature.initSign(this.ecPrivateKey, new SecureRandom());
		   signature.update(data.getBytes());
		   byte[] signed=signature.sign();
		   return Base64.encodeBase64String(signed);
		}
		catch (NoSuchAlgorithmException ex) { UTILS.CONSOLE.write(ex.getMessage()); }
		catch (SignatureException ex) { UTILS.CONSOLE.write(ex.getMessage()); }
		catch (NoSuchProviderException ex) { UTILS.CONSOLE.write(ex.getMessage()); }
		catch (InvalidKeyException ex) { UTILS.CONSOLE.write(ex.getMessage()); }
		
		
		return "";
	}
}