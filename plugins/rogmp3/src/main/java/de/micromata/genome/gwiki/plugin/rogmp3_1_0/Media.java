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

import static de.micromata.genome.util.xml.xmlbuilder.Xml.text;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import de.micromata.genome.gwiki.utils.WebUtils;
import de.micromata.genome.util.types.Converter;
import de.micromata.genome.util.types.Pair;
import de.micromata.genome.util.xml.xmlbuilder.Xml;
import de.micromata.genome.util.xml.xmlbuilder.html.Html;

public class Media extends RecBase implements EntityWithTracks
{
  public static final int PK = 0;

  public static final int NAME1 = 1;

  public static final int NAME2 = 2;

  public static final int NAME3 = 3;

  public static final int SERIENR = 4;

  public static final int MEDIACOUNT = 5;

  public static final int STORE = 6;

  public static final int LABEL = 7;

  public static final int LABELID = 8;

  public static final int MEDIATYPE = 9;

  public static final int COVERFRONT = 11;

  public static final int COVERBACK = 12;

  public static final int DATE_INDB = 14;

  public static final int REIHE = 16;

  public static final int JPCID = 23;

  public static final int IGNORE_ROGIPOD = 25;

  List<Track> tracks = null;

  /**
   * @param db
   * @param rec
   */
  public Media(Mp3Db db, String[] rec)
  {
    super(db, rec);
  }

  public String getPk()
  {
    return get(PK);
  }

  public String getListName()
  {
    StringBuilder sb = new StringBuilder();
    String name = get(NAME1);
    if (name.equals("*DummyCD Doris*") == true) {
      name = "DummyCD 3";
    }
    if (name.equals("*DummyCD Marco*") == true) {
      name = "DummyCD 2";
    }
    if (name.equals("*DummyCD Roger*") == true) {
      name = "DummyCD 1";
    }
    sb.append(name);
    if (get(NAME2).length() > 0) {
      sb.append(", ").append(get(NAME2));
    }
    if (get(NAME3).length() > 0) {
      sb.append(", ").append(get(NAME3));
    }
    sb.append(Track.getUsageDisplay(this));
    return sb.toString();
  }

  public List<Title> getTitleList()
  {
    List<String[]> recs = db.getTitle().findEquals(Title.MEDIA_FK, getPk());
    db.getTitle().sortByIndex(recs, Title.PK);
    List<Title> ret = new ArrayList<Title>();
    for (String[] rec : recs) {
      ret.add(new Title(db, rec));
    }
    return ret;
  }

  public String getDetailDescription(String localUrl)
  {
    StringBuilder sb = new StringBuilder();
    sb.append(renderDetailRec("Reihe: ", REIHE));

    sb.append(renderDetailRec("Label: ", LABEL));
    sb.append(renderDetailRec("Label-ID: ", LABELID));
    sb.append(renderDetailRec("In DB: ", DATE_INDB));

    sb.append(renderDetailRec("Media: ", MEDIATYPE));
    sb.append(renderDetailRec("Media-Anzahl: ", MEDIACOUNT));

    sb.append(renderDetailRec("Store1: ", STORE));
    sb.append(renderDetailRec("Store2: ", IGNORE_ROGIPOD));

    sb.append("Last heared: ");
    sb.append((int) getUsageCount()).append("|").append(Converter.formatByIsoDayFormat(getLastUsage()));
    sb.append(Html.a(Xml.attrs("href", localUrl + "?mediaPk=" + WebUtils.encodeUrlParam(getPk()) + "&upUse=true"), text("Up")).toString());
    sb.append(" ");
    sb.append(Html.a(Xml.attrs("href", localUrl + "?mediaPk=" + WebUtils.encodeUrlParam(getPk()) + "&upUse=false"), text("Down")).toString());
    

    sb.append("<br/>\n");

    return sb.toString();
  }

  public List<Track> getTracks()
  {
    return getTrackList();
  }

  public List<Track> getTrackList()
  {
    if (tracks != null) {
      return tracks;
    }
    List<Title> titles = getTitleList();
    List<Track> ret = new ArrayList<Track>();
    for (Title titel : titles) {
      List<Track> st = titel.getTracks();
      ret.addAll(st);
    }
    // NOT if muliple CDs'
    // Collections.sort(ret, new Comparator<Track>() {
    //
    // @Override
    // public int compare(Track o1, Track o2)
    // {
    // return o1.getNameOnFs().compareTo(o2.getNameOnFs());
    // }
    // });
    return tracks = ret;
  }

  public float getUsageCount()
  {
    return Track.getUsageCount(getTracks());
  }

  public Date getLastUsage()
  {
    return Track.getLastUsage(getTracks());
  }

  public Pair<String, byte[]> getMp3Zip()
  {
    List<Track> tracks = getTrackList();
    String fname = get(NAME1);
    if (StringUtils.isNotBlank(get(NAME2)) == true) {
      fname = fname + " " + get(NAME2);
    }
    if (fname.length() > 200) {
      fname = fname.substring(0, 200);
    }
    fname = fname.replace('\\', '_').replace('/', '_').replace(':', '_').replace(' ', '_');
    fname += ".zip";
    return db.getMp3Zip(tracks, fname);
  }

  public String getImageFront()
  {
    return get(COVERFRONT);
  }

  public String getImageBack()
  {
    return get(COVERBACK);
  }

  public String getJpcId()
  {
    String imagePk = get(JPCID);
    if (StringUtils.contains(imagePk, "e+") == true) {
      Double l = NumberUtils.toDouble(imagePk);
      imagePk = Long.toString(l.longValue());
    }
    return imagePk;
  }

  public boolean ignoreForRogIpod()
  {
    return StringUtils.equals(get(IGNORE_ROGIPOD), "-1");
  }
}
