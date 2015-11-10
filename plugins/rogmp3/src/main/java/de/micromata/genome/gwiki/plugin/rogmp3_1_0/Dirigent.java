package de.micromata.genome.gwiki.plugin.rogmp3_1_0;

public class Dirigent extends RecBase
{
  public static final int PK = 0;

  public static final int NAME = 1;

  public Dirigent(Mp3Db db, String[] rec)
  {
    super(db, rec);
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
