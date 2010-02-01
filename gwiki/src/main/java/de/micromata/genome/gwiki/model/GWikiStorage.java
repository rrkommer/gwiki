/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   18.10.2009
// Copyright Micromata 18.10.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.model;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import de.micromata.genome.gdbfs.FileSystem;
import de.micromata.genome.gwiki.page.GWikiContext;
import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.runtime.CallableX;

public interface GWikiStorage
{
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
   * @param lastModification
   * @return
   */
  // public int incrementModification(int lastModification);

  GWikiElement createElement(GWikiElementInfo ei);

  void loadPageInfos(Map<String, GWikiElementInfo> map);

  /**
   * 
   * @param path
   * @return null if not found.
   */
  GWikiElementInfo loadElementInfo(String path);

  /**
   * Used in debug mode, if artefacts are changed outside.
   * 
   * @param ei
   * @return null if element is clean otherwise fresh loaded element
   */
  GWikiElement hasModifiedArtefakts(GWikiElementInfo ei);

  GWikiElement loadElement(String pageId);

  GWikiElement loadElement(GWikiElementInfo id);

  GWikiElement storeElement(GWikiContext wikiContext, GWikiElement elm, boolean keepModifiedAt);

  /**
   * load page infos from storage.
   * 
   * @param path directory
   * @return
   */
  List<GWikiElementInfo> loadPageInfos(String path);

  List<GWikiElementInfo> getVersions(String id);

  List<String> findDeletedPages(Matcher<String> filter);

  /**
   * deletes an elements
   * 
   * @param elm
   */
  void deleteElement(GWikiContext wikiContext, GWikiElement elm);

  /**
   * This element has to be a archive element.
   * 
   * @param elm
   * @return restored elementInfo
   */
  GWikiElementInfo restoreElement(GWikiContext wikiContext, GWikiElement elm);

  /**
   * 
   * @param type
   * @return null if not found
   */
  String getArtefaktClassNameFromType(String type);

  public boolean isArchivePageId(String path);

  public void rebuildIndex(GWikiContext wikiContext, Collection<GWikiElementInfo> eis, boolean completeUpdate);

  public void setWikiWeb(GWikiWeb wikiWeb);

  public <R> R runInTransaction(long lockWaitTime, CallableX<R, RuntimeException> callback);

  /**
   * 
   * @return virtual file system.
   */
  public FileSystem getFileSystem();
}
