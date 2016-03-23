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

import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.RenderModes;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiBodyEvalMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiRuntimeMacro;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.MacroInfo;
import de.micromata.genome.gwiki.page.impl.wiki.MacroInfoParam;

/**
 * if macro to test against language (lang) or right.
 * 
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
@MacroInfo(info = "if macro to test against language (lang) or right.",
    params = {
        @MacroInfoParam(name = "lang", info = "Test if user has given language (de,en)"),
        @MacroInfoParam(name = "right", info = "Test if user has given right"),
        @MacroInfoParam(name = "renderMode", info = "Test if Page is render in given RenderMode.<br/>"
            + "See class RenderModes"),
    })
public class GWikiIfMacro extends GWikiMacroBean implements GWikiBodyEvalMacro, GWikiRuntimeMacro
{

  private static final long serialVersionUID = -4851793784009861436L;

  private String lang;

  private String right;

  private String renderMode;

  public boolean checkCondition(GWikiContext ctx, MacroAttributes attrs)
  {
    if (StringUtils.isNotEmpty(lang) == true) {
      Locale uloc = ctx.getWikiWeb().getAuthorization().getCurrentUserLocale(ctx);
      if (uloc.getLanguage().equals(lang) == true) {
        return true;
      }
      return false;
    } else if (StringUtils.isNotEmpty(right) == true) {
      if (ctx.isAllowTo(right) == true) {
        return true;
      }
      return false;
    } else if (StringUtils.isNotEmpty(renderMode) == true) {
      RenderModes rm = RenderModes.getRenderMode(renderMode);
      if (rm == null) {
        return true;
      }
      return rm.isSet(ctx.getRenderMode());
    }
    return true;
  }

  @Override
  public boolean renderImpl(GWikiContext ctx, MacroAttributes attrs)
  {
    if (checkCondition(ctx, attrs) == true) {
      attrs.getChildFragment().render(ctx);
    }
    return true;
  }

  public String getLang()
  {
    return lang;
  }

  public void setLang(String lang)
  {
    this.lang = lang;
  }

  public String getRight()
  {
    return right;
  }

  public void setRight(String right)
  {
    this.right = right;
  }

  public String getRenderMode()
  {
    return renderMode;
  }

  public void setRenderMode(String renderMode)
  {
    this.renderMode = renderMode;
  }

}
