////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2010 Micromata GmbH
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

/**
 * Source Render modes for macros.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public enum GWikiMacroRenderFlags
{
  /**
   * If rendered as source add new line after start of macro body.
   */
  NewLineAfterStart(0x0001), //
  /**
   * if rendered as source insert new line before end of macro body.
   */
  NewLineBeforeEnd(0x0002), //
  /**
   * Wenn parsing macro body, remove white spaces before tokenize.
   */
  TrimTextContent(0x0010), //
  /**
   * This macro should not be wrapped into a paragraph (
   * <p/>
   * )
   */
  NoWrapWithP(0x0020), //
  /**
   * The evaled macro body is a text block, which may wrapped in a p.
   */
  ContainsTextBlock(0x0040), //
  ;
  private int flag;

  private GWikiMacroRenderFlags(int flag)
  {
    this.flag = flag;
  }

  public static int combine(GWikiMacroRenderFlags... modes)
  {
    int flags = 0;
    for (GWikiMacroRenderFlags r : modes) {
      flags |= r.getFlag();
    }
    return flags;
  }

  public boolean isSet(int flags)
  {
    return (flags & flag) == flag;
  }

  public int getFlag()
  {
    return flag;
  }
}
