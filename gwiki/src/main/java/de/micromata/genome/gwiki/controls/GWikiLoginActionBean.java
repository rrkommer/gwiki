////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer
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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.auth.GWikiSimpleUserAuthorization;
import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiAuthorization;
import de.micromata.genome.gwiki.model.GWikiAuthorizationExt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiEmailProvider;
import de.micromata.genome.gwiki.model.GWikiProps;
import de.micromata.genome.gwiki.model.GWikiPropsArtefakt;
import de.micromata.genome.gwiki.model.logging.GWikiLog;
import de.micromata.genome.gwiki.model.logging.GWikiLogCategory;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;
import de.micromata.genome.logging.GLog;
import de.micromata.genome.util.runtime.LocalSettings;
import de.micromata.genome.util.runtime.RuntimeIOException;
import de.micromata.genome.util.text.PlaceHolderReplacer;

/**
 * ActionBean for standard login dialog.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiLoginActionBean extends ActionBeanBase
{
  public static final String AUTH_ALLOW_PASSWORD_FORGOTTEN = "AUTH_ALLOW_PASSWORD_FORGOTTEN";

  public static final String AUTH_ALLOW_REGISTER_USER = "AUTH_ALLOW_REGISTER_USER";

  public static final String AUTH_REGISTER_USER_DOUBLE_OPT_IN = "AUTH_REGISTER_USER_DOUBLE_OPT_IN";

  private String pageId;

  private String user;

  private String password;

  private String passwordForgottenUser;

  private boolean allowPasswortForgotten = true;

  private boolean publicRegister = false;

  private boolean doubleOptInRegister = true;
  /**
   * if login token should stored in cookie.
   */
  private boolean keepLoginInSession = false;

  protected void checkPublicRegister()
  {
    GWikiProps props = wikiContext.getElementFinder().getConfigProps("admin/config/GWikiAuthConfig");
    allowPasswortForgotten = props.getBooleanValue(AUTH_ALLOW_PASSWORD_FORGOTTEN, false);
    if ((wikiContext.getWikiWeb().getAuthorization() instanceof GWikiAuthorizationExt) == false) {
      return;
    }
    GWikiElementInfo rp = wikiContext.getWikiWeb().findElementInfo("admin/RegisterUser");
    if (rp == null) {
      return;
    }
    if (wikiContext.getWikiWeb().getAuthorization().isAllowToView(wikiContext, rp) == false) {
      return;
    }

    publicRegister = props.getBooleanValue(AUTH_ALLOW_REGISTER_USER, false);
    doubleOptInRegister = props.getBooleanValue(AUTH_REGISTER_USER_DOUBLE_OPT_IN, false);
  }

  protected Object checkSecureLogin()
  {
    String httpsRed = LocalSettings.get().get("gwiki.public.url.https");
    if (StringUtils.isBlank(httpsRed) == true) {
      return null;
    }
    StringBuffer reqUri = wikiContext.getRequest().getRequestURL();
    if (StringUtils.startsWith(reqUri.toString(), "https:") == true) {
      return null;
    }
    String thisUrl = wikiContext.getRealPathInfo();
    String retUrl = httpsRed + thisUrl;
    try {
      wikiContext.getResponse().sendRedirect(retUrl);
    } catch (IOException ex) {
      throw new RuntimeIOException(ex);
    }
    return noForward();
  }

  @Override
  public Object onInit()
  {
    password = "";
    Object ret = checkSecureLogin();
    if (ret != null) {
      return ret;
    }
    checkPublicRegister();
    return null;
  }

  public Object onLogin()
  {
    checkPublicRegister();
    if (StringUtils.isBlank(user) == true || StringUtils.isBlank(password) == true) {
      wikiContext.addValidationError("gwiki.page.admin.Login.message.userandpasswordneeded");
      password = "";
      return null;
    }
    GWikiAuthorization auth = wikiContext.getWikiWeb().getAuthorization();
    boolean success = auth.login(wikiContext, StringUtils.trim(user),
        StringUtils.trim(password));
    if (success == false) {
      wikiContext.addValidationError("gwiki.page.admin.Login.message.unknownuserpassword");

      GLog.note(GWikiLogCategory.Wiki, "Invalid login: user: " + user + "; ");
      password = "";
      return null;
    }
    if (keepLoginInSession == true) {
      auth.createAuthenticationCookie(wikiContext, user, password);
    } else {
      auth.clearAuthenticationCookie(wikiContext, user);
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
    checkPublicRegister();
    wikiContext.getWikiWeb().getAuthorization().logout(wikiContext);
    password = "";
    return null;
  }

  public static final String VALID_CHARS = "ABCDEFGHKLMNPQRSTUVWXYZ23456789";

  private static int getCharacterPosFromDictionary(char c)
  {
    return VALID_CHARS.indexOf(c);
  }

  private static char getCheckSum(String s)
  {
    int cs = 0;
    for (int i = 0; i < s.length(); ++i) {
      char c = s.charAt(i);
      cs += getCharacterPosFromDictionary(c);
    }
    int mod = cs % VALID_CHARS.length();
    return VALID_CHARS.charAt(mod);
  }

  public static String genPassword()
  {
    int c = 10 - 1;
    String ret = RandomStringUtils.random(c, VALID_CHARS);
    ret = ret + getCheckSum(ret);
    return ret;
  }

  public static void sendPasswordToUser(GWikiContext wikiContext, String user, String email, String newPass)
  {

    Map<String, String> mailContext = new HashMap<String, String>();
    mailContext.put(GWikiEmailProvider.TO, email);
    mailContext.put(GWikiEmailProvider.FROM, wikiContext.getWikiWeb().getWikiConfig().getSendEmail());
    mailContext.put("USER", user);
    mailContext.put("PUBURL", wikiContext.getWikiWeb().getWikiConfig().getPublicURL());
    mailContext.put("NEWPASS", newPass);
    String subject = wikiContext.getWikiWeb().getI18nProvider()
        .translate(wikiContext, "gwiki.page.admin.Login.message.mailsubject", "GWiki; Password changed");
    subject = PlaceHolderReplacer.resolveReplaceDollarVars(subject, mailContext);
    String message = wikiContext
        .getWikiWeb()
        .getI18nProvider()
        .translate(wikiContext, "gwiki.page.admin.Login.message.mailtext",
            "The password for user ${USER} on\n${PUBURL}\nhas changed to: ${NEWPASS}");
    message = PlaceHolderReplacer.resolveReplaceDollarVars(message, mailContext);
    mailContext.put(GWikiEmailProvider.SUBJECT, subject);

    mailContext.put(GWikiEmailProvider.TEXT, message);
    wikiContext.getWikiWeb().getDaoContext().getEmailProvider().sendEmail(mailContext);
    String failMessage = mailContext.get(GWikiEmailProvider.SENDEMAILFAILED);
    if (StringUtils.isNotEmpty(failMessage) == true) {
      throw new RuntimeException(failMessage);
    }
  }

  public Object onResetPassword()
  {
    checkPublicRegister();
    if (allowPasswortForgotten == false) {
      return null;
    }
    passwordForgottenUser = StringUtils.trimToEmpty(passwordForgottenUser);
    if (StringUtils.isEmpty(passwordForgottenUser) == true) {
      wikiContext.addValidationError("gwiki.page.admin.Login.message.resetpassw.userneeded");
      return null;
    }
    String userId = "admin/user/" + passwordForgottenUser;

    GWikiElement el = wikiContext.getWikiWeb().findElement(userId);
    if (el == null) {
      GLog.note(GWikiLogCategory.Wiki, "Passwort reset requested for unknown user: " + passwordForgottenUser);
      wikiContext.addValidationError("gwiki.page.admin.Login.message.resetpassw.emailsent");
      // wikiContext.addValidationError("gwiki.page.admin.Login.message.resetpassw.unkownuser");
      return null;
    }
    GWikiArtefakt<?> art = el.getPart("");
    if ((art instanceof GWikiPropsArtefakt) == false) {
      GWikiLog.warn("No Valid user, cann not determine email. User: " + passwordForgottenUser);
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
    try {
      sendPasswordToUser(wikiContext, passwordForgottenUser, email, newPass);
      wikiContext.addValidationError("gwiki.page.admin.Login.message.resetpassw.emailsent");
    } catch (Exception ex) {
      GWikiLog.warn("Cannot send reset password: " + ex.getMessage(), ex);
      wikiContext.addValidationError("gwiki.page.admin.RegisterUser.message.unabletosend");
    }
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

  public boolean isPublicRegister()
  {
    return publicRegister;
  }

  public void setPublicRegister(boolean publicRegister)
  {
    this.publicRegister = publicRegister;
  }

  public boolean isAllowPasswortForgotten()
  {
    return allowPasswortForgotten;
  }

  public void setAllowPasswortForgotten(boolean allowPasswortForgotten)
  {
    this.allowPasswortForgotten = allowPasswortForgotten;
  }

  public boolean isDoubleOptInRegister()
  {
    return doubleOptInRegister;
  }

  public void setDoubleOptInRegister(boolean doubleOptInRegister)
  {
    this.doubleOptInRegister = doubleOptInRegister;
  }

  public boolean isKeepLoginInSession()
  {
    return keepLoginInSession;
  }

  public void setKeepLoginInSession(boolean keepLoginInSession)
  {
    this.keepLoginInSession = keepLoginInSession;
  }

}
