/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   10.01.2010
// Copyright Micromata 10.01.2010
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki.macros;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiExecutableArtefakt;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiRuntimeMacro;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;

/**
 * Includes another Page.
 * 
 * 
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiIncludeMacro extends GWikiMacroBean implements GWikiRuntimeMacro
{
  private static final long serialVersionUID = -1172470071868033038L;

  /**
   * required page id to include
   */
  private String pageId;

  /**
   * Name of the part of the element. The part must implement the GWikiExecutableArtefakt interface.
   * 
   * Default is MainPage.
   */
  private String partName;

  /**
   * Does not include only artefakt of element, but the complate page.
   * 
   * Default is false.
   */
  private boolean complete;

  @SuppressWarnings("unchecked")
  @Override
  public boolean renderImpl(GWikiContext ctx, MacroAttributes attrs)
  {
    if (StringUtils.isEmpty(pageId) == true) {
      pageId = attrs.getArgs().getStringValue(MacroAttributes.DEFAULT_VALUE_KEY);
    }
    GWikiElement el = ctx.getWikiWeb().findElement(pageId);
    if (el == null) {
      renderErrorMessage(ctx, "include; Cannot find page with: " + pageId, attrs);
      return true;
    }
    if (complete == true) {
      ctx.getWikiWeb().reloadPage(pageId);
      return true;
    }
    Map<String, GWikiArtefakt< ? >> parts = new HashMap<String, GWikiArtefakt< ? >>();
    el.collectParts(parts);
    if (StringUtils.isNotEmpty(partName) == true) {
      GWikiArtefakt< ? > art = parts.get(partName);
      if (art == null) {
        renderErrorMessage(ctx, "include; Cannot find part " + partName + " in  " + pageId, attrs);
        return true;
      }
      if ((art instanceof GWikiExecutableArtefakt) == false) {
        renderErrorMessage(ctx, "include; Part is not executable: " + partName + " in  " + pageId, attrs);
        return true;
      }
      ((GWikiExecutableArtefakt) art).render(ctx);
    } else {
      String lPart = "MainPage";
      if (parts.get(lPart) instanceof GWikiExecutableArtefakt) {
        ((GWikiExecutableArtefakt) parts.get(lPart)).render(ctx);
      } else {
        renderErrorMessage(ctx, "include; Cannot find executable Part MainPage in  " + pageId, attrs);
      }
    }
    return true;
  }

  public String getPageId()
  {
    return pageId;
  }

  public void setPageId(String pageId)
  {
    this.pageId = pageId;
  }

  public String getPartName()
  {
    return partName;
  }

  public void setPartName(String partName)
  {
    this.partName = partName;
  }

  public boolean isComplete()
  {
    return complete;
  }

  public void setComplete(boolean complete)
  {
    this.complete = complete;
  }

}
