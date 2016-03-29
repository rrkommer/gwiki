package de.micromata.genome.gwiki.launcher;

import de.micromata.genome.gwiki.launcher.config.GWikiLocalSettingsConfigModel;
import de.micromata.genome.logging.LoggingServiceManager;
import de.micromata.genome.logging.config.LsLoggingImpl;
import de.micromata.genome.util.i18n.ChainedResourceBundleTranslationResolver;
import de.micromata.genome.util.i18n.DefaultWarnI18NTranslationProvider;
import de.micromata.genome.util.i18n.I18NTranslationProvider;
import de.micromata.genome.util.i18n.I18NTranslationProviderImpl;
import de.micromata.genome.util.i18n.I18NTranslations;
import de.micromata.genome.util.i18n.PlaceholderTranslationProvider;
import de.micromata.genome.util.jdbc.LauncherDataSource;
import de.micromata.genome.util.runtime.InitWithCopyFromCpLocalSettingsClassLoader;
import de.micromata.genome.util.runtime.LocalSettings;
import de.micromata.genome.util.runtime.LocalSettingsEnv;
import de.micromata.genome.util.runtime.config.ExtLocalSettingsLoader;
import de.micromata.mgc.application.jetty.JettyServer;
import de.micromata.mgc.application.jetty.MgcApplicationWithJettyApplication;
import de.micromata.mgc.application.webserver.config.JettyConfigModel;

/**
 * GWiki application.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class GWikiLauncherApplication extends MgcApplicationWithJettyApplication<GWikiLocalSettingsConfigModel>
{
  public GWikiLauncherApplication()
  {
    LocalSettingsEnv.dataSourceSuplier = () -> new LauncherDataSource();
    LocalSettings.localSettingsLoaderFactory = new InitWithCopyFromCpLocalSettingsClassLoader(
        () -> {
          ExtLocalSettingsLoader ret = new ExtLocalSettingsLoader();
          ret.setLocalSettingsPrefixName("gwiki");
          return ret;
        });

    I18NTranslationProvider provider = new DefaultWarnI18NTranslationProvider(new PlaceholderTranslationProvider(
        new I18NTranslationProviderImpl(I18NTranslations.systemDefaultLocaleProvider(),
            new ChainedResourceBundleTranslationResolver("gwikilauncher", "mgclauncher", "mgcapp", "mgcjetty"))));

    setTranslateService(provider);
  }

  @Override
  public GWikiLocalSettingsConfigModel loadConfigModel()
  {
    GWikiLocalSettingsConfigModel ret = super.loadConfigModel();
    LocalSettingsEnv.get();
    LoggingServiceManager.get().setLogging(new LsLoggingImpl());
    return ret;
  }

  @Override
  protected void storeToLocalSettings(GWikiLocalSettingsConfigModel model)
  {
    super.storeToLocalSettings(model);
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
