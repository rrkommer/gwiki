package de.micromata.genome.gwiki.page.impl.wiki.smileys;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiArtefakt;
import de.micromata.genome.gwiki.model.GWikiElement;
import de.micromata.genome.gwiki.model.GWikiI18NArtefakt;
import de.micromata.genome.gwiki.page.GWikiContext;

public class GWikiSmileyConfig
{
  private Map<String, GWikiSmileyInfo> smileysByName = new HashMap<>();
  private Map<String, GWikiSmileyInfo> smileysByShortCut = new HashMap<>();
  private Map<String, GWikiSmileyInfo> smileys = new HashMap<>();
  private static GWikiSmileyConfig INSTANCE = null;
  private static GWikiElement CACHED_ELEMENT = null;

  public static GWikiSmileyConfig get(GWikiContext ctx)
  {
    if (INSTANCE == null) {
      INSTANCE = load(ctx);
    } else {
      GWikiElement ne = ctx.getWikiWeb().findElement("admin/config/SmileysConfig");
      if (ne != CACHED_ELEMENT) {
        INSTANCE = load(ctx, ne);
        CACHED_ELEMENT = ne;
      }
    }

    return INSTANCE;
  }

  private static GWikiSmileyConfig load(GWikiContext ctx)
  {
    CACHED_ELEMENT = ctx.getWikiWeb().findElement("admin/config/SmileysConfig");
    return load(ctx, CACHED_ELEMENT);
  }

  private static GWikiSmileyConfig load(GWikiContext ctx, GWikiElement configElement)
  {

    if (configElement == null) {
      return new GWikiSmileyConfig();
    }
    GWikiArtefakt<?> part = configElement.getMainPart();
    GWikiSmileyConfig nc = new GWikiSmileyConfig(ctx, (GWikiI18NArtefakt) part);
    return nc;

  }

  public GWikiSmileyConfig()
  {

  }

  public GWikiSmileyConfig(GWikiContext ctx, GWikiI18NArtefakt art)
  {
    for (Map.Entry<String, String> me : art.getCompiledObject().entrySet()) {
      String key = me.getKey();
      String val = me.getValue();
      int idx = val.indexOf('|');
      String shortCut = null;
      if (idx != -1) {
        shortCut = val.substring(idx + 1);
        val = val.substring(0, idx);
      }
      GWikiSmileyInfo ni = new GWikiSmileyInfo(shortCut, key, val);
      smileysByName.put(key, ni);
      smileys.put(key, ni);
      if (StringUtils.isNotBlank(shortCut) == true) {
        smileysByShortCut.put(shortCut, ni);
        smileys.put(shortCut, ni);
      }
    }
  }

  public Map<String, GWikiSmileyInfo> getSmileysByName()
  {
    return smileysByName;
  }

  public Map<String, GWikiSmileyInfo> getSmileysByShortCut()
  {
    return smileysByShortCut;
  }

  public Map<String, GWikiSmileyInfo> getSmileys()
  {
    return smileys;
  }

}
