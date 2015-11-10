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
import java.util.List;
import java.util.Map;

/**
 * Standard GWikiBinaryArtefakt base implementation
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public abstract class GWikiBinaryArtefaktBase<T extends Serializable> implements GWikiBinaryArtefakt<T>, GWikiPersistArtefakt<T>
{

  private static final long serialVersionUID = -2412235746170911693L;

  private byte[] storageData;

  public boolean isNoArchiveData()
  {
    return false;
  }

  public byte[] getStorageData()
  {
    return storageData;
  }

  public void setStorageData(byte[] data)
  {
    this.storageData = data;
  }

  public void collectArtefakts(List<GWikiArtefakt< ? >> al)
  {

  }

  public void collectParts(Map<String, GWikiArtefakt< ? >> map)
  {

  }

}
