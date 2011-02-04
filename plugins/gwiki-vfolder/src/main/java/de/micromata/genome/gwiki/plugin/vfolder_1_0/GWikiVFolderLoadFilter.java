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
package de.micromata.genome.gwiki.plugin.vfolder_1_0;

import java.util.ArrayList;
import java.util.List;

import de.micromata.genome.gdbfs.FsObject;
import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.GWikiProps;
import de.micromata.genome.gwiki.model.GWikiSettingsProps;
import de.micromata.genome.gwiki.model.GWikiXmlConfigArtefakt;
import de.micromata.genome.gwiki.model.config.GWikiMetaTemplate;
import de.micromata.genome.gwiki.model.filter.GWikiFilterChain;
import de.micromata.genome.gwiki.model.filter.GWikiLoadElementInfosFilter;
import de.micromata.genome.gwiki.model.filter.GWikiLoadElementInfosFilterEvent;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiVFolderLoadFilter implements GWikiLoadElementInfosFilter
{
  public static final String VFILE_METATEMPLATEID = "admin/templates/VFileMetaTemplate";

  protected List<String> loadFiles(GWikiContext wikiContext, GWikiVFolderNode node)
  {
    List<FsObject> files = wikiContext.getWikiWeb().getStorage().getFileSystem().listFilesByPattern("", node.getMatcherRule(), 'F', true);
    List<String> ret = new ArrayList<String>();
    for (FsObject fso : files) {
      ret.add(fso.getName());
    }
    return ret;
  }

  protected void createElement(GWikiContext wikiContext, GWikiElementInfo ei, GWikiVFolderNode node, String fn)
  {
    if (fn.startsWith("/") == true) {
      fn = fn.substring(1);
    }
    GWikiProps props = new GWikiSettingsProps();
    props.setStringValue(GWikiPropKeys.PARENTPAGE, ei.getId());
    props.setStringValue("VFILE_FILE", fn);

    // TODO how to put node may derive GWikiElementInfo to hold node.
    GWikiMetaTemplate mt = wikiContext.getWikiWeb().findMetaTemplate(VFILE_METATEMPLATEID);
    GWikiElementInfo ninf = new GWikiElementInfo(props, mt);
    ninf.setId(fn);
    wikiContext.getWikiWeb().getDaoContext().getPageCache().putPageInfo(ninf);
  }

  protected void onLoadVFolderElement(GWikiContext wikiContext, GWikiElementInfo ei, GWikiVFolderNode node)
  {
    List<String> fl = loadFiles(wikiContext, node);
    for (String fn : fl) {
      createElement(wikiContext, ei, node, fn);
    }
  }

  protected void onLoadVFolderElement(GWikiContext wikiContext, GWikiElementInfo ei)
  {
    GWikiElement el = wikiContext.getWikiWeb().loadNewElement(ei.getId());
    GWikiArtefakt< ? > art = el.getPart("VFolderConfig");
    if ((art instanceof GWikiXmlConfigArtefakt< ? >) == false) {
      return;
    }
    GWikiXmlConfigArtefakt< ? > cfg = (GWikiXmlConfigArtefakt< ? >) art;
    List<GWikiVFolderNode> nodes = (List<GWikiVFolderNode>) cfg.getCompiledObject();
    if (nodes == null) {
      return;
    }
    for (GWikiVFolderNode node : nodes) {
      onLoadVFolderElement(wikiContext, ei, node);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.filter.GWikiFilter#filter(de.micromata.genome.gwiki.model.filter.GWikiFilterChain,
   * de.micromata.genome.gwiki.model.filter.GWikiFilterEvent)
   */
  public Void filter(GWikiFilterChain<Void, GWikiLoadElementInfosFilterEvent, GWikiLoadElementInfosFilter> chain,
      GWikiLoadElementInfosFilterEvent event)
  {
    chain.nextFilter(event);
    for (GWikiElementInfo ei : event.getPageInfos().values()) {
      if ("admin/templates/VFolderMetaTemplate".equals(ei.getProps().getStringValue(GWikiPropKeys.WIKIMETATEMPLATE)) == true) {
        onLoadVFolderElement(event.getWikiContext(), ei);
      }
    }
    return null;
  }

}
