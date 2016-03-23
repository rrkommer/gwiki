package de.micromata.genome.gwiki.page.impl.wiki.macros.registry;

import java.util.Map;

import de.micromata.genome.gwiki.page.impl.wiki.GWikiMacroFactory;

/**
 * Should be registered by jre ServiceLoader.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public interface GWikiMacroProviderService
{
  /**
   * Get all macros with given name.
   * 
   * @return
   */
  Map<String, GWikiMacroFactory> getMacros();
}
