//
// Copyright (C) 2010-2016 Roger Rene Kommer & Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package de.micromata.genome.gdbfs;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.runtime.CallableX;

/**
 * Wrapper pattern for a FileSystem, delegating calls to nested file system.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class FileSystemWrapper extends AbstractFileSystem
{

  /**
   * The nested.
   */
  protected FileSystem nested;

  /**
   * Instantiates a new file system wrapper.
   */
  public FileSystemWrapper()
  {

  }

  /**
   * Instantiates a new file system wrapper.
   *
   * @param nested the nested
   */
  public FileSystemWrapper(FileSystem nested)
  {
    this.nested = nested;
  }

  @Override
  public boolean delete(String name)
  {
    return nested.delete(name);
  }

  @Override
  public boolean deleteRecursive(String name)
  {
    return nested.deleteRecursive(name);
  }

  @Override
  public void erase()
  {
    nested.erase();
  }

  @Override
  public boolean exists(String name)
  {
    return nested.exists(name);
  }

  @Override
  public FsObject getFileObject(String name)
  {
    return nested.getFileObject(name);
  }

  @Override
  public String getFileSystemName()
  {
    return nested.getFileSystemName();
  }

  @Override
  public boolean isReadOnly()
  {
    return nested.isReadOnly();
  }

  @Override
  public List<FsObject> listFiles(String name, Matcher<String> matcher, Character searchType, boolean recursive)
  {
    return nested.listFiles(name, matcher, searchType, recursive);
  }

  @Override
  public boolean mkdir(String name)
  {
    return nested.mkdir(name);
  }

  @Override
  public boolean mkdirs(String name)
  {
    return nested.mkdirs(name);
  }

  @Override
  public void readBinaryFile(String file, OutputStream os)
  {
    nested.readBinaryFile(file, os);
  }

  @Override
  public byte[] readBinaryFile(String file)
  {
    return nested.readBinaryFile(file);
  }

  @Override
  public String readTextFile(String file)
  {
    return nested.readTextFile(file);
  }

  @Override
  public boolean rename(String oldName, String newName)
  {
    return nested.rename(oldName, newName);
  }

  @Override
  public void setReadOnly(boolean readOnly)
  {
    nested.setReadOnly(readOnly);
  }

  @Override
  public void writeBinaryFile(String file, byte[] data, boolean overWrite)
  {
    nested.writeBinaryFile(file, data, overWrite);
  }

  @Override
  public void writeBinaryFile(String file, InputStream is, boolean overWrite)
  {
    nested.writeBinaryFile(file, is, overWrite);
  }

  @Override
  public void writeFile(String file, byte[] data, boolean overWrite)
  {
    nested.writeFile(file, data, overWrite);
  }

  @Override
  public void writeTextFile(String file, String content, boolean overWrite)
  {
    nested.writeTextFile(file, content, overWrite);
  }

  @Override
  public long getLastModified(final String name)
  {
    return nested.getLastModified(name);
  }

  @Override
  public long getModificationCounter()
  {
    return nested.getModificationCounter();
  }

  @Override
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

  @Override
  public void cleanupTempDirs()
  {
    nested.cleanupTempDirs();
  }

  @Override
  public FsDirectoryObject createTempDir(String name, long timeToLive)
  {
    return nested.createTempDir(name, timeToLive);
  }
}
