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

package de.micromata.genome.gwiki.page.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import de.micromata.genome.gwiki.model.GWikiAuthorizationRights;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.GWikiProps;
import de.micromata.genome.gwiki.model.GWikiSettingsProps;
import de.micromata.genome.gwiki.model.GWikiStorage;
import de.micromata.genome.gwiki.model.config.GWikiMetaTemplate;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.util.runtime.CallableX;
import de.micromata.genome.util.types.Pair;

/**
 * Format. &lt;id1\n LocalIndex entries &gt;id1\n &lt;id2 LocalIndex entries &gt;id1\n
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GlobalIndexFile implements GWikiPropKeys
{

  /**
   * The Constant GLOBAL_INDEX_PAGEID.
   */
  public static final String GLOBAL_INDEX_PAGEID = "admin/GlobalTextIndex";

  /**
   * The Constant GLOBAL_INDEX_METAFILE_TEMPLATE.
   */
  public static final String GLOBAL_INDEX_METAFILE_TEMPLATE = "admin/templates/intern/GlobalTextIndexMetaTemplate";

  /**
   * Creates the element.
   *
   * @param ctx the ctx
   * @return the g wiki element
   */
  public static GWikiElement createElement(GWikiContext ctx)
  {
    GWikiMetaTemplate metaTemplate = ctx.getWikiWeb().findMetaTemplate(GLOBAL_INDEX_METAFILE_TEMPLATE);
    GWikiProps props = new GWikiSettingsProps();
    props.setStringValue(WIKIMETATEMPLATE, GLOBAL_INDEX_METAFILE_TEMPLATE);
    // props.setStringValue(TYPE, metaTemplate.getElementType());
    props.setStringValue(TITLE, "Global Full Text Index");
    props.setStringValue(AUTH_EDIT, GWikiAuthorizationRights.GWIKI_ADMIN.name());
    props.setStringValue(AUTH_CREATE, GWikiAuthorizationRights.GWIKI_ADMIN.name());
    props.setStringValue(AUTH_VIEW, GWikiAuthorizationRights.GWIKI_ADMIN.name());
    GWikiElementInfo ei = new GWikiElementInfo(props, metaTemplate);
    ei.setId(GLOBAL_INDEX_PAGEID);
    GWikiElement elementToEdit = ctx.getWikiWeb().getStorage().createElement(ei);
    // elementToEdit.setMetaTemplate(metaTemplate);
    return elementToEdit;
  }

  /**
   * Write global index files.
   *
   * @param ctx the ctx
   * @param map the map
   */
  public static void writeGlobalIndexFiles(GWikiContext ctx, Map<String, WordIndexTextArtefakt> map)
  {
    GWikiElement el = ctx.getWikiWeb().findElement(GLOBAL_INDEX_PAGEID);
    if (el == null) {
      el = createElement(ctx);
    }
    StringBuilder sb = new StringBuilder();
    for (Map.Entry<String, WordIndexTextArtefakt> me : map.entrySet()) {
      String content = me.getValue().getStorageData();
      if (StringUtils.isNotEmpty(content) == true) {
        sb.append("<").append(me.getKey()).append("\n");
        sb.append(content);
        sb.append(">").append(me.getKey()).append("\n");
      }
    }
    GlobalWordIndexTextArtefakt art = (GlobalWordIndexTextArtefakt) el.getMainPart();
    art.setStorageData(sb.toString());
    ctx.getWikiWeb().saveElement(ctx, el, false);
  }

  /**
   * Write global index files.
   *
   * @param ctx the ctx
   * @param elements the elements
   */
  public static void writeGlobalIndexFiles(GWikiContext ctx, Collection<GWikiElementInfo> elements)
  {
    GWikiElement el = ctx.getWikiWeb().findElement(GLOBAL_INDEX_PAGEID);
    if (el == null) {
      el = createElement(ctx);
    }
    StringBuilder sb = new StringBuilder();
    for (GWikiElementInfo ei : elements) {
      String pageId = ei.getId();
      String indexFile = pageId + "TextIndex.txt";
      String content = IndexTextFilesContentSearcher.readFileContent(ctx, indexFile);
      if (StringUtils.isNotEmpty(content) == true) {
        sb.append("<").append(pageId).append("\n");
        sb.append(content);
        sb.append(">").append(pageId).append("\n");
      }
    }
    GlobalWordIndexTextArtefakt art = (GlobalWordIndexTextArtefakt) el.getMainPart();
    art.setStorageData(sb.toString());
    ctx.getWikiWeb().saveElement(ctx, el, false);
  }

  /**
   * Update segments.
   *
   * @param storage the storage
   * @param elmL the elm l
   */
  public static void updateSegments(GWikiStorage storage, List<Pair<GWikiElement, String>> elmL)
  {
    final GWikiContext ctx = GWikiContext.getCurrent();
    GWikiElement el = ctx.getWikiWeb().findElement(GLOBAL_INDEX_PAGEID);
    if (el == null) {
      el = createElement(ctx);
    }
    GlobalWordIndexTextArtefakt art = (GlobalWordIndexTextArtefakt) el.getMainPart();

    String cont = StringUtils.defaultString(art.getStorageData());
    for (Pair<GWikiElement, String> elmp : elmL) {
      GWikiElement elm = elmp.getFirst();
      String pageId = elm.getElementInfo().getId();
      String startTag = "<" + pageId + "\n";
      String endTag = ">" + pageId + "\n";

      int idx = cont.indexOf(startTag);
      String ncont;
      if (idx == -1) {
        ncont = cont + startTag + elmp.getSecond() + endTag;
      } else {
        int eidx = cont.indexOf(endTag);
        if (eidx == -1) {
          // oops
          ncont = cont + startTag + elmp.getSecond() + endTag;
        } else {
          ncont = cont.substring(0, idx) + startTag + elmp.getSecond() + endTag
              + cont.substring(eidx + endTag.length());
        }
      }
      cont = ncont;
      if (art.getIndexFileMap() != null) {
        art.getIndexFileMap().put(pageId, elmp.getSecond());
      }
    }
    art.setStorageData(cont);

    final GWikiElement fel = el;
    ctx.getWikiWeb().getAuthorization().runAsSu(ctx, new CallableX<Void, RuntimeException>()
    {

      @Override
      public Void call() throws RuntimeException
      {
        ctx.getWikiWeb().getStorage().storeElement(ctx, fel, false);
        return null;
      }
    });
  }

  /**
   * Update segment.
   *
   * @param storage the storage
   * @param elm the elm
   * @param indexContent the index content
   */
  public static void updateSegment(GWikiStorage storage, GWikiElement elm, String indexContent)
  {

    List<Pair<GWikiElement, String>> l = new ArrayList<Pair<GWikiElement, String>>(1);
    l.add(Pair.make(elm, indexContent));
    updateSegments(storage, l);
  }
}
