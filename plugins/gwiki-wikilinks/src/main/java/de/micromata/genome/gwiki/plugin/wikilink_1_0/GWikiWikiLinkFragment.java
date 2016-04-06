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
package de.micromata.genome.gwiki.plugin.wikilink_1_0;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.AuthorizationFailedException;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentChildContainer;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentHtml;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentLink;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentText;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentVisitor;
import de.micromata.genome.gwiki.utils.WebUtils;

/**
 * @author roger
 * 
 */
public class GWikiWikiLinkFragment extends GWikiFragmentHtml
{

  private static final long serialVersionUID = -5747423263450227160L;

  private String text;

  public GWikiWikiLinkFragment(String text)
  {
    this.text = text;
  }

  private static enum ParseState
  {
    Start, InWikiLink
  }

  public static boolean isWikiLinkWord(String text)
  {
    if (StringUtils.isBlank(text) == true) {
      return false;
    }
    if (text.length() < 4) {
      return false;
    }
    boolean lastUpperCase = true;
    int uccount = 1;
    for (int i = 1; i < text.length(); ++i) {
      char c = text.charAt(i);
      boolean uc = Character.isUpperCase(c);
      if (uc == true && lastUpperCase == true) {
        return false;
      }
      uccount += (uc ? 1 : 0);
      lastUpperCase = uc;
    }
    if (uccount > 1) {
      return true;
    } else {
      return false;
    }
  }

  private static void addTextFrag(GWikiFragmentChildContainer fcc, String text)
  {
    if (fcc.getChilds().isEmpty() == true) {
      fcc.addChild(new GWikiFragmentText(text));
      return;
    }
    GWikiFragment lf = fcc.getChilds().get(fcc.getChilds().size() - 1);
    if (lf instanceof GWikiFragmentText) {
      GWikiFragmentText ltf = (GWikiFragmentText) lf;
      ltf.addText(text);
    } else {
      fcc.addChild(new GWikiFragmentText(text));
    }
  }

  private static GWikiFragment unfoldParsed(GWikiFragmentChildContainer fcc)
  {
    if (fcc.getChilds().size() != 1) {
      return fcc;
    }
    return fcc.getChilds().get(0);
  }

  public static GWikiFragment parseText(GWikiFragmentText textFrag)
  {
    String text = textFrag.getSource();
    ParseState state = ParseState.Start;
    int startLink = 0;
    GWikiFragmentChildContainer fcc = new GWikiFragmentChildContainer();
    boolean startWord = true;
    for (int i = 0; i < text.length(); ++i) {
      char c = text.charAt(i);

      switch (state) {
        case Start:
          boolean ws = Character.isWhitespace(c);
          if (ws == true || Character.isLetter(c) == false) {
            // addTextFrag(fcc, text.substring(startLink, i));
            // startLink = i;
            startWord = ws;
            continue;
          }

          if (startWord == true && Character.isUpperCase(c) == true) {
            if (startLink != i) {
              addTextFrag(fcc, text.substring(startLink, i));
            }
            startLink = i;
            state = ParseState.InWikiLink;
            break;
          }
          startWord = ws;
          break;
        case InWikiLink:
          if (Character.isLetter(c) == false) {
            String k = text.substring(startLink, i);
            if (isWikiLinkWord(k) == true) {
              fcc.addChild(new GWikiWikiLinkFragment(k));
            } else {
              addTextFrag(fcc, k);
            }

            startLink = i;
            state = ParseState.Start;
          }

          break;
      }
    }
    if (startLink < text.length()) {
      String k = text.substring(startLink);
      if (state == ParseState.InWikiLink && isWikiLinkWord(k) == true) {
        fcc.addChild(new GWikiWikiLinkFragment(k));
      } else {
        addTextFrag(fcc, k);
      }
    }
    return unfoldParsed(fcc);
  }

  @Override
  public boolean render(GWikiContext ctx)
  {
    GWikiElement ce = ctx.getCurrentElement();
    if (ce == null) {
      ctx.append(getHtml());
      return true;
    }
    String thisid = ce.getElementInfo().getId();
    int lidx = thisid.lastIndexOf('/');
    String cp = text;

    if (lidx != -1) {
      cp = thisid.substring(0, lidx + 1) + cp;
    }
    GWikiElementInfo tei = ctx.getWikiWeb().findElementInfo(cp);
    if (tei != null) {
      if (ce.getElementInfo().getId().equals(cp) == true) {
        ctx.append(getHtml());
        return true;
      }
      new GWikiFragmentLink(cp).render(ctx);
      return true;
    }

    if (ctx.getWikiWeb().getAuthorization().isAllowToCreate(ctx, ce.getElementInfo()) == false) {
      ctx.append(getHtml());
      return true;

    }
    String tenc = WebUtils.encodeUrlParam(text);
    ctx.append(getHtml()) //
        .append("<a href='")//
        .append(ctx.localUrl("edit/EditPage?newPage=true&parentPageId="))//
        .append(WebUtils.encodeUrlParam(thisid))//
        .append("&pageId=") //
        .append(tenc)//
        .append("&title=")//
        .append(tenc)//
        .append("&metaTemplatePageId=").append(WebUtils.encodeUrlParam("admin/templates/StandardWikiPageMetaTemplate"))//
        .append("'") //
        .append(" class='gwikiMissingLink'").append(">").append("+").append("</a>");
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragementBase#getSource(java.lang.StringBuilder)
   */
  @Override
  public void getSource(StringBuilder sb)
  {
    sb.append(text);
  }

  @Override
  public void ensureRight(GWikiContext ctx) throws AuthorizationFailedException
  {
    // nested.ensureRight(ctx);
  }

  @Override
  public List<GWikiFragment> getChilds()
  {
    List<GWikiFragment> childs = new ArrayList<GWikiFragment>(1);
    // childs.add(nested);
    return childs;
  }

  @Override
  public String getHtml()
  {
    return StringEscapeUtils.escapeHtml(text.toString());
  }

  @Override
  public void setHtml(String html)
  {
    // nested.setHtml(html);
  }

  @Override
  public void iterate(GWikiFragmentVisitor visitor, GWikiFragment parent)
  {
    // visitor.begin(this);
    // nested.iterate(visitor);
    // visitor.end(this);
  }

  @Override
  public String getSource()
  {
    return text;
  }

  @Override
  public String toString()
  {
    return text;
  }
}
