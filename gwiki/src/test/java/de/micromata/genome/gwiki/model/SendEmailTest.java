//
// Copyright (C) 2010-2016 Roger Rene Kommer & Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

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
