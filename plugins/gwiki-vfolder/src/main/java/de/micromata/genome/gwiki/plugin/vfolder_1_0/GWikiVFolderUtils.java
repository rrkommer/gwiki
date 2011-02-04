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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gdbfs.FileNameUtils;
import de.micromata.genome.gdbfs.FsObject;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiPageCache;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.GWikiProps;
import de.micromata.genome.gwiki.model.GWikiSettingsProps;
import de.micromata.genome.gwiki.model.GWikiWebUtils;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.util.text.PipeValueList;
import de.micromata.genome.util.types.Converter;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiVFolderUtils
{
  public static final String VFOLDERCACHEFILE = "gwikivfoldercache.txt";

  public static final String VDIRMETATEMPLATE = "admin/templates/intern/VDirMetaTemplate";

  public static final String VFILEMETATEMPLATE = "admin/templates/intern/VFileMetaTemplate";

  public static void mountFs(GWikiContext wikiContext, GWikiElement el, GWikiVFolderNode node)
  {
    GWikiVFolderCachedFileInfos cache = readCache(node);
    List<GWikiElementInfo> ell = resolveParents(wikiContext, el, cache);
    GWikiPageCache pageCache = wikiContext.getWikiWeb().getDaoContext().getPageCache();
    for (GWikiElementInfo ei : ell) {
      pageCache.putPageInfo(ei);
    }
  }

  public static void dismountFs(GWikiContext wikiContext, GWikiElement el, GWikiVFolderNode node)
  {
    GWikiVFolderCachedFileInfos cache = readCache(node);
    GWikiPageCache pageCache = wikiContext.getWikiWeb().getDaoContext().getPageCache();

    for (Map.Entry<String, GWikiElementInfo> me : cache.getVfolderFiles().entrySet()) {
      String id = el.getElementInfo().getId() + "/" + me.getKey();
      pageCache.removePageInfo(id);
    }
  }

  public static List<GWikiElementInfo> resolveParents(GWikiContext wikiContext, GWikiElement el, GWikiVFolderCachedFileInfos cache)
  {
    List<GWikiElementInfo> ret = new ArrayList<GWikiElementInfo>();
    for (Map.Entry<String, GWikiElementInfo> me : cache.getVfolderFiles().entrySet()) {

      String id = el.getElementInfo().getId() + "/" + me.getKey();
      GWikiElement nel = GWikiWebUtils.createNewElement(wikiContext, id, me.getValue().getProps().getStringValue(
          GWikiPropKeys.WIKIMETATEMPLATE), me.getValue().getTitle());
      GWikiElementInfo nei = nel.getElementInfo();
      nei.getProps().getMap().putAll(me.getValue().getProps().getMap());
      nei.getProps().setStringValue("FVOLDER", el.getElementInfo().getId());

      // me.getValue().setMetaTemplate(wikiContext.getWikiWeb().findMetaTemplate(me.getValue().getProps().f))
      String pid = FileNameUtils.getParentDir(me.getKey());
      if (StringUtils.isNotEmpty(pid) == true) {
        GWikiElementInfo pei = cache.getElementInfoByLocalName(pid);
        if (pei != null) {
          String prid = el.getElementInfo().getId() + "/" + pid;
          nei.getProps().setStringValue(GWikiPropKeys.PARENTPAGE, prid);
        } else {
          System.out.println("No parent found: " + me.getKey());
        }
      } else {
        nei.getProps().setStringValue(GWikiPropKeys.PARENTPAGE, el.getElementInfo().getId());
      }
      ret.add(nei);
    }
    return ret;
  }

  static GWikiElementInfo createElementInfo(GWikiContext wikiContext, String localName, GWikiElement pe, FsObject fs)
  {
    String pid = pe.getElementInfo().getId();
    String id = pid + '/' + localName;
    String fileMetaTemplate = VFILEMETATEMPLATE;
    if (fs.isDirectory() == true) {
      fileMetaTemplate = VDIRMETATEMPLATE;
    }
    GWikiElement nel = GWikiWebUtils.createNewElement(wikiContext, id, fileMetaTemplate, fs.getNamePart());
    GWikiElementInfo ei = nel.getElementInfo();
    ei.getProps().setDateValue(GWikiPropKeys.MODIFIEDAT, fs.getModifiedAt());
    ei.getProps().setStringValue(GWikiPropKeys.MODIFIEDBY, fs.getModifiedBy());
    ei.getProps().setStringValue(GWikiPropKeys.CREATEDBY, fs.getCreatedBy());
    ei.getProps().setDateValue(GWikiPropKeys.CREATEDAT, fs.getCreatedAt());
    return nel.getElementInfo();

  }

  public static void checkFolders(GWikiContext wikiContext, GWikiElement el, GWikiVFolderNode node)
  {
    GWikiVFolderCachedFileInfos cache = readCache(node);
    if (updateFolders(wikiContext, el, node, cache) == true) {
      writeCache(node, cache);
    }
  }

  public static boolean updateFolders(GWikiContext wikiContext, GWikiElement el, GWikiVFolderNode node, GWikiVFolderCachedFileInfos cache)
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
      if (ei == null) {
        newFiles.put(name, file);
        continue;
      }
      Date d = file.getModifiedAt();
      if (ObjectUtils.equals(d, ei.getModifiedAt()) == false) {
        newFiles.put(name, file);
      }
    }
    boolean deletedSome = false;
    for (String ts : cache.getLocalNames()) {
      if (allFiles.containsKey(ts) == false) {
        deletedSome = true;
        cache.removeElement(ts);
        // TODO next
        // wikiContext.getWikiWeb().getDaoContext().getPageCache().removePageInfo(pageId)
      }
    }
    for (Map.Entry<String, FsObject> me : newFiles.entrySet()) {
      GWikiElementInfo ei = createElementInfo(wikiContext, me.getKey(), el, me.getValue());
      cache.addElement(me.getKey(), ei);
    }
    return newFiles.isEmpty() == false && deletedSome == false;
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
  }

}
