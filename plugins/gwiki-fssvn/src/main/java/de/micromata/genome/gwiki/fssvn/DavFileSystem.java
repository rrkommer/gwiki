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

package de.micromata.genome.gwiki.fssvn;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.googlecode.sardine.DavResource;
import com.googlecode.sardine.Sardine;
import com.googlecode.sardine.SardineFactory;
import com.googlecode.sardine.util.SardineException;

import de.micromata.genome.gdbfs.AbstractFileSystem;
import de.micromata.genome.gdbfs.FileNameUtils;
import de.micromata.genome.gdbfs.FsException;
import de.micromata.genome.gdbfs.FsObject;
import de.micromata.genome.gwiki.model.logging.GWikiLogCategory;
import de.micromata.genome.logging.GLog;
import de.micromata.genome.logging.LogExceptionAttribute;
import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.runtime.CallableX;

/**
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class DavFileSystem extends AbstractFileSystem
{
  private String url;

  private String user;

  private String pass;

  private Sardine sardine;

  public DavFileSystem()
  {
    setReadOnly(true);
  }

  public DavFileSystem(String url)
  {
    this();
    this.url = url;
  }

  public DavFileSystem(String url, String user, String pass)
  {
    this(url);
    this.user = user;
    this.pass = pass;
  }

  protected void connect()
  {
    if (sardine != null) {
      return;
    }
    try {
      if (StringUtils.isNotEmpty(user) == true) {
        sardine = SardineFactory.begin(user, pass, SslUtils.createEasySSLSocketFactory());
      } else {
        sardine = SardineFactory.begin(SslUtils.createEasySSLSocketFactory());
      }
    } catch (SardineException ex) {
      throw new FsException("Cannot initialize webdav: " + ex.getMessage(), ex);
    }
  }

  protected String gu(String localName)
  {
    return FileNameUtils.join(url, localName);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gdbfs.FileSystem#exists(java.lang.String)
   */
  @Override
  public boolean exists(String name)
  {
    connect();
    try {
      return sardine.exists(gu(name));
    } catch (SardineException ex) {
      throw new FsException(ex);
    }
  }

  protected FsObject trans(DavResource res)
  {
    return trans(res, "");
  }

  protected FsObject trans(DavResource res, String localPrefix)
  {

    Date lm = res.getModified();
    long lml = 0;
    if (lm != null) {
      lml = lm.getTime();
    }
    Map<String, String> attrs = res.getCustomProps();
    FsObject ret;
    if (res.isDirectory() == true) {
      ret = new DavFsDirectoryObject(this, FileNameUtils.join(localPrefix, res.getName()), lml, attrs);
    } else {
      ret = new DavFsFileObject(this, FileNameUtils.join(localPrefix, res.getName()), res.getContentType(), lml, attrs);
      Long cl = res.getContentLength();
      if (cl != null) {
        ret.setLength((int) (long) res.getContentLength());
      }
    }
    Date ct = res.getCreation();
    if (ct != null) {
      ret.setCreatedAt(ct);
    }

    String creator = attrs.get("creator-displayname");
    if (creator == null) {
      creator = attrs.get("creator");
    }
    if (creator != null) {
      ret.setCreatedBy(creator);
      ret.setModifiedBy(creator);
    }
    ret.setAttributes(res.getCustomProps().toString());
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gdbfs.FileSystem#getFileObject(java.lang.String)
   */
  @Override
  public FsObject getFileObject(String name)
  {
    connect();
    List<DavResource> df;
    try {
      df = sardine.getResources(gu(name));
    } catch (SardineException ex) {
      throw new FsException(ex);
    }
    if (df.isEmpty() == true) {
      return null;
    }
    return trans(df.get(0));
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gdbfs.FileSystem#getFileSystemName()
   */
  @Override
  public String getFileSystemName()
  {
    return url;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gdbfs.FileSystem#getLastModified(java.lang.String)
   */
  @Override
  public long getLastModified(String name)
  {
    FsObject f = getFileObject(name);
    if (f == null) {
      return 0;
    }
    return f.getLastModified();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gdbfs.FileSystem#getModificationCounter()
   */
  @Override
  public long getModificationCounter()
  {
    FsObject obj = getFileObject("");
    if (obj instanceof FsAttributedObject) {
      FsAttributedObject ao = (FsAttributedObject) obj;
      String vname = ao.getAttribute("version-name");
      if (StringUtils.isNumeric(vname) == true) {
        return Long.parseLong(vname);
      }
    }
    return 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gdbfs.FileSystem#listFiles(java.lang.String, de.micromata.genome.util.matcher.Matcher,
   * java.lang.Character, boolean)
   */
  @Override
  public List<FsObject> listFiles(String name, Matcher<String> matcher, Character searchType, boolean recursive)
  {
    connect();
    final List<FsObject> ret = new ArrayList<FsObject>();
    listFiles(name, matcher, searchType, recursive, ret);
    return ret;
  }

  protected void listFiles(String name, Matcher<String> matcher, Character searchType, boolean recursive,
      List<FsObject> ret)
  {
    try {
      List<DavResource> dfl = sardine.getResources(gu(name));
      if (dfl.isEmpty() == true) {
        return;
      }
      for (DavResource df : dfl) {
        String dfn = df.getName();
        if (name.equals(dfn) == true || dfn.equals("") == true) {
          continue;
        }
        boolean matches = true;
        String fn = FileNameUtils.join(name, dfn);
        if (matcher != null) {
          if (matcher.match(fn) == false) {
            matches = false;
          }
        }
        if (df.isDirectory() == true) {
          if (matches == true && (searchType == null || searchType == 'D')) {
            ret.add(trans(df, name));
          }
          if (recursive == true) {
            listFiles(fn, matcher, searchType, recursive, ret);
          }
        } else {
          if (matches == true && (searchType == null || searchType == 'F')) {
            ret.add(trans(df, name));
          }
        }
      }
    } catch (SardineException ex) {
      throw new FsException(ex);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gdbfs.FileSystem#mkdir(java.lang.String)
   */
  @Override
  public boolean mkdir(String name)
  {
    checkReadOnly();
    connect();
    String fqname = gu(name);
    try {
      sardine.createDirectory(fqname);
    } catch (SardineException ex) {
      GLog.note(GWikiLogCategory.Wiki, "Dav create failed: " + fqname + "; " + ex.getMessage(),
          new LogExceptionAttribute(ex));
      return false;
    }
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gdbfs.FileSystem#readBinaryFile(java.lang.String, java.io.OutputStream)
   */
  @Override
  public void readBinaryFile(String file, OutputStream os)
  {
    connect();
    try {
      InputStream is = sardine.getInputStream(this.gu(file));
      IOUtils.copy(is, os);
    } catch (SardineException ex) {
      throw new FsException(ex);
    } catch (IOException ex) {
      throw new FsException(ex);
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gdbfs.FileSystem#rename(java.lang.String, java.lang.String)
   */
  @Override
  public boolean rename(String oldName, String newName)
  {
    checkReadOnly();
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gdbfs.FileSystem#runInTransaction(java.lang.String, long, boolean,
   * de.micromata.genome.util.runtime.CallableX)
   */
  @Override
  public <R> R runInTransaction(String lockFile, long timeOut, boolean noModFs, CallableX<R, RuntimeException> callback)
  {
    return callback.call();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gdbfs.FileSystem#writeBinaryFile(java.lang.String, java.io.InputStream, boolean)
   */
  @Override
  public void writeBinaryFile(String file, InputStream is, boolean overWrite)
  {
    checkReadOnly();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gdbfs.FileSystem#delete(java.lang.String)
   */
  @Override
  public boolean delete(String name)
  {
    checkReadOnly();
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.micromata.genome.gdbfs.FileSystem#erase()
   */
  @Override
  public void erase()
  {
    checkReadOnly();
  }

  public String getUrl()
  {
    return url;
  }

  public void setUrl(String url)
  {
    this.url = url;
  }

  public String getUser()
  {
    return user;
  }

  public void setUser(String user)
  {
    this.user = user;
  }

  public String getPass()
  {
    return pass;
  }

  public void setPass(String pass)
  {
    this.pass = pass;
  }
}
