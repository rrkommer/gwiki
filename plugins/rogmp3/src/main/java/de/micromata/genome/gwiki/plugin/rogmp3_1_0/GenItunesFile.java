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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import de.micromata.genome.util.runtime.RuntimeIOException;

/**
 * Generate xml
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 * 
 */
public class GenItunesFile
{
  private static final Logger log = Logger.getLogger(GenItunesFile.class);

  private File outFile;

  Mp3Db db;

  FileOutputStream fos;

  OutputStreamWriter sout;

  int cid = 1;

  private List<String> trackIds = new ArrayList<String>();

  private Map<String, String> mp3toituneTrackIds = new HashMap<String, String>();

  long sumMp3Size = 0;

  public GenItunesFile(Mp3Db db, String outFile)
  {
    this.db = db;
    this.outFile = new File(outFile);
  }

  private String genPersId()
  {
    return "";
  }

  public static final String T_TIMESTAMP = "yyyy-MM-dd'T'HH:mm:ssZ";

  private int getId()
  {
    return ++cid;
  }

  private String getIds()
  {
    return Integer.toString(getId());
  }

  private String getTime()
  {
    return new SimpleDateFormat(T_TIMESTAMP).format(new Date());
  }

  public void renderItunes()
  {
    try {
      render();
    } catch (IOException ex) {
      throw new RuntimeIOException(ex);
    }
  }

  private void render() throws IOException
  {
    fos = new FileOutputStream(outFile);
    BufferedOutputStream bufout = new BufferedOutputStream(fos, 10000000);
    sout = new OutputStreamWriter(bufout);

    sout.write(getHead());
    // genTracksForMedia();
    genAllTrack();
    sout.write(getFoot());

    sout.close();
    bufout.close();
    fos.close();
  }

  private GenItunesFile a(String s) throws IOException
  {
    sout.write(s);
    return this;
  }

  public static String esc(String s)
  {
    s = StringUtils.replace(s, "&", "&amp;");
    s = StringUtils.replace(s, "<", "&lt;");
    s = StringUtils.replace(s, ">", "&gt;");
    // return StringEscapeUtils.escapeXml(s);
    return s;
  }

  private String getTitleArtists(Title title)
  {
    StringBuilder sb = new StringBuilder();
    String dir = title.get(Title.DIRIGENT);
    if (StringUtils.isNotEmpty(dir) == true) {
      sb.append("Dir: ").append(dir);
    }
    String orch = title.get(Title.ORCHESTER);
    if (StringUtils.isNotEmpty(orch) == true) {
      sb.append("Orch: ").append(orch);
    }
    String inter = title.get(Title.INTERPRETERLIST);
    if (StringUtils.isNotEmpty(inter) == true) {
      sb.append("Inter: ").append(inter);
    }
    return sb.toString();
  }

  private void genTracksForMedia() throws IOException
  {
    List<Media> medias = db.getMedia();
    List<Media> smedias = medias.subList(3, 100);
    List<Title> titles = new ArrayList<Title>();
    for (Media media : smedias) {
      List<Title> tl = media.getTitleList();
      titles.addAll(tl);
    }
    genTracks(titles, smedias);
    genPlayList(smedias);
  }

  private void genAllTrack() throws IOException
  {
    List<Title> titles = new ArrayList<Title>();
    for (Composer composer : db.getComposers()) {
      titles.addAll(db.getTitelFromKomposer(composer.getName()));
    }
    List<Media> medias = db.getMedia();
    genTracks(titles, medias);
    genPlayList(medias);
  }

  private void genTracks(List<Title> titles, List<Media> medias) throws IOException
  {
    a("  <key>Tracks</key>\n");
    a("   <dict>\n");
    for (Title title : titles) {
      if (title.ignoreForRogIpod() == true) {
        log.info("Ignore title: " + title.getComposerName() + ": " + title.getTitleName());
        continue;
      }
      Composer composer = title.getComposer();
      Media media = title.getMedia();
      if (media == null) {
        log.error("Titel without Media: " + title.getComposerName() + ": " + title.getTitleName());
      } else {
        if (media.ignoreForRogIpod() == true) {
          log.info("Ignore Media: " + title.getComposerName() + ": " + title.getTitleName());
          continue;
        }
      }
      String gruppe = title.get(Title.GRUPPE);
      if (StringUtils.isBlank(gruppe) == true) {
        gruppe = "Klassik";
      }
      String titleArt = getTitleArtists(title);
      String medianame = "";
      if (media != null) {
        medianame = media.getListName();
      }
      for (Track track : title.getTracks()) {

        String ids = getIds();
        trackIds.add(ids);
        mp3toituneTrackIds.put(track.getPk(), ids);
        File fn = track.getMp3Path();
        String abp = fn.getAbsolutePath();
        sumMp3Size += fn.length();
        String prf = "file://localhost/" + abp.replace('\\', '/');
        String addEntries =
        // "<key>Album Artist</key><string>" + esc(titleArt) + "</string>\r\n"
        "            <key>Composer</key><string>"
            + esc(titleArt)
            + "</string>\r\n"
            + "            <key>Grouping</key><string>"
            + esc(medianame)
            + "</string>\r\n"
            + "           <key>Compilation</key><false/>\n"
            + "            <key>Genre</key><string>"
            + esc(gruppe)
            + "</string>\n"
        // + "<key>Comments</key><string>Abels Kommentar</string>"
        ;
        String entry = //
        "          <key>"
            + ids
            + "</key>\r\n"
            + "        <dict>\r\n"
            + "            <key>Track ID</key><integer>"
            + ids
            + "</integer>\r\n"
            + "            <key>Name</key><string>"
            + esc(track.getName())
            + "</string>\r\n"
            + "            <key>Artist</key><string>"
            + esc(composer.getListDescription())
            + "</string>\r\n"
            + "            <key>Album</key><string>"
            + esc(title.getTitleName())
            + "</string>\r\n"
            + "            <key>Kind</key><string>MPEG-Audiodatei</string>\r\n"
            // + "            <key>Size</key><integer>2872183</integer>\r\n"
            // + "            <key>Total Time</key><integer>179435</integer>\r\n"
            // + "            <key>Track Number</key><integer>3</integer>\r\n"
            // + "            <key>Date Modified</key><date>2010-11-27T13:47:18Z</date>\r\n"
            // + "            <key>Date Added</key><date>2013-07-29T16:20:51Z</date>\r\n"
            // + "            <key>Bit Rate</key><integer>128</integer>\r\n"
            // + "            <key>Sample Rate</key><integer>44100</integer>\r\n"
            + "            <key>Persistent ID</key><string>"
            + genPersId()
            + "</string>\r\n"
            + "            <key>Track Type</key><string>File</string>\r\n"
            + "            <key>Location</key><string>"
            + prf
            + "</string>\r\n"
            + "            <key>File Folder Count</key><integer>-1</integer>\r\n"
            + "            <key>Library Folder Count</key><integer>-1</integer>\r\n"
            + addEntries
            + "        </dict>\n";

        ;
        a(entry);

      }
    }
    a("   </dict>\n");
  }

  private void genPlayList(List<Media> medias) throws IOException
  {
    String pls = //
    "      <key>Playlists</key>\r\n"
        + "    <array>\r\n"
        + "        <dict>\r\n"
        + "            <key>Name</key><string>Mediathek</string>\r\n"
        + "            <key>Master</key><true/>\r\n"
        + "            <key>Playlist ID</key><integer>"
        + getIds()
        + "</integer>\r\n"
        + "            <key>Playlist Persistent ID</key><string>"
        + genPersId()
        + "</string>\r\n"
        + "            <key>Visible</key><true/>\r\n"
        + "            <key>All Items</key><true/>\r\n"
        + "            <key>Playlist Items</key>\n"
        + "              <array>\n";

    a(pls);
    for (String tid : trackIds) {
      String ts = "                <dict>\r\n"
          + "                    <key>Track ID</key><integer>"
          + tid
          + "</integer>\r\n"
          + "                </dict>\n";

      a(ts);
    }
    a("              </array>\n" + "           </dict>\n");
    addMediaPlayLists(medias);
    a("          </array>");

  }

  private void addMediaPlayLists(List<Media> medias) throws IOException
  {
    int skipCount = 0;
    for (Media m : medias) {
      if (m.ignoreForRogIpod() == true) {
        continue;
      }
      List<Title> tl = m.getTitleList();
      List<Title> filtered = new ArrayList<Title>();
      for (Title tit : tl) {
        if (tit.ignoreForRogIpod() == false) {
          filtered.add(tit);
        }
      }

      if (filtered.size() == 1) {
        ++skipCount;
        continue;
      }
      List<Track> tracks = m.getTrackList();
      List<Track> filteredTracks = new ArrayList<Track>();
      for (Track tr : tracks) {
        Title title = tr.getTitle();
        if (title.ignoreForRogIpod() == false) {
          filteredTracks.add(tr);

        }
      }
      addMediaPlayList(m.getListName(), filteredTracks);
    }
    log.warn("Skipped " + skipCount + " medias because contains only one title");
  }

  private void addMediaPlayList(String name, List<Track> tracks) throws IOException
  {
    String pls = "        <dict>\r\n"
        + "            <key>Name</key><string>"
        + esc(name)
        + "</string>\r\n"
        + "            <key>Playlist ID</key><integer>"
        + getIds()
        + "</integer>\r\n"
        + "            <key>Playlist Persistent ID</key><string>"
        + genPersId()
        + "</string>\r\n"
        + "            <key>All Items</key><true/>\r\n"
        + "            <key>Playlist Items</key>\n"
        + "             <array>";

    a(pls);
    for (Track track : tracks) {
      String tid = mp3toituneTrackIds.get(track.getPk());
      if (tid == null) {
        log.warn("Cannot find itunes track id for Track " + track.getPk() + track.getName());
        continue;
      }
      String ts = "                <dict>\r\n"
          + "                    <key>Track ID</key><integer>"
          + tid
          + "</integer>\r\n"
          + "                </dict>\n";
      a(ts);
    }
    a("           </array>\n" //
        + "        </dict>\n");
  }

  public String getHead()
  {
    String head = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
        + "<!DOCTYPE plist PUBLIC \"-//Apple Computer//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\r\n"
        + "<plist version=\"1.0\">\r\n"
        + "<dict>\r\n"
        + "    <key>Major Version</key><integer>1</integer>\r\n"
        + "    <key>Minor Version</key><integer>1</integer>\r\n"
        + "    <key>Date</key><date>"
        + getTime()
        + "</date>\r\n"
        + "    <key>Application Version</key><string>10.7</string>\r\n"
        + "    <key>Features</key><integer>5</integer>\r\n"
        + "    <key>Show Content Ratings</key><true/>\r\n"
        + "    <key>Music Folder</key><string>file://localhost/C:/Users/roger/Music/iTunes/iTunes%20Media/</string>\r\n"
        + "    <key>Library Persistent ID</key><string>"
        + genPersId()
        + "</string>\r\n";
    return head;
  }

  public String getFoot()
  {
    return "</dict>\r\n" + "</plist>";
  }

  public long getSumMp3Size()
  {
    return sumMp3Size;
  }

  public void setSumMp3Size(long sumMp3Size)
  {
    this.sumMp3Size = sumMp3Size;
  }
}
