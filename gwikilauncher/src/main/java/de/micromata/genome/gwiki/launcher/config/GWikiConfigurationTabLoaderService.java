package de.micromata.genome.gwiki.launcher.config;

import java.util.ArrayList;
import java.util.List;

import de.micromata.genome.util.runtime.config.CastableLocalSettingsConfigModel;
import de.micromata.mgc.javafx.launcher.gui.TabConfig;
import de.micromata.mgc.javafx.launcher.gui.generic.ConfigurationTabLoaderService;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class GWikiConfigurationTabLoaderService implements ConfigurationTabLoaderService
{

  @Override
  public List<TabConfig> getTabsByConfiguration(CastableLocalSettingsConfigModel configModel)
  {
    List<TabConfig> ret = new ArrayList<>();

    GWikiSystemUserConfigModel launcherConfig = configModel.castToForConfigDialog(GWikiSystemUserConfigModel.class);
    if (launcherConfig != null) {
      ret.add(new TabConfig(GWikiSystemUserController.class, launcherConfig));
    }
    GWikiFilesystemConfigModel filesystemConfig = configModel.castToForConfigDialog(GWikiFilesystemConfigModel.class);
    if (filesystemConfig != null) {
      ret.add(new TabConfig(GWikiFilesystemController.class, filesystemConfig));
    }

    return ret;
  }

}
