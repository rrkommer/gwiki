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

package de.micromata.genome.gwiki.chronos.spi.jdbc;

import de.micromata.genome.jpa.StdRecordDO;

public class ChronosBaseDO extends StdRecordDO<Long>
{
  public ChronosBaseDO()
  {

  }

  public ChronosBaseDO(ChronosBaseDO other)
  {
    this.createdAt = other.createdAt;
    this.createdBy = other.createdBy;
    this.modifiedAt = other.modifiedAt;
    this.modifiedBy = other.modifiedBy;
    this.updateCounter = other.updateCounter;
    this.pk = other.pk;
  }

  public Long getId()
  {
    return getPk();
  }

  @Override
  public Long getPk()
  {
    return pk;
  }

}
