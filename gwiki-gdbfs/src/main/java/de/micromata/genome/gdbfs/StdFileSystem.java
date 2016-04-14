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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.runtime.CallableX;
import de.micromata.genome.util.runtime.RuntimeIOException;
import de.micromata.genome.util.types.TimeInMillis;

/**
 * FileSystem implementation using the standard operation system file system.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class StdFileSystem extends AbstractFileSystem
{
  public static final String STD_LOCKFILENAME = ".fslock";

  /**
   * always true, only for unittest may will be false.
   */
  private boolean localSync = true;

  private String root;

  private File rootFile;

  private String canonRoot;

  private String globalLockFileName = STD_LOCKFILENAME;

  private StdFileSystemLock globalLock;

  private int localModificationCount;

  private ThreadLocal<Map<String, StdFileSystemLock>> lockedLocks = new ThreadLocal<Map<String, StdFileSystemLock>>()
  {

    @Override
    protected Map<String, StdFileSystemLock> initialValue()
    {
      return new HashMap<String, StdFileSystemLock>();
    }
  };

  public StdFileSystem()
  {

  }

  public StdFileSystem(String root)
  {
    setRoot(root);
  }

  @Override
  public String toString()
  {
    return canonRoot;
  }

  public int incLocalModificationCount()
  {
    return ++localModificationCount;
  }

  public void eraseRec(String name)
  {
    List<FsObject> dirs = listFiles(name, null, 'D', false);
    for (FsObject dir : dirs) {
      eraseRec(dir.getName());
    }
    List<FsObject> files = listFiles(name, null, 'F', false);
    for (FsObject file : files) {
      delete(file.getName());
    }
    delete(name);
  }

  @Override
  public void erase()
  {
    eraseRec("");
    setRoot(canonRoot);
  }

  @Override
  public boolean exists(String name)
  {
    File f = new File(rootFile, name);
    try {
      ensureFileInFs(f);
      return f.exists();
    } catch (AccessControlException ex) {
      // on some fs access is not allowed.
      return false;
    }
  }

  @Override
  public FsObject getFileObject(String name)
  {
    File f;
    if (name.length() == 0 || name.equals("/") == true) {
      f = rootFile;
    } else {
      f = new File(rootFile, name);
    }
    ensureFileInFs(f);
    if (f.exists() == false) {
      return null;
    }
    return fileToFsObject(f);
  }

  @Override
  public String getFileSystemName()
  {
    return root;
  }

  @Override
  public boolean mkdir(String name)
  {
    checkReadOnly();
    File f = new File(rootFile, name);
    ensureFileInFs(f);
    boolean ret = f.mkdir();
    if (ret == true) {
      incLocalModificationCount();
      addEvent(FileSystemEventType.Created, name, System.currentTimeMillis());
    }
    return ret;
  }

  protected void ensureFileInFs(File f)
  {
    String cn;
    try {
      // TODO note: this is on windows a very expensive method:
      // long ms = System.currentTimeMillis();
      cn = f.getCanonicalPath();
      // long end = System.currentTimeMillis();
      // long dif = end - ms;
    } catch (IOException ex) {
      throw new FsInvalidNameException("Cannot resolve filename: " + f.getName() + "; " + ex.getMessage(), ex);
    }
    ensureCanonFileInFs(cn);
  }

  protected void ensureCanonFileInFs(String canonPath)
  {
    if (canonPath.startsWith(canonRoot) == false) {
      throw new FsInvalidNameException("File is not content of file system: " + canonPath);
    }
  }

  @Override
  public boolean mkdirs(String name)
  {
    checkReadOnly();
    File f = new File(rootFile, name);
    ensureFileInFs(f);
    boolean ret = f.mkdirs();
    if (ret == true) {
      incLocalModificationCount();
      addEvent(FileSystemEventType.Created, name, System.currentTimeMillis());
    }
    return ret;
  }

  @Override
  public boolean rename(String oldName, String newName)
  {
    checkReadOnly();
    File f = new File(rootFile, oldName);
    ensureFileInFs(f);
    File toFile = new File(rootFile, newName);
    ensureFileInFs(toFile);
    boolean ret = f.renameTo(toFile);
    if (ret == true) {
      incLocalModificationCount();
      addEvent(FileSystemEventType.Renamed, newName, System.currentTimeMillis(), oldName);

    }
    return ret;
  }

  @Override
  public String readTextFile(String name)
  {

    byte[] data = readBinaryFile(name);
    try {
      return new String(data, STANDARD_STRING_ENCODING);
    } catch (UnsupportedEncodingException ex) {
      throw new RuntimeException(ex);
    }
  }

  @Override
  public void readBinaryFile(String name, OutputStream os)
  {
    File f = new File(rootFile, name);
    ensureFileInFs(f);
    try {
      FileInputStream fin = new FileInputStream(f);
      IOUtils.copy(fin, os);
      IOUtils.closeQuietly(fin);
    } catch (IOException ex) {
      throw new RuntimeIOException("Failed to read file: " + f.getName() + "; " + ex.getMessage(), ex);
    }
  }

  private void checkUnexistantFile(String file)
  {
    File f = new File(rootFile, file);
    if (f.exists() == true) {
      throw new FsFileExistsException("File exists: " + file);
    }
  }

  protected void createParentDirectories(File f)
  {
    if (isAutoCreateDirectories() == false) {
      return;
    }
    File pdir = f.getParentFile();
    if (pdir.exists() == true) {
      return;
    }
    pdir.mkdirs();
  }

  @Override
  public void writeBinaryFile(String name, InputStream is, boolean overWrite)
  {
    checkReadOnly();
    FileSystemEventType eventType = FileSystemEventType.Modified;

    File f = new File(rootFile, name);
    createParentDirectories(f);
    ensureFileInFs(f);
    if (overWrite == false) {
      checkUnexistantFile(name);
      eventType = FileSystemEventType.Created;
    } else {
      if (f.exists() == false) {
        eventType = FileSystemEventType.Created;
      }
    }

    try {
      FileOutputStream fout = new FileOutputStream(f);
      IOUtils.copy(is, fout);
      IOUtils.closeQuietly(fout);
      incLocalModificationCount();
      addEvent(eventType, name, System.currentTimeMillis());
    } catch (FileNotFoundException ex) {
      throw new FsFileExistsException("Parent file not found: " + name, ex);
    } catch (IOException ex) {
      throw new RuntimeIOException(ex);
    }
  }

  @Override
  public void writeTextFile(String name, String content, boolean overWrite)
  {
    checkReadOnly();
    if (overWrite == false) {
      checkUnexistantFile(name);
    }
    byte[] data = null;
    if (content != null) {
      try {
        data = content.getBytes(STANDARD_STRING_ENCODING);
      } catch (UnsupportedEncodingException ex) {
        throw new RuntimeException(ex);
      }

    }
    writeBinaryFile(name, data, overWrite);
  }

  private String canonPath(File f)
  {
    try {
      return f.getCanonicalPath();
    } catch (IOException ex) {
      throw new RuntimeIOException(ex);
    }
  }

  protected String getRelName(File f)
  {
    String cn = canonPath(f);
    String r = cn.substring(canonRoot.length());
    return r.replace('\\', '/');
  }

  protected FsObject fileToFsObject(File f)
  {
    ensureFileInFs(f);
    FsObject ret;
    String rn = getRelName(f);
    if (rn.startsWith("/") == true) {
      rn = rn.substring(1);
    }
    if (f.isDirectory() == true) {
      ret = new FsDirectoryObject(this, rn, f.lastModified());
    } else if (f.isFile() == true) {
      ret = new FsFileObject(this, rn, "", f.lastModified());
      ret.setLength((int) f.length());
    } else {
      return null;
    }
    ret.setModifiedAt(new Date(f.lastModified()));
    ret.setCreatedAt(ret.getModifiedAt());
    return ret;
  }

  @Override
  public List<FsObject> listFiles(final String name, final Matcher<String> matcher, final Character searchType,
      boolean recursive)
  {
    final List<FsObject> ret = new ArrayList<FsObject>();
    File f = new File(rootFile, name);
    try {
      if (f.exists() == false || f.isDirectory() == false) {
        return ret;
      }
    } catch (AccessControlException ex) {
      return ret;
    }
    listFiles(canonPath(f), f, matcher, searchType, ret, recursive);
    return ret;
  }

  public boolean isSystemFile(File f)
  {
    String name = f.getName();
    return globalLockFileName.equals(name);
  }

  protected void listFiles(String absRootName, File f, Matcher<String> matcher, final Character searchType,
      List<FsObject> ret,
      boolean recursive)
  {

    File[] files = f.listFiles();
    if (files == null) {
      return;
    }
    for (File el : files) {
      if (isSystemFile(el) == true) {
        continue;
      }
      boolean matches = true;
      if (matcher != null) {
        String tb = canonPath(el);
        String relp = tb.substring(absRootName.length());
        relp = StringUtils.replace(relp, "\\", "/");
        if (relp.startsWith("/") == true) {
          relp = relp.substring(1);
        }
        if (matcher.match(relp) == false) {
          matches = false;
        }
      }
      if (el.isDirectory() == true) {
        if (matches == true && (searchType == null || searchType == 'D')) {
          ret.add(fileToFsObject(el));
        }
        if (recursive == true) {
          listFiles(absRootName, el, matcher, searchType, ret, recursive);
        }
      } else if (el.isFile() == true) {
        if (matches == true && (searchType == null || searchType == 'F')) {
          ret.add(fileToFsObject(el));
        }
      }
    }
  }

  @Override
  public boolean delete(String name)
  {
    checkReadOnly();
    File f = new File(rootFile, name);
    ensureFileInFs(f);
    boolean ret = f.delete();
    if (ret == true) {
      incLocalModificationCount();
      addEvent(FileSystemEventType.Deleted, name, System.currentTimeMillis());
    }
    return ret;
  }

  @Override
  public long getLastModified(final String name)
  {
    File f = new File(rootFile, name);
    ensureFileInFs(f);
    if (f.exists() == false) {
      return 0;
    }
    return f.lastModified();
  }

  @Override
  protected void finalize() throws Throwable
  {
    if (globalLock == null) {
      return;
    }
    globalLock.closeLock();
    globalLock = null;
  }

  public StdFileSystemLock getGlobalLock()
  {
    if (globalLock != null) {
      return globalLock;
    }
    File f = new File(rootFile, ".fslock");
    globalLock = new StdFileSystemLock(f);
    return globalLock;
  }

  public void releaseLock(StdFileSystemLock lock, boolean noFsMod)
  {
    String canonf = canonPath(lock.getFile());
    StdFileSystemLock cl = lockedLocks.get().get(canonf);
    if (cl != null) {
      if (cl.decLockCount() > 0) {
        return;
      }
    }
    try {
      lockedLocks.get().remove(canonf);
      if (lock == globalLock) {
        int mod = localModificationCount;
        localModificationCount = 0;
        if (mod != 0 && noFsMod == true) {
          long gmod = getModificationCounter();
          setModificationCounter(gmod + mod);
        }
      }
    } finally {
      lock.releaseLock();
      // if (lock != globalLock) {
      lock.closeLock();
      // }
    }
  }

  protected StdFileSystemLock getLock(String name)
  {
    if (name == null) {
      return getGlobalLock();
    }
    File f = new File(rootFile, name);
    ensureFileInFs(f);
    StdFileSystemLock lock = new StdFileSystemLock(f);
    return lock;
  }

  protected StdFileSystemLock getLock(String name, long timeOutMs)
  {
    if (timeOutMs == -1) {
      timeOutMs = TimeInMillis.MINUTE * 20;
    }
    StdFileSystemLock lock;
    String nomName = name;
    if (name == null) {
      nomName = globalLockFileName;

    }
    File f = new File(rootFile, nomName);
    ensureFileInFs(f);
    String fqName = canonPath(f);
    lock = lockedLocks.get().get(fqName);

    if (lock != null) {
      lock.incLockCount();
      return lock;
    }
    lock = getLock(name);
    if (lock.aquireLock(timeOutMs) == false) {
      releaseLock(lock, true);
      return null;
    }
    lock.incLockCount();
    lockedLocks.get().put(fqName, lock);
    return lock;
  }

  protected <R> R runInTransactionInternal(String lockFile, long timeOutMs, CallableX<R, RuntimeException> callback,
      boolean noFsMod)
  {
    StdFileSystemLock lock = getLock(lockFile, timeOutMs);
    if (lock == null) {
      throw new FsFileLockException(
          "Cannot lock file " + (lockFile == null ? globalLockFileName : lockFile) + " in " + timeOutMs + " ms");
    }
    try {
      return callback.call();
    } finally {
      releaseLock(lock, noFsMod);
    }
  }

  @Override
  public long getModificationCounter()
  {
    Long ret = runInTransaction(null, 10000L, false, new CallableX<Long, RuntimeException>()
    {
      @Override
      public Long call() throws RuntimeException
      {
        try {
          int bufSize = 1000;
          ByteBuffer buffer = ByteBuffer.allocate(bufSize);
          int readed = globalLock.getChannel().read(buffer, 0);
          if (readed == 0 || readed == -1) {
            return 0L;
          }
          byte[] data = buffer.array();
          try {
            return Long.parseLong(new String(data, 0, readed), 10);
          } catch (NumberFormatException ex) {
            return 0L;
          }
        } catch (IOException ex) {
          return 0L;
        }
      }
    });
    return ret;
  }

  public void setModificationCounter(long nc)
  {
    byte[] data = Long.toString(nc).getBytes();
    try {
      globalLock.openLock();
      globalLock.getChannel().write(ByteBuffer.wrap(data), 0);
      globalLock.getChannel().force(true);
    } catch (IOException ex) {
      throw new RuntimeIOException(ex);
    }
  }

  @Override
  public <R> R runInTransaction(String lockFile, long timeOut, boolean noFsMod, CallableX<R, RuntimeException> callback)
  {
    if (localSync == true) {
      synchronized (this) {
        return runInTransactionInternal(lockFile, timeOut, callback, noFsMod);
      }
    }
    return runInTransactionInternal(lockFile, timeOut, callback, noFsMod);
  }

  public String getRoot()
  {
    return root;
  }

  public void setRoot(String root)
  {
    this.root = root;
    this.canonRoot = canonPath(new File(root));
    this.rootFile = new File(canonRoot);
    if (rootFile.exists() == false) {
      boolean success = rootFile.mkdirs();
      if (success == false) {
        // strange behavoir on Windows. first call return false, but after a sleep return true;
        try {
          Thread.sleep(100);
        } catch (InterruptedException ex) {
          // ignore it.
        }
        success = rootFile.mkdirs();
        if (success == false) {
          throw new FsException("Cannot create FileSystem root directory: " + canonRoot);
        }
      }
    }
  }

  public boolean isLocalSync()
  {
    return localSync;
  }

  public void setLocalSync(boolean localSync)
  {
    this.localSync = localSync;
  }

  public String getGlobalLockFileName()
  {
    return globalLockFileName;
  }

  public void setGlobalLockFileName(String globalLockFileName)
  {
    this.globalLockFileName = globalLockFileName;
  }

  public int getLocalModificationCount()
  {
    return localModificationCount;
  }

  public void setLocalModificationCount(int modificationCount)
  {
    this.localModificationCount = modificationCount;
  }

}
