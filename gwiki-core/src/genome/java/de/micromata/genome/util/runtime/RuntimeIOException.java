/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   24.09.2008
// Copyright Micromata 24.09.2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.util.runtime;

import java.io.IOException;

/**
 * RuntimeException wrapper for IOException
 * 
 * @author roger@micromata.de
 * 
 */
public class RuntimeIOException extends RuntimeException
{

  private static final long serialVersionUID = 9199669223884215271L;

  public RuntimeIOException()
  {
  }

  public RuntimeIOException(String message)
  {
    super(message);
  }

  public RuntimeIOException(IOException cause)
  {
    super(cause);
  }

  public RuntimeIOException(String message, IOException cause)
  {
    super(message, cause);
  }

}
