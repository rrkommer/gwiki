package de.micromata.genome.gwiki.page.impl.wiki.parser;

import org.apache.commons.lang.StringUtils;

/**
 * Utilities to convert from edit control to/from wiki
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class WeditWikiUtils
{
  public static String weditToWiki(String text)
  {
    String ret = StringUtils.defaultString(text);
    //    ret = StringUtils.replace(ret, "\n", "");
    //    ret = StringUtils.replace(ret, "\r", "");
    //    ret = StringUtils.replace(ret, "</p>", "\n");
    //    ret = StringUtils.replace(ret, "<p>", "");

    return ret;
  }

  public static String wikiToWedit(String text)
  {
    String ret = StringUtils.defaultString(text);
    //    ret = StringUtils.replace(ret, "\n", "<br/>\n");
    //    ret = "<p>" + ret + "</p>";
    return ret;
  }

}
