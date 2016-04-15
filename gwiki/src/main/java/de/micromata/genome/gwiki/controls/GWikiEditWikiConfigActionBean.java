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

package de.micromata.genome.gwiki.controls;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gdbfs.FileNameUtils;
import de.micromata.genome.gwiki.model.AuthorizationFailedException;
import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiAuthorizationRights;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.page.impl.GWikiEditableArtefakt;
import de.micromata.genome.gwiki.page.impl.GWikiEditorArtefakt;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;
import de.micromata.genome.gwiki.page.search.QueryResult;
import de.micromata.genome.gwiki.page.search.SearchQuery;
import de.micromata.genome.gwiki.page.search.SearchResult;
import de.micromata.genome.util.collections.ArrayMap;
import de.micromata.genome.util.types.Pair;

/**
 * Class to edit group configs.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiEditWikiConfigActionBean extends ActionBeanBase
{
  /**
   * pageId to config to edit.
   */
  protected Map<String, Pair<GWikiElement, GWikiArtefakt<?>>> configs = new TreeMap<String, Pair<GWikiElement, GWikiArtefakt<?>>>();

  protected ArrayMap<String, GWikiEditorArtefakt<?>> editors = new ArrayMap<String, GWikiEditorArtefakt<?>>();

  private String backUrl;

  protected boolean init()
  {
    String queryS = "PROP:PAGEID like \"admin/config/*\" and NOT (PROP:PAGEID like \"admin/config*/*\") and PROP:PAGEID like \"*Config\" ";
    SearchQuery query = new SearchQuery(queryS, wikiContext.getWikiWeb().getElementInfos(),
        wikiContext.getWikiWeb().getElementInfoCount());
    query.setFindUnindexed(true);
    QueryResult result = wikiContext.getWikiWeb().getDaoContext().getContentSearcher().search(wikiContext, query);
    for (SearchResult sr : result.getResults()) {
      String pid = sr.getPageId();
      GWikiElement el = wikiContext.getWikiWeb().findElement(pid);
      if (el == null) {
        continue;
      }
      GWikiArtefakt<?> art = el.getMainPart();
      if ((art instanceof GWikiEditableArtefakt) == false) {
        continue;
      }
      GWikiEditableArtefakt editableArt = (GWikiEditableArtefakt) art;
      configs.put(pid, new Pair<GWikiElement, GWikiArtefakt<?>>(el, art));
      String partName = FileNameUtils.getNamePart(pid);

      editors.put(FileNameUtils.getNamePart(pid), editableArt.getEditor(el, null, partName));
    }
    Collections.sort(editors.getEntries(), new Comparator<Map.Entry<String, GWikiEditorArtefakt<?>>>()
    {

      @Override
      public int compare(Entry<String, GWikiEditorArtefakt<?>> o1, Entry<String, GWikiEditorArtefakt<?>> o2)
      {
        int cmp = o1.getKey().compareTo(o2.getKey());
        if (cmp == 0) {
          return 0;
        }
        if (o1.getKey().equals("GWikiConfig") == true) {
          return -1;
        }
        if (o2.getKey().equals("GWikiConfig") == true) {
          return 1;
        }
        return cmp;
      }
    });
    for (GWikiEditorArtefakt<?> ea : editors.values()) {
      ea.prepareHeader(wikiContext);
    }
    return true;
  }

  protected void checkAccess()
  {
    if (wikiContext.getWikiWeb().getAuthorization().isAllowTo(wikiContext,
        GWikiAuthorizationRights.GWIKI_ADMIN.name()) == false) {
      throw new AuthorizationFailedException(translate("gwiki.authorization.message.cannoteditpage",
          GWikiAuthorizationRights.GWIKI_ADMIN.name()));
    }
  }

  @Override
  public Object onInit()
  {
    if (init() == false) {
      return null;
    }
    checkAccess();

    return null;
  }

  protected Object goBack()
  {
    if (StringUtils.isNotBlank(backUrl) == true) {
      return backUrl;
    }
    return getWikiContext().getWikiWeb().getHomeElement(getWikiContext());
  }

  public Object onSave()
  {
    if (init() == false) {
      return null;
    }
    checkAccess();
    for (Map.Entry<String, Pair<GWikiElement, GWikiArtefakt<?>>> me : configs.entrySet()) {
      String pid = me.getKey();
      GWikiElement el = wikiContext.getWikiWeb().findElement(pid);
      if (el == null) {
        continue;
      }
      String partName = FileNameUtils.getNamePart(pid);
      GWikiEditorArtefakt<?> ped = editors.get(partName);
      if (ped == null) {
        continue;
      }
      ped.onSave(wikiContext);
      if (wikiContext.hasValidationErrors() == true) {
        break;
      }
      wikiContext.getWikiWeb().saveElement(wikiContext, me.getValue().getFirst(), false);
    }
    if (wikiContext.hasValidationErrors() == false) {
      wikiContext.getWikiWeb().getDaoContext().reinitConfig();
      return goBack();
    }
    return null;
  }

  public Object onCancel()
  {
    return goBack();
  }

  public ArrayMap<String, GWikiEditorArtefakt<?>> getEditors()
  {
    return editors;
  }

  public void setEditors(ArrayMap<String, GWikiEditorArtefakt<?>> editors)
  {
    this.editors = editors;
  }

  public Map<String, Pair<GWikiElement, GWikiArtefakt<?>>> getConfigs()
  {
    return configs;
  }

  public void setConfigs(Map<String, Pair<GWikiElement, GWikiArtefakt<?>>> configs)
  {
    this.configs = configs;
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
