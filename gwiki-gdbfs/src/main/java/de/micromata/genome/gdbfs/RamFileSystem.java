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

import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.runtime.CallableX;
import de.micromata.genome.util.runtime.RuntimeIOException;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FileSystem implementation holds all in RAM.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class RamFileSystem extends AbstractFileSystem implements Serializable
{

  /**
   * The Constant serialVersionUID.
   */
  private static final long serialVersionUID = 2761704084927338307L;

  /**
   * The fs name.
   */
  protected String fsName;

  /**
   * The files.
   */
  protected Map<String, FsObjectContainer> files = new HashMap<String, FsObjectContainer>();

  /**
   * The modification counter.
   */
  protected long modificationCounter = 0;

  /**
   * Instantiates a new ram file system.
   */
  public RamFileSystem()
  {
    this("ramfs");
  }

  /**
   * Instantiates a new ram file system.
   *
   * @param fsName the fs name
   */
  public RamFileSystem(String fsName)
  {
    this.fsName = fsName;
    erase();
  }

  /**
   * Normalize file name.
   *
   * @param name the name
   * @return the string
   */
  protected String normalizeFileName(String name)
  {
    if (name.equals("/") == true) {
      return "";
    }
    if (name.startsWith("/") == true) {
      return name.substring(1);
    }
    // must not otherwise inconsitent
    // if (name.endsWith("/") == true) {
    // return name.substring(0, name.length() - 1);
    // }
    return name;
  }

  @Override
  public boolean delete(String name)
  {
    checkReadOnly();
    name = normalizeFileName(name);
    if (files.containsKey(name) == false) {
      return false;
    }
    files.remove(name);
    ++modificationCounter;
    addEvent(FileSystemEventType.Deleted, name, System.currentTimeMillis());
    return true;
  }

  @Override
  public void erase()
  {
    checkReadOnly();
    modificationCounter += files.size();
    files.clear();
    files.put("", new FsObjectContainer(new FsDirectoryObject(this, "", System.currentTimeMillis())));
  }

  @Override
  public boolean exists(String name)
  {
    name = normalizeFileName(name);
    return files.containsKey(name);
  }

  @Override
  public FsObject getFileObject(String name)
  {
    name = normalizeFileName(name);
    FsObjectContainer fc = files.get(name);
    if (fc == null) {
      return null;
    }
    return fc.getFile();
  }

  @Override
  public String getFileSystemName()
  {
    if (fsName == null) {
      fsName = "ramfs";
    }
    return fsName;
  }

  @Override
  public List<FsObject> listFiles(String name, Matcher<String> matcher, Character searchType, boolean recursive)
  {
    name = normalizeFileName(name);
    List<FsObject> ret = new ArrayList<FsObject>();
    String dirName = name + "/";
    if (name.length() == 0) {
      dirName = name;
    }
    for (Map.Entry<String, FsObjectContainer> me : files.entrySet()) {
      if (me.getKey().startsWith(dirName) == false) {
        continue;
      }
      if (me.getKey().equals(dirName) == true) {
        continue;
      }
      String pf = me.getKey();
      if (name.length() > 0) {
        pf = me.getKey().substring(name.length() + 1);
      }
      if (searchType != null) {
        if (searchType == 'F' && me.getValue().getFile().isFile() == false) {
          continue;
        }
        if (searchType == 'D' && me.getValue().getFile().isDirectory() == false) {
          continue;
        }
      }
      if (recursive == false) {
        if (pf.contains("/") == true) {
          continue;
        }
      }
      if (matcher != null) {
        if (matcher.match(pf) == false) {
          continue;
        }
      }
      /*if (me.getKey().equals(me.getValue().getFile().getName()) == false) {
        throw new FsException(
            "Inkonsistent fs: RamKey: " + me.getKey() + "; fileName: " + me.getValue().getFile().getName());
      } */
      ret.add(me.getValue().getFile());
    }
    return ret;
  }

  /**
   * Lock file.
   *
   * @param lockFile the lock file
   * @param timeout the timeout
   * @return true, if successful
   */
  public boolean lockFile(String lockFile, long timeout)
  {
    return true;
  }

  @Override
  public boolean mkdir(String name)
  {
    checkReadOnly();
    name = normalizeFileName(name);
    if (existsForWrite(name) == true) {
      return false;
    }
    String parent = getParentDirString(name);
    if (exists(parent) == false) {
      return false;
    }
    FsObjectContainer nc = new FsObjectContainer(new FsDirectoryObject(this, name, System.currentTimeMillis()));
    files.put(name, nc);
    addEvent(FileSystemEventType.Created, name, System.currentTimeMillis());
    return true;
  }

  @Override
  public void readBinaryFile(String name, OutputStream os)
  {
    name = normalizeFileName(name);
    ensureFileObject(name);
    FsObjectContainer c = files.get(name);
    try {
      IOUtils.copy(new ByteArrayInputStream(c.getByteData()), os);
    } catch (IOException ex) {
      throw new RuntimeIOException("Error reading file: " + name + "; " + ex.getMessage(), ex);
    }
  }

  @Override
  public boolean rename(String oldName, String newName)
  {
    checkReadOnly();
    oldName = normalizeFileName(oldName);
    newName = normalizeFileName(newName);
    if (exists(oldName) == false) {
      return false;
    }
    FsObjectContainer c = files.get(oldName);
    if (existsForWrite(newName) == false) {
      return false;
    }
    if (c.getFile().isFile() == false) {
      return false;
    }
    if (exists(getParentDirString(newName)) == false) {
      return false;
    }
    c.getFile().setName(newName);
    files.put(newName, c);
    files.remove(oldName);
    ++modificationCounter;
    addEvent(FileSystemEventType.Renamed, newName, System.currentTimeMillis(), oldName);
    return true;
  }

  @Override
  public void writeBinaryFile(String name, InputStream is, boolean overWrite)
  {
    checkReadOnly();

    name = normalizeFileName(name);
    boolean existed = existsForWrite(name);
    ensureFileToWrite(name, overWrite);
    FsObjectContainer old = files.get(name);
    FsFileObject newFile = new FsFileObject(this, name, "", System.currentTimeMillis());
    FsObjectContainer nc = new FsObjectContainer(newFile);
    try {
      byte[] data = IOUtils.toByteArray(is);
      nc.setByteData(data);
      newFile.setLength(data.length);
      newFile.setModifiedAt(new Date());
      if (old != null) {
        newFile.setCreatedAt(old.getFile().getCreatedAt());
        newFile.setCreatedBy(old.getFile().getCreatedBy());
      }
      if (name.equals(nc.getFile().getName()) == false) {
        throw new RuntimeException("Inkonsistent fs");
      }
      files.put(name, nc);
      ++modificationCounter;
      addEvent(existed == false ? FileSystemEventType.Created : FileSystemEventType.Modified, name,
          System.currentTimeMillis());
    } catch (IOException ex) {
      throw new RuntimeIOException("Error writing file: " + name + "; " + ex.getMessage(), ex);
    }
  }

  @Override
  public long getLastModified(final String name)
  {
    FsObject fsobj = getFileObject(name);
    if (fsobj == null) {
      return 0;
    }
    return fsobj.getLastModified();
  }

  @Override
  public long getModificationCounter()
  {
    return modificationCounter;
  }

  /**
   * Run in transaction.
   *
   * @param <R> the generic type
   * @param timeOut the time out
   * @param callback the callback
   * @return the r
   */
  public <R> R runInTransaction(long timeOut, CallableX<R, RuntimeException> callback)
  {
    return callback.call();
  }

  @Override
  public synchronized <R> R runInTransaction(String lockFile, long timeOut, boolean noModFs,
      CallableX<R, RuntimeException> callback)
  {
    try {
      this.wait(timeOut);
      R r = callback.call();
      return r;
    } catch (InterruptedException ex) {
      throw new FsFileLockException("Interupped while waiting for lock", ex);
    } finally {
      this.notify();
    }
  }

  /**
   * Adds the file.
   *
   * @param file the file
   */
  public void addFile(FsObject file)
  {
    final String name = normalizeFileName(file.getName());
    this.files.put(name, new FsObjectContainer(file));
  }

  public String getFsName()
  {
    return fsName;
  }

  public void setFsName(String fsName)
  {
    this.fsName = fsName;
  }

  public Map<String, FsObjectContainer> getFiles()
  {
    return files;
  }

  public void setFiles(Map<String, FsObjectContainer> files)
  {
    this.files = files;
  }

  @Override
  public boolean isReadOnly()
  {
    return readOnly;
  }

  @Override
  public void setReadOnly(boolean readOnly)
  {
    this.readOnly = readOnly;
  }

}
