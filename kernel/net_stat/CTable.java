package wallet.kernel.net_stat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import org.apache.commons.io.FileUtils;
import wallet.kernel.UTILS;

public class CTable 
{
    // Name
    public String name;
    
    // Hash
    public String hash;
    
    // Json
    public String json;
    
    public CTable(String name)
    {
       // Name
       this.name=name;
       
       // Json
       this.json="{\"table\" : \""+name+"\", \"rows\" : [";
    }
    
    public void fromDB() throws Exception
    {
        
    }
    
    public void fromJSON() throws Exception
    {
        
    }
    
    public void flush() throws Exception
    {
        File fout = new File(UTILS.WRITEDIR+"tables/"+this.name+".table");
        FileUtils.writeByteArrayToFile(fout, UTILS.BASIC.compress(this.json.getBytes())); 
        System.out.println("Done");
    }
    
    
    public void addRow(String col, long val)
    {
        this.json=this.json+"{\"adr\" : \""+val+"\", ";
    }
}
