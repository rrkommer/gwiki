/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   20.11.2009
// Copyright Micromata 20.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gdbfs;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.runtime.CallableX;

/**
 * Wrapper pattern for a FileSystem, delegating calls to nested file system.
 * 
 * @author roger@micromata.de
 * 
 */
public class FileSystemWrapper extends AbstractFileSystem
{

  protected FileSystem nested;

  public FileSystemWrapper()
  {

  }

  public FileSystemWrapper(FileSystem nested)
  {
    this.nested = nested;
  }

  public boolean delete(String name)
  {
    return nested.delete(name);
  }

  public boolean deleteRecursive(String name)
  {
    return nested.deleteRecursive(name);
  }

  public void erase()
  {
    nested.erase();
  }

  public boolean exists(String name)
  {
    return nested.exists(name);
  }

  public FsObject getFileObject(String name)
  {
    return nested.getFileObject(name);
  }

  public String getFileSystemName()
  {
    return nested.getFileSystemName();
  }

  public boolean isReadOnly()
  {
    return nested.isReadOnly();
  }

  public List<FsObject> listFiles(String name, Matcher<String> matcher, Character searchType, boolean recursive)
  {
    return nested.listFiles(name, matcher, searchType, recursive);
  }

  public boolean mkdir(String name)
  {
    return nested.mkdir(name);
  }

  public boolean mkdirs(String name)
  {
    return nested.mkdirs(name);
  }

  public void readBinaryFile(String file, OutputStream os)
  {
    nested.readBinaryFile(file, os);
  }

  public byte[] readBinaryFile(String file)
  {
    return nested.readBinaryFile(file);
  }

  public String readTextFile(String file)
  {
    return nested.readTextFile(file);
  }

  public boolean rename(String oldName, String newName)
  {
    return nested.rename(oldName, newName);
  }

  public void setReadOnly(boolean readOnly)
  {
    nested.setReadOnly(readOnly);
  }

  public void writeBinaryFile(String file, byte[] data, boolean overWrite)
  {
    nested.writeBinaryFile(file, data, overWrite);
  }

  public void writeBinaryFile(String file, InputStream is, boolean overWrite)
  {
    nested.writeBinaryFile(file, is, overWrite);
  }

  public void writeFile(String file, byte[] data, boolean overWrite)
  {
    nested.writeFile(file, data, overWrite);
  }

  public void writeTextFile(String file, String content, boolean overWrite)
  {
    nested.writeTextFile(file, content, overWrite);
  }

  public long getLastModified(final String name)
  {
    return nested.getLastModified(name);
  }

  public long getModificationCounter()
  {
    return nested.getModificationCounter();
  }

  public <R> R runInTransaction(String lockFile, long timeOut, boolean noModFs, CallableX<R, RuntimeException> callback)
  {
    return nested.runInTransaction(lockFile, timeOut, noModFs, callback);
  }

  public FileSystem getNested()
  {
    return nested;
  }

  public void setNested(FileSystem nested)
  {
    this.nested = nested;
  }

  public void cleanupTempDirs()
  {
    nested.cleanupTempDirs();
  }

  public FsDirectoryObject createTempDir(String name, long timeToLive)
  {
    return nested.createTempDir(name, timeToLive);
  }
}
