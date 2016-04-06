package de.micromata.genome.gwiki.page.impl.wiki.smileys;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class GWikiSmileyInfo
{
  private String shortCut;
  private String shortName;
  private String source;

  public GWikiSmileyInfo(String shortCut, String shortName, String source)
  {
    this.shortCut = shortCut;
    this.shortName = shortName;
    this.source = source;
  }

  public String getShortCut()
  {
    return shortCut;
  }

  public void setShortCut(String shortCut)
  {
    this.shortCut = shortCut;
  }

  public String getShortName()
  {
    return shortName;
  }

  public void setShortName(String shortName)
  {
    this.shortName = shortName;
  }

  public String getSource()
  {
    return source;
  }

  public void setSource(String source)
  {
    this.source = source;
  }

}