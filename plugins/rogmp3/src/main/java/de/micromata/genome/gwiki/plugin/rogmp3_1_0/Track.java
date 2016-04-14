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

package de.micromata.genome.gwiki.plugin.rogmp3_1_0;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.util.types.Converter;

public class Track extends RecBase
{
  /**
   * Asume maximum tracks in DB
   */
  public static final int MAX_DB_TRACKS = 100000;

  public static final int PK = 0;

  public static final int FK_TITLE = 1;

  public static final int NAME = 3;

  public static final int LENGTH = 4;

  public static final int NAMEONCD = 5;

  public static final int REC_SICE = 7;

  private boolean synteticTrack = false;

  /**
   * Number of track
   */
  private int trackNo = 0;

  private static int cursynteticTrackPk = MAX_DB_TRACKS;

  private static int nextSynteticTrackPk()
  {
    return ++cursynteticTrackPk;
  }

  /**
   * key is Title.PK_no
   */
  static Map<String, Integer> synteticTracks = new HashMap<String, Integer>();

  private static ThreadLocal<SimpleDateFormat> trackTimeFormat = new ThreadLocal<SimpleDateFormat>() {

    @Override
    protected SimpleDateFormat initialValue()
    {
      return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

  };

  public Track(Mp3Db db, String[] rec)
  {
    super(db, rec);
  }

  static String[] buildRecFromFile(Title title, File mp3file, int no)
  {

    String[] rec = new String[REC_SICE];
    String key = "" + title.getPk() + "_" + no;
    Integer pk = synteticTracks.get(key);
    if (pk == null) {
      pk = nextSynteticTrackPk();
      synteticTracks.put(key, pk);
    }
    rec[PK] = pk.toString();
    rec[FK_TITLE] = title.getPk();
    rec[NAME] = mp3file.getName();
    rec[NAMEONCD] = mp3file.getName();
    return rec;

  }

  public Track(Mp3Db db, File mp3file, Title title, int no)
  {
    super(db, buildRecFromFile(title, mp3file, no));
    this.mp3path = mp3file;
    synteticTrack = true;
    trackNo = no;
  }

  public Title getTitle()
  {
    return db.getTitleByPk(get(FK_TITLE));
  }

  public String getNameOnFs()
  {
    String r = get(NAMEONCD);
    if (StringUtils.isBlank(r) == false) {
      return r;
    }
    return normalize(getName());
  }

  public File getMp3Path()
  {
    if (mp3path != null) {
      return mp3path;
    }
    Title comp = getTitle();
    mp3path = new File(comp.getMp3Path(), getNameOnFs());
    readMp3Infos();
    return mp3path;
  }

  public Usage getUsage(String userName)
  {
    return db.getUsageDb().getUsage(userName, Mp3UsageDB.TRACK, getPk());
  }

  public String getTitleFk()
  {
    return get(FK_TITLE);
  }

  public String getPk()
  {
    return get(PK);
  }

  public String getName()
  {
    return get(NAME);
  }

  public String getHtmlForList()
  {
    if (isSynteticTrack() == true) {
      return esc(get(NAME));
    }
    StringBuilder sb = new StringBuilder();
    sb.append(esc(get(NAME))); // + " (" + esc(get(NAMEONCD) + ")"
    String l = get(LENGTH);
    if (StringUtils.isNotBlank(l) == true) {
      int idx = l.indexOf(' ');
      if (idx != -1) {
        l = l.substring(idx);
      }
      sb.append(" [").append(esc(l)).append("]");
    }
    return sb.toString();
  }

  public long getTime()
  {
    String l = get(LENGTH);
    if (StringUtils.isBlank(l) == true) {
      return 0;
    }
    try {
      Date d = trackTimeFormat.get().parse(l);
      return d.getSeconds() + d.getMinutes() * 60 + d.getHours() * 3600;
    } catch (ParseException e) {
      System.out.println("cannot parse length: " + l);
      return 0;
    }
  }

  public long getSize()
  {
    String s = get(6);
    if (StringUtils.isBlank(s) == true) {
      return 0;
    }
    try {
      return Long.parseLong(s) * 1024;
    } catch (NumberFormatException ex) {
      System.out.println("cannot parse size: " + s);
      return 0;
    }
  }

  public void readMp3Infos()
  {

    // throws javax.sound.sampled.UnsupportedAudioFileException: file is not a supported file type
    // try {
    // File file = getMp3Path();
    // // AudioFileFormat baseFileFormat = new MpegAudioFileReader().getAudioFileFormat(file);
    // AudioFileFormat baseFileFormat = AudioSystem.getAudioFileFormat(file);
    // Map properties = baseFileFormat.properties();
    // Long duration = (Long) properties.get("duration");
    // } catch (Exception ex) {
    // ex.printStackTrace();
    // }
  }

  public static void sortByUsage(List< ? extends EntityWithTracks> list, final boolean desc)
  {
    Collections.sort(list, new Comparator<EntityWithTracks>() {

      @Override
      public int compare(EntityWithTracks o1, EntityWithTracks o2)
      {
        float ua1 = o1.getUsageCount();
        float ua2 = o2.getUsageCount();
        if (ua1 > ua2) {
          return desc ? 1 : -1;
        } else if (ua1 < ua2) {
          return desc ? -1 : 1;
        }
        return 0;
      }
    });
  }
  public static void sortByDate(List< ? extends EntityWithTracks> list, final boolean desc)
  {
    Collections.sort(list, new Comparator<EntityWithTracks>() {

      @Override
      public int compare(EntityWithTracks o1, EntityWithTracks o2)
      {
        Date ua1 = o1.getLastUsage();
        Date ua2 = o2.getLastUsage();
        return ua1.compareTo(ua2) * (desc ? -1 : 1);
      }
    });
  }
  public static float getUsageCount(EntityWithTracks ent)
  {
    return getUsageCount(ent.getTracks());
  }

  public static Date getLastUsage(EntityWithTracks ent)
  {
    return getLastUsage(ent.getTracks());
  }

  public static float getUsageCount(List<Track> tracks)
  {
    if (tracks.isEmpty() == true) {
      return 0;
    }
    int sum = 0;
    for (Track track : tracks) {
      sum += track.getUsage(UserUtils.getCurrentUserName()).getCount();
    }
    return (float) sum / tracks.size();
  }

  public static String getUsageDisplay(EntityWithTracks ent)
  {
    return " (" + (int)ent.getUsageCount() + "|" + Converter.formatByIsoDayFormat(ent.getLastUsage()) + ")";
  }
  public static Date getLastUsage(List<Track> tracks)
  {
    if (tracks.isEmpty() == true) {
      return new Date(0);
    }
    Date d = new Date(0);
    for (Track track : tracks) {
      Date td = track.getUsage(UserUtils.getCurrentUserName()).getDate();
      if (td.compareTo(d) > 0) {
        d = td;
      }
    }
    return d;
  }

  public boolean isSynteticTrack()
  {
    return synteticTrack;
  }

  public void setSynteticTrack(boolean synteticTrack)
  {
    this.synteticTrack = synteticTrack;
  }

  public int getTrackNo()
  {
    return trackNo;
  }

  public void setTrackNo(int trackNo)
  {
    this.trackNo = trackNo;
  }

}
