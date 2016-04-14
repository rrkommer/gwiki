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

package de.micromata.genome.gwiki.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.util.runtime.CallableX;

/**
 * Common Base implementation for artefakt. TODO T is not necessary serializable.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public abstract class GWikiArtefaktBase<T extends Serializable> implements GWikiArtefakt<T>
{
  private static final long serialVersionUID = 4441119106931189866L;

  protected Map<String, GWikiArtefakt<?>> parts = new HashMap<String, GWikiArtefakt<?>>();

  /**
   * compiled object. should be always reconstructed by storage data.
   */
  private transient T compiledObject;

  abstract public boolean renderWithParts(final GWikiContext ctx);

  public void prepareHeader(GWikiContext wikiContext)
  {
    wikiContext.getRequiredJs().add("/static/gwiki/gwikijqdialog.js");
  }

  public boolean render(final GWikiContext ctx)
  {
    return ctx.runWithParts(parts, new CallableX<Boolean, RuntimeException>()
    {
      @Override
      public Boolean call()
      {
        return renderWithParts(ctx);
      }
    });
  }

  public void collectArtefakts(List<GWikiArtefakt<?>> al)
  {
    al.add(this);
    if (parts == null || parts.isEmpty() == true) {
      return;
    }
    for (GWikiArtefakt<?> a : parts.values()) {
      al.add(a);
    }
  }

  @Override
  public void collectParts(Map<String, GWikiArtefakt<?>> map)
  {
    if (parts == null || parts.isEmpty() == true) {
      return;
    }
    map.putAll(parts);
    for (GWikiArtefakt<?> a : parts.values()) {
      a.collectParts(map);
    }
  }

  @Override
  public T getCompiledObject()
  {
    return compiledObject;
  }

  @Override
  public void setCompiledObject(T compiledObject)
  {
    this.compiledObject = compiledObject;
  }

  public Map<String, GWikiArtefakt<?>> getParts()
  {
    return parts;
  }

  public void setParts(Map<String, GWikiArtefakt<?>> parts)
  {
    this.parts = parts;
  }

}
