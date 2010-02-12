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

/**
 * Exception thrown by FileSystem.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class FsException extends RuntimeException

{

  private static final long serialVersionUID = -3256791863540206821L;

  public FsException()
  {
    super();
  }

  public FsException(Throwable cause)
  {
    super(cause);
  }

  public FsException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public FsException(String message)
  {
    super(message);
  }

}
