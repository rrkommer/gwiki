import de.micromata.genome.gwiki.page.impl.actionbean.*;
import org.apache.commons.lang.*;

class GWikiSendEMailActionBean extends ActionBeanBase {
  String receiver;
  String input;
  String subject;
  public Object onInit() {
    return null;
  }

  public Object onSend() {
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
    return null;
  }

  public String getInput() {
    return input;
  }

  public void setInput(String inp) {
    input = inp;
  }
  public String getReceiver() {
   return receiver;
  }
  public void setReceiver(String rec) {
    receiver = rec;
  }
  public String getSubject() { return subject; }
  public void setSubject(String s) { subject = s; }
}
