/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   02.11.2009
// Copyright Micromata 02.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki.macros;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.tagext.BodyContent;

import de.micromata.genome.gwiki.model.AuthorizationFailedException;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiPropKeys;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.RenderModes;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiRuntimeMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiScriptMacroFactory;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;

/**
 * A macro implemented by a GWiki element script.
 * 
 * @author roger
 * 
 */
public class GWikiScriptMacro implements GWikiRuntimeMacro, GWikiPropKeys
{

  private static final long serialVersionUID = -5695121521370458991L;

  private String name;

  private String execPageId;

  private boolean withBody;

  private boolean evalBody;

  private String requestPrefix;

  private GWikiScriptMacroFactory macroFactory;

  private int renderModes = 0;

  public GWikiScriptMacro(GWikiScriptMacroFactory macroFactory, GWikiElement executer)
  {
    this.macroFactory = macroFactory;
    this.execPageId = executer.getElementInfo().getId();
    this.name = GWikiContext.getNamePartFromPageId(execPageId);
    this.withBody = executer.getElementInfo().getProps().getBooleanValue(GWikiPropKeys.MACRO_WITH_BODY);
    this.evalBody = executer.getElementInfo().getProps().getBooleanValue(GWikiPropKeys.MACRO_EVAL_BODY);
    this.renderModes = executer.getElementInfo().getProps().getIntValue(GWikiPropKeys.MACRO_RENDER_MODES, 0);
  }

  public void ensureRight(MacroAttributes attrs, GWikiContext ctx) throws AuthorizationFailedException
  {
    // nothing
  }

  public boolean render(MacroAttributes attrs, GWikiContext ctx)
  {
    GWikiElement executer = ctx.getWikiWeb().getElement(execPageId);
    boolean preview = RenderModes.ForText.isSet(ctx.getRenderMode());
    if (preview == true && executer.getElementInfo().getProps().getBooleanValue(MACRO_WITH_PREVIEW) == false) {
      if (attrs.getChildFragment() != null) {
        attrs.getChildFragment().render(ctx);
      }
      return true;
    }
    BodyContent bc = ctx.getCreatePageContext().pushBody();
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("GWIKI_MACRO_NAME", name);
    params.put("GWIKI_MACRO_ATTRIBUTES", attrs);
    params.put("GWIKI_MACRO_BODY", attrs.getBody());
    params.put("GWIKI_MACRO_CHILD", attrs.getChildFragment());
    params.put("GWIKI_MACRO_PREVIEW", false);
    params.put("GWIKI_MACRO_APPENDABLE", ctx);

    params.putAll(attrs.getArgs().getMap());

    Map<String, Object> sm = ctx.pushNativeParams(params);
    executer.serve(ctx);
    ctx.pushNativeParams(sm);
    ctx.getCreatePageContext().popBody();
    ctx.append(bc.getString());
    return true;
  }

  public boolean evalBody()
  {
    return evalBody;
  }

  public boolean hasBody()
  {
    return withBody;
  }

  public boolean isWithBody()
  {
    return withBody;
  }

  public void setWithBody(boolean withBody)
  {
    this.withBody = withBody;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public boolean isEvalBody()
  {
    return evalBody;
  }

  public void setEvalBody(boolean evalBody)
  {
    this.evalBody = evalBody;
  }

  public String getExecPageId()
  {
    return execPageId;
  }

  public void setExecPageId(String execPageId)
  {
    this.execPageId = execPageId;
  }

  public String getRequestPrefix()
  {
    return requestPrefix;
  }

  public void setRequestPrefix(String requestPrefix)
  {
    this.requestPrefix = requestPrefix;
  }

  public GWikiScriptMacroFactory getMacroFactory()
  {
    return macroFactory;
  }

  public void setMacroFactory(GWikiScriptMacroFactory macroFactory)
  {
    this.macroFactory = macroFactory;
  }

  public int getRenderModes()
  {
    return renderModes;
  }

  public void setRenderModes(int renderModes)
  {
    this.renderModes = renderModes;
  }

}
