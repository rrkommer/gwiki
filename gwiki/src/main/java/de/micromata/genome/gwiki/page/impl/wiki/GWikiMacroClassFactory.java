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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroInfo.MacroParamInfo;
import de.micromata.genome.gwiki.utils.ClassUtils;
import de.micromata.genome.gwiki.utils.StringUtils;

public class GWikiMacroClassFactory implements GWikiMacroFactory
{

  private Class<? extends GWikiMacro> clazz;

  /**
   * combinations of GWikiMacroRenderFlags.
   * 
   * if 0, take getRenderModes() from macro class.
   */
  private int renderModes = 0;

  public GWikiMacroClassFactory()
  {

  }

  public GWikiMacroClassFactory(Class<? extends GWikiMacro> clazz)
  {
    this.clazz = clazz;
  }

  public GWikiMacroClassFactory(Class<? extends GWikiMacro> clazz, int renderModes)
  {
    this.clazz = clazz;
    this.renderModes = renderModes;
  }

  @Override
  public String toString()
  {
    return "GWikiMacroClassFactory(" + clazz.getName() + ")";
  }

  @Override
  public GWikiMacro createInstance()
  {
    GWikiMacro macro = ClassUtils.createDefaultInstance(clazz);
    if (macro instanceof GWikiMacroBase && macro.getRenderModes() == 0) {
      ((GWikiMacroBase) macro).setRenderModes(renderModes);
    }
    if (macro instanceof GWikiMacroBase) {
      ((GWikiMacroBase) macro).setMacroInfo(getMacroInfo());
    }
    return macro;
  }

  @Override
  public GWikiMacroInfo getMacroInfo()
  {
    if (GWikiMacroWithInfo.class.isAssignableFrom(clazz) == true) {
      return ((GWikiMacroWithInfo) createInstance()).getMacroInfo();
    }

    List<MacroInfo> anots = de.micromata.genome.util.runtime.ClassUtils.findClassAnnotations(clazz, MacroInfo.class);
    //    Map<String, MacroParamInfo> params = new HashMap<>();
    List<MacroParamInfo> paramlist = new ArrayList<>();
    Set<String> paramNames = new HashSet<>();
    String info = null;
    int renderFlags = 0;
    for (MacroInfo mi : anots) {
      if (StringUtils.isBlank(info) == true) {
        info = mi.info();
      }
      for (MacroInfoParam pi : mi.params()) {
        if (paramNames.contains(pi.name()) == true) {
          continue;
        }
        paramNames.add(pi.name());

        paramlist.add(new MacroParamInfo(pi));
      }
      for (GWikiMacroRenderFlags rf : mi.renderFlags()) {
        renderFlags |= rf.getFlag();
      }
    }
    int collectedRenderFlags = renderFlags;
    String macinfo = info;
    GWikiMacroClassFactory fac = this;
    return new GWikiMacroInfoBase()
    {

      @Override
      public String getInfo()
      {
        return macinfo;
      }

      @Override
      public boolean hasBody()
      {
        return fac.hasBody();
      }

      @Override
      public boolean evalBody()
      {
        return fac.evalBody();
      }

      @Override
      public boolean isRteMacro()
      {
        return fac.isRteMacro();
      }

      @Override
      public List<MacroParamInfo> getParamInfos()
      {
        return paramlist;
      }

      @Override
      public int getRenderFlags()
      {
        return collectedRenderFlags;
      }

    };

  }

  @Override
  public boolean evalBody()
  {
    return GWikiBodyEvalMacro.class.isAssignableFrom(clazz);
  }

  @Override
  public boolean hasBody()
  {
    return GWikiBodyMacro.class.isAssignableFrom(clazz);
  }

  @Override
  public boolean isRteMacro()
  {
    return GWikiMacroRte.class.isAssignableFrom(clazz);
  }

  public Class<? extends GWikiMacro> getClazz()
  {
    return clazz;
  }

  public void setClazz(Class<? extends GWikiMacro> clazz)
  {
    this.clazz = clazz;
  }

}
