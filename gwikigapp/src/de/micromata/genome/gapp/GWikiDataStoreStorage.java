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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.micromata.genome.gdbfs.FileSystem;
import de.micromata.genome.gdbfs.FsObject;
import de.micromata.genome.gwiki.model.GWikiElementInfo;
import de.micromata.genome.gwiki.spi.storage.GWikiFileStorage;
import de.micromata.genome.util.matcher.BooleanListRulesFactory;
import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.runtime.CallableX;

/**
 * Currently not used
 * 
 * @author roger
 * 
 */
public class GWikiDataStoreStorage extends GWikiFileStorage
{
  private FileSystem settingsFileSystem;

  /**
   * @param storage
   */
  public GWikiDataStoreStorage(FileSystem storage)
  {
    super(storage);
  }

  @Override
  public synchronized void loadPageInfos(final Map<String, GWikiElementInfo> ret)
  {
    if (settingsFileSystem == null) {
      super.loadPageInfos(ret);
    }
    storage.runInTransaction(null, standardLockTimeout, false, new CallableX<Void, RuntimeException>() {

      public Void call() throws RuntimeException
      {
        loadPageInfosImpl(settingsFileSystem, ret);
        return null;
      }
    });
  }

  @Override
  public List<GWikiElementInfo> loadPageInfos(String path)
  {
    if (settingsFileSystem == null) {
      return super.loadPageInfos(path);
    }
    List<GWikiElementInfo> ret = new ArrayList<GWikiElementInfo>();
    Matcher<String> matcher = new BooleanListRulesFactory<String>().createMatcher("*Settings.properties");
    List<FsObject> elements = settingsFileSystem.listFiles(path, matcher, 'F', true);
    for (FsObject e : elements) {
      ret.add(createElementInfo(e));
    }
    return ret;
  }

  public FileSystem getSettingsFileSystem()
  {
    return settingsFileSystem;
  }

  public void setSettingsFileSystem(FileSystem settingsFileSystem)
  {
    this.settingsFileSystem = settingsFileSystem;
  }

}
