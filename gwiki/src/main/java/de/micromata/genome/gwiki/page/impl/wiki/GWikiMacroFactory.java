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
import java.util.List;

public interface GWikiMacroFactory
{
  GWikiMacro createInstance();

  boolean hasBody();

  boolean evalBody();

  /**
   * Can be transformed for rich text edit and back.
   * 
   * @return
   */
  boolean isRteMacro();

  default GWikiMacroInfo getMacroInfo()
  {
    GWikiMacroFactory fac = this;
    return new GWikiMacroInfoBase()
    {

      @Override
      public String getInfo()
      {
        return "";
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
        return new ArrayList<>();
      }

    };
  }
}
