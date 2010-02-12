/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   22.10.2009
// Copyright Micromata 22.10.2009
//
/////////////////////////////////////////////////////////////////////////////
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
}
