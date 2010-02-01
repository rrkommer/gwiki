/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   20.11.2009
// Copyright Micromata 20.11.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gdbfs;

import java.io.IOException;
import java.nio.channels.Channel;

/**
 * Similar to apache IOUtils for NIO.
 * 
 * Only internal needed methods are implemented.
 * 
 * @author roger@micromata.de
 * 
 */
public class NIOUtils
{
  public static void closeQuitly(Channel chanel)
  {
    if (chanel == null) {
      return;
    }
    try {
      chanel.close();
    } catch (IOException ex) {
      // nothing
    }
  }
}
