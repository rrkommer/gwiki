package de.micromata.genome.gwiki.launcher;

import de.micromata.genome.gwiki.launcher.config.GWikiLocalSettingsConfigModel;
import de.micromata.mgc.javafx.launcher.MgcLauncher;
import de.micromata.mgc.javafx.launcher.gui.generic.GenericMainWindow;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class GWikiLauncherMain extends MgcLauncher<GWikiLocalSettingsConfigModel>
{
  public static void main(String[] args)
  {
    GWikiLauncherMain el = new GWikiLauncherMain();
    el.launch(args);
  }

  public GWikiLauncherMain()
  {
    super(new GWikiLauncherApplication(), (Class) GenericMainWindow.class);
  }
}
