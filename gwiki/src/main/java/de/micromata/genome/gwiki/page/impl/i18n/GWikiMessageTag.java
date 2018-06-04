//
// Copyright (C) 2010-2016 Roger Rene Kommer & Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package de.micromata.genome.gwiki.page.impl.i18n;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.taglibs.standard.tag.rt.fmt.MessageTag;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiMessageTag extends MessageTag
{

  private static final long serialVersionUID = -6285073054785094424L;

  public static final String GWIKI_MESSAGE_ATTR = GWikiMessageTag.class.getCanonicalName() + ".GWIKI_MESSAGE_ATTR";

  public static final String GWIKI_MESSAGE_KEYM = GWikiMessageTag.class.getCanonicalName() + ".GWIKI_MESSAGE_KEYM";

  private String tagId;

  /**
   * true, false or class name
   */
  private String deco = "true";

  @SuppressWarnings("unchecked")
  public static void addI18NDomMap(PageContext pageContext, String messageKey, String domId)
  {
    Map<String, String> m = (Map<String, String>) pageContext.getRequest().getAttribute(GWIKI_MESSAGE_KEYM);
    if (m == null) {
      m = new HashMap<String, String>();
      pageContext.getRequest().setAttribute(GWIKI_MESSAGE_KEYM, m);
    }
    m.put(messageKey, domId);
  }

  @SuppressWarnings("unchecked")
  public static String getDomId4I18N(HttpServletRequest req, String key)
  {
    Map<String, String> m = (Map<String, String>) req.getAttribute(GWIKI_MESSAGE_KEYM);
    if (m == null) {
      return null;
    }
    return m.get(key);
  }

  @SuppressWarnings("unchecked")
  public static void renderPatchDom(PageContext pageContext) throws JspException
  {
    Object l = pageContext.getAttribute(GWIKI_MESSAGE_ATTR);
    if ((l instanceof List) == false) {
      return;
    }
    List<GWikiMessageTag> tags = (ArrayList<GWikiMessageTag>) l;
    if (tags.isEmpty() == true) {
      return;
    }
    StringBuilder sb = new StringBuilder();
    sb.append("<script type=\"text/javascript\">gwikiPatchI18NElements([");
    boolean first = true;
    for (GWikiMessageTag tag : tags) {
      if (first == false) {
        sb.append(", ");
      } else {
        first = false;
      }
      sb.append("{ id: '" + tag.getTagId() + "', i18nKey: '" + tag.getKeyAttrValue() + "'}");
    }
    sb.append("]);</script>");

    try {
      pageContext.getOut().write(sb.toString());
    } catch (IOException e) {
      throw new JspException(e);
    }

  }

  @SuppressWarnings("unchecked")
  protected void addToTagList()
  {
    Object l = pageContext.getAttribute(GWIKI_MESSAGE_ATTR);
    List<GWikiMessageTag> tags = null;
    if ((l instanceof List) == false) {
      tags = new ArrayList<GWikiMessageTag>();
      pageContext.setAttribute(GWIKI_MESSAGE_ATTR, tags);
    } else {
      tags = (List<GWikiMessageTag>) l;
    }
    tags.add(this);
  }

  @Override
  public int doEndTag() throws JspException
  {
    if (StringUtils.isEmpty(tagId) == true) {
      // TODO wrapp here
      return super.doEndTag();
    }

    return super.doEndTag();
  }

  public String getKeyAttrValue()
  {
    return keyAttrValue;
  }

  public String getTagId()
  {
    return tagId;
  }

  public void setTagId(String tagId)
  {
    this.tagId = tagId;
  }

  public String getDeco()
  {
    return deco;
  }

  public void setDeco(String deco)
  {
    this.deco = deco;
  }

}
