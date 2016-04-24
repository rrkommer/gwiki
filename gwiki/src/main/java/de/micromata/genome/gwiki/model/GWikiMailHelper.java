package de.micromata.genome.gwiki.model;

import java.io.UnsupportedEncodingException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.StringUtils;

/**
 * Little helper to set email fields
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class GWikiMailHelper
{
  private MimeMessage mimeMessage;
  private String charset;

  private boolean multipart;

  public GWikiMailHelper(MimeMessage mimeMessage, boolean multipart, String charset)
  {

    this.mimeMessage = mimeMessage;
    this.multipart = multipart;
    this.charset = charset;
  }

  public InternetAddress[] parseAddresses(String adressesString) throws MessagingException
  {
    String[] addresses = StringUtils.split(adressesString, ",");
    InternetAddress[] iaddresses = new InternetAddress[addresses.length];
    for (int i = 0; i < addresses.length; ++i) {
      String sa = addresses[i];
      InternetAddress[] parsed = InternetAddress.parse(sa);
      if (parsed.length != 1) {
        throw new AddressException("Illegal address", sa);
      }
      InternetAddress raw = parsed[0];
      try {
        iaddresses[i] = (charset != null ? new InternetAddress(raw.getAddress(), raw.getPersonal(), charset.toString())
            : raw);
      } catch (UnsupportedEncodingException ex) {
        throw new MessagingException("Failed to parse embedded personal name to correct encoding", ex);
      }
    }
    return iaddresses;
  }

  public void setFrom(String fromAddresses) throws MessagingException
  {
    InternetAddress[] iaddr = parseAddresses(fromAddresses);
    if (iaddr.length != 1) {
      throw new MessagingException("From allows only one address: " + fromAddresses);
    }
    mimeMessage.setFrom(iaddr[0]);
  }

  public void setTo(String fromAddresses) throws MessagingException
  {
    InternetAddress[] iaddr = parseAddresses(fromAddresses);
    mimeMessage.addRecipients(Message.RecipientType.TO, iaddr);
  }

  public void setCc(String fromAddresses) throws MessagingException
  {
    InternetAddress[] iaddr = parseAddresses(fromAddresses);
    mimeMessage.addRecipients(Message.RecipientType.CC, iaddr);
  }

  public void setBcc(String fromAddresses) throws MessagingException
  {
    InternetAddress[] iaddr = parseAddresses(fromAddresses);
    mimeMessage.addRecipients(Message.RecipientType.BCC, iaddr);
  }

  public void setSubject(String subject) throws MessagingException
  {
    if (charset != null) {
      mimeMessage.setSubject(subject, charset.toString());
    } else {
      mimeMessage.setSubject(subject);
    }
  }

  public void setBody(String text) throws MessagingException
  {
    if (multipart) {
      throw new MessagingException("Multimime currently not supported");
    }
    mimeMessage.setText(text, charset);
  }
}
