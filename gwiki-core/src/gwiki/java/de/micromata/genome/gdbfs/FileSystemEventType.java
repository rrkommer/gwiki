/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   08.01.2010
// Copyright Micromata 08.01.2010
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gdbfs;

/**
 * Type of the FileSystemEvent
 * 
 * @author roger@micromata.de
 * 
 */
public enum FileSystemEventType
{
  /**
   * On some file system Modified can also mean, that the file was created.
   */
  Modified, //
  /**
   * file was created.
   */
  Created, //
  /**
   * file was deleted.
   */
  Deleted, //
  /**
   * file was renamed.
   */
  Renamed, //
  ;
}
