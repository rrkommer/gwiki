package de.micromata.genome.gwiki.launcher.config;

import de.micromata.genome.util.runtime.config.AbstractCompositLocalSettingsConfigModel;
import de.micromata.genome.util.runtime.config.LocalSettingsWriter;
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

  @Override
  public LocalSettingsWriter toProperties(LocalSettingsWriter writer)
  {
    writer.put("gwiki.enable.webdav", "false");
    return super.toProperties(writer);
  }

}
