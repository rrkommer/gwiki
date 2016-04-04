package de.micromata.genome.gwiki.page.impl.wiki.rte;

public interface DomListener
{
  /**
   * less is higher prio
   * 
   * @return
   */
  default int getPrio()
  {
    return 100;
  }
}
