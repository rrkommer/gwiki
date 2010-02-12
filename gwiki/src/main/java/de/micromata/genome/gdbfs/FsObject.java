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

import de.micromata.genome.dao.db.StdRecordDO;

/**
 * Representing a file or a directory in the FileSystem.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class FsObject extends StdRecordDO
{

  private static final long serialVersionUID = -6003825859639182662L;

  protected FileSystem fileSystem;

  protected String name;

  protected char type;

  protected String mimeType;

  protected String attributes;

  protected int length;

  protected long lastModified;

  public FsObject(FileSystem fileSystem, String name, char type, String mimeType, long lastModified)
  {
    this.fileSystem = fileSystem;
    this.name = name;
    this.type = type;
    this.mimeType = mimeType;
    this.lastModified = lastModified;
  }

  public String getNamePart()
  {
    int idx = name.lastIndexOf('/');
    if (idx == -1) {
      return name;
    }
    return name.substring(idx + 1);
  }

  public FsDirectoryObject getParent()
  {
    return (FsDirectoryObject) fileSystem.getFileObject(getPathPart());
  }

  public String getPathPart()
  {
    int idx = name.lastIndexOf('/');
    if (idx == -1) {
      return "";
    }
    return name.substring(0, idx);
  }

  public String toString()
  {
    return name;
  }

  public boolean isFile()
  {
    return type == 'F';
  }

  public boolean isDirectory()
  {
    return type == 'D';
  }

  public boolean exists()
  {
    return type != 0;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public char getType()
  {
    return type;
  }

  public void setType(char type)
  {
    this.type = type;
  }

  public FileSystem getFileSystem()
  {
    return fileSystem;
  }

  public void setFileSystem(FileSystem fileSystem)
  {
    this.fileSystem = fileSystem;
  }

  public String getMimeType()
  {
    return mimeType;
  }

  public void setMimeType(String mimeType)
  {
    this.mimeType = mimeType;
  }

  public String getAttributes()
  {
    return attributes;
  }

  public void setAttributes(String attributes)
  {
    this.attributes = attributes;
  }

  public int getLength()
  {
    return length;
  }

  public void setLength(int length)
  {
    this.length = length;
  }

  public long getLastModified()
  {
    return lastModified;
  }

  public void setLastModified(long lastModified)
  {
    this.lastModified = lastModified;
  }

}
