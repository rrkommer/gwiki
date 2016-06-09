//
// Copyright (C) 2010-2016 Roger Rene Kommer & Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package de.micromata.genome.gwiki.launcher;

import de.micromata.genome.util.bean.PrivateBeanUtils;
import de.micromata.genome.util.runtime.Log4JInitializer;
import de.micromata.mgc.application.MgcApplicationStartStopStatus;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class GWikiLauncherMain
{
  private static boolean noWindow(String[] args)
  {
    for (String arg : args) {
      if (arg.equals("-nogui") == true) {
        return true;
      }
    }
    return false;
  }

  public static void main(String[] args)
  {
    try {
      if (noWindow(args) == true) {
        runCli(args);
      } else {
        runGui(args);
      }
    } catch (Throwable ex) {
      ex.printStackTrace();
    }
  }

  public static void runCli(String[] args)
  {
    GWikiLauncherApplication sapplication = new GWikiLauncherApplication();
    Log4JInitializer.copyLogConfigFileFromCp();
    Log4JInitializer.initializeLog4J();
    StringBuilder errBuff = new StringBuilder();
    if (sapplication.checkConfiguration(errBuff) == false) {
      System.err.println(errBuff.toString());
      throw new RuntimeException("Configuration is invalid; Check logs");
    }
    if (sapplication.initWithConfig() == false) {
      throw new RuntimeException("Failure intializing config; Check logs");
    }
    if (sapplication.start(args) != MgcApplicationStartStopStatus.StartSuccess) {
      throw new RuntimeException("Failure starting application; Check logs");
    }
  }

  public static void runGui(String[] args) throws Exception
  {
    Class<?> guilclas = Class.forName("de.micromata.genome.gwiki.launcher.GWikiWithGuiLauncher");
    PrivateBeanUtils.invokeStaticMethod(guilclas, "main", new Object[] { args });
  }

}
