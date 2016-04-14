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

/**
 * Standard rights used by the wiki.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public enum GWikiAuthorizationRights
{
  GWIKI_VIEWPAGES, //
  GWIKI_EDITPAGES, //
  GWIKI_CREATEPAGES, //
  GWIKI_DELETEPAGES, //

  GWIKI_DEVELOPER, //
  GWIKI_ADMIN, //
  /**
   * CREATEDBY muss aktuellem User entsprechen.
   */
  GWIKI_PRIVATE, //
  /**
   * Page can be viewed without any authentfication
   */
  GWIKI_PUBLIC, //
  /**
   * Allow to create HTML and use HTML-wiki elements
   */
  GWIKI_EDITHTML, //
  /**
   * Allow to access Webdav file system.
   */
  GWIKI_FSWEBDAV, //
  /**
   * Does not allow anybody even not system editor
   */
  GWIKI_DISALLOW, //
}
