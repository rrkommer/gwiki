////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2010 Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
////////////////////////////////////////////////////////////////////////////

package de.micromata.genome.gwiki.model;

import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.StringUtils;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 * Standard implementation for a GWikiEmailProvider.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiStandardEmailProvider implements GWikiEmailProvider
{
  private static final String DEFAULT_MAIL_ENCODING = "ISO-8859-1";

  public void sendEmail(Map<String, String> ctx)
  {
    try {
      sendEmailImpl(ctx);
      GWikiLog.note("Send email: " + ctx.get(TO), GLogAttributeNames.EmailMessage, ctx.toString());
    } catch (MessagingException ex) {
      GWikiLog.warn("Fail to send email: " + ctx.get(TO) + ": " + ex.getMessage(), ex, GLogAttributeNames.EmailMessage, ctx.toString());
    }
  }

  public void sendEmailImpl(Map<String, String> ctx) throws MessagingException
  {
    Map<String, String> headers = new HashMap<String, String>();
    if (ctx.containsKey(TO) == true) {
      headers.put(TO, (String) ctx.get(TO));
    }
    if (ctx.containsKey(FROM) == true) {
      headers.put(FROM, (String) ctx.get(FROM));
    }
    if (ctx.containsKey(CC) == true) {
      headers.put(CC, (String) ctx.get(CC));
    }
    if (ctx.containsKey(BCC) == true) {
      headers.put(BCC, (String) ctx.get(BCC));
    }
    if (ctx.containsKey(SUBJECT) == true) {
      headers.put(SUBJECT, (String) ctx.get(SUBJECT));
    }
    Session emailSession = GWikiWeb.get().getDaoContext().getMailSession();
    MimeMessage message = new MimeMessage(emailSession);
    boolean multipart = false;
    String encoding = DEFAULT_MAIL_ENCODING;
    MimeMessageHelper mh = new MimeMessageHelper(message, multipart, encoding);
    setHeaders(mh, headers);
    mh.setText(ctx.get(TEXT));
    MimeMessage m = mh.getMimeMessage();
    Transport.send(m);

  }

  private void setHeaders(MimeMessageHelper helper, Map<String, String> mth) throws MessagingException
  {

    String to = mth.get(TO);
    if (to == null) {
      throw new MessagingException("No recipient given");
    }

    helper.setTo(StringUtils.split(to, ","));

    if (mth.containsKey(FROM)) {
      helper.setFrom(mth.get(FROM));
    }

    if (mth.containsKey(SUBJECT)) {
      helper.setSubject(mth.get(SUBJECT));
    }

    String cc = mth.get(CC);
    if (cc != null) {
      helper.setCc(StringUtils.split(cc, ","));
    }

    String bcc = mth.get(BCC);
    if (bcc != null) {
      helper.setBcc(StringUtils.split(bcc, ","));
    }
  }

}
