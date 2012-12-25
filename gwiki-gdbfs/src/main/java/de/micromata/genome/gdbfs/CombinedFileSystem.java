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
import java.util.Set;
import java.util.TreeSet;

import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.runtime.CallableX;

/**
 * Combines a primary read/write and a secondary read only file system.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public abstract class CombinedFileSystem extends AbstractFileSystem
{
  protected FileSystem primary;

  protected FileSystem secondary;

  public CombinedFileSystem()
  {

  }

  public CombinedFileSystem(FileSystem primary, FileSystem secondary)
  {
    this.primary = primary;
    this.secondary = secondary;
  }

  public abstract FileSystem getFsForRead(String name);

  public abstract FileSystem getFsForWrite(String name);

  public void checkEvents(boolean force)
  {
    primary.checkEvents(force);
    secondary.checkEvents(force);
  }

  public void cleanupTempDirs()
  {
    primary.cleanupTempDirs();
  }

  public FsDirectoryObject createTempDir(String name, long timeToLive)
  {
    return primary.createTempDir(name, timeToLive);
  }

  public boolean delete(String name)
  {
    return getFsForWrite(name).delete(name);
  }

  public boolean deleteRecursive(String name)
  {
    return getFsForWrite(name).deleteRecursive(name);
  }

  public void erase()
  {
    primary.erase();
  }

  public boolean exists(String name)
  {
    return getFsForRead(name).exists(name);
  }

  public boolean existsForWrite(String name)
  {
    return getFsForWrite(name).existsForWrite(name);
  }

  public FsObject getFileObject(String name)
  {
    return getFsForRead(name).getFileObject(name);
  }

  public String getFileSystemName()
  {
    return "combinedfs(" + primary.getFileSystemName() + "|" + secondary.getFileSystemName() + ")";
  }

  public long getLastModified(String name)
  {
    return getFsForRead(name).getLastModified(name);
  }

  public String getMimeType(String fileName)
  {
    return getFsForRead(fileName).getMimeType(fileName);
  }

  public long getModificationCounter()
  {
    return getFsForWrite("").getModificationCounter();
  }

  public boolean isReadOnly()
  {
    return getFsForWrite("").isReadOnly();
  }

  public boolean isTextMimeType(String mimType)
  {
    return getFsForWrite("").isTextMimeType(mimType);
  }

  public List<FsObject> listFiles(String name, Matcher<String> matcher, Character searchType, boolean recursive)
  {
    List<FsObject> ret1 = secondary.listFiles(name, matcher, searchType, recursive);
    List<FsObject> ret2 = primary.listFiles(name, matcher, searchType, recursive);
    List<FsObject> ret = new ArrayList<FsObject>();
    Set<String> names = new TreeSet<String>();
    ret.addAll(ret2);
    for (FsObject fs : ret2) {
      names.add(fs.getName());
    }
    for (FsObject fs : ret1) {
      if (names.contains(fs.name) == false) {
        ret.add(fs);
      }
    }
    return ret;
  }

  public boolean mkdir(String name)
  {
    return getFsForWrite(name).mkdir(name);
  }

  public boolean mkdirs(String name)
  {
    return getFsForWrite(name).mkdirs(name);
  }

  public void readBinaryFile(String file, OutputStream os)
  {
    getFsForRead(file).readBinaryFile(file, os);
  }

  public byte[] readBinaryFile(String file)
  {
    return getFsForRead(file).readBinaryFile(file);
  }

  public String readTextFile(String file)
  {
    return getFsForRead(file).readTextFile(file);
  }

  public void registerListener(FileSystemEventType eventType, Matcher<String> fileNameMatcher, FileSystemEventListener listener)
  {
    primary.registerListener(eventType, fileNameMatcher, listener);
    secondary.registerListener(eventType, fileNameMatcher, listener);
  }

  public boolean rename(String oldName, String newName)
  {
    FileSystem fsr = getFsForRead(oldName);
    FileSystem fsw = getFsForWrite(newName);
    if (fsr == fsw) {
      return fsr.rename(oldName, newName);
    }
    byte[] data = fsr.readBinaryFile(oldName);
    fsw.writeBinaryFile(newName, data, false);
    return true;
  }

  public <R> R runInTransaction(String lockFile, long timeOut, boolean noModFs, CallableX<R, RuntimeException> callback)
  {
    return primary.runInTransaction(lockFile, timeOut, noModFs, callback);
  }

  public void setReadOnly(boolean readOnly)
  {
    primary.setReadOnly(readOnly);
    secondary.setReadOnly(readOnly);

  }

  public void writeBinaryFile(String file, InputStream is, boolean overWrite)
  {
    getFsForWrite(file).writeBinaryFile(file, is, overWrite);
  }

  public void writeBinaryFile(String file, byte[] data, boolean overWrite)
  {
    getFsForWrite(file).writeBinaryFile(file, data, overWrite);
  }

  public void writeFile(String file, byte[] data, boolean overWrite)
  {
    getFsForWrite(file).writeFile(file, data, overWrite);
  }

  public void writeFile(String file, InputStream is, boolean overWrite)
  {
    getFsForWrite(file).writeFile(file, is, overWrite);
  }

  public void writeTextFile(String file, String content, boolean overWrite)
  {
    getFsForWrite(file).writeTextFile(file, content, overWrite);
  }

  @Override
  public void setAutoCreateDirectories(boolean autoCreateDirectories)
  {
    if (primary != null) {
      primary.setAutoCreateDirectories(autoCreateDirectories);
    }
    if (secondary != null) {
      secondary.setAutoCreateDirectories(autoCreateDirectories);
    }
    super.setAutoCreateDirectories(autoCreateDirectories);
  }

  public FileSystem getPrimary()
  {
    return primary;
  }

  public void setPrimary(FileSystem primary)
  {
    this.primary = primary;
  }

  public FileSystem getSecondary()
  {
    return secondary;
  }

  public void setSecondary(FileSystem secondary)
  {
    this.secondary = secondary;
  }

}
