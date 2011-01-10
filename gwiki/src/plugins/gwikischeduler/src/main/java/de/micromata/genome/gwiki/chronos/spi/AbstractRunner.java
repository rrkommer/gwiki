/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   18.08.2008
// Copyright Micromata 18.08.2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.gwiki.chronos.spi;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.dgc.VMID;

/**
 * 
 * @author roger@micromata.de
 * 
 */
@Deprecated
public class AbstractRunner
{
  private static String HOSTNAME;
  static {
    try {
      HOSTNAME = InetAddress.getLocalHost().getHostName();
    } catch (final UnknownHostException ex) {
      HOSTNAME = "UnkownHost: " + new VMID().toString();
    }
  }

  public static String getThisHostName()
  {
    return HOSTNAME;
  }
}
