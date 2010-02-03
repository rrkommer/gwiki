package de.micromata.genome.gdbfs;

import java.io.File;

import junit.framework.TestCase;

public class StandardFileSystemTest extends TestCase
{
  public void testSecurityAccessWindowsC()
  {
    char csep = File.separatorChar;
    if (csep != '\\') {
      return;
    }
    String rootDir = "./tmp/unittests/gdfstests";
    FileSystem fs = new StdFileSystem(rootDir);
    try {
      fs.mkdir("c:/");
      fs.writeTextFile("c:/GwikiFs.txt", "Should be written", true);
      fail("FsInvalidNameException should be thrown");
    } catch (FsInvalidNameException ex) {
      System.out.println("Expected ex: " + ex.getMessage());
    }
  }

  public void testSecurityAccessParent()
  {
    String rootDir = "./tmp/unittests/gdfstests";
    FileSystem fs = new StdFileSystem(rootDir);
    try {
      fs.mkdirs("../thisDirShouldNotBeCreated");
      fail("FsInvalidNameException should be thrown");
    } catch (FsInvalidNameException ex) {
      System.out.println("Expected ex: " + ex.getMessage());
    }
  }
}
