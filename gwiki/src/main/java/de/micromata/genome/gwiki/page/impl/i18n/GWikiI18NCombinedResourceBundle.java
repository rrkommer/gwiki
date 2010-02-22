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
package de.micromata.genome.gwiki.page.impl.i18n;

import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.jsp.jstl.fmt.LocalizationContext;

import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.page.impl.GWikiI18nElement;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiI18NCombinedResourceBundle extends ResourceBundle
{
  private Locale locale;

  private LocalizationContext prevLocContext;

  private List<String> modules;

  public GWikiI18NCombinedResourceBundle(Locale locale, LocalizationContext prevLocContext, List<String> modules)
  {
    super();
    this.locale = locale;
    this.prevLocContext = prevLocContext;
    this.modules = modules;
  }

  @Override
  public Enumeration<String> getKeys()
  {
    // TODO gwiki?
    // wikiContext.getI18nMaps();
    return null;
  }

  protected Object getFromWiki(String key)
  {
    GWikiWeb wikiWeb = GWikiWeb.get();

    if (wikiWeb == null) {
      return null;
    }
    for (String module : modules) {
      GWikiElement el = wikiWeb.findElement(module);
      if (el == null) {
        // TODO gwiki warn
        continue;
      }
      if ((el instanceof GWikiI18nElement) == false) {
        // TODO gwiki warn
        continue;
      }
      GWikiI18nElement i18nel = (GWikiI18nElement) el;

      String v = i18nel.getMessage(locale.getLanguage(), key);
      if (v != null) {
        return v;
      }
    }
    return null;
  }

  @Override
  protected Object handleGetObject(String key)
  {
    Object ret = getFromWiki(key);
    if (ret != null) {
      return ret;
    }
    if (prevLocContext == null) {
      return null;
    }
    return prevLocContext.getResourceBundle().getObject(key);
  }
}
