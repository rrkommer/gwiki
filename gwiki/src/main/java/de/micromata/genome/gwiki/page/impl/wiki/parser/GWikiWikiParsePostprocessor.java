package de.micromata.genome.gwiki.page.impl.wiki.parser;

/**
 * After Parsing the wiki syntax a postprozessor can be manipulate the GWikiFragment tree.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public interface GWikiWikiParsePostprocessor
{
  void process(GWikiWikiParserContext parseContext);

  default int getPrio()
  {
    return 100;
  }
}
