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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import de.micromata.genome.gwiki.controls.GWikiWeditServiceActionBean.SearchType;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.GWikiWeb;

/**
 * ActionBean to Change Pagetree.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiPageTreeActionBean extends ActionBeanAjaxBase
{
  private String node;
  private String parent;
  private String old_parent;
  private Integer position;
  private Integer old_position;

  public GWikiPageTreeActionBean()
  {

  }

  @Override
  public Object onInit()
  {
    wikiContext.addContentJs("/static/jstree/jstree.js");
    return super.onInit();
  }

  public Object onAsyncMoveTree()
  {
    if (StringUtils.isBlank(parent) == true || StringUtils.isBlank(old_parent) == true || position == null
        || old_position == null) {
      return returnError(10, "Invalid request parameter");

    }
    if (parent.equals(old_parent) == true) {
      return reorder();
    } else {
      return relink();
    }
  }

  private Object returnError(int code, String message)
  {
    JsonObject res = new JsonObject();
    res.add("rc", code);
    res.add("message", message);
    return sendResponse(res);
  }

  private Object relink()
  {
    GWikiWeb wikiWeb = wikiContext.getWikiWeb();
    GWikiElement nel = wikiWeb.findElement(node);
    GWikiElementInfo pei = wikiWeb.findElementInfo(parent);
    if (nel == null || pei == null) {
      return returnError(10, "Cannot find Elements");
    }
    if (wikiWeb.getAuthorization().isAllowToEdit(wikiContext, nel.getElementInfo()) == false) {
      return returnError(20, "Insufficent right to edit Page: " + node);
    }
    nel.getElementInfo().getProps().setStringValue(GWikiPropKeys.PARENTPAGE, pei.getId());
    wikiWeb.saveElement(wikiContext, nel, false);
    return reorder();

  }

  private Object reorder()
  {
    GWikiWeb wikiWeb = wikiContext.getWikiWeb();
    GWikiElementInfo pei = wikiWeb.findElementInfo(parent);

    GWikiElement nel = wikiWeb.findElement(node);
    if (nel == null || pei == null) {
      return returnError(10, "Cannot find Elements");
    }

    List<GWikiElementInfo> childs = wikiContext.getElementFinder().getAllDirectChildsSorted(pei);

    int fidx = -1;
    for (int i = 0; i < childs.size(); ++i) {
      if (childs.get(i).getId().equals(node) == true) {
        fidx = i;
        break;
      }
    }
    if (fidx == position) {
      return returnError(0, "");
    }
    childs.remove(fidx);
    childs.add(position, nel.getElementInfo());
    List<GWikiElement> toSave = new ArrayList<>();
    for (int i = 0; i < childs.size(); ++i) {
      GWikiElementInfo ch = childs.get(i);

      int order = ch.getProps().getIntValue(GWikiPropKeys.ORDER, -1);
      if (order != i) {
        GWikiElement elch = wikiWeb.getElement(ch);
        elch.getElementInfo().getProps().setIntValue(GWikiPropKeys.ORDER, i);
        toSave.add(elch);
      }
    }
    for (GWikiElement el : toSave) {
      if (wikiWeb.getAuthorization().isAllowToEdit(wikiContext, el.getElementInfo()) == false) {
        return returnError(20, "Insufficent right to edit Page: " + el.getElementInfo().getId());
      }
    }
    for (GWikiElement el : toSave) {
      wikiWeb.saveElement(wikiContext, el, true);
    }
    return returnError(0, "");
  }

  public String renderTree()
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
    bean.setWikiContext(wikiContext);
    bean.setRootPage(null);
    bean.setType(SearchType.All.name());
    bean.onLoadAsync();
    return res.toString();
  }

  public String getParent()
  {
    return parent;
  }

  public void setParent(String parent)
  {
    this.parent = parent;
  }

  public String getOld_parent()
  {
    return old_parent;
  }

  public void setOld_parent(String old_parent)
  {
    this.old_parent = old_parent;
  }

  public Integer getPosition()
  {
    return position;
  }

  public void setPosition(Integer position)
  {
    this.position = position;
  }

  public Integer getOld_position()
  {
    return old_position;
  }

  public void setOld_position(Integer old_position)
  {
    this.old_position = old_position;
  }

  public String getNode()
  {
    return node;
  }

  public void setNode(String node)
  {
    this.node = node;
  }

}
