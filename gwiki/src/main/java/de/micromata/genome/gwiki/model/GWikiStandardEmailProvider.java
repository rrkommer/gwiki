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

import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;

import de.micromata.genome.gwiki.model.logging.GWikiLog;
import de.micromata.genome.gwiki.model.logging.GWikiLogCategory;
import de.micromata.genome.gwiki.page.GWikiStandaloneContext;
import de.micromata.genome.logging.GLog;
import de.micromata.genome.logging.GenomeAttributeType;
import de.micromata.genome.logging.LogAttribute;
import de.micromata.genome.logging.LogExceptionAttribute;

/**
 * Standard implementation for a GWikiEmailProvider.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiStandardEmailProvider implements GWikiEmailProvider
{
  private static final String DEFAULT_MAIL_ENCODING = "ISO-8859-1";

  @Override
  public void sendEmail(Map<String, String> ctx)
  {
    try {
      if (ctx.containsKey("MAILTEMPLATE") == true) {
        evaluateMailTemplate(ctx.get("MAILTEMPLATE"), ctx);
      }
      sendEmailImpl(ctx);
      GLog.note(GWikiLogCategory.Wiki, "Send email: " + ctx.get(TO),
          new LogAttribute(GenomeAttributeType.EmailMessage, ctx.toString()));
    } catch (MessagingException ex) {
      ctx.put(SENDEMAILFAILED, ex.getMessage());
      GLog.warn(GWikiLogCategory.Wiki, "Fail to send email: " + ctx.get(TO) + ": " + ex.getMessage(),
          new LogExceptionAttribute(ex), new LogAttribute(GenomeAttributeType.EmailMessage,
              ctx.toString()));
    }
  }

  protected void evaluateMailTemplate(String mailTemplate, Map<String, String> ctx)
  {
    GWikiStandaloneContext wikiContext = GWikiStandaloneContext.create();
    wikiContext.setRequestAttribute("mailContext", ctx);

    for (Map.Entry<String, String> me : ctx.entrySet()) {
      wikiContext.setRequestAttribute(me.getKey(), me.getValue());
    }
    GWikiElement el = wikiContext.getWikiWeb().getElement(mailTemplate);
    el.serve(wikiContext);
    wikiContext.flush();
    String text = wikiContext.getOutString();
    for (Map.Entry<String, Object> me : wikiContext.getRequestAttributes().entrySet()) {
      if (me.getValue() instanceof String) {
        ctx.put(me.getKey(), (String) me.getValue());
      }
    }
    text = StringUtils.trimToEmpty(text);
    if (StringUtils.isNotBlank(text) == true) {
      ctx.put(TEXT, text);
    }
  }

  public void sendEmailImpl(Map<String, String> ctx) throws MessagingException
  {
    Map<String, String> headers = new HashMap<String, String>();
    if (ctx.containsKey(TO) == true) {
      headers.put(TO, ctx.get(TO));
    }
    if (ctx.containsKey(FROM) == true) {
      headers.put(FROM, ctx.get(FROM));
    }
    if (ctx.containsKey(CC) == true) {
      headers.put(CC, ctx.get(CC));
    }
    if (ctx.containsKey(BCC) == true) {
      headers.put(BCC, ctx.get(BCC));
    }
    if (ctx.containsKey(SUBJECT) == true) {
      headers.put(SUBJECT, ctx.get(SUBJECT));
    }
    Session emailSession = getEmailSession();
    if (emailSession == null) {
      GWikiLog.warn("Cannot send email, because no emailSession is configured");
      return;
    }
    MimeMessage message = new MimeMessage(emailSession);
    String encoding = DEFAULT_MAIL_ENCODING;
    GWikiMailHelper mh = new GWikiMailHelper(message, false, encoding);
    setHeaders(mh, headers);
    mh.setBody(ctx.get(TEXT));
    Transport.send(message);
  }

  protected Session getEmailSession()
  {
    Session emailSession = GWikiWeb.get().getDaoContext().getMailSession();
    return emailSession;

  }

  private void setHeaders(GWikiMailHelper helper, Map<String, String> mth) throws MessagingException
  {

    String to = mth.get(TO);
    if (to == null) {
      throw new MessagingException("No recipient given");
    }

    helper.setTo(to);

    if (mth.containsKey(FROM)) {
      helper.setFrom(mth.get(FROM));
    }

    if (mth.containsKey(SUBJECT)) {
      helper.setSubject(mth.get(SUBJECT));
    }

    String cc = mth.get(CC);
    if (cc != null) {
      helper.setCc(cc);
    }

    String bcc = mth.get(BCC);
    if (bcc != null) {
      helper.setBcc(bcc);
    }
  }

}
