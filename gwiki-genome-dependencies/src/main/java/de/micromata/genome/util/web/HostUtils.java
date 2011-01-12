/*
 Created on 08.01.2008
 */
package de.micromata.genome.util.web;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.dgc.VMID;
import java.util.Date;

/**
 * 
 * @author roger
 */
public class HostUtils
{
  private static ThreadLocal<String> runContextName = new ThreadLocal<String>();

  private static String HOSTNAME;

  private static long START_TIME;

  private static final String VM = new VMID().toString();
  static {
    START_TIME = new Date().getTime();
    try {
      HOSTNAME = InetAddress.getLocalHost().getHostName();
    } catch (final UnknownHostException ex) {
      HOSTNAME = "UnkownHost: " + new VMID().toString();
    }
  }

  public String getHostName()
  {
    return HOSTNAME;
  }

  public static String getThisHostName()
  {
    return HOSTNAME;
  }

  /**
   * This method should only be called in test code
   * 
   * @param hostname
   */
  public static void setThisHostName(String hostname)
  {
    HOSTNAME = hostname;
  }

  public static String getVm()
  {
    return VM;
  }

  
  public static long getStartTime()
  {
    return START_TIME;
  }

  public static String getNodeName()
  {
    return HOSTNAME;
  }

  public static String getRunContext(Thread thread)
  {
    String tid = "-1";
    if (thread != null)
      tid = Long.toHexString(thread.getId());
    String time = Long.toHexString(START_TIME / 1000);
    String t = time + ';' + tid;
    return t;
  }

  public static String getRunContext()
  {
    if (runContextName.get() != null)
      return runContextName.get();
    String t = getRunContext(Thread.currentThread());
    runContextName.set(t);
    return t;
  }
}
