package de.micromata.genome.gwiki.page.impl.wiki.rte.els;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.page.impl.wiki.parser.GWikiWikiParserContext;
import de.micromata.genome.gwiki.page.impl.wiki.rte.DomElementEvent;
import de.micromata.genome.gwiki.page.impl.wiki.rte.DomElementListener;
import de.micromata.genome.gwiki.page.impl.wiki.smileys.GWikiFragmentSmiley;
import de.micromata.genome.gwiki.page.impl.wiki.smileys.GWikiSmileyConfig;
import de.micromata.genome.gwiki.page.impl.wiki.smileys.GWikiSmileyInfo;

public class RteSmileyDomElementListener implements DomElementListener
{
  GWikiSmileyConfig smileyConfig = null;

  @Override
  public boolean listen(DomElementEvent event)
  {
    if (smileyConfig == null) {
      smileyConfig = GWikiSmileyConfig.get(event.getWikiContext());
    }
    String smilyId = event.getAttr("data-wiki-smiley");
    if (StringUtils.isBlank(smilyId) == true) {
      return true;
    }
    GWikiWikiParserContext parseContext = event.getParseContext();
    parseContext.flushText();
    event.walker.skipChildren();
    GWikiSmileyInfo smi = smileyConfig.getSmileysByName().get(smilyId);
    if (smi != null) {
      parseContext.addFragment(new GWikiFragmentSmiley(smi));
    }
    return false;
  }

  @Override
  public int getPrio()
  {
    return 20;
  }

}
