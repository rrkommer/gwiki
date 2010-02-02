/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   28.12.2009
// Copyright Micromata 28.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki.macros;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.AuthorizationFailedException;
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
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiParserContext;
import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiTokens;
import de.micromata.genome.gwiki.utils.ClassUtils;

/**
 * Macro imports another macro for usage.
 * 
 * @author roger@micromata.de
 * 
 */
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

  public Collection<GWikiFragment> getFragments(GWikiMacroFragment macroFrag, GWikiWikiTokens tks, GWikiWikiParserContext ctx)
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

  public void ensureRight(MacroAttributes attrs, GWikiContext ctx) throws AuthorizationFailedException
  {
  }

  public boolean evalBody()
  {
    return false;
  }

  public boolean hasBody()
  {
    return false;
  }

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
