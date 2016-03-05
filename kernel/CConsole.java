// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.kernel;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.io.FileUtils;
import org.hsqldb.types.Charset;

public class CConsole 
{	
	// To File
	public boolean toFile=false;
	
        // To Screen
        public boolean toScreen=true;
        
        // To db
        public boolean toDB=false;
        
	public CConsole() 
	{
		
	}
	
	public void write(String mes) throws Exception
	{
            // Format message
            Calendar cal = Calendar.getInstance();
	    cal.getTime();
	    SimpleDateFormat sdf = new SimpleDateFormat("MMM,DD,yyyy HH:mm:ss");
	    String ms="("+sdf.format(System.currentTimeMillis())+") "+mes;
            
            try
	    {
	        // Write to screen
                if (this.toScreen) 
                    System.out.println(ms);
		
                // Write to file
                if (this.toFile) 
                    FileUtils.writeStringToFile(new File(UTILS.WRITEDIR+"log.txt"), ms+"\n", true);
                
                // Write to DB
                if (this.toDB && UTILS.DB!=null) 
                    UTILS.DB.executeUpdate("INSERT INTO console (mes, tstamp) "
                                        + "VALUES('"+mes+"', '"+UTILS.BASIC.tstamp()+"')");
	    }
	    catch (IOException ex)
	    {
		UTILS.LOG.log("IOException", ex.getMessage(), "CConsole.java", 28);
	    }
	}
}
