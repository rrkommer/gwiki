//
// Copyright (C) 2010-2016 Roger Rene Kommer & Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package de.micromata.genome.gwiki.page.impl.wiki;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.micromata.genome.util.types.Pair;

/**
 * Information about a macro.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public interface GWikiMacroInfo
{
  public static enum MacroParamType
  {
    String, Boolean, Integer, PageId,
  }

  public static class MacroParamInfo
  {
    private MacroParamType type;
    private String name;
    private boolean required;
    private String defaultValue;
    private String info;

    private List<String> enumValues = new ArrayList<>();

    public MacroParamInfo()
    {

    }

    public MacroParamInfo(MacroInfoParam anot)
    {
      this.type = anot.type();
      this.name = anot.name();
      this.required = anot.required();
      // TODO restricted
      this.defaultValue = anot.defaultValue();
      this.info = anot.info();
      this.enumValues = Arrays.asList(anot.enumValues());
    }

    public MacroParamType getType()
    {
      return type;
    }

    public void setType(MacroParamType type)
    {
      this.type = type;
    }

    public String getName()
    {
      return name;
    }

    public void setName(String name)
    {
      this.name = name;
    }

    public boolean isRequired()
    {
      return required;
    }

    public void setRequired(boolean required)
    {
      this.required = required;
    }

    public String getDefaultValue()
    {
      return defaultValue;
    }

    public void setDefaultValue(String defaultValue)
    {
      this.defaultValue = defaultValue;
    }

    public String getInfo()
    {
      return info;
    }

    public void setInfo(String info)
    {
      this.info = info;
    }

    public List<String> getEnumValues()
    {
      return enumValues;
    }

    public void setEnumValues(List<String> enumValues)
    {
      this.enumValues = enumValues;
    }

  }

  public static class MacroInfoBean extends GWikiMacroInfoBase
  {
    boolean hasBody;
    boolean evalBody;
    boolean rteMacro;
    String info;
    int renderFlags;
    List<MacroParamInfo> paramsInfos = new ArrayList<>();

    @Override
    public boolean hasBody()
    {
      return hasBody;
    }

    @Override
    public boolean evalBody()
    {
      return evalBody;
    }

    @Override
    public boolean isRteMacro()
    {
      return rteMacro;
    }

    @Override
    public String getInfo()
    {
      return info;
    }

    @Override
    public List<MacroParamInfo> getParamInfos()
    {
      return paramsInfos;
    }

    public void setHasBody(boolean hasBody)
    {
      this.hasBody = hasBody;
    }

    public void setEvalBody(boolean evalBody)
    {
      this.evalBody = evalBody;
    }

    public void setRteMacro(boolean rteMacro)
    {
      this.rteMacro = rteMacro;
    }

    public void setInfo(String info)
    {
      this.info = info;
    }

    @Override
    public int getRenderFlags()
    {
      return renderFlags;
    }

    public void setRenderFlags(int renderFlags)
    {
      this.renderFlags = renderFlags;
    }

  }

  boolean hasBody();

  boolean evalBody();

  /**
   * Can be transformed for rich text edit and back.
   * 
   * @return
   */
  boolean isRteMacro();

  /**
   * Information about the macro.
   *
   * @return the info
   */
  String getInfo();

  List<MacroParamInfo> getParamInfos();

  Pair<String, String> getRteTemplate(String macroHead);

  int getRenderFlags();
}
