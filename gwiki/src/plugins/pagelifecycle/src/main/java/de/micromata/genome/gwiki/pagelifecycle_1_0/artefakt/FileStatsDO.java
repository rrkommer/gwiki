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
package de.micromata.genome.gwiki.pagelifecycle_1_0.artefakt;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.pagelifecycle_1_0.model.FileState;
import de.micromata.genome.util.text.PipeValueList;

/**
 * Representation of one line in the central branch file stats file
 * 
 * @author Stefan Stuetzer (s.stuetzer@micromata.com)
 */
public class FileStatsDO
{
  public static final String PAGE_ID = "page_id";
  public static final String FILE_STATE = "file_state";
  public static final String CREATED_AT = "created_at";
  public static final String CREATED_BY = "created_by";
  public static final String MODIFIED_AT = "modified_at";
  public static final String MODIFIED_BY = "modified_by";
  
  private String pageId;
  
  private FileState fileState;

  private String createdBy;
  
  private String createdAt;
  
  private String lastModifiedBy;
  
  private String lastModifiedAt;

  /**
   * @return the pageId
   */
  public String getPageId()
  {
    return pageId;
  }

  /**
   * @param pageId the pageId to set
   */
  public void setPageId(String pageId)
  {
    this.pageId = pageId;
  }

  /**
   * @return the fileState
   */
  public FileState getFileState()
  {
    return fileState;
  }

  /**
   * @param fileState the fileState to set
   */
  public void setFileState(FileState fileState)
  {
    this.fileState = fileState;
  }

  /**
   * @return the createdBy
   */
  public String getCreatedBy()
  {
    return createdBy;
  }

  /**
   * @param createdBy the createdBy to set
   */
  public void setCreatedBy(String createdBy)
  {
    this.createdBy = createdBy;
  }

  /**
   * @return the createdAt
   */
  public String getCreatedAt()
  {
    return createdAt;
  }

  /**
   * @param createdAt the createdAt to set
   */
  public void setCreatedAt(String createdAt)
  {
    this.createdAt = createdAt;
  }

  /**
   * @return the lastModifiedBy
   */
  public String getLastModifiedBy()
  {
    return lastModifiedBy;
  }

  /**
   * @param lastModifiedBy the lastModifiedBy to set
   */
  public void setLastModifiedBy(String lastModifiedBy)
  {
    this.lastModifiedBy = lastModifiedBy;
  }

  /**
   * @return the lastModifiedAt
   */
  public String getLastModifiedAt()
  {
    return lastModifiedAt;
  }

  /**
   * @param lastModifiedAt the lastModifiedAt to set
   */
  public void setLastModifiedAt(String lastModifiedAt)
  {
    this.lastModifiedAt = lastModifiedAt;
  }
  
  /**
   * Returns map to ease serialize with {@link PipeValueList}
   * @return
   */
  public Map<String, String> getAsMap() {
    Map<String, String> contentMap = new HashMap<String, String>();

    contentMap.put(PAGE_ID, pageId);
    contentMap.put(FILE_STATE, fileState.name());
    contentMap.put(CREATED_AT, createdAt);
    contentMap.put(CREATED_BY, createdBy);
    contentMap.put(MODIFIED_AT, StringUtils.defaultString(lastModifiedAt));
    contentMap.put(MODIFIED_BY, StringUtils.defaultString(lastModifiedBy));
    return contentMap;
  }
  
}
