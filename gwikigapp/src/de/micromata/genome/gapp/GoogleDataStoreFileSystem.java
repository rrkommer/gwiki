////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer
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

package de.micromata.genome.gapp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.ShortBlob;
import com.google.appengine.api.datastore.Text;

import de.micromata.genome.gdbfs.AbstractFileSystem;
import de.micromata.genome.gdbfs.FsFileExistsException;
import de.micromata.genome.gdbfs.FsFileObject;
import de.micromata.genome.gdbfs.FsObject;
import de.micromata.genome.gwiki.model.GWikiLog;
import de.micromata.genome.gwiki.utils.StringUtils;
import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.runtime.CallableX;
import de.micromata.genome.util.runtime.RuntimeIOException;

/**
 * An FileSystem implementation for Google Data Store.
 * 
 * TODO synchronizing over multiple instances?
 * 
 * @author roger
 * 
 */
public class GoogleDataStoreFileSystem extends AbstractFileSystem
{
  private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  private static final String DEFAULT_FSNAME = "fs";

  // private static final String NAME = "name";

  private static final String BDATA = "bdata";

  private static final String MODIFIEDAT = "modifiedat";

  // private static final String MODIFIEDBY = "modifiedby";

  private static final String CREATEDAT = "createdat";

  // private static final String CREATEDby = "createdby";

  private String fileSystemName = DEFAULT_FSNAME;

  public GoogleDataStoreFileSystem()
  {

  }

  public GoogleDataStoreFileSystem(String fileSystemName)
  {
    this.fileSystemName = fileSystemName;
  }

  private Key key(String fileName)
  {
    return KeyFactory.createKey(fileSystemName, fileName);
  }

  private long modifiedat(Entity ent)
  {
    Object o = ent.getProperty(MODIFIEDAT);
    if (o instanceof Date) {
      return ((Date) o).getTime();
    }
    return -1;
  }

  @Override
  public String getFileSystemName()
  {
    return "googledsfs(" + fileSystemName + ")";
  }

  @Override
  public boolean mkdir(String name)
  {
    return true;
  }

  @Override
  public boolean exists(String name)
  {
    try {
      if (StringUtils.isEmpty(name) == true) {
        return true;
      }
      Entity data = datastore.get(key(name));
      return data != null;
    } catch (EntityNotFoundException ex) {
      return false;
    }
  }

  @Override
  public boolean rename(String oldName, String newName)
  {
    try {
      Entity data = datastore.get(key(oldName));
      Entity ent = new Entity(key(newName));
      ent.setPropertiesFrom(data);
      datastore.put(ent);
      datastore.delete(key(oldName));
      return true;
    } catch (EntityNotFoundException ex) {
      return false;
    }
  }

  @Override
  public boolean delete(String name)
  {
    datastore.delete(key(name));
    return true;
  }

  @Override
  public FsObject getFileObject(String name)
  {
    try {
      Entity ent = datastore.get(key(name));
      return getFileObject(ent);
    } catch (EntityNotFoundException ex) {
      return null;
    }
  }

  private FsObject getFileObject(Entity ent)
  {
    FsObject ret;
    long modat = modifiedat(ent);
    ret = new FsFileObject(this, ent.getKey().getName(), "", modat);
    ret.setCreatedAt(new Date(modat));
    ret.setLength(0); // TODO
    return ret;
  }

  @Override
  public long getLastModified(String name)
  {
    try {
      Entity ent = datastore.get(key(name));
      if (ent == null) {
        return -1;
      }
      return modifiedat(ent);
    } catch (EntityNotFoundException ex) {
      throw new FsFileExistsException("File not exists: " + name + "; " + ex.getMessage(), ex);
    }
  }

  private void writeEntity(String file, Object data, boolean overwrite)
  {
    Date now = new Date();
    Entity ent;
    try {
      ent = datastore.get(key(file));
      if (overwrite == false) {
        throw new FsFileExistsException("File already exists: " + file);
      }
    } catch (EntityNotFoundException ex) {
      ent = new Entity(key(file));
      ent.setProperty(CREATEDAT, now);
    }
    ent.setProperty(MODIFIEDAT, now);
    ent.setProperty(BDATA, data);
    datastore.put(ent);
  }

  @Override
  public void writeBinaryFile(String file, InputStream is, boolean overWrite)
  {

    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    try {
      IOUtils.copy(is, bout);
    } catch (IOException ex) {
      throw new RuntimeIOException(ex);
    }
    IOUtils.closeQuietly(bout);
    byte[] data = bout.toByteArray();
    Object value;
    if (data.length < 500) {
      value = new ShortBlob(data);
    } else {
      value = new Blob(data);
    }
    writeEntity(file, value, overWrite);

  }

  @Override
  public void readBinaryFile(String file, OutputStream os)
  {
    try {
      Entity ent = datastore.get(key(file));
      Object o = ent.getProperty(BDATA);
      byte[] data = null;
      if (o instanceof ShortBlob) {
        data = ((ShortBlob) o).getBytes();
      } else if (o instanceof Blob) {
        data = ((Blob) o).getBytes();
      } else {
        throw new RuntimeIOException("Read binary data from googlfs. Unknown type: " + o.getClass().toString() + "; file: " + file);
      }
      IOUtils.write(data, os);
    } catch (IOException ex) {
      throw new RuntimeIOException("Cannot read file: " + file + "; " + ex.getMessage(), ex);
    } catch (EntityNotFoundException ex) {
      throw new FsFileExistsException("File not exists: " + file + "; " + ex.getMessage(), ex);
    }
  }

  @Override
  public String readTextFile(String file)
  {
    try {
      Entity ent = datastore.get(key(file));
      Object o = ent.getProperty(BDATA);
      if (o == null) {
        return super.readTextFile(file);
      }
      if (o instanceof String) {
        return (String) o;
      }
      if (o instanceof Text) {
        return ((Text) o).getValue();
      } else {
        return super.readTextFile(file);
      }
    } catch (EntityNotFoundException ex) {
      throw new FsFileExistsException("File not exists: " + file + "; " + ex.getMessage(), ex);
    }
  }

  @Override
  public void writeTextFile(String file, String content, boolean overWrite)
  {
    Object data;
    if (content.length() < 500) {
      data = content;
    } else {
      data = new Text(content);
    }
    writeEntity(file, data, overWrite);
  }

  @Override
  public void erase()
  {
    List<FsObject> fsobs = listFiles("/", null, null, true);
    for (FsObject fso : fsobs) {
      delete(fso.getName());
    }
  }

  @Override
  public List<FsObject> listFiles(String name, Matcher<String> matcher, Character searchType, boolean recursive)
  {
    if (StringUtils.equals(name, "/") == true) {
      name = "";
    }
    Query q = new Query(fileSystemName);

    List<FsObject> ret = new ArrayList<FsObject>();
    // q.addProjection(new PropertyProjection("ID", String.class));
    q.addProjection(new PropertyProjection(MODIFIEDAT, Date.class));
    PreparedQuery pq = datastore.prepare(q);
    int count = 0;
    long startt = System.currentTimeMillis();
    for (Entity ent : pq.asIterable()) {
      String fn = ent.getKey().getName();
      ++count;
      if (name != null && fn.startsWith(name) == false) {
        continue;
      }
      if (matcher != null && matcher.match(fn) == false) {
        continue;
      }
      ret.add(getFileObject(ent));
    }
    long endt = System.currentTimeMillis();
    GWikiLog.note("fs listFiles. name=" + name + "; count=" + count + "; ms=" + (endt - startt) + "; matcher=" + matcher);
    return ret;
  }

  @Override
  public long getModificationCounter()
  {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public <R> R runInTransaction(String lockFile, long timeOut, boolean noModFs, CallableX<R, RuntimeException> callback)
  {
    return callback.call();
  }

  public void setFileSystemName(String fileSystemName)
  {
    this.fileSystemName = fileSystemName;
  }

}
