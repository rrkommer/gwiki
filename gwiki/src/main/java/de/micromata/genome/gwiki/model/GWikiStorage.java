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

package de.micromata.genome.gwiki.model;

import java.util.List;
import java.util.Map;

import de.micromata.genome.gdbfs.FileSystem;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.runtime.CallableX;

/**
 * Dealing with persistancy of Elements and Artefakts.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public interface GWikiStorage
{

  /**
   * The Constant SETTINGS_SUFFIX.
   */
  public static final String SETTINGS_SUFFIX = "Settings.properties";

  /**
   * Boolean flag storage should not create archive.
   * 
   * Stored aa request attribute.
   */
  public final static String STORE_NO_ARCHIVE = "de.micromata.genome.gwiki.model.GWikiStorage.STORE_NO_ARCHIVE";

  /**
   * Boolean flag if storage should not create index file.
   * 
   * Stored as request attribute.
   */
  public final static String STORE_NO_INDEX = "de.micromata.genome.gwiki.model.GWikiStorage.STORE_NO_INDEX";

  /**
   * Laufender Zaehler fuer Modification.
   * 
   * @return
   */
  long getModificationCounter();

  /**
   * Atomar incrementation of gwiki version counter.
   *
   * @param ei the ei
   * @return the g wiki element
   */
  // public int incrementModification(int lastModification);

  GWikiElement createElement(GWikiElementInfo ei);

  /**
   * Load all pageinfos.
   *
   * @param map the map
   */
  void loadPageInfos(Map<String, GWikiElementInfo> map);

  /**
   * Load element info.
   *
   * @param path the path
   * @return null if not found.
   */
  GWikiElementInfo loadElementInfo(String path);

  /**
   * Used in debug mode, if artefacts are changed outside.
   *
   * @param ei the ei
   * @return null if element is clean otherwise fresh loaded element
   */
  GWikiElement hasModifiedArtefakts(GWikiElementInfo ei);

  /**
   * Loads element.
   *
   * @param pageId the page id
   * @return null if not found
   */
  GWikiElement loadElement(String pageId);

  /**
   * Loads element.
   *
   * @param id the id
   * @return null if not found
   */
  GWikiElement loadElement(GWikiElementInfo id);

  /**
   * Store element.
   * 
   * In most cases you have GWikiWeb.saveElement() instead of calling this method directly.
   *
   * @param wikiContext the wiki context
   * @param elm the elm
   * @param keepModifiedAt the keep modified at
   * @return stored element.
   */
  GWikiElement storeElement(GWikiContext wikiContext, GWikiElement elm, boolean keepModifiedAt);

  /**
   * load page infos from storage.
   *
   * @param path directory
   * @return the list
   */
  List<GWikiElementInfo> loadPageInfos(String path);

  /**
   * Gets the versions.
   *
   * @param id the id
   * @return the versions
   */
  List<GWikiElementInfo> getVersions(String id);

  /**
   * Find deleted pages.
   *
   * @param filter the filter
   * @return the list
   */
  List<String> findDeletedPages(Matcher<String> filter);

  /**
   * deletes an elements.
   *
   * @param wikiContext the wiki context
   * @param elm the elm
   */
  void deleteElement(GWikiContext wikiContext, GWikiElement elm);

  /**
   * This element has to be a archive element.
   *
   * @param wikiContext the wiki context
   * @param elm the elm
   * @return restored elementInfo
   */
  GWikiElementInfo restoreElement(GWikiContext wikiContext, GWikiElement elm);

  /**
   * Gets the artefakt class name from type.
   *
   * @param type the type
   * @return null if not found
   */
  String getArtefaktClassNameFromType(String type);

  /**
   * Checks if is archive page id.
   *
   * @param path the path
   * @return true, if is archive page id
   */
  public boolean isArchivePageId(String path);

  /**
   * Rebuild index.
   *
   * @param wikiContext the wiki context
   * @param eis the eis
   * @param completeUpdate the complete update
   */
  public void rebuildIndex(GWikiContext wikiContext, Iterable<GWikiElementInfo> eis, boolean completeUpdate);

  public void setWikiWeb(GWikiWeb wikiWeb);

  /**
   * Run in transaction.
   *
   * @param <R> the generic type
   * @param lockWaitTime the lock wait time
   * @param callback the callback
   * @return the r
   */
  public <R> R runInTransaction(long lockWaitTime, CallableX<R, RuntimeException> callback);

  /**
   * 
   * @return virtual file system.
   */
  public FileSystem getFileSystem();

  /**
   * Set file system
   * 
   * @param fileSystem
   */
  public void setFileSystem(FileSystem fileSystem);
}
