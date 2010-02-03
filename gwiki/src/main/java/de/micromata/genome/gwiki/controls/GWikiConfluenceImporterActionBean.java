/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   27.11.2009
// Copyright Micromata 27.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.controls;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gdbfs.FileNameUtils;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.search.QueryResult;
import de.micromata.genome.gwiki.page.search.SearchQuery;
import de.micromata.genome.gwiki.page.search.SearchResult;
import de.micromata.genome.util.types.Converter;

/**
 * ActionBean for importing confluence XML-Export files.
 * 
 * @author roger@micromata.de
 * 
 */
public class GWikiConfluenceImporterActionBean extends GWikiPageListActionBean
{
  public static final String ONLYNEW = "ONLYNEW";

  public static final String ONLYNEWER = "ONLYNEWER";

  public static final String OVERWRITEALL = "OVERWRITEALL";

  public static enum CompareStatus
  {
    /**
     * source is new
     */
    NEW, //
    /**
     * source is newer
     */
    NEWER, //
    /**
     * either source or target has no modification date.
     */
    EXISTS, //
    /**
     * source is older.o
     */
    OLDER, //
    /**
     * source na target has same modification time.
     */
    EQUAL, //

  }

  private String tmpDirName;

  /**
   * ONLYNEW ONLYNEWER OVERWRITEALL
   */
  private String overWriteMode = "ONLYNEW";

  private String targetDir = "";

  private boolean onlyChecked;

  private String selIds;

  @Override
  public Object onInit()
  {
    // super.onInit();
    return null;
  }

  public Object onUpload()
  {
    String name = "dataFile_attachment";
    FileItem dataFile = wikiContext.getFileItem(name);
    if (dataFile == null) {
      wikiContext.addSimpleValidationError("Keine Datei hochgeladen");
      return null;
    }
    GWikiMountedWeb mountedWeb = new GWikiMountedWeb();
    // mountedWeb.initialize(wikiContext, dataFile, co);
    tmpDirName = mountedWeb.getTmpDirName();
    return null;
  }

  protected boolean filterBeforeQuery(GWikiElementInfo ei)
  {
    return ei.getId().startsWith(tmpDirName);
  }

  public static void copy(GWikiContext wikiContext, GWikiElementInfo ei, String newId)
  {
    GWikiElement element = wikiContext.getWikiWeb().getStorage().loadElement(ei);
    element.getElementInfo().setId(newId);
    wikiContext.getWikiWeb().saveElement(wikiContext, element, true);
  }

  public Object onImport()
  {
    List<String> ids;
    if (onlyChecked == true) {
      ids = Converter.parseStringTokens(selIds, ",", false);
    } else {
      QueryResult query = query();
      ids = new ArrayList<String>(query.getResults().size());
      for (SearchResult sr : query.getResults()) {
        ids.add(getPageIdNoTemp(sr.getElementInfo()));
      }
    }
    if (ids.size() == 0) {
      wikiContext.append("Keine Elemente ausgew&auml;hlt").flush();
      return noForward();
    }
    // TODO gwiki parentIds patchen!

    // List<GWikiElementInfo> elements = new ArrayList<GWikiElementInfo>(ids.size());
    StringBuilder sb = new StringBuilder();
    sb.append("Imported:\n<br>");
    for (String id : ids) {
      String tid = FileNameUtils.join(tmpDirName, id);
      GWikiElementInfo ei = wikiContext.getWikiWeb().getStorage().loadElementInfo(tid);
      if (ei == null) {
        sb.append("\n<br/>WARN: " + tid + " not found");
        continue;
      }
      String oei = getPageIdNoTemp(ei);
      CompareStatus st = getCompareStatus(wikiContext, ei, oei);
      switch (st) {
        case OLDER:
          // no break
        case EQUAL:
          if (OVERWRITEALL.equals(overWriteMode) == false) {
            continue;
          }
          // no break
        case NEWER:
          if (ONLYNEW.equals(overWriteMode) == true) {
            continue;
          }
          // no break
        case NEW:
          copy(wikiContext, ei, oei);
          sb.append("<li>").append(oei).append("</li>\n");
          break;
      }

    }
    wikiContext.append(sb.toString()).flush();
    return noForward();
  }

  protected QueryResult query()
  {
    if (StringUtils.isBlank(tmpDirName) == true) {
      writeXmlErrorResponse("Keine Dateien gefunden");
      return null;
    }
    String searchExpr = "";
    try {
      List<GWikiElementInfo> elems = wikiContext.getWikiWeb().getStorage().loadPageInfos(tmpDirName);
      List<SearchResult> sr = new ArrayList<SearchResult>(elems.size());
      for (GWikiElementInfo wi : elems) {
        String oei = getPageIdNoTemp(wi);
        CompareStatus st = getCompareStatus(wikiContext, wi, oei);
        wi.getProps().setStringValue("IMPSTATUS", st.name());
        sr.add(new SearchResult(wi));
      }
      searchExpr = getSearchExpression();
      SearchQuery query = new SearchQuery(StringUtils.defaultString(searchExpr), false, sr);

      QueryResult qr = filter(query);
      return qr;
    } catch (Exception ex) {
      wikiContext.append(ex.getMessage() + " for " + searchExpr);
      return null;
    }
  }

  public Object onFilter()
  {
    QueryResult qr = query();
    if (qr == null) {
      wikiContext.flush();
      return noForward();
    }
    writeXmlResult(qr);
    return noForward();
  }

  protected String getPageIdNoTemp(GWikiElementInfo ei)
  {
    String sf = ei.getId();
    String tmpdir = tmpDirName;
    if (tmpdir.startsWith("/") == true) {
      tmpdir = tmpdir.substring(1);
    }
    if (sf.startsWith(tmpdir) == true) {
      sf = sf.substring(tmpdir.length());
    }
    if (sf.startsWith("/") == true) {
      sf = sf.substring(1);
    }
    return sf;
  }

  public static CompareStatus getCompareStatus(GWikiContext wikiContext, GWikiElementInfo ei, String oldPageId)
  {
    GWikiElementInfo oei = wikiContext.getWikiWeb().getStorage().loadElementInfo(oldPageId);
    if (oei == null) {
      return CompareStatus.NEW;
    }
    Date nd = ei.getModifiedAt();
    Date od = oei.getModifiedAt();
    if (od == null || nd == null) {
      return CompareStatus.EXISTS;
    }
    if (nd.after(od) == true) {
      return CompareStatus.NEWER;
    } else if (nd.before(od) == true) {
      return CompareStatus.OLDER;
    }
    return CompareStatus.EQUAL;
  }

  public String renderField(String fieldName, GWikiElementInfo ei)
  {
    // if (fieldName)
    if (fieldName.equals("PAGEID") == true) {
      String sf = getPageIdNoTemp(ei);
      return sf;
      // } else if (fieldName.equals("IMPSTATUS") == true) {
      // String sf = getPageIdNoTemp(ei);
      // CompareStatus st = getCompareStatus(wikiContext, ei, sf);
      // return st.name();
    } else {
      return super.renderField(fieldName, ei);

    }

  }

  public String getTmpDirName()
  {
    return tmpDirName;
  }

  public void setTmpDirName(String tmpDirName)
  {
    this.tmpDirName = tmpDirName;
  }

  public String getTargetDir()
  {
    return targetDir;
  }

  public void setTargetDir(String targetDir)
  {
    this.targetDir = targetDir;
  }

  public String getSelIds()
  {
    return selIds;
  }

  public void setSelIds(String selIds)
  {
    this.selIds = selIds;
  }

  public String getOverWriteMode()
  {
    return overWriteMode;
  }

  public void setOverWriteMode(String overWriteMode)
  {
    this.overWriteMode = overWriteMode;
  }

  public boolean isOnlyChecked()
  {
    return onlyChecked;
  }

  public void setOnlyChecked(boolean onlyChecked)
  {
    this.onlyChecked = onlyChecked;
  }
}
