package de.micromata.genome.gwiki.launcher;

import de.micromata.genome.gwiki.launcher.config.GWikiLocalSettingsConfigModel;
import de.micromata.genome.util.i18n.ChainedResourceBundleTranslationResolver;
import de.micromata.genome.util.i18n.DefaultWarnI18NTranslationProvider;
import de.micromata.genome.util.i18n.I18NTranslationProvider;
import de.micromata.genome.util.i18n.I18NTranslationProviderImpl;
import de.micromata.genome.util.i18n.I18NTranslations;
import de.micromata.genome.util.i18n.PlaceholderTranslationProvider;
import de.micromata.genome.util.jdbc.LauncherDataSource;
import de.micromata.genome.util.runtime.LocalSettingsEnv;
import de.micromata.genome.util.runtime.Log4JInitializer;
import de.micromata.mgc.jettystarter.JettyConfigModel;
import de.micromata.mgc.jettystarter.JettyServer;
import de.micromata.mgc.jettystarter.MgcApplicationWithJettyApplication;
import de.micromata.mgc.launcher.MgcApplicationStartStopStatus;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class GWikiLauncherApplication extends MgcApplicationWithJettyApplication<GWikiLocalSettingsConfigModel>
{
  public GWikiLauncherApplication()
  {
    I18NTranslationProvider provider = new DefaultWarnI18NTranslationProvider(new PlaceholderTranslationProvider(
        new I18NTranslationProviderImpl(I18NTranslations.systemDefaultLocaleProvider(),
            new ChainedResourceBundleTranslationResolver("gwikilauncher", "mgclauncher", "mgcapp", "mgcjetty"))));
    setTranslateService(provider);
  }

  @Override
  public MgcApplicationStartStopStatus start(String[] args)
  {
    LocalSettingsEnv.dataSourceSuplier = () -> new LauncherDataSource();

    configureLogging();
    return super.start(args);
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
    GWikiJettyServer ret = new GWikiJettyServer();
    ret.initJetty(cfg);
    return ret;
  }

}
