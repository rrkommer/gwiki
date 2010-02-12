package de.micromata.genome.gdbfs;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * RAM file system initially loaded from a zip from standard file system.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class FileZipRamFileSystem extends ZipRamFileSystem
{

  private static final long serialVersionUID = -7317267244076883488L;

  public FileZipRamFileSystem(String zipFile, String fsName)
  {
    this(zipFile, fsName, false);
  }

  public FileZipRamFileSystem(String zipFile, String fsName, boolean readOnly)
  {
    super(fsName);
    try {
      InputStream is = new FileInputStream(zipFile);
      load(is);
      setReadOnly(readOnly);
    } catch (IOException ex) {
      throw new FsException("Cannot open ZipFile: " + zipFile + "; " + ex.getMessage(), ex);
    }
  }
}
