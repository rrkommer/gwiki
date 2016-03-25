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

package de.micromata.genome.gwiki.page.impl.wiki.macros;

import de.micromata.genome.gwiki.page.impl.wiki.GWikiBodyEvalMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroClassFactory;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFactory;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroRenderFlags;
import de.micromata.genome.gwiki.page.impl.wiki.macros.html.GWikiHtmlDivMacro;
import de.micromata.genome.gwiki.page.impl.wiki.macros.html.GWikiHtmlTableMacro;
import de.micromata.genome.gwiki.page.impl.wiki.macros.html.GWikiHtmlTdMacro;
import de.micromata.genome.gwiki.page.impl.wiki.macros.html.GWikiHtmlTrMacro;
import de.micromata.genome.gwiki.utils.html.Html2WikiTransformInfo;

/**
 * HTML tag macro with a evaluated body.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiHtmlBodyTagMacro extends GWikiHtmlTagMacro implements GWikiBodyEvalMacro
{

  private static final long serialVersionUID = 9204139467653157793L;

  public static int getStandardBodyRenderFlags()
  {
    return GWikiMacroRenderFlags.combine(GWikiMacroRenderFlags.NewLineAfterStart,
        GWikiMacroRenderFlags.NewLineBeforeEnd,
        GWikiMacroRenderFlags.TrimTextContent, GWikiMacroRenderFlags.ContainsTextBlock,
        GWikiMacroRenderFlags.NoWrapWithP);
  }

  public static int getStandardNestedBodyRenderFlags()
  {
    return GWikiMacroRenderFlags.combine(GWikiMacroRenderFlags.NewLineAfterStart,
        GWikiMacroRenderFlags.NewLineBeforeEnd,
        GWikiMacroRenderFlags.TrimTextContent, GWikiMacroRenderFlags.TrimWsAfter);
  }

  public static GWikiMacroFactory standardBody()
  {
    return new GWikiMacroClassFactory(GWikiHtmlBodyTagMacro.class, getStandardBodyRenderFlags());
  }

  public static GWikiMacroFactory nestedHtmlBody()
  {
    return new GWikiMacroClassFactory(GWikiHtmlBodyTagMacro.class, getStandardNestedBodyRenderFlags());
  }

  public GWikiHtmlBodyTagMacro(int renderModes)
  {
    setRenderModes(renderModes);
  }

  public GWikiHtmlBodyTagMacro()
  {
    setRenderModesFromAnnot();
    if (getRenderModes() == 0) {
      setRenderModes(getStandardBodyRenderFlags());
    }
  }

  public static GWikiMacroFactory table()
  {
    return new GWikiMacroClassFactory(GWikiHtmlTableMacro.class);
  }

  public static GWikiMacroFactory tr()
  {
    return new GWikiMacroClassFactory(GWikiHtmlTrMacro.class);
  }

  public static GWikiMacroFactory td()
  {
    return new GWikiMacroClassFactory(GWikiHtmlTdMacro.class);
  }

  public static GWikiMacroFactory div()
  {
    return new GWikiMacroClassFactory(GWikiHtmlDivMacro.class);
  }

  public Html2WikiTransformInfo getTransformInfo()
  {
    // no transform needed
    return null;
  }
}
