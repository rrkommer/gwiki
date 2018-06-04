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

package de.micromata.genome.gwiki.page.impl.wiki.macros;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBase;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroInfo;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroInfo.MacroInfoBean;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiRuntimeMacro;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.utils.WebUtils;

/**
 * Macro place holder for unknown macro.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiMacroUnknown extends GWikiMacroBase implements GWikiRuntimeMacro
{

  private static final long serialVersionUID = -1990609591192712242L;

  private String message;

  public GWikiMacroUnknown()
  {
  }

  public GWikiMacroUnknown(String message)
  {
    this.message = message;
  }

  @Override
  public boolean evalBody()
  {
    return false;
  }

  @Override
  public boolean hasBody()
  {
    return false;
  }

  @Override
  public boolean render(MacroAttributes attrs, GWikiContext ctx)
  {
    String msg = message;
    if (msg == null) {
      msg = ctx.getTranslated("gwiki.macro.unknown.error") + WebUtils.escapeHtml(attrs.getCmd());
    }
    ctx.append("<font color=\"red\">").append(WebUtils.escapeHtml(msg)).append("</font>");
    return true;
  }

  @Override
  public int getRenderModes()
  {
    return 0;
  }

  @Override
  public boolean isRestricted(MacroAttributes attrs, GWikiContext ctx)
  {
    return true;
  }

  @Override
  public GWikiMacroInfo getMacroInfo()
  {
    MacroInfoBean mi = new MacroInfoBean();
    mi.setInfo("Unknown macro.");
    return mi;
  }

}
