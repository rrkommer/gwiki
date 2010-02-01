package de.micromata.genome.gdbfs;

import java.io.InputStream;
import java.util.List;

import junit.framework.TestCase;

public class ReadWriteCombinedFileSystemTest extends TestCase
{
  public FileSystem loadZipFs()
  {
    InputStream is = ReadWriteCombinedFileSystemTest.class.getResourceAsStream("/de/micromata/genome/gdbfs/ZipFs.zip");
    assertNotNull(is);
    RamFileSystem fs = new RamFileSystem("ramfs");
    FileSystemUtils.copyFromZip(is, fs.getFileObject(""));
    fs.setReadOnly(true);
    return fs;
  }

  public CombinedFileSystem getCombined()
  {
    String rootDir = "./tmp/unittests/gdfstests";
    FileSystem primary = new StdFileSystem(rootDir);
    primary.erase();
    return new ReadWriteCombinedFileSystem(primary, loadZipFs());
  }

  public void testZip()
  {
    FileSystem fs = loadZipFs();
    String dfn = "gdfstests/datafile/textfile.txt";
    assertTrue(fs.exists(dfn));
    String text = fs.readTextFile(dfn);
    assertEquals("BlaBla", text);
  }

  public void testRead()
  {
    CombinedFileSystem fs = getCombined();
    String dfn = "gdfstests/datafile/textfile.txt";
    assertTrue(fs.exists(dfn));
    assertFalse(fs.delete(dfn));
    fs.mkdirs("gdfstests/datafile");
    fs.writeTextFile(dfn, "Blub", true);
    String readed = fs.readTextFile(dfn);
    assertEquals("Blub", readed);
    fs.delete(dfn);
    assertTrue(fs.exists(dfn));
    assertEquals("BlaBla", fs.readTextFile(dfn));
  }

  public void testListFiles()
  {
    CombinedFileSystem fs = getCombined();
    fs.mkdirs("gdfstests/datafile");
    fs.writeTextFile("gdfstests/datafile/anotherfile.txt", "jo", false);
    List<FsObject> fsl = fs.listFilesByPattern("gdfstests/datafile", "*.txt", null, true);
    assertEquals(2, fsl.size());
  }
}
