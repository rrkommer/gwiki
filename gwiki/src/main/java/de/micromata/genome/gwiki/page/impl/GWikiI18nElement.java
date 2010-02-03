/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   25.11.2009
// Copyright Micromata 25.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl;

import java.util.Map;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiI18NArtefakt;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * For Internationalization.
 * 
 * @author roger@micromata.de
 * 
 */
public class GWikiI18nElement extends GWikiWikiPage
{

  private static final long serialVersionUID = 5117850385113074134L;

  public GWikiI18nElement(GWikiElementInfo elementInfo)
  {
    super(elementInfo);
  }

  public boolean hasKey(String lang, String key)
  {
    GWikiArtefakt<?> f = parts.get(lang);
    if (f == null || (f instanceof GWikiI18NArtefakt) == false) {
      return false;
    }
    GWikiI18NArtefakt ia = (GWikiI18NArtefakt) f;
    return ia.getCompiledObject().containsKey(key);
  }

  /**
   * 
   * @param lang
   * @param key
   * @return null if not found
   */
  public String getMessage(String lang, String key)
  {
    GWikiArtefakt<?> f = parts.get(lang);
    if (f == null || (f instanceof GWikiI18NArtefakt) == false) {
      return null;
    }
    GWikiI18NArtefakt ia = (GWikiI18NArtefakt) f;
    return ia.getCompiledObject().get(key);
  }

  public GWikiArtefakt<?> getMainPart()
  {
    return null;
  }

  public void serve(GWikiContext ctx)
  {

  }

  @Override
  public void saveParts(GWikiContext ctx, Map<String, GWikiEditorArtefakt<?>> editors)
  {
    super.saveParts(ctx, editors);
  }

}
