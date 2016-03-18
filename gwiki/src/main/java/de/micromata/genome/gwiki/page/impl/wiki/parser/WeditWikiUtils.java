package de.micromata.genome.gwiki.page.impl.wiki.parser;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Utilities to convert from edit control to/from wiki
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class WeditWikiUtils
{
  private static final Logger LOG = Logger.getLogger(WeditWikiUtils.class);

  public static String weditToWiki(String text)
  {
    String ret = StringUtils.defaultString(text);
    ret = StringUtils.replace(ret, "<br/>", "");
    ret = StringUtils.replace(ret, "<br>", "");
    ret = StringUtils.replace(ret, "</br>", "");
    ret = StringUtils.replace(ret, "&nbsp;", " ");

    //    ret = StringUtils.replace(ret, "\n", "");
    //    ret = StringUtils.replace(ret, "\r", "");
    //    ret = StringUtils.replace(ret, "</p>", "\n");
    //    ret = StringUtils.replace(ret, "<p>", "");
    ret = StringUtils.replace(ret, "&lt;", "<");
    ret = StringUtils.replace(ret, "&gt;", ">");
    LOG.debug("weditToWiki\nwedit: " + text + "\n\nwiki: " + ret);
    return ret;
  }

  public static String wikiToWedit(String text)
  {
    String ret = StringUtils.defaultString(text);
    ret = StringEscapeUtils.escapeXml(ret);

    //    ret = StringUtils.replace(ret, "\n", "<br/>\n");
    //    ret = "<p>" + ret + "</p>";
    return ret;
  }

}
