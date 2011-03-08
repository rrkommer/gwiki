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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import de.micromata.genome.gwiki.model.GWikiProps;
import de.micromata.genome.gwiki.pagelifecycle_1_0.model.FileState;
import de.micromata.genome.util.text.PipeValueList;

/**
 * Representation of one line in the central branch file stats file
 * 
 * @author Stefan Stuetzer (s.stuetzer@micromata.com)
 */
public class FileStatsDO
{
  public static final String PAGE_ID = "pageId";

  public static final String FILE_STATE = "fileState";

  public static final String CREATED_AT = "createdAt";

  public static final String CREATED_BY = "createdBy";

  public static final String MODIFIED_AT = "modifiedAt";

  public static final String MODIFIED_BY = "modifiedBy";

  public static final String ASSIGNED_TO = "assignedTo";

  public static final String OPERATORS = "operators";

  public static final String START_AT = "startAt";

  public static final String END_AT = "endAt";

  public static final String PREVIOUS_ASSIGNEE = "previousAssignee";

  private String pageId;

  private FileState fileState;

  private String createdBy;

  private String createdAt;

  private String lastModifiedBy;

  private String lastModifiedAt;

  private String assignedTo;

  private Set<String> operators;

  private String startAt;

  private String endAt;
  
  private String previousAssignee;

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

  public Date getCreatedAtDate()
  {
    return GWikiProps.parseTimeStamp(this.createdAt);
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
   * 
   * @return
   */
  public Map<String, String> getAsMap()
  {
    Map<String, String> contentMap = new HashMap<String, String>();

    contentMap.put(PAGE_ID, pageId);
    contentMap.put(FILE_STATE, fileState.name());
    contentMap.put(ASSIGNED_TO, assignedTo);
    contentMap.put(CREATED_AT, createdAt);
    contentMap.put(CREATED_BY, createdBy);
    contentMap.put(MODIFIED_AT, StringUtils.defaultString(lastModifiedAt));
    contentMap.put(MODIFIED_BY, StringUtils.defaultString(lastModifiedBy));
    contentMap.put(OPERATORS, StringUtils.defaultString(StringUtils.join(operators, ",")));
    contentMap.put(START_AT, StringUtils.defaultString(startAt));
    contentMap.put(END_AT, StringUtils.defaultString(endAt));
    contentMap.put(PREVIOUS_ASSIGNEE, StringUtils.defaultString(previousAssignee));
    
    return contentMap;
  }

  /**
   * @param assignedTo the assignedTo to set
   */
  public void setAssignedTo(String assignedTo)
  {
    this.assignedTo = assignedTo;
  }

  /**
   * @return the assignedTo
   */
  public String getAssignedTo()
  {
    return assignedTo;
  }

  /**
   * @param operators the operators to set
   */
  public void setOperators(Set<String> operators)
  {
    this.operators = operators;
  }

  /**
   * @return the operators
   */
  public Set<String> getOperators()
  {
    return operators;
  }

  /**
   * @param startAt the startAt to set
   */
  public void setStartAt(String startAt)
  {
    this.startAt = startAt;
  }

  /**
   * @return the startAt
   */
  public String getStartAt()
  {
    return startAt;
  }

  /**
   * @param endAt the endAt to set
   */
  public void setEndAt(String endAt)
  {
    this.endAt = endAt;
  }

  /**
   * @return the endAt
   */
  public String getEndAt()
  {
    return endAt;
  }

  /**
   * @param previousAssignee the previousAssignee to set
   */
  public void setPreviousAssignee(String previousAssignee)
  {
    this.previousAssignee = previousAssignee;
  }

  /**
   * @return the previousAssignee
   */
  public String getPreviousAssignee()
  {
    return previousAssignee;
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
  }
}
