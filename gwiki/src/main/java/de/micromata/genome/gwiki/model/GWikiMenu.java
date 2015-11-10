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
package de.micromata.genome.gwiki.model;

import java.util.ArrayList;
import java.util.List;

import de.micromata.genome.util.types.Converter;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiMenu
{
  private boolean divider = false;

  private String label;

  private String linkTitle;

  private String iconMedium;

  private String url;

  private String target;

  private List<GWikiMenu> children = new ArrayList<GWikiMenu>();

  public GWikiMenu()
  {

  }

  public static GWikiMenu createDivider()
  {
    GWikiMenu menu = new GWikiMenu();
    menu.setDivider(true);
    return menu;
  }

  public void addUrlParam(String key, String value)
  {
    if (url == null) {
      url = "";
    }
    if (url.indexOf('?') == -1) {
      url += "?";
    } else {
      url += "&";
    }
    url += Converter.encodeUrlParam(key) + "=" + Converter.encodeUrlParam(value);
  }

  public String getLabel()
  {
    return label;
  }

  public void setLabel(String label)
  {
    this.label = label;
  }

  public String getIconMedium()
  {
    return iconMedium;
  }

  public void setIconMedium(String iconMedium)
  {
    this.iconMedium = iconMedium;
  }

  public String getUrl()
  {
    return url;
  }

  public void setUrl(String url)
  {
    this.url = url;
  }

  public String getTarget()
  {
    return target;
  }

  public void setTarget(String target)
  {
    this.target = target;
  }

  public List<GWikiMenu> getChildren()
  {
    return children;
  }

  public void setChildren(List<GWikiMenu> children)
  {
    this.children = children;
  }

  public boolean isDivider()
  {
    return divider;
  }

  public void setDivider(boolean divider)
  {
    this.divider = divider;
  }

  public String getLinkTitle()
  {
    return linkTitle;
  }

  public void setLinkTitle(String description)
  {
    this.linkTitle = description;
  }

}
