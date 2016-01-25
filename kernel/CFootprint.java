package wallet.kernel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CFootprint 
{
    // Packet type
    String packet_type;
    
    // Packet hash
    String packet_hash;
    
    // Payload hash
    String payload_hash;
    
    // Payload names
    String[] names;
    
    // Payload values
    String values[];
    
    // Fee source
    String fee_src;
    
    // Fee amount
    double fee_amount;
    
    // Fee hash
    String fee_hash;
    
    // Block
    long block;
    
    // No
    int no;
    
    public  CFootprint(String packet_type, 
                       String packet_hash, 
                       String payload_hash,
                       String fee_src,
                       double fee_amount,
                       String fee_hash,
                       long block)
    {
       // Packet type
       this.packet_type=packet_type;
        
       // Packet hash
       this.packet_hash=packet_hash;
       
       // Payload hash
       this.payload_hash=payload_hash;
       
       // Fee source
       this.fee_src=fee_src;
       
       // Fee amount
       this.fee_amount=fee_amount;
       
       // Fee hash
       this.fee_hash=fee_hash;
       
       // Block
       this.block=block;
       
       // Names
       this.names=new String[30];
       
       // Values
       this.values=new String[30];
       
       // Init
       for (int a=0; a<=29; a++) this.values[a]="";
       for (int a=0; a<=29; a++) this.names[a]="";
       
       // Number
       no=0;
    }
    
    public void add(String name, String val)
    {
        // Increase size
        no++;
        
        // Insert name
        this.names[no]=name;
        
        // Insert value
        this.values[no]=val;
    }
    
    public void write()
    {
        try
        {
    	   // Statement
           Statement s=UTILS.DB.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, 
                                                    ResultSet.CONCUR_READ_ONLY);
           
           // Already exist
           ResultSet rs=s.executeQuery("SELECT * "
                                       + "FROM footprints "
                                      + "WHERE packet_hash='"+this.packet_hash+"'");
           
           // Load address data
           if (!UTILS.DB.hasData(rs))
           s.executeUpdate("INSERT INTO footprints(packet_type, "
                                                 + "packet_hash, "
                                                 + "payload_hash, "
                                                 + "fee_src, "
                                                 + "fee_amount, "
                                                 + "fee_hash, "
                                                 + "par_1_name, "
                                                 + "par_1_val, "
                                                 + "par_2_name, "
                                                 + "par_2_val, "
                                                 + "par_3_name, "
                                                 + "par_3_val, "
                                                 + "par_4_name, "
                                                 + "par_4_val, "
                                                 + "par_5_name, "
                                                 + "par_5_val,"
                                                 + "block,"
                                                 + "tstamp) VALUES ('"
                                                 + this.packet_type+"', '"
                                                 + this.packet_hash+"', '"
                                                 + this.payload_hash+"', '"
                                                 + this.fee_src+"', '"
                                                 + this.fee_amount+"', '"
                                                 + this.fee_hash+"', '"
                                                 + this.names[1]+"', '"
                                                 + UTILS.BASIC.base64_encode(this.values[1])+"', '"
                                                 + this.names[2]+"', '"
                                                 + UTILS.BASIC.base64_encode(this.values[2])+"', '"
                                                 + this.names[3]+"', '"
                                                 + UTILS.BASIC.base64_encode(this.values[3])+"', '"
                                                 + this.names[4]+"', '"
                                                 + UTILS.BASIC.base64_encode(this.values[4])+"', '"
                                                 + this.names[5]+"', '"
                                                 + UTILS.BASIC.base64_encode(this.values[5])+"', '"
                                                 + this.block+"', '"
                                                 +UTILS.BASIC.tstamp()+"')");
           
           // Close
           s.close();
           
        }
        catch (SQLException ex) 
       	{  
            UTILS.LOG.log("SQLException", ex.getMessage(), "CFootprint.java", 57);
        }
    }
}
