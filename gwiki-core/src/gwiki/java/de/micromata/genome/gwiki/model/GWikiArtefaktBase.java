/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   25.10.2009
// Copyright Micromata 25.10.2009
//
/////////////////////////////////////////////////////////////////////////////
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
 * @author roger@micromata.de
 * 
 */
public abstract class GWikiArtefaktBase<T extends Serializable> implements GWikiArtefakt<T>
{
  private static final long serialVersionUID = 4441119106931189866L;

  protected Map<String, GWikiArtefakt< ? >> parts = new HashMap<String, GWikiArtefakt< ? >>();

  /**
   * compiled object. should be always reconstructed by storage data.
   */
  private transient T compiledObject;

  abstract public boolean renderWithParts(final GWikiContext ctx);

  public void prepareHeader(GWikiContext wikiContext)
  {
    
  }

  public boolean render(final GWikiContext ctx)
  {
    return ctx.runWithParts(parts, new CallableX<Boolean, RuntimeException>() {
      public Boolean call()
      {
        return renderWithParts(ctx);
      }
    });
  }

  public void collectArtefakts(List<GWikiArtefakt< ? >> al)
  {
    al.add(this);
    if (parts == null || parts.isEmpty() == true)
      return;
    for (GWikiArtefakt< ? > a : parts.values()) {
      al.add(a);
    }
  }

  public void collectParts(Map<String, GWikiArtefakt< ? >> map)
  {
    if (parts == null || parts.isEmpty() == true)
      return;
    map.putAll(parts);
    for (GWikiArtefakt< ? > a : parts.values()) {
      a.collectParts(map);
    }
  }

  public T getCompiledObject()
  {
    return compiledObject;
  }

  public void setCompiledObject(T compiledObject)
  {
    this.compiledObject = compiledObject;
  }

  public Map<String, GWikiArtefakt< ? >> getParts()
  {
    return parts;
  }

  public void setParts(Map<String, GWikiArtefakt< ? >> parts)
  {
    this.parts = parts;
  }

}
