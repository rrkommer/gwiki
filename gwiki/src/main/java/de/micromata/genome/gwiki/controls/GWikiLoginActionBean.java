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

package de.micromata.genome.gwiki.controls;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.auth.GWikiSimpleUserAuthorization;
import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiEmailProvider;
import de.micromata.genome.gwiki.model.GWikiPropsArtefakt;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;
import de.micromata.genome.util.text.PlaceHolderReplacer;

/**
 * ActionBean for standard login dialog.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiLoginActionBean extends ActionBeanBase
{
  private String pageId;

  private String user;

  private String password;

  private String passwordForgottenUser;

  public Object onInit()
  {
    password = "";
    return null;
  }

  public Object onLogin()
  {
    if (StringUtils.isBlank(user) == true || StringUtils.isBlank(password) == true) {
      wikiContext.addValidationError("gwiki.page.admin.Login.message.userandpasswordneeded");
      password = "";
      return null;
    }

    boolean success = wikiContext.getWikiWeb().getAuthorization().login(wikiContext, StringUtils.trim(user), StringUtils.trim(password));
    if (success == false) {
      wikiContext.addValidationError("gwiki.page.admin.Login.message.unknownuserpassword");
      password = "";
      return null;
    }
    password = "";
    if (StringUtils.isBlank(pageId) == false) {
      GWikiElementInfo ei = wikiContext.getWikiWeb().findElementInfo(pageId);
      if (ei != null) {
        return ei;
      }
    }
    return wikiContext.getWikiWeb().getHomeElement(wikiContext);
  }

  public Object onLogout()
  {
    wikiContext.getWikiWeb().getAuthorization().logout(wikiContext);
    password = "";
    return null;
  }

  public static final String VALID_CHARS = "ABCDEFGHKLMNPQRSTUVWXYZ23456789";

  private int getCharacterPosFromDictionary(char c)
  {
    return VALID_CHARS.indexOf(c);
  }

  private char getCheckSum(String s)
  {
    int cs = 0;
    for (int i = 0; i < s.length(); ++i) {
      char c = s.charAt(i);
      cs += getCharacterPosFromDictionary(c);
    }
    int mod = cs % VALID_CHARS.length();
    return VALID_CHARS.charAt(mod);
  }

  public String genPassword()
  {
    int c = 10 - 1;
    String ret = RandomStringUtils.random(c, VALID_CHARS);
    ret = ret + getCheckSum(ret);
    return ret;
  }

  public Object onResetPassword()
  {
    passwordForgottenUser = StringUtils.trimToEmpty(passwordForgottenUser);
    if (StringUtils.isEmpty(passwordForgottenUser) == true) {
      wikiContext.addValidationError("gwiki.page.admin.Login.message.resetpassw.userneeded");
      return null;
    }
    String userId = "admin/user/" + passwordForgottenUser;

    GWikiElement el = wikiContext.getWikiWeb().findElement(userId);
    if (el == null) {
      wikiContext.addValidationError("gwiki.page.admin.Login.message.resetpassw.unkownuser");
      return null;
    }
    GWikiArtefakt< ? > art = el.getPart("");
    if ((art instanceof GWikiPropsArtefakt) == false) {
      wikiContext.addValidationError("gwiki.page.admin.Login.message.resetpassw.noemail");
      return null;
    }
    GWikiPropsArtefakt userP = (GWikiPropsArtefakt) art;
    String email = userP.getStorageData().get("email");
    if (StringUtils.isBlank(email) == true) {
      return null;
    }
    String newPass = genPassword();

    String crypedPass = GWikiSimpleUserAuthorization.encrypt(newPass);
    userP.getStorageData().put("password", crypedPass);
    wikiContext.getWikiWeb().saveElement(wikiContext, el, false);

    Map<String, String> mailContext = new HashMap<String, String>();
    mailContext.put(GWikiEmailProvider.TO, email);
    mailContext.put(GWikiEmailProvider.FROM, wikiContext.getWikiWeb().getWikiConfig().getSendEmail());
    mailContext.put("USER", passwordForgottenUser);
    mailContext.put("PUBURL", wikiContext.getWikiWeb().getWikiConfig().getPublicURL());
    mailContext.put("NEWPASS", newPass);
    String subject = wikiContext.getWikiWeb().getI18nProvider().translate(wikiContext, "gwiki.page.admin.Login.message.mailsubject",
        "GWiki; Password changed");
    subject = PlaceHolderReplacer.resolveReplaceDollarVars(subject, mailContext);
    String message = wikiContext.getWikiWeb().getI18nProvider().translate(wikiContext, "gwiki.page.admin.Login.message.mailtext",
        "The password for user ${USER} on\n${PUBURL}\nhas changed to: ${NEWPASS}");
    message = PlaceHolderReplacer.resolveReplaceDollarVars(message, mailContext);
    mailContext.put(GWikiEmailProvider.SUBJECT, subject);

    mailContext.put(GWikiEmailProvider.TEXT, message);
    wikiContext.getWikiWeb().getDaoContext().getEmailProvider().sendEmail(mailContext);
    wikiContext.addValidationError("gwiki.page.admin.Login.message.resetpassw.emailsent");

    return null;
  }

  public String getUser()
  {
    return user;
  }

  public void setUser(String user)
  {
    this.user = user;
  }

  public String getPassword()
  {
    return password;
  }

  public void setPassword(String password)
  {
    this.password = password;
  }

  public String getPageId()
  {
    return pageId;
  }

  public void setPageId(String pageId)
  {
    this.pageId = pageId;
  }

  public String getPasswordForgottenUser()
  {
    return passwordForgottenUser;
  }

  public void setPasswordForgottenUser(String passwordForgottenUser)
  {
    this.passwordForgottenUser = passwordForgottenUser;
  }

}
