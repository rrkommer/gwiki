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
 * Notification Event fired after file system is changed.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public interface FileSystemEventListener
{
  public void onFileSystemChanged(FileSystemEvent event);
}
