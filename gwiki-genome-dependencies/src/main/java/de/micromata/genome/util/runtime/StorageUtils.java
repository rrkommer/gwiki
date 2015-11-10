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

/////////////////////////////////////////////////////////////////////////////
//
// Project Genome Core
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