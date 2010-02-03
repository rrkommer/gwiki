/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   28.10.2009
// Copyright Micromata 28.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.wiki;

import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

import de.micromata.genome.gwiki.model.AuthorizationFailedException;
import de.micromata.genome.gwiki.model.GWikiLog;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.RenderModes;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentChildContainer;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentChildsBase;

/**
 * A wiki fragment containing a macro.
 * 
 * @author roger@micromata.de
 * 
 */
public class GWikiMacroFragment extends GWikiFragmentChildsBase
{

  private static final long serialVersionUID = 3598133547184060289L;

  private GWikiMacro macro;

  private MacroAttributes attrs;

  public GWikiMacroFragment(GWikiMacro macro, MacroAttributes attrs)
  {
    this.macro = macro;
    this.attrs = attrs;
  }

  @Override
  public void ensureRight(GWikiContext ctx) throws AuthorizationFailedException
  {
    macro.ensureRight(attrs, ctx);
    super.ensureRight(ctx);
  }

  @Override
  public void addChilds(List<GWikiFragment> childs)
  {
    GWikiFragmentChildContainer frag = attrs.getChildFragment();
    if (frag == null) {
      attrs.setChildFragment(new GWikiFragmentChildContainer(childs));
    } else {
      attrs.getChildFragment().addChilds(childs);
    }
    super.addChilds(childs);
  }

  @Override
  public void addChild(GWikiFragment child)
  {
    GWikiFragmentChildContainer frag = attrs.getChildFragment();
    if (frag == null) {
      attrs.setChildFragment(new GWikiFragmentChildContainer());
    }
    attrs.getChildFragment().addChild(child);
    super.addChild(child);
  }

  public void renderSourceHead(StringBuilder sb)
  {
    attrs.toString(sb);
  }

  public void renderSourceFoot(StringBuilder sb)
  {
    sb.append("{").append(attrs.getCmd()).append("}");
  }

  @Override
  public void getSource(StringBuilder sb)
  {
    renderSourceHead(sb);
    if (macro.hasBody() == false) {
      return;
    }

    StringBuilder nsb = new StringBuilder();
    if (macro.evalBody() == true) {
      if (attrs.getChildFragment() != null && attrs.getChildFragment().getChilds().size() > 0) {
        attrs.getChildFragment().getSource(nsb);
      }
    } else {
      nsb.append(attrs.getBody());
    }
    if (GWikiMacroRenderFlags.NewLineAfterStart.isSet(macro.getRenderModes()) == true) {
      if (nsb.length() != 0 && nsb.charAt(0) != '\n') {
        sb.append("\n");
      }
    }
    sb.append(nsb.toString());
    if (GWikiMacroRenderFlags.NewLineBeforeEnd.isSet(macro.getRenderModes()) == true) {
      if (sb.charAt(sb.length() - 1) != '\n') {
        sb.append("\n");
      }
    }
    renderSourceFoot(sb);
  }

  public boolean render(GWikiContext ctx)
  {
    if (RenderModes.ForRichTextEdit.isSet(ctx.getRenderMode()) == true && (macro instanceof GWikiMacroRte) == false) {
      StringBuilder sb = new StringBuilder();
      renderSourceHead(sb);
      ctx.append(sb.toString());
      if (macro.hasBody() == false) {
        return true;
      }
      sb = new StringBuilder();
      
      if (macro.evalBody() == true) {
        if (attrs.getChildFragment() != null && attrs.getChildFragment().getChilds().size() > 0) {
          attrs.getChildFragment().render(ctx);
        } else {
          sb.append(attrs.getBody());
        }
      } else {
        sb.append(StringEscapeUtils.escapeHtml(attrs.getBody()));
      }
      renderSourceFoot(sb);
      ctx.append(sb.toString());
      return true;
    }
    try {
      return ((GWikiRuntimeMacro) macro).render(attrs, ctx);
    } catch (Exception ex) {
      GWikiLog.warn("Failed to render macro: " + attrs.toString() + ": " + ex.getMessage(), ex);
      ctx.append("<span color=\"red\">Failed to render Macro: ", attrs.toString(), "</span>");
      return true;
    }
  }

  public MacroAttributes getAttrs()
  {
    return attrs;
  }

  public void setAttrs(MacroAttributes attrs)
  {
    this.attrs = attrs;
  }

  public GWikiMacro getMacro()
  {
    return macro;
  }

  public void setMacro(GWikiMacro macro)
  {
    this.macro = macro;
  }

}
