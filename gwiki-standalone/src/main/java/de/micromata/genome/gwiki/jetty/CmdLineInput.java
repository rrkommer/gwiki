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

package de.micromata.genome.gwiki.jetty;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;

public class CmdLineInput
{
  public static String readLine()
  {
    StringBuilder sb = new StringBuilder();
    try {
      do {
        int c = System.in.read();
        if (c == '\n') {
          String s = sb.toString();
          if (s.length() > 0 && s.charAt(s.length() - 1) == '\r') {
            s = s.substring(0, s.length() - 1);
          }
          return s;
        }
        sb.append((char) c);
      } while (true);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  public static void message(String message)
  {
    System.out.println(message);
  }

  public static boolean ask(String message)
  {
    System.out.println(message);
    do {
      System.out.print("(Yes/No): ");
      String inp = StringUtils.trim(readLine());
      inp = inp.toLowerCase();
      if (inp.equals("y") == true || inp.equals("yes") == true) {
        return true;
      }
      if (inp.equals("n") == true || inp.equals("no") == true) {
        return false;
      }
    } while (true);
  }

  public static boolean ask(String message, boolean defaultYes)
  {
    System.out.println(message);
    do {
      if (defaultYes == true) {
        System.out.print("([Yes]/No): ");
      } else {
        System.out.print("(Yes/[No]): ");
      }
      String inp = StringUtils.trim(readLine());
      inp = inp.toLowerCase();
      if (StringUtils.isEmpty(inp) == true) {
        return defaultYes;
      }
      if (inp.equalsIgnoreCase("y") == true || inp.equalsIgnoreCase("yes") == true) {
        return true;
      }
      if (inp.equalsIgnoreCase("n") == true || inp.equalsIgnoreCase("no") == true) {
        return false;
      }
    } while (true);
  }

  public static String getInput(String prompt)
  {
    return getInput(prompt, null);
  }

  public static String getInput(String prompt, String defaultValue)
  {
    return getInput(prompt, defaultValue, false);
  }

  public static String getInput(String prompt, String defaultValue, boolean allowEmpty)
  {
    do {
      if (defaultValue == null) {
        message(prompt + ": ");
      } else {
        message(prompt + "[" + defaultValue + "]: ");
      }
      String inp = readLine();
      inp = StringUtils.trim(inp);
      if (defaultValue != null) {
        if (inp.length() == 0) {
          return defaultValue;
        }
      }
      if (allowEmpty == true || inp.length() > 0) {
        return inp;
      }
    } while (true);
  }

}
