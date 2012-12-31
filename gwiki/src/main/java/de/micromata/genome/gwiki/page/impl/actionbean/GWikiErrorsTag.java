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

package de.micromata.genome.gwiki.page.impl.actionbean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.util.matcher.BooleanListRulesFactory;
import de.micromata.genome.util.matcher.EveryMatcher;
import de.micromata.genome.util.matcher.Matcher;

public class GWikiErrorsTag extends TagSupport
{
  /**
   * 
   */
  private static final long serialVersionUID = 7488553928774176060L;

  public static final String ERRORS_TAG_REQUEST_ATTRIBUTE = "de.micromata.genome.gwiki.page.impl.actionbean.ActionMessages";

  /**
   * Matcher pattern for field names.
   */
  private String pattern;

  /**
   * TODO currently not implemented.
   */
  private boolean escapeXml = true;

  /**
   * The default locale on our server.
   * 
   * @deprecated Use Locale.getDefault() directly.
   */
  @Deprecated
  protected static Locale defaultLocale = Locale.getDefault();

  /**
   * The message resource key for errors header.
   */
  protected String header = null;

  /**
   * The message resource key for errors footer.
   */
  protected String footer = null;

  /**
   * The message resource key for errors prefix.
   */
  protected String prefix = null;

  /**
   * The message resource key for errors suffix.
   */
  protected String suffix = null;

  // ------------------------------------------------------- Public Methods

  protected void initHeaderFooter()
  {
    if (header == null) {
      header = "<ul class='error'>";
    }
    if (footer == null) {
      footer = "</ul>";
    }
    if (prefix == null) {
      prefix = "<li>";
    }
    if (suffix == null) {
      suffix = "</li>";
    }
  }

  /**
   * Render the specified error messages if there are any.
   * 
   * @exception JspException if a JSP exception has occurred
   */
  @Override
  public int doStartTag() throws JspException
  {
    ActionMessages am = (ActionMessages) pageContext.getRequest().getAttribute(ERRORS_TAG_REQUEST_ATTRIBUTE);
    if (am == null || am.isEmpty() == true) {
      return EVAL_BODY_INCLUDE;
    }
    initHeaderFooter();
    StringBuilder sb = new StringBuilder();
    sb.append(this.header);
    Locale loc = Locale.getDefault();
    Matcher<String> m = new EveryMatcher<String>();
    if (StringUtils.isNotBlank(pattern) == true) {
      m = new BooleanListRulesFactory<String>().createMatcher(pattern);
    }
    for (Map.Entry<String, List<ActionMessage>> me : am.entrySet()) {
      if (m.match(me.getKey()) == false) {
        continue;
      }
      for (ActionMessage amm : me.getValue()) {
        if (amm instanceof SimpleActionMessage) {
          sb.append(this.prefix).append(StringEscapeUtils.escapeHtml(amm.getMessage(loc))).append(this.suffix);
        } else {
          sb.append(this.prefix).append(StringEscapeUtils.escapeHtml(amm.getMessage(loc))).append(this.suffix);
        }
      }
    }
    sb.append(footer);
    try {
      pageContext.getOut().write(sb.toString());
    } catch (IOException ex) {
      throw new JspException(ex);
    }
    return EVAL_BODY_INCLUDE;

  }

  /**
   * Verwendet einen StringTokenizer und liefert das Ergebnis als Liste
   */
  public static List<String> parseStringTokens(String text, String delimiter, boolean returnDelimiter)
  {
    List<String> result = new ArrayList<String>();
    StringTokenizer st = new StringTokenizer(text, delimiter, returnDelimiter);
    while (st.hasMoreTokens() == true) {
      result.add(st.nextToken());
    }
    return result;
  }

  /**
   * Release any acquired resources.
   */
  @Override
  public void release()
  {
    super.release();
    // bundle = Globals.MESSAGES_KEY;
    // locale = Globals.LOCALE_KEY;
    // name = Globals.ERROR_KEY;
    pattern = null;
    header = null;
    footer = null;
    prefix = null;
    suffix = null;
    escapeXml = true;
  }

  public void setPattern(String pattern)
  {
    this.pattern = pattern;
  }

  public void setEscapeXml(boolean escapeXml)
  {
    this.escapeXml = escapeXml;
  }

  public String getHeader()
  {
    return header == null ? "errors.header" : header;
  }

  public void setHeader(String header)
  {
    this.header = header;
  }

  public String getFooter()
  {
    return footer == null ? "errors.footer" : footer;
  }

  public void setFooter(String footer)
  {
    this.footer = footer;
  }

  public String getPrefix()
  {
    return prefix == null ? "errors.prefix" : prefix;
  }

  public void setPrefix(String prefix)
  {
    this.prefix = prefix;
  }

  public String getSuffix()
  {
    return suffix == null ? "errors.suffix" : suffix;
  }

  public void setSuffix(String suffix)
  {
    this.suffix = suffix;
  }

  public boolean isEscapeXml()
  {
    return escapeXml;
  }

  public String getPattern()
  {
    return pattern;
  }
}
