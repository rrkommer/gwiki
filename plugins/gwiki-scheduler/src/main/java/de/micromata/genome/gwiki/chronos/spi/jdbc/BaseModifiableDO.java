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

/////////////////////////////////////////////////////////////////////////////
//
// Project Genome Core
//
// Author    roger@micromata.de
// Created   16.02.2007
// Copyright Micromata 16.02.2007
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos.spi.jdbc;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

public class BaseModifiableDO extends BaseCreatableDO
{
  private String modifiedBy;

  private Date modifiedAt;

  private int updateCounter;

  public Date getModifiedAt()
  {
    return modifiedAt;
  }

  public void setModifiedAt(Date modifiedAt)
  {
    this.modifiedAt = modifiedAt;
  }

  public String getModifiedBy()
  {
    return modifiedBy;
  }

  public void setModifiedBy(String modifiedBy)
  {
    this.modifiedBy = modifiedBy;
  }

  public int getUpdateCounter()
  {
    return updateCounter;
  }

  public void setUpdateCounter(int updateCounter)
  {
    this.updateCounter = updateCounter;
  }

  public String getModifiedAtString()
  {
    if (modifiedAt == null)
      return null;
    return formatDate(modifiedAt);
  }

  @Override
  public void toString(ToStringBuilder sb)
  {
    sb.append("modified", getModifiedAtString());
    sb.append("updateCounter", updateCounter);
    super.toString(sb);
  }
}
