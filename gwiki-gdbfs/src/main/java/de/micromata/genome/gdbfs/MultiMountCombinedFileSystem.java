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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;

import de.micromata.genome.util.matcher.BooleanListRulesFactory;
import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.matcher.MatcherFactory;
import de.micromata.genome.util.runtime.CallableX;
import de.micromata.genome.util.types.Pair;

/**
 * File System with a list of Filesystems, mounted by pattern. The last Filesystem should contain * as matcher rule.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class MultiMountCombinedFileSystem extends AbstractFileSystem implements InitializingBean
{

  /**
   * The file system name.
   */
  private String fileSystemName;

  /**
   * The file systems.
   */
  private Map<String, FileSystem> fileSystems;

  /**
   * The file system matchers.
   */
  private List<Pair<Matcher<String>, FileSystem>> fileSystemMatchers;

  /**
   * Instantiates a new multi mount combined file system.
   */
  public MultiMountCombinedFileSystem()
  {

  }

  /**
   * Instantiates a new multi mount combined file system.
   *
   * @param fileSystems the file systems
   */
  public MultiMountCombinedFileSystem(Map<String, FileSystem> fileSystems)
  {
    super();
    this.fileSystems = fileSystems;
  }

  /**
   * Gets the mount.
   *
   * @param name the name
   * @return the mount
   */
  public FileSystem getMount(String name)
  {
    if (name.startsWith("/") == true) {
      name = name.substring(1);
    }
    for (Pair<Matcher<String>, FileSystem> p : fileSystemMatchers) {
      if (p.getFirst().match(name) == true) {
        return p.getSecond();
      }
    }
    throw new FsException("No filesystem found for mount: " + name);
  }

  @Override
  public FileSystem getFsForRead(String name)
  {
    return getMount(name);
  }

  @Override
  public FileSystem getFsForWrite(String name)
  {
    return getMount(name);
  }

  @Override
  public void afterPropertiesSet() throws Exception
  {
    if (fileSystems == null || fileSystems.isEmpty() == true) {
      throw new FsException("No file systems are configured in MultiMountCombinedFileSystem");
    }
    fileSystemMatchers = new ArrayList<Pair<Matcher<String>, FileSystem>>();
    MatcherFactory<String> fac = new BooleanListRulesFactory<String>();
    for (Map.Entry<String, FileSystem> me : fileSystems.entrySet()) {
      fileSystemMatchers.add(Pair.make(fac.createMatcher(me.getKey()), me.getValue()));
      me.getValue().setAutoCreateDirectories(isAutoCreateDirectories());
    }

  }

  @Override
  public void checkEvents(boolean force)
  {
    for (Pair<Matcher<String>, FileSystem> p : fileSystemMatchers) {
      p.getSecond().checkEvents(force);
    }
  }

  @Override
  public void cleanupTempDirs()
  {
    for (Pair<Matcher<String>, FileSystem> p : fileSystemMatchers) {
      p.getSecond().cleanupTempDirs();
    }
  }

  @Override
  public FsDirectoryObject createTempDir(String name, long timeToLive)
  {
    String ptdir = FileNameUtils.join(getTempDirName(), name);
    return getFsForWrite(ptdir).createTempDir(name, timeToLive);
  }

  @Override
  public boolean delete(String name)
  {
    return getFsForWrite(name).delete(name);
  }

  @Override
  public boolean deleteRecursive(String name)
  {
    return getFsForWrite(name).deleteRecursive(name);
  }

  @Override
  public void erase()
  {
    for (Pair<Matcher<String>, FileSystem> p : fileSystemMatchers) {
      p.getSecond().erase();
    }
  }

  @Override
  public boolean exists(String name)
  {
    return getFsForRead(name).exists(name);
  }

  @Override
  public boolean existsForWrite(String name)
  {
    return getFsForWrite(name).existsForWrite(name);
  }

  @Override
  public FsObject getFileObject(String name)
  {
    return getFsForRead(name).getFileObject(name);
  }

  @Override
  public String getFileSystemName()
  {
    if (fileSystemName == null) {
      StringBuilder sb = new StringBuilder();
      sb.append("mmcfs(");
      for (Pair<Matcher<String>, FileSystem> p : fileSystemMatchers) {
        sb.append(p.getFirst().toString()).append("=").append(p.getSecond().getFileSystemName()).append("|");
      }
      sb.append(")");
      fileSystemName = sb.toString();

    }
    return fileSystemName;
  }

  @Override
  public long getLastModified(String name)
  {
    return getFsForRead(name).getLastModified(name);
  }

  @Override
  public String getMimeType(String fileName)
  {
    return getFsForRead(fileName).getMimeType(fileName);
  }

  @Override
  public long getModificationCounter()
  {
    return getFsForWrite("").getModificationCounter();
  }

  @Override
  public boolean isReadOnly()
  {
    return getFsForWrite("").isReadOnly();
  }

  @Override
  public boolean isTextMimeType(String mimType)
  {
    return getFsForWrite("").isTextMimeType(mimType);
  }

  @Override
  public List<FsObject> listFiles(String name, Matcher<String> matcher, Character searchType, boolean recursive)
  {

    List<FsObject> ret = new ArrayList<FsObject>();
    // Set<String> names = new TreeSet<String>();
    for (Pair<Matcher<String>, FileSystem> p : fileSystemMatchers) {
      List<FsObject> ret1 = p.getSecond().listFiles(name, matcher, searchType, recursive);
      for (FsObject fso : ret1) {

        String fname = fso.getName();
        if (fname.startsWith("/") == true) {
          fname = fname.substring(1);
        }
        if (p.getFirst().match(fname) == false) {
          continue;
        }
        ret.add(fso);
      }
      // ret.addAll(ret1);
    }
    return ret;
  }

  @Override
  public void setAutoCreateDirectories(boolean autoCreateDirectories)
  {
    if (fileSystemMatchers != null) {
      for (Pair<Matcher<String>, FileSystem> me : fileSystemMatchers) {
        me.getSecond().setAutoCreateDirectories(autoCreateDirectories);
      }
    } else {
      for (FileSystem fe : fileSystems.values()) {
        fe.setAutoCreateDirectories(autoCreateDirectories);
      }
    }
    super.setAutoCreateDirectories(autoCreateDirectories);
  }

  @Override
  public boolean mkdir(String name)
  {
    return getFsForWrite(name).mkdir(name);
  }

  @Override
  public boolean mkdirs(String name)
  {
    return getFsForWrite(name).mkdirs(name);
  }

  @Override
  public void readBinaryFile(String file, OutputStream os)
  {
    getFsForRead(file).readBinaryFile(file, os);
  }

  @Override
  public byte[] readBinaryFile(String file)
  {
    return getFsForRead(file).readBinaryFile(file);
  }

  @Override
  public String readTextFile(String file)
  {
    return getFsForRead(file).readTextFile(file);
  }

  @Override
  public void registerListener(FileSystemEventType eventType, Matcher<String> fileNameMatcher,
      FileSystemEventListener listener)
  {
    for (Pair<Matcher<String>, FileSystem> p : fileSystemMatchers) {
      p.getSecond().registerListener(eventType, fileNameMatcher, listener);
    }
  }

  @Override
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

  @Override
  public <R> R runInTransaction(String lockFile, long timeOut, boolean noModFs, CallableX<R, RuntimeException> callback)
  {
    return fileSystemMatchers.get(0).getSecond().runInTransaction(lockFile, timeOut, noModFs, callback);
  }

  @Override
  public void setReadOnly(boolean readOnly)
  {
    for (Pair<Matcher<String>, FileSystem> p : fileSystemMatchers) {
      p.getSecond().setReadOnly(readOnly);
    }

  }

  @Override
  public void writeBinaryFile(String file, InputStream is, boolean overWrite)
  {
    getFsForWrite(file).writeBinaryFile(file, is, overWrite);
  }

  @Override
  public void writeBinaryFile(String file, byte[] data, boolean overWrite)
  {
    getFsForWrite(file).writeBinaryFile(file, data, overWrite);
  }

  @Override
  public void writeFile(String file, byte[] data, boolean overWrite)
  {
    getFsForWrite(file).writeFile(file, data, overWrite);
  }

  @Override
  public void writeFile(String file, InputStream is, boolean overWrite)
  {
    getFsForWrite(file).writeFile(file, is, overWrite);
  }

  @Override
  public void writeTextFile(String file, String content, boolean overWrite)
  {
    getFsForWrite(file).writeTextFile(file, content, overWrite);
  }

  public Map<String, FileSystem> getFileSystems()
  {
    return fileSystems;
  }

  public void setFileSystems(Map<String, FileSystem> fileSystems)
  {
    this.fileSystems = fileSystems;
  }

  public void setFileSystemName(String fileSystemName)
  {
    this.fileSystemName = fileSystemName;
  }

}
