package de.micromata.genome.gwiki.plugin.rogmp3_1_0;

import java.util.List;

public class Orchester extends RecBase
{
  public static final int PK = 0;

  public static final int NAME = 1;

  public Orchester(Mp3Db db, String[] rec)
  {
    super(db, rec);
  }

  public List<Title> getTitles()
  {
    return db.getTitelsFromOrchester(this);
  }

  public String getPk()
  {
    return get(PK);
  }

  public String getName()
  {
    return get(NAME);
  }
}
