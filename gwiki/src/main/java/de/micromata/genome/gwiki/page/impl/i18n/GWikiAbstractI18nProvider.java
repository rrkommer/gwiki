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

import java.text.MessageFormat;

import de.micromata.genome.gwiki.model.GWikiI18nProvider;
import de.micromata.genome.gwiki.model.logging.GWikiLog;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.util.text.PlaceHolderReplacer;
import de.micromata.genome.util.text.StringResolver;

/**
 * 
 * @author Roger Rene Kommer (r.kommer@artefaktur.com)
 * 
 */
public abstract class GWikiAbstractI18nProvider implements GWikiI18nProvider
{
  public abstract String getI18nValue(GWikiContext ctx, String key, String defaultValue);

  @Override
  public String translate(GWikiContext ctx, String key)
  {
    return translate(ctx, key, "");
  }

  @Override
  public String translate(GWikiContext ctx, String key, String defaultValue, Object... args)
  {
    String value = getI18nValue(ctx, key, defaultValue);
    if (value == null) {
      if (defaultValue == null) {
        GWikiLog.warn("Message key has no translation: " + key);
        return "???" + key + "???";
      }
      return defaultValue;
    }
    return MessageFormat.format(value, args);
  }

  @Override
  public String translate(GWikiContext ctx, String key, String defaultValue)
  {
    return getI18nValue(ctx, key, defaultValue);
  }

  @Override
  public String translateProp(final GWikiContext ctx, String key)
  {
    if (key == null) {
      return key;
    }
    String ret = PlaceHolderReplacer.resolveReplace(key, "I{", "}", new StringResolver() {

      @Override
      public String resolve(String placeholder)
      {
        return translate(ctx, placeholder);
      }
    });
    return ret;
  }
}
