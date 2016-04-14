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

package de.micromata.genome.gwiki.pagelifecycle_1_0.artefakt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.micromata.genome.util.text.PipeValueList;

/**
 * Holds the entire stats for every element in the tenant
 * 
 * @author Stefan Stuetzer (s.stuetzer@micromata.com)
 */
public class BranchFileStats implements Serializable
{
  private static final long serialVersionUID = 2445215324804619206L;

  /**
   * Page-Id -> Filestats for page
   */
  private Map<String, FileStatsDO> contentMap = new HashMap<String, FileStatsDO>();
  
  public void addFileStats(FileStatsDO statsDO) {
    getContentMap().put(statsDO.getPageId(), statsDO);
  }
  
  /**
   * Returns filestats for given id. returns an empty object if id is not present
   * @param id
   */
  public FileStatsDO getFileStatsForId(String id)
  {
	  return contentMap.get(id);
  }

  /**
   * Returns filestats for given id. returns an empty object if id is not present
   * @param id
   */
  public List<FileStatsDO> getFileStatsForIds(List<String> pageIds)
  {
    List<FileStatsDO> fileStats = new ArrayList<FileStatsDO>();
    for (final String id : pageIds) {
      if (contentMap.containsKey(id)) {
        fileStats.add(contentMap.get(id));
      }
    }
    return fileStats;
  }
  
  public boolean isPagePresent(final String pageId) {
	  return contentMap.get(pageId) == null ? false : true;
  }
  
  /**
   * @param id
   */
  public void removePage(String id)
  {
    getContentMap().remove(id);
  }
  
  /**
   * @param contentMap the contentMap to set
   */
  public void setContentMap(Map<String, FileStatsDO> contentMap)
  {
    this.contentMap = contentMap;
  }

  /**
   * @return the contentMap
   */
  public Map<String, FileStatsDO> getContentMap()
  {
    return contentMap;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    for (FileStatsDO fileStatsDO : contentMap.values()) {
      String serializedLine = PipeValueList.encode(fileStatsDO.getAsMap());
      sb.append(serializedLine).append("\n");
    }
    return sb.toString();
  }
}
