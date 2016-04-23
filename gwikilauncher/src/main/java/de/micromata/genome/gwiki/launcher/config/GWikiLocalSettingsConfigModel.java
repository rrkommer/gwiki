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

import org.apache.commons.codec.Charsets;

import de.micromata.genome.logging.config.LsLoggingLocalSettingsConfigModel;
import de.micromata.genome.util.runtime.config.AbstractCompositLocalSettingsConfigModel;
import de.micromata.genome.util.runtime.config.AbstractTextConfigFileConfigModel;
import de.micromata.genome.util.runtime.config.HibernateSchemaConfigModel;
import de.micromata.genome.util.runtime.config.JdbcLocalSettingsConfigModel;
import de.micromata.genome.util.runtime.config.LocalSettingsConfigModel;
import de.micromata.genome.util.runtime.config.LocalSettingsWriter;
import de.micromata.genome.util.runtime.config.MailSessionLocalSettingsConfigModel;
import de.micromata.mgc.application.webserver.config.JettyConfigModel;
import de.micromata.mgc.javafx.launcher.gui.generic.LauncherLocalSettingsConfigModel;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class GWikiLocalSettingsConfigModel extends AbstractCompositLocalSettingsConfigModel
{
  @SuppressWarnings("unused")
  private LauncherLocalSettingsConfigModel launcherConfig = new LauncherLocalSettingsConfigModel();

  @SuppressWarnings("unused")
  private JettyConfigModel jettyConfig = new JettyConfigModel();

  @SuppressWarnings("unused")
  private GWikiSystemUserConfigModel systemUser = new GWikiSystemUserConfigModel();

  @SuppressWarnings("unused")
  private GWikiFilesystemConfigModel fileSystem = new GWikiFilesystemConfigModel();

  private MailSessionLocalSettingsConfigModel emailConfig = new MailSessionLocalSettingsConfigModel("gwikimailsession");

  private LsLoggingLocalSettingsConfigModel loggerConfig = new LsLoggingLocalSettingsConfigModel();
  @SuppressWarnings("unused")
  private HibernateSchemaConfigModel hibernateSchemaConfig = new HibernateSchemaConfigModel();
  @SuppressWarnings("unused")
  private AbstractTextConfigFileConfigModel log4jConfig = new AbstractTextConfigFileConfigModel("Log4J",
      "log4j.properties", Charsets.ISO_8859_1);

  public GWikiLocalSettingsConfigModel()
  {
    emailConfig.setDefaultEmailSender("gwiki@locahost");
    emailConfig.setJndiName("java:comp/env/gwiki/mail/mailSession");
  }

  @Override
  public <T extends LocalSettingsConfigModel> T castToForConfigDialog(Class<T> other)
  {
    if (other == JdbcLocalSettingsConfigModel.class) {
      return null;
    }
    return super.castToForConfigDialog(other);
  }

  @Override
  public LocalSettingsWriter toProperties(LocalSettingsWriter writer)
  {
    writer.put("gwiki.enable.webdav", "false");
    return super.toProperties(writer);
  }

}
