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
package de.micromata.genome.gwiki.pagelifecycle_1_0.model;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.model.GWikiProps;
import de.micromata.genome.gwiki.pagelifecycle_1_0.artefakt.FileStatsDO;

/**
 * Simple wrapper class for holding file information
 * 
 * @author Stefan Stuetzer (s.stuetzer@micromata.com)
 */
public class FileInfoWrapper
{
  private String branch;

  private GWikiElementInfo elementInfo;

  private FileStatsDO fileStats;

  /**
   * @param branch
   * @param elementInfo
   * @param fileStats
   */
  public FileInfoWrapper(String branch, GWikiElementInfo elementInfo, FileStatsDO fileStats)
  {
    super();
    this.branch = branch;
    this.elementInfo = elementInfo;
    this.fileStats = fileStats;
  }
  
  public String getCategoryString() {
    final String cat = StringUtils.substringBeforeLast(elementInfo.getId(), "/");
    return StringUtils.replace(cat, "/", " -> ");
  }

  /**
   * @param branch the branch to set
   */
  public void setBranch(String branch)
  {
    this.branch = branch;
  }

  /**
   * @return the branch
   */
  public String getBranch()
  {
    return branch;
  }

  /**
   * @param ei the ei to set
   */
  public void setElementInfo(GWikiElementInfo ei)
  {
    this.elementInfo = ei;
  }

  /**
   * @return the ei
   */
  public GWikiElementInfo getElementInfo()
  {
    return elementInfo;
  }

  /**
   * @param fileStats the fileStats to set
   */
  public void setFileStats(FileStatsDO fileStats)
  {
    this.fileStats = fileStats;
  }

  /**
   * @return the fileStats
   */
  public FileStatsDO getFileStats()
  {
    return fileStats;
  }
  
  public Date getStartAt() {
    return GWikiProps.parseTimeStamp(getFileStats().getStartAt());
  }
}
