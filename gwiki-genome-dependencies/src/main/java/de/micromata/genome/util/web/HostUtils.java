////////////////////////////////////////////////////////////////////////////
// 
// Copyright (C) 2010-2013 Micromata GmbH / Roger Rene Kommer
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// 
////////////////////////////////////////////////////////////////////////////

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
