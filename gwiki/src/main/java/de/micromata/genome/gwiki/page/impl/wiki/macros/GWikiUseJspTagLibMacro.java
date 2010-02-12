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

package de.micromata.genome.gwiki.page.impl.wiki.macros;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.jsp.tagext.TagInfo;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.gspt.taglibs.TagLibraryInfoImpl;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiCompileTimeMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiCompileTimeMacroBase;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFragment;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiParserContext;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiTokens;

/**
 * Macro which registers a tag libarary as macro.
 * 
 * Registered macros will be access by prefix_tag
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiUseJspTagLibMacro extends GWikiCompileTimeMacroBase implements GWikiCompileTimeMacro
{

  private static final long serialVersionUID = -7610724416297860345L;

  public Collection<GWikiFragment> getFragments(GWikiMacroFragment macroFrag, GWikiWikiTokens tks, GWikiWikiParserContext ctx)
  {
    String uri = macroFrag.getAttrs().getArgs().getStringValue("uri");
    if (StringUtils.isEmpty(uri) == true) {
      uri = macroFrag.getAttrs().getArgs().getStringValue(MacroAttributes.DEFAULT_VALUE_KEY);
    }
    String prefix = macroFrag.getAttrs().getArgs().getStringValue("prefix");

    TagLibraryInfoImpl tagLib = new TagLibraryInfoImpl(GWikiContext.getCurrent().getCreatePageContext(), prefix, uri);
    for (TagInfo ti : tagLib.getTags()) {
      String macroName = prefix + ti.getTagName();
      ctx.getMacroFactories().put(macroName, new GWikiJspTagMacroFactory(ti));
    }
    List<GWikiFragment> l = new ArrayList<GWikiFragment>();
    l.add(macroFrag);
    return l;
  }

}
