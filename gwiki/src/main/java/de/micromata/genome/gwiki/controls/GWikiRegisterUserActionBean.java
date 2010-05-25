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

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.auth.GWikiSimpleUserAuthorization;
import de.micromata.genome.gwiki.model.GWikiAuthorizationExt;
import de.micromata.genome.gwiki.model.GWikiProps;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;
import de.micromata.genome.gwiki.utils.EmailValidator;
import de.micromata.genome.gwiki.utils.MathCaptcha;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiRegisterUserActionBean extends ActionBeanBase
{
  public static final String CalcCaptchaSessionKey = "gwiki.CalcC";

  private String user;

  private String email;

  private String pass;

  private String pass2;

  private String catchaText;

  private String catchaInput;

  private boolean allowRegister = false;

  private boolean doubleOptIn = true;

  private boolean showForm = true;

  protected void init()
  {
    GWikiProps props = wikiContext.getElementFinder().getConfigProps("admin/config/GWikiAuthConfig");
    allowRegister = props.getBooleanValue(GWikiLoginActionBean.AUTH_ALLOW_REGISTER_USER, false);
    doubleOptIn = props.getBooleanValue(GWikiLoginActionBean.AUTH_REGISTER_USER_DOUBLE_OPT_IN, true);
    if (allowRegister == false) {
      wikiContext.addSimpleValidationError("Register User is not activated");
      showForm = false;
    }
  }

  public Object onInit()
  {
    init();
    if (wikiContext.hasValidationErrors() == true) {
      return null;
    }
    calcCaptcha();
    return null;
  }

  public void calcCaptcha()
  {
    MathCaptcha mc = new MathCaptcha();
    wikiContext.setSessionAttribute(CalcCaptchaSessionKey, mc);
    catchaText = "" + mc.getFirstVal() + " " + mc.getOperation() + " " + mc.getSecondVal();
  }

  protected boolean checkCatcha()
  {
    Object o = wikiContext.getSessionAttribute(CalcCaptchaSessionKey);
    if ((o instanceof MathCaptcha) == false) {
      calcCaptcha();
      return false;
    }
    MathCaptcha mc = (MathCaptcha) o;
    try {
      int res = Integer.parseInt(catchaInput);
      if (mc.checkResult(res) == false) {
        calcCaptcha();
        return false;
      }
    } catch (NumberFormatException ex) {
      calcCaptcha();
      return false;
    }
    return true;
  }

  public Object onRegister()
  {
    init();
    if (wikiContext.hasValidationErrors() == true) {
      return null;
    }
    if (doRegister() == false) {
      calcCaptcha();
      return null;
    }
    if (showForm == false) {
      return null;
    }
    return wikiContext.getWikiWeb().getHomeElement(wikiContext);
  }

  protected boolean checkEmail(String email)
  {
    if (email == null) {
      return false;
    }
    return EmailValidator.validateEmail(email);
  }

  protected boolean doRegister()
  {
    if (StringUtils.isBlank(catchaInput) == true) {
      wikiContext.addValidationFieldError("gwiki.page.admin.RegisterUser.message.nocatcha", "catchaInput");
      return false;
    }
    if (checkCatcha() == false) {
      wikiContext.addValidationFieldError("gwiki.page.admin.RegisterUser.message.wrongcatcha", "catchaInput");
      return false;
    }
    user = StringUtils.trim(user);
    if (StringUtils.isBlank(user) == true) {
      wikiContext.addValidationFieldError("gwiki.page.admin.RegisterUser.message.userempty", "user");
      return false;
    }
    email = StringUtils.trim(email);
    if (StringUtils.isBlank(email) == true) {
      wikiContext.addValidationFieldError("gwiki.page.admin.RegisterUser.message.emailempty", "email");
      return false;
    }
    if (checkEmail(email) == false) {
      wikiContext.addValidationFieldError("gwiki.page.admin.RegisterUser.message.emailnotvalid", "email");
      return false;
    }
    if (doubleOptIn == false) {
      if (StringUtils.isBlank(pass) == true) {
        wikiContext.addValidationFieldError("gwiki.page.admin.RegisterUser.message.passempty", "pass");
        pass2 = "";
        return false;
      }
      if (pass.equals(pass2) == false) {
        wikiContext.addValidationFieldError("gwiki.page.admin.RegisterUser.message.passnotequal", "pass");
        return false;
      }
    } else {
      pass = GWikiLoginActionBean.genPassword();
    }

    GWikiAuthorizationExt authExt = (GWikiAuthorizationExt) wikiContext.getWikiWeb().getAuthorization();
    if (authExt.hasUser(wikiContext, user) == true) {
      wikiContext.addValidationFieldError("gwiki.page.admin.RegisterUser.message.userexists", "user");
      return false;
    }
    if (GWikiSimpleUserAuthorization.rankPasswort(pass) < 1000) {
      wikiContext.addValidationFieldError("gwiki.profile.message.password_too_simple", "pass");
      return false;
    }
    String cp = GWikiSimpleUserAuthorization.encrypt(pass);
    GWikiProps props = new GWikiProps();
    props.setStringValue(GWikiAuthorizationExt.USER_PROP_EMAIL, email);
    props.setStringValue(GWikiAuthorizationExt.USER_PROP_RIGHTSRULE, "GWIKI_VIEWPAGES");
    props.setStringValue(GWikiAuthorizationExt.USER_PROP_PASSWORD, cp);
    if (authExt.createUser(wikiContext, user, props) == false) {
      wikiContext.addValidationError("gwiki.page.admin.RegisterUser.message.internalerrorstore");
      return false;
    }
    if (doubleOptIn == true) {
      GWikiLoginActionBean.sendPasswordToUser(wikiContext, user, email, pass);
      wikiContext.addValidationError("gwiki.page.admin.Login.message.resetpassw.emailsent");
      showForm = false;
    } else {
      boolean success = wikiContext.getWikiWeb().getAuthorization().login(wikiContext, StringUtils.trim(user), StringUtils.trim(pass));
      if (success == false) {
        return false;
      }
    }
    return true;
  }

  public String getUser()
  {
    return user;
  }

  public void setUser(String user)
  {
    this.user = user;
  }

  public String getEmail()
  {
    return email;
  }

  public void setEmail(String email)
  {
    this.email = email;
  }

  public String getCatchaText()
  {
    return catchaText;
  }

  public void setCatchaText(String catchaText)
  {
    this.catchaText = catchaText;
  }

  public String getCatchaInput()
  {
    return catchaInput;
  }

  public void setCatchaInput(String catchaInput)
  {
    this.catchaInput = catchaInput;
  }

  public String getPass()
  {
    return pass;
  }

  public void setPass(String pass)
  {
    this.pass = pass;
  }

  public String getPass2()
  {
    return pass2;
  }

  public void setPass2(String pass2)
  {
    this.pass2 = pass2;
  }

  public boolean isAllowRegister()
  {
    return allowRegister;
  }

  public void setAllowRegister(boolean allowRegister)
  {
    this.allowRegister = allowRegister;
  }

  public boolean isDoubleOptIn()
  {
    return doubleOptIn;
  }

  public void setDoubleOptIn(boolean doubleOptIn)
  {
    this.doubleOptIn = doubleOptIn;
  }

  public boolean isShowForm()
  {
    return showForm;
  }

  public void setShowForm(boolean showForm)
  {
    this.showForm = showForm;
  }

}
