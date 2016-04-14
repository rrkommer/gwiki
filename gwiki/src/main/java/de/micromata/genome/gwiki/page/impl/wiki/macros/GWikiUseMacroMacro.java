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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiAuthorizationRights;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiCompileTimeMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiCompileTimeMacroBase;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroClassFactory;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFactory;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFragment;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiScriptMacroFactory;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;
import de.micromata.genome.gwiki.page.impl.wiki.MacroInfo;
import de.micromata.genome.gwiki.page.impl.wiki.MacroInfoParam;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiParserContext;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiTokens;
import de.micromata.genome.gwiki.utils.ClassUtils;

/**
 * Macro imports another macro for usage.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
@MacroInfo(info = "Use a declared Macro in this page",
    params = { @MacroInfoParam(name = "localName", info = "local macro name", required = true),
        @MacroInfoParam(name = "pageId", info = "macro is user defined an can be loaded from there."),
        @MacroInfoParam(name = "macroClass", info = "Macro is a class either Macro or MacroFactory."),
    })
public class GWikiUseMacroMacro extends GWikiCompileTimeMacroBase implements GWikiCompileTimeMacro
{
  private static final long serialVersionUID = 1423464798547568584L;

  /**
   * local macro name
   */
  private String localName;

  /**
   * macro is user defined an can be loaded from there
   */
  private String pageId;

  /**
   * Macro is a class either Macro or MacroFactory
   */
  private String macroClass;

  protected void populate(MacroAttributes attrs)
  {

    try {
      BeanUtilsBean.getInstance().populate(this, attrs.getArgs().getMap());
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  protected Collection<GWikiFragment> error(String message)
  {
    List<GWikiFragment> ret = new ArrayList<GWikiFragment>();
    MacroAttributes ma = new MacroAttributes();
    ret.add(new GWikiMacroFragment(new GWikiMacroUnknown(message), ma));
    return ret;
  }

  @Override
  public Collection<GWikiFragment> getFragments(GWikiMacroFragment macroFrag, GWikiWikiTokens tks,
      GWikiWikiParserContext ctx)
  {
    populate(macroFrag.getAttrs());

    if (StringUtils.isEmpty(localName) == true) {
      return error("usemacro; In usemacro localName has to be defined");
    }
    if (StringUtils.isNotEmpty(macroClass) == true) {
      try {
        Object o = ClassUtils.createDefaultInstance(macroClass);
        if (o instanceof GWikiMacroFactory) {
          ctx.getMacroFactories().put(localName, (GWikiMacroFactory) o);
        } else if (o instanceof GWikiMacro) {
          ctx.getMacroFactories().put(localName, new GWikiMacroClassFactory(((GWikiMacro) o).getClass()));
        } else {
          return error("usemacro; Invalid type of macro: " + o.getClass().getName());
        }
      } catch (Exception ex) {
        return error("usemacro; Cannot find/create Macro class: " + macroClass);
      }
    } else if (StringUtils.isNotEmpty(pageId) == true) {
      GWikiContext wikiContext = GWikiContext.getCurrent();
      GWikiElementInfo ei = wikiContext.getWikiWeb().findElementInfo(pageId);
      if (ei == null) {
        return error("usemacro; Cannot find pageId: " + pageId);
      }
      ctx.getMacroFactories().put(localName, new GWikiScriptMacroFactory(ei));
    } else {
      return error("usemacro; either macroClass or pageId has to be defined");
    }
    List<GWikiFragment> l = new ArrayList<GWikiFragment>();
    l.add(macroFrag);
    return l;
  }

  @Override
  protected GWikiAuthorizationRights requiredRight()
  {
    return GWikiAuthorizationRights.GWIKI_DEVELOPER;
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
  public int getRenderModes()
  {
    return 0;
  }

  public String getLocalName()
  {
    return localName;
  }

  public void setLocalName(String localName)
  {
    this.localName = localName;
  }

  public String getPageId()
  {
    return pageId;
  }

  public void setPageId(String pageId)
  {
    this.pageId = pageId;
  }

  public String getMacroClass()
  {
    return macroClass;
  }

  public void setMacroClass(String macroClass)
  {
    this.macroClass = macroClass;
  }

}
