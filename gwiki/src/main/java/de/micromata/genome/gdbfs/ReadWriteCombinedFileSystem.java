package de.micromata.genome.gdbfs;

/**
 * Combines a primary read/write and a secondary read only file system.
 * 
 * @author roger
 * 
 */
public class ReadWriteCombinedFileSystem extends CombinedFileSystem
{

  public ReadWriteCombinedFileSystem()
  {
    super();
  }

  public ReadWriteCombinedFileSystem(FileSystem primary, FileSystem secondary)
  {
    super(primary, secondary);
  }

  public FileSystem getFsForRead(String name)
  {
    if (primary.exists(name) == true) {
      return primary;
    }
    if (secondary.exists(name) == true) {
      return secondary;
    }
    return primary;
  }

  public FileSystem getFsForWrite(String name)
  {
    return primary;
  }

}
