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

package de.micromata.genome.gwiki.page.impl.actionbean;

import org.apache.commons.lang3.ObjectUtils;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.utils.WebUtils;

public class ActionBeanBase implements ActionBean
{
  protected GWikiContext wikiContext;

  /**
   * for private forms.
   * 
   * @return
   */
  @Override
  public String getRequestPrefix()
  {
    return "";
  }

  @Override
  public Object onInit()
  {
    return null;
  }

  @Override
  public GWikiContext getWikiContext()
  {
    return wikiContext;
  }

  @Override
  public void setWikiContext(GWikiContext wikiContext)
  {
    this.wikiContext = wikiContext;
  }

  public Object noForward()
  {
    return NoForward.class;
  }

  protected String getReqParam(String key)
  {
    return wikiContext.getRequestParameter(key);
  }

  /**
   * Little helper method.
   * 
   * @param key
   * @return translated.
   */
  protected String translate(String key, Object... args)
  {
    return wikiContext.getWikiWeb().getI18nProvider().translate(wikiContext, key, null, args);
  }

  /**
   * Escape html output
   * 
   * @param text
   * @return
   */
  protected final String esc(String text)
  {
    if (text == null) {
      return "";
    }
    return WebUtils.escapeHtml(text);
  }

  protected final String translateEsc(String key, Object... args)
  {
    return esc(translate(key, args));
  }

  public ActionBeanBase print(String s)
  {
    wikiContext.append(s);
    return this;
  }

  public ActionBeanBase print(Object s)
  {
    return print(ObjectUtils.toString(s));
  }
}
