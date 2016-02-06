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
