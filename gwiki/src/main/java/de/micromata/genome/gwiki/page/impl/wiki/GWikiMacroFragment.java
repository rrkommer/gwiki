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

package de.micromata.genome.gwiki.page.impl.wiki;

import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

import de.micromata.genome.gwiki.model.AuthorizationFailedException;
import de.micromata.genome.gwiki.model.logging.GWikiLog;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.RenderModes;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragment;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentBase;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentChildContainer;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiFragmentVisitor;
import de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiNestableFragment;
import de.micromata.genome.util.types.Pair;

/**
 * A wiki fragment containing a macro.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiMacroFragment extends GWikiFragmentBase implements GWikiNestableFragment
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
  }

  public void addChild(GWikiFragment child)
  {
    GWikiFragmentChildContainer frag = attrs.getChildFragment();
    if (frag == null) {
      attrs.setChildFragment(new GWikiFragmentChildContainer());
    }
    attrs.getChildFragment().addChild(child);
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
    if (macro instanceof GWikiMacroSourceable) {
      ((GWikiMacroSourceable) macro).toSource(this, sb);
    } else {
      getMacroSource(sb);
    }

  }

  public void getMacroSource(StringBuilder sb)
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

  private static String esc(String text)
  {
    return StringEscapeUtils.escapeXml(text);
  }

  @Override
  public boolean render(GWikiContext ctx)
  {
    if (RenderModes.ForRichTextEdit.isSet(ctx.getRenderMode()) == true && (macro instanceof GWikiMacroRte) == false) {
      //      if ((macro instanceof GWikiMacroRte) == false) {
      StringBuilder sbsourehead = new StringBuilder();
      attrs.toHeadContent(sbsourehead);
      if (macro.getMacroInfo() == null) {
        macro.getMacroInfo();
      }
      Pair<String, String> templ = macro.getMacroInfo().getRteTemplate(sbsourehead.toString());
      ctx.append(templ.getFirst());
      if (macro.evalBody() == true) {
        if (attrs.getChildFragment() != null && attrs.getChildFragment().getChilds().size() > 0) {
          attrs.getChildFragment().render(ctx);
        }
      } else {
        ctx.append(StringEscapeUtils.escapeHtml(attrs.getBody()));
      }
      ctx.append(templ.getSecond());
      return true;
    }
    try {
      return ((GWikiRuntimeMacro) macro).render(attrs, ctx);
    } catch (Exception ex) {
      GWikiLog.warn("Failed to render macro: " + attrs.toString() + ": " + ex.getMessage(), ex);
      ctx.append("<span color=\"red\">");
      ctx.append(ctx.getTranslated("gwiki.macro.fragment.error"));
      ctx.append(" ", attrs.toString(), "</span>");
      return true;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiNestableFragment#addChilds(de.micromata.genome.gwiki.page.
   * impl.wiki.fragment .GWikiFragment)
   */
  @Override
  public void addChilds(GWikiFragment child)
  {
    getChilds().add(child);

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiNestableFragment#renderChilds(de.micromata.genome.gwiki.page
   * .GWikiContext)
   */
  @Override
  public void renderChilds(GWikiContext ctx)
  {
    for (GWikiFragment frag : getChilds()) {
      frag.render(ctx);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.micromata.genome.gwiki.page.impl.wiki.fragment.GWikiNestableFragment#replaceChilds(de.micromata.genome.gwiki.
   * page.impl.wiki.fragment .GWikiFragment, java.util.List)
   */
  @Override
  public boolean replaceChilds(GWikiFragment search, List<GWikiFragment> replace)
  {
    List<GWikiFragment> lchilds = getChilds();
    int idx = lchilds.indexOf(search);
    if (idx == -1) {
      return false;
    }
    lchilds.remove(idx);
    for (GWikiFragment ins : replace) {
      lchilds.add(idx++, ins);
    }
    return true;
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

  @Override
  public List<GWikiFragment> getChilds()
  {
    if (attrs.getChildFragment() == null) {
      attrs.setChildFragment(new GWikiFragmentChildContainer());
    }
    return attrs.getChildFragment().getChilds();
  }

  @Override
  public void prepareHeader(GWikiContext ctx)
  {
    if (macro instanceof GWikiWithHeaderPrepare) {
      ((GWikiWithHeaderPrepare) macro).prepareHeader(ctx, attrs);
    }
  }

  @Override
  public boolean requirePrepareHeader(GWikiContext ctx)
  {
    return macro instanceof GWikiWithHeaderPrepare;
  }

  @Override
  public void iterate(GWikiFragmentVisitor visitor)
  {
    visitor.begin(this);
    if (attrs != null && attrs.getChildFragment() != null) {
      attrs.getChildFragment().iterate(visitor);
    }
    visitor.end(this);
  }

}
