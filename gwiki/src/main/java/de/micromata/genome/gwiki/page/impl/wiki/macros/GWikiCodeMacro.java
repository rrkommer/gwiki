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
import de.micromata.genome.gwiki.page.impl.wiki.MacroInfo;
import de.micromata.genome.gwiki.page.impl.wiki.MacroInfoParam;
import de.micromata.genome.gwiki.utils.html.Html2WikiTransformInfo;

/**
 * GWiki macro code.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
@MacroInfo(info = "The macro code wrap a text as source code.",
    params = { @MacroInfoParam(name = "title", info = "Title for the code"),
        @MacroInfoParam(name = "lang", defaultValue = "java", info = "Code language",
            enumValues = { "wiki", "markup", "groovy", "java", "xml", "css", "javascript", "json", "c", "cpp",
                "objectivec", "pascal", "perl", "php", "python", "scala", "sql", "tcl" }) },
    renderFlags = { GWikiMacroRenderFlags.NoWrapWithP })
public class GWikiCodeMacro extends GWikiMacroBean implements GWikiBodyMacro, GWikiRuntimeMacro
{

  private static final long serialVersionUID = -5140863862389680264L;

  private String defaultValue;

  private String title = "";

  private String lang = "java";

  //  private static Html2WikiTransformInfo transformInfo = new Html2WikiTransformInfo("pre",
  //      CommonMatchers.and(SaxElementMatchers.nameMatcher("pre"),
  //          SaxElementMatchers.attribute("class", StringMatchers.containsString("wikiCode"))),
  //      "code", GWikiCodeMacro.class);

  public static GWikiMacroFactory getFactory()
  {
    return new GWikiMacroClassFactory(GWikiCodeMacro.class);
  }

  public GWikiCodeMacro()
  {
    setRenderModesFromAnnot();
  }

  @Override
  public boolean renderImpl(GWikiContext ctx, MacroAttributes attrs)
  {
    if (RenderModes.ForIndex.isSet(ctx.getRenderMode()) == true) {
      ctx.append(StringEscapeUtils.escapeHtml(attrs.getBody()));
      return true;
    }
    // following will not be rendered.
    if (RenderModes.ForRichTextEdit.isSet(ctx.getRenderMode()) == true) {
      // TODO attributes rendering
      String body = attrs.getBody();

      body = StringEscapeUtils.escapeHtml(body);

      ctx.append("<pre class=\"wikiCode\"");
      Html2WikiTransformInfo.renderMacroArgs(ctx, attrs);
      ctx.append(">").append(body).append("</pre>");
      return true;
    }
    String body = attrs.getBody();
    body = StringUtils.trim(body);
    boolean forText = RenderModes.ForText.isSet(ctx.getRenderMode());
    String clang = determineLang(body);
    ctx.append("<pre><code");

    if (StringUtils.isNotBlank(clang) == true) {
      ctx.append(" class='language-" + clang + "'");
    }
    if (forText == false) {
      body = StringEscapeUtils.escapeXml(body);
    }
    ctx.append(">").append(body).append("</code></pre>");

    //    ctx.append("<div class=\"preformatted panel\" style=\"border-width: 1px;\">", //
    //        "<div class=\"preformattedContent panelContent\">\n", //
    //        preview ? attrs.getBody() : colorize(lang, body), //
    //        "</div></div>");
    return true;
  }

  private String determineLang(String body)
  {
    if (StringUtils.isNotBlank(lang) == true) {
      if (lang.equals("wiki") == true) {
        lang = "textile";
      }
      return lang;
    }
    if (body.trim().startsWith("<") == true) {
      return "xml";
    }
    return lang;
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

  //  @Override
  //  public Html2WikiTransformInfo getTransformInfo()
  //  {
  //    return transformInfo;
  //  }

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
