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

package de.micromata.genome.gwiki.jetty;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.micromata.genome.gwiki.auth.PasswordUtils;
import de.micromata.genome.gwiki.spi.storage.LsFileSystemFactoryBean;
import de.micromata.genome.launcher.config.CastableConfigModel;
import de.micromata.genome.launcher.config.ConfigModel;
import de.micromata.genome.launcher.config.JettyConfigModel;
import de.micromata.genome.util.collections.SortedProperties;
import de.micromata.genome.util.runtime.LocalSettings;
import de.micromata.genome.util.runtime.config.ALocalSettingsPath;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class GWikiStarterConfig implements CastableConfigModel
{
  private static Logger LOG = Logger.getLogger(GWikiStarterConfig.class);

  private JettyConfigModel jettyConfig = new JettyConfigModel();

  @ALocalSettingsPath(key = "gwiki.sys.user.enabled", defaultValue = "false")
  private String systemUserEnabled;

  @ALocalSettingsPath(key = "gwiki.sys.user", defaultValue = "gwikisys")
  private String systemUserName;

  @ALocalSettingsPath(key = "gwiki.sys.passwordhash")
  private String systemUserEncPass;

  private String systemUserClearPass;

  @ALocalSettingsPath(key = "gwiki.wikifilepath", defaultValue = "./gwiki")
  private String storageFilePath;

  @ALocalSettingsPath(key = "db.ds.gwikdb.drivername")
  private String jdbcDriver;

  @ALocalSettingsPath(key = "db.ds.gwikdb.url")
  private String jdbcUrl;
  @ALocalSettingsPath(key = "db.ds.gwikdb.username")
  private String jdbcUserName;

  @ALocalSettingsPath(key = "db.ds.gwikdb.password")
  private String jdbcPassword;

  @ALocalSettingsPath(key = "gwiki.webdav.enabled", defaultValue = "false")
  private String enableWebDav;

  @ALocalSettingsPath(key = "gwiki.filesystem.type", defaultValue = LsFileSystemFactoryBean.LOCAL_FILE_SYSTEM)
  private String fileSystemType;

  @ALocalSettingsPath(key = "gwiki.smpt.enabled", defaultValue = "false")
  private String emailEnabled;
  @ALocalSettingsPath(key = "mail.session.gwiki.smtp.host", defaultValue = "localhost")
  private String emailHost;
  @ALocalSettingsPath(key = "mail.session.gwiki.smtp.port", defaultValue = "25")
  private String emailPort;

  @ALocalSettingsPath(key = "mail.session.gwiki.smtp.auth", defaultValue = "false")
  private String emailAuthEnabled;

  @ALocalSettingsPath(key = "mail.session.gwiki.smtp.user")
  private String emailAuthUser;

  @ALocalSettingsPath(key = "mail.session.gwiki.smtp.password")
  private String emailAuthPass;

  @ALocalSettingsPath(key = "mail.session.gwiki.smtp.starttls.enable", defaultValue = "false")
  private String emailAuthEnableStartTls;
  @ALocalSettingsPath(key = "mail.session.gwiki.smtp.ssl.enable", defaultValue = "false")
  private String emailAuthEnableStartSsl;

  public GWikiStarterConfig()
  {

  }

  @Override
  public <T extends ConfigModel> T castTo(Class<T> other)
  {
    if (other.isAssignableFrom(JettyConfigModel.class) == true) {
      return (T) jettyConfig;
    }
    return null;
  }

  public void storeConfig(ValidationContext ctx)
  {
    SortedProperties props = toProperties();
    File propFile = new GWikiInitialSetup().getGwikiProperties();
    try (FileOutputStream fis = new FileOutputStream(propFile)) {
      props.store(fis, "Generated by Gwiki");
    } catch (IOException ex) {
      ctx.error("Cannot write properties " + propFile.getAbsolutePath() + ": " + ex.getMessage());
    }
  }

  @Override
  public void fromLocalSettings(LocalSettings localSettings)
  {
    CastableConfigModel.super.fromLocalSettings(localSettings);
    jettyConfig.fromLocalSettings(localSettings);
  }

  @Override
  public void toProperties(SortedProperties props)
  {
    if (isSystemUserEnabled() == true) {
      if (StringUtils.isNotBlank(systemUserClearPass) == true) {
        systemUserEncPass = PasswordUtils.createSaltedPassword(systemUserClearPass);
      }
    }

    if (isUseJdbc() == true) {
      props.put("db.ds.gwikdb.name", "gwikidb");
      props.put("genomeds", "gwikidb");
      props.put("jndi.bind.standard.target", "java:comp/env/genome/jdbc/dsWeb");
      props.put("jndi.bind.standard.type", "DataSource");
      props.put("jndi.bind.standard.source", "gwikidb");
    } else {

    }
    jettyConfig.toProperties(props);
    CastableConfigModel.super.toProperties(props);
  }

  public SortedProperties toProperties()
  {
    SortedProperties ret = new SortedProperties();
    toProperties(ret);
    return ret;
  }

  @Override
  public void validate(ValidationContext ctx)
  {
    jettyConfig.validate(ctx);
    validateSystemUser(ctx);
    validateStorage(ctx);
  }

  private void validateSystemUser(ValidationContext ctx)
  {
    if (isSystemUserEnabled() == false) {
      return;
    }
    if (StringUtils.isBlank(systemUserName) == true) {
      ctx.error("systemUserName", "Please provide a name for system user");
    }
    if (StringUtils.isBlank(systemUserClearPass) == true && StringUtils.isBlank(systemUserEncPass) == true) {
      ctx.error("systemUserClearPass", "Please provide password for system user");
    }
  }

  public boolean validateStorage(ValidationContext ctx)
  {
    if (isUseJdbc() == true) {
      return validateJdbcStorage(ctx);
    } else {
      return validateLocalFilesystem(ctx);
    }
  }

  private boolean validateLocalFilesystem(ValidationContext ctx)
  {
    if (StringUtils.isBlank(storageFilePath) == true) {
      return ctx.error("storageFilePath", "Please provide directory");
    }
    File dir = new File(storageFilePath);
    if (dir.isFile() == true) {
      ctx.error("storageFilePath", "Please provide directory instead of file");
    }
    return ctx.hasErrors();
  }

  private boolean validateJdbcStorage(ValidationContext ctx)
  {
    if (StringUtils.isBlank(jdbcDriver) == true) {
      ctx.error("jdbcDriver", "Please select jdbcDriver");
    }
    String url = jdbcUrl;
    if (StandaloneDatabases.LOCAL_DERBY.getDriver().equals(jdbcDriver) == true) {
      url = jdbcUrl + ";create=true";
    }
    if (StringUtils.isBlank(url) == true) {
      ctx.error("jdbcUrl", "Please select jdbcUrl");
    }
    if (ctx.hasErrors() == true) {
      return false;
    }

    return checkDbUrl(ctx, jdbcDriver, url, jdbcUserName, jdbcPassword);
  }

  private boolean checkDbUrl(ValidationContext ctx, String driver, String url, String user, String pass)
  {
    try {
      Class.forName(driver);
      try (Connection con = DriverManager.getConnection(url, user, pass)) {
        try (Statement stmt = con.createStatement()) {
          ctx.info("Created DB Connection....");
        }
      }
      return true;
    } catch (ClassNotFoundException e) {
      ctx.error("Cannot find db driver: " + driver);
      LOG.error(e);
      return false;
    } catch (SQLException e) {
      ctx.error("Cannot create connection: " + e.getMessage());
      SQLException ne = e.getNextException();
      if (ne != null && ne != e) {
        ctx.error(ne.getMessage());

      }
      LOG.error(e);
      return false;
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

  public String getStorageFilePath()
  {
    return storageFilePath;
  }

  public void setStorageFilePath(String storageFilePath)
  {
    this.storageFilePath = storageFilePath;
  }

  public String getSystemUserClearPass()
  {
    return systemUserClearPass;
  }

  public void setSystemUserClearPass(String systemUserClearPass)
  {
    this.systemUserClearPass = systemUserClearPass;
  }

  public String getJdbcDriver()
  {
    return jdbcDriver;
  }

  public void setJdbcDriver(String jdbcDriver)
  {
    this.jdbcDriver = jdbcDriver;
  }

  public String getJdbcUrl()
  {
    return jdbcUrl;
  }

  public void setJdbcUrl(String jdbcUrl)
  {
    this.jdbcUrl = jdbcUrl;
  }

  public String getJdbcUserName()
  {
    return jdbcUserName;
  }

  public void setJdbcUserName(String jdbcUserName)
  {
    this.jdbcUserName = jdbcUserName;
  }

  public String getJdbcPassword()
  {
    return jdbcPassword;
  }

  public void setJdbcPassword(String jdbcPassword)
  {
    this.jdbcPassword = jdbcPassword;
  }

  public boolean isUseJdbc()
  {
    return LsFileSystemFactoryBean.JPA_FILE_SYSTEM.equals(fileSystemType);
  }

  public void setUseJdbc(boolean useJdbc)
  {
    fileSystemType = useJdbc ? LsFileSystemFactoryBean.JPA_FILE_SYSTEM : LsFileSystemFactoryBean.LOCAL_FILE_SYSTEM;
  }

}
