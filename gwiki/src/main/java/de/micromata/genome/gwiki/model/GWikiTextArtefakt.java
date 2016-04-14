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

/**
 * Artefakt with has a string as storage.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 * @param <T> compiled version of the artefakt.
 */
public interface GWikiTextArtefakt<T extends Serializable> extends GWikiArtefakt<T>
{
  String getStorageData();

  void setStorageData(String data);

  /**
   * 
   * @return true if the data should be archived.
   */
  boolean isNoArchiveData();

}
