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

////////////////////////////////////////////////////////////////////////////


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

////////////////////////////////////////////////////////////////////////////


package de.micromata.genome.gwiki.page.impl.wiki2;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import de.micromata.genome.gwiki.page.GWikiStandaloneContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFactory;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiParser;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiParserContext;

public abstract class GWikiWikiParserTestBase extends TestCase
{

  protected List<GWikiFragment> parseText(String wikiText, String macroName, GWikiMacroFactory macroFactorie)
  {
    Map<String, GWikiMacroFactory> macroFactories = new HashMap<String, GWikiMacroFactory>();
    macroFactories.put(macroName, macroFactorie);
    return parseText(wikiText, macroFactories);
  }

  protected List<GWikiFragment> parseText(String wikiText, Map<String, GWikiMacroFactory> macroFactories)
  {
    GWikiWikiParserContext ctx = new GWikiWikiParserContext();
    ctx.getMacroFactories().putAll(macroFactories);
    GWikiWikiParser parser = new GWikiWikiParser();
    parser.parseFrags(wikiText, ctx);
    List<GWikiFragment> frags = ctx.popFragList();
    return frags;
  }

  protected List<GWikiFragment> parseText(String wikiText)
  {
    Map<String, GWikiMacroFactory> macroFactories = Collections.emptyMap();
    return parseText(wikiText, macroFactories);
  }

  protected String wiki2html(String wikiText)
  {
    List<GWikiFragment> fr = parseText(wikiText);
    GWikiStandaloneContext ctx = new GWikiStandaloneContext();
    for (GWikiFragment f : fr) {
      f.render(ctx);
    }
    return ctx.getJspWriter().getString();
  }

  protected void w2htest(String wiki, String html)
  {
    String genHtml = wiki2html(wiki);
    if (html.equals(genHtml) == false) {
      System.out.println("exp:[" + html + "]\nrec:[" + genHtml + "]");
    }
    assertEquals(html, genHtml);
  }
}
