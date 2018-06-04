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
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import de.micromata.genome.util.types.Pair;

public class Mp3UsageDB extends CsvTable
{
  public static String TITLE = "title";

  public static String TRACK = "track";

  public static String MEDIA = "media";

  File dbfile;

  public Mp3UsageDB(File dbfile)
  {
    this.dbfile = dbfile;
    if (dbfile.exists() == true) {
      load();
    }
    createIndex(Usage.PK);
  }

  private void load()
  {
    load(dbfile);
  }

  public void store()
  {
    store(dbfile);
  }

  public void add(String userName, String type, String pk)
  {
    List<String[]> res = findMultiEquals(Pair.make(Usage.PK, pk), Pair.make(Usage.USER, userName), Pair.make(Usage.TYPE, type));
    String[] entry;
    if (res.size() == 0) {
      Usage usage = new Usage(userName, type, pk);
      usage.setCount(1);
      usage.setDate(new Date());
      entry = usage.getRec();

      add(entry);
      return;
    }
    Usage us = new Usage(res.get(0));
    us.incrementCount();
    us.setDate(new Date());
  }

  public void dec(String userName, String type, String pk)
  {
    List<String[]> res = findMultiEquals(Pair.make(Usage.PK, pk), Pair.make(Usage.USER, userName), Pair.make(Usage.TYPE, type));
    if (res.isEmpty() == true) {
      return;
    }
    Usage us = new Usage(res.get(0));
    int count = us.getCount();
    if (count <= 0) {
      return;
    }
    us.setCount(count - 1);

  }

  public void addListen(String userName, String trackPk, String titpk, String mediaPk)
  {
    // if (StringUtils.isNotBlank(titpk) == true) {
    // add(userName, TITLE, titpk);
    // }
    // if (StringUtils.isNotBlank(mediaPk) == true) {
    // add(userName, MEDIA, mediaPk);
    // }
    if (StringUtils.isNotBlank(trackPk) == true) {
      add(userName, TRACK, trackPk);
    }
    store();
  }

  public void upUse(Mp3Db db, String titpk, String mediaPk, boolean upUse)
  {
    List<Track> tracks = null;
    if (StringUtils.isNotBlank(titpk) == true) {
      Title title = db.getTitleByPk(titpk);
      tracks = title.getTracks();
    } else if (StringUtils.isNotBlank(mediaPk) == true) {
      Media media = db.getMediaByPk(mediaPk);
      tracks = media.getTracks();
    }
    if (tracks == null) {
      return;
    }
    String userName = UserUtils.getCurrentUserName();
    for (Track track : tracks) {
      if (upUse == true) {
        add(userName, TRACK, track.getPk());
      } else {
        dec(userName, TRACK, track.getPk());
      }
    }
    store();
  }

  public Usage getUsage(String userName, String type, String pk)
  {
    List<String[]> res = findMultiEquals(Pair.make(Usage.PK, pk), Pair.make(Usage.USER, userName), Pair.make(Usage.TYPE, type));

    if (res.size() == 0) {
      return new Usage(userName, type, pk);
    }
    return new Usage(res.get(0));
  }
}
