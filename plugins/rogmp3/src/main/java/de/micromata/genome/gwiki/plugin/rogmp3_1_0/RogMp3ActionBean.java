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

import static de.micromata.genome.util.xml.xmlbuilder.Xml.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import de.micromata.genome.gwiki.model.logging.GWikiLog;
import de.micromata.genome.gwiki.model.logging.GWikiLogAttributeType;
import de.micromata.genome.gwiki.model.logging.GWikiLogCategory;
import de.micromata.genome.gwiki.page.impl.actionbean.ActionBeanBase;
import de.micromata.genome.gwiki.utils.WebUtils;
import de.micromata.genome.logging.GLog;
import de.micromata.genome.logging.LogAttribute;
import de.micromata.genome.util.types.Pair;
import de.micromata.genome.util.xml.xmlbuilder.XmlElement;
import de.micromata.genome.util.xml.xmlbuilder.html.Html;

public class RogMp3ActionBean extends ActionBeanBase
{

  Mp3Db db;

  // private String mode;
  /**
   * search for composer
   */
  private String komp;

  /**
   * pk
   */
  private String tit;

  private int no = 0;

  private String trackPk;

  private String mediaPk;

  private String interpretPk;

  private String orchesterPk;

  private String dirigentPk;

  /**
   * Booklet
   */
  private String imagePk;

  private String jpcImagePk;

  private String naxosImagePk;

  private boolean imageBack = false;

  private boolean serveMp3;

  private boolean downloadMp3;

  private String showT;

  private String showKomposerMedias;

  /**
   * Index number of field
   */
  private String sort;

  private boolean desc;

  private String country;

  /**
   * Upuse or downuase
   */
  private String upUse;

  private boolean upUseOnServeFile = false;

  private String localUrl()
  {
    return wikiContext.localUrl(wikiContext.getCurrentElement().getElementInfo().getId());
  }

  private String getDbPath()
  {
    return wikiContext.getCurrentElement().getElementInfo().getProps().getStringValue("DBPath");
  }

  private String getMp3Root()
  {
    return wikiContext.getCurrentElement().getElementInfo().getProps().getStringValue("MP3Root");
  }

  private void init()
  {
    db = Mp3Db.get(getDbPath(), getMp3Root());

    Html5Player.renderAudioJs(wikiContext, mediaPk, tit, trackPk);
    String playcss = "ol { padding: 0px; margin: 0px; list-style: decimal-leading-zero inside; color: #ccc; width: 1200px; border-top: 1px solid #ccc; font-size: 0.9em; }\r\n"
        + "      ol li { position: relative; margin: 0px; padding: 9px 2px 10px; border-bottom: 1px solid #ccc; cursor: pointer; }\r\n"
        + "      ol li a { display: block; text-indent: -3.3ex; padding: 0px 0px 0px 20px; }\r\n"
        + "      li.playing { color: #aaa; text-shadow: 1px 1px 0px rgba(255, 255, 255, 0.3); }\r\n"
        + "      li.playing a { color: #000; }\r\n"
        + "      li.playing:before { content: '♬'; width: 14px; height: 14px; padding: 3px; line-height: 14px; margin: 0px; position: absolute; left: -24px; top: 9px; color: #000; font-size: 13px; text-shadow: 1px 1px 0px rgba(0, 0, 0, 0.2); }";

    String css = " <style type=\"text/css\">\n"
        + playcss
        // + ".playing { color: purple;\n" + " background-color: #d8da3d \n" + "}\n" //
        + "</style>\n";
    wikiContext.addHeaderContent(css);

  }

  @Override
  public Object onInit()
  {
    init();
    if (StringUtils.isBlank(imagePk) == false || StringUtils.isBlank(jpcImagePk) == false
        || StringUtils.isBlank(naxosImagePk) == false) {
      return onServeCover();
    }
    if (serveMp3 == true) {
      if (StringUtils.isNotBlank(trackPk) == true) {
        return onServeTrackMp3();

      } else if (StringUtils.isNotBlank(tit) == true) {
        return onServeTitleMp3();
      }
    }
    if (serveM3u == true) {
      return onServeM3u();
    }

    if (downloadMp3 == true) {
      if (StringUtils.isNotBlank(tit) == true) {
        return onDownloadTitleMp3();
      } else if (StringUtils.isNotBlank(mediaPk) == true) {
        return onDownloadMediaMp3();
      }
    }
    return super.onInit();
  }

  private Object onDownloadTitleMp3()
  {
    Title title = db.getTitleByPk(tit);
    Pair<String, byte[]> zip = title.getMp3Zip();
    GLog.note(GWikiLogCategory.Wiki,
        "RogMp3; Download title: " + title.getComposer().getNameOnFs() + "/" + title.getNameOnFs());
    serveFile(zip.getSecond(), zip.getFirst(), "application/zip");
    return noForward();
  }

  private Object onDownloadMediaMp3()
  {
    Media media = db.getMediaByPk(mediaPk);
    GLog.note(GWikiLogCategory.Wiki, "RogMp3; Download media: " + media.getDetailDescription(localUrl()),
        new LogAttribute(GWikiLogAttributeType.RemoteHost,
            wikiContext.getRequest().getRemoteHost()));
    Pair<String, byte[]> zip = media.getMp3Zip();
    serveFile(zip.getSecond(), zip.getFirst(), "application/zip");
    return noForward();
  }

  private void serveFile(byte[] data, String name, String contentType)
  {
    GLog.note(GWikiLogCategory.Wiki, "RogMp3; datafile: " + name);
    try {
      wikiContext.getResponse().setContentType(contentType);
      wikiContext.getResponse().addHeader("Content-Disposition", "attachment; filename=\"" + name + "\"");
      wikiContext.getResponse().setContentLength(data.length);
      OutputStream os = wikiContext.getResponse().getOutputStream();
      IOUtils.copy(new ByteArrayInputStream(data), os);
    } catch (IOException ex) {
      GLog.note(GWikiLogCategory.Wiki, "Cannot serve  file: " + name + ": " + ex.toString());
    }
  }

  private void serveFile(File f, String contentType)
  {
    GLog.note(GWikiLogCategory.Wiki, "RogMp3; serveFile: " + f.getAbsolutePath());
    try {
      byte[] data = FileUtils.readFileToByteArray(f);
      serveFile(data, f.getName(), contentType);
    } catch (IOException e) {
      GLog.note(GWikiLogCategory.Wiki, "Cannot read  file: " + f.getAbsolutePath() + ": " + e.toString());
    }
  }

  private Object onServeCover()
  {
    File f = null;
    if (StringUtils.isNotBlank(imagePk) == true) {
      f = db.getBookletFile(imagePk, imageBack);

    }
    if (StringUtils.isNotBlank(jpcImagePk) == true) {
      f = db.getJpcBookletFile(jpcImagePk, imageBack);
    }
    if (StringUtils.isNotBlank(naxosImagePk) == true) {
      f = db.getNaxosImage(naxosImagePk);
    }
    if (f == null) {
      return super.onInit();
    }
    serveFile(f, "image/jpeg");
    return noForward();
  }

  File getMp3RootFile()
  {
    File dpath = new File(getMp3Root());
    if (dpath.exists() == false) {
      wikiContext.addSimpleValidationError("Cannot find mp3root: " + dpath.getAbsolutePath());
      return null;
    }
    return dpath;
  }

  public File getComposerFile(Composer composer, File mp3root)
  {
    File compName = new File(mp3root, composer.getNameOnFs());
    if (compName.exists() == false) {
      wikiContext.addSimpleValidationError("Cannot find compName: " + compName.getAbsolutePath());
      return null;
    }
    return compName;
  }

  public File getTitleFile(Title title, File composerFile)
  {
    File titpath = new File(composerFile, title.getNameOnFs());
    if (titpath.exists() == false) {
      wikiContext.addSimpleValidationError("Cannot find titpath: " + titpath.getAbsolutePath());
      return null;
    }
    return titpath;
  }

  public File getTrackFile(Track track, File titleFile)
  {
    File titpath = new File(titleFile, track.getNameOnFs());
    if (titpath.exists() == false) {
      wikiContext.addSimpleValidationError("Cannot find trackpath: " + titpath.getAbsolutePath());
      return null;
    }
    return titpath;
  }

  public Object onServeTitleMp3()
  {
    if (true) {
      return onServiceTitleMp3Merged();
    }
    Title title = db.getTitleByPk(tit);
    List<Track> tracks = title.getTracks();
    if (tracks.size() > no) {
      Track track = tracks.get(no);
      serveMp3File(track.getMp3Path(), track.getPk());
      return noForward();
    }
    return super.onInit();
  }

  public Object onServiceTitleMp3Merged()
  {
    Title title = db.getTitleByPk(tit);
    List<Track> tracks = title.getTracks();
    String userName = getWikiContext().getWikiWeb().getAuthorization().getCurrentUserName(getWikiContext());
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    try {
      for (Track track : tracks) {
        byte[] trdata = FileUtils.readFileToByteArray(track.getMp3Path());
        bout.write(trdata);
        if (upUseOnServeFile == true) {
          db.getUsageDb().addListen(userName, track.getPk(), tit, mediaPk);
        }
      }
      serveFile(bout.toByteArray(), title.getNameOnFs() + ".mp3", "audio/mpeg");
    } catch (IOException ex) {
      wikiContext.addSimpleValidationError("Cannot find track: " + ex.getMessage());
      return super.onInit();
    }
    // db.getUsageDb().addListen(userName, trackPk, tit, mediaPk);
    return noForward();
  }

  public Object onServeM3u()
  {
    List<Track> tracks = null;
    if (StringUtils.isNotBlank(tit) == true) {
      Title title = db.getTitleByPk(tit);
      tracks = title.getTracks();
    } else if (StringUtils.isNotBlank(mediaPk) == true) {
      Media media = db.getMediaByPk(mediaPk);
      tracks = media.getTrackList();
    }
    if (tracks == null) {
      GWikiLog.warn("Cannot find Tracks for m3u");
      return noForward();
    }
    StringBuilder sb = new StringBuilder();
    sb.append("#EXTM3U\n\n");
    for (Track track : tracks) {
      Title title = track.getTitle();
      String compname = title.getComposerName();
      String surl = wikiContext.globalUrl(wikiContext.getCurrentElement().getElementInfo().getId())
          + ";jsessionid="
          + wikiContext.getRequest().getSession().getId()
          + "?serveMp3=true&trackPk="
          + track.getPk();
      sb.append("#EXTINF:-1," + compname + " - " + title.getTitleName() + " - " + track.getName() + ":\n");
      sb.append(surl).append("\n");
    }
    wikiContext.getResponse().setContentType("audio/x-mpegurl   m3u");
    try {
      wikiContext.getResponse().getWriter().print(sb.toString());
    } catch (IOException ex) {
      GWikiLog.warn("Error writing m3u: " + ex.toString(), ex);
    }
    return noForward();
  }

  public Object onServeTrackMp3()
  {

    Track track = db.getTrackByPk(trackPk);
    if (track == null) {
      wikiContext.addSimpleValidationError("Cannot find track: " + trackPk);
      return super.onInit();
    }

    serveMp3File(track.getMp3Path(), trackPk);
    return noForward();
  }

  private void serveMp3File(File trackPath, String trackPk)
  {
    serveFile(trackPath, "audio/mpeg");
    if (upUseOnServeFile == true) {
      String userName = getWikiContext().getWikiWeb().getAuthorization().getCurrentUserName(getWikiContext());
      db.getUsageDb().addListen(userName, trackPk, tit, mediaPk);
    }
  }

  public void renderHeader()
  {
    XmlElement node = element("div", "name", "x");
    node.add(Html.a(attrs("href", localUrl() + "?showT=composers"), text("Komponisten")));
    node.add(Html.a(attrs("href", localUrl() + "?showT=timeline"), text("Timeline")));
    node.add(Html.a(attrs("href", localUrl() + "?showT=media"), text("Medien")));
    node.add(Html.a(attrs("href", localUrl() + "?showT=orchester"), text("Orchester")));
    node.add(Html.a(attrs("href", localUrl() + "?showT=dirigent"), text("Dirigent")));
    node.add(Html.a(attrs("href", localUrl() + "?showT=interpret"), text("Interpreten")));
    node.add(Html.a(attrs("href", localUrl() + "?showT=stats"), text("Statistiken")));

    node.add(code("<hr>\n"));
    wikiContext.append(node.toString());
  }

  public void renderContent()
  {
    renderHeader();
    if (StringUtils.isBlank(tit) == false) {
      renderTitel();
    } else if (StringUtils.isBlank(mediaPk) == false) {
      renderMedia();
    } else if (StringUtils.isBlank(komp) == false) {
      renderKomposer();
    } else if (StringUtils.isNotBlank(orchesterPk) == true) {
      renderOrchester();
    } else if (StringUtils.isNotEmpty(dirigentPk) == true) {
      renderDirigent();
    } else if (StringUtils.isNotEmpty(interpretPk) == true) {
      renderInterpret();
    } else {
      if (StringUtils.equals(showT, "media") == true) {
        renderMediaList();
      } else if (StringUtils.equals(showT, "timeline") == true) {
        renderTimeline();
      } else if (StringUtils.equals(showT, "orchester") == true) {
        renderOrchesterList();
      } else if (StringUtils.equals(showT, "interpret") == true) {
        renderInterpretList();
      } else if (StringUtils.equals(showT, "dirigent") == true) {
        renderDirigentList();
      } else if (StringUtils.equals(showT, "stats") == true) {
        renderStatistics();
      } else {
        renderKomposers();
      }
    }
  }

  private void renderDirigentList()
  {
    XmlElement node = element("div", "name", "x");
    node.add(element("H1", text("Dirigenten")));
    List<Dirigent> dirigents = db.getDirigenten();
    node.add(code("<ul>"));
    for (Dirigent dirigent : dirigents) {

      node.add(Html.li( //
          Html.a(attrs("href", localUrl() + "?dirigentPk=" + WebUtils.encodeUrlParam(dirigent.getPk())),
              text(dirigent.getName()))));
    }
    node.add(code("</ul>"));
    wikiContext.append(node.toString());

  }

  private void renderDirigent()
  {
    Dirigent dirigent = db.getDirigentByPk(this.dirigentPk);
    List<Title> titles = db.getTitlesByDirigent(dirigent);
    // TODO render orchester
    XmlElement node = element("div", "name", "x");
    node.add(element("H1", text(dirigent.getName())));
    node.add(code("<ul>"));
    for (Title titel : titles) {

      node.add(Html.li( //
          Html.a(attrs("href", localUrl() + "?tit=" + WebUtils.encodeUrlParam(titel.getPk())),
              text(titel.getComposerName() + ": " + titel.getTitleName()))));
    }
    node.add(code("</ul>"));
    wikiContext.append(node.toString());
  }

  private void renderOrchesterList()
  {
    XmlElement node = element("div", "name", "x");
    node.add(element("H1", text("Orchester")));
    List<Orchester> orchs = db.getOrchester();
    node.add(code("<ul>"));
    for (Orchester orch : orchs) {

      node.add(Html.li( //
          Html.a(attrs("href", localUrl() + "?orchesterPk=" + WebUtils.encodeUrlParam(orch.getPk())),
              text(orch.getName()))));
    }
    node.add(code("</ul>"));
    wikiContext.append(node.toString());
  }

  private void renderOrchester()
  {
    Orchester orchester = db.getOrchesterByPk(this.orchesterPk);
    List<Title> titles = orchester.getTitles();
    // TODO render orchester
    XmlElement node = element("div", "name", "x");
    node.add(element("H1", text(orchester.getName())));
    node.add(code("<ul>"));
    for (Title titel : titles) {

      node.add(Html.li( //
          Html.a(attrs("href", localUrl() + "?tit=" + WebUtils.encodeUrlParam(titel.getPk())),
              text(titel.getComposerName() + ": " + titel.getTitleName()))));
    }
    node.add(code("</ul>"));
    wikiContext.append(node.toString());
  }

  private void renderInterpretList()
  {
    XmlElement node = element("div", "name", "x");
    node.add(element("H1", text("Interpreten")));
    List<Interpret> orchs = db.getInterpreten();
    node.add(code("<ul>"));
    for (Interpret orch : orchs) {

      node.add(Html.li( //
          Html.a(attrs("href", localUrl() + "?interpretPk=" + WebUtils.encodeUrlParam(orch.getPk())),
              text(orch.getName() + ": " + orch.getInstrument()))));
    }
    node.add(code("</ul>"));
    wikiContext.append(node.toString());

  }

  private void renderInterpret()
  {
    Interpret interpret = db.getInterpretByPk(this.interpretPk);
    List<Title> titles = interpret.getTitles();
    // TODO render orchester
    XmlElement node = element("div", "name", "x");
    node.add(element("H1", text(interpret.getName())));
    node.add(code("<ul>"));
    for (Title titel : titles) {

      node.add(Html.li( //
          Html.a(attrs("href", localUrl() + "?tit=" + WebUtils.encodeUrlParam(titel.getPk())),
              text(titel.getComposerName() + ": " + titel.getTitleName()))));
    }
    node.add(code("</ul>"));
    wikiContext.append(node.toString());

  }

  private XmlElement renderMediaLink(Media media)
  {
    return Html.a(attrs("href", localUrl() + "?mediaPk=" + WebUtils.encodeUrlParam(media.getPk())),
        text(media.getListName()));
  }

  private void renderMediaList()
  {
    XmlElement node = element("div", "name", "x");
    node.add(element("H1", text("Medien")));
    node.add(Html.p( //
        Html.a(attrs("href", localUrl() + "?showT=media"), text("Name")), //
        text(" |"), //
        Html.a(attrs("href", localUrl() + "?showT=media&sort=" + Media.DATE_INDB + "&desc=" + (desc == false)),
            text("Date")), //
        text(" |"), //
        Html.a(attrs("href", localUrl() + "?showT=media&sort=-1&desc=" + (desc == false)), text("Usage Count")), //
        text(" |"), //
        Html.a(attrs("href", localUrl() + "?showT=media&sort=-2&desc=" + (desc == false)), text("Usage Date")) //
    ));

    List<Media> medias = db.getMedia();
    if (NumberUtils.isNumber(sort) == true) {
      int isort = NumberUtils.toInt(sort);
      if (isort == -1) {
        Track.sortByUsage(medias, desc);
      } else if (isort == -2) {
        Track.sortByDate(medias, desc);
      } else {
        RecBase.sortByIdx(medias, isort, desc);
      }
    }
    node.add(code("<ul>"));
    for (Media m : medias) {
      node.add(Html.li(renderMediaLink(m)));
    }
    node.add(code("</ul>"));
    wikiContext.append(node.toString());
  }

  private void renderMediaImages(XmlElement node, Media media)
  {
    String imf = media.getImageFront();
    if (StringUtils.isNotBlank(imf) == true) {
      node.add(Html.img("src", localUrl() + "?imagePk=" + imf + "&imageBack=false"));
    }
    imf = media.getImageBack();
    if (StringUtils.isNotBlank(imf) == true) {
      node.add(Html.img("src", localUrl() + "?imagePk=" + imf + "&imageBack=true"));
    }
    String jpcid = media.getJpcId();
    if (StringUtils.isNotBlank(jpcid) == true) {
      if (db.getJpcBookletFile(jpcid, false) != null) {
        node.add(Html.img("src", localUrl() + "?jpcImagePk=" + jpcid + "&imageBack=false"));
      }
      if (db.getJpcBookletFile(jpcid, true) != null) {
        node.add(Html.img("src", localUrl() + "?jpcImagePk=" + jpcid + "&imageBack=true"));
      }
    }
    if (db.getNaxosImage(media) != null) {
      node.add(Html.img("src", localUrl() + "?naxosImagePk=" + media.get(Media.LABELID) + "&imageBack=false"));
    }
  }

  private void renderMedia()
  {
    Media media = db.getMediaByPk(mediaPk);

    if (StringUtils.equals(upUse, "true") == true) {
      db.getUsageDb().upUse(db, null, mediaPk, true);
    } else if (StringUtils.equals(upUse, "false") == true) {
      db.getUsageDb().upUse(db, null, mediaPk, false);
    }

    XmlElement node = element("div", "name", "x");
    node.add(element("H1", text(media.getListName())));
    renderMediaImages(node, media);

    node.add(code("<br/>"));
    node.add(code(media.getDetailDescription(localUrl())));
    List<Title> titles = media.getTitleList();

    Set<String> composerPks = new HashSet<String>();

    for (Title tit : titles) {
      composerPks.add(tit.getComposer().getPk());
    }
    if (composerPks.size() == 1) {
      Composer composer = titles.get(0).getComposer();
      node.add(
          element("h4", Html.a(attrs("href", localUrl() + "?komp=" + WebUtils.encodeUrlParam(composer.getPk())),
              text(composer.getName()))))//
      ;
      node.add(element("p", code(composer.getDetailDescription())));
    }
    node.add(element("H3", text("Titel")));
    node.add(Html.p(Html.a(attrs("href", localUrl() + "?mediaPk=" + mediaPk + "&serveM3u=true"), text("M3U List"))));

    renderTitleList(node, titles, composerPks.size() > 1);

    node.add(element("H3", text("Tracks")));
    wikiContext.append(node.toString());
    renderTrackPlayList(media.getTrackList(), "mediaPk", mediaPk);
    node = Html.p(Html.a(attrs("href", localUrl() + "?mediaPk=" + mediaPk + "&downloadMp3=true"), text("Download")));
    wikiContext.append(node.toString());
    wikiContext.append(node.toString());
    GLog.note(GWikiLogCategory.Wiki, "RogMp3; Media: " + media.getDetailDescription(localUrl()));

  }

  private void renderTitleList(XmlElement node, List<Title> titels, boolean withComposer)
  {
    node.add(code("<ul>"));
    for (Title t : titels) {
      node.add(Html.li( //
          Html.a(attrs("href", localUrl() + "?tit=" + WebUtils.encodeUrlParam(t.getPk())),
              text(t.getTitleName() + Track.getUsageDisplay(t)))));
    }

    node.add(code("</ul>"));
  }

  private void renderKomposer()
  {
    Composer comp = db.getComposerByPk(komp);
    XmlElement node = element("div", "name", "x");
    node.add(element("H1", text(comp.getName())));
    node.add(element("p", code(comp.getDetailDescription())));
    node.add(
        Html.a(
            attrs("href", localUrl() + "?komp=" + WebUtils.encodeUrlParam(comp.getPk()) + "&showKomposerMedias=false"),
            text("Titel")));
    node.add(text(" "));
    node.add(
        Html.a(
            attrs("href", localUrl() + "?komp=" + WebUtils.encodeUrlParam(comp.getPk()) + "&showKomposerMedias=true"),
            text("Medien")));

    List<Title> titels = db.getTitelFromKomposer(comp.getName());
    if (StringUtils.equals(showKomposerMedias, "true") == true) {
      renderKomposerMedias(node, comp, titels);
    } else {
      renderTitleList(node, titels, false);
    }
    wikiContext.append(node.toString());
    GLog.note(GWikiLogCategory.Wiki, "RogMp3; composer: " + comp.getNameOnFs());
  }

  private void renderKomposerMedias(XmlElement node, Composer comp, List<Title> titels)
  {
    Map<String, Media> mediamap = new HashMap<String, Media>();
    for (Title tit : titels) {
      Media m = tit.getMedia();
      mediamap.put(m.getPk(), m);
    }
    List<Media> medias = new ArrayList<Media>();
    medias.addAll(mediamap.values());

    if (NumberUtils.isNumber(sort) == false) {
      sort = Integer.toString(Media.NAME1);
    }
    RecBase.sortByIdx(medias, NumberUtils.toInt(sort), desc);
    node.add(code("<ul>"));
    for (Media m : medias) {
      node.add(Html.li(renderMediaLink(m)));
    }
    node.add(code("</ul>"));
  }

  String getTrackUrl(Track track, String... addParams)
  {
    String url;
    if (track.isSynteticTrack() == true) {
      url = localUrl() + "?serveMp3=true&tit=" + track.getTitleFk() + "&no=" + track.getTrackNo();
    } else {
      url = localUrl() + "?serveMp3=true&trackPk=" + WebUtils.encodeUrlParam(track.getPk());
    }
    for (int i = 0; i < addParams.length; ++i) {
      String key = addParams[i];
      String value = addParams[++i];
      url += "&" + key + "=" + value;
    }
    return url;
  }

  private void renderTitel()
  {
    Title title = db.getTitleByPk(tit);

    if (StringUtils.equals(upUse, "true") == true) {
      db.getUsageDb().upUse(db, tit, null, true);
    } else if (StringUtils.equals(upUse, "false") == true) {
      db.getUsageDb().upUse(db, tit, null, false);
    }

    Composer composer = title.getComposer();
    XmlElement node = element("div", "name", "x");
    node.add(
        element("H2", Html.a(attrs("href", localUrl() + "?komp=" + WebUtils.encodeUrlParam(composer.getPk())),
            text(composer.getName()))))//
    ;

    node.add(element("H1", text(title.getTitleName())));

    node.add(code(title.getDetailDescription(localUrl())));
    Media media = title.getMedia();
    if (media != null) {

      node.nest(text("Media: "),
          Html.a(attrs("href", localUrl() + "?mediaPk=" + WebUtils.encodeUrlParam(media.getPk())),
              text(media.getListName())));
      node.add(code("<br/>"));
      renderMediaImages(node, media);
      node.add(code("<br/>"));
    }
    node.add(code("<br/><br/>"));
    node.add(Html.p(Html.a(attrs("href", localUrl() + "?tit=" + tit + "&serveM3u=true"), text("M3U List"))));

    wikiContext.append(node.toString());

    renderaudios(title);
    GLog.note(GWikiLogCategory.Wiki, "RogMp3; title: " + composer.getNameOnFs() + "/" + title.getNameOnFs());
    node = Html.p(Html.a(attrs("href", localUrl() + "?tit=" + WebUtils.encodeUrlParam(tit) + "&downloadMp3=true"),
        text("Download")));
    wikiContext.append(node.toString());

  }

  private void renderaudios(Title title)
  {
    StringBuilder sb = new StringBuilder();
    String url = localUrl() + "?serveMp3=true&tit=" + title.getPk();
    sb.append("Alles<br/><audio src=\""
        + url
        + "\" type=\"audio/mpeg\" id=\"titlplayer\" controls style=\"width: 800px;\"  preload=\"none\"></audio><p/>");
    wikiContext.append(sb.toString());
    List<Track> tracks = title.getTracks();
    renderTrackPlayList(tracks, "tit", title.getPk());
  }

  private void renderTrackPlayList(List<Track> tracks, String... addParams)
  {
    Html5Player.renderAudioPlayer(wikiContext);
    wikiContext.append("<ol>\n");
    for (Track track : tracks) {
      String url = getTrackUrl(track);
      Title t = track.getTitle();
      Composer c = t.getComposer();
      String n = esc(c.getName()) + ": " + esc(t.getTitleName());
      String text = track.getHtmlForList() + ": " + n;
      Html5Player.renderTrackLink(wikiContext, url, text);
      // sb.append(" <li><a href=\"#\" data-src=\"" + url +
      // "\">").append(track.getHtmlForList()).append(": ").append(n).append("</a></li>\n");
    }
    wikiContext.append("</ol>");
    // wikiContext.append(code);

    String aftercode = "    <div id=\"shortcuts\">\r\n"
        + "      <div>\r\n"
        + "        <h1>Keyboard shortcuts:</h1>\r\n"
        + "        <p><em>&rarr;</em> Next track</p>\r\n"
        + "        <p><em>&larr;</em> Previous track</p>\r\n"
        + "        <p><em>Space</em> Play/pause</p>\r\n"
        + "      </div>\r\n"
        + "    </div>";
    wikiContext.append(aftercode);

  }

  private void renderTimeline()
  {
    Map<Integer, TimeLineEl> tl = db.getComposerTimeLine(country);
    XmlElement node = Html.div("none", "none");
    node.add(element("H1", text("Timeline")));

    XmlElement countryP = Html.p(Html.a(attrs("href", localUrl() + "?showT=timeline&country="), code("All")));

    for (Map.Entry<String, Integer> me : db.getComposersCountries().entrySet()) {
      countryP.add(code(" "));
      countryP.add(Html.a(attrs("href", localUrl() + "?showT=timeline&country=" + WebUtils.encodeUrlParam(me.getKey())),
          text(me.getKey() + " (" + me.getValue() + ")")));
    }
    node.add(countryP);
    XmlElement table = Html.table(attrs("border", "1"));
    table.add(Html.tr(Html.th(text("Jahr")), Html.th(text("Geboren")), Html.th(text("Gestorben"))));
    for (TimeLineEl te : tl.values()) {
      XmlElement born = Html.td(attrs());
      for (Composer comp : te.start) {
        if (born.getChilds().isEmpty() == false) {
          born.add(code("<br/>"));
        }
        born.add(Html.a(attrs("href", localUrl() + "?komp=" + WebUtils.encodeUrlParam(comp.getPk())),
            text(comp.getListDescription())));
      }
      XmlElement died = Html.td(attrs());
      for (Composer comp : te.end) {
        if (died.getChilds().isEmpty() == false) {
          died.add(code("<br/>"));
        }
        died.add(Html.a(attrs("href", localUrl() + "?komp=" + WebUtils.encodeUrlParam(comp.getPk())),
            text(comp.getListDescription())));
      }
      table.nest(Html.tr(//
          Html.td(text(Integer.toString(te.year))), //
          born, //
          died //
      ));
    }
    node.add(table);
    wikiContext.append(node.toString());

  }

  private void renderStatistics()
  {
    XmlElement node = Html.div("none", "none");
    Mp3Stats.genStatistics(db, node);
    String b = node.toString();

    wikiContext.append(b);
  }

  private void renderKomposers()
  {
    XmlElement node = Html.div("none", "none");
    node.add(element("H1", text("Komponisten")));

    node.add(Html.br());
    // XmlElement node = Html.p(text("Komposers:")).add(Html.br());
    node.add(code("<ul>"));
    for (Composer c : db.getComposers()) {
      node.add(Html.li( //
          Html.a(attrs("href", localUrl() + "?komp=" + WebUtils.encodeUrlParam(c.getPk())),
              text(c.getListDescription()))));
    }
    node.add(code("</ul>"));
    String b = node.toString();
    wikiContext.append(b);
  }

  public String getKomp()
  {
    return komp;
  }

  public void setKomp(String komposer)
  {
    this.komp = komposer;
  }

  public String getTit()
  {
    return tit;
  }

  public void setTit(String titel)
  {
    this.tit = titel;
  }

  public String getTrackPk()
  {
    return trackPk;
  }

  public void setTrackPk(String trackPk)
  {
    this.trackPk = trackPk;
  }

  public boolean isServeMp3()
  {
    return serveMp3;
  }

  public void setServeMp3(boolean serveMp3)
  {
    this.serveMp3 = serveMp3;
  }

  public int getNo()
  {
    return no;
  }

  public void setNo(int no)
  {
    this.no = no;
  }

  public String getShowT()
  {
    return showT;
  }

  public void setShowT(String showT)
  {
    this.showT = showT;
  }

  public String getMediaPk()
  {
    return mediaPk;
  }

  public void setMediaPk(String mediaPk)
  {
    this.mediaPk = mediaPk;
  }

  public String getImagePk()
  {
    return imagePk;
  }

  public void setImagePk(String imagePk)
  {
    this.imagePk = imagePk;
  }

  public boolean isImageBack()
  {
    return imageBack;
  }

  public void setImageBack(boolean imageBack)
  {
    this.imageBack = imageBack;
  }

  public boolean isDownloadMp3()
  {
    return downloadMp3;
  }

  public void setDownloadMp3(boolean downloadMp3)
  {
    this.downloadMp3 = downloadMp3;
  }

  public String getSort()
  {
    return sort;
  }

  public void setSort(String sort)
  {
    this.sort = sort;
  }

  public boolean isDesc()
  {
    return desc;
  }

  public void setDesc(boolean desc)
  {
    this.desc = desc;
  }

  public String getJpcImagePk()
  {
    return jpcImagePk;
  }

  public void setJpcImagePk(String jpcImagePk)
  {
    this.jpcImagePk = jpcImagePk;
  }

  public String getNaxosImagePk()
  {
    return naxosImagePk;
  }

  public void setNaxosImagePk(String naxosImagePk)
  {
    this.naxosImagePk = naxosImagePk;
  }

  public String getInterpretPk()
  {
    return interpretPk;
  }

  public void setInterpretPk(String interpretPk)
  {
    this.interpretPk = interpretPk;
  }

  public String getOrchesterPk()
  {
    return orchesterPk;
  }

  public void setOrchesterPk(String orchesterPk)
  {
    this.orchesterPk = orchesterPk;
  }

  public String getDirigentPk()
  {
    return dirigentPk;
  }

  public void setDirigentPk(String dirigentPk)
  {
    this.dirigentPk = dirigentPk;
  }

  public String getCountry()
  {
    return country;
  }

  public void setCountry(String country)
  {
    this.country = country;
  }

  public String getShowKomposerMedias()
  {
    return showKomposerMedias;
  }

  public void setShowKomposerMedias(String showKomposerMedias)
  {
    this.showKomposerMedias = showKomposerMedias;
  }

  public String getUpUse()
  {
    return upUse;
  }

  public void setUpUse(String upUse)
  {
    this.upUse = upUse;
  }

  private boolean serveM3u;

  public boolean isServeM3u()
  {
    return serveM3u;
  }

  public void setServeM3u(boolean serveM3u)
  {
    this.serveM3u = serveM3u;
  }
}
