/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   02.11.2009
// Copyright Micromata 02.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gdbfs;

import java.io.IOException;

/**
 * Exception thrown by FileSystem.
 * 
 * @author roger@micromata.de
 * 
 */
public class FsIOException extends FsException
{

  private static final long serialVersionUID = -3256791863540206821L;

  public FsIOException()
  {
    super();
  }

  public FsIOException(IOException cause)
  {
    super(cause);
  }

  public FsIOException(String message, IOException cause)
  {
    super(message, cause);
  }

  public FsIOException(String message)
  {
    super(message);
  }

}
