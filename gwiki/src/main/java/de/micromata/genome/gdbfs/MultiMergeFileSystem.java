////////////////////////////////////////////////////////////////////////////
// 
// Copyright (C) 2010 Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// 
////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gdbfs;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.runtime.CallableX;

/**
 * A file system merged other file systems. Because there is no decent matching, dispatching write access will be work via finding first
 * file system, the parent directory exits.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class MultiMergeFileSystem extends AbstractFileSystem
{
  private List<FileSystem> fileSystems = new ArrayList<FileSystem>();

  public FileSystem getFsForWrite(String name)
  {
    FileSystem fs = getFsForRead(name);
    if (fs != null) {
      return fs;
    }
    String parent = FileNameUtils.getParentDir(name);
    fs = getFsForRead(parent);
    return fs;
  }

  @Override
  public FileSystem getFsForRead(String name)
  {
    for (FileSystem fs : fileSystems) {
      if (fs.exists(name) == true) {
        return fs;
      }
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gdbfs.FileSystem#delete(java.lang.String)
   */
  public boolean delete(String name)
  {
    FileSystem fs = getFsForWrite(name);
    if (fs == null) {
      return false;
    }
    return fs.delete(name);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gdbfs.FileSystem#erase()
   */
  public void erase()
  {

  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gdbfs.FileSystem#exists(java.lang.String)
   */
  public boolean exists(String name)
  {
    return getFsForRead(name) != null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gdbfs.FileSystem#getFileObject(java.lang.String)
   */
  public FsObject getFileObject(String name)
  {
    FileSystem fs = getFsForRead(name);
    if (fs == null) {
      return null;
    }
    return fs.getFileObject(name);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gdbfs.FileSystem#getFileSystemName()
   */
  public String getFileSystemName()
  {
    return "TODO";
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gdbfs.FileSystem#getLastModified(java.lang.String)
   */
  public long getLastModified(String name)
  {
    FileSystem fs = getFsForRead(name);
    if (fs == null) {
      return 0;
    }
    return fs.getLastModified(name);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gdbfs.FileSystem#getModificationCounter()
   */
  public long getModificationCounter()
  {
    long ret = 0;
    for (FileSystem fs : fileSystems) {
      ret += fs.getModificationCounter();
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gdbfs.FileSystem#listFiles(java.lang.String, de.micromata.genome.util.matcher.Matcher, java.lang.Character,
   * boolean)
   */
  public List<FsObject> listFiles(String name, Matcher<String> matcher, Character searchType, boolean recursive)
  {
    List<FsObject> ret = new ArrayList<FsObject>();
    for (FileSystem fs : fileSystems) {
      ret.addAll(fs.listFiles(name, matcher, searchType, recursive));
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gdbfs.FileSystem#mkdir(java.lang.String)
   */
  public boolean mkdir(String name)
  {
    String pdi = FileNameUtils.getParentDir(name);
    FileSystem fs = getFsForWrite(name);
    if (fs == null) {
      return false;
    }
    return fs.mkdir(name);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gdbfs.FileSystem#readBinaryFile(java.lang.String, java.io.OutputStream)
   */
  public void readBinaryFile(String file, OutputStream os)
  {
    getFsForRead(file).readBinaryFile(file, os);

  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gdbfs.FileSystem#rename(java.lang.String, java.lang.String)
   */
  public boolean rename(String oldName, String newName)
  {
    FileSystem fs = getFsForWrite(oldName);
    if (fs == null) {
      return false;
    }
    return fs.rename(oldName, newName);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gdbfs.FileSystem#writeBinaryFile(java.lang.String, java.io.InputStream, boolean)
   */
  public void writeBinaryFile(String file, InputStream is, boolean overWrite)
  {
    FileSystem fs = getFsForWrite(file);
    if (fs == null) {
      throw new FsException("Cannot determine sub file system for writing: " + file);
    }
    fs.writeBinaryFile(file, is, overWrite);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gdbfs.FileSystem#runInTransaction(java.lang.String, long, boolean, de.micromata.genome.util.runtime.CallableX)
   */
  public <R> R runInTransaction(String lockFile, long timeOut, boolean noModFs, CallableX<R, RuntimeException> callback)
  {
    if (fileSystems.isEmpty() == true) {
      throw new FsException("No subfilesystem available for write lockfile");
    }
    return fileSystems.get(0).runInTransaction(lockFile, timeOut, noModFs, callback);
  }

  public List<FileSystem> getFileSystems()
  {
    return fileSystems;
  }

  public void setFileSystems(List<FileSystem> fileSystems)
  {
    this.fileSystems = fileSystems;
  }

}
