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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.runtime.CallableX;
import de.micromata.genome.util.runtime.RuntimeIOException;

/**
 * Load files out of classloader
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class ClassLoaderFileSystem extends AbstractFileSystem
{
  ClassLoader classLoader;

  public ClassLoaderFileSystem()
  {
    classLoader = getClass().getClassLoader();
  }

  public ClassLoaderFileSystem(ClassLoader classLoader)
  {
    this.classLoader = classLoader;
  }

  @Override
  public String getFileSystemName()
  {
    return "classpath";
  }

  @Override
  public boolean mkdir(String name)
  {
    return false;
  }

  private String normalize(String name)
  {
    return name;
  }

  @Override
  public boolean exists(String name)
  {
    URL url = classLoader.getResource(normalize(name));
    return url != null;
  }

  @Override
  public boolean rename(String oldName, String newName)
  {
    return false;
  }

  @Override
  public boolean delete(String name)
  {
    return false;
  }

  @Override
  public FsObject getFileObject(String name)
  {
    name = normalize(name);
    URL url = classLoader.getResource(normalize(name));
    if (url == null) {
      return null;
    }
    FsFileObject ret = new FsFileObject(this, name, "", 0);
    return ret;
  }

  @Override
  public long getLastModified(String name)
  {
    return 0;
  }

  @Override
  public void writeBinaryFile(String file, InputStream is, boolean overWrite)
  {

  }

  @Override
  public void readBinaryFile(String file, OutputStream os)
  {
    String name = normalize(file);

    try (InputStream is = classLoader.getResourceAsStream(name)) {
      if (is == null) {
        return;
      }
      IOUtils.copy(is, os);

    } catch (IOException ex) {
      throw new RuntimeIOException(ex);
    }

  }

  @Override
  public void erase()
  {

  }

  @Override
  public List<FsObject> listFiles(String name, Matcher<String> matcher, Character searchType, boolean recursive)
  {
    List<FsObject> ret = new ArrayList<>();
    return ret;
  }

  @Override
  public long getModificationCounter()
  {
    return 0;
  }

  @Override
  public <R> R runInTransaction(String lockFile, long timeOut, boolean noModFs, CallableX<R, RuntimeException> callback)
  {
    return callback.call();
  }

}
