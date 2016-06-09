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

package de.micromata.genome.gwiki.launcher;

import de.micromata.genome.gwiki.launcher.config.GWikiLocalSettingsConfigModel;
import de.micromata.genome.logging.GLog;
import de.micromata.genome.logging.GenomeLogCategory;
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
import de.micromata.genome.util.runtime.config.LocalSettingsConfigModel;
import de.micromata.genome.util.validation.ValContext;
import de.micromata.genome.util.validation.ValMessage;
import de.micromata.mgc.application.MgcApplicationStartStopStatus;
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
  public MgcApplicationStartStopStatus startImpl(String[] args) throws Exception
  {
    MgcApplicationStartStopStatus ret = super.startImpl(args);
    if (ret == MgcApplicationStartStopStatus.StartSuccess) {
      ((GWikiJettyServer) jettyServer).buildIndex();
    }
    return ret;
  }

  public boolean checkConfiguration(StringBuilder errorBuffer)
  {
    LocalSettingsConfigModel configuraiton = getConfigModel();
    ValContext ctx = new ValContext();
    configuraiton.validate(ctx);
    ctx.translateMessages(getTranslateService());
    for (ValMessage msg : ctx.getMessages()) {
      GLog.logValMessage(GenomeLogCategory.System, msg);
      String message = msg.getMessage();
      if (message == null) {
        message = "???" + msg.getI18nkey() + "???";
      }
      errorBuffer.append(message).append("\n");
    }
    return ctx.hasErrors() == false;
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
