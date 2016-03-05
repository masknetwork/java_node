// Author : Vlad Cristian
// Contact : vcris@gmx.com

package wallet.kernel;

import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class CEmail 
{
	public CEmail() 
	{
	}
	
	public void sendEmail(String to, String subj, String header, String mes, String footer)
	{
		  // Sender's email ID needs to be mentioned
	      String from = "anonymous@anonymous.com";

	      // Assuming you are sending email from localhost
	      String host = "localhost";

	      // Get system properties
	      Properties properties = System.getProperties();

	      // Setup mail server
	      properties.setProperty("mail.smtp.host", host);
	      properties.put("mail.smtp.port", "25");
	      properties.put("mail.smtp.connectiontimeout", 10000);

	      // Get the default Session object.
	      Session session = Session.getDefaultInstance(properties);

	      try
	      {
	    	// Create a default MimeMessage object.
	          MimeMessage message = new MimeMessage(session);

	          // Set From: header field of the header.
	          message.setFrom(new InternetAddress(from));

	          // Set To: header field of the header.
	          message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

	          // Set Subject: header field
	          message.setSubject(subj);

	         // Fill the message
	         String html="<table width=\"800\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr>"+
                        "<td height=\"10\"><a href=\"http://www.masknetwork.com\" target=\"_blank\"><img style=\"display:block\" src=\"http://www.masknetwork.com/email_top.png\"/></a></td></tr><tr><td align=\"center\" background=\"http://www.masknetwork.com/email_middle.png\" height=\"100%\"><table width=\"750\" border=\"0\" cellspacing=\"0\" cellpadding=\"5\">"+
                         "<tr><td height=\"35\" valign=\"top\" style=\"font:'Times New Roman', Times, serif; color:#999999; font-size:16px;\">"+header+"</td>"+
                         "</tr><tr><td height=\"200\" valign=\"top\" bgcolor=\"#ffffff\" style=\"font:'Times New Roman', Times, serif; color:#000000; font-size:18px; color:#2d3740\">&quot;"+mes+"&quot;</td>"+
                         "</tr><tr><td height=\"40\" valign=\"bottom\" style=\"font:Arial, Helvetica, sans-serif; font-size:10px; color:#555555\">"+footer+"</td>"+
                         "</tr></table></td></tr><tr><td width=\"0\" height=\"0\"><img style=\"display:block\" src=\"http://www.masknetwork.com/email_bottom.png\" width=\"800\" height=\"10\" /></td></tr</table>";
                          		
	         // Send the actual HTML message, as big as you like
	          message.setContent(html, "text/html" );
	         
	         // Send message
	         Transport.send(message);
	         
	 
	      }
	      catch (MessagingException ex) 
	      {
	         UTILS.LOG.log("MessagingException", ex.getMessage(), "CEmail", 86);
	      }
	}
	
	public void sendBackup(String to)
	{
		 // Sender's email ID needs to be mentioned
	      String from = "noreply@anonymous.com";

	      // Assuming you are sending email from localhost
	      String host = "localhost";

	      // Get system properties
	      Properties properties = System.getProperties();

	      // Setup mail server
	      properties.setProperty("mail.smtp.host", host);
	      properties.put("mail.smtp.port", "25");
	      properties.put("mail.smtp.connectiontimeout", 10000);

	      // Get the default Session object.
	      Session session = Session.getDefaultInstance(properties);

	      try{
	         // Create a default MimeMessage object.
	         MimeMessage message = new MimeMessage(session);

	         // Set From: header field of the header.
	         message.setFrom(new InternetAddress(from));

	         // Set To: header field of the header.
	         message.addRecipient(Message.RecipientType.TO,
	                                  new InternetAddress(to));

	         // Set Subject: header field
	         message.setSubject("You MaskWallet backup");

	         // Create the message part 
	         BodyPart messageBodyPart = new MimeBodyPart();

	         // Fill the message
	         String mes="<table width=\"800\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">"+
                        "<tr><td height=\"500\" align=\"center\" valign=\"top\" background=\"http://www.masknetwork.com/email_backup.jpg\"><table width=\"750\" border=\"0\" cellspacing=\"0\" cellpadding=\"5\">"+
                        "<tr><td width=\"335\" height=\"120\">&nbsp;</td><td width=\"395\">&nbsp;</td></tr><tr><td>&nbsp;</td>"+
                        "<td align=\"left\" style=\"font-family:Verdana, Geneva, sans-serif; font-size:26px; color:#ffffff\">Hi, i am your wallet</td>"+
                        "</tr><tr><td>&nbsp;</td><td align=\"left\" style=\"font-family: Verdana, Geneva, sans-serif; font-size:16px; color:#f0f0f0\">I'm sending you this email because you have requested an email backup of your MaskWallet. Attached to this email is an AES encrypted wallet backup which contains everything you need to restore your MaskCoin balance.</td>"+
                        "</tr><tr><td>&nbsp;</td><td align=\"left\">&nbsp;</td></tr><tr><td>&nbsp;</td><td align=\"left\" style=\"font-family: Verdana, Geneva, sans-serif; font-size:12px; color:#aaaaaa\">In order to restore your wallet, import the attached file or copy the file to your wallet directory (for windows the wallet location is ApplicationData/MaskWallet, for Macos is /your username/MaskWallet, for unix is /your usrname/MaskWallet).</td>"+
                        "</tr><tr><td height=\"85\" colspan=\"2\" valign=\"bottom\"><table width=\"710\" border=\"0\" align=\"right\" cellpadding=\"0\" cellspacing=\"0\"><tr>"+
                        "<td style=\"font-family: Verdana, Geneva, sans-serif; font-size:10px; color:#aaaaaa\">This email is not spam. You receive this email because you have requested a wallet backup. If  you don't want to receive this emails anymore, open your MaskWalleet, go to settings / automated backup and uncheck 'Send me automated wallet backups'</td>"+
                        "</tr></table></td></tr></table></td> </tr></table>";
	        
                 messageBodyPart.setContent(mes, "text/html" );
	         
	         // Create a multipar message
	         Multipart multipart = new MimeMultipart();

	         // Set text message part
	         multipart.addBodyPart(messageBodyPart);

	         // Part two is attachment
	         messageBodyPart = new MimeBodyPart();
	         String filename = UTILS.WRITEDIR+"wallet.msk";
	         DataSource source = new FileDataSource(filename);
	         messageBodyPart.setDataHandler(new DataHandler(source));
	         messageBodyPart.setFileName(filename);
	         multipart.addBodyPart(messageBodyPart);

	         // Send the complete message parts
	         message.setContent(multipart );

	         // Send message
	         Transport.send(message);
	 
	      }
	      catch (MessagingException ex) 
	      {
	         UTILS.LOG.log("MessagingException", ex.getMessage(), "CEmail.java", 151);
	      }
	}

}