//
// Copyright (C) 2010-2016 Roger Rene Kommer & Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

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
public enum GWikiLogAttributeType implements LogAttributeType
{
  /**
   * The current Page
   */
  PageId,
  /**
   * A list of page ids
   */
  PageIds,

  BranchId,
  /**
   * Calling host.
   */
  RemoteHost(new GWikiRemoteHostFiller()),

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
  private GWikiLogAttributeType()
  {

  }

  /**
   * Instantiates a new genome attribute type.
   *
   * @param renderer the renderer
   */
  private GWikiLogAttributeType(LogAttributeRenderer renderer)
  {
    this(null, -1, null, renderer);
  }

  /**
   * Instantiates a new genome attribute type.
   *
   * @param columnName the column name
   * @param maxSize the max size
   */
  private GWikiLogAttributeType(String columnName, int maxSize)
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
  private GWikiLogAttributeType(String columnName, int maxSize, AttributeTypeDefaultFiller defaultFiller,
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
  private GWikiLogAttributeType(String columnName, int maxSize, AttributeTypeDefaultFiller defaultFiller)
  {
    this(columnName, maxSize, defaultFiller, new DefaultLogAttributeRenderer());
  }

  /**
   * Instantiates a new genome attribute type.
   *
   * @param defaultFiller the default filler
   * @param renderer the renderer
   */
  private GWikiLogAttributeType(AttributeTypeDefaultFiller defaultFiller, LogAttributeRenderer renderer)
  {
    this(null, -1, defaultFiller, renderer);
  }

  /**
   * Instantiates a new genome attribute type.
   *
   * @param defaultFiller the default filler
   */
  private GWikiLogAttributeType(AttributeTypeDefaultFiller defaultFiller)
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
