// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.kernel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import wallet.network.packets.CBroadcastPacket;
import wallet.network.packets.CPayload;
import wallet.network.packets.adr.CProfilePayload;

public class CPackets 
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
    String fee_amount;
    
    // Fee hash
    String fee_hash;
    
    // Payload size
    long payload_size;
    
    // Block
    long block;
    
    // No
    int no;
    
    public  CPackets(CBroadcastPacket packet) throws Exception
    {
       // Packet type
       this.packet_type=packet.tip;
        
       // Packet hash
       this.packet_hash=packet.hash;
       
       // Payload hash
       CPayload payload=(CPayload) UTILS.SERIAL.deserialize(packet.payload);
       this.payload_hash=payload.hash;
       
       // Fee source
       this.fee_src=packet.fee.src;
       
       // Fee amount
       this.fee_amount=UTILS.FORMAT_8.format(packet.fee.amount);
       
       // Fee hash
       this.fee_hash=packet.fee.hash;
       
       // Payload size
       this.payload_size=packet.payload.length;
       
       // Block
       this.block=packet.block;
       
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
    
    public void add(String name, long val)
    {
        // Increase size
        no++;
        
        // Insert name
        this.names[no]=name;
        
        // Insert value
        this.values[no]=String.valueOf(val);
    }
    
    public void add(String name, double val)
    {
        // Increase size
        no++;
        
        // Insert name
        this.names[no]=name;
        
        // Insert value
        this.values[no]=String.valueOf(val);
    }
    
    public void write() throws Exception
    {
        // Already exist
        ResultSet rs=UTILS.DB.executeQuery("SELECT * "
                                       + "FROM packets "
                                      + "WHERE packet_hash='"+this.packet_hash+"'");
           
        // Load address data
        if (!UTILS.DB.hasData(rs))
        UTILS.DB.executeUpdate("INSERT INTO packets "
                                     + "SET packet_type='"+this.packet_type+"', "
                                         + "packet_hash='"+this.packet_hash+"', "
                                         + "payload_hash='"+this.payload_hash+"', "
                                         + "fee_src='"+this.fee_src+"', "
                                         + "fee_amount='"+this.fee_amount+"', "
                                         + "fee_hash='"+this.fee_hash+"', "
                                         + "payload_size='"+this.payload_size+"', "
                                         + "par_1_name='"+this.names[1]+"', "
                                         + "par_1_val='"+UTILS.BASIC.base64_encode(this.values[1])+"', "
                                         + "par_2_name='"+this.names[2]+"', "
                                         + "par_2_val='"+UTILS.BASIC.base64_encode(this.values[2])+"', "
                                         + "par_3_name='"+this.names[3]+"', "
                                         + "par_3_val='"+UTILS.BASIC.base64_encode(this.values[3])+"', "
                                         + "par_4_name='"+this.names[4]+"', "
                                         + "par_4_val='"+UTILS.BASIC.base64_encode(this.values[4])+"', "
                                         + "par_5_name='"+this.names[5]+"', "
                                         + "par_5_val='"+UTILS.BASIC.base64_encode(this.values[5])+"', "
                                         + "par_6_name='"+this.names[6]+"', "
                                         + "par_6_val='"+UTILS.BASIC.base64_encode(this.values[6])+"', "
                                         + "par_7_name='"+this.names[7]+"', "
                                         + "par_7_val='"+UTILS.BASIC.base64_encode(this.values[7])+"', "
                                         + "par_8_name='"+this.names[8]+"', "
                                         + "par_8_val='"+UTILS.BASIC.base64_encode(this.values[8])+"', "
                                         + "par_9_name='"+this.names[9]+"', "
                                         + "par_9_val='"+UTILS.BASIC.base64_encode(this.values[9])+"', "
                                         + "par_10_name='"+this.names[10]+"', "
                                         + "par_10_val='"+UTILS.BASIC.base64_encode(this.values[10])+"', "
                                         + "par_11_name='"+this.names[11]+"', "
                                         + "par_11_val='"+UTILS.BASIC.base64_encode(this.values[11])+"', "
                                         + "par_12_name='"+this.names[12]+"', "
                                         + "par_12_val='"+UTILS.BASIC.base64_encode(this.values[12])+"', "
                                         + "par_13_name='"+this.names[13]+"', "
                                         + "par_13_val='"+UTILS.BASIC.base64_encode(this.values[13])+"', "
                                         + "par_14_name='"+this.names[14]+"', "
                                         + "par_14_val='"+UTILS.BASIC.base64_encode(this.values[14])+"', "
                                         + "par_15_name='"+this.names[15]+"', "
                                         + "par_15_val='"+UTILS.BASIC.base64_encode(this.values[15])+"', "
                                         + "par_16_name='"+this.names[16]+"', "
                                         + "par_16_val='"+UTILS.BASIC.base64_encode(this.values[16])+"', "
                                         + "par_17_name='"+this.names[17]+"', "
                                         + "par_17_val='"+UTILS.BASIC.base64_encode(this.values[17])+"', "
                                         + "par_18_name='"+this.names[18]+"', "
                                         + "par_18_val='"+UTILS.BASIC.base64_encode(this.values[18])+"', "
                                         + "par_19_name='"+this.names[19]+"', "
                                         + "par_19_val='"+UTILS.BASIC.base64_encode(this.values[19])+"', "
                                         + "par_20_name='"+this.names[20]+"', "
                                         + "par_20_val='"+UTILS.BASIC.base64_encode(this.values[20])+"', "
                                         + "block='"+this.block+"',"
                                         + "tstamp='"+UTILS.BASIC.tstamp()+"'"); 
    }
}
