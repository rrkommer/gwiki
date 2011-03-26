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

package de.micromata.genome.gwiki.page.impl.wiki.macros;

import java.io.IOException;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import com.uwyn.jhighlight.renderer.GroovyXhtmlRenderer;
import com.uwyn.jhighlight.renderer.JavaXhtmlRenderer;
import com.uwyn.jhighlight.renderer.XhtmlRenderer;
import com.uwyn.jhighlight.renderer.XmlXhtmlRenderer;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.RenderModes;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiBodyMacro;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroClassFactory;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFactory;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroRenderFlags;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiRuntimeMacro;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;

/**
 * GWiki macro code.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiCodeMacro extends GWikiMacroBean implements GWikiBodyMacro, GWikiRuntimeMacro
{

  private static final long serialVersionUID = -5140863862389680264L;

  private String defaultValue;

  private String title = "";

  private String lang = "java";

  public static GWikiMacroFactory getFactory()
  {
    return new GWikiMacroClassFactory(GWikiCodeMacro.class);
  }

  public GWikiCodeMacro()
  {
    setRenderModes(GWikiMacroRenderFlags.combine(GWikiMacroRenderFlags.NoWrapWithP, GWikiMacroRenderFlags.TrimTextContent));
  }

  @Override
  public boolean renderImpl(GWikiContext ctx, MacroAttributes attrs)
  {
    if (RenderModes.ForIndex.isSet(ctx.getRenderMode()) == true) {
      ctx.append(StringEscapeUtils.escapeHtml(attrs.getBody()));
      return true;
    }
    if (RenderModes.ForRichTextEdit.isSet(ctx.getRenderMode()) == true) {
      ctx.append("<code>").appendEscText(attrs.getBody()).append("</code>");
      return true;
    }
    boolean preview = RenderModes.ForText.isSet(ctx.getRenderMode());
    ctx.append("<div class=\"preformatted panel\" style=\"border-width: 1px;\">", //
        "<div class=\"preformattedContent panelContent\">\n", //
        preview ? attrs.getBody() : colorize(lang, attrs.getBody()), //
        "</div></div>");
    return true;
  }

  protected XhtmlRenderer getRenderer(String lang, String code)
  {
    if (StringUtils.isBlank(lang) == true) {
      if (code.trim().startsWith("<") == true) {
        lang = "xml";
      }
    }
    if (StringUtils.equals(lang, "java") == true) {
      return new JavaXhtmlRenderer();
    } else if (StringUtils.equals(lang, "groovy") == true) {
      return new GroovyXhtmlRenderer();
    } else if (StringUtils.equals(lang, "xml") == true || StringUtils.equals(lang, "html") == true) {
      return new XmlXhtmlRenderer();
    } else {
      return new JavaXhtmlRenderer();
    }

  }

  public String colorize(String lang, String code)
  {
    try {
      XhtmlRenderer renderer = getRenderer(lang, code);
      String ccode = renderer.highlight(title, code, "UTF-8", true);

      if (StringUtils.isEmpty(ccode)) {
        return "<pre>\n" + StringEscapeUtils.escapeHtml(code) + "</pre>\n";
      }
      int idx = ccode.indexOf("-->");
      if (idx != -1) {
        ccode = ccode.substring(idx + 3);
      }
      return ccode;
    } catch (IOException ex) {
      // throw new RuntimeIOException(ex);
    }
    return "<pre>\n" + StringEscapeUtils.escapeHtml(code) + "</pre>\n";
  }

  public String getDefaultValue()
  {
    return defaultValue;
  }

  public void setDefaultValue(String defaultValue)
  {
    this.defaultValue = defaultValue;
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public String getLang()
  {
    return lang;
  }

  public void setLang(String lang)
  {
    this.lang = lang;
  }

}
