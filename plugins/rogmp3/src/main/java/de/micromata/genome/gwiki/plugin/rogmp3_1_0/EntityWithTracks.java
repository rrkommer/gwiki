package de.micromata.genome.gwiki.plugin.rogmp3_1_0;

import java.util.Date;
import java.util.List;

public interface EntityWithTracks
{
  List<Track> getTracks();
  float getUsageCount();
  Date getLastUsage();
}
