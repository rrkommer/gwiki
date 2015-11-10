package de.micromata.genome.gwiki.plugin.rogmp3_1_0;

import java.util.List;

public class Interpret extends RecBase
{
  public static final int PK = 0;

  public static final int NAME = 1;

  public static final int DETAIL_NAME = 0;

  public static final int INSTRUMENT = 2;

  public static final int DETAIL_INSTRUMENT = 1;

  public static final int DETAIL_TITEL_FK = 2;

  public static final int ROLLE = 3;

  public Interpret(Mp3Db db, String[] rec)
  {
    super(db, rec);
  }

  public List<Title> getTitles()
  {
    return db.getTitelsFromInterpret(this);
  }

  public String getPk()
  {
    return get(PK);
  }

  public String getName()
  {
    return get(NAME);
  }

  public String getInstrument()
  {
    return get(INSTRUMENT);
  }

}
