package de.micromata.genome.gwiki.plugin.rogmp3_1_0;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.gdbfs.ZipWriteFileSystem;
import de.micromata.genome.gwiki.model.logging.GWikiLogCategory;
import de.micromata.genome.logging.GLog;
import de.micromata.genome.logging.LogExceptionAttribute;
import de.micromata.genome.util.types.Pair;

public class Mp3Db
{
  private static Mp3Db INSTANCE = null;

  private Mp3UsageDB usageDb;

  public static final Mp3Db get(String dbpath, String mp3root)
  {
    if (INSTANCE != null) {
      return INSTANCE;
    }
    INSTANCE = new Mp3Db(dbpath, mp3root);
    return INSTANCE;
  }

  private File dbpath;

  private File mp3root;

  CsvTable composers = new CsvTable();

  CsvTable title = new CsvTable();

  CsvTable tracks = new CsvTable();

  CsvTable medien = new CsvTable();

  CsvTable dirigents = new CsvTable();

  CsvTable interprets = new CsvTable();

  CsvTable interpretsDetail = new CsvTable();

  CsvTable orchesters = new CsvTable();

  CsvTable orchestersDetail = new CsvTable();

  public Mp3Db(String path, String mp3root)
  {
    dbpath = new File(path);
    this.mp3root = new File(mp3root);
    composers.load(new File(dbpath, "TabKomponist.csv"));
    composers.createIndex(Composer.NAME);
    title.load(new File(dbpath, "Titel.csv"));
    title.createIndex(Title.PK);
    title.createIndex(Title.MEDIA_FK);
    tracks.load(new File(dbpath, "TitelSub.csv"));
    tracks.createIndex(Track.PK);
    tracks.createIndex(Track.FK_TITLE);
    medien.load(new File(dbpath, "TabMedien.csv"));
    medien.createIndex(Media.PK);

    orchesters.load(new File(dbpath, "TabOrchester.csv"));
    orchesters.createIndex(Orchester.PK);
    orchestersDetail.load(new File(dbpath, "TabDetailOrchester.csv"));

    dirigents.load(new File(dbpath, "TabDirigent.csv"));
    dirigents.createIndex(Dirigent.PK);
    interprets.load(new File(dbpath, "TabInterpret.csv"));
    interprets.createIndex(Interpret.PK);
    interpretsDetail.load(new File(dbpath, "TabDetailInterpret.csv"));
    usageDb = new Mp3UsageDB(new File(dbpath, "mp3usages.csv"));

  }

  public List<Composer> getComposers()
  {
    Collection<String[]> recs = composers.getUniqueSorted(Composer.NAME);
    List<Composer> ret = new ArrayList<Composer>();
    for (String[] rec : recs) {
      ret.add(new Composer(this, rec));
    }
    return ret;
  }

  public Map<Integer, TimeLineEl> getComposerTimeLine(String country)
  {
    Map<Integer, TimeLineEl> ret = new TreeMap<Integer, TimeLineEl>();
    List<String[]> recs = composers.getUniqueSorted(Composer.NAME);
    for (String[] rec : recs) {

      Composer composer = new Composer(this, rec);
      String composerCountry = composer.get(Composer.COUNTRY);
      List<String> ccl = Arrays.asList(StringUtils.split(composerCountry, ";"));
      if (StringUtils.isNotBlank(country) == true && ccl.contains(country) == false) {
        continue;
      }
      int by = composer.getBornYear();
      int dy = composer.getDiedYear();
      TimeLineEl tl = ret.get(by);
      if (tl == null) {
        tl = new TimeLineEl(by);
        ret.put(by, tl);
      }
      tl.start.add(composer);
      tl = ret.get(dy);
      if (tl == null) {
        tl = new TimeLineEl(dy);
        ret.put(dy, tl);
      }
      tl.end.add(composer);

    }
    return ret;

  }

  public List<Media> getMedia()
  {
    List<String[]> rec = medien.getSortByIndex(Media.NAME1, Media.NAME2, Media.NAME3);
    List<Media> med = new ArrayList<Media>(rec.size());
    for (String[] r : rec) {
      med.add(new Media(this, r));
    }
    return med;
  }

  public List<Orchester> getOrchester()
  {
    List<String[]> rec = orchesters.getSortByIndex(Orchester.NAME);
    List<Orchester> med = new ArrayList<Orchester>(rec.size());
    for (String[] r : rec) {
      med.add(new Orchester(this, r));
    }
    return med;
  }

  public List<Dirigent> getDirigenten()
  {
    List<String[]> rec = dirigents.getSortByIndex(Dirigent.NAME);
    List<Dirigent> med = new ArrayList<Dirigent>(rec.size());
    for (String[] r : rec) {
      med.add(new Dirigent(this, r));
    }
    return med;
  }

  public List<Title> getTitelsFromOrchester(Orchester orchester)
  {
    List<Title> ret = new ArrayList<Title>();
    for (String[] od : orchestersDetail.table) {
      if (od.length >= 2) {
        if (StringUtils.equals(od[0], orchester.getName()) == true) {
          ret.add(getTitleByPk(od[1]));
        }
      }
    }
    return ret;
  }

  public List<Title> getTitelsFromInterpret(Interpret interpret)
  {
    List<Title> ret = new ArrayList<Title>();
    for (String[] od : interpretsDetail.table) {
      if (od.length >= 3) {
        if (StringUtils.equals(od[0], interpret.getName()) == true) {
          ret.add(getTitleByPk(od[2]));
        }
      }
    }
    return ret;
  }

  public Composer getComposerByPk(String pk)
  {
    return new Composer(this, composers.findFirst(Composer.PK, pk));
  }

  public Orchester getOrchesterByPk(String pk)
  {
    return new Orchester(this, orchesters.findFirst(Orchester.PK, pk));
  }

  public Composer getComposerByName(String name)
  {
    return new Composer(this, composers.findFirst(Composer.NAME, name));
  }

  public List<Title> getTitelFromKomposer(String komposer)
  {
    List<String[]> recs = title.findEquals(1, komposer);
    title.sortByIndex(recs, Title.TITEL, Title.TNUMMER, Title.OP, Title.TONART, Title.EINSPIELUNG);
    List<Title> ret = new ArrayList<Title>();
    for (String[] rec : recs) {
      ret.add(new Title(this, rec));
    }
    return ret;
  }

  public List<Track> getTracksFromTitle(Title title)
  {
    List<String[]> recs = tracks.findEquals(Track.FK_TITLE, title.getPk());
    tracks.sortByIndex(recs, Track.PK);
    List<Track> ret = new ArrayList<Track>();
    if (recs.isEmpty() == false) {
      for (String[] rec : recs) {
        ret.add(new Track(this, rec));
      }
      for (Track t : ret) {
        if (t.getMp3Path().exists() == false) {
          ret.clear();
          break;
        }
      }
    }
    if (ret.isEmpty() == false) {

    }
    if (ret.isEmpty() == true) {
      File dir = title.getMp3Path();
      if (dir == null || dir.exists() == false) {
        return ret;
      }
      int no = 0;
      for (File f : dir.listFiles()) {
        if (f.getName().endsWith(".mp3") == false) {
          continue;
        }
        Track nt = new Track(this, f, title, no);
        ret.add(nt);
        ++no;
      }
      Collections.sort(ret, new Comparator<Track>()
      {

        @Override
        public int compare(Track o1, Track o2)
        {
          return o1.getNameOnFs().compareTo(o2.getNameOnFs());
        }
      });

    }
    return ret;
  }

  public Media getMediaByPk(String pk)
  {
    return new Media(this, medien.findFirst(Media.PK, pk));
  }

  public Title getTitleByPk(String pk)
  {
    return new Title(this, title.findFirst(Title.PK, pk));
  }

  public Track getTrackByPk(String pk)
  {
    return new Track(this, tracks.findFirst(Track.PK, pk));
  }

  public Interpret getInterpretByPk(String pk)
  {
    String[] ip = interprets.findFirst(Interpret.PK, pk);
    if (ip == null) {
      return null;
    }
    return new Interpret(this, ip);
  }

  public Dirigent getDirigentByPk(String pk)
  {
    return new Dirigent(this, dirigents.findFirst(Dirigent.PK, pk));
  }

  public File getMp3root()
  {
    return mp3root;
  }

  public void setMp3root(File mp3root)
  {
    this.mp3root = mp3root;
  }

  public CsvTable getTitle()
  {
    return title;
  }

  public CsvTable getTracks()
  {
    return tracks;
  }

  public CsvTable getMedien()
  {
    return medien;
  }

  public File getBookletFile(String imagePk, boolean imageBack)
  {
    File images = new File(mp3root.getParentFile().getParentFile(), "imagesmedien");
    String fn = "c" + imagePk + "-" + (imageBack == true ? "f" : "b") + ".jpg";
    File file = new File(images, fn);
    if (file.exists() == true) {
      return file;
    }
    String fqn = file.getAbsolutePath();
    return null;
  }

  public File getJpcBookletFile(String imagePk, boolean imageBack)
  {

    String prefix = imageBack == true ? "back" : "";
    File images = new File(mp3root.getParentFile().getParentFile(), "imagesmedienjpc");
    String fn = prefix + imagePk + ".jpg";
    File file = new File(images, fn);
    if (file.exists() == true) {
      return file;
    }
    String fqn = file.getAbsolutePath();
    return null;
  }

  public File getNaxosImage(Media media)
  {
    if (StringUtils.equals(media.get(Media.LABEL), "Naxos") == false) {
      return null;
    }
    String labelId = media.get(Media.LABELID);
    return getNaxosImage(labelId);
  }

  public File getNaxosImage(String labelId)
  {
    if (StringUtils.isBlank(labelId) == true) {
      return null;
    }

    int idx = labelId.indexOf('.');
    if (idx != -1) {
      labelId = labelId.substring(idx + 1);
    }
    idx = StringUtils.indexOfAnyBut(labelId, "0123456789");
    if (idx != -1) {
      labelId = labelId.substring(0, idx);
    }
    File images = new File(mp3root.getParentFile().getParentFile(), "imagesmediennaxos");
    File image = new File(images, labelId + ".gif");
    if (image.exists() == true) {
      return image;
    }
    return null;
  }

  public Pair<String, byte[]> getMp3Zip(List<Track> tracks, String nameOfZip)
  {
    Pair<String, byte[]> ret = new Pair<String, byte[]>();

    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    ZipWriteFileSystem zfs = new ZipWriteFileSystem(bout);
    for (Track track : tracks) {
      File f = track.getMp3Path();
      try {
        byte[] data = FileUtils.readFileToByteArray(f);
        Title title = track.getTitle();
        Composer comp = title.getComposer();
        String pname = comp.getNameOnFs() + "/" + track.getTitle().getNameOnFs() + "/" + track.getNameOnFs();
        zfs.writeBinaryFile(pname, data, false);
      } catch (IOException ex) {
        GLog.warn(GWikiLogCategory.Wiki, "RogMp3; Error zipping file: " + f.getAbsolutePath() + "; " + ex.getMessage(),
            new LogExceptionAttribute(ex));
      }
    }
    zfs.close();
    ret.setFirst(nameOfZip);
    ret.setSecond(bout.toByteArray());
    return ret;
  }

  public List<Interpret> getInterpreten()
  {
    List<String[]> rec = interprets.getSortByIndex(Interpret.NAME);
    List<Interpret> med = new ArrayList<Interpret>(rec.size());
    for (String[] r : rec) {
      med.add(new Interpret(this, r));
    }
    return med;
  }

  public List<Interpret> getInterpretByTitel(String pk)
  {
    List<String[]> la = interpretsDetail.findEquals(2, pk);
    List<Interpret> ret = new ArrayList<Interpret>(la.size());
    for (String[] sa : la) {
      for (String[] it : interprets.table) {
        if (StringUtils.equals(it[Interpret.NAME], sa[Interpret.DETAIL_NAME]) == true
            && StringUtils.equals(it[Interpret.INSTRUMENT], sa[Interpret.DETAIL_INSTRUMENT]) == true) {
          ret.add(new Interpret(this, it));
        }
      }
    }
    return ret;
  }

  public SortedMap<String, Integer> getComposersCountries()
  {
    SortedMap<String, Integer> ret = new TreeMap<String, Integer>();
    for (String[] comp : composers.table) {
      Composer cp = new Composer(this, comp);
      String rcountry = cp.get(Composer.COUNTRY);
      List<String> ccl = Arrays.asList(StringUtils.split(rcountry, ";"));
      for (String country : ccl) {
        Integer c = ret.get(country);
        if (c == null) {
          ret.put(country, 1);
        } else {
          ret.put(country, c + 1);
        }
      }
    }
    return ret;
  }

  public List<Orchester> getOrchesterByTitel(String pk)
  {
    List<String[]> orl = orchestersDetail.findEquals(1, pk);
    List<Orchester> ret = new ArrayList<Orchester>(orl.size());
    for (String[] o : orl) {
      for (String[] orchs : orchesters.findEquals(Orchester.NAME, o[0])) {
        ret.add(new Orchester(this, orchs));
      }
    }
    return ret;
  }

  public List<Title> getTitlesByDirigent(Dirigent dirigent)
  {
    List<String[]> fl = title.findEquals(Title.DIRIGENT, dirigent.getName());
    List<Title> ret = new ArrayList<Title>(fl.size());
    for (String[] t : fl) {
      ret.add(new Title(this, t));
    }
    return ret;
  }

  public Mp3UsageDB getUsageDb()
  {
    return usageDb;
  }
}
