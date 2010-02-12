/////////////////////////////////////////////////////////////////////////////
//
// Project   genome-gwiki-standalone
//
// Author    roger@micromata.de
// Created   28.12.2009
// Copyright Micromata 28.12.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;

/**
 * Standalone response output stream for batch processing and unit tests.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class StandaloneServletOutputStream extends ServletOutputStream
{
  private ByteArrayOutputStream out = new ByteArrayOutputStream();

  /** Pass through method calls ByteArrayOutputStream.write(int b). */
  public void write(int b) throws IOException
  {
    out.write(b);
  }

  /** Returns the array of bytes that have been written to the output stream. */
  public byte[] getBytes()
  {
    return out.toByteArray();
  }

  /** Returns, as a character string, the output that was written to the output stream. */
  public String getString()
  {
    return out.toString();
  }
}
