package de.micromata.genome.gwiki.model;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.InitialContext;

import org.junit.Test;

import de.micromata.genome.util.runtime.LocalSettings;
import de.micromata.genome.util.runtime.LocalSettingsEnv;

public class SendEmailTest
{
  @Test
  public void testSendEmail()
  {
    LocalSettings.get();
    LocalSettingsEnv.get();

    SecurityManager security = System.getSecurityManager();
    try {

      Session session = (Session) new InitialContext().lookup("java:/comp/env/genome/mail/Session");
      session.setDebug(true);
      String emailBody = "TestEmail delete this";
      String emailSubject = "Test from de.micromata.genome.gwiki.model.SendEmailTest";
      MimeMessage msg = new MimeMessage(session);
      msg.setText(emailBody);
      msg.setSubject(emailSubject);
      msg.setFrom(new InternetAddress("kommer@artefaktur.com"));
      msg.addRecipient(Message.RecipientType.TO,
          new InternetAddress("kommer@artefaktur.com"));
      Transport.send(msg);
      System.out.println("Message send Successfully:)");
    } catch (Exception mex) {
      mex.printStackTrace();
    }
  }
}
