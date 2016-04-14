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

import java.io.File;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gwiki.utils.WebUtils;
import de.micromata.genome.util.types.Converter;
import de.micromata.genome.util.types.Pair;
import de.micromata.genome.util.xml.xmlbuilder.Xml;
import de.micromata.genome.util.xml.xmlbuilder.html.Html;

public class Title extends RecBase implements EntityWithTracks
{
  public static final int PK = 0;

  public static final int KOMPOSER = 1;

  public static final int TITEL = 2;

  public static final int TNUMMER = 3;

  public static final int EINSPIELUNG = 4;

  public static final int TONART = 5;

  public static final int OP = 6;

  public static final int TITEL2 = 7;

  public static final int GRUPPE = 8;

  public static final int YEAR = 9;

  public static final int RECORDTYPE = 10;

  public static final int RECORDYEAR = 13;

  public static final int INTERPRETERLIST = 14;

  public static final int ORCHESTER = 15;

  public static final int DIRIGENT = 16;

  public static final int MEDIA_FK = 23;

  public static final int MEDIA_TNUMMER = 24;

  public static final int NAMEONCD = 26;

  public static final int DURATION = 28;

  public static final int IGNORE_ROGIPOD = 31;

  private List<Track> tracks = null;

  public Title(Mp3Db db, String[] rec)
  {
    super(db, rec);

  }

  public List<Track> getTracks()
  {
    if (tracks != null) {
      return tracks;
    }
    return tracks = db.getTracksFromTitle(this);
  }

  public float getUsageCount()
  {
    return Track.getUsageCount(getTracks());
  }

  public Date getLastUsage()
  {
    return Track.getLastUsage(getTracks());
  }

  public static String makeTitelName(String[] rec)
  {
    StringBuilder sb = new StringBuilder();
    sb.append(rec[TITEL]);
    if (isRecFilled(rec, TNUMMER) == true) {
      sb.append(" ").append(rec[TNUMMER]);
    }
    if (isRecFilled(rec, TONART) == true) {
      sb.append(" in ").append(rec[TONART]);
    }

    if (isRecFilled(rec, OP) == true) {
      sb.append(" Op.").append(rec[OP]);
    }
    if (isRecFilled(rec, EINSPIELUNG) == true) {
      sb.append(" E").append(rec[EINSPIELUNG]);
    }
    return sb.toString();
  }

  public String getComposerName()
  {
    return get(KOMPOSER);
  }

  public Composer getComposer()
  {
    return db.getComposerByName(getComposerName());
  }

  public Media getMedia()
  {
    String mediaFk = get(MEDIA_FK);
    if (StringUtils.isBlank(mediaFk) == true) {
      return null;
    }
    return db.getMediaByPk(mediaFk);
  }

  public String getNameOnFs()
  {
    String n = get(NAMEONCD);
    if (StringUtils.isBlank(n) == false) {
      return n;
    }
    return normalize(getTitleName());

  }

  public File getMp3Path()
  {
    if (mp3path != null) {
      return mp3path;
    }
    Composer comp = getComposer();
    mp3path = new File(comp.getMp3Path(), getNameOnFs());
    return mp3path;
  }

  public String getTitleName()
  {
    return makeTitelName(rec);
  }

  public String getDetailDescription(String localUrl)
  {
    StringBuilder sb = new StringBuilder();
    String s = get(TITEL2);
    if (StringUtils.isNotEmpty(s) == true) {
      sb.append("<b>").append(esc(s)).append("</b><br/>");
    }
    sb.append(renderDetailRec("Opus: ", OP));
    sb.append(renderDetailRec("Tonart: ", TONART));
    sb.append(renderDetailRec("Einspielung: ", EINSPIELUNG));
    sb.append(renderDetailRec("Spieldauer: ", DURATION));

    sb.append(renderDetailRec("Jahr: ", YEAR));

    List<Interpret> interpretList = db.getInterpretByTitel(getPk());
    if (interpretList.isEmpty() == false) {
      sb.append("Interpreten: ");
      for (Interpret ip : interpretList) {
        sb.append("<a href=\"" + localUrl + "?interpretPk=" + ip.getPk() + "\">")
            .append(StringEscapeUtils.escapeHtml(ip.getName() + ": " + ip.getInstrument())).append("</a> ");
      }
      sb.append("<br/>");
    } else {
      sb.append(renderDetailRec("Interpreten: ", INTERPRETERLIST));
    }
    List<Orchester> orchlist = db.getOrchesterByTitel(getPk());
    if (orchlist.isEmpty() == false) {
      sb.append("Orchester: ");
      for (Orchester orch : orchlist) {
        sb.append("<a href=\"" + localUrl + "?orchesterPk=" + orch.getPk() + "\">").append(StringEscapeUtils.escapeHtml(orch.getName()))
            .append("</a> ");
      }
      sb.append("<br/>");
    } else {
      sb.append(renderDetailRec("Orchester: ", ORCHESTER));
    }
    String dirigentName = get(DIRIGENT);
    if (StringUtils.isNotBlank(dirigentName) == true) {
      List<String[]> dirig = db.dirigents.findEquals(1, dirigentName);
      if (dirig.isEmpty() == false) {
        sb.append("Dirigent: ");
        for (String[] sa : dirig) {
          sb.append("<a href=\"" + localUrl + "?dirigentPk=" + sa[0] + "\">").append(StringEscapeUtils.escapeHtml(sa[1])).append("</a> ");
        }
        sb.append("<br/>");
      } else {
        sb.append(renderDetailRec("Dirigent: ", DIRIGENT));
      }
    }
    sb.append(renderDetailRec("Gruppe: ", GRUPPE));
    sb.append(renderDetailRec("Aufnahmetyp: ", RECORDTYPE));
    sb.append(renderDetailRec("Aufnahmejahr: ", RECORDYEAR));
    sb.append(renderDetailRec("Store2: ", IGNORE_ROGIPOD));
    sb.append("Last heared: ");
    sb.append((int) getUsageCount()).append("|").append(Converter.formatByIsoDayFormat(getLastUsage()));
    sb.append(Html.a(Xml.attrs("href", localUrl + "?tit=" + WebUtils.encodeUrlParam(getPk()) + "&upUse=true"), text("Up")).toString());
    sb.append(" ");
    sb.append(Html.a(Xml.attrs("href", localUrl + "?tit=" + WebUtils.encodeUrlParam(getPk()) + "&upUse=false"), text("Down")).toString());
    sb.append("<br/>\n");
    return sb.toString();
  }

  public Pair<String, byte[]> getMp3Zip()
  {
    List<Track> tracks = getTracks();
    String name = getComposer().getNameOnFs() + "_" + getNameOnFs() + ".zip";
    return db.getMp3Zip(tracks, name);
  }

  public String getPk()
  {
    return get(PK);
  }

  public boolean ignoreForRogIpod()
  {
    return StringUtils.equals(get(IGNORE_ROGIPOD), "-1");
  }
}
