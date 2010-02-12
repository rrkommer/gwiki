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

package de.micromata.genome.gwiki.page.impl.wiki.fragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.micromata.genome.gwiki.model.AuthorizationFailedException;
import de.micromata.genome.gwiki.model.GWikiAuthorizationRights;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiCompileTimeMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiCompileTimeMacroBase;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFactory;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFragment;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiParserContext;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiTokens;

public class GWikiFragmentUnsecureHtml extends GWikiFragmentHtml
{

  private static final long serialVersionUID = 6726490960427363801L;

  public static class Macro extends GWikiCompileTimeMacroBase implements GWikiCompileTimeMacro
  {

    private static final long serialVersionUID = 6651596226823444417L;

    public void ensureRight(MacroAttributes attrs, GWikiContext ctx) throws AuthorizationFailedException
    {
    }

    public boolean evalBody()
    {
      return false;
    }

    public boolean hasBody()
    {
      return true;
    }

    public Collection<GWikiFragment> getFragments(GWikiMacroFragment macroFrag, GWikiWikiTokens tks, GWikiWikiParserContext ctx)
    {
      List<GWikiFragment> fragl = new ArrayList<GWikiFragment>();
      macroFrag.addChild(new GWikiFragmentUnsecureHtml(macroFrag.getAttrs().getBody()));
      fragl.add(macroFrag);
      return fragl;
    }

    public int getRenderModes()
    {
      return 0;
    }

  }

  public static GWikiMacroFactory getFactory()
  {
    return new GWikiMacroFactory() {

      public GWikiMacro createInstance()
      {
        return new Macro();
      }

      public boolean evalBody()
      {
        return false;
      }

      public boolean hasBody()
      {
        return true;
      }

      public boolean isRteMacro()
      {
        return false;
      }

    };
  }

  public GWikiFragmentUnsecureHtml()
  {

  }

  public GWikiFragmentUnsecureHtml(String html)
  {
    super(html);
  }

  public GWikiFragmentUnsecureHtml(GWikiFragmentUnsecureHtml other)
  {
    super(other);
  }

  @Override
  public Object clone()
  {
    return new GWikiFragmentUnsecureHtml(this);
  }

  public void ensureRight(MacroAttributes attrs, GWikiContext ctx) throws AuthorizationFailedException
  {
    if (ctx.getWikiWeb().getAuthorization().isAllowTo(ctx, GWikiAuthorizationRights.GWIKI_EDITHTML.name()) == false) {
      throw new AuthorizationFailedException("Unsecure usage of HTML: " + getHtml());
    }
  }

  @Override
  public void getSource(StringBuilder sb)
  {
    sb.append("{html}").append(html).append("{html}");
  }

}
