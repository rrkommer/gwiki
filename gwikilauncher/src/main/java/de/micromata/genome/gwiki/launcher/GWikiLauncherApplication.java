package de.micromata.genome.gwiki.launcher;

import de.micromata.genome.gwiki.launcher.config.GWikiLocalSettingsConfigModel;
import de.micromata.genome.util.jdbc.LauncherDataSource;
import de.micromata.genome.util.runtime.LocalSettingsEnv;
import de.micromata.genome.util.runtime.Log4JInitializer;
import de.micromata.genome.util.validation.ValTranslateService;
import de.micromata.genome.util.validation.ValTranslateServices;
import de.micromata.mgc.jettystarter.JettyConfigModel;
import de.micromata.mgc.jettystarter.JettyServer;
import de.micromata.mgc.jettystarter.MgcApplicationWithJettyApplication;
import de.micromata.mgc.launcher.MgcApplicationStartStopListener;
import de.micromata.mgc.launcher.MgcApplicationStartStopStatus;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class GWikiLauncherApplication extends MgcApplicationWithJettyApplication<GWikiLocalSettingsConfigModel>
{
  @Override
  public ValTranslateService getTranslateService()
  {
    return ValTranslateServices.noTranslation();
  }

  @Override
  public MgcApplicationStartStopStatus start(String[] args, MgcApplicationStartStopListener listener)
  {
    LocalSettingsEnv.dataSourceSuplier = () -> new LauncherDataSource();

    configureLogging();
    return super.start(args, listener);
  }

  protected void configureLogging()
  {
    Log4JInitializer.copyLogConfigFileFromCp();
    Log4JInitializer.initializeLog4J();

  }

  @Override
  public void reInit()
  {
    // TODO Auto-generated method stub

  }

  @Override
  protected GWikiLocalSettingsConfigModel newModel()
  {
    return new GWikiLocalSettingsConfigModel();
  }

  @Override
  protected JettyServer newJettyServer(JettyConfigModel cfg)
  {
    return new GWikiJettyServer(cfg);
  }

}
