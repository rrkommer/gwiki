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

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public abstract class GWikiBaseHtmlTag extends GWikiBaseTag
{

  private static final long serialVersionUID = -5795082670921841554L;
  
  @ElementProperty
  @TagProperty
  protected String id = null;

  @ElementProperty
  @TagProperty
  protected String accesskey = null;

  @ElementProperty
  @TagProperty
  protected String tabindex = null;

  protected boolean indexed = false;

  @ElementProperty
  @TagProperty
  private String onclick = null;

  @ElementProperty
  @TagProperty
  private String ondblclick = null;

  @ElementProperty
  @TagProperty
  private String onmouseover = null;

  @ElementProperty
  @TagProperty
  private String onmouseout = null;

  @ElementProperty
  @TagProperty
  private String onmousemove = null;

  @ElementProperty
  @TagProperty
  private String onmousedown = null;

  @ElementProperty
  @TagProperty
  private String onmouseup = null;

  @ElementProperty
  @TagProperty
  private String onkeydown = null;

  @ElementProperty
  @TagProperty
  private String onkeyup = null;

  @ElementProperty
  @TagProperty
  private String onkeypress = null;

  @ElementProperty
  @TagProperty
  private String onselect = null;

  @ElementProperty
  @TagProperty
  private String onchange = null;

  // Focus Events and States
  @ElementProperty
  @TagProperty
  private String onblur = null;

  @ElementProperty
  @TagProperty
  private String onfocus = null;

  @ElementProperty(ignoreValue = "false")
  @TagProperty
  private boolean disabled = false;

  @ElementProperty(ignoreValue = "false")
  @TagProperty
  private boolean readonly = false;

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

  @TagProperty
  private String errorKey = null;

  // Other Common Attributes
  @ElementProperty
  @TagProperty
  private String alt = null;

  @ElementProperty
  @TagProperty
  private String altKey = null;

  public String getAccesskey()
  {
    return accesskey;
  }

  public void setAccesskey(String accesskey)
  {
    this.accesskey = accesskey;
  }

  public String getTabindex()
  {
    return tabindex;
  }

  public void setTabindex(String tabindex)
  {
    this.tabindex = tabindex;
  }

  public boolean isIndexed()
  {
    return indexed;
  }

  public void setIndexed(boolean indexed)
  {
    this.indexed = indexed;
  }

  public String getOnclick()
  {
    return onclick;
  }

  public void setOnclick(String onclick)
  {
    this.onclick = onclick;
  }

  public String getOndblclick()
  {
    return ondblclick;
  }

  public void setOndblclick(String ondblclick)
  {
    this.ondblclick = ondblclick;
  }

  public String getOnmouseover()
  {
    return onmouseover;
  }

  public void setOnmouseover(String onmouseover)
  {
    this.onmouseover = onmouseover;
  }

  public String getOnmouseout()
  {
    return onmouseout;
  }

  public void setOnmouseout(String onmouseout)
  {
    this.onmouseout = onmouseout;
  }

  public String getOnmousemove()
  {
    return onmousemove;
  }

  public void setOnmousemove(String onmousemove)
  {
    this.onmousemove = onmousemove;
  }

  public String getOnmousedown()
  {
    return onmousedown;
  }

  public void setOnmousedown(String onmousedown)
  {
    this.onmousedown = onmousedown;
  }

  public String getOnmouseup()
  {
    return onmouseup;
  }

  public void setOnmouseup(String onmouseup)
  {
    this.onmouseup = onmouseup;
  }

  public String getOnkeydown()
  {
    return onkeydown;
  }

  public void setOnkeydown(String onkeydown)
  {
    this.onkeydown = onkeydown;
  }

  public String getOnkeyup()
  {
    return onkeyup;
  }

  public void setOnkeyup(String onkeyup)
  {
    this.onkeyup = onkeyup;
  }

  public String getOnkeypress()
  {
    return onkeypress;
  }

  public void setOnkeypress(String onkeypress)
  {
    this.onkeypress = onkeypress;
  }

  public String getOnselect()
  {
    return onselect;
  }

  public void setOnselect(String onselect)
  {
    this.onselect = onselect;
  }

  public String getOnchange()
  {
    return onchange;
  }

  public void setOnchange(String onchange)
  {
    this.onchange = onchange;
  }

  public String getOnblur()
  {
    return onblur;
  }

  public void setOnblur(String onblur)
  {
    this.onblur = onblur;
  }

  public String getOnfocus()
  {
    return onfocus;
  }

  public void setOnfocus(String onfocus)
  {
    this.onfocus = onfocus;
  }

  public boolean isDisabled()
  {
    return disabled;
  }

  public void setDisabled(boolean disabled)
  {
    this.disabled = disabled;
  }

  public boolean isReadonly()
  {
    return readonly;
  }

  public void setReadonly(boolean readonly)
  {
    this.readonly = readonly;
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

  public String getErrorKey()
  {
    return errorKey;
  }

  public void setErrorKey(String errorKey)
  {
    this.errorKey = errorKey;
  }

  public String getAlt()
  {
    return alt;
  }

  public void setAlt(String alt)
  {
    this.alt = alt;
  }

  public String getAltKey()
  {
    return altKey;
  }

  public void setAltKey(String altKey)
  {
    this.altKey = altKey;
  }
  
  /**
   * @return the id
   */
  public String getId()
  {
    return id;
  }

  /**
   * @param id the id to set
   */
  public void setId(String id)
  {
    this.id = id;
  }

}
