/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    lado@micromata.de
// Created   Jan 20, 2009
// Copyright Micromata Jan 20, 2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.util.runtime;

import org.apache.commons.lang.StringUtils;

public class StorageUtils
{

  /**
   * Oft wird eine Datei mit einem Absoltugen Pfadnamen in dem ZipStorage abgefragt. Z.B. so: /WEB-INF/etwas.xml. Diese Datei ist im Storage
   * als WEB-INF/etwas.xml abgelegt. Also wird führendes Slash weggemacht. Ausserden werden die // durch / ersetzt.
   * 
   * @param entryName
   * @return Normalisierten Namen für den ZipStorage
   */
  public static String normalizeZipStorageName(String entryName)
  {
    entryName = StringUtils.replace(entryName, "//", "/");

    if (entryName.startsWith("/") == true)
      entryName = entryName.substring(1);
    return entryName;

  }

}