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
package de.micromata.genome.gwiki.web.tags;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiExecutableArtefakt;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.GWikiStandaloneContext;
import de.micromata.genome.gwiki.page.impl.GWikiWikiPageArtefakt;

/**
 * Tag to include text from other page.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiIncludeTag extends TagSupport
{

  private static final long serialVersionUID = 7133471788777183330L;

  /**
   * If page is null, use the current page.
   */
  private String pageId;

  /**
   * name of the part/artefakt. if null, try "", "MainPage"/
   */
  private String part;

  /**
   * Only works in connection with wiki page. include the chunk.
   */
  private String chunk;

  protected GWikiArtefakt< ? > getPart(GWikiElement el)
  {
    GWikiArtefakt< ? > art = null;
    if (part == null) {
      art = el.getMainPart();
      if (art == null) {
        art = el.getPart("");
        if (art == null) {
          art = el.getPart("MainPart");
        }
      }
    } else {
      art = el.getPart(part);
    }
    return art;
  }

  @Override
  public int doEndTag() throws JspException
  {

    GWikiElement el = null;
    GWikiArtefakt< ? > art = null;
    GWikiContext ctx = null;
    GWikiWeb wikiWeb = GWikiWeb.getWiki();
    try {
      if (StringUtils.isEmpty(pageId) == true) {
        ctx = (GWikiContext) pageContext.getAttribute("wikiContext");
        if (ctx == null) {
          throw new RuntimeException("no wikiContext set in GWikiInclude tag with no given pageId");
        }
        el = ctx.getCurrentElement();
        art = getPart(el);
      } else {
        el = wikiWeb.getElement(pageId);
      }
      if (part != null) {
        art = el.getPart(part);
      }
      if (StringUtils.isNotEmpty(chunk) == true) {
        if (part == null && art == null) {
          art = getPart(el);
        }
        if ((art instanceof GWikiWikiPageArtefakt) == false) {
          throw new RuntimeException("Including section, Part is not wiki page");
        }
        GWikiWikiPageArtefakt wpa = (GWikiWikiPageArtefakt) art;
        if (ctx != null) {
          wpa.renderChunk(ctx, chunk);
        } else {
          GWikiStandaloneContext sactx = GWikiStandaloneContext.create();
          // GWikiContext ctx = new GWikiContext(wikiWeb, servlet, request, response)
          wpa.renderChunk(sactx, chunk);
          String outt = sactx.getOutString();
          pageContext.getOut().append(outt);

        }
      } else {
        if (art == null) {
          pageContext.include(wikiWeb.getServletPath() + "/" + pageId);
        } else {
          if ((art instanceof GWikiExecutableArtefakt< ? >) == false) {
            throw new RuntimeException("Cannot render part because not a executable: " + art + " in page " + pageId);
          }
          GWikiExecutableArtefakt< ? > exec = (GWikiExecutableArtefakt< ? >) art;
          GWikiStandaloneContext sactx = GWikiStandaloneContext.create();
          exec.render(sactx);
          String outt = sactx.getOutString();
          pageContext.getOut().append(outt);
        }
      }
    } catch (ServletException e) {
      throw new JspException("Servlet exception wile rendering gwiki:include: " + e.getMessage(), e);
    } catch (IOException e) {
      throw new JspException("IO error wile rendering gwiki:include: " + e.getMessage(), e);
    }
    return super.doEndTag();
  }

  public String getPageId()
  {
    return pageId;
  }

  public void setPageId(String page)
  {
    this.pageId = page;
  }

  public String getPart()
  {
    return part;
  }

  public void setPart(String part)
  {
    this.part = part;
  }

  public String getChunk()
  {
    return chunk;
  }

  public void setChunk(String section)
  {
    this.chunk = section;
  }

}
