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
package de.micromata.genome.gwiki.plugin.keywordsmarttags_1_0;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.AuthorizationFailedException;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.RenderModes;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentHtml;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentVisitor;
import de.micromata.genome.util.types.Pair;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiFragmentKeywordHtml extends GWikiFragmentHtml
{

  private static final long serialVersionUID = -8960733154116086458L;

  private GWikiFragmentHtml nested;

  public GWikiFragmentKeywordHtml(GWikiFragmentHtml nested)
  {
    this.nested = nested;
  }

  private void createToolTip(StringBuilder sb, String matched, List<GWikiElementInfo> els)
  {
    GWikiContext ctx = GWikiContext.getCurrent();
    boolean hasForeignLink = false;

    StringBuilder ts = new StringBuilder();
    ts.append("<ul>");
    for (GWikiElementInfo ei : els) {
      if (StringUtils.equals(ei.getId(), ctx.getCurrentElement().getElementInfo().getId()) == true) {
        continue;
      }
      if (ctx.getWikiWeb().getAuthorization().isAllowToView(ctx, ei) == false) {
        continue;
      }
      hasForeignLink = true;

      ts.append("<li>")
          //
          .append(StringEscapeUtils.escapeHtml(matched)).append(": ")
          //
          .append("<a href='").append(ctx.localUrl(ei.getId())).append("'>")
          .append(ctx.getTranslatedProp(ei.getTitle())).append("</a>")
          .append("</li>");
    }
    ts.append("</ul>");
    if (hasForeignLink == false) {
      sb.append(matched);
      return;
    }
    sb.append(
        "<a class=\"wikiSmartTag\" href='#' onclick='return false;' onmouseover=\"displayHilfeLayer('")
        .append(StringEscapeUtils.escapeJavaScript(ts.toString())).append("', '").append(ctx.genHtmlId(""))
        .append("')\" onmouseout=\"doNotOpenHilfeLayer();\">") //
        .append(StringEscapeUtils.escapeHtml(matched)).append("</a>");
    sb.append("");
  }

  // protected boolean isInLink()
  // {
  // for (int i = 0; i < stack.size(); ++i) {
  // GWikiFragment frag = stack.peek(i);
  // if (frag instanceof GWikiFragementLink || frag instanceof GWikiFragmentHeading) {
  // return true;
  // }
  // }
  // return false;
  // }

  protected void patch(KeyWordRanges kranges, String html, Map.Entry<String, Pair<Pattern, List<GWikiElementInfo>>> me)
  {
    if (StringUtils.isBlank(html) == true) {
      return;
    }
    Matcher m = me.getValue().getFirst().matcher(html);
    while (m.find() == true) {
      int sidx = m.start();
      int eidx = m.end();
      if (sidx != 0) {
        if (Character.isLetterOrDigit(html.charAt(sidx - 1)) == true) {
          continue;
        }
      }
      if (eidx < html.length() - 1) {
        if (Character.isLetterOrDigit(html.charAt(eidx)) == true) {
          continue;
        }
      }
      kranges.addIntersect(sidx, eidx, me.getValue().getSecond());
    }
  }

  protected String renderPatched(String html, KeyWordRanges kranges)
  {
    Collections.sort(kranges, new Comparator<KeyWordRange>()
    {

      @Override
      public int compare(KeyWordRange o1, KeyWordRange o2)
      {
        if (o1.start < o2.start) {
          return -1;
        }
        if (o1.start > o2.start) {
          return 1;
        }
        return 0;
      }
    });
    StringBuilder sb = new StringBuilder();
    int lastEidx = 0;

    for (KeyWordRange kr : kranges) {
      if (kr.start > lastEidx) {
        sb.append(html.substring(lastEidx, kr.start));
      }
      String matched = html.substring(kr.start, kr.end);
      createToolTip(sb, matched, kr.links);
      lastEidx = kr.end;
    }
    if (lastEidx < html.length()) {
      sb.append(html.substring(lastEidx));
    }
    return sb.toString();
  }

  @Override
  public boolean render(GWikiContext ctx)
  {
    GWikiKeywordLoadElementInfosFilter fe = GWikiKeywordLoadElementInfosFilter.getInstance();
    if (fe == null) {
      return nested.render(ctx);
    }
    if (RenderModes.NoPageDecoration.isSet(ctx.getRenderMode()) == true) {
      return nested.render(ctx);
    }
    String space = ctx.getCurrentElement().getElementInfo().getWikiSpace(ctx);
    Map<String, Pair<Pattern, List<GWikiElementInfo>>> spaceKeyWords = fe.getKeywords(ctx).get(space);
    if (spaceKeyWords == null) {
      return nested.render(ctx);
    }
    String html = nested.getHtml();
    KeyWordRanges kranges = new KeyWordRanges();
    for (Map.Entry<String, Pair<Pattern, List<GWikiElementInfo>>> me : spaceKeyWords.entrySet()) {
      patch(kranges, html, me);
    }
    if (kranges.isEmpty() == false) {
      String p = renderPatched(html, kranges);
      ctx.append(p);
      return true;
    } else {
      return nested.render(ctx);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragementBase#getSource(java.lang.StringBuilder)
   */
  @Override
  public void getSource(StringBuilder sb)
  {
    nested.getSource(sb);
  }

  @Override
  public void ensureRight(GWikiContext ctx) throws AuthorizationFailedException
  {
    nested.ensureRight(ctx);
  }

  @Override
  public List<GWikiFragment> getChilds()
  {
    List<GWikiFragment> childs = new ArrayList<GWikiFragment>(1);
    childs.add(nested);
    return childs;
  }

  @Override
  public String getHtml()
  {
    return nested.getHtml();
  }

  @Override
  public void setHtml(String html)
  {
    nested.setHtml(html);
  }

  @Override
  public void iterate(GWikiFragmentVisitor visitor)
  {
    visitor.begin(this);
    nested.iterate(visitor);
    visitor.end(this);
  }

  @Override
  public String getSource()
  {
    return nested.getSource();
  }

  @Override
  public String toString()
  {
    return nested.toString();
  }
}
