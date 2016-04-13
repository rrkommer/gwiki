////////////////////////////////////////////////////////////////////////////
// 
// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import de.micromata.genome.gwiki.controls.GWikiWeditServiceActionBean.SearchType;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.logging.GWikiLogCategory;
import de.micromata.genome.gwiki.model.matcher.GWikiElementPropMatcher;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiElementByChildOrderComparator;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiElementByI18NPropsComparator;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiElementByIntPropComparator;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiElementByOrderComparator;
import de.micromata.genome.gwiki.page.search.QueryResult;
import de.micromata.genome.gwiki.page.search.SearchQuery;
import de.micromata.genome.gwiki.page.search.SearchResult;
import de.micromata.genome.gwiki.page.search.expr.SearchUtils;
import de.micromata.genome.gwiki.utils.JsonBuilder;
import de.micromata.genome.logging.GLog;
import de.micromata.genome.logging.LogExceptionAttribute;
import de.micromata.genome.util.matcher.EqualsMatcher;
import de.micromata.genome.util.matcher.LogicalMatcherFactory;
import de.micromata.genome.util.matcher.Matcher;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class GWikiTreeChildrenActionBean extends ActionBeanAjaxBase
{
  private boolean forNavigation = false;

  private String rootPage;
  private String type;
  private Map<String, String> rootCategories;

  public static String renderTree(GWikiContext ctx, String rootPageId)
  {
    return renderTree(ctx, rootPageId, "gwiki");
  }

  public static String renderTree(GWikiContext ctx, String rootPageId, String searchType)
  {
    StringBuilder res = new StringBuilder();
    GWikiTreeChildrenActionBean bean = new GWikiTreeChildrenActionBean()
    {

      @Override
      protected Object sendResponse(JsonValue obj)
      {
        res.append(obj.toString());
        return null;
      }
    };
    bean.setForNavigation(true);
    bean.setWikiContext(ctx);
    bean.setRootPage(rootPageId);
    bean.setType(searchType);

    bean.onLoadAsync();
    return res.toString();
  }

  public Object onLoadAsync()
  {
    GWikiElementInfo el = null;
    List<GWikiElementInfo> rootElements = null;
    SearchType.fromString(type);
    String superCategory = rootPage;
    if (forNavigation == true && superCategory == null) {
      superCategory = wikiContext.getWikiWeb().getWelcomePageId(wikiContext);
    }
    if (StringUtils.isBlank(superCategory) || superCategory.equals("#") == true) {
      rootElements = getRootElements();
    } else {

      el = wikiContext.getWikiWeb().findElementInfo(superCategory);
      if (forNavigation == true && el != null) {
        rootElements = new ArrayList<>();
        rootElements.add(el);
      } else {
        rootElements = wikiContext.getElementFinder().getAllDirectChilds(el);
      }
    }
    SearchType searchType = SearchType.fromString(type);
    JsonArray rootNodes = JsonBuilder.array();
    Collections.sort(rootElements, new GWikiElementByChildOrderComparator(//
        new GWikiElementByOrderComparator(//
            new GWikiElementByIntPropComparator("ORDER", 0, //
                new GWikiElementByI18NPropsComparator(wikiContext, GWikiPropKeys.TITLE)))));
    for (GWikiElementInfo ei : rootElements) {
      JsonObject children = buildNodeInfo(ei, searchType);
      if (children != null) {
        rootNodes.add(children);
      }

    }

    return sendResponse(rootNodes);
  }

  private JsonObject buildNodeInfo(GWikiElementInfo ei, SearchType searchType)
  {
    if (wikiContext.getWikiWeb().getAuthorization().isAllowToView(wikiContext, ei) == false) {
      return null;
    }
    JsonObject ret = new JsonObject();

    String title = ei.getTitle();
    if (ei.getTitle().startsWith("I{") == true) {
      title = wikiContext.getTranslatedProp(title);
    }
    if (StringUtils.isBlank(title) == true) {
      return null;
    }
    List<GWikiElementInfo> childs = wikiContext.getElementFinder().getAllDirectChildsSorted(ei);

    JsonArray childNodes = JsonBuilder.array();
    for (GWikiElementInfo sid : childs) {
      JsonObject subnode = buildNodeInfo(sid, searchType);
      if (subnode != null) {
        childNodes.add(subnode);
      }
    }
    SearchType st = SearchType.fromElementInfo(wikiContext, ei);
    boolean match = searchType.matches(st);
    if (match == false && childNodes.size() == 0) {
      return null;
    }

    ret.add("children", childNodes);
    JsonObject data = new JsonObject();
    ret.add("data", data);
    if (!match) {
      ret.add("state", JsonBuilder.map("disabled", "true"));
    }
    data.add("url", ei.getId());
    ret.add("id", StringUtils.replace(ei.getId(), "/", "_"));
    //    ret.add("id", ei.getId());
    String targetLink = wikiContext.localUrl(ei.getId());

    //    data.add("url", targetLink);

    data.add("type", st.getElmentJsonType());

    data.add("matchtype", match);

    data.add("title", title);
    ret.add("text", title);
    return ret;
  }

  private List<GWikiElementInfo> getRootElements()
  {
    GWikiElementPropMatcher rootPageMatcher = new GWikiElementPropMatcher(wikiContext, GWikiPropKeys.PARENTPAGE,
        new EqualsMatcher<String>(
            null));
    List<GWikiElement> rootPages = wikiContext.getElementFinder().getPages(rootPageMatcher);
    List<GWikiElementInfo> validRootPages = new ArrayList<GWikiElementInfo>();

    for (GWikiElement elem : rootPages) {
      if (elem.getElementInfo().isIndexed()
          && elem.getElementInfo().isViewable()
          && StringUtils.equals("gwiki", elem.getElementInfo().getType())
          && elem.getElementInfo().isNoToc() == false) {

        validRootPages.add(elem.getElementInfo());
      }
    }

    return validRootPages;
  }

  private JsonObject addToNode(Map<String, JsonObject> tree, String dir)
  {
    if (tree.containsKey(dir) == true) {
      return tree.get(dir);
    }
    int lidx = dir.lastIndexOf('/');
    JsonObject pnode;
    if (lidx != -1) {
      String pn = dir.substring(0, lidx);
      pnode = tree.get(pn);
      if (pnode == null) {
        pnode = addToNode(tree, pn);
      }
    } else {
      pnode = tree.get("");
    }
    JsonObject node = createDirNode(dir);
    JsonArray children = (JsonArray) pnode.get("children");
    children.add(node);
    tree.put(dir, node);
    return node;
  }

  public Object onGetPhysicalPaths()
  {
    JsonObject rootNode = createDirNode("");
    try {
      String queryexpr = SearchUtils.createLinkExpression("", true, null);
      SearchQuery query = new SearchQuery(queryexpr, wikiContext.getWikiWeb());
      QueryResult qr = wikiContext.getWikiWeb().getContentSearcher().search(wikiContext, query);
      Map<String, JsonObject> tree = new TreeMap<>();

      tree.put("", rootNode);

      for (SearchResult sr : qr.getResults()) {
        String pageid = sr.getPageId();
        int lidx = pageid.lastIndexOf('/');
        if (lidx == -1) {
          continue;
        }
        String s = pageid.substring(0, lidx);
        addToNode(tree, s);
      }
      return sendResponse(rootNode);
      //      
      //      GWikiStorage storage = wikiContext.getWikiWeb().getDaoContext().getStorage();
      //      FileSystem fileSystem = storage.getFileSystem();
      //      List<FsObject> dirs = fileSystem.listFiles("", null, 'D', true);
      //      // TODO RK restrictions on filesystem.
      //
      //      JsonObject node = createDirNode("");
      //      Map<String, JsonObject> tree = new TreeMap<>();
      //      for (FsObject dir : dirs) {
      //        String sdir = dir.toString();
      //        if (isValidDir(sdir) == false) {
      //          continue;
      //        }
      //        node = createDirNode(sdir);
      //        tree.put(sdir, node);
      //        FsDirectoryObject parent = dir.getParent();
      //        if (parent == null || StringUtils.isBlank(parent.toString()) == true) {
      //          rootNodes.add(node);
      //        } else {
      //          JsonObject pnode = tree.get(parent.toString());
      //          if (pnode == null) {
      //            continue;
      //          }
      //          JsonArray childa = (JsonArray) pnode.get("children");
      //          childa.add(node);
      //        }
      //      }

    } catch (Exception ex) {
      GLog.error(GWikiLogCategory.Wiki, "Error building dir tree: " + ex.getMessage(), new LogExceptionAttribute(ex));
      return sendResponse(rootNode);
    }
  }

  protected JsonObject createDirNode(String path)
  {
    JsonObject node = new JsonObject();
    String title = StringUtils.substringAfterLast(path, "/");
    if (StringUtils.isEmpty(title) == true) {
      if (StringUtils.isEmpty(path) == true) {
        title = "Root";
      } else {
        title = path;
      }
    }
    String id = StringUtils.replace(path, "/", "_");
    node.add("id", id);
    node.add("url", path);

    node.add("text", title);
    node.add("children", new JsonArray());
    JsonObject data = new JsonObject();
    node.add("data", data);
    data.add("url", path);
    return node;
  }

  protected boolean isValidDir(String dir)
  {
    Matcher<String> matcher = new LogicalMatcherFactory<String>()
        .createMatcher("static,*/tmp,*/tmp/*,edit/*,admin,admin/*");
    if (matcher.match(dir) == true) {
      return false;
    }
    return true;
  }

  /**
   * @param rootPage the rootPage to set
   */
  public void setRootPage(String rootPage)
  {
    this.rootPage = rootPage;
  }

  /**
   * @return the rootPage
   */
  public String getRootPage()
  {
    if (StringUtils.isBlank(rootPage)) {
      rootPage = wikiContext.getWikiWeb().getWelcomePageId(wikiContext);
    }
    return rootPage;
  }

  /**
   * @return the rootCategories
   */
  public Map<String, String> getRootCategories()
  {
    if (this.rootCategories == null) {
      this.rootCategories = new HashMap<String, String>();
    }
    return rootCategories;
  }

  public String getType()
  {
    return type;
  }

  public void setType(String type)
  {
    this.type = type;
  }

  public boolean isForNavigation()
  {
    return forNavigation;
  }

  public void setForNavigation(boolean forNavigation)
  {
    this.forNavigation = forNavigation;
  }

}
