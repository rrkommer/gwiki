/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   09.12.2009
// Copyright Micromata 09.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.model;

import de.micromata.genome.gwiki.model.config.GWikiMetaTemplate;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * GWikiWeb static utils.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiWebUtils
{
  public static GWikiElement createNewElement(GWikiContext wikiContext, String id, String metaTemplateId, String title)
  {
    GWikiProps props = new GWikiProps();
    GWikiMetaTemplate template = wikiContext.getWikiWeb().findMetaTemplate(metaTemplateId);
    props.setStringValue(GWikiPropKeys.TYPE, template.getElementType());
    props.setStringValue(GWikiPropKeys.WIKIMETATEMPLATE, metaTemplateId);
    props.setStringValue(GWikiPropKeys.TITLE, title);
    GWikiElementInfo neiei = new GWikiElementInfo(props, template);
    neiei.setId(id);
    GWikiElement elementToEdit = wikiContext.getWikiWeb().getStorage().createElement(neiei);
    return elementToEdit;
  }

}
