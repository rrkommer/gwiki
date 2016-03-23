package de.micromata.genome.gwiki.page.impl.wiki.macros.registry;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.TreeMap;

import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFactory;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class GWikiMacroProviderRegistry
{
  public static Map<String, GWikiMacroFactory> getMacros()
  {
    Map<String, GWikiMacroFactory> ret = new TreeMap<String, GWikiMacroFactory>();
    ServiceLoader<GWikiMacroProviderService> sl = ServiceLoader.load(GWikiMacroProviderService.class);
    for (GWikiMacroProviderService service : sl) {
      ret.putAll(service.getMacros());
    }
    return ret;
  }
}
