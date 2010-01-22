/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   04.12.2009
// Copyright Micromata 04.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki.macros;

import java.util.List;
import java.util.Vector;

import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiSessionProvider;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.RenderModes;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroClassFactory;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFactory;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;

public class GWikiBreadcrumbsMacroBean extends GWikiMacroBean
{

  private static final long serialVersionUID = -2372704729286948715L;

  public static final String GWikiBreadcrumbsFilterKEY = "de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiBreadcrumbsMacroBean.key";

  private int maxItems = 10;

  public static GWikiMacroFactory getFactory()
  {
    return new GWikiMacroClassFactory(GWikiBreadcrumbsMacroBean.class);
  }

  @SuppressWarnings("unchecked")
  public void registerPage(GWikiContext wikiContext)
  {
    GWikiElement el = wikiContext.getWikiElement();
    if (el == null) {
      return;
    }
    if (el.getElementInfo().isViewable() == false) {
      return;
    }
    if (el.getElementInfo().isIndexed() == false) {
      return;
    }
    if (wikiContext.getBooleanRequestAttribute(GWikiWeb.WIKI_NOCACHE_REQ_ATTR) == true) {
      return;
    }
    GWikiSessionProvider sessprov = wikiContext.getWikiWeb().getSessionProvider();
    Vector<String> current = (Vector<String>) sessprov.getSessionAttribute(wikiContext, GWikiBreadcrumbsFilterKEY);
    if (current == null) {
      // use Vector because it is serialized and synchronized.
      current = new Vector<String>();
    }

    String id = el.getElementInfo().getId();
    int found = current.indexOf(id);
    if (found != -1) {
      current.remove(found);
    }
    if (current.size() >= maxItems) {
      current.remove(0);
    }
    current.add(id);
    sessprov.setSessionAttribute(wikiContext, GWikiBreadcrumbsFilterKEY, current);
  }

  @SuppressWarnings("unchecked")
  public List<String> getBreadcrumbs(GWikiContext wikiContext)
  {
    GWikiSessionProvider sessprov = wikiContext.getWikiWeb().getSessionProvider();
    return (List<String>) sessprov.getSessionAttribute(wikiContext, GWikiBreadcrumbsFilterKEY);
  }

  public static void doRender(GWikiContext ctx)
  {
    new GWikiBreadcrumbsMacroBean().render(new MacroAttributes(), ctx);
  }

  @Override
  public boolean renderImpl(GWikiContext ctx, MacroAttributes attrs)
  {
    if (RenderModes.NoToc.isSet(ctx.getRenderMode()) == true) {
      return true;
    }
    List<String> list = getBreadcrumbs(ctx);
    if (list == null) {
      registerPage(ctx);
      return true;
    }
    synchronized (list) {
      for (String id : list) {
        GWikiElementInfo ei = ctx.getWikiWeb().findElementInfo(id);
        if (ei != null && ei.isViewable() == true && ei.isIndexed() == true) {
          ctx.append(ctx.renderLocalUrl(id), " | ");
        }
      }
    }
    registerPage(ctx);
    return true;
  }

  public int getMaxItems()
  {
    return maxItems;
  }

  public void setMaxItems(int maxItems)
  {
    this.maxItems = maxItems;
  }

}
