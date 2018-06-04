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

package de.micromata.genome.gwiki.page.impl.i18n;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroInfo.MacroParamType;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.MacroInfo;
import de.micromata.genome.gwiki.page.impl.wiki.MacroInfoParam;
import de.micromata.genome.gwiki.utils.WebUtils;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
@MacroInfo(info = "Render a text which is translated by users current language",
    params = {
        //        @MacroInfoParam(name = "module"),
        @MacroInfoParam(name = "escapeHtml", type = MacroParamType.Boolean),
        @MacroInfoParam(name = "lang", info = "language in short form (en, de)"),
        @MacroInfoParam(name = "key", required = true, info = "I18N key"),
        @MacroInfoParam(name = "defaultValue", info = "if translation not found, use this text to render")

    })
public class GWikiI18nMacroBean extends GWikiMacroBean
{

  private static final long serialVersionUID = -3628546889099798125L;

  /**
   * Optional page id
   */
  private String module;

  /**
   * escape content HTML
   */
  private boolean escapeHtml = false;

  /**
   * Use language.
   */
  private String lang;

  /**
   * Key
   */
  private String key;

  /**
   * default value
   */
  private String defaultValue;

  /**
   * Additionally args.
   */
  private Object[] args;

  @Override
  public boolean renderImpl(GWikiContext ctx, MacroAttributes attrs)
  {
    if (key == null) {
      if (attrs != null) {
        key = attrs.getDefaultValue();
      }
    }
    String value;
    String defVal = defaultValue;
    if (defVal == null) {
      defVal = key;
    }
    if (key == null) {
      key = defaultValue;
    }
    if (args == null) {
      value = ctx.getWikiWeb().getI18nProvider().translate(ctx, key, defVal);
    } else {
      value = ctx.getWikiWeb().getI18nProvider().translate(ctx, key, defVal, args);
    }
    if (escapeHtml == true) {
      ctx.append(WebUtils.escapeHtml(value));
    } else {
      ctx.append(value);
    }
    return true;
  }

  public String getModule()
  {
    return module;
  }

  public void setModule(String module)
  {
    this.module = module;
  }

  public String getLang()
  {
    return lang;
  }

  public void setLang(String lang)
  {
    this.lang = lang;
  }

  public String getKey()
  {
    return key;
  }

  public void setKey(String key)
  {
    this.key = key;
  }

  public boolean isEscapeHtml()
  {
    return escapeHtml;
  }

  public void setEscapeHtml(boolean escapeHtml)
  {
    this.escapeHtml = escapeHtml;
  }

  public String getDefaultValue()
  {
    return defaultValue;
  }

  public void setDefaultValue(String defaultValue)
  {
    this.defaultValue = defaultValue;
  }

  public Object[] getArgs()
  {
    return args;
  }

  public void setArgs(Object[] args)
  {
    this.args = args;
  }

}
