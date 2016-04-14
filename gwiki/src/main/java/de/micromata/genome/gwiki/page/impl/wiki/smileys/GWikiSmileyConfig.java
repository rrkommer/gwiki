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

package de.micromata.genome.gwiki.page.impl.wiki.smileys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiI18NArtefakt;
import de.micromata.genome.gwiki.model.GWikiI18nMap;
import de.micromata.genome.gwiki.page.GWikiContext;

public class GWikiSmileyConfig
{
  private Map<String, GWikiSmileyInfo> smileysByName = new HashMap<>();
  private Map<String, GWikiSmileyInfo> smileysByShortCut = new HashMap<>();
  private Map<String, GWikiSmileyInfo> smileys = new HashMap<>();
  // sorted list
  private List<GWikiSmileyInfo> smileyList = new ArrayList<>();
  private static GWikiSmileyConfig INSTANCE = null;
  private static GWikiElement CACHED_ELEMENT = null;

  public static GWikiSmileyConfig get(GWikiContext ctx)
  {
    if (INSTANCE == null) {
      INSTANCE = load(ctx);
    } else {
      GWikiElement ne = ctx.getWikiWeb().findElement("admin/config/SmileysConfig");
      if (ne != CACHED_ELEMENT) {
        INSTANCE = load(ctx, ne);
        CACHED_ELEMENT = ne;
      }
    }

    return INSTANCE;
  }

  private static GWikiSmileyConfig load(GWikiContext ctx)
  {
    CACHED_ELEMENT = ctx.getWikiWeb().findElement("admin/config/SmileysConfig");
    return load(ctx, CACHED_ELEMENT);
  }

  private static GWikiSmileyConfig load(GWikiContext ctx, GWikiElement configElement)
  {

    if (configElement == null) {
      return new GWikiSmileyConfig();
    }
    GWikiArtefakt<?> part = configElement.getMainPart();
    GWikiSmileyConfig nc = new GWikiSmileyConfig(ctx, (GWikiI18NArtefakt) part);
    return nc;

  }

  public GWikiSmileyConfig()
  {

  }

  public GWikiSmileyConfig(GWikiContext ctx, GWikiI18NArtefakt art)
  {
    GWikiI18nMap compiledObject = art.getCompiledObject();
    for (Map.Entry<String, String> me : compiledObject.entrySet()) {
      String key = me.getKey();
      String val = me.getValue();
      int idx = val.indexOf('|');
      String shortCut = null;
      if (idx != -1) {
        shortCut = val.substring(idx + 1);
        val = val.substring(0, idx);
      }
      GWikiSmileyInfo ni = new GWikiSmileyInfo(shortCut, key, val);
      smileysByName.put(key, ni);
      smileys.put(key, ni);
      if (StringUtils.isNotBlank(shortCut) == true) {
        smileysByShortCut.put(shortCut, ni);
        smileys.put(shortCut, ni);
      }

    }
    for (String key : compiledObject.getSortedKeys()) {
      smileyList.add(getSmileysByName().get(key));
    }
  }

  public Map<String, GWikiSmileyInfo> getSmileysByName()
  {
    return smileysByName;
  }

  public Map<String, GWikiSmileyInfo> getSmileysByShortCut()
  {
    return smileysByShortCut;
  }

  public Map<String, GWikiSmileyInfo> getSmileys()
  {
    return smileys;
  }

  public List<GWikiSmileyInfo> getSmileyList()
  {
    return smileyList;
  }

}
