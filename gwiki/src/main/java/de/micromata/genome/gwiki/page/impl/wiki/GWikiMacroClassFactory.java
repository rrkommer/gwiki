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

import de.micromata.genome.gwiki.utils.ClassUtils;

public class GWikiMacroClassFactory implements GWikiMacroFactory
{
  private Class< ? extends GWikiMacro> clazz;

  private int renderModes = 0;

  public GWikiMacroClassFactory()
  {

  }

  public GWikiMacroClassFactory(Class< ? extends GWikiMacro> clazz)
  {
    this.clazz = clazz;
  }

  public GWikiMacroClassFactory(Class< ? extends GWikiMacro> clazz, int renderModes)
  {
    this.clazz = clazz;
    this.renderModes = renderModes;
  }

  public String toString()
  {
    return "GWikiMacroClassFactory(" + clazz.getName() + ")";
  }

  public GWikiMacro createInstance()
  {
    GWikiMacro macro = ClassUtils.createDefaultInstance(clazz);
    if (macro instanceof GWikiMacroBase && macro.getRenderModes() == 0) {
      ((GWikiMacroBase) macro).setRenderModes(renderModes);
    }
    return macro;
  }

  public boolean evalBody()
  {
    return GWikiBodyEvalMacro.class.isAssignableFrom(clazz);
  }

  public boolean hasBody()
  {
    return GWikiBodyMacro.class.isAssignableFrom(clazz);
  }

  public boolean isRteMacro()
  {
    return GWikiMacroRte.class.isAssignableFrom(clazz);
  }

  public Class< ? extends GWikiMacro> getClazz()
  {
    return clazz;
  }

  public void setClazz(Class< ? extends GWikiMacro> clazz)
  {
    this.clazz = clazz;
  }

}
