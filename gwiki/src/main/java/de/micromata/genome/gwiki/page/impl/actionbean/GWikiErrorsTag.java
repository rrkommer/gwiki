/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   03.11.2009
// Copyright Micromata 03.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.page.impl.actionbean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringEscapeUtils;

import de.micromata.genome.util.types.Pair;

public class GWikiErrorsTag extends TagSupport
{
  public static final String ERRORS_TAG_REQUEST_ATTRIBUTE = "de.micromata.genome.gwiki.page.impl.actionbean.ActionMessages";

  /**
   * 
   */

  private String pattern;

  private boolean escapeXml = true;

  private List<Pair<Boolean, Pattern>> rules;

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
      header = "<ul>";
    }
    if (footer == null) {
      footer = "</u>";
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
  @SuppressWarnings("unchecked")
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
    for (Map.Entry<String, List<ActionMessage>> me : am.entrySet()) {
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
    //    
    // // Were any error messages specified?
    // ActionMessages errors = null;
    // try {
    // errors = TagUtils.getInstance().getActionMessages(pageContext, name);
    // } catch (JspException e) {
    // TagUtils.getInstance().saveException(pageContext, e);
    // throw e;
    // }
    //
    // if ((errors == null) || errors.isEmpty()) {
    // return (EVAL_BODY_INCLUDE);
    // }
    //
    // boolean headerPresent = TagUtils.getInstance().present(pageContext, bundle, locale, getHeader());
    //
    // boolean footerPresent = TagUtils.getInstance().present(pageContext, bundle, locale, getFooter());
    //
    // boolean prefixPresent = TagUtils.getInstance().present(pageContext, bundle, locale, getPrefix());
    //
    // boolean suffixPresent = TagUtils.getInstance().present(pageContext, bundle, locale, getSuffix());
    //
    // // Rules einlesen
    // this.rules = RegexpRulesParser.parseRules(pattern);
    //
    // // regel anwenden
    // // Iterator<ActionMessage> matches =
    //
    // // Render the error messages appropriately
    // StringBuffer results = new StringBuffer();
    // boolean headerDone = false;
    // String message = null;
    // Iterator<ActionMessage> reports = null;
    //
    // if (pattern == null) {
    // reports = errors.get();
    // } else {
    // this.rules = RegexpRulesParser.parseRules(pattern);
    // reports = getMathes(errors).get();
    // }
    //
    // while (reports.hasNext()) {
    // ActionMessage report = reports.next();
    // if (!headerDone) {
    // if (headerPresent) {
    // message = TagUtils.getInstance().message(pageContext, bundle, locale, getHeader());
    //
    // results.append(message);
    // }
    // headerDone = true;
    // }
    //
    // if (prefixPresent) {
    // message = TagUtils.getInstance().message(pageContext, bundle, locale, getPrefix());
    // results.append(message);
    // }
    //
    // final String reportKey = report.getKey();
    //
    // final Object[] args = handleCusomActionMessage(report);
    //
    // if (report.isResource()) {
    // message = TagUtils.getInstance().message(pageContext, bundle, locale, reportKey, args);
    // } else {
    // message = report.getKey();
    // }
    //
    // if (message != null) {
    // if (this.escapeXml == true) {
    // results.append(StringEscapeUtils.escapeXml(message));
    // } else {
    // results.append(message);
    // }
    // }
    //
    // if (suffixPresent) {
    // message = TagUtils.getInstance().message(pageContext, bundle, locale, getSuffix());
    // results.append(message);
    // }
    // }
    //
    // if (headerDone && footerPresent) {
    // message = TagUtils.getInstance().message(pageContext, bundle, locale, getFooter());
    // results.append(message);
    // }
    //
    // TagUtils.getInstance().write(pageContext, results.toString());

  }

  // private Object[] handleCusomActionMessage(ActionMessage am)
  // {
  //
  // Object[] args = am.getValues();
  //
  // CustomActionMessage cr = null;
  //
  // if ((am instanceof CustomActionMessage) == false) {
  // return args;
  // }
  //
  // cr = (CustomActionMessage) am;
  //
  // if (cr.isEscape() == true) {
  // this.escapeXml = true;
  // return args;
  // }
  //
  // this.escapeXml = false;
  //
  // if (args == null) {
  // return null;
  // }
  //
  // if (cr.isEscapeArgs() == false) {
  // return args;
  // }
  //
  // for (int i = 0; i < args.length; ++i) {
  // if (args[i] instanceof String) {
  // args[i] = StringEscapeUtils.escapeXml((String) args[i]);
  // }
  // }
  // return args;
  // }
  //
  // @SuppressWarnings("unchecked")
  // private ActionMessages getMathes(ActionMessages errors)
  // {
  // Iterator<String> iter = errors.properties();
  // ActionMessages matched = new ActionMessages();
  // while (iter.hasNext() == true) {
  // String currentProperty = iter.next();
  // boolean matches = false;
  // for (Pair<Boolean, Pattern> rule : rules) {
  // if (rule.getSecond().matcher(currentProperty).matches() == true) {
  // matches = rule.getFirst();
  // }
  // }
  // if (matches == true) {
  // Iterator<ActionMessage> it = errors.get(currentProperty);
  // while (it.hasNext() == true) {
  // matched.add(currentProperty, it.next());
  // }
  // // matched.add(currentProperty, errors.
  // }
  // }
  // return matched;
  // }

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
}
