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

package de.micromata.genome.gwiki.wicket;

import java.util.Locale;

import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.locator.IResourceStreamLocator;

import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiWeb;

/**
 * Locates Content from the GWiki.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiResourceStreamLocator implements IResourceStreamLocator
{
  protected IResourceStreamLocator parent;

  public GWikiResourceStreamLocator(IResourceStreamLocator parent)
  {
    this.parent = parent;
  }

  public IResourceStream locate(Class< ? > clazz, String path)
  {
    return parent.locate(clazz, path);
  }

  public IResourceStream locate(Class< ? > clazz, String path, String style, Locale locale, String extension)
  {
    String id = path + "." + extension;
    GWikiWeb wiki = GWikiWeb.getWiki();
    GWikiElementInfo el = wiki.findElementInfo(id);
    if (el == null) {
      return parent.locate(clazz, path, style, locale, extension);
    }
    return new GWikiPageResourceStream(id);
  }

  public IResourceStreamLocator getParent()
  {
    return parent;
  }

  public void setParent(IResourceStreamLocator parent)
  {
    this.parent = parent;
  }

}
