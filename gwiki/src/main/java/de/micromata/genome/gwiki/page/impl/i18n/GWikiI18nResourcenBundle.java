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

import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

import de.micromata.genome.gwiki.page.GWikiContext;

public class GWikiI18nResourcenBundle extends ResourceBundle
{
  // TODO gwiki never used.
  private Locale locale;

  private GWikiContext wikiContext;

  public GWikiI18nResourcenBundle(GWikiContext wikiContext, Locale locale)
  {
    this.locale = locale;
    this.wikiContext = wikiContext;
  }

  @Override
  public Enumeration<String> getKeys()
  {
    // TODO gwiki?
    // wikiContext.getI18nMaps();
    return null;
  }

  @Override
  protected Object handleGetObject(String key)
  {
    return wikiContext.getWikiWeb().getI18nProvider().translate(wikiContext, key);
  }

}
