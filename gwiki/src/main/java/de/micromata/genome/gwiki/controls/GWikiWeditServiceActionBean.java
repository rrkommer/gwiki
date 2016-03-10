package de.micromata.genome.gwiki.controls;

import de.micromata.genome.gwiki.page.impl.wiki.parser.WeditWikiUtils;

/**
 * Ajax services.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class GWikiWeditServiceActionBean extends ActionBeanAjaxBase
{
  private String txt;

  public Object onWeditToWiki()
  {
    String text = WeditWikiUtils.weditToWiki(txt);
    return sendJsonResponse("txt", text);
  }

  public Object onWikiToWedit()
  {
    String text = WeditWikiUtils.wikiToWedit(txt);
    return sendJsonResponse("txt", text);
  }

  public String getTxt()
  {
    return txt;
  }

  public void setTxt(String txt)
  {
    this.txt = txt;
  }

}
