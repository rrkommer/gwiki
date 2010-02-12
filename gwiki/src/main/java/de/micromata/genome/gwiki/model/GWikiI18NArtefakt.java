/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   25.11.2009
// Copyright Micromata 25.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.model;

import java.util.Map;
import java.util.Properties;

import de.micromata.genome.gwiki.controls.GWikiEditPageActionBean;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.GWikiEditableArtefakt;
import de.micromata.genome.gwiki.page.impl.GWikiEditorArtefakt;
import de.micromata.genome.gwiki.page.impl.GWikiTextPageEditorArtefakt;
import de.micromata.genome.gwiki.utils.PropUtils;

/**
 * Artefakt holding I18N-Properties.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiI18NArtefakt extends GWikiTextArtefaktBase<GWikiI18nMap> implements GWikiEditableArtefakt
{

  private static final long serialVersionUID = 1333234153013724487L;

  @Override
  public boolean renderWithParts(GWikiContext ctx)
  {
    return false;
  }

  public String getFileSuffix()
  {
    return ".properties";
  }

  public GWikiEditorArtefakt< ? > getEditor(GWikiElement elementToEdit, GWikiEditPageActionBean bean, String partKey)
  {
    return new GWikiTextPageEditorArtefakt(elementToEdit, bean, partKey, this);
  }

  @SuppressWarnings("unchecked")
  @Override
  public GWikiI18nMap getCompiledObject()
  {
    if (super.getCompiledObject() != null) {
      return super.getCompiledObject();
    }
    String data = getStorageData();
    Properties props = PropUtils.toProperties(data);
    GWikiI18nMap nm = new GWikiI18nMap();
    nm.putAll((Map) props);
    setCompiledObject(nm);
    return nm;
  }

}
