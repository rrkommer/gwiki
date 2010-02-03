/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   27.10.2009
// Copyright Micromata 27.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.controls.GWikiEditPageActionBean;
import de.micromata.genome.gwiki.model.GWikiArtefaktBase;
import de.micromata.genome.gwiki.model.GWikiElement;

public abstract class GWikiEditorArtefaktBase<T extends Serializable> extends GWikiArtefaktBase<T> implements GWikiEditorArtefakt<T>
{

  private static final long serialVersionUID = -5010610329543479810L;

  protected String partName;

  protected GWikiElement elementToEdit;

  protected GWikiEditPageActionBean editBean;

  public GWikiEditorArtefaktBase(GWikiElement elementToEdit, GWikiEditPageActionBean editBean, String partName)
  {
    this.partName = partName;
    this.elementToEdit = elementToEdit;
    this.editBean = editBean;
  }

  public String getTabTitle()
  {
    return StringUtils.defaultString(partName);
  }

  public String getPartName()
  {
    return partName;
  }

  public void setPartName(String partName)
  {
    this.partName = partName;
  }

  public GWikiElement getElementToEdit()
  {
    return elementToEdit;
  }

  public void setElementToEdit(GWikiElement elementToEdit)
  {
    this.elementToEdit = elementToEdit;
  }

  public GWikiEditPageActionBean getEditBean()
  {
    return editBean;
  }

  public void setEditBean(GWikiEditPageActionBean editBean)
  {
    this.editBean = editBean;
  }
}
