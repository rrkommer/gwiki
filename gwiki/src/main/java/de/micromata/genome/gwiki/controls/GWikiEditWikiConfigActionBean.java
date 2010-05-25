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
package de.micromata.genome.gwiki.controls;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import de.micromata.genome.gdbfs.FileNameUtils;
import de.micromata.genome.gwiki.model.AuthorizationFailedException;
import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiAuthorizationRights;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiPropsArtefakt;
import de.micromata.genome.gwiki.page.impl.GWikiEditorArtefakt;
import de.micromata.genome.gwiki.page.impl.GWikiPropsEditorArtefakt;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;
import de.micromata.genome.gwiki.page.search.QueryResult;
import de.micromata.genome.gwiki.page.search.SearchQuery;
import de.micromata.genome.gwiki.page.search.SearchResult;
import de.micromata.genome.util.types.ArrayMap;
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
   * pageId -> config to edit.
   */
  protected Map<String, Pair<GWikiElement, GWikiPropsArtefakt>> configs = new TreeMap<String, Pair<GWikiElement, GWikiPropsArtefakt>>();

  protected ArrayMap<String, GWikiEditorArtefakt< ? >> editors = new ArrayMap<String, GWikiEditorArtefakt< ? >>();

  protected boolean init()
  {
    String queryS = "PROP:PAGEID like \"admin/config/*\" and NOT (PROP:PAGEID like \"admin/config*/*\") and PROP:PAGEID like \"*Config\" ";
    SearchQuery query = new SearchQuery(queryS, wikiContext.getWikiWeb().getPageInfos());
    QueryResult result = wikiContext.getWikiWeb().getDaoContext().getContentSearcher().search(wikiContext, query);
    for (SearchResult sr : result.getResults()) {
      String pid = sr.getPageId();
      GWikiElement el = wikiContext.getWikiWeb().findElement(pid);
      if (el == null) {
        continue;
      }
      GWikiArtefakt< ? > art = el.getMainPart();
      if ((art instanceof GWikiPropsArtefakt) == false) {
        continue;
      }
      GWikiPropsArtefakt propArt = (GWikiPropsArtefakt) art;
      configs.put(pid, Pair.make(el, propArt));
      String partName = FileNameUtils.getNamePart(pid);

      editors.put(FileNameUtils.getNamePart(pid), propArt.getEditor(el, null, partName));
    }
    Collections.sort(editors.getEntries(), new Comparator<Map.Entry<String, GWikiEditorArtefakt< ? >>>() {

      public int compare(Entry<String, GWikiEditorArtefakt< ? >> o1, Entry<String, GWikiEditorArtefakt< ? >> o2)
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
    return true;
  }

  protected void checkAccess()
  {
    if (wikiContext.getWikiWeb().getAuthorization().isAllowTo(wikiContext, GWikiAuthorizationRights.GWIKI_ADMIN.name()) == false) {
      throw new AuthorizationFailedException(translate("gwiki.authorization.message.cannoteditpage", GWikiAuthorizationRights.GWIKI_ADMIN
          .name()));
    }
  }

  public Object onInit()
  {
    if (init() == false) {
      return null;
    }
    checkAccess();

    return null;
  }

  public Object onSave()
  {
    if (init() == false) {
      return null;
    }
    checkAccess();
    for (Map.Entry<String, Pair<GWikiElement, GWikiPropsArtefakt>> me : configs.entrySet()) {
      String pid = me.getKey();
      GWikiElement el = wikiContext.getWikiWeb().findElement(pid);
      if (el == null) {
        continue;
      }
      GWikiArtefakt< ? > art = el.getMainPart();
      if ((art instanceof GWikiPropsArtefakt) == false) {
        continue;
      }
      String partName = FileNameUtils.getNamePart(pid);
      GWikiPropsEditorArtefakt< ? > ped = (GWikiPropsEditorArtefakt< ? >) editors.get(partName);
      if (ped == null) {
        continue;
      }
      ped.onSave(wikiContext);
      if (wikiContext.hasValidationErrors() == true) {
        break;
      }
      wikiContext.getWikiWeb().saveElement(wikiContext, me.getValue().getFirst(), false);

    }
    return null;
  }

  public Object onCancel()
  {
    return null;
  }

  public ArrayMap<String, GWikiEditorArtefakt< ? >> getEditors()
  {
    return editors;
  }

  public void setEditors(ArrayMap<String, GWikiEditorArtefakt< ? >> editors)
  {
    this.editors = editors;
  }

  public Map<String, Pair<GWikiElement, GWikiPropsArtefakt>> getConfigs()
  {
    return configs;
  }

  public void setConfigs(Map<String, Pair<GWikiElement, GWikiPropsArtefakt>> configs)
  {
    this.configs = configs;
  }

}
