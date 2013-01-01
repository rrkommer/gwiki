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
package de.micromata.genome.gwiki.web.tags;

import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.jsp.JspException;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections15.iterators.EnumerationIterator;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiHtmlOptionsCollectionTag extends GWikiBaseTag
{

  private static final long serialVersionUID = -1732167133644150919L;

  @TagProperty
  private boolean escapeHtml;

  @TagProperty
  private String property;

  @TagProperty
  private String label;

  @TagProperty
  private String value;

  // CSS Style Support
  @ElementProperty
  @TagProperty
  private String style = null;

  @ElementProperty(name = "class")
  @TagProperty
  private String styleClass = null;

  @ElementProperty(name = "id")
  @TagProperty
  private String styleId = null;

  @SuppressWarnings({ "unchecked", "rawtypes"})
  protected Iterator< ? > getIterator(Object collection) throws JspException
  {
    Class< ? > clcls = collection.getClass();
    if (clcls.isArray()) {
      collection = Arrays.asList((Object[]) collection);
    }

    if (collection instanceof Collection) {
      return (((Collection< ? >) collection).iterator());

    } else if (collection instanceof Iterator) {
      return ((Iterator< ? >) collection);

    } else if (collection instanceof Map) {
      return (((Map< ? , ? >) collection).entrySet().iterator());

    } else if (collection instanceof Enumeration) {
      return new EnumerationIterator((Enumeration< ? >) collection);

    } else {
      throw new JspException("form." + property + " does not return a valid collection");
    }
  }

  public int doStartTag() throws JspException
  {
    GWikiHtmlSelectTag selTag = (GWikiHtmlSelectTag) pageContext.getAttribute(GWikiHtmlSelectTag.GWikiHtmlSelectTag_KEY);
    if (selTag == null) {
      throw new JspException("cann use optionsCollection only inside a select tag");
    }
    Object collection = GWikiTagRenderUtils.readFormValue(pageContext, property);
    if (collection == null) {
      throw new JspException("Jsp: form." + property + " is a null value");
    }

    Iterator< ? > iter = getIterator(collection);

    StringBuilder sb = new StringBuilder();

    while (iter.hasNext()) {

      Object bean = iter.next();
      Object beanLabel = null;
      Object beanValue = null;

      // Get the label for this option
      try {
        beanLabel = PropertyUtils.getProperty(bean, label);
        if (beanLabel == null) {
          beanLabel = "";
        }
      } catch (Exception ex) {
        throw new JspException("Cannot access label ' " + label + " from collection form." + property + ": " + ex.getMessage(), ex);
      }

      // Get the value for this option
      try {
        beanValue = PropertyUtils.getProperty(bean, value);
        if (beanValue == null) {
          beanValue = "";
        }
      } catch (Exception ex) {
        throw new JspException("Cannot access value ' " + value + " from collection form." + property + ": " + ex.getMessage(), ex);
      }
      String stringLabel = beanLabel.toString();
      String stringValue = beanValue.toString();
      sb.append("<option value=\"").append(escapeHtml ? StringEscapeUtils.escapeHtml(stringValue) : stringValue).append("\"");
      if (selTag.hasValue(stringValue) == true) {
        sb.append(" selected=\"selected\"");
      }
      sb.append(" ");
      GWikiTagRenderUtils.renderTagAttributes(this, sb);
      sb.append(">").append(escapeHtml ? StringEscapeUtils.escapeHtml(stringLabel) : stringLabel);
      sb.append("</option>");
    }
    GWikiTagRenderUtils.write(pageContext, sb.toString());
    return SKIP_BODY;
  }

  public String getStyle()
  {
    return style;
  }

  public void setStyle(String style)
  {
    this.style = style;
  }

  public String getStyleClass()
  {
    return styleClass;
  }

  public void setStyleClass(String styleClass)
  {
    this.styleClass = styleClass;
  }

  public String getStyleId()
  {
    return styleId;
  }

  public void setStyleId(String styleId)
  {
    this.styleId = styleId;
  }

  public String getProperty()
  {
    return property;
  }

  public void setProperty(String property)
  {
    this.property = property;
  }

  @Override
  public void prepare()
  {
    // TODO Auto-generated method stub

  }

  public String getLabel()
  {
    return label;
  }

  public void setLabel(String label)
  {
    this.label = label;
  }

  public String getValue()
  {
    return value;
  }

  public void setValue(String value)
  {
    this.value = value;
  }

  public boolean isEscapeHtml()
  {
    return escapeHtml;
  }

  public void setEscapeHtml(boolean escapeHtml)
  {
    this.escapeHtml = escapeHtml;
  }

}
