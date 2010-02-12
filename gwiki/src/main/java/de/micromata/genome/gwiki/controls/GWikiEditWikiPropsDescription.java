/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   23.10.2009
// Copyright Micromata 23.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.controls;

/**
 * TODO gwiki delete me.
 * 
 * @deprecated now implemented via MetaTemplates.
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
@Deprecated
public enum GWikiEditWikiPropsDescription implements GWikiEditPropsDescription
{
  TYPE(false, "Type of Page"), //
  PARENTPAGE(false, "Parent page id"), //
  AUTH_VIEW(true, "Right to view page"), //
  AUTH_EDIT(true, "Right to edit page"), //
  CONTENTYPE(true, "Content type delivered by page"), //
  WIKITEMPLATE(true, "Template page id"), //
  WIKICONTROLERCLASS(true, "Controler page id"), //
  ;
  private boolean editable;

  private String description;

  private GWikiEditWikiPropsDescription(boolean editable, String description)
  {
    this.editable = editable;
    this.description = description;
  }

  public String getName()
  {
    return name();
  }

  public String getDescription()
  {
    return description;
  }

  public boolean isEditable()
  {
    return editable;
  }

}
