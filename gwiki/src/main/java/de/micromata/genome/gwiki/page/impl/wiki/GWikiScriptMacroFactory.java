/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   09.11.2009
// Copyright Micromata 09.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki;

import java.io.Serializable;

import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.model.GWikiWeb;
import de.micromata.genome.gwiki.page.impl.wiki.macros.GWikiScriptMacro;

public class GWikiScriptMacroFactory implements GWikiMacroFactory, GWikiPropKeys, Serializable
{

  private static final long serialVersionUID = 396117543440444247L;

  private GWikiElementInfo elementInfo;

  public GWikiScriptMacroFactory(GWikiElementInfo elementInfo)
  {
    this.elementInfo = elementInfo;
  }
  public String toString()
  {
    return "GWikiScriptMacro(" + elementInfo.getId() + ")"; 
    
  }
  public GWikiMacro createInstance()
  {
    GWikiScriptMacro ma = new GWikiScriptMacro(this, GWikiWeb.get().getElement(elementInfo));
    return ma;
  }

  public boolean evalBody()
  {
    return elementInfo.getProps().getBooleanValue(MACRO_EVAL_BODY);
  }

  public boolean hasBody()
  {
    return elementInfo.getProps().getBooleanValue(MACRO_WITH_BODY);
  }

  public boolean isRteMacro()
  {
    return false;
  }

  public GWikiElementInfo getElement()
  {
    return elementInfo;
  }

  public void setElement(GWikiElementInfo elementInfo)
  {
    this.elementInfo = elementInfo;
  }

}
