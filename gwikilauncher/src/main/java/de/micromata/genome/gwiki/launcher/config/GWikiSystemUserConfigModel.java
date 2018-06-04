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

package de.micromata.genome.gwiki.launcher.config;

import org.apache.commons.lang3.StringUtils;

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
