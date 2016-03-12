package de.micromata.genome.gwiki.launcher;

import de.micromata.genome.gwiki.launcher.config.GWikiLocalSettingsConfigModel;
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
import de.micromata.mgc.jettystarter.JettyConfigModel;
import de.micromata.mgc.jettystarter.JettyServer;
import de.micromata.mgc.jettystarter.MgcApplicationWithJettyApplication;

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
