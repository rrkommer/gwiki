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
package de.micromata.genome.gwiki.pagetemplates_1_0.editor;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiLog;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.GWikiContent;
import de.micromata.genome.gwiki.page.impl.GWikiWikiPageArtefakt;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiCollectFragmentTypeVisitor;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentLink;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiParser;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public abstract class PtWikiTextEditorBase extends PtSectionEditorBase
{
  private static final long serialVersionUID = -6191445352617140361L;

  /**
   * content of whole page
   */
  protected String wikiText;

  /**
   * start idx of section
   */
  protected int startSec = -1;

  /**
   * end idx of section
   */
  protected int endSec = -1;

  private GWikiWikiPageArtefakt wikiArtefakt;

  /**
   * @param element
   * @param sectionName
   * @param editor
   */
  public PtWikiTextEditorBase(GWikiElement element, String sectionName, String editor, String hint)
  {
    super(element, sectionName, editor, hint);
    extractContent();
  }

  public boolean render(final GWikiContext ctx)
  {
    showHint(ctx);
    renderWithParts(ctx);

    return true;
  }

  private GWikiWikiPageArtefakt getPageArtefakt()
  {
    Map<String, GWikiArtefakt< ? >> map = new HashMap<String, GWikiArtefakt< ? >>();
    element.collectParts(map);
    GWikiWikiPageArtefakt wiki = null;
    for (GWikiArtefakt< ? > art : map.values()) {
      if ((art instanceof GWikiWikiPageArtefakt) == true) {
        wiki = (GWikiWikiPageArtefakt) art;
        break;
      }
    }
    return wiki;
  }

  private void showHint(final GWikiContext ctx)
  {
    if (hint != null && hint.length() > 0) {
      String hintDesc = ctx.getTranslated("gwiki.editor.hint");
      ctx.append("<p>" + hintDesc + ": " + hint + "</p>");
    }
  }

  /**
   * returns a pageId to for deletion
   * 
   * @param ctx
   * @param newContent
   * @param fieldNumber
   * @return
   */
  protected String updateSection(final GWikiContext ctx, String newContent, String fieldNumber)
  {
    String fieldPageId = null;

    extractContent();

    if (endSec == -1) {
      return null;
    }

    StringBuilder sb = new StringBuilder();

    if (fieldNumber != null) {
      int index = 0;

      try {
        index = Integer.parseInt(fieldNumber);
      } catch (NumberFormatException e) {
        GWikiLog.warn("failed to parse number", e);
        return null;
      }

      String[] contentArray = getEditContent().split(",");

      if (index >= contentArray.length) {
        return null;
      }

      if (StringUtils.isEmpty(newContent)) {
        GWikiFragmentLink link = getLinkForField(ctx, index, contentArray);

        if (link != null) {
          fieldPageId = link.getTargetPageId();
        }
      }

      contentArray[index] = newContent;

      sb.append(wikiText.substring(0, startSec));

      for (int i = 0; i < contentArray.length; i++) {
        if (StringUtils.isNotEmpty(contentArray[i])) {
          if (i != 0) { // && i < (contentArray.length - 1)) {
            sb.append(",");
          }
          sb.append(contentArray[i]);
        }
      }

      sb.append(wikiText.substring(endSec));
    } else {
      sb.append(wikiText.substring(0, endSec));

      if (StringUtils.isNotEmpty(wikiText.substring(startSec, endSec))) {
        sb.append(",");
      }

      sb.append(newContent);
      sb.append(wikiText.substring(endSec));
    }

    wikiArtefakt.setStorageData(sb.toString());

    return fieldPageId;
  }

  /**
   * @param ctx
   * @param fieldPageId
   * @param index
   * @param contentArray
   * @return
   */
  protected GWikiFragmentLink getLinkForField(final GWikiContext ctx, int index, String[] contentArray)
  {
    GWikiFragmentLink link = null;
    GWikiWikiParser wkparse = new GWikiWikiParser();
    GWikiContent gwikiContent = wkparse.parse(ctx, contentArray[index]);

    GWikiCollectFragmentTypeVisitor links = new GWikiCollectFragmentTypeVisitor(GWikiFragmentLink.class);
    gwikiContent.iterate(links);

    if (!links.getFound().isEmpty()) {
      link = (GWikiFragmentLink) links.getFound().get(0);
    }

    return link;
  }

  protected void updateSection(final GWikiContext ctx, String newContent)
  {
    if (StringUtils.isEmpty(newContent)) {
      ctx.addSimpleValidationError(ctx.getTranslated("gwiki.editor.empty"));
      return;
    }

    extractContent();
    if (endSec == -1) {
      return;
    }
    String nc = wikiText.substring(0, startSec) + newContent + wikiText.substring(endSec);
    wikiArtefakt.setStorageData(nc);
  }

  protected void renderAttr(final GWikiContext ctx, String key, String value)
  {
    ctx.append(" " + key + "=\"").append(StringEscapeUtils.escapeHtml(value)).append("\"");
  }

  private void extractContent()
  {
    String start = "{pteditsection:name=" + sectionName;

    wikiArtefakt = getPageArtefakt();
    if (wikiArtefakt == null) {
      return;
    }
    wikiText = wikiArtefakt.getStorageData();
    int idx = wikiText.indexOf(start);
    if (idx == -1) {
      return;
    }
    idx += start.length();
    String sec = wikiText.substring(idx);
    int lidx = sec.indexOf('}');
    if (lidx == -1) {
      return;
    }
    startSec = idx + lidx + 1;
    sec = sec.substring(lidx);
    lidx = sec.indexOf("{pteditsection}");
    if (lidx == -1) {
      return;
    }
    endSec = startSec + lidx - 1;

  }

  protected String getEditContent()
  {
    if (endSec == -1) {
      return "";
    }
    return wikiText.substring(startSec, endSec);
  }

}
