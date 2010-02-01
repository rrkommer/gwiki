/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   09.01.2010
// Copyright Micromata 09.01.2010
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gdbfs;

/**
 * Will be called if FileSystemEventListener implement this interface, after all events in the queue are send.
 * 
 * This is usable, if lister want to implement batch operations.
 * 
 * @author roger@micromata.de
 * 
 */
public interface FileSystemFinalizedEventListener
{
  public void onFileSystemChangedFinalized();
}
