/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   21.01.2010
// Copyright Micromata 21.01.2010
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gdbfs;

/**
 * Invalid file name.
 * 
 * File name contains invalid name.
 * 
 * @author roger@micromata.de
 * 
 */
public class FsInvalidNameException extends FsException
{

  private static final long serialVersionUID = -5338331094642365074L;

  public FsInvalidNameException()
  {
    super();
  }

  public FsInvalidNameException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public FsInvalidNameException(String message)
  {
    super(message);
  }

  public FsInvalidNameException(Throwable cause)
  {
    super(cause);
  }

}
