package wallet.network.packets.trans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.kernel.CAddress;
import wallet.kernel.CECC;
import wallet.kernel.UTILS;
import wallet.network.CResult;
import wallet.network.packets.CPayload;
import wallet.network.packets.blocks.CBlockPayload;

public class CMultisigTransSignPayload extends CPayload
{
    // Transaction hash
    String trans_hash;
	
    // Type
    String signature;
	
    public CMultisigTransSignPayload(String trans_hash, 
                                     String signer) 
    {
        super(signer);
			
        // Transaction hash
	this.trans_hash=trans_hash;
		
	// Sign 
	CAddress signer_adr=UTILS.WALLET.getAddress(signer);
        this.signature=signer_adr.sign(trans_hash);
		 
        // Hash
	hash=UTILS.BASIC.hash(this.getHash()+
		              this.trans_hash+
			      this.signature);
		 
	// Sign
        this.sign();
    }
	
    public CResult check(CBlockPayload block)
    {
        try
    	{ 
	    // Super class
	    CResult res=super.check(block);
	    if (res.passed==false) return res;
            
            // Check signature
            CECC ecc=new CECC(this.target_adr);
    	   if (ecc.checkSig(this.trans_hash, this.signature)==false)
    		return new CResult(false, "Invalid signature", "CMultisigTransSignPayload.java", 79);
           
            // Load transaction data
            Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
    	          
            // Finds the user
            ResultSet rs=s.executeQuery("SELECT * "
                                        + "FROM multisig "
                                       + "WHERE trans_hash='"+this.trans_hash+"'");
           
            // Has data
            if (!UTILS.DB.hasData(rs))
               return new CResult(false, "Invalid transaction hash", "CMultisigTransSignPayload.java", 79);
           
            // Next
            rs.next();
                
            // Signers
            String signer_1=rs.getString("signer_1");
            String signer_2=rs.getString("signer_2");
            String signer_3=rs.getString("signer_3");
            String signer_4=rs.getString("signer_4");
            String signer_5=rs.getString("signer_5");
                
            // Signatures
            String sign_1=rs.getString("sign_1");
            String sign_2=rs.getString("sign_2");
            String sign_3=rs.getString("sign_3");
            String sign_4=rs.getString("sign_4");
            String sign_5=rs.getString("sign_5");
                
            // Required
            int req=rs.getInt("required");
                
            // Signer valid ?
            if (!this.target_adr.equals(signer_1) && 
                !this.target_adr.equals(signer_2) && 
                !this.target_adr.equals(signer_3) && 
                !this.target_adr.equals(signer_4) && 
                !this.target_adr.equals(signer_5))
            return new CResult(false, "Invalid transaction hash", "CMultisigTransSignPayload.java", 79);
            
            // Already signed ?
            if ((this.target_adr.equals(signer_1) && sign_1.length()>10) || 
                (this.target_adr.equals(signer_2) && sign_2.length()>10) || 
                (this.target_adr.equals(signer_3) && sign_3.length()>10) || 
                (this.target_adr.equals(signer_4) && sign_4.length()>10) || 
                (this.target_adr.equals(signer_5) && sign_5.length()>10))
            return new CResult(false, "Already signed", "CMultisigTransSignPayload.java", 79);
            
	    // Check hash
	    String h=UTILS.BASIC.hash(this.getHash()+
		                      this.trans_hash+
			              this.signature);
	         
            // Invalid hash
            if (!this.hash.equals(h))
	       return new CResult(false, "Invalid hash", "CMesPayload.java", 79);
	}
    	catch (SQLException ex)
    	{
    	       UTILS.LOG.log("SQLException", ex.getMessage(), "CMesPayload.java", 158);
    	}
        
	// Return
	return new CResult(true, "Ok", "CNewAssetPayload", 67); 
    }

    public CResult commit(CBlockPayload block)
    {
        try
    	{ 
	    // Superclass
	    super.commit(block);
               
            // Load transaction data
            Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
    	          
            // Finds the user
            ResultSet rs=s.executeQuery("SELECT * "
                                        + "FROM multisig "
                                       + "WHERE trans_hash='"+this.trans_hash+"'");
           
            // Has data
            if (!UTILS.DB.hasData(rs))
               return new CResult(false, "Invalid transaction hash", "CMultisigTransSignPayload.java", 79);
           
            // Next
            rs.next();
                
            // Signers
            String signer_1=rs.getString("signer_1");
            String signer_2=rs.getString("signer_2");
            String signer_3=rs.getString("signer_3");
            String signer_4=rs.getString("signer_4");
            String signer_5=rs.getString("signer_5");
                
            // Signatures
            String sign_1=rs.getString("sign_1");
            String sign_2=rs.getString("sign_2");
            String sign_3=rs.getString("sign_3");
            String sign_4=rs.getString("sign_4");
            String sign_5=rs.getString("sign_5");
                
            // Required
            int req=rs.getInt("required");
                
            // Receiver
            String receiver=rs.getString("rec_adr");
                   
            // Currency
            float amount=rs.getFloat("amount");
                   
            // Currency
            String cur=rs.getString("cur");
                   
            // Signer 1
            if (signer_1.equals(this.target_adr))
            {
                // Signer
                sign_1=this.signature;
                            
                UTILS.DB.executeUpdate("UPDATE multisig "
                                        + "SET sign_1='"+this.signature+"' "
                                      + "WHERE trans_hash='"+this.trans_hash+"'");
            }
                
            // Signer 2
            if (signer_2.equals(this.target_adr))
            {
                // Signer
                sign_2=this.signature;
                            
                UTILS.DB.executeUpdate("UPDATE multisig "
                                        + "SET sign_2='"+this.signature+"' "
                                      + "WHERE trans_hash='"+this.trans_hash+"'");
            }
                
            // Signer 3
            if (signer_3.equals(this.target_adr))
            {
                // Signer
                sign_3=this.signature;
                            
                UTILS.DB.executeUpdate("UPDATE multisig "
                                        + "SET sign_3='"+this.signature+"' "
                                      + "WHERE trans_hash='"+this.trans_hash+"'");
            }
                
            // Signer 4
            if (signer_4.equals(this.target_adr))
            {
                // Signer
                sign_4=this.signature;
                            
                UTILS.DB.executeUpdate("UPDATE multisig "
                                        + "SET sign_4='"+this.signature+"' "
                                      + "WHERE trans_hash='"+this.trans_hash+"'");
            }
                
            // Signer 5
            if (signer_5.equals(this.target_adr))
            {
                // Signer
                signer_5=this.target_adr;
                            
                UTILS.DB.executeUpdate("UPDATE multisig "
                                        + "SET sign_5='"+this.signature+"' "
                                      + "WHERE trans_hash='"+this.trans_hash+"'");
            }
                
            // Signed
            int signers_no=0;
                
            // Number of signers
            if (sign_1.length()>25) signers_no++;
            if (sign_2.length()>25) signers_no++;
            if (sign_3.length()>25) signers_no++;
            if (sign_4.length()>25) signers_no++;
            if (sign_5.length()>25) signers_no++;
                
            // Fully signed ?
            if (signers_no>=req)
            {
                // Executes 
                UTILS.BASIC.newTrans(receiver, 
                                    amount,
                                    true,
                                    cur, 
                                    "Transaction has been fully signed", 
                                    "", 
                                    this.hash, 
                                    this.block);
            
                 // Clear transactions
                 UTILS.BASIC.clearTrans(this.hash, "ID_ALL");
               
                 // Delete transaction
                 UTILS.DB.executeUpdate("DELETE FROM multisig "
                                            + "WHERE trans_hash='"+this.trans_hash+"'");
            } 
	}
    	catch (SQLException ex)
    	{
    	    UTILS.LOG.log("SQLException", ex.getMessage(), "CMesPayload.java", 158);
    	}
                
        // Return 
	return new CResult(true, "Ok", "CMesPayload.java", 149);
     }
}
	


