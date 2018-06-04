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

package de.micromata.genome.gwiki.plugin.vfolder_1_0;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import de.micromata.genome.gdbfs.FileNameUtils;
import de.micromata.genome.gdbfs.FileSystem;
import de.micromata.genome.gdbfs.FsObject;
import de.micromata.genome.gdbfs.ReadWriteCombinedFileSystem;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiPageCache;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.GWikiProps;
import de.micromata.genome.gwiki.model.GWikiSettingsProps;
import de.micromata.genome.gwiki.model.GWikiWebUtils;
import de.micromata.genome.gwiki.model.logging.GWikiLog;
import de.micromata.genome.gwiki.model.logging.GWikiLogCategory;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.attachments.TextExtractorUtils;
import de.micromata.genome.gwiki.utils.AppendableI;
import de.micromata.genome.logging.GLog;
import de.micromata.genome.util.text.PipeValueList;
import de.micromata.genome.util.types.Converter;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiVFolderUtils
{
  public static final String FVOLDER = "FVOLDER";

  public static final String VFOLDERCACHEFILE = "gwikivfoldercache.txt";

  public static final String VFOLDERSTATUSFILE = "gwikivfolderstatus.txt";

  public static final String VDIRMETATEMPLATE = "admin/templates/intern/VDirMetaTemplate";

  public static final String VFILEMETATEMPLATE = "admin/templates/intern/VFileMetaTemplate";

  public static Pattern titlePattern = Pattern.compile(".*<title>(.*)</title>.*", Pattern.DOTALL);

  public static String[] indexFileNames = new String[] { "index.html", "Index.html", "index.htm", "Index.htm" };

  public static final String VFILE_INDEXHTML = "VFILE_INDEXHTML";

  public static List<GWikiElementInfo> loadFsElements(GWikiContext wikiContext, GWikiElement el, GWikiVFolderNode node)
  {
    GWikiVFolderCachedFileInfos cache = readCache(node);
    List<GWikiElementInfo> ell = resolveParents(wikiContext, el, cache);
    return ell;

  }

  public static void mountFs(GWikiContext wikiContext, GWikiElement el, GWikiVFolderNode node)
  {
    List<GWikiElementInfo> ell = loadFsElements(wikiContext, el, node);
    GWikiPageCache pageCache = wikiContext.getWikiWeb().getPageCache();
    for (GWikiElementInfo ei : ell) {
      pageCache.putPageInfo(ei);
    }
  }

  public static void dismountFs(GWikiContext wikiContext, GWikiElement el, GWikiVFolderNode node)
  {
    GWikiVFolderCachedFileInfos cache = readCache(node);
    GWikiPageCache pageCache = wikiContext.getWikiWeb().getPageCache();

    for (Map.Entry<String, GWikiElementInfo> me : cache.getVfolderFiles().entrySet()) {
      String id = el.getElementInfo().getId() + "/" + me.getKey();
      pageCache.removePageInfo(id);
    }
  }

  public static String getParentId(GWikiContext wikiContext, GWikiVFolderCachedFileInfos cache, String parentPath)
  {
    for (String idxf : indexFileNames) {
      String fqp = parentPath + "/" + idxf;
      GWikiElementInfo pei = cache.getElementInfoByLocalName(fqp);
      if (pei != null) {
        return pei.getId();
      }
    }
    GWikiElementInfo pei = cache.getElementInfoByLocalName(parentPath);
    if (pei != null) {
      return parentPath;
    }
    return null;
  }

  protected static void resolveIndexHtmls(GWikiContext wikiContext, GWikiElement el, GWikiVFolderCachedFileInfos cache)
  {
    for (Map.Entry<String, GWikiElementInfo> me : cache.getVfolderFiles().entrySet()) {
      String parentPath = me.getKey();

      for (String idxf : indexFileNames) {
        String fqp = parentPath + "/" + idxf;
        GWikiElementInfo pei = cache.getElementInfoByLocalName(fqp);
        if (pei != null) {
          me.getValue().getProps().setStringValue(VFILE_INDEXHTML, el.getElementInfo().getId() + "/" + fqp);
        }
      }
    }
  }

  protected static boolean isIndexHtmlFile(String id)
  {
    for (String idxf : indexFileNames) {
      if (id.endsWith(idxf) == true) {
        return true;
      }
    }
    return false;
  }

  public static List<GWikiElementInfo> resolveParents(GWikiContext wikiContext, GWikiElement el,
      GWikiVFolderCachedFileInfos cache)
  {
    resolveIndexHtmls(wikiContext, el, cache);
    List<GWikiElementInfo> ret = new ArrayList<GWikiElementInfo>();
    for (Map.Entry<String, GWikiElementInfo> me : cache.getVfolderFiles().entrySet()) {

      String id = el.getElementInfo().getId() + "/" + me.getKey();
      String mt = me.getValue().getProps().getStringValue(GWikiPropKeys.WIKIMETATEMPLATE);
      GWikiElement nel = GWikiWebUtils.createNewElement(wikiContext, id, mt, me.getValue().getTitle());
      GWikiElementInfo nei = nel.getElementInfo();
      nei.getProps().getMap().putAll(me.getValue().getProps().getMap());
      nei.getProps().setStringValue(FVOLDER, el.getElementInfo().getId());
      String indexHtml = me.getValue().getProps().getStringValue(VFILE_INDEXHTML);
      if (StringUtils.isNotEmpty(indexHtml) == true) {
        nei.getProps().setStringValue(VFILE_INDEXHTML, indexHtml);
      }
      String pid;
      if (StringUtils.isEmpty(nei.getParentId()) == true) {

        pid = FileNameUtils.getParentDir(me.getKey());

        if (StringUtils.isNotEmpty(pid) == true) {
          GWikiElementInfo parentLocal = cache.getVfolderFiles().get(pid);
          boolean isItselfIndexHtml = isIndexHtmlFile(me.getKey());
          String pindexHtml = parentLocal.getProps().getStringValue(VFILE_INDEXHTML);
          if (pindexHtml != null && isItselfIndexHtml == false) {
            pid = pindexHtml;
          } else {
            pid = el.getElementInfo().getId() + "/" + pid;
          }
        } else {
          pid = el.getElementInfo().getId();
        }
        nei.getProps().setStringValue(GWikiPropKeys.PARENTPAGE, pid);
      } else {
        pid = nei.getParentId();
        System.out.println("No parent set: " + me.getKey() + ": " + nei.getParentId());
      }
      nei.getProps().setStringValue(GWikiPropKeys.PARENTPAGE, pid);
      // if (StringUtils.isNotEmpty(pid) == true) {
      // GWikiElementInfo pei = cache.getElementInfoByLocalName(pid);
      // if (pei != null) {
      // String prid = el.getElementInfo().getId() + "/" + pid;
      // nei.getProps().setStringValue(GWikiPropKeys.PARENTPAGE, prid);
      // } else {
      // System.out.println("No parent found: " + me.getKey());
      // }
      // } else {
      // nei.getProps().setStringValue(GWikiPropKeys.PARENTPAGE, el.getElementInfo().getId());
      // }
      ret.add(nei);
    }
    return ret;
  }

  static GWikiElementInfo createElementInfo(GWikiContext wikiContext, String localName, GWikiElement vfolderElement,
      GWikiVFolderNode node,
      FsObject fs)
  {
    String pid = vfolderElement.getElementInfo().getId();
    String id = pid + '/' + localName;
    String fileMetaTemplate = VFILEMETATEMPLATE;
    if (fs.isDirectory() == true) {
      fileMetaTemplate = VDIRMETATEMPLATE;
    }
    GWikiElement nel = GWikiWebUtils.createNewElement(wikiContext, id, fileMetaTemplate, fs.getNamePart());
    GWikiElementInfo ei = nel.getElementInfo();
    String mby = fs.getModifiedBy();
    if (mby == null) {
      mby = "SYSTEM";
    }
    String title = ei.getTitle();
    int ext = title.lastIndexOf('.');
    if (ext != -1) {
      String sext = title.substring(ext);
      if (sext.equalsIgnoreCase(".htm") || sext.equalsIgnoreCase(".html")) {
        title = title.substring(0, ext);
        String source = getHtmlSource(vfolderElement, node, id);
        if (StringUtils.isNotEmpty(source) == true) {
          Matcher m = titlePattern.matcher(source);
          if (m.matches() == true) {
            title = m.group(1);
          }
        }
        ei.getProps().setStringValue(GWikiPropKeys.TITLE, title);
      }
    }
    ei.getProps().setDateValue(GWikiPropKeys.MODIFIEDAT, fs.getModifiedAt());
    ei.getProps().setStringValue(GWikiPropKeys.MODIFIEDBY, mby);
    ei.getProps().setStringValue(GWikiPropKeys.CREATEDBY, mby);
    ei.getProps().setDateValue(GWikiPropKeys.CREATEDAT, fs.getCreatedAt());
    ei.getProps().setIntValue(GWikiPropKeys.SIZE, fs.getLength());
    return nel.getElementInfo();
  }

  public static boolean anyChangesInFs(GWikiVFolderNode node, GWikiVFolderCachedFileInfos cache)
  {
    FileSystem fs = node.getFileSystem();
    if (fs instanceof ReadWriteCombinedFileSystem) {
      fs = ((ReadWriteCombinedFileSystem) fs).getSecondary();
    }
    long mc = fs.getModificationCounter();
    if (mc == 0) {
      return true;
    }
    if (mc <= cache.getLastFsModifiedCounter()) {
      return false;
    }
    cache.setLastFsModifiedCounter(mc);
    return true;
  }

  /**
   * 
   * @param wikiContext
   * @param el VFolder el
   * @param node
   * @param incrementel
   */
  public static void checkFolders(GWikiContext wikiContext, GWikiElement el, GWikiVFolderNode node, boolean incrementel)
  {
    GWikiVFolderCachedFileInfos cache = readCache(node);
    if (anyChangesInFs(node, cache) == false) {
      return;
    }
    if (updateFolders(wikiContext, el, node, cache, incrementel) == true) {
      writeCache(node, cache);
    }
  }

  /**
   * 
   * @param wikiContext
   * @param vfolderElement vfolder element
   * @param node
   * @param cache
   * @param increment
   * @return
   */
  public static boolean updateFolders(GWikiContext wikiContext, GWikiElement vfolderElement, GWikiVFolderNode node,
      GWikiVFolderCachedFileInfos cache, boolean increment)
  {
    Map<String, FsObject> newFiles = new TreeMap<String, FsObject>();
    Map<String, FsObject> allFiles = new HashMap<String, FsObject>();
    List<FsObject> files = node.getFileSystem().listFilesByPattern("", node.getMatcherRule(), null, true);
    for (FsObject file : files) {
      String name = file.getName();
      if (name.startsWith("/") == true) {
        name = name.substring(1);
      }

      allFiles.put(name, file);
      // System.out.println(name);
      GWikiElementInfo ei = cache.getElementInfoByLocalName(name);
      if (increment == false || ei == null) {
        newFiles.put(name, file);
        continue;
      }
      Date d = file.getModifiedAt();
      if (ObjectUtils.equals(d, ei.getModifiedAt()) == false) {
        newFiles.put(name, file);
      }
    }
    boolean deletedSome = false;
    Iterator<String> it = cache.getLocalNames().iterator();
    for (; it.hasNext();) {
      // for (String ts : cache.getLocalNames()) {
      String ts = it.next();
      if (allFiles.containsKey(ts) == false) {
        deletedSome = true;
        it.remove();
        // TODO next
        // wikiContext.getWikiWeb().getDaoContext().getPageCache().removePageInfo(pageId)
      }
    }
    for (Map.Entry<String, FsObject> me : newFiles.entrySet()) {
      GWikiElementInfo ei = createElementInfo(wikiContext, me.getKey(), vfolderElement, node, me.getValue());
      cache.addElement(me.getKey(), ei);
    }
    return newFiles.isEmpty() == false || deletedSome == true;
  }

  public static GWikiVFolderCachedFileInfos readCache(GWikiVFolderNode node)
  {
    GWikiVFolderCachedFileInfos ret = new GWikiVFolderCachedFileInfos();
    if (node.getFileSystem().exists(VFOLDERCACHEFILE) == false) {
      return ret;
    }
    String content = node.getFileSystem().readTextFile(VFOLDERCACHEFILE);
    List<String> lines = Converter.parseStringTokens(content, "\n", false);
    for (String line : lines) {
      int idx = line.indexOf(':');
      if (idx == -1) {
        continue;
      }
      String localName = line.substring(0, idx);
      String rest = line.substring(idx + 1);
      Map<String, String> atts = PipeValueList.decode(rest);
      GWikiProps props = new GWikiSettingsProps();
      props.setMap(atts);
      // TODO set meta template
      GWikiElementInfo ei = new GWikiElementInfo(props, null);

      ret.addElement(localName, ei);
    }
    if (node.getFileSystem().exists(VFOLDERSTATUSFILE) == true) {
      String tc = node.getFileSystem().readTextFile(VFOLDERSTATUSFILE);
      Map<String, String> m = PipeValueList.decode(tc);
      if (StringUtils.isNumeric(m.get("fsmodcounter")) == true) {
        ret.setLastFsModifiedCounter(NumberUtils.createLong(m.get("fsmodcounter")));
      }
    }
    return ret;
  }

  public static void writeCache(GWikiVFolderNode node, GWikiVFolderCachedFileInfos cache)
  {
    StringBuilder sb = new StringBuilder();

    for (Map.Entry<String, GWikiElementInfo> me : cache.getVfolderFiles().entrySet()) {
      sb.append(me.getKey()).append(":");
      sb.append(PipeValueList.encode(me.getValue().getProps().getMap()));
      sb.append("\n");
    }
    node.getFileSystem().writeTextFile(VFOLDERCACHEFILE, sb.toString(), true);
    Map<String, String> m = new TreeMap<String, String>();
    m.put("fsmodcounter", Long.toString(cache.getLastFsModifiedCounter()));
    String s = PipeValueList.encode(m);
    node.getFileSystem().writeTextFile(VFOLDERSTATUSFILE, s, true);
  }

  static String getLocalFromFilePageId(GWikiElement vfolderEl, String filePageId)
  {
    String vfid = vfolderEl.getElementInfo().getId();
    return filePageId.substring(vfid.length());
  }

  public static void writeContent(GWikiElement vfolderEl, String filePageId, HttpServletResponse resp)
      throws IOException
  {

    // resp.setContentType(getContentType());
    String localName = getLocalFromFilePageId(vfolderEl, filePageId);
    GWikiVFolderNode fvn = GWikiVFolderNode.getVFolderFromElement(vfolderEl);

    FsObject file = fvn.getFileSystem().getFileObject(localName);
    if (file.exists() == false) {
      return;
    }
    resp.setContentLength(file.getLength());
    OutputStream os = resp.getOutputStream();
    fvn.getFileSystem().readBinaryFile(localName, os);
  }

  public static String getHtmlSource(GWikiElement vfolderEl, GWikiVFolderNode fvn, String filePageId)
  {
    String localName = getLocalFromFilePageId(vfolderEl, filePageId);
    try {

      FsObject file = fvn.getFileSystem().getFileObject(localName);
      if (file.exists() == false) {
        return "NOT FOUND";
      }
      ByteArrayOutputStream bout = new ByteArrayOutputStream();
      fvn.getFileSystem().readBinaryFile(localName, bout);

      String s = bout.toString(fvn.getHtmlContentEncoding());
      return s;
    } catch (Exception ex) {
      GWikiLog.error("Error reading vfile: " + localName + "; " + ex.getMessage(), ex);
      return "Page cannot be read";
    }

  }

  public static String getHtmlBody(GWikiElement vfolderEl, GWikiVFolderNode fvn, String filePageId)
  {

    String source = getHtmlSource(vfolderEl, fvn, filePageId);
    String ret = fvn.extractHtmlVFileBody(source);
    return ret;
  }

  public static void getPreview(GWikiContext wikiContext, GWikiElement vfileEl, AppendableI sb)
  {
    GWikiElement vfe = wikiContext.getWikiWeb()
        .getElement(vfileEl.getElementInfo().getProps().getStringValue(GWikiVFolderUtils.FVOLDER));
    String localName = getLocalFromFilePageId(vfe, vfileEl.getElementInfo().getId());
    GWikiVFolderNode fvn = GWikiVFolderNode.getVFolderFromElement(vfe);
    try {
      byte[] data = fvn.getFileSystem().readBinaryFile(localName);
      String t = TextExtractorUtils.getTextExtract(wikiContext, vfileEl.getElementInfo().getId(),
          new ByteArrayInputStream(data));
      sb.append(t);
    } catch (Throwable ex) {
      GLog.note(GWikiLogCategory.Wiki, "Failure extracting text: " + ex.getMessage());
    }
  }
}
