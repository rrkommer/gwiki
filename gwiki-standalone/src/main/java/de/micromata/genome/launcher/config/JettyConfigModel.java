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

package de.micromata.genome.launcher.config;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import de.micromata.genome.gwiki.jetty.ValidationContext;
import de.micromata.genome.util.runtime.config.ALocalSettingsPath;

/**
 * Configuration Model for Jetty.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class JettyConfigModel implements ConfigModel
{
  @ALocalSettingsPath(key = "gwiki.jetty.port", defaultValue = "8081")
  private String serverPort;
  @ALocalSettingsPath(key = "gwiki.jetty.contextpath", defaultValue = "/")
  private String serverContextPath;

  @ALocalSettingsPath(key = "gwiki.public.url", defaultValue = "http://localhost:8081/")
  private String publicUrl;

  @Override
  public void validate(ValidationContext ctx)
  {
    if (StringUtils.isBlank(serverPort) == true) {
      ctx.error("serverPort", "Please provide a server port");
    } else {
      if (NumberUtils.isDigits(serverPort) == false) {
        ctx.error("serverPort", "Please provid numeric port number");
      }
    }
  }

  public String getServerPort()
  {
    return serverPort;
  }

  public void setServerPort(String serverPort)
  {
    this.serverPort = serverPort;
  }

  public String getServerContextPath()
  {
    return serverContextPath;
  }

  public void setServerContextPath(String serverContextPath)
  {
    this.serverContextPath = serverContextPath;
  }

  public String getPublicUrl()
  {
    return publicUrl;
  }

  public void setPublicUrl(String publicUrl)
  {
    this.publicUrl = publicUrl;
  }

}
