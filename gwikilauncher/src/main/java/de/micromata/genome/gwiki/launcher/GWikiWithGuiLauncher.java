package de.micromata.genome.gwiki.launcher;

import de.micromata.genome.gwiki.launcher.config.GWikiLocalSettingsConfigModel;
import de.micromata.mgc.javafx.launcher.MgcLauncher;
import de.micromata.mgc.javafx.launcher.gui.generic.GenericMainWindow;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class GWikiWithGuiLauncher extends MgcLauncher<GWikiLocalSettingsConfigModel>
{
  public GWikiWithGuiLauncher()
  {
    super(new GWikiLauncherApplication(), (Class) GenericMainWindow.class);
  }

  public static void main(String[] args)
  {
    GWikiWithGuiLauncher el = new GWikiWithGuiLauncher();
    el.launch(args);
  }
}
