////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2010 Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
////////////////////////////////////////////////////////////////////////////

package de.micromata.genome.gwiki.model.config;

import javax.mail.Session;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.beans.factory.BeanFactory;

import de.micromata.genome.gdbfs.FileSystem;
import de.micromata.genome.gwiki.model.GWikiAuthorization;
import de.micromata.genome.gwiki.model.GWikiEmailProvider;
import de.micromata.genome.gwiki.model.GWikiI18nProvider;
import de.micromata.genome.gwiki.model.GWikiLogging;
import de.micromata.genome.gwiki.model.GWikiLoggingBufferedLogger;
import de.micromata.genome.gwiki.model.GWikiLoggingLog4J;
import de.micromata.genome.gwiki.model.GWikiMenuProvider;
import de.micromata.genome.gwiki.model.GWikiMimeTypeProvider;
import de.micromata.genome.gwiki.model.GWikiMimeTypeStandardProvider;
import de.micromata.genome.gwiki.model.GWikiPageCache;
import de.micromata.genome.gwiki.model.GWikiPageCacheTimedImpl;
import de.micromata.genome.gwiki.model.GWikiSchedulerProvider;
import de.micromata.genome.gwiki.model.GWikiSessionProvider;
import de.micromata.genome.gwiki.model.GWikiStandardEmailProvider;
import de.micromata.genome.gwiki.model.GWikiStandardMenuProvider;
import de.micromata.genome.gwiki.model.GWikiStandardSchedulerProvider;
import de.micromata.genome.gwiki.model.GWikiStandardSessionProvider;
import de.micromata.genome.gwiki.model.GWikiStandardWikiSelector;
import de.micromata.genome.gwiki.model.GWikiStorage;
import de.micromata.genome.gwiki.model.GWikiWikiSelector;
import de.micromata.genome.gwiki.page.gspt.GWikiJspProcessor;
import de.micromata.genome.gwiki.page.gspt.GenomeJspProcessor;
import de.micromata.genome.gwiki.page.impl.i18n.GWikiI18nStandardProvider;
import de.micromata.genome.gwiki.page.search.ContentSearcher;
import de.micromata.genome.gwiki.page.search.expr.SearchExpressionContentSearcher;
import de.micromata.genome.gwiki.plugin.GWikiPluginRepository;
import de.micromata.genome.gwiki.umgmt.GWikiUserAuthorization;
import de.micromata.genome.util.bean.PrivateBeanUtils;

/**
 * Wiki Bootstrapping Config.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiDAOContext
{
  /**
   * Storage implementierung.
   */
  private GWikiStorage storage;

  private GWikiJspProcessor jspProcessor = new GenomeJspProcessor();

  private GWikiAuthorization authorization = new GWikiUserAuthorization();

  private ContentSearcher contentSearcher = new SearchExpressionContentSearcher();

  private GWikiLogging logging = new GWikiLoggingBufferedLogger(new GWikiLoggingLog4J());

  private GWikiSessionProvider sessionProvider = new GWikiStandardSessionProvider();

  private GWikiI18nProvider i18nProvider = new GWikiI18nStandardProvider();

  private GWikiMimeTypeProvider mimeTypeProvider = new GWikiMimeTypeStandardProvider();

  private GWikiSchedulerProvider schedulerProvider = new GWikiStandardSchedulerProvider();

  private GWikiEmailProvider emailProvider = new GWikiStandardEmailProvider();

  private GWikiMenuProvider menuProvider = new GWikiStandardMenuProvider();

  private GWikiPageCache pageCache = new GWikiPageCacheTimedImpl();

  private Session mailSession;

  private boolean enableWebDav = false;

  private String webDavUserName;

  private String webDavPasswordHash;

  /**
   * if true, read the static/ from class path and not from web context path.
   * 
   * Must be true if working with GWiki Plugins.
   */
  private boolean staticContentFromClassPath = true;

  /**
   * if set, serve static content from this location.
   */
  private FileSystem staticContentFileSystem;

  private GWikiPluginRepository pluginRepository = new GWikiPluginRepository();

  private GWikiWikiSelector wikiSelector = new GWikiStandardWikiSelector();

  private BeanFactory beanFactory;

  public GWikiDAOContext()
  {

  }

  /**
   * Copy constructor.
   * 
   * @param other instance. must not be null.
   */
  public GWikiDAOContext(GWikiDAOContext other)
  {
    PrivateBeanUtils.copyInstanceProperties(other, this);
  }

  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("storage: ").append(ObjectUtils.toString(storage)).append("\n") //
        .append("authorization: ").append(ObjectUtils.toString(authorization)).append("\n") //
    ;
    return sb.toString();
  }

  @SuppressWarnings("unchecked")
  public <T> T getBean(String name, Class<T> requiredType)
  {
    return (T) beanFactory.getBean(name, requiredType);
  }

  public GWikiStorage getStorage()
  {
    return storage;
  }

  public void setStorage(GWikiStorage storage)
  {
    this.storage = storage;
  }

  public GWikiAuthorization getAuthorization()
  {
    return authorization;
  }

  public void setAuthorization(GWikiAuthorization authorization)
  {
    this.authorization = authorization;
  }

  public ContentSearcher getContentSearcher()
  {
    return contentSearcher;
  }

  public void setContentSearcher(ContentSearcher contentSearcher)
  {
    this.contentSearcher = contentSearcher;
  }

  public GWikiJspProcessor getJspProcessor()
  {
    return jspProcessor;
  }

  public void setJspProcessor(GWikiJspProcessor jspProcessor)
  {
    this.jspProcessor = jspProcessor;
  }

  public GWikiLogging getLogging()
  {
    return logging;
  }

  public void setLogging(GWikiLogging logging)
  {
    this.logging = logging;
  }

  public GWikiSessionProvider getSessionProvider()
  {
    return sessionProvider;
  }

  public void setSessionProvider(GWikiSessionProvider sessionProvider)
  {
    this.sessionProvider = sessionProvider;
  }

  public GWikiI18nProvider getI18nProvider()
  {
    return i18nProvider;
  }

  public void setI18nProvider(GWikiI18nProvider i18nProvider)
  {
    this.i18nProvider = i18nProvider;
  }

  public GWikiSchedulerProvider getSchedulerProvider()
  {
    return schedulerProvider;
  }

  public void setSchedulerProvider(GWikiSchedulerProvider schedulerProvider)
  {
    this.schedulerProvider = schedulerProvider;
  }

  public GWikiEmailProvider getEmailProvider()
  {
    return emailProvider;
  }

  public void setEmailProvider(GWikiEmailProvider emailProvider)
  {
    this.emailProvider = emailProvider;
  }

  public Session getMailSession()
  {
    return mailSession;
  }

  public void setMailSession(Session mailSession)
  {
    this.mailSession = mailSession;
  }

  public GWikiPageCache getPageCache()
  {
    return pageCache;
  }

  public void setPageCache(GWikiPageCache pageCache)
  {
    this.pageCache = pageCache;
  }

  public String getWebDavUserName()
  {
    return webDavUserName;
  }

  public void setWebDavUserName(String webDavUserName)
  {
    this.webDavUserName = webDavUserName;
  }

  public String getWebDavPasswordHash()
  {
    return webDavPasswordHash;
  }

  public void setWebDavPasswordHash(String webDavPasswordHash)
  {
    this.webDavPasswordHash = webDavPasswordHash;
  }

  public boolean isEnableWebDav()
  {
    return enableWebDav;
  }

  public void setEnableWebDav(boolean enableWebDav)
  {
    this.enableWebDav = enableWebDav;
  }

  public boolean isStaticContentFromClassPath()
  {
    return staticContentFromClassPath;
  }

  public void setStaticContentFromClassPath(boolean staticContentFromClassPath)
  {
    this.staticContentFromClassPath = staticContentFromClassPath;
  }

  public FileSystem getStaticContentFileSystem()
  {
    return staticContentFileSystem;
  }

  public void setStaticContentFileSystem(FileSystem staticContentFileSystem)
  {
    this.staticContentFileSystem = staticContentFileSystem;
  }

  public GWikiPluginRepository getPluginRepository()
  {
    return pluginRepository;
  }

  public void setPluginRepository(GWikiPluginRepository pluginRepository)
  {
    this.pluginRepository = pluginRepository;
  }

  public GWikiMenuProvider getMenuProvider()
  {
    return menuProvider;
  }

  public void setMenuProvider(GWikiMenuProvider menuProvider)
  {
    this.menuProvider = menuProvider;
  }

  public GWikiWikiSelector getWikiSelector()
  {
    return wikiSelector;
  }

  public void setWikiSelector(GWikiWikiSelector wikiSelector)
  {
    this.wikiSelector = wikiSelector;
  }

  public BeanFactory getBeanFactory()
  {
    return beanFactory;
  }

  public void setBeanFactory(BeanFactory beanFactory)
  {
    this.beanFactory = beanFactory;
  }

  public GWikiMimeTypeProvider getMimeTypeProvider()
  {
    return mimeTypeProvider;
  }

  public void setMimeTypeProvider(GWikiMimeTypeProvider mimeTypeProvider)
  {
    this.mimeTypeProvider = mimeTypeProvider;
  }

}
