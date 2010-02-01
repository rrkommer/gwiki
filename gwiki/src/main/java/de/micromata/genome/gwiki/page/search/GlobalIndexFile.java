/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   22.11.2009
// Copyright Micromata 22.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.search;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiAuthorizationRights;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.GWikiProps;
import de.micromata.genome.gwiki.model.GWikiStorage;
import de.micromata.genome.gwiki.model.config.GWikiMetaTemplate;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.GWikiConfigElement;
import de.micromata.genome.util.runtime.CallableX;

/**
 * Format. <id1\n LocalIndex entries >id1\n <id2 LocalIndex entries >id1\n
 * 
 * @author roger@micromata.de
 * 
 */
public class GlobalIndexFile implements GWikiPropKeys
{
  public static final String GLOBAL_INDEX_PAGEID = "admin/GlobalTextIndex";

  public static final String GLOBAL_INDEX_METAFILE_TEMPLATE = "admin/templates/intern/GlobalTextIndexMetaTemplate";

  public static GWikiElement createElement(GWikiContext ctx)
  {
    GWikiMetaTemplate metaTemplate = ctx.getWikiWeb().findMetaTemplate(GLOBAL_INDEX_METAFILE_TEMPLATE);
    GWikiProps props = new GWikiProps();
    props.setStringValue(WIKIMETATEMPLATE, GLOBAL_INDEX_METAFILE_TEMPLATE);
    props.setStringValue(TYPE, metaTemplate.getElementType());
    props.setStringValue(TITLE, "Global Full Text Index");
    props.setStringValue(AUTH_EDIT, GWikiAuthorizationRights.GWIKI_ADMIN.name());
    props.setStringValue(AUTH_CREATE, GWikiAuthorizationRights.GWIKI_ADMIN.name());
    props.setStringValue(AUTH_VIEW, GWikiAuthorizationRights.GWIKI_ADMIN.name());
    GWikiElementInfo ei = new GWikiElementInfo(props, metaTemplate);
    ei.setId(GLOBAL_INDEX_PAGEID);
    GWikiElement elementToEdit = ctx.getWikiWeb().getStorage().createElement(ei);
    //elementToEdit.setMetaTemplate(metaTemplate);
    return elementToEdit;
  }

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

  public static void writeGlobalIndexFiles(GWikiContext ctx, Collection<GWikiElementInfo> elements)
  {
    GWikiElement el = ctx.getWikiWeb().findElement(GLOBAL_INDEX_PAGEID);
    if (el == null) {
      el = createElement(ctx);
    }
    StringBuilder sb = new StringBuilder();
    IndexTextFilesContentSearcher searcher = new IndexTextFilesContentSearcher();
    for (GWikiElementInfo ei : elements) {
      String pageId = ei.getId();
      String indexFile = pageId + "TextIndex.txt";
      String content = searcher.readFileContent(ctx, indexFile);
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

  public static void updateSegment(GWikiStorage storage, GWikiElement elm, String indexContent)
  {
    final GWikiContext ctx = GWikiContext.getCurrent();
    GWikiElement el = ctx.getWikiWeb().findElement(GLOBAL_INDEX_PAGEID);
    if (el == null) {
      el = createElement(ctx);
    }
    GlobalWordIndexTextArtefakt art = (GlobalWordIndexTextArtefakt) el.getMainPart();

    String cont = art.getStorageData();
    String pageId = elm.getElementInfo().getId();
    String startTag = "<" + pageId + "\n";
    String endTag = ">" + pageId + "\n";

    int idx = cont.indexOf(startTag);
    String ncont;
    if (idx == -1) {
      ncont = cont + startTag + indexContent + endTag;
    } else {
      int eidx = cont.indexOf(endTag);
      if (eidx == -1) {
        // oops
        ncont = cont + startTag + indexContent + endTag;
      } else {
        ncont = cont.substring(0, idx) + startTag + indexContent + endTag + cont.substring(eidx + endTag.length());
      }
    }
    art.setStorageData(ncont);
    if (art.getIndexFileMap() != null) {
      art.getIndexFileMap().put(pageId, indexContent);
    }
    final GWikiElement fel = el;
    ctx.getWikiWeb().getAuthorization().runAsSu(ctx, new CallableX<Void, RuntimeException>() {

      public Void call() throws RuntimeException
      {
        ctx.getWikiWeb().getStorage().storeElement(ctx, fel, false);
        return null;
      }
    });

  }
}
