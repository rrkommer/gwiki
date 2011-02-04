package de.micromata.genome.gwiki.admintools_1_0.email;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.config.GWikiMetaTemplate;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;
import de.micromata.genome.util.matcher.MatcherBase;

/*
 Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class SendEmailActionBean extends ActionBeanBase
{
  private Map<String, String> availableTemplates = new HashMap<String, String>();

  private String selectedTemplate;

  private String receiver;

  private String subject;

  private String input;

  private void collectAvialbleTemplates()
  {
    availableTemplates.put("", "");
    final GWikiMetaTemplate mt = wikiContext.getWikiWeb().findMetaTemplate("admin/templates/JspMailTemplateMetaTemplate");
    if (mt == null) {
      return;
    }
    for (GWikiElementInfo ei : wikiContext.getElementFinder().getPageInfos(new MatcherBase<GWikiElementInfo>() {

      public boolean match(GWikiElementInfo ei)
      {
        GWikiMetaTemplate ot = ei.getMetaTemplate();
        if (ot != null && ot.getPageId().equals(mt.getPageId()) == true) {
          return true;
        }
        return false;
      }

    })) {
      availableTemplates.put(ei.getTitle(), ei.getId());
    }
  }

  public Object onInit()
  {
    collectAvialbleTemplates();
    return null;
  }

  public Object onSendNormal()
  {
    collectAvialbleTemplates();
    sendNormal();
    return null;
  }

  public Object onSendByTemplate()
  {
    collectAvialbleTemplates();
    if (StringUtils.isBlank(selectedTemplate) == true) {
      wikiContext.addSimpleValidationError("No Email template selected");
      return null;
    }
    Map<String, String> mctx = new HashMap<String, String>();
    for (int i = 0; i < 10; ++i) {
      String k = wikiContext.getRequestParameter("KEY" + i);
      if (StringUtils.isEmpty(k) == true) {
        continue;
      }
      String v = wikiContext.getRequestParameter("VALUE" + i);
      mctx.put(k, v);
    }
    mctx.put("MAILTEMPLATE", selectedTemplate);
    wikiContext.getWikiWeb().getDaoContext().getEmailProvider().sendEmail(mctx);
    wikiContext.addSimpleValidationError("Email is sent.");
    return null;
  }

  protected void sendNormal()
  {
    Map<String, String> mc = new HashMap<String, String>();
    if (StringUtils.isEmpty(receiver) == true) {
      wikiContext.addSimpleValidationError("Receiver must not be empty");
    }
    if (StringUtils.isEmpty(input) == true) {
      wikiContext.addSimpleValidationError("Text must not be empty");
    }
    if (StringUtils.isEmpty(subject) == true) {
      wikiContext.addSimpleValidationError("Subject must not be empty");
    }
    mc.put("TO", receiver);
    mc.put("FROM", wikiContext.getWikiWeb().getWikiConfig().getSendEmail());
    mc.put("SUBJECT", subject);
    mc.put("TEXT", input);

    wikiContext.getWikiWeb().getDaoContext().getEmailProvider().sendEmail(mc);
    wikiContext.addSimpleValidationError("Email is sent.");

  }

  public String getReceiver()
  {
    return receiver;
  }

  public void setReceiver(String receiver)
  {
    this.receiver = receiver;
  }

  public String getSubject()
  {
    return subject;
  }

  public void setSubject(String subject)
  {
    this.subject = subject;
  }

  public String getInput()
  {
    return input;
  }

  public void setInput(String input)
  {
    this.input = input;
  }

  public String getSelectedTemplate()
  {
    return selectedTemplate;
  }

  public void setSelectedTemplate(String selectedTemplate)
  {
    this.selectedTemplate = selectedTemplate;
  }

  public Map<String, String> getAvailableTemplates()
  {
    return availableTemplates;
  }

  public void setAvailableTemplates(Map<String, String> availableTemplates)
  {
    this.availableTemplates = availableTemplates;
  }

}
