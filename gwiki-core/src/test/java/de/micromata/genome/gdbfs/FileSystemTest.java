/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   12.01.2010
// Copyright Micromata 12.01.2010
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gdbfs;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import de.micromata.genome.util.matcher.string.EndsWithMatcher;

public class FileSystemTest extends TestCase
{
  private FileSystem fsys;

  private String testName;

  public FileSystemTest(String name, FileSystem fsys)
  {
    this.fsys = fsys;
    this.testName = name;
    setName(name);
  }

  @Override
  public String getName()
  {
    return testName + "." + super.getName();
  }

  public void testMkDirs()
  {
    assertFalse(fsys.mkdir("mkdir/a/b"));
    assertTrue(fsys.mkdirs("mkdir/a/b"));
    assertTrue(fsys.mkdir("mkdir/a/b/c"));
  }

  public void testNoParentDir()
  {
    try {
      String fname = "dir/does/not/exists/MyFile.txt";
      FsFileObject fo = new FsFileObject(fsys, fname, "text", 0);
      String tstr = "Hallo das sind meine Daten";
      fo.writeFile(tstr);
    } catch (FsFileExistsException ex) {
      System.out.println("Expected ex: " + ex.getMessage());
    }
  }

  public void testModTime()
  {
    assertTrue(fsys.mkdir("modTime"));
    String fname = "modTime/MyFile.txt";
    FsFileObject fo = new FsFileObject(fsys, fname, "text", 0);
    assertFalse(fsys.exists(fname));
    String tstr = "Hallo das sind meine Daten";
    fo.writeFile(tstr);
    assertTrue(fsys.exists(fname));
    long time = fsys.getLastModified(fname);
    try {
      Thread.sleep(100);
    } catch (Exception ex) {
      // nothing;
    }
    fo = new FsFileObject(fsys, fname, "text", 0);
    tstr = "Hallo das sind meine Daten 2";
    fo.writeFile(tstr);
    long time2 = fsys.getLastModified(fname);
    assertTrue(time2 > time);
  }

  public void testReadWrite()
  {

    fsys.mkdirs("unittests/testit");
    FsFileObject fo = new FsFileObject(fsys, "unittests/testit/MyFile.txt", "text", 0);
    String tstr = "Hallo das sind meine Daten";
    fo.writeFile(tstr);
    FsDirectoryObject dir = (FsDirectoryObject) fsys.getFileObject("unittests/testit");
    FsFileObject fo2 = (FsFileObject) fsys.getFileObject("unittests/testit/MyFile.txt");
    String data = fo2.readString();
    assertEquals(data, tstr);
  }

  public void testUpdate()
  {
    FsFileObject fo = new FsFileObject(fsys, "MyFile.txt", "text", 0);
    String tstr = "Hallo das sind meine Daten";
    fo.writeFile(tstr);
    tstr = "Das die neuen Daten";
    fo.writeFile(tstr);
    FsFileObject fo2 = new FsFileObject(fsys, "MyFile.txt", "text", 0);
    String data = fo2.readString();
    assertEquals(data, tstr);
    fo2.delete();
    FsObject fo3 = fsys.getFileObject("/MyFile.txt");
    assertTrue(fo3 == null);
  }

  public void testUpdateLong()
  {

    byte data[] = new byte[10024];
    for (int i = 0; i < data.length; ++i) {
      data[i] = 'a';
    }
    FsFileObject fo = new FsFileObject(fsys, "MyFile2.txt", "binary/data", 0);

    fo.writeFile(data);
    for (int i = 0; i < data.length; ++i) {
      data[i] = 'b';
    }
    fo.writeFile(data);
    FsFileObject fo2 = new FsFileObject(fsys, "MyFile2.txt", "binary/data", 0);
    byte[] datar = fo2.readData();
    assertEquals('b', (char) datar[0]);

    fo2.delete();
    FsObject fo3 = fsys.getFileObject("MyFile2.txt");
    assertTrue(fo3 == null);
  }

  public void testEvents()
  {
    final List<FileSystemEvent> events = new ArrayList<FileSystemEvent>();

    fsys.registerListener(null, new EndsWithMatcher<String>(".txt"), new FileSystemEventListener() {

      public void onFileSystemChanged(FileSystemEvent event)
      {
        events.add(event);
      }
    });
    FsFileObject fo = new FsFileObject(fsys, "MyFilex.txt", "text", 0);
    String tstr = "Hallo das sind meine Daten";
    fo.writeFile(tstr);
    fsys.checkEvents(true);
    assertEquals(1, events.size());
    assertEquals(FileSystemEventType.Created, events.get(0).getEventType());
    assertEquals("MyFilex.txt", events.get(0).getFileName());
    events.clear();
    tstr = "Das die neuen Daten";
    fo.writeFile(tstr);
    fsys.checkEvents(true);
    assertEquals(1, events.size());
    assertEquals(FileSystemEventType.Modified, events.get(0).getEventType());
    assertEquals("MyFilex.txt", events.get(0).getFileName());
    events.clear();
    fo.delete();
    fsys.checkEvents(true);
    assertEquals(1, events.size());
    assertEquals(FileSystemEventType.Deleted, events.get(0).getEventType());
    assertEquals("MyFilex.txt", events.get(0).getFileName());
  }

  public void testOverWrite()
  {
    FsFileObject fo = new FsFileObject(fsys, "OverwriteFile.txt", "text", 0);
    String tstr = "Hallo das sind meine Daten";
    fo.writeFile(tstr);
    try {
      fsys.writeTextFile(fo.getName(), "Was anders", false);
      fail("Overwrite File should fail");
    } catch (FsFileExistsException ex) {
      System.out.println("Expected ex: " + ex.getMessage());
    }
  }

  private void createFiles(String... fileNames)
  {
    for (String fname : fileNames) {
      FsFileObject fo = new FsFileObject(fsys, fname, "text", 0);
      FsDirectoryObject pdir = fo.getParent();
      String pdirname = fo.getPathPart();
      if (pdir == null) {
        fsys.mkdirs(pdirname);
      }
      fsys.writeTextFile(fname, "asdf", false);
    }
  }

  public void testMatch()
  {
    createFiles(//
        "match/sub1/Test1.txt", //
        "match/sub1/Test2.txt", //
        "match/sub1/Test2.dat" //
    );
    assertEquals(0, fsys.listFilesByPattern("match", "*.dat", 'D', true).size());
    assertEquals(1, fsys.listFilesByPattern("match", "*.dat", 'F', true).size());

  }

  @Override
  protected void setUp() throws Exception
  {
    super.setUp();
    fsys.erase();
  }

}
