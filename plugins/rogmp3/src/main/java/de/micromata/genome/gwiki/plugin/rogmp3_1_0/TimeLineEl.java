package de.micromata.genome.gwiki.plugin.rogmp3_1_0;

import java.util.ArrayList;
import java.util.List;

public class TimeLineEl
{
  public int year;

  public List<Composer> start = new ArrayList<Composer>();

  public List<Composer> end = new ArrayList<Composer>();

  public TimeLineEl(int year)
  {
    this.year = year;
  }
}
