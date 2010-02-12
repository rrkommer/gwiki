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
 * If file lock is not possible due timeout.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class FsFileLockException extends FsException
{

  private static final long serialVersionUID = -5030674480778775399L;

  public FsFileLockException()
  {
  }

  public FsFileLockException(Throwable cause)
  {
    super(cause);
  }

  public FsFileLockException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public FsFileLockException(String message)
  {
    super(message);
  }

}
