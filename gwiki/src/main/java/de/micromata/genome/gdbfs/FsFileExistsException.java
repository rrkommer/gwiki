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
 * Exception throws if file written without overwrite and file already exists.
 * 
 * @author roger@micromata.de
 * 
 */
public class FsFileExistsException extends FsIOException
{

  private static final long serialVersionUID = -5030674480778775399L;

  public FsFileExistsException()
  {
  }

  public FsFileExistsException(IOException cause)
  {
    super(cause);
  }

  public FsFileExistsException(String message, IOException cause)
  {
    super(message, cause);
  }

  public FsFileExistsException(String message)
  {
    super(message);
  }

}
