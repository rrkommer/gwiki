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

package de.micromata.genome.gwiki.page.impl.i18n;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

import de.micromata.genome.gwiki.model.GWikiAuthorization;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.GWikiI18nElement;

/**
 * Standard I18N provider uses I18N elements.
 * 
 * Roger Rene Kommer (r.kommer@artefaktur.com)
 * 
 */
public class GWikiI18nStandardProvider extends GWikiAbstractI18nProvider
{
  protected boolean containsContext(GWikiContext ctx, String pageId)
  {
    if (ctx.getI18nMaps() == null) {
      return false;
    }
    for (GWikiI18nElement m : ctx.getI18nMaps()) {
      if (m.getElementInfo().getId().equals(pageId) == true) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void addTranslationElement(GWikiContext ctx, String pageId)
  {
    if (containsContext(ctx, pageId) == true) {
      return;
    }
    GWikiI18nElement el = (GWikiI18nElement) ctx.getWikiWeb().getElement(pageId);
    if (ctx.getI18nMaps() == null) {
      ctx.setI18nMaps(new ArrayList<GWikiI18nElement>());
    }
    ctx.getI18nMaps().add(el);
  }

  @Override
  public String getI18nValue(GWikiContext ctx, String key, String defaultValue)
  {
    String lang = ctx.getWikiWeb().getAuthorization().getUserProp(ctx, GWikiAuthorization.USER_LANG);
    if (StringUtils.isEmpty(lang) == true) {
      lang = "en";
    }
    if (ctx.getI18nMaps() == null) {
      return defaultValue;
    }
    for (GWikiI18nElement m : ctx.getI18nMaps()) {
      String v = m.getMessage(lang, key);
      if (v != null) {
        return v;
      }
    }
    return defaultValue;
  }

}
