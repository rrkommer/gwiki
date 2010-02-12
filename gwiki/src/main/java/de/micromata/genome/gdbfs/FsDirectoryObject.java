/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   24.10.2009
// Copyright Micromata 24.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gdbfs;

import java.util.List;

/**
 * Represents a directory in the FileSystem.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class FsDirectoryObject extends FsObject
{

  private static final long serialVersionUID = 7503728953607974196L;

  public FsDirectoryObject(FileSystem fileSystem, String name, long lastModified)
  {
    super(fileSystem, name, FileSystem.TYPE_DIR, null, lastModified);
  }

  public FsDirectoryObject mkdir(String dir)
  {
    String locName = FileNameUtils.join(getName(), dir);
    if (fileSystem.existsForWrite(locName) == true) {
      FsObject fso = fileSystem.getFileObject(locName);
      if (fso instanceof FsDirectoryObject) {
        return (FsDirectoryObject) fso;
      }
      return null;
    }
    if (fileSystem.mkdir(locName) == false)
      return null;
    return (FsDirectoryObject) fileSystem.getFileObject(locName);
  }

  public FsObject getChild(String childName)
  {
    return fileSystem.getFileObject(FileNameUtils.join(getName(), childName));
  }

  public List<FsObject> getChilds(Character type)
  {
    return fileSystem.listFiles(getName(), null, type, false);
  }
}
