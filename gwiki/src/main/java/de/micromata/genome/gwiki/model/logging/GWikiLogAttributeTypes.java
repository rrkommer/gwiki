package de.micromata.genome.gwiki.model.logging;

import de.micromata.genome.logging.AttributeTypeDefaultFiller;
import de.micromata.genome.logging.BaseLogging;
import de.micromata.genome.logging.DefaultLogAttributeRenderer;
import de.micromata.genome.logging.LogAttributeRenderer;
import de.micromata.genome.logging.LogAttributeType;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public enum GWikiLogAttributeTypes implements LogAttributeType
{
  PageId

  ;
  static {
    BaseLogging.registerLogAttributeType(values());
  }

  /**
   * The column name.
   */
  private String columnName;

  /**
   * The max size.
   */
  private int maxSize = -1;

  /**
   * The attribute default filler.
   */
  private AttributeTypeDefaultFiller attributeDefaultFiller;

  /**
   * The renderer.
   */
  private LogAttributeRenderer renderer = null;

  /**
   * Instantiates a new genome attribute type.
   */
  private GWikiLogAttributeTypes()
  {

  }

  /**
   * Instantiates a new genome attribute type.
   *
   * @param renderer the renderer
   */
  private GWikiLogAttributeTypes(LogAttributeRenderer renderer)
  {
    this(null, -1, null, renderer);
  }

  /**
   * Instantiates a new genome attribute type.
   *
   * @param columnName the column name
   * @param maxSize the max size
   */
  private GWikiLogAttributeTypes(String columnName, int maxSize)
  {
    this.columnName = columnName;
    this.maxSize = maxSize;
  }

  /**
   * Instantiates a new genome attribute type.
   *
   * @param columnName the column name
   * @param maxSize the max size
   * @param defaultFiller the default filler
   * @param renderer the renderer
   */
  private GWikiLogAttributeTypes(String columnName, int maxSize, AttributeTypeDefaultFiller defaultFiller,
      LogAttributeRenderer renderer)
  {
    this.columnName = columnName;
    this.maxSize = maxSize;
    this.attributeDefaultFiller = defaultFiller;
    this.renderer = renderer;
  }

  /**
   * Instantiates a new genome attribute type.
   *
   * @param columnName the column name
   * @param maxSize the max size
   * @param defaultFiller the default filler
   */
  private GWikiLogAttributeTypes(String columnName, int maxSize, AttributeTypeDefaultFiller defaultFiller)
  {
    this(columnName, maxSize, defaultFiller, new DefaultLogAttributeRenderer());
  }

  /**
   * Instantiates a new genome attribute type.
   *
   * @param defaultFiller the default filler
   * @param renderer the renderer
   */
  private GWikiLogAttributeTypes(AttributeTypeDefaultFiller defaultFiller, LogAttributeRenderer renderer)
  {
    this(null, -1, defaultFiller, renderer);
  }

  /**
   * Instantiates a new genome attribute type.
   *
   * @param defaultFiller the default filler
   */
  private GWikiLogAttributeTypes(AttributeTypeDefaultFiller defaultFiller)
  {
    this(null, -1, defaultFiller, new DefaultLogAttributeRenderer());
  }

  @Override
  public AttributeTypeDefaultFiller getAttributeDefaultFiller()
  {
    return attributeDefaultFiller;
  }

  @Override
  public String columnName()
  {
    return columnName;
  }

  @Override
  public boolean isSearchKey()
  {
    return columnName != null;
  }

  @Override
  public int maxValueSize()
  {
    return maxSize;
  }

  @Override
  public LogAttributeRenderer getRenderer()
  {
    if (this.renderer == null) {
      return DefaultLogAttributeRenderer.INSTANCE;
    }
    return this.renderer;
  }

  public void setRenderer(LogAttributeRenderer renderer)
  {
    this.renderer = renderer;
  }
}
