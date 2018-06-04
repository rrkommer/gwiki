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

package de.micromata.genome.gwiki.page.impl.wiki.fragment;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroRenderFlags;

public class GWikiFragmentList extends GWikiFragmentChildsBase
{

  private static final long serialVersionUID = -10964121817921598L;

  private String listTag;

  private String addClass;

  public GWikiFragmentList(String listTag)
  {
    this.listTag = listTag;
  }

  public GWikiFragmentList(String listTag, List<GWikiFragment> childs)
  {
    super(childs);
    this.listTag = listTag;
  }

  public boolean sameType(GWikiFragmentList other)
  {
    return StringUtils.equals(this.listTag, other.listTag);
  }

  @Override
  public boolean render(GWikiContext ctx)
  {
    final char type = listTag.charAt(listTag.length() - 1);
    final String tag = tagForList(type);
    final String attrs = attrForList(type);
    // if childs is ListFragment no li
    ctx.append("<").append(tag).append(attrs).append(">");
    renderChilds(ctx);
    ctx.append("</").append(tag).append(">");
    return true;
  }

  @Override
  public void getSource(StringBuilder sb)
  {
    if (sb.length() > 0 && sb.charAt(sb.length() - 1) != '\n') {
      sb.append("\n");
    }
    getChildSouce(sb);
  }

  protected String tagForList(char tk)
  {
    switch (tk) {
      case '-':
        return "ul";
      case '*':
        return "ul";
      case '#':
        return "ol";
      default:
        return "ul";
    }
  }

  protected String attrForList(char tk)
  {
    String ac = "";

    if (addClass != null) {
      ac = " " + addClass;
    }
    switch (tk) {
      case '-':
        return " class=\"minus" + ac + "\" type=\"square\"";
      case '*':
        return " class=\"star" + ac + "\"";
      default:
        if (addClass != null) {
          return " class=\"" + addClass + "\"";
        }
        return "";
    }
  }

  @Override
  public int getRenderModes()
  {
    return GWikiMacroRenderFlags.combine(GWikiMacroRenderFlags.NoWrapWithP);
  }

  public String getListTag()
  {
    return listTag;
  }

  public void setListTag(String listTag)
  {
    this.listTag = listTag;
  }

  public String getAddClass()
  {
    return addClass;
  }

  public void setAddClass(String addClass)
  {
    this.addClass = addClass;
  }

}
