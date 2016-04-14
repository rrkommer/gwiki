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

package de.micromata.genome.gwiki.model;

import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Provides interationalization.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public interface GWikiI18nProvider
{
  public void addTranslationElement(GWikiContext ctx, String pageId);

  public String translate(GWikiContext ctx, String key);

  /**
   * key kann contains I${} values, which will replaced with text.
   * 
   * @param ctx
   * @param key
   * @return null if no mapping found
   */
  public String translateProp(GWikiContext ctx, String key);

  /**
   * See other translate
   * 
   * @param ctx
   * @param key
   * @param defaultValue
   * @return null if no mapping found
   */
  public String translate(GWikiContext ctx, String key, String defaultValue);

  /**
   * Translates
   * 
   * @param ctx wiki context
   * @param key i18n key
   * @param defaultValue return this, if no key was found. it returns ???key???
   * @param args optional args
   * @return
   */
  public String translate(GWikiContext ctx, String key, String defaultValue, Object... args);

}
