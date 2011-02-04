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
package de.micromata.genome.gwiki.plugin.vfolder_1_0;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import de.micromata.genome.gwiki.model.GWikiElementInfo;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class GWikiVFolderCachedFileInfos implements Serializable
{

  private static final long serialVersionUID = -3087624148091649306L;

  private Map<String, GWikiElementInfo> vfolderFiles = new TreeMap<String, GWikiElementInfo>();

  public GWikiElementInfo getElementInfoByLocalName(String localFsName)
  {
    return vfolderFiles.get(localFsName);
  }

  public Iterable<String> getLocalNames()
  {
    return vfolderFiles.keySet();
  }

  public Iterable<GWikiElementInfo> getElementInfos()
  {
    return vfolderFiles.values();
  }

  public void removeElement(String localFsName)
  {
    vfolderFiles.remove(localFsName);
  }

  public void addElement(String localFsName, GWikiElementInfo ei)
  {
    vfolderFiles.put(localFsName, ei);
  }

  public Map<String, GWikiElementInfo> getVfolderFiles()
  {
    return vfolderFiles;
  }

  public void setVfolderFiles(Map<String, GWikiElementInfo> vfolderFiles)
  {
    this.vfolderFiles = vfolderFiles;
  }
}
