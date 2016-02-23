////////////////////////////////////////////////////////////////////////////
// 
// Copyright (C) 2010 Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// 
////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.model;

import de.micromata.genome.logging.BaseLogging;
import de.micromata.genome.logging.LogCategory;

/**
 * Log Category for GWIKI.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public enum GWikiLogCategory implements LogCategory
{

  /**
   * The Wiki.
   */
  Wiki,
  /**
   * Launcher application
   */
  WikiLauncher;
  static {
    BaseLogging.registerLogCategories(values());
  }

  /**
   * The fq name.
   */
  private String fqName;

  /**
   * Instantiates a new g wiki log category.
   */
  private GWikiLogCategory()
  {
    fqName = "GWIKI" + "." + name();
  }

  @Override
  public String getFqName()
  {
    return fqName;
  }

  @Override
  public String getPrefix()
  {
    return "GNM";
  }
}
