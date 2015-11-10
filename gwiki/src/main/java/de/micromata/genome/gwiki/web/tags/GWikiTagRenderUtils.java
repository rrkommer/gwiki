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
package de.micromata.genome.gwiki.web.tags;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.util.bean.PrivateBeanUtils;
import de.micromata.genome.util.types.Pair;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiTagRenderUtils
{
  public static final String FORM_ATTR_NAME = "form";

  public static String readFormStringValue(PageContext pageContext, String name)
  {
    Object o = readFormValue(pageContext, name);
    if (o == null) {
      return null;
    }
    return ObjectUtils.toString(o);
  }

  public static Object readFormValue(PageContext pageContext, String name)
  {
    Object bean = pageContext.getRequest().getAttribute(FORM_ATTR_NAME);
    if (bean == null) {
      throw new RuntimeException("No form under '" + FORM_ATTR_NAME + "' defined in request attribute");
    }
    try {
      Object o = PropertyUtils.getProperty(bean, name);
      return o;
    } catch (Exception ex) {
      throw new RuntimeException("Cannot read property '" + FORM_ATTR_NAME + "'." + name + ": " + ex.getMessage(), ex);
    }
  }

  public static void renderAttribute(Object tag, Pair<Field, ? extends Annotation> p, StringBuilder sb)
  {
    Object value = PrivateBeanUtils.readField(tag, p.getFirst());
    if (value == null) {
      return;
    }

    if (sb.length() > 0) {
      sb.append(" ");
    }
    ElementProperty an = (ElementProperty) p.getSecond();
    String svalue = value.toString();
    if (an.ignoreValue().length() > 0 && an.ignoreValue().equals(svalue) == true) {
      return;
    }
    String name = an.name();
    if (StringUtils.isEmpty(name) == true) {
      name = p.getFirst().getName();
    }
    sb.append(name).append("=").append("\"").append(StringEscapeUtils.escapeXml(ObjectUtils.toString(value))).append("\"");
  }

  public static void renderTagAttributes(Object tag, StringBuilder sb)
  {
    List<Pair<Field, ? extends Annotation>> fields = PrivateBeanUtils.findFieldsWithAnnotation(tag, ElementProperty.class);
    for (Pair<Field, ? extends Annotation> p : fields) {
      renderAttribute(tag, p, sb);
    }
  }

  public static void renderSimpleHtmlTag(Object tag, String tagName, StringBuilder sb)
  {
    sb.append("<").append(tagName);
    renderTagAttributes(tag, sb);
    sb.append("/>");
  }

  public static void renderOpenHtmlTag(Object tag, String tagName, StringBuilder sb)
  {
    sb.append("<").append(tagName);
    renderTagAttributes(tag, sb);
    sb.append(">");
  }

  public static void write(PageContext pageContext, String data) throws JspException
  {
    try {
      pageContext.getOut().write(data);
    } catch (IOException e) {
      throw new JspException("Failure writing tag", e);
    }
  }
}
