package de.micromata.genome.gdbfs;

import java.io.InputStream;

/**
 * Zip Ram File system.
 * 
 * @author roger
 * 
 */
public class ZipRamFileSystem extends RamFileSystem
{

  private static final long serialVersionUID = 2733712903364025301L;

  public ZipRamFileSystem()
  {
    super();
  }

  public ZipRamFileSystem(String fsName)
  {
    super(fsName);
  }

  public ZipRamFileSystem(String fsName, InputStream is)
  {
    super(fsName);
    load(is);
  }

  public void load(InputStream is)
  {
    FileSystemUtils.copyFromZip(is, this.getFileObject(""));
  }
}
