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

import java.io.Serializable;
import java.util.Map;

/**
 * An artefakt is a part of a GWikiElement.
 * 
 * T is the compiled representation of the artefakt.
 * 
 * An artefakt can contain itself more artefakt. So a controler artefakt often contains a gspt-Template or a wiki page as nested artefakt.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
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
