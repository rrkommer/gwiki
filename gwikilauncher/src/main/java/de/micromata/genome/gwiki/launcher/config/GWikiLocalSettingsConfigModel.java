package de.micromata.genome.gwiki.launcher.config;

import de.micromata.genome.util.runtime.config.AbstractCompositLocalSettingsConfigModel;
import de.micromata.genome.util.runtime.config.HibernateSchemaConfigModel;
import de.micromata.genome.util.runtime.config.JdbcLocalSettingsConfigModel;
import de.micromata.genome.util.runtime.config.LocalSettingsConfigModel;
import de.micromata.genome.util.runtime.config.LocalSettingsWriter;
import de.micromata.genome.util.runtime.config.MailSessionLocalSettingsConfigModel;
import de.micromata.mgc.javafx.launcher.gui.generic.LauncherLocalSettingsConfigModel;
import de.micromata.mgc.jettystarter.JettyConfigModel;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class GWikiLocalSettingsConfigModel extends AbstractCompositLocalSettingsConfigModel
{
  private LauncherLocalSettingsConfigModel launcherConfig = new LauncherLocalSettingsConfigModel();

  private JettyConfigModel jettyConfig = new JettyConfigModel();

  private GWikiSystemUserConfigModel systemUser = new GWikiSystemUserConfigModel();

  private GWikiFilesystemConfigModel fileSystem = new GWikiFilesystemConfigModel();
  private MailSessionLocalSettingsConfigModel emailConfig = new MailSessionLocalSettingsConfigModel("gwikimailsession");
  @SuppressWarnings("unused")
  private HibernateSchemaConfigModel hibernateSchemaConfig = new HibernateSchemaConfigModel();

  public GWikiLocalSettingsConfigModel()
  {
    emailConfig.setDefaultEmailSender("gwiki@locahost");
    emailConfig.setJndiName("java:/comp/env/gwiki/mail/mailSession");
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
