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

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.util.web.MimeUtils;

/**
 * Representing a File (not a directory) in the file system.
 * 
 * @author roger@micromata.de
 * 
 */
public class FsFileObject extends FsObject
{

  private static final long serialVersionUID = -252297990366094145L;

  public FsFileObject(FileSystem fileSystem, String name, String mimeType, long lastModified)
  {
    super(fileSystem, name, FileSystem.TYPE_FILE, mimeType, lastModified);
  }

  public boolean isTextFile()
  {
    String mime = mimeType;
    if (StringUtils.isBlank(mime) == true) {
      mime = MimeUtils.getMimeTypeFromFile(name);
    }
    if (mime == null) {
      if (name.endsWith(".gwiki") == true || name.endsWith(".properties") == true) {
        mime = "text/text";
      } else {
        return false;
      }
    }
    if (mime.startsWith("text/") == true)
      return true;
    return false;
  }

  public byte[] readData()
  {
    return fileSystem.readBinaryFile(name);
  }

  public String readString()
  {
    return fileSystem.readTextFile(name);
  }

  // TODO exception
  public void writeNewFile(byte[] data)
  {
    fileSystem.writeBinaryFile(name, data, false);
  }

  public void writeFile(byte[] data)
  {
    fileSystem.writeBinaryFile(name, data, true);
  }

  public void writeNewFile(String data)
  {
    fileSystem.writeTextFile(name, data, false);
  }

  public void writeFile(String data)
  {
    fileSystem.writeTextFile(name, data, true);
  }

  public void delete()
  {
    fileSystem.delete(name);
  }
}
