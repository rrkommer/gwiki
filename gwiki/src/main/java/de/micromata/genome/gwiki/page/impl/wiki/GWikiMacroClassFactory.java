////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
////////////////////////////////////////////////////////////////////////////

package de.micromata.genome.gwiki.page.impl.wiki;

import de.micromata.genome.gwiki.utils.ClassUtils;

public class GWikiMacroClassFactory implements GWikiMacroFactory
{
  private Class< ? extends GWikiMacro> clazz;

  /**
   * combinations of GWikiMacroRenderFlags.
   * 
   * if 0, take getRenderModes() from macro class.
   */
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
