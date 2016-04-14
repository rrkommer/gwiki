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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiI18NArtefakt;
import de.micromata.genome.gwiki.page.impl.GWikiI18nElement;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;
import de.micromata.genome.gwiki.utils.PropUtils;
import de.micromata.genome.util.types.Pair;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiI18NAjaxEditActionBean extends ActionBeanBase
{
  private String backUrl;

  private String key;

  private String pageId;

  private List<Pair<String, String>> langValues = new ArrayList<Pair<String, String>>();

  private GWikiI18nElement i18El;

  protected List<Pair<String, GWikiI18NArtefakt>> getArtefakts()
  {
    if (StringUtils.isEmpty(pageId) == true || StringUtils.isEmpty(key) == true) {
      wikiContext.addSimpleValidationError("pageId or key is not set");
      return null;
    }
    GWikiElement el = wikiContext.getWikiWeb().findElement(pageId);
    if ((el instanceof GWikiI18nElement) == false) {
      wikiContext.addSimpleValidationError("Cannot find pageId or not i18n: " + pageId);
      return null;
    }
    i18El = (GWikiI18nElement) el;
    Map<String, GWikiArtefakt< ? >> map = new HashMap<String, GWikiArtefakt< ? >>();
    List<Pair<String, GWikiI18NArtefakt>> ret = new ArrayList<Pair<String, GWikiI18NArtefakt>>();
    i18El.collectParts(map);
    for (Map.Entry<String, GWikiArtefakt< ? >> me : map.entrySet()) {
      if ((me.getValue() instanceof GWikiI18NArtefakt) == false) {
        continue;
      }
      ret.add(Pair.make(me.getKey(), (GWikiI18NArtefakt) me.getValue()));
    }
    return ret;
  }

  @Override
  public Object onInit()
  {
    List<Pair<String, GWikiI18NArtefakt>> afc = getArtefakts();
    if (afc == null) {
      return null;
    }
    for (Pair<String, GWikiI18NArtefakt> me : afc) {
      langValues.add(Pair.make(me.getKey(), me.getValue().getCompiledObject().get(key)));
    }
    return null;
  }

  public Object onStore()
  {
    List<Pair<String, GWikiI18NArtefakt>> afc = getArtefakts();
    for (Pair<String, GWikiI18NArtefakt> p : afc) {
      String v = wikiContext.getRequestParameter(p.getKey() + "_value");
      p.getSecond().getCompiledObject().put(key, v);
      p.getSecond().setStorageData(PropUtils.fromProperties(p.getSecond().getCompiledObject()));
    }
    getWikiContext().getWikiWeb().saveElement(wikiContext, i18El, false);
    return null;
  }

  public String getKey()
  {
    return key;
  }

  public void setKey(String key)
  {
    this.key = key;
  }

  public String getPageId()
  {
    return pageId;
  }

  public void setPageId(String pageId)
  {
    this.pageId = pageId;
  }

  public List<Pair<String, String>> getLangValues()
  {
    return langValues;
  }

  public void setLangValues(List<Pair<String, String>> langValues)
  {
    this.langValues = langValues;
  }

  public String getBackUrl()
  {
    return backUrl;
  }

  public void setBackUrl(String backUrl)
  {
    this.backUrl = backUrl;
  }

}
