package de.micromata.genome.gwiki.model.logging;

import de.micromata.genome.logging.BaseLogging;
import de.micromata.genome.logging.LogCategory;

public enum GWikiLogCategory implements LogCategory
{
  Wiki

  ;
  static {
    BaseLogging.registerLogCategories(values());
  }

  /**
   * The fq name.
   */
  private String fqName;

  /**
   * Instantiates a new genome log category.
   */
  private GWikiLogCategory()
  {
    fqName = getPrefix() + "." + name();
  }

  @Override
  public String getFqName()
  {
    return fqName;
  }

  @Override
  public String getPrefix()
  {
    return "WIKI";
  }
}
