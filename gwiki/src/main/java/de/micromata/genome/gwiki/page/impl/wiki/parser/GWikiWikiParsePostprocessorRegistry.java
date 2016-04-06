package de.micromata.genome.gwiki.page.impl.wiki.parser;

import java.util.ArrayList;
import java.util.List;

import de.micromata.genome.gwiki.page.impl.wiki.smileys.GWikiWikiSmileyParsePostprocessor;

public class GWikiWikiParsePostprocessorRegistry
{
  private static GWikiWikiParsePostprocessorRegistry INSTANCE = new GWikiWikiParsePostprocessorRegistry();
  private List<GWikiWikiParsePostprocessor> processors = new ArrayList<>();

  public static GWikiWikiParsePostprocessorRegistry get()
  {
    return INSTANCE;
  }

  public GWikiWikiParsePostprocessorRegistry()
  {
    register(new GWikiWikiParagraphParsePostprocessor());
    register(new GWikiWikiSmileyParsePostprocessor());
  }

  public List<GWikiWikiParsePostprocessor> getProcessors()
  {
    return processors;
  }

  public void register(GWikiWikiParsePostprocessor proc)
  {
    for (int i = 0; i < processors.size(); ++i) {
      if (processors.get(i).getPrio() > proc.getPrio()) {
        processors.add(i, proc);
        return;
      }
    }
    processors.add(proc);
  }
}
