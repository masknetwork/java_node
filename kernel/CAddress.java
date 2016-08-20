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
    
    // Private key
    public byte[] private_key;
    
    // Balance
    public double balance=0;
    
    // Description
    public String description="";
    
    // Public key
    ECPublicKey ecPublicKey;
    
    // PRivate key
    ECPrivateKey ecPrivateKey;
 	
    // Curve
    String curve;
 	
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
	    // Load provider
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		  
            // Key factory
            KeyFactory keyFactory = KeyFactory.getInstance("EC", "BC");
		  
            // Key spec
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.decodeBase64(public_key));
		  
            // Public
            this.ecPublicKey = (ECPublicKey) keyFactory.generatePublic(publicKeySpec);
		  
            // Private
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(private_key));
		  
            // Set private key
            this.ecPrivateKey = (ECPrivateKey) keyFactory.generatePrivate(privateKeySpec);
		
	    // Public key format
	    this.public_key=Base64.decodeBase64(public_key);
	
            // Private key format
            this.private_key=Base64.decodeBase64(private_key);
		
            // Balance
            this.balance=0;
		
            // Description
            this.description=tag;
		
	    // Address exist 
            ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                               + "FROM my_adr "
                                              + "WHERE adr='"+public_key+"'");
		
            if (!UTILS.DB.hasData(rs))
            {
		// Insert address
	        UTILS.DB.executeUpdate("INSERT INTO my_adr SET userID='"+UTILS.BASIC.getUserID(user)+"', "
                                                                + "adr='"+public_key+"', "
                                                                + "description='"+UTILS.BASIC.base64_encode(tag)+"'");
				        
		
		
		// Adds to wallet
		UTILS.WALLET.add(this);
                    
                // Set signer
                UTILS.CBLOCK.setSigner();
            }
                
	    return true;
	}
	
	
	public void generate(String curve) throws Exception
	{
            // Set curve
            this.curve=curve;
            
            // Key generator
	    KeyPairGenerator keyPairGenerator =KeyPairGenerator.getInstance("EC", "BC");
	           
            // Curve specification     
            ECNamedCurveParameterSpec curveParameterSpec = ECNamedCurveTable.getParameterSpec(curve);
		   
            // Init
            keyPairGenerator.initialize(curveParameterSpec, new SecureRandom()); 
	    
            // Pair
            KeyPair KeyPair = keyPairGenerator.generateKeyPair();
		   
            // Pub key
            ecPublicKey = (ECPublicKey) KeyPair.getPublic();
		   
            // Private key
            ecPrivateKey = (ECPrivateKey) KeyPair.getPrivate();
	
            // Encoded
            this.public_key=ecPublicKey.getEncoded();
        
            // Encoded private
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
	    // Chiper
            javax.crypto.Cipher c = javax.crypto.Cipher.getInstance("ECIESwithAES/DHAES/PKCS7Padding", "BC"); 
		   
            // Init
            c.init(c.ENCRYPT_MODE, this.ecPublicKey, new SecureRandom());   
		   
            // Encrypt
            byte[] encrypted=c.doFinal(data.getBytes(), 0, (data.getBytes()).length);
		   
            // Return
            return Base64.encodeBase64String(encrypted);
	}
	
	public String decrypt(String data) throws Exception
	{
            // Decode
	    byte[] d=Base64.decodeBase64(data);
	    
            // Choper
            javax.crypto.Cipher c = javax.crypto.Cipher.getInstance("ECIESwithAES/DHAES/PKCS7Padding", "BC"); 
			
            // Init
            c.init(c.DECRYPT_MODE, this.ecPrivateKey, new SecureRandom());   
			
            // Decrypt
            byte[] decrypted=c.doFinal(d, 0, d.length);
			
            // Return
            return new String(decrypted);
	}
	
	public String sign(String data) throws Exception
	{
	    // Signature
            Signature signature = Signature.getInstance("ECDSA", "BC");
	
            // Init signature
            signature.initSign(this.ecPrivateKey, new SecureRandom());
		   
            // Update
            signature.update(data.getBytes());
		   
            // Sign
            byte[] signed=signature.sign();
		   
            // Return
            return Base64.encodeBase64String(signed);
	}
}