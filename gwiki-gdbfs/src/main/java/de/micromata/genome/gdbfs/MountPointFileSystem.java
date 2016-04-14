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

import org.springframework.beans.factory.InitializingBean;

import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.runtime.CallableX;

/**
 * FileSystem which has another mountpoint.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public abstract class MountPointFileSystem extends AbstractFileSystem implements InitializingBean
{
  protected FileSystem parentFileSystem;

  protected String path;

  public MountPointFileSystem()
  {

  }

  public MountPointFileSystem(FileSystem parentFileSystem, String path)
  {
    this.parentFileSystem = parentFileSystem;
    this.path = path;
    setReadOnly(parentFileSystem.isReadOnly());
  }

  protected abstract String getFqName(String name);

  public void afterPropertiesSet() throws Exception
  {
    // needed, because getFileSystemForWrite
    setAutoCreateDirectories(true);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gdbfs.FileSystem#delete(java.lang.String)
   */
  public boolean delete(String name)
  {
    return parentFileSystem.delete(getFqName(name));
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gdbfs.FileSystem#erase()
   */
  public void erase()
  {
    throw new FsException("Erase not supported on SubFileSystem");
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gdbfs.FileSystem#exists(java.lang.String)
   */
  public boolean exists(String name)
  {
    return parentFileSystem.exists(getFqName(name));
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gdbfs.FileSystem#getFileObject(java.lang.String)
   */
  public FsObject getFileObject(String name)
  {
    FsObject fsobj = parentFileSystem.getFileObject(getFqName(name));
    parentToThis(fsobj);
    return fsobj;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gdbfs.FileSystem#getFileSystemName()
   */
  public String getFileSystemName()
  {
    return parentFileSystem.getFileSystemName() + "/" + path;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gdbfs.FileSystem#getLastModified(java.lang.String)
   */
  public long getLastModified(String name)
  {
    return parentFileSystem.getLastModified(getFqName(name));
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gdbfs.FileSystem#getModificationCounter()
   */
  public long getModificationCounter()
  {
    return parentFileSystem.getModificationCounter();
  }

  protected final void parentToThis(FsObject f)
  {
    if (f == null) {
      return;
    }
    String fn = f.getName();
    if (fn.startsWith("/") == true) {
      fn = fn.substring(1);
    }
    String fnp = fn;
    String npath = path;
    if (npath.startsWith("/") == true) {
      npath = npath.substring(1);
    }
    if (fnp.startsWith(npath) == true) {
      fnp = fn.substring(npath.length());
    }
    if (fnp.startsWith("/") == true) {
      fnp = fnp.substring(1);
    }
    if (fnp.endsWith("/") == true) {
      fnp = fnp.substring(fnp.length() - 1);
    }
    f.setFileSystem(this);
    f.setName(fnp);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gdbfs.FileSystem#listFiles(java.lang.String, de.micromata.genome.util.matcher.Matcher, java.lang.Character,
   * boolean)
   */
  public List<FsObject> listFiles(String name, Matcher<String> matcher, Character searchType, boolean recursive)
  {
    List<FsObject> lso = parentFileSystem.listFiles(getFqName(name), matcher, searchType, recursive);
    List<FsObject> ret = new ArrayList<FsObject>(lso.size());
    for (FsObject f : lso) {
      FsObject fc = (FsObject) f.clone();
      parentToThis(fc);
      ret.add(fc);

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
    return parentFileSystem.mkdir(getFqName(name));
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gdbfs.FileSystem#readBinaryFile(java.lang.String, java.io.OutputStream)
   */
  public void readBinaryFile(String file, OutputStream os)
  {
    parentFileSystem.readBinaryFile(getFqName(file), os);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gdbfs.FileSystem#rename(java.lang.String, java.lang.String)
   */
  public boolean rename(String oldName, String newName)
  {
    String old = getFqName(oldName);
    String ne = getFqName(newName);
    return parentFileSystem.rename(old, ne);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gdbfs.FileSystem#runInTransaction(java.lang.String, long, boolean, de.micromata.genome.util.runtime.CallableX)
   */
  public <R> R runInTransaction(String lockFile, long timeOut, boolean noModFs, CallableX<R, RuntimeException> callback)
  {
    if (lockFile == null) {
      lockFile = StdFileSystem.STD_LOCKFILENAME;
    }
    return parentFileSystem.runInTransaction(getFqName(lockFile), timeOut, noModFs, callback);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gdbfs.FileSystem#writeBinaryFile(java.lang.String, java.io.InputStream, boolean)
   */
  public void writeBinaryFile(String file, InputStream is, boolean overWrite)
  {
    parentFileSystem.writeBinaryFile(getFqName(file), is, overWrite);
  }

  public FileSystem getParentFileSystem()
  {
    return parentFileSystem;
  }

  public void setParentFileSystem(FileSystem parentFileSystem)
  {
    this.parentFileSystem = parentFileSystem;
  }

  public String getPath()
  {
    return path;
  }

  public void setPath(String path)
  {
    this.path = path;
  }

  @Override
  public void setAutoCreateDirectories(boolean autoCreateDirectories)
  {
    parentFileSystem.setAutoCreateDirectories(autoCreateDirectories);
    super.setAutoCreateDirectories(autoCreateDirectories);
  }

}
