/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   21.10.2009
// Copyright Micromata 21.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.model;

import java.util.Map;

import de.micromata.genome.gwiki.controls.GWikiEditPageActionBean;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.GWikiEditableArtefakt;
import de.micromata.genome.gwiki.page.impl.GWikiEditorArtefakt;
import de.micromata.genome.gwiki.page.impl.GWikiPropsDescriptor;
import de.micromata.genome.gwiki.page.impl.GWikiPropsEditorArtefakt;

public class GWikiPropsArtefakt extends GWikiPersistArtefaktBase<GWikiProps> implements GWikiEditableArtefakt
{
  private static final long serialVersionUID = -7444414246579867245L;

  private GWikiPropsDescriptor propDescriptor = null;

  public GWikiPropsArtefakt()
  {

  }

  public GWikiPropsArtefakt(GWikiProps props)
  {
    setCompiledObject(props);
  }

  @Override
  public boolean renderWithParts(GWikiContext ctx)
  {
    ctx.append("Properties cannot be viewed");
    return true;
  }

  public String getFileSuffix()
  {
    return ".properties";
  }

  @SuppressWarnings("unchecked")
  public GWikiEditorArtefakt getEditor(GWikiElement elementToEdit, GWikiEditPageActionBean bean, String partKey)
  {
    return new GWikiPropsEditorArtefakt(elementToEdit, bean, partKey, this, propDescriptor);
  }

  public Map<String, String> getStorageData()
  {
    return getCompiledObject().getMap();
  }

  public void setStorageData(Map<String, String> map)
  {
    setCompiledObject(new GWikiProps(map));
  }

  public GWikiPropsDescriptor getPropDescriptor()
  {
    return propDescriptor;
  }

  public void setPropDescriptor(GWikiPropsDescriptor propDescriptor)
  {
    this.propDescriptor = propDescriptor;
  }

}
