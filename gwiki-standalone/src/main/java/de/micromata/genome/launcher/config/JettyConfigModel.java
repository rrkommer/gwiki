package de.micromata.genome.launcher.config;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

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
