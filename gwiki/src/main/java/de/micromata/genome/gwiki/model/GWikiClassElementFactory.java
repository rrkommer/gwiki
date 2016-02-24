////////////////////////////////////////////////////////////////////////////
// 
// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer
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

import java.lang.reflect.Constructor;

import org.springframework.util.Assert;

import de.micromata.genome.gwiki.model.logging.GWikiLog;
import de.micromata.genome.gwiki.page.GWikiContext;

/**
 * Factory to create an GWikiElement based on GWikiElement class.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiClassElementFactory implements GWikiElementFactory
{
  // private Class< ? extends GWikiElement> elementClass;

  Constructor< ? extends GWikiElement> constructor;

  public GWikiClassElementFactory(Class< ? extends GWikiElement> elementClass)
  {
    // this.elementClass = elementClass;
    Assert.notNull(elementClass);
    try {
      constructor = elementClass.getConstructor(GWikiElementInfo.class);
    } catch (Exception e) {
      GWikiLog.warn("Cannot find matching constructor" + elementClass.getCanonicalName() + "(GWikiElement)");
      throw new RuntimeException(e);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gwiki.model.GWikiElementFactory#createElement(de.micromata.genome.gwiki.model.GWikiElementInfo,
   * de.micromata.genome.gwiki.page.GWikiContext)
   */
  public GWikiElement createElement(GWikiElementInfo ei, GWikiContext wikiContext)
  {
    try {
      return constructor.newInstance(ei);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
