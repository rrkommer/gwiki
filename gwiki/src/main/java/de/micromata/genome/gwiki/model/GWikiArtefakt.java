/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   21.10.2009
// Copyright Micromata 21.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.model;

import java.io.Serializable;
import java.util.Map;

/**
 * An artefakt is a part of a GWikiElement.
 * 
 * T is the compiled representation of the artefakt.
 * 
 * An artefakt can contain itself more artefakt. So a controler artefakt often contains a gspt-Template or a wiki page as nested artefakt.
 * 
 * @author roger@micromata.de
 * 
 */
public interface GWikiArtefakt<T extends Serializable> extends Serializable
{
  /**
   * Getter for the compiled object.
   * 
   * @return may return null if not compiled
   */
  T getCompiledObject();

  /**
   * Setter of the compiled object.
   * 
   * @param compiledObject
   */
  void setCompiledObject(T compiledObject);

  /**
   * Collect all artefakts, including this
   * 
   * @param al
   */
  void collectParts(Map<String, GWikiArtefakt< ? >> map);
}
