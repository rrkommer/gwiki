package de.micromata.genome.gwiki.launcher.config;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.auth.PasswordUtils;
import de.micromata.genome.util.runtime.config.ALocalSettingsPath;
import de.micromata.genome.util.runtime.config.AbstractLocalSettingsConfigModel;
import de.micromata.genome.util.runtime.config.LocalSettingsWriter;
import de.micromata.genome.util.validation.ValContext;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class GWikiSystemUserConfigModel extends AbstractLocalSettingsConfigModel
{
  @ALocalSettingsPath(key = "gwiki.sys.user.enabled", defaultValue = "true", comment = "Use a administrator user")
  private String systemUserEnabled;

  @ALocalSettingsPath(key = "gwiki.sys.user", defaultValue = "gwikisys", comment = "The user name for the adminstrator")
  private String systemUserName;

  @ALocalSettingsPath(key = "gwiki.sys.passwordhash")
  private String systemUserEncPass;

  private String systemUserClearPass;

  @Override
  public LocalSettingsWriter toProperties(LocalSettingsWriter writer)
  {

    if (isSystemUserEnabled() == true) {
      if (StringUtils.isNotBlank(systemUserClearPass) == true) {
        systemUserEncPass = PasswordUtils.createSaltedPassword(systemUserClearPass);
      }
    }
    return super.toProperties(writer);
  }

  @Override
  public void validate(ValContext ctx)
  {
    if (isSystemUserEnabled() == false) {
      return;
    }
    if (StringUtils.isBlank(systemUserName) == true) {
      ctx.directError("systemUserName", "Please provide a name for system user");
    }
    if (StringUtils.isBlank(systemUserClearPass) == true && StringUtils.isBlank(systemUserEncPass) == true) {
      ctx.directError("systemUserClearPass", "Please provide password for system user");
    }

  }

  public boolean isSystemUserEnabled()
  {
    return StringUtils.equals(systemUserEnabled, "true");
  }

  public void setSystemUserEnabled(boolean enable)
  {
    systemUserEnabled = Boolean.toString(enable);
  }

  public String getSystemUserEnabled()
  {
    return systemUserEnabled;
  }

  public void setSystemUserEnabled(String systemUserEnabled)
  {
    this.systemUserEnabled = systemUserEnabled;
  }

  public String getSystemUserName()
  {
    return systemUserName;
  }

  public void setSystemUserName(String systemUserName)
  {
    this.systemUserName = systemUserName;
  }

  public String getSystemUserEncPass()
  {
    return systemUserEncPass;
  }

  public void setSystemUserEncPass(String systemUserEncPass)
  {
    this.systemUserEncPass = systemUserEncPass;
  }

  public String getSystemUserClearPass()
  {
    return systemUserClearPass;
  }

  public void setSystemUserClearPass(String systemUserClearPass)
  {
    this.systemUserClearPass = systemUserClearPass;
  }

}
