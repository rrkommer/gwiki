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
package de.micromata.genome.gwiki.page.impl.wiki.blueprint;

import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiAuthorizationRights;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFragment;
import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroSourceable;
import de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes;

/**
 * TODO validate form.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */

public class GWikiFormInputMacro extends GWikiMacroBean implements GWikiMacroSourceable
{

  private static final long serialVersionUID = -5170233131716774400L;

  public static final String EVAL_FORM = GWikiFormInputMacro.class.getName() + ".EVAL_FORM";

  private String name = null;

  /**
   * allowed is text, wiki and html.
   */
  private String contentType = "wiki";

  private String inputType;

  private String value = "";

  private String tooltip = null;

  protected void renderAttr(GWikiContext ctx, String name, String value)
  {
    ctx.append(" " + name + "=\"").append(StringEscapeUtils.escapeHtml(value)).append("\"");
  }

  protected void renderStandardAttr(String attr, GWikiContext ctx, MacroAttributes attrs)
  {
    if (StringUtils.isNotBlank(attrs.getArgs().getStringValue(attr)) == true) {
      ctx.append(" " + attr + "=\"").append(StringEscapeUtils.escapeHtml(attrs.getArgs().getStringValue(attr)))
          .append("\"");
    }
  }

  protected void renderStandardAttr(GWikiContext ctx, MacroAttributes attrs)
  {
    renderStandardAttr("class", ctx, attrs);
    renderStandardAttr("style", ctx, attrs);
    renderStandardAttr("accesskey", ctx, attrs);
    renderStandardAttr("disabled", ctx, attrs);
    renderStandardAttr("tabindex", ctx, attrs);

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroBean#renderImpl(de.micromata.genome.gwiki.page.GWikiContext,
   * de.micromata.genome.gwiki.page.impl.wiki.MacroAttributes)
   */
  @Override
  public boolean renderImpl(GWikiContext ctx, MacroAttributes attrs)
  {
    if (StringUtils.isEmpty(inputType) == true) {
      inputType = attrs.getDefaultValue();
    }
    if (StringUtils.equals(inputType, "text") == true) {
      ctx.append("<input type=\"text\" name=\"" + name + "\" value=\"" + StringEscapeUtils.escapeHtml(value) + "\"");
      renderStandardAttr(ctx, attrs);
      renderStandardAttr("size", ctx, attrs);
      renderStandardAttr("maxlength", ctx, attrs);
      ctx.append("/>");
    } else if (StringUtils.equals(inputType, "textarea") == true) {
      ctx.append("<textarea name=\"" + name + "\"");
      renderStandardAttr(ctx, attrs);
      renderStandardAttr("cols", ctx, attrs);
      renderStandardAttr("rows", ctx, attrs);
      renderStandardAttr("wrap", ctx, attrs);
      String v = value;
      ctx.append(">" + StringEscapeUtils.escapeHtml(v) + "</textarea>");
    } else if (StringUtils.equals(inputType, "checkbox") == true) {
      ctx.append("<input type=\"checkbox\" name=\"" + name + "\"");
      renderStandardAttr(ctx, attrs);
      if (StringUtils.equals(attrs.getArgs().getStringValue("checked"), "true") == true) {
        renderAttr(ctx, "checked", "checked");
      }
      ctx.append("/>");
    } else if (StringUtils.equals(inputType, "radio") == true) {
      ctx.append("<input type=\"radio\" name=\"" + name + "\"");
      renderStandardAttr(ctx, attrs);
      if (StringUtils.equals(attrs.getArgs().getStringValue("checked"), "true") == true) {
        renderAttr(ctx, "checked", "checked");
      }
      ctx.append("/>");
    } else if (StringUtils.equals(inputType, "select") == true) {
      ctx.append("<select name=\"" + name + "\"");
      renderStandardAttr(ctx, attrs);

      List<String> values = attrs.getArgs().getStringList("values");

      for (String v : values) {
        v = StringEscapeUtils.escapeHtml(v);
        String st = "";
        if (StringUtils.equals(v, value) == true) {
          st = " selected=\"selected\"";
        }
        ctx.append("<option " + st + "value=\"").append(v).append("\">").append(v).append("</option>");
      }
      ctx.append(">");

      ctx.append("</select>");
    } else if (StringUtils.equals(inputType, "label") == true) {
      ctx.append(StringEscapeUtils.escapeHtml(value));
    } else {
      // TODO general method to report error for macros.
      ctx.append("Unkown inputType");
    }
    return true;
  }

  protected boolean getFormSource(StringBuilder sb)
  {

    if (GWikiContext.getCurrent() == null) {
      return false;
    }
    if (GWikiContext.getCurrent().getRequestAttribute(EVAL_FORM) != Boolean.TRUE) {
      return false;
    }
    String t = StringUtils.defaultString(inputType);
    if (t.equals("label") == true || t.equals("radio") == true || t.equals("checkbox") == true) {
      return true;
    }
    String v = GWikiContext.getCurrent().getRequestParameter(name);
    if (v == null) {
      return false;
    }
    sb.append(v);
    return true;
  }

  public boolean validate(GWikiContext wikiContext)
  {
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroSourceable#toSource(de.micromata.genome.gwiki.page.impl.wiki.
   * GWikiMacroFragment, java.lang.StringBuilder)
   */
  @Override
  public void toSource(GWikiMacroFragment macroFragment, StringBuilder sb)
  {
    if (getFormSource(sb) == false) {
      macroFragment.getMacroSource(sb);
    }
  }

  @Override
  protected GWikiAuthorizationRights requiredRight()
  {
    return GWikiAuthorizationRights.GWIKI_EDITHTML;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getContentType()
  {
    return contentType;
  }

  public void setContentType(String contentType)
  {
    this.contentType = contentType;
  }

  public String getInputType()
  {
    return inputType;
  }

  public void setInputType(String inputType)
  {
    this.inputType = inputType;
  }

  public String getValue()
  {
    return value;
  }

  public void setValue(String defaultValue)
  {
    this.value = defaultValue;
  }

  public String getTooltip()
  {
    return tooltip;
  }

  public void setTooltip(String tooltip)
  {
    this.tooltip = tooltip;
  }

}
