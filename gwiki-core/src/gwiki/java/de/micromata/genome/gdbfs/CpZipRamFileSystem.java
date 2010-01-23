package de.micromata.genome.gdbfs;

import java.io.InputStream;

/**
 * Load RAM File system from Zip loaded from Classpath.
 * 
 * @author roger
 * 
 */
public class CpZipRamFileSystem extends ZipRamFileSystem
{

  private static final long serialVersionUID = -6217635628070031205L;

  public CpZipRamFileSystem(String zipFile)
  {
    this(zipFile, "embedded");
  }

  public CpZipRamFileSystem(String zipFile, String fsName)
  {
    this(zipFile, fsName, false);
  }

  public CpZipRamFileSystem(String zipFile, String fsName, boolean readOnly)
  {
    super(fsName);
    InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(zipFile);
    if (is == null) {
      throw new FsException("Cannot open ZipFile: " + zipFile);
    }
    load(is);
    setReadOnly(readOnly);
  }
}
